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
package thebob.assetloader.tileset;

import ja2.xml.tilesets.TilesetDef;
import ja2.xml.tilesets.TilesetFile;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import thebob.assetloader.dat.tileset.data.TilesetData;
import thebob.assetloader.vfs.VFSConfig;
import thebob.assetloader.vfs.accessors.VFSAccessor;
import thebob.assetloader.xml.XmlLoader;

/**
 *
 * @author the_bob
 */
public class TilesetLoader {

    final VFSConfig vfs;

    Tileset tilesetZero = null;
    int filesPerTileset;

    Map<String, TileArray> tileCache = new HashMap<String, TileArray>();

    public TilesetLoader(VFSConfig vfs, int filesPerTileset) {
	this.vfs = vfs;
	this.filesPerTileset = filesPerTileset;
    }
    
    // 1 - load def for tileset 0
    // 2 - load def fot desired tileset over it
    // 3 - loop through files to produce gTileTypeStartIndex[ ];

    // TODO: de-duplicate the code in these load methods.
    public Tileset loadTilesetFromXmlDef(TilesetDef tileDef) {
	int index = tileDef.getIndex();
	String name = tileDef.getName();

	Tileset tileset = new Tileset(index,filesPerTileset,name);

	VFSAccessor fileBuffer = null;

	// System.out.println("tileDef " + index + ":" + tileDef.getName());
	for (TilesetFile fileDef : tileDef.getFiles().getFile()) {

	    TileArray tiles = null;
	    String fileName = fileDef.getValue();
	    int fileIndex = fileDef.getIndex();

	    String fPath = ("\\TILESETS\\" + index + "\\" + fileName).toUpperCase();
	    String fPathFallback = ("\\TILESETS\\0\\" + fileName).toUpperCase();

	    fileBuffer = vfs.getFileAccess(fPath);

	    if (fileBuffer != null) {

		if (tileCache.containsKey(fPath)) {
		    tiles = tileCache.get(fPath);
		} else {
		    tiles = new TileArray(fileBuffer, fileIndex);
		    tileCache.put(fPath, tiles);
		}

	    } else {
		fileBuffer = vfs.getFileAccess(fPathFallback);

		if (fileBuffer != null) {

		    if (tileCache.containsKey(fPathFallback)) {
			tiles = tileCache.get(fPathFallback);
		    } else {
			tiles = new TileArray(fileBuffer, fileIndex);
			tileCache.put(fPathFallback, tiles);
		    }

		} else {
		    // System.out.println(index + ":" + tileDef.getName() + ": Tileset file not found:\t" + fileIndex + " -> " + fileDef.getValue());
		}

	    }

	    if (tiles != null) {
		tileset.setTiles(fileIndex, tiles);
	    }

	}

	if (index == 0) {
	    tilesetZero = tileset;
	} else {
	    tileset.setFallback(tilesetZero);
	}

	return tileset;
    }

    public Tileset loadTilesetFromData(TilesetData tilesetData) {
	Tileset tileset = new Tileset(tilesetData.index, tilesetData.files.size(), tilesetData.name);

	for (int fileIndex = 0; fileIndex < tilesetData.files.size(); fileIndex++) {
	    String fileName = tilesetData.files.get(fileIndex);

	    if (fileName == null) {
		continue;
	    }

	    VFSAccessor fileBuffer = null;
	    String fPath = ("\\TILESETS\\" + tilesetData.index + "\\" + fileName).toUpperCase();
	    String fPathFallback = ("\\TILESETS\\0\\" + fileName).toUpperCase();

	    fileBuffer = vfs.getFileAccess(fPath);

	    if (fileBuffer == null) {
		fPath = fPathFallback;
		fileBuffer = vfs.getFileAccess(fPathFallback);
	    }

	    if (fileBuffer != null) {
		TileArray tiles = null;
		
		if (tileCache.containsKey(fPath)) {
		    tiles = tileCache.get(fPath);
		} else {
		    tiles = new TileArray(fileBuffer, fileIndex);
		    tileCache.put(fPath, tiles);
		}
		
		if (tiles != null) {
		    tileset.setTiles(fileIndex, tiles);
		}
	    }
	}
	
	if (tilesetData.index == 0) {
	    tilesetZero = tileset;
	} else {
	    tileset.setFallback(tilesetZero);
	}

	return tileset;
    }

}
