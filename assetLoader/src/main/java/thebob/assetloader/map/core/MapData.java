/* 
 * The MIT License
 *
 * Copyright 2017 the_bob.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package thebob.assetloader.map.core;

import java.io.RandomAccessFile;
import static java.lang.System.gc;
import static java.lang.System.runFinalization;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Formatter;
import javolution.text.TextBuilder;
import thebob.assetloader.map.MapLoader;
import thebob.assetloader.map.core.components.MapActors;
import thebob.assetloader.map.core.components.MapContentInfo;
import thebob.assetloader.map.core.components.MapLayers;
import thebob.assetloader.map.core.components.MapSettings;
import thebob.assetloader.map.helpers.GridPos;
import thebob.assetloader.xml.XmlLoader;

public class MapData {

    public int junkBytes = 0;
    public static XmlLoader xmlDataSource = null;

    MapSettings settings;
    MapContentInfo info;
    MapActors actors;
    MapLayers layers;

    ByteBuffer byteBuffer;
    FileChannel fc;

    long bufferLength = 0;
    private int ubAmbientLightLevel;

    protected void initComponents() {
        settings = new MapSettings(this);
        info = new MapContentInfo(this);
        actors = new MapActors(this);
        layers = new MapLayers(this);
        GridPos.map = this; // FIXME
    }

    public boolean saveMap(String fileName) {
        try (final RandomAccessFile file = new RandomAccessFile(fileName, "rw")) {
            fc = file.getChannel();

            MappedByteBuffer outputBuffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, 2 * 1024 * 1024);
            outputBuffer.order(ByteOrder.LITTLE_ENDIAN);

            saveData(outputBuffer);

            outputBuffer.force();
            bufferLength = outputBuffer.position();
            outputBuffer = null;

            runFinalization();
            gc();

            Thread.sleep(250);

            fc.truncate(bufferLength);

            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public boolean loadMap(ByteBuffer byteBuffer) {
	
        this.byteBuffer =  ByteBuffer.allocate(byteBuffer.remaining());
	this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
	this.byteBuffer.put(byteBuffer);
	this.byteBuffer.flip();
	
	//this.byteBuffer = byteBuffer;
        bufferLength=byteBuffer.limit();
        
        boolean result = false;
        initComponents();

        try {
            result = loadData();
            System.out.println("loadMap(): done loading, processed " + byteBuffer.position() + " / " + bufferLength + " bytes");
        } catch (Exception exception) {
            System.out.println("loadMap(): FAILED loading, processed " + byteBuffer.position() + " / " + bufferLength + " bytes");
            if (MapLoader.logEverything) {
                exception.printStackTrace();
            } else {
                System.out.println(exception + ": " + exception.getMessage());
            }

        }

        junkBytes += (bufferLength - byteBuffer.position());

        if ((byteBuffer.position() != bufferLength) && MapLoader.printJunk) {
            printJunkAtEndOfFile();
        }

        return result;
    }

    public static String printBytes(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        String hex = formatter.toString();
        TextBuilder tmp = new TextBuilder();
        TextBuilder tmp2 = new TextBuilder();
        int cnt = 0;
        for (char c : hex.toCharArray()) {

            if (cnt % 2 == 0) {
                tmp.append(' ');
                char c2 = (char) bytes[cnt / 2];
                switch (c2) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case '\b':
                        tmp2.append('X');
                        break;
                    default:
                        tmp2.append(c2);
                }

            }

            if (cnt % 16 == 0) {
                tmp.append(' ');
            }

            if (cnt % 64 == 0) {
                tmp.append('\t');
                tmp.append(tmp2.toCharArray());
                tmp2.clear();
                tmp.append('\n');
            }

            tmp.append(c);

            cnt++;
        }

        while (true) {
            if (cnt % 2 == 0) {
                tmp.append(' ');
                tmp2.append(' ');
            }

            if (cnt % 16 == 0) {
                tmp.append(' ');
            }

            if (cnt % 64 == 0) {
                tmp.append('\t');
                tmp.append(tmp2.toCharArray());
                tmp.append("|<-END");
                tmp.append('\n');

                break;
            }

            tmp.append(' ');
            cnt++;
        }

        return (tmp.toString());
    }

    private void printJunkAtEndOfFile() {
        byte[] bytes = new byte[(int) (bufferLength - byteBuffer.position())];

        byteBuffer.get(bytes);

        System.out.println("loader.core.MapData.printJunkAtEndOfFile(): " + bytes.length + "B leftover data at end of file:");
        System.out.println(printBytes(bytes));
    }

    public boolean loadData() {
        if (!settings.loadMapHeader()) {
            System.out.println("loader.core.MapData.loadData(): failed while loading map header!");
            return false;
        }

        info.loadHeightValues();

        layers.loadLayerCounts();
        layers.loadLayers();

        // skip some old stuff we dont care about
        if (settings.dMajorMapVersion == 6.00 && settings.ubMinorMapVersion < 27) {
            if (MapLoader.logEverything) {
                System.out.println("\n\tloadData(): SKIP 37 BYTES!\n");
            }
            byteBuffer.position(byteBuffer.position() + 37 * 4);
            settings.dMajorMapVersion = 5.00f;
        }

        info.loadRoomInfos();

        if (settings.MAP_WORLDITEMS_SAVED) {
            if (!actors.loadWorldItems()) {
                System.out.println("loader.core.MapData.loadData(): failed while loading World Items!");
                return false;
            }
        }

        // TODO: figure where to move this (settings or infos)
        if (settings.MAP_AMBIENTLIGHTLEVEL_SAVED) {
            if (MapLoader.logFileIO) {
                System.out.println("loader.core.MapData.loadData(): load ambient Light level START @" + byteBuffer.position());
            }
            //Ambient light levels are only saved in underground levels
            settings.gfBasement = byteBuffer.get() > 0;
            settings.gfCaves = byteBuffer.get() > 0;
            ubAmbientLightLevel = byteBuffer.get() & 0xFF;
            if (MapLoader.logFileIO) {
                System.out.println("loader.core.MapData.loadData(): load ambient Light level END @" + byteBuffer.position());
            }
        } else {
            //We are above ground.
            settings.gfBasement = false;
            settings.gfCaves = false;
        }

        if (settings.MAP_WORLDLIGHTS_SAVED) {
            info.loadMapLights();
        }

        settings.loadMapInfo();

        if (settings.MAP_FULLSOLDIER_SAVED) {
            if (!actors.loadPlacements()) {
                System.out.println("loader.core.MapData.loadData(): failed while loading placements!");
                return false;
            }
        }
        if (settings.MAP_EXITGRIDS_SAVED) {
            info.loadExitGrids();
        }
        if (settings.MAP_DOORTABLE_SAVED) {
            info.loadDoorTables();
        }
        if (settings.MAP_EDGEPOINTS_SAVED) {
            info.loadEdgePoints();
        }
        if (settings.MAP_NPCSCHEDULES_SAVED) {
            if (!actors.loadSchedules()) {
                System.out.println("loader.core.MapData.loadData(): failed while Loading Schedules!");
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "MapData"
                + "\n------------------------------\n"
                + "\t settings: "
                + "\n------------------------------\n"
                + settings
                + "\n------------------------------\n"
                + "\t info: "
                + "\n------------------------------\n"
                + info
                + "\n------------------------------\n"
                + "\t layers: "
                + "\n------------------------------\n"
                + layers
                + "\n------------------------------\n"
                + "\t actors: "
                + "\n------------------------------\n"
                + actors
                + "\n------------------------------\n";
    }

    // getters and setters
    public MapSettings getSettings() {
        return settings;
    }

    public MapContentInfo getInfo() {
        return info;
    }

    public MapActors getActors() {
        return actors;
    }

    public MapLayers getLayers() {
        return layers;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    private void saveData(MappedByteBuffer outputBuffer) {
        settings.saveMapHeader(outputBuffer);
        info.saveHeightValues(outputBuffer);

        layers.saveLayerCounts(outputBuffer);
        layers.saveLayers(outputBuffer);

        info.saveRoomInfos(outputBuffer);

        if (settings.MAP_WORLDITEMS_SAVED) {
            actors.saveWorldItems(outputBuffer);
        }

        // TODO: figure where to move this (settings or infos)
        if (settings.MAP_AMBIENTLIGHTLEVEL_SAVED) {
            if (MapLoader.logFileIO) {
                System.out.println("loader.core.MapData.saveData(): save ambient Light level START @" + outputBuffer.position());
            }
            //Ambient light levels are only saved in underground levels
            outputBuffer.put(settings.gfBasement ? (byte) 1 : (byte) 0);
            outputBuffer.put(settings.gfCaves ? (byte) 1 : (byte) 0);
            outputBuffer.put((byte) ubAmbientLightLevel);
            if (MapLoader.logFileIO) {
                System.out.println("loader.core.MapData.saveData(): save ambient Light level END @" + outputBuffer.position());
            }
        } else {
            //We are above ground.
            settings.gfBasement = false;
            settings.gfCaves = false;
        }

        if (settings.MAP_WORLDLIGHTS_SAVED) {
            info.saveMapLights(outputBuffer);
        }

        settings.saveMapInfo(outputBuffer);

        if (settings.MAP_FULLSOLDIER_SAVED) {
            actors.savePlacements(outputBuffer);
        }
        if (settings.MAP_EXITGRIDS_SAVED) {
            info.saveExitGrids(outputBuffer);
        }
        if (settings.MAP_DOORTABLE_SAVED) {
            info.saveDoorTables(outputBuffer);
        }
        if (settings.MAP_EDGEPOINTS_SAVED) {
            info.saveEdgePoints(outputBuffer);
        }
        if (settings.MAP_NPCSCHEDULES_SAVED) {
            actors.saveSchedules(outputBuffer);
        }
    }

    public void setXmlDataSource(XmlLoader xml) {
        xmlDataSource = xml;
    }

}
