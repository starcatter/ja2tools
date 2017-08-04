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
import thebob.ja2maptool.scopes.mapping.TilesetMappingScope;
import de.saxsys.mvvmfx.ViewModel;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import thebob.assetloader.tileset.Tile;
import thebob.assetloader.vfs.accessors.VFSAccessor;
import thebob.ja2maptool.model.TileCategoryMapping;
import thebob.ja2maptool.model.TileMapping;
import thebob.ja2maptool.scopes.MainScope;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.util.mapping.MappingIO;
import thebob.ja2maptool.util.tilesearch.TileSearchResult;
import thebob.ja2maptool.util.tilesearch.TileSearch;

/**
 *
 * @author the_bob
 */
public class TilesetMappingTabViewModel implements ViewModel {

    // notification names
    public static final String SHOW_TILE_SELECTOR = "SHOW_TILE_SELECTOR";
    public static final String REFRESH_LIST = "REFRESH_LIST";
    public static final String REFRESH_TABLE = "REFRESH_TABLE";
    public static final String REBUILD_TABLE = "REBUILD_TABLE";

    @InjectScope
    VfsAssetScope vfsAssets;
    @InjectScope
    MainScope mainScreen;
    Integer selectedCategory = 0;

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

	System.out.println("sourceFiles: " + sourceFiles);
	System.out.println("targetFiles: " + targetFiles);

	int j = 0;
	for (int i = 0; i < sourceFiles; i++) {
	    Tile[] sourceTiles = mappingScope.getSourceTileset().getTiles(i);

	    if (sourceTiles == null || sourceTiles.length < 1) {
		System.out.println("thebob.ja2maptool.ui.tabs.mapping.tileset.TilesetMappingTabViewModel.initTilesetMappingLists(): skip src type " + i);
		continue;
	    }

	    Tile[] targetTiles = mappingScope.getTargetTileset().getTiles(j);
	    if (targetTiles == null || targetTiles.length < 1) {
		System.out.println("thebob.ja2maptool.ui.tabs.mapping.tileset.TilesetMappingTabViewModel.initTilesetMappingLists(): skip dst type " + j);
	    };

	    ObservableList<TileMapping> mappingListForType = buildMappingListForTileType(i, j, sourceTiles.length, targetTiles.length);

	    TileCategoryMapping tileCat = new TileCategoryMapping(i, TilesetMappingScope.getTileCategortyName(i), TileCategoryMapping.RemapStatus.None, mappingListForType);
	    mappingScope.getMappings().add(tileCat);
	    mappingScope.getMappingList().put(i, tileCat);

	    if (j < targetFiles - 1) {
		j++;
	    }
	}

    }

    public ObservableList<TileMapping> buildMappingListForTileType(int type, int dstType, int srcLength, int targetLength) {
	ObservableList<TileMapping> mappingListForType = FXCollections.observableArrayList();

	int j = 0;
	for (int i = 0; i < srcLength; i++) {

	    TileMapping tm = new TileMapping(type, i, dstType, j);
	    mappingListForType.add(tm);

	    if (targetLength > j + 1) {
		j++;
	    }
	}

	return mappingListForType;
    }

    public ObservableList<TileCategoryMapping> getTileCategories() {
	return mappingScope.getMappings();
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
	return mappingScope.getMappingList().get(i).getMappings();
    }

    void mapCurrentTypeTo(int selectedIndex, int startIndex) {
	if (lastMappingTypeRequested == -1) {
	    return;
	}
	if (startIndex == -1) {
	    startIndex = 0;
	}

	int targetIndex = selectedIndex > 0 ? selectedIndex : lastMappingTypeRequested;

	ObservableList<TileMapping> currentType = mappingScope.getMappingList().get(lastMappingTypeRequested).getMappings();
	ObservableList<TileMapping> targetType = mappingScope.getMappingList().get(targetIndex).getMappings();

	int j = 0;
	for (int i = startIndex; i < currentType.size() && j < targetType.size(); i++, j++) {
	    TileMapping target = targetType.get(j);
	    currentType.get(i).setTargetIndex(target.getTargetIndex());
	    currentType.get(i).setTargetType(target.getTargetType());
	}

	publish(REFRESH_TABLE);
    }

    void autoMap(double autoSwapDistance) {

	int srcCount = mappingScope.getSourceAssets().getTilesets().getNumFiles();
	int dstCount = mappingScope.getTargetAssets().getTilesets().getNumFiles();

	Map< VFSAccessor, Integer> fileMap = new HashMap< VFSAccessor, Integer>();
	Map< String, Integer> fileNameMap = new HashMap< String, Integer>();
	TileSearch cache = new TileSearch(mappingScope.getTargetTileset(), TileSearch.SearchMethod.HistogramSimple);

	for (int i = 0; i < dstCount; i++) {
	    fileMap.put(mappingScope.getTargetTileset().getSource(i), i);
	    String fileName = Paths.get(mappingScope.getTargetTileset().getSource(i).getPath()).getFileName().toString();
	    fileNameMap.put(fileName, i);
	}

	ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 100L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(2048));
	executor.setThreadFactory(new ThreadFactory() {
	    @Override
	    public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setDaemon(true);
		return t;
	    }
	});

	for (int sourceIndex = 0; sourceIndex < srcCount; sourceIndex++) {
	    VFSAccessor file = mappingScope.getSourceTileset().getSource(sourceIndex);
	    String fileName = Paths.get(mappingScope.getSourceTileset().getSource(sourceIndex).getPath()).getFileName().toString();
	    Integer targetIndex = fileMap.get(file);

	    if (targetIndex != null) {
		if (targetIndex == sourceIndex) {
		    //System.out.println("same files @" + sourceIndex);
		    mappingScope.getMappingList().get(sourceIndex).setStatus(TileCategoryMapping.RemapStatus.Same);
		} else {
		    //System.out.println("found matching files " + sourceIndex + " -> " + targetIndex);
		    mappingScope.getMappingList().get(sourceIndex).setStatus(TileCategoryMapping.RemapStatus.Matched);
		    for (TileMapping mapping : mappingScope.getMappingList().get(sourceIndex).getMappings()) {
			mapping.setTargetType(targetIndex);
			mapping.setMappingMode(TileMapping.MappingMode.MatchedFile);
		    }
		}
	    } else {
		Integer targetFileIndex = fileNameMap.get(fileName);
		if (targetFileIndex != null) {
		    if (targetFileIndex == sourceIndex) {
			//System.out.println("same file names @" + sourceIndex);
			mappingScope.getMappingList().get(sourceIndex).setStatus(TileCategoryMapping.RemapStatus.Same);
		    } else {
			//System.out.println("found matching file names " + sourceIndex + " -> " + targetFileIndex);
			mappingScope.getMappingList().get(sourceIndex).setStatus(TileCategoryMapping.RemapStatus.Matched);
			for (TileMapping mapping : mappingScope.getMappingList().get(sourceIndex).getMappings()) {
			    mapping.setTargetType(targetFileIndex);
			    mapping.setMappingMode(TileMapping.MappingMode.MatchedFile);
			}
		    }
		} else {
		    //System.out.println("no match @" + sourceIndex + " for " + fileName);
		    mappingScope.getMappingList().get(sourceIndex).setStatus(TileCategoryMapping.RemapStatus.Manual);
		    Object saveSync = new Object();

		    for (Tile tile : mappingScope.getSourceTileset().getTiles(sourceIndex)) {

			final TileCategoryMapping categoryMapping = mappingScope.getMappingList().get(sourceIndex);

			Task<TileSearchResult> loadTask = new Task<TileSearchResult>() {
			    @Override
			    protected TileSearchResult call() throws Exception {
				return cache.tileSearch(tile);
			    }
			};

			loadTask.setOnSucceeded(event -> {
			    TileSearchResult results = loadTask.getValue();
			    if (results != null && results.getResults().size() > 0) {
				synchronized (saveSync) {
				    categoryMapping.setStatus(TileCategoryMapping.RemapStatus.Found);
				    TileMapping mapping = categoryMapping.getMappings().get(tile.getIndex());

				    mapping.setAutoMapResult(results);
				    double bestResultDistance = results.getBestResult();

				    if (bestResultDistance <= autoSwapDistance) {
					int bestIndex = results.getResultValues().indexOf(bestResultDistance);

					Tile bestTile = results.getResults().get(bestIndex);

					mapping.setSourceIndex(tile.getIndex());
					mapping.setSourceType(tile.getType());

					mapping.setTargetIndex(bestTile.getIndex());
					mapping.setTargetType(bestTile.getType());
					mapping.setMappingMode(TileMapping.MappingMode.AutoMatched);

					System.out.println("Set mapping: " + mapping + " for tile " + tile);
				    }
				    publish(REFRESH_LIST);
				    publish(REFRESH_TABLE);

				}
			    }
			});

			executor.submit(loadTask);
		    }
		}
	    }
	}
	/*
	Platform.runLater(() -> {
	    executor.shutdown();
	});*/

	publish(REFRESH_LIST);
	publish(REFRESH_TABLE);
    }

    ObservableList<TileMapping> getCurrentMappings() {
	if (selectedCategory != null) {
	    return mappingScope.getMappingList().get(selectedCategory).getMappings();
	}
	return null;
    }

    public Integer getSelectedCategory() {
	return selectedCategory;
    }

    public void setSelectedCategory(Integer selectedCategory) {
	lastMappingTypeRequested = selectedCategory;
	this.selectedCategory = selectedCategory;
    }

}
