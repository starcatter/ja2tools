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
package thebob.ja2maptool.ui.tabs.mapping.tileset;

import de.saxsys.mvvmfx.InjectScope;
import thebob.ja2maptool.scopes.TilesetMappingScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import static thebob.assetloader.common.LayerConstants.gTileSurfaceName;
import thebob.assetloader.tileset.Tile;
import thebob.ja2maptool.model.TileMapping;
import thebob.ja2maptool.scopes.MainScope;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.util.MappingIO;
import thebob.ja2maptool.util.TileMappingFileData;

/**
 *
 * @author the_bob
 */
public class TilesetMappingTabViewModel implements ViewModel {

    // notification names
    public static final String SHOW_TILE_SELECTOR = "SHOW_TILE_SELECTOR";
    public static final String REFRESH_TABLE = "REFRESH_TABLE";
    public static final String REBUILD_TABLE = "REBUILD_TABLE";

    @InjectScope
    VfsAssetScope vfsAssets;
    @InjectScope
    MainScope mainScreen;
    
    @InjectScope
    private TilesetMappingScope mappingScope;

    public TilesetMappingScope getMappingScope() {
	return mappingScope;
    }

    public void initialize() {
	mappingScope.subscribe(TilesetMappingScope.REFRESH_MAPPING_LIST, (key, payload) -> {
	    publish(REBUILD_TABLE);
	});
    }

    void saveMapping(String fileName) {
	MappingIO.saveTilesetMapping(fileName, mappingScope);
    }
   
    void loadMapping(String fileName) {
	TilesetMappingScope scope = TilesetMappingScope.loadFromFile(fileName, vfsAssets);
	mainScreen.freeTilesetMappingScope(mappingScope);
	mainScreen.registerTilesetMappingScope(scope);	
	mappingScope = scope;
	publish(REFRESH_TABLE);
    }

    public void initTilesetMappingLists() {

	// init tile mappings
	int sourceFiles = mappingScope.getSourceAssets().getTilesets().getNumFiles();
	int targetFiles = mappingScope.getTargetAssets().getTilesets().getNumFiles();

	System.out.println("sourceFiles: "+ sourceFiles);
	System.out.println("targetFiles: "+ targetFiles);
	
	int j = 0;
	for (int i = 0; i < sourceFiles; i++) {
	    Tile[] sourceTiles = mappingScope.getSourceTileset().getTiles(i);
	    
	    if (sourceTiles == null || sourceTiles.length < 1) {
		System.out.println("thebob.ja2maptool.ui.tabs.mapping.tileset.TilesetMappingTabViewModel.initTilesetMappingLists(): skip src type "+ i);
		continue;
	    }

	    Tile[] targetTiles = mappingScope.getTargetTileset().getTiles(j);
	    if (targetTiles == null || targetTiles.length < 1) {
		System.out.println("thebob.ja2maptool.ui.tabs.mapping.tileset.TilesetMappingTabViewModel.initTilesetMappingLists(): skip dst type "+ j);
	    };

	    ObservableList<TileMapping> mappingListForType = buildMappingListForTileType(i, j, sourceTiles.length, targetTiles.length);

	    mappingScope.getMappingList().put(i, mappingListForType);
	    
	    if( j < targetFiles - 1 ) j++;
	}

    }

    public ObservableList<thebob.ja2maptool.model.TileMapping> buildMappingListForTileType(int type, int dstType, int srcLength, int targetLength) {
	ObservableList<thebob.ja2maptool.model.TileMapping> mappingListForType = FXCollections.observableArrayList();

	int j = 0;
	for (int i = 0; i < srcLength; i++) {

	    TileMapping tm = new thebob.ja2maptool.model.TileMapping(type, i, dstType, j);
	    mappingListForType.add(tm);

	    if (targetLength > j + 1) {
		j++;
	    }
	}

	return mappingListForType;
    }

    public ObservableList<String> getTileCategories() {
	ObservableList<String> tileTypes = FXCollections.observableArrayList();
	int files = mappingScope.getSourceAssets().getTilesets().getNumFiles();
	for (int i = 0; i < files; i++) {
	    if (i < gTileSurfaceName.length) {
		tileTypes.add(gTileSurfaceName[i]);
	    } else {
		tileTypes.add("Extra " + (1 + i - gTileSurfaceName.length));
	    }
	}

	return FXCollections.observableArrayList(tileTypes);
    }

    Image getSourceTileImage(int type, int index) {
	if (type >= 0 && index >= 0) {
	    Tile tile = mappingScope.getSourceAssets().getTilesets().getTileset(mappingScope.getSourceTilesetId()).getTile(type, index);
	    if (tile != null) {
		return tile.getImage();
	    }
	}
	return null;
    }

    Image getTargetTileImage(int type, int index) {
	if (type >= 0 && index >= 0) {
	    Tile tile = mappingScope.getTargetAssets().getTilesets().getTileset(mappingScope.getTargetTilesetId()).getTile(type, index);
	    if (tile != null) {
		return tile.getImage();
	    }
	}
	return null;
    }

    int lastMappingTypeRequested = -1;
    ObservableList<TileMapping> getMappingForType(int i) {
	lastMappingTypeRequested = i;
	return mappingScope.getMappingList().get(i);
    }

    void mapCurrentTypeTo(int selectedIndex) {
	ObservableList<TileMapping> currentType = mappingScope.getMappingList().get(lastMappingTypeRequested);
	for( TileMapping mapping : currentType ){
	    mapping.setTargetType(selectedIndex);
	}
	publish(REFRESH_TABLE);
    }
}
