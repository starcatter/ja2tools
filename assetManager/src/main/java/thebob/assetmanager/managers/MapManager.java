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
package thebob.assetmanager.managers;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import thebob.assetloader.map.MapLoader;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.vfs.VFSConfig;
import thebob.assetmanager.AssetManager;

/**
 *
 * @author the_bob
 */
public class MapManager extends VFSContextBoundManager {

    List<String> mapFiles = new ArrayList<String>();

    VFSConfig vfs;
    MapLoader loader;

    public MapManager(AssetManager context) {
	super(context);
    }

    @Override
    public boolean init() {
	context.getVfs().getFileList().stream().filter(f -> f.startsWith("\\MAPS\\")).sorted().forEach(str -> {
	    mapFiles.add(str);
	});

	vfs = context.getVfs();
	loader = new MapLoader(context.getXml());

	return true;
    }

    public List<String> getMapFiles() {
	return mapFiles;
    }

    public MapData loadMap(String mapName) {
	ByteBuffer map = vfs.getFile(mapName);
	if (map != null) {
	    return loader.loadMap(map);
	} else {
	    throw new RuntimeException("Cannot find map asset: " + mapName);
	}
    }

    public MapData loadMapFile(String mapName) {
	return loader.loadMapFile(mapName);
    }

    public MapData loadMapData(ByteBuffer map) {
	return loader.loadMap(map);
    }

    @Override
    public String toString() {
	return "MapManager{" + "mapFiles=" + mapFiles.size() + "}";
    }

}
