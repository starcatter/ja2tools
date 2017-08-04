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
package thebob.assetloader.map.core.components;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import thebob.assetloader.map.MapLoader;
import thebob.assetloader.map.helpers.GridPos;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.map.structures.DOOR;
import thebob.assetloader.map.structures.legacy.OLD_DOOR;
import thebob.assetloader.map.structures.EXITGRID;
import thebob.assetloader.map.structures.LIGHT_SPRITE;
import thebob.assetloader.map.structures.SGPPaletteEntry;
import thebob.assetloader.map.structures.legacy.OLD_EXITGRID;
import thebob.assetloader.map.wrappers.WorldItemStack;

/**
 *
 * @author the_bob
 */
public class MapContentInfo extends MapComponent {

    short[] worldLevelData;
    short[] gusWorldRoomInfo;
    private int usNumLights;
    private short usNumExitGrids;
    private int gubNumDoors;
    int[][] mapEdgePoints = new int[8][];
    int[] mapMidPoints = new int[8];

    EXITGRID[] exitGrids = null;
    DOOR[] doors = null;
    private SGPPaletteEntry[] LColors;
    private byte ubNumColors;

    LIGHT_SPRITE[] lights = null;
    private String[] lightNames = null;

    public MapContentInfo(MapData source) {
        super(source);
    }

    public boolean loadHeightValues() {
        try {
            if (MapLoader.logFileIO) {
                System.out.println("loader.core.MapContentInfo.loadHeightValues() start @" + byteBuffer.position());
            }
            // Read height values
            worldLevelData = new short[settings().iWorldSize];
            if (MapLoader.logEverything) {
                System.out.println("loadData(): load " + settings().iWorldSize + " * short =\t" + Short.BYTES * settings().iWorldSize);
            }
            for (int i = 0; i < settings().iWorldSize; i++) {
                //LOADDATA(&gpWorldLevelData[cnt].sHeight, pBuffer, sizeof(INT16));
                worldLevelData[i] = byteBuffer.getShort();
            }
            if (MapLoader.logFileIO) {
                System.out.println("loader.core.MapContentInfo.loadHeightValues() end @" + byteBuffer.position());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean loadRoomInfos() {
        try {
            if (MapLoader.logFileIO) {
                System.out.println("loader.core.MapContentInfo.loadRoomInfos() start @" + byteBuffer.position());
            }
            if (MapLoader.logEverything) {
                System.out.println("loadData(): load " + settings().settings().iWorldSize + " * Short =\t" + Short.BYTES * settings().settings().iWorldSize);
            }
            gusWorldRoomInfo = new short[settings().iWorldSize];
            for (int i = 0; i < settings().iWorldSize; i++) {
                if (settings().ubMinorMapVersion < 29) {
                    gusWorldRoomInfo[i] = byteBuffer.get();
                } else {
                    gusWorldRoomInfo[i] = byteBuffer.getShort();
                }
            }
            if (MapLoader.logFileIO) {
                System.out.println("loader.core.MapContentInfo.loadRoomInfos() end @" + byteBuffer.position());
            }
            return true;
        } catch (Exception e) {
            System.out.println("loadRoomInfos(): " + e.toString());
        }
        return false;
    }

    public void loadEdgePoints() {
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.loadEdgePoints() start @" + byteBuffer.position());
        }
        for (int i = 0; i < 8; i++) {
            int gus1stNorthEdgepointArraySize = byteBuffer.getShort() & 0xFFFF;
            int gus1stNorthEdgepointMiddleIndex = byteBuffer.getShort() & 0xFFFF;
            if (gus1stNorthEdgepointArraySize > 0) {

                if (MapLoader.logEverything) {
                    System.out.println("loadEdgePoints() " + (i + 1) + " / 8: load " + gus1stNorthEdgepointArraySize + " Edge Points:\t");
                }

                mapMidPoints[i] = gus1stNorthEdgepointMiddleIndex;
                mapEdgePoints[i] = new int[gus1stNorthEdgepointArraySize];

                if (settings().dMajorMapVersion < 7.0) {
                    for (int j = 0; j < gus1stNorthEdgepointArraySize; j++) {
                        mapEdgePoints[i][j] = byteBuffer.getShort();
                    }
                } else {
                    for (int j = 0; j < gus1stNorthEdgepointArraySize; j++) {
                        mapEdgePoints[i][j] = byteBuffer.getInt();
                    }
                }

                // print loaded edgepoints - if they look like junk then there's a problem loading the map!
                if (MapLoader.logEverything) {
                    for (int j = 0; j < gus1stNorthEdgepointArraySize; j++) {
                        System.out.print(new GridPos(mapEdgePoints[i][j]));
                        System.out.print(' ');
                        if (j % 3 == 0) {
                            System.out.print("\n\t");
                        }
                    }
                }

            } else if (MapLoader.logEverything) {
                System.out.println("loadEdgePoints() " + (i + 1) + " / 8: NO Edge Points");
            }
        }
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.loadEdgePoints() end @" + byteBuffer.position());
        }
    }

    public void loadDoorTables() {
        // "Loading door tables..."
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.loadDoorTables() start @" + byteBuffer.position());
        }
        gubNumDoors = byteBuffer.get() & 0xFF;
        if (gubNumDoors < 1) {
            if (MapLoader.logEverything) {
                System.out.println("loadDoorTables(): Doors saved yet count < 1!!!");
            }
        } else {
            if (MapLoader.logEverything) {
                System.out.println("loadDoorTables(): Loading " + gubNumDoors + " doors...");
            }
            doors = new DOOR[gubNumDoors];
        }
        for (int cnt = 0; cnt < gubNumDoors; cnt++) {

            DOOR door = new DOOR();
            if (settings().dMajorMapVersion < 7.0) {
                OLD_DOOR door_old = new OLD_DOOR();
                door_old.setSource(byteBuffer);
                door.loadOld(door_old);
            } else {
                door.setSource(byteBuffer);
            }

            if (MapLoader.logEverything) {
                System.out.println("loadDoorTables(): loaded \n\t" + door);
            }

            doors[cnt] = door;
        }
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.loadDoorTables() end @" + byteBuffer.position());
        }
    }

    public void loadExitGrids() {
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.loadExitGrids() start @" + byteBuffer.position());
        }
        // "Loading exit grids..."
        //LOADDATA(&usNumExitGrids, *hBuffer, sizeof(usNumExitGrids));
        usNumExitGrids = byteBuffer.getShort();
        if (usNumExitGrids < 1) {
            if (MapLoader.logEverything) {
                System.out.println("loadExitGrids(): Exit grids saved yet count < 1!!!\n\n");
            }
        } else {
            if (MapLoader.logEverything) {
                System.out.println("loadExitGrids(): Loading " + usNumExitGrids + " Exit grids...");
            }
            exitGrids = new EXITGRID[usNumExitGrids];
        }

        for (int i = 0; i < usNumExitGrids; i++) {
            EXITGRID exitGrid = new EXITGRID();

            if (settings().dMajorMapVersion < 7.0) {
                OLD_EXITGRID oldExitGrid = new OLD_EXITGRID();
                oldExitGrid.setSource(byteBuffer);
                exitGrid.loadOld(oldExitGrid);
            } else {
                exitGrid.setSource(byteBuffer);
            }

            if (MapLoader.logEverything) {
                System.out.println("loadData(): ExitGrid #" + i + "\n\t" + "iMapIndex: " + exitGrid.iMapIndex.get() + "\n\t" + "usGridNo: " + exitGrid.usGridNo.get() + "\n\t" + "ubGotoSectorX: " + exitGrid.ubGotoSectorX.get() + "\n\t" + "ubGotoSectorY: " + exitGrid.ubGotoSectorY.get() + "\n\t" + "ubGotoSectorZ: " + exitGrid.ubGotoSectorZ.get() + "\n\t" + exitGrid.toString());
            }

            exitGrids[i] = exitGrid;
        }

        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.loadExitGrids() end @" + byteBuffer.position());
        }
    }

    public void LoadMapLights() {
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.LoadMapLights() start @" + byteBuffer.position());
        }
        ubNumColors = byteBuffer.get(); // max 3
        if (MapLoader.logEverything) {
            System.out.println("loader.core.MapContentInfo.LoadMapLights(): load colors=" + ubNumColors);
        }

        LColors = new SGPPaletteEntry[3];
        for (int i = 0; i < ubNumColors; i++) {
            LColors[i] = new SGPPaletteEntry();
            LColors[i].setByteBuffer(byteBuffer, byteBuffer.position());
            byteBuffer.position(byteBuffer.position() + LColors[i].size());
        }

        usNumLights = byteBuffer.getShort() & 0xFFFF;
        if (MapLoader.logEverything) {
            System.out.println("loader.core.MapContentInfo.LoadMapLights(): usNumLights=" + usNumLights);
        }

        lights = new LIGHT_SPRITE[usNumLights];
        lightNames = new String[usNumLights];

        for (int cnt = 0; cnt < usNumLights; cnt++) {
            LIGHT_SPRITE TmpLight = new LIGHT_SPRITE();

            if (MapLoader.logFileIO) {
                System.out.println("loader.core.MapContentInfo.LoadMapLights() load light " + (cnt + 1) + " / (" + usNumLights + ") @" + byteBuffer.position());
            }

            TmpLight.setSource(byteBuffer);

            // name?
            byte ubStrLen = byteBuffer.get();
            String name = "Light" + cnt;
            if (ubStrLen > 0) {

                byte[] arr = new byte[ubStrLen];
                byteBuffer.get(arr);

                try {
                    String newName = new String(arr, "ASCII");
                    if (!newName.isEmpty()) {
                        name = newName;
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(MapContentInfo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (MapLoader.logFileIO) {
                System.out.println("loader.core.MapContentInfo.LoadMapLights() loaded light " + (cnt + 1) + ": " + name + " @" + byteBuffer.position());
            }
            lights[cnt] = TmpLight;
            lightNames[cnt] = name;
        }
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.LoadMapLights() end @" + byteBuffer.position());
        }
    }

    @Override
    public String toString() {
        return "\tusNumLights=" + usNumLights + "\n\tusNumExitGrids=" + usNumExitGrids + "\n\tgubNumDoors=" + gubNumDoors;
    }

    public void saveHeightValues(ByteBuffer outputBuffer) {
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.saveHeightValues() start @" + outputBuffer.position());
        }
        for (int i = 0; i < settings().iWorldSize; i++) {
            outputBuffer.putShort(worldLevelData[i]);
        }
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.saveHeightValues() end @" + outputBuffer.position());
        }
    }

    public void saveRoomInfos(ByteBuffer outputBuffer) {
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.saveRoomInfos() start @" + outputBuffer.position());
        }
        for (int i = 0; i < settings().iWorldSize; i++) {
            outputBuffer.putShort(gusWorldRoomInfo[i]);
        }
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.saveRoomInfos() end @" + outputBuffer.position());
        }
    }

    public void saveMapLights(ByteBuffer outputBuffer) {
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.saveMapLights() start @" + outputBuffer.position());
            System.out.println("loader.core.MapContentInfo.saveMapLights() save " + ubNumColors + " colors...");
        }
        outputBuffer.put(ubNumColors); // max 3
        try {
            ByteArrayOutputStream itemAsBytes = new ByteArrayOutputStream();

            for (int i = 0; i < ubNumColors; i++) {
                if (MapLoader.logFileIO) System.out.println("loader.core.MapContentInfo.saveMapLights() save color " + i + " @" + outputBuffer.position());

                LColors[i].write(itemAsBytes);
                outputBuffer.put(itemAsBytes.toByteArray());
                itemAsBytes.flush();
                itemAsBytes.reset();
            }

            if (MapLoader.logFileIO)System.out.println("loader.core.MapContentInfo.saveMapLights() save lights count (" + usNumLights + ")  @" + outputBuffer.position());
            outputBuffer.putShort((short) usNumLights);

            for (int j = 0; j < lights.length; j++) {

                itemAsBytes.flush();
                itemAsBytes.reset();

                if (MapLoader.logFileIO)System.out.println("loader.core.MapContentInfo.saveMapLights() save light " + j + " @" + outputBuffer.position());
                lights[j].write(itemAsBytes);
                outputBuffer.put(itemAsBytes.toByteArray());

                if (MapLoader.logFileIO)System.out.println("loader.core.MapContentInfo.saveMapLights() save light " + j + " name length @" + outputBuffer.position());
                outputBuffer.put((byte) lightNames[j].length());
                if (lightNames[j].length() > 0) {
                    if (MapLoader.logFileIO) System.out.println("loader.core.MapContentInfo.saveMapLights() save light " + j + " name (" + lightNames[j] + ") @" + outputBuffer.position());
                    outputBuffer.put(lightNames[j].getBytes());
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(WorldItemStack.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (MapLoader.logFileIO) {
            System.out.println("loader.core.MapContentInfo.saveMapLights() end @" + outputBuffer.position());
        }
    }

    public void saveExitGrids(ByteBuffer outputBuffer) {
        outputBuffer.putShort(usNumExitGrids);

        if (usNumExitGrids > 0) {
            try {
                ByteArrayOutputStream itemAsBytes = new ByteArrayOutputStream();
                for (EXITGRID exitGrid : exitGrids) {
                    exitGrid.write(itemAsBytes);
                }
                outputBuffer.put(itemAsBytes.toByteArray());
            } catch (IOException ex) {
                Logger.getLogger(WorldItemStack.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void saveDoorTables(ByteBuffer outputBuffer) {
        outputBuffer.put((byte) gubNumDoors);

        if (gubNumDoors > 0) {
            try {
                ByteArrayOutputStream itemAsBytes = new ByteArrayOutputStream();
                for (DOOR door : doors) {
                    door.write(itemAsBytes);
                }
                outputBuffer.put(itemAsBytes.toByteArray());
            } catch (IOException ex) {
                Logger.getLogger(WorldItemStack.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void saveEdgePoints(ByteBuffer outputBuffer) {
        for (int i = 0; i < 8; i++) {
            if (mapEdgePoints[i] != null) {
                outputBuffer.putShort((short) mapEdgePoints[i].length);
                outputBuffer.putShort((short) mapMidPoints[i]);
                for (int ep : mapEdgePoints[i]) {
                    outputBuffer.putInt(ep);
                }
            } else {
                outputBuffer.putShort((short) 0);
                outputBuffer.putShort((short) 0);
            }
        }
    }

    public short[] getGusWorldRoomInfo() {
	return gusWorldRoomInfo;
    }

    
    
}
