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

import thebob.ja2maptool.scopes.map.MapScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.controlsfx.control.PropertySheet;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.map.core.components.MapSettings;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.components.SimplePropertyItem;
import thebob.ja2maptool.scopes.map.ConvertMapScope;
import thebob.ja2maptool.scopes.mapping.ItemMappingScope;
import thebob.ja2maptool.scopes.MainScope;
import static thebob.ja2maptool.scopes.MainScope.UPDATE_SCOPES;
import thebob.ja2maptool.scopes.mapping.TilesetMappingScope;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.scopes.map.MapSnippetScope;
import thebob.ja2maptool.ui.dialogs.scopeselect.ScopeSelectionDialogViewModel.ScopeSelectorType;
import static thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.PreviewMode.DIRECT;
import static thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.PreviewMode.REMAPPED;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabViewModel;
import thebob.ja2maptool.util.MapTransformer;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SnippetIO;
import thebob.ja2maptool.util.map.controller.editors.converter.IMapConverterController;

@ScopeProvider(scopes = {MapScope.class})   // we need to provide the scope for the map viewer to load, it will be replaced once setPreviewModel() is called
public class ConvertMapTabViewModel implements ViewModel {

    public static final String MAP_LOADED = "MAP_LOADED";
    public static final String TILE_MAPPING_LOADED = "TILE_MAPPING_LOADED";
    public static final String ITEM_MAPPING_LOADED = "ITEM_MAPPING_LOADED";
    public static final String SNIPPET_LIST_INIT = "SNIPPET_LIST_INIT";

    @InjectScope
    VfsAssetScope vfsAssets;

    @InjectScope
    MainScope mainScreen;

    @InjectScope
    ConvertMapScope convertMapScope;

    MapViewerTabViewModel mapViewer = null;
    IMapConverterController converter = null;

    PreviewMode rendererMode = DIRECT;

    // compositor tab
    StringProperty selectionTextProperty = new SimpleStringProperty();
    StringProperty totalTextProperty = new SimpleStringProperty();
    StringProperty landTextProperty = new SimpleStringProperty();
    StringProperty structTextProperty = new SimpleStringProperty();
    StringProperty objectTextProperty = new SimpleStringProperty();
    StringProperty shadowsTextProperty = new SimpleStringProperty();
    StringProperty roofTextProperty = new SimpleStringProperty();
    StringProperty onRoofTextProperty = new SimpleStringProperty();

    // compositor checkboxes
    BooleanProperty snippet_land = new SimpleBooleanProperty(true);
    BooleanProperty snippet_objects = new SimpleBooleanProperty(true);
    BooleanProperty snippet_structures = new SimpleBooleanProperty(true);
    BooleanProperty snippet_shadows = new SimpleBooleanProperty(true);
    BooleanProperty snippet_roofs = new SimpleBooleanProperty(true);
    BooleanProperty snippet_onRoof = new SimpleBooleanProperty(true);

    StringProperty snippet_name = new SimpleStringProperty();

    void setPreviewModel(MapViewerTabViewModel viewModel) {
	mapViewer = viewModel;
	mapViewer.setMapScope(convertMapScope.getMap());
	mapViewer.setViewerMode(MapViewerTabViewModel.MapViewerMode.Editor);
	mapViewer.initialize();
	// hook up the map converter interface to the DisplayManager
	converter = mapViewer.getRenderer().connectConverter(convertMapScope);
    }

    public static enum PreviewMode {
	ORIGINAL,
	REMAPPED,
	DIRECT
    }

    public void initialize() {
	convertMapScope.getMap().subscribe(MapScope.MAP_UPDATED, (key, values) -> {
	    updateRenderer(true);
	    mainScreen.publish(UPDATE_SCOPES);
	});

	convertMapScope.getMap().subscribe(MapScope.SELECTION_UPDATED, (key, values) -> {
	    System.out.println("thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.initialize() -> updateSelection()");
	    updateSelection();
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

	// handle hooking up this converter to loaded snippets... based on the remap snippets checkbox
	convertMapScope.getRemap_out().addListener(state -> {
	    if (convertMapScope.getSnippets() != null) {
		if (convertMapScope.getRemap_out().get()) {
		    for (SelectedTiles snippet : convertMapScope.getSnippets().getSnippets()) {
			snippet.setConverter(convertMapScope);
		    }
		} else {
		    for (SelectedTiles snippet : convertMapScope.getSnippets().getSnippets()) {
			snippet.setConverter(null);
		    }
		}
		convertMapScope.publish(ConvertMapScope.SNIPPETS_UPDATED);
	    }
	});

	convertMapScope.getRemap_sel().addListener(state -> {
	    convertMapScope.publish(ConvertMapScope.SNIPPETS_UPDATED);
	});

    }
    // preview window renderer handlers

    public void updateRenderer(boolean centerMap) {
	if (convertMapScope.getMap() == null || convertMapScope.getMap().getMapData() == null) {
	    return;
	}

	mapViewer.updateRenderer(centerMap);

	if (convertMapScope.getTilesetMapping() != null && (rendererMode == DIRECT || rendererMode == REMAPPED)) {
	    Tileset targetTileset = convertMapScope.getTilesetMapping().getTargetTileset();

	    mapViewer.getRenderer().setMapTileset(targetTileset);

	    if (rendererMode == REMAPPED) {
		int oldX = mapViewer.getViewer().getWindowOffsetX();
		int oldY = mapViewer.getViewer().getWindowOffsetY();

		MapTransformer transformer = new MapTransformer(convertMapScope);
		mapViewer.getRenderer().loadMap(transformer.getRemappedData(true));

		if (!centerMap) {
		    mapViewer.getViewer().setWindowOffsetX(oldX);
		    mapViewer.getViewer().setWindowOffsetY(oldY);
		}
	    }
	    // mapViewer.getRenderer().moveWindow(0, 0); // <- renderer should update itself
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

    // selection snippets
    private void updateSelection() {
	SelectedTiles selection = convertMapScope.getMap().getSelection();

	if (selection != null) {
	    StringProperty[] layerPropertyMap = new StringProperty[]{landTextProperty, objectTextProperty, structTextProperty, shadowsTextProperty, roofTextProperty, onRoofTextProperty};

	    int startX = selection.getStartX();
	    int endX = selection.getEndX();
	    int startY = selection.getStartY();
	    int endY = selection.getEndY();

	    int total = (1 + endX - startX) * (1 + endY - startY);

	    selectionTextProperty.set("(" + startX + "," + startY + ") - (" + endX + "," + endY + ")");
	    totalTextProperty.set(total + " cells");

	    int layerIndex = 0;

	    for (IndexedElement[][] layer : selection.getLayers()) {

		if (layerPropertyMap.length <= layerIndex) {
		    System.out.println("thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.updateSelection(): Got more layers than we can handle today! (" + layerIndex + ")");
		    layerIndex++;
		    continue;
		}

		if (layer != null) {
		    int cnt = 0;
		    for (IndexedElement[] stack : layer) {
			if (stack != null) {
			    cnt += stack.length;
			}
		    }
		    layerPropertyMap[layerIndex].set(cnt + " tiles");
		} else {
		    layerPropertyMap[layerIndex].set("(empty)");
		}
		layerIndex++;
	    }
	}
	convertMapScope.publish(ConvertMapScope.SNIPPETS_UPDATED);
    }

    ObservableList<SelectedTiles> getSnippetList() {
	return convertMapScope.getSnippets().getSnippets();
    }

    // snippet management
    void addSnippet() {
	if (convertMapScope.getMap() == null || convertMapScope.getMap().getSelection() == null) {
	    return;
	}

	if (convertMapScope.getSnippets() == null) {
	    convertMapScope.setSnippets(new MapSnippetScope());
	    publish(SNIPPET_LIST_INIT);
	}

	SelectedTiles srcSnippet = convertMapScope.getMap().getSelection();
	SelectedTiles snippet = new SelectedTiles(srcSnippet); // clone this snippet in case it gets saved more than once... 

	if (convertMapScope.getRemap_in().get()) {
	    MapTransformer transformer = new MapTransformer(convertMapScope);
	    transformer.remapSnippet(snippet);
	}

	if ((snippet_name != null) && (snippet_name.get() != null) && (snippet_name.get().isEmpty() == false)) {
	    snippet.setName(snippet_name.get() + "");
	} else {
	    snippet.setName(
		    convertMapScope.getMap().getMapName()
		    + "@(" + snippet.getStartX() + "," + snippet.getStartY() + ")"
		    + "-"
		    + "(" + snippet.getEndX() + "," + snippet.getEndY() + ")");
	}

	if (convertMapScope.getRemap_out().get()) {
	    snippet.setConverter(convertMapScope);
	}

	convertMapScope.getSnippets().getSnippets().add(snippet);
	convertMapScope.publish(ConvertMapScope.SNIPPETS_UPDATED);
    }

    void saveSnippetList(String path) {
	SnippetIO.saveSnippetList(path, convertMapScope.getSnippets().getSnippets());
    }

    void loadSnippetList(String path) {
	ObservableList<SelectedTiles> list = SnippetIO.loadSnippetList(path);
	if (convertMapScope.getSnippets() == null) {
	    convertMapScope.setSnippets(new MapSnippetScope());
	    publish(SNIPPET_LIST_INIT);
	}

	if (convertMapScope.getRemap_out().get()) {
	    for (SelectedTiles snippet : list) {
		snippet.setConverter(convertMapScope);
	    }
	}

	convertMapScope.getSnippets().getSnippets().addAll(list);
	convertMapScope.publish(ConvertMapScope.SNIPPETS_UPDATED);
    }

    void deleteSnippet(SelectedTiles snippet) {
	convertMapScope.getSnippets().getSnippets().remove(snippet);
	convertMapScope.publish(ConvertMapScope.SNIPPETS_UPDATED);
    }

    // sel status
    public StringProperty getSelectionTextProperty() {
	return selectionTextProperty;
    }

    public StringProperty getTotalTextProperty() {
	return totalTextProperty;
    }

    public StringProperty getLandTextProperty() {
	return landTextProperty;
    }

    public StringProperty getObjectTextProperty() {
	return objectTextProperty;
    }

    public StringProperty getStructTextProperty() {
	return structTextProperty;
    }

    public StringProperty getShadowsTextProperty() {
	return shadowsTextProperty;
    }

    public StringProperty getRoofTextProperty() {
	return roofTextProperty;
    }

    public StringProperty getOnRoofTextProperty() {
	return onRoofTextProperty;
    }

    public BooleanProperty getSnippet_land() {
	return snippet_land;
    }

    public BooleanProperty getSnippet_objects() {
	return snippet_objects;
    }

    public BooleanProperty getSnippet_structures() {
	return snippet_structures;
    }

    public BooleanProperty getSnippet_shadows() {
	return snippet_shadows;
    }

    public BooleanProperty getSnippet_roofs() {
	return snippet_roofs;
    }

    public BooleanProperty getSnippet_onRoof() {
	return snippet_onRoof;
    }

    public StringProperty getSnippet_name() {
	return snippet_name;
    }

    public BooleanProperty getRemap_in() {
	return convertMapScope.getRemap_in();
    }

    public BooleanProperty getRemap_out() {
	return convertMapScope.getRemap_out();
    }

    public BooleanProperty getRemap_sel() {
	return convertMapScope.getRemap_sel();
    }

}
