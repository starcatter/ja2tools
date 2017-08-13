/* 
 * The MIT License
 *
 * Copyright 2017 starcatter.
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
package thebob.ja2maptool.scopes.map;

import de.saxsys.mvvmfx.Scope;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.tileset.Tileset;
import thebob.assetmanager.AssetManager;
import thebob.ja2maptool.util.compositor.SelectedTiles;

/**
 *
 * @author the_bob
 */
public class MapScope implements Scope {
    public static final String MAP_UPDATED = "MAP_UPDATED";
    public static final String SELECTION_UPDATED = "SELECTION_UPDATED";
    
    public enum mapLoadMode{
	From_VFS,
	From_File
    }
    
    String mapName = null;	    // short name for the map
    String mapAssetPath = null;	    // vfs path for the map
    mapLoadMode loadMode = null;
    
    AssetManager mapAssets = null;
    
    Integer tilesetId = null;
    Tileset tileset = null;
    MapData mapData = null;
    
    SelectedTiles selection = null;

    public SelectedTiles getSelection() {
	return selection;
    }

    public void setSelection(SelectedTiles selection) {
	this.selection = selection;
	publish(SELECTION_UPDATED);
    }
    
    public String getMapName() {
	return mapName;
    }

    public void setMapName(String mapName) {
	this.mapName = mapName;
    }

    public String getMapAssetPath() {
	return mapAssetPath;
    }

    public void setMapAssetPath(String mapAssetPath) {
	this.mapAssetPath = mapAssetPath;
    }

    public mapLoadMode getLoadMode() {
	return loadMode;
    }

    public void setLoadMode(mapLoadMode loadMode) {
	this.loadMode = loadMode;
    }

    public AssetManager getMapAssets() {
	return mapAssets;
    }

    public void setMapAssets(AssetManager mapAssets) {
	this.mapAssets = mapAssets;
    }

    public Tileset getTileset() {
	return tileset;
    }

    public void setTileset(Tileset tileset) {
	this.tileset = tileset;
    }

    public MapData getMapData() {
	return mapData;
    }

    public void setMapData(MapData mapData) {
	this.mapData = mapData;
    }

    public Integer getTilesetId() {
	return tilesetId;
    }

    public void setTilesetId(Integer tilesetId) {
	this.tilesetId = tilesetId;
    }

    @Override
    public String toString() {
	return "MapScope{" + "mapName=" + mapName + ", loadMode=" + loadMode + ", tilesetId=" + tilesetId + ", selection=" + selection + '}';
    }
    
}
