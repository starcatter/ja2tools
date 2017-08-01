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

import java.util.HashMap;
import java.util.Map;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.sti.StiLoader;
import thebob.assetloader.vfs.accessors.VFSAccessor;

/**
 *
 * @author the_bob
 */
public class Tileset {

    // tile indices seem to start at +1 in map layer data
    public static boolean offsetIndex = true;

    int index;
    int fileCount;
    String name;
    
    Map<Integer, Tile[]> tiles = new HashMap<Integer, Tile[]>();
    Map<Integer, VFSAccessor> sources = new HashMap<Integer, VFSAccessor>();
    Map<Integer, StiLoader> loaders = new HashMap<Integer, StiLoader>();
    Tileset fallback;

    public Tileset(int index, int fileCount, String name) {
	this.index = index;
	this.fileCount = fileCount;
	this.name = name;
    }    
    
    public void setTiles(int index, TileArray tileArray) {
	tiles.put(index, tileArray.getTiles());
	sources.put(index, tileArray.getFile());
	loaders.put(index, tileArray.getLoader());
    }

    void setName(String name) {
	this.name = name;
    }

    void setIndex(int index) {
	this.index = index;
    }

    void setFallback(Tileset t) {
	this.fallback = t;
    }

    public String getName() {
	return name;
    }

    public Tile[] getTiles(int type) {

	if (!tiles.containsKey(type)) {
	    if (fallback != null) {
		return fallback.getTiles(type);
	    } else {
		return null;
	    }
	}

	return tiles.get(type);
    }

    public Tile getTile(IndexedElement tile) {
	int type = tile.type;
	int index = tile.index;

	return getTile(type, index);
    }

    public Tile getTile(int type, int index) {
	if (!tiles.containsKey(type)) {

	    if (fallback != null) {
		return fallback.getTile(type, index);
	    } else {
		System.out.println("thebob.assetloader.tileset.Tileset.getTile(): no tile type " + type);
		return null;
	    }
	}

	if (offsetIndex) {
	    index--;
	}

	if (tiles.containsKey(type) && tiles.get(type).length <= index) {
	    // System.out.println("thebob.assetloader.tileset.Tileset.getTile(): no tile for index " + index);
	    return null;
	}

	// System.out.println( gTileSurfaceName[tile.type] + ": "+tile);	
	return tiles.get(type)[index];
    }

    public VFSAccessor getSource(int index) {
	return sources.containsKey(index) ? sources.get(index) : fallback.getSource(index);
    }

    public StiLoader getLoader(int index) {
	return loaders.get(index);
    }

    public int getFileCount() {
	return fileCount;
    }

    public void setFileCount(int fileCount) {
	this.fileCount = fileCount;
    }
    
    

}
