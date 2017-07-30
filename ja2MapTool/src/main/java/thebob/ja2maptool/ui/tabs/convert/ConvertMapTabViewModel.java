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
package thebob.ja2maptool.ui.tabs.convert;

import thebob.ja2maptool.scopes.MapScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;
import thebob.assetloader.map.core.components.MapSettings;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.components.SimplePropertyItem;
import thebob.ja2maptool.scopes.ConvertMapScope;
import thebob.ja2maptool.scopes.ItemMappingScope;
import thebob.ja2maptool.scopes.MainScope;
import static thebob.ja2maptool.scopes.MainScope.UPDATE_SCOPES;
import thebob.ja2maptool.scopes.TilesetMappingScope;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.ui.dialogs.scopeselect.ScopeSelectionDialogViewModel.ScopeSelectorType;
import static thebob.ja2maptool.ui.main.MainScreenViewModel.UPDATE_SCOPE_MENUS;
import static thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.PreviewMode.DIRECT;
import static thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.PreviewMode.ORIGINAL;
import static thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.PreviewMode.REMAPPED;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabViewModel;
import thebob.ja2maptool.util.MapRenderer;
import thebob.ja2maptool.util.MapTransformer;

@ScopeProvider(scopes = {MapScope.class})   // we need to provide the scope for the map viewer to load, it will be replaced once setPreviewModel() is called
public class ConvertMapTabViewModel implements ViewModel {

    public static final String MAP_LOADED = "MAP_LOADED";
    public static final String TILE_MAPPING_LOADED = "TILE_MAPPING_LOADED";
    public static final String ITEM_MAPPING_LOADED = "ITEM_MAPPING_LOADED";

    @InjectScope
    VfsAssetScope vfsAssets;

    @InjectScope
    MainScope mainScreen;

    @InjectScope
    ConvertMapScope convertMapScope;
        
    MapViewerTabViewModel mapViewer;

    PreviewMode rendererMode = DIRECT;

    void setPreviewModel(MapViewerTabViewModel viewModel) {
	mapViewer = viewModel;
	mapViewer.setMapScope(convertMapScope.getMap());
	mapViewer.initialize();
    }

    public static enum PreviewMode {
	ORIGINAL,
	REMAPPED,
	DIRECT
    }

    public void initialize() {	
	convertMapScope.getMap().subscribe(convertMapScope.getMap().MAP_UPDATED, (key, values) -> {
	    updateRenderer(true);
	    mainScreen.publish(UPDATE_SCOPES);
	});

	convertMapScope.subscribe(ConvertMapScope.SCOPE_SELECTED, (key, values) -> {
	    ScopeSelectorType scopeType = (ScopeSelectorType) values[0];
	    switch (scopeType) {
		case SCOPE_MAP_ITEMS: {
		    ItemMappingScope loadedScope = (ItemMappingScope) values[1];
		    convertMapScope.setItemMapping(loadedScope);

		    publish(ITEM_MAPPING_LOADED);
		}
		break;
		case SCOPE_MAP_TILES: {
		    TilesetMappingScope loadedScope = (TilesetMappingScope) values[1];
		    convertMapScope.setTilesetMapping(loadedScope);

		    publish(TILE_MAPPING_LOADED);
		}
	    }
	    updateRenderer(true);
	});
    }

    // preview window renderer handlers
    
    public void updateRenderer(boolean centerMap) {
	if (convertMapScope.getMap() == null || convertMapScope.getMap().getMapData() == null) {
	    return;
	}
	
	mapViewer.updateRenderer(centerMap);

	if (convertMapScope.getTilesetMapping() != null && ( rendererMode == DIRECT || rendererMode == REMAPPED )) {
	    Tileset targetTileset = convertMapScope.getTilesetMapping().getTargetTileset();

	    mapViewer.getRenderer().setTileset(targetTileset);
	    
	    if (rendererMode == REMAPPED) {
		// TODO use MapTransformer to do this and remove the remapping code from the renderer
		mapViewer.getRenderer().applyRemapping(convertMapScope.getTilesetMapping().getMappingList());
	    }
	    mapViewer.getRenderer().moveWindow(0, 0);
	}
	
	publish(MAP_LOADED);	
    }

    void setRendererMode(PreviewMode previewMode) {
	rendererMode = previewMode;
    }
    
    // load/unload mappings
    
    void loadTileMapping(String path) {
	TilesetMappingScope mappingScope = TilesetMappingScope.loadFromFile(path, vfsAssets);
	convertMapScope.setTilesetMapping(mappingScope);

	mainScreen.registerTilesetMappingScope(mappingScope);
	publish(TILE_MAPPING_LOADED);
    }

    void loadItemMapping(String path) {
	ItemMappingScope mappingScope = ItemMappingScope.loadFromFile(path, vfsAssets);
	convertMapScope.setItemMapping(mappingScope);

	mainScreen.registerItemMappingScope(mappingScope);
	publish(ITEM_MAPPING_LOADED);
    }

    void unloadImageMapping() {
	convertMapScope.setItemMapping(null);
	publish(ITEM_MAPPING_LOADED);
    }

    void unloadTileMapping() {
	convertMapScope.setTilesetMapping(null);
	publish(TILE_MAPPING_LOADED);
    }

    // export the map with remapped items/tiles
    
    void saveMap(String path) {
	MapTransformer transformer = new MapTransformer(convertMapScope);
	transformer.saveTo(path);
    }

    // property display handling
    
    void updateMapProps(ObservableList<PropertySheet.Item> items) {
	items.clear();

	MapSettings settings = convertMapScope.getMap().getMapData().getSettings();

	items.add(new SimplePropertyItem("Map file", convertMapScope.getMap().getMapName(), "General", ""));
	items.add(new SimplePropertyItem("Tileset Id", settings.iTilesetID + "", "General", ""));
	items.add(new SimplePropertyItem("Basement", settings.gfBasement + "", "General", ""));
	items.add(new SimplePropertyItem("Cave", settings.gfCaves + "", "General", ""));

	items.add(new SimplePropertyItem("Rows", settings.iRowSize + "", "Size", ""));
	items.add(new SimplePropertyItem("Columns", settings.iColSize + "", "Size", ""));

	items.add(new SimplePropertyItem("Major", settings.dMajorMapVersion + "", "Version", ""));
	items.add(new SimplePropertyItem("Minor", settings.ubMinorMapVersion + "", "Version", ""));
	items.add(new SimplePropertyItem("MapInfo", settings.mapInfo.ubMapVersion.get() + "", "Version", "The version stored in map info struct"));

	items.add(new SimplePropertyItem("Soldiers", settings.mapInfo.ubNumIndividuals.get() + "", "Stats", ""));

    }

    void updateTileMappingProps(ObservableList<PropertySheet.Item> items) {
	items.clear();
	if (convertMapScope.getTilesetMapping() != null) {
	    items.add(new SimplePropertyItem("Source tileset id", convertMapScope.getTilesetMapping().getSourceTilesetId() + "", "Source", ""));
	    items.add(new SimplePropertyItem("Source config", convertMapScope.getTilesetMapping().getSourceAssets().getVfsConfigName(), "Source", ""));

	    items.add(new SimplePropertyItem("Target tileset id", convertMapScope.getTilesetMapping().getTargetTilesetId() + "", "Target", ""));
	    items.add(new SimplePropertyItem("Target config", convertMapScope.getTilesetMapping().getTargetAssets().getVfsConfigName(), "Target", ""));
	} else {
	    items.add(new SimplePropertyItem("Loaded", "No", "", ""));
	}
    }

    void updateItemMappingProps(ObservableList<PropertySheet.Item> items) {
	items.clear();
	if (convertMapScope.getItemMapping() != null) {
	    items.add(new SimplePropertyItem("Remapped items", convertMapScope.getItemMapping().getMapping().size() + "", "General", ""));
	} else {
	    items.add(new SimplePropertyItem("Loaded", "No", "", ""));
	}
    }

    // scope access
    
    public ConvertMapScope getConvertMapScope() {
	return convertMapScope;
    }

    public MapScope getMapScope() {
	return convertMapScope.getMap();
    }

}
