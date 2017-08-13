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
package thebob.ja2maptool.scopes.mapping;

import de.saxsys.mvvmfx.Scope;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thebob.assetloader.common.LayerConstants;
import thebob.assetloader.tileset.Tileset;
import thebob.assetmanager.AssetManager;
import thebob.ja2maptool.model.TileCategoryMapping;
import thebob.ja2maptool.model.TileMapping;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.util.mapping.MappingIO;
import thebob.ja2maptool.util.mapping.TileMappingFileData;

/**
 *
 * @author the_bob
 */
public class TilesetMappingScope implements Scope {

    public static final String REFRESH_MAPPING_LIST = "REFRESH_MAPPING_LIST";

    public static TilesetMappingScope loadFromFile(String path, VfsAssetScope vfsAssets) {
	TileMappingFileData mappingData = MappingIO.loadTilesetMapping(path);

	AssetManager sourceAssets = vfsAssets.getOrLoadAssetManager(mappingData.getSrcConfDir(), mappingData.getSrcConf());
	AssetManager targetAssets = vfsAssets.getOrLoadAssetManager(mappingData.getDstConfDir(), mappingData.getDstConf());

	TilesetMappingScope scope = new TilesetMappingScope();
	scope.getMappingList().putAll(mappingData.getMappingList());

	scope.setTargetTilesetId(mappingData.getTargetTilesetId());
	if (targetAssets != null) {
	    scope.setTargetAssets(targetAssets);
	    scope.setTargetTileset(targetAssets.getTilesets().getTileset(mappingData.getTargetTilesetId()));
	} else {
	    // TODO: display a prompt here to either ignore this error or pick a directory!
	    System.out.println("thebob.ja2maptool.scopes.TilesetMappingScope.loadFromFile(): failed to load " + mappingData.getDstConfDir() + "/" + mappingData.getDstConf());
	}

	scope.setSourceTilesetId(mappingData.getSourceTilesetId());
	if (sourceAssets != null) {
	    scope.setSourceAssets(sourceAssets);
	    scope.setSourceTileset(sourceAssets.getTilesets().getTileset(mappingData.getSourceTilesetId()));
	} else {
	    // TODO: display a prompt here to either ignore this error or pick a directory!
	    System.out.println("thebob.ja2maptool.scopes.TilesetMappingScope.loadFromFile(): failed to load " + mappingData.getSrcConfDir() + "/" + mappingData.getSrcConf());
	}

	return scope;
    }

    public static String getTileCategortyName(int i) {
	if (i < LayerConstants.gTileSurfaceName.length) {
	    return LayerConstants.gTileSurfaceName[i];
	} else {
	    return "Extra " + (1 + i - LayerConstants.gTileSurfaceName.length);
	}
    }

    ObservableList<TileCategoryMapping> mappings = FXCollections.observableArrayList();
    Map<Integer, TileCategoryMapping> mappingList = new HashMap<Integer, TileCategoryMapping>();

    int sourceTilesetId;
    Tileset sourceTileset;
    AssetManager sourceAssets;

    int targetTilesetId;
    Tileset targetTileset;
    AssetManager targetAssets;

    public Map<Integer, TileCategoryMapping> getMappingList() {
	return mappingList;
    }

    public int getSourceTilesetId() {
	return sourceTilesetId;
    }

    public void setSourceTilesetId(int sourceTilesetId) {
	this.sourceTilesetId = sourceTilesetId;
    }

    public Tileset getSourceTileset() {
	return sourceTileset;
    }

    public void setSourceTileset(Tileset sourceTileset) {
	this.sourceTileset = sourceTileset;
    }

    public AssetManager getSourceAssets() {
	return sourceAssets;
    }

    public void setSourceAssets(AssetManager sourceAssets) {
	this.sourceAssets = sourceAssets;
    }

    public int getTargetTilesetId() {
	return targetTilesetId;
    }

    public void setTargetTilesetId(int targetTilesetId) {
	this.targetTilesetId = targetTilesetId;
    }

    public Tileset getTargetTileset() {
	return targetTileset;
    }

    public void setTargetTileset(Tileset targetTileset) {
	this.targetTileset = targetTileset;
    }

    public AssetManager getTargetAssets() {
	return targetAssets;
    }

    public void setTargetAssets(AssetManager targetAssets) {
	this.targetAssets = targetAssets;
    }

    public ObservableList<TileCategoryMapping> getMappings() {
	return mappings;
    }

}
