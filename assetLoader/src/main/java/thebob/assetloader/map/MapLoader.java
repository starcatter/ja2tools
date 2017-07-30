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
package thebob.assetloader.map;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.xml.XmlLoader;

public class MapLoader {
    protected XmlLoader xml;

    public MapLoader(XmlLoader xml) {
        this.xml = xml;
    }    
    
    public MapData loadMapFile(String fileName) {
        try (final RandomAccessFile file = new RandomAccessFile(fileName, "r")) {
            FileChannel fc = file.getChannel();
            MappedByteBuffer fileBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            fileBuffer.order(ByteOrder.LITTLE_ENDIAN);

            return loadMap(fileBuffer);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public MapData loadMap(ByteBuffer data) {
        MapData map = new MapData();
        
        map.setXmlDataSource(xml);
        map.loadMap(data);
        
        return map;
    }

    // ===================================================
    public static final boolean logFileIO = false;
    public static final boolean logEverything = false;
    public static final boolean printJunk = false;

}
