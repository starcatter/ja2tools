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
package thebob.assetloader.dat.tileset;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import thebob.assetloader.dat.tileset.data.TilesetData;
import thebob.assetloader.dat.tileset.data.TilesetDataFile;

/**
 *
 * @author the_bob
 */
public class TilesetDataFileLoader {

    private static void trimByteString(byte[] in){
	    boolean eos = false;
	    for (int j = 0; j < in.length; j++) {
		if (eos) {
		    in[j] = 0;
		}
		eos = (in[j] == (byte) 0);
	    }    
    }
    
    public static TilesetDataFile load(ByteBuffer tilesetDefs) {
	int nameSize = 32;

	TilesetDataFile fileData = new TilesetDataFile();

	fileData.tilesetCount = tilesetDefs.get();   // byte
	fileData.filesPerTileset = tilesetDefs.getInt();   // int

	for (int i = 0; i < fileData.tilesetCount; i++) {
	    TilesetData tilesetData = new TilesetData();
	    byte[] tilesetNameBytes = new byte[nameSize];
	    tilesetDefs.get(tilesetNameBytes);	  
	    
	    trimByteString(tilesetNameBytes);// trim to the first \0 string terminator. Files are filled with junk.

	    tilesetData.name = new String(tilesetNameBytes, StandardCharsets.US_ASCII);
	    tilesetData.ambienceId = tilesetDefs.get();
	    tilesetData.index = i;

	    // System.out.println("\n-> " + tilesetData.name);

	    for (int j = 0; j < fileData.filesPerTileset; j++) {
		byte[] fileNameBytes = new byte[nameSize];
		tilesetDefs.get(fileNameBytes);
		
		trimByteString(fileNameBytes);
		
		String fileName = new String(fileNameBytes, StandardCharsets.US_ASCII).trim();
		// System.out.println("\t"+j+ " ->\t" + fileName);
		tilesetData.files.put(j, fileName.length() > 0 ? fileName : null);
	    }
	    fileData.tilesets.add(tilesetData);
	}
	return fileData;
    }
}
