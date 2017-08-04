/*
 * Copyright (C) 2017 the_bob.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package thebob.ja2maptool.ui.tabs.compositor;

import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;
import thebob.ja2maptool.scopes.MainScope;
import static thebob.ja2maptool.scopes.MainScope.UPDATE_SCOPES;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.scopes.map.ConvertMapScope;
import thebob.ja2maptool.scopes.map.MapCompositorScope;
import thebob.ja2maptool.scopes.map.MapScope;
import thebob.ja2maptool.scopes.map.MapSnippetScope;
import static thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.MAP_LOADED;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabViewModel;
import thebob.ja2maptool.util.MapTransformer;
import thebob.ja2maptool.util.compositor.SelectedTiles;

@ScopeProvider(scopes = {MapScope.class})   // we need to provide the scope for the map viewer to load, it will be replaced once setPreviewModel() is called
public class CompositorTabViewModel implements ViewModel {

    public static final String TREE_UPDATED = "TREE_UPDATED";

    @InjectScope
    VfsAssetScope vfsAssets;

    @InjectScope
    MainScope mainScreen;

    @InjectScope
    MapCompositorScope compositorScope;

    TreeItem<String> root = new TreeItem<String>("Snippet sources");
    Map<TreeItem<String>, SelectedTiles> treeItemMap = new HashMap<TreeItem<String>, SelectedTiles>();
    Set<ConvertMapScope> subbedScopes = new HashSet<ConvertMapScope>();	// keep track of what we're subscribed to, it's easier than dealing with unsubscribing

    MapViewerTabViewModel mapViewer;

    // checkboxes
    BooleanProperty snippet_land = new SimpleBooleanProperty(true);
    BooleanProperty snippet_objects = new SimpleBooleanProperty(true);
    BooleanProperty snippet_structures = new SimpleBooleanProperty(true);
    BooleanProperty snippet_shadows = new SimpleBooleanProperty(true);
    BooleanProperty snippet_roofs = new SimpleBooleanProperty(true);
    BooleanProperty snippet_onRoof = new SimpleBooleanProperty(true);

    BooleanProperty snippet_land_floors = new SimpleBooleanProperty(true);
    BooleanProperty snippet_structures_walls = new SimpleBooleanProperty(true);

    public void initialize() {
	updateConverterSubscriptions();

	mainScreen.getActiveMapConversions().addListener((ListChangeListener.Change<? extends ConvertMapScope> c) -> {
	    updateConverterSubscriptions();
	});

	compositorScope.getMap().subscribe(MapScope.MAP_UPDATED, (key, values) -> {
	    updateRenderer(true);
	    mainScreen.publish(UPDATE_SCOPES);
	});
    }

    void updateConverterSubscriptions() {
	for (ConvertMapScope converter : mainScreen.getActiveMapConversions()) {
	    if (!subbedScopes.contains(converter)) {
		subbedScopes.add(converter);

		converter.subscribe(ConvertMapScope.SNIPPETS_UPDATED, (key, value) -> {
		    System.out.println("thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.updateConverterSubscriptions() updating tree...");
		    updateTree();
		    System.out.println("thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.updateConverterSubscriptions() updating selection...");
		    updateSnippetSelection();
		});
	    }
	}
	updateTree();
    }

    void setPreviewModel(MapViewerTabViewModel viewModel) {
	mapViewer = viewModel;
	mapViewer.setMapScope(compositorScope.getMap());
	mapViewer.initialize();
    }

    // preview window renderer handlers
    public void updateRenderer(boolean centerMap) {
	if (compositorScope.getMap() == null || compositorScope.getMap().getMapData() == null) {
	    return;
	}

	mapViewer.updateRenderer(centerMap);
	publish(MAP_LOADED);
    }

    MapScope getMapScope() {
	return compositorScope.getMap();
    }

    TreeItem<String> getListRoot() {
	return root;
    }

    public void updateTree() {
	root.getChildren().clear();
	root.setExpanded(true);
	treeItemMap.clear();

	if (compositorScope.getLoadedSnippets() != null) {
	    TreeItem<String> copyNode = new TreeItem<String>("Clipboard");
	    copyNode.setExpanded(true);

	    for (SelectedTiles snippet : compositorScope.getLoadedSnippets().getSnippets()) {
		TreeItem<String> snippetNode = new TreeItem<String>("Snippet: " + snippet);
		treeItemMap.put(snippetNode, snippet);
		copyNode.getChildren().add(snippetNode);
	    }
	    root.getChildren().add(copyNode);
	}

	for (ConvertMapScope converter : mainScreen.getActiveMapConversions()) {
	    String converterName = "Converter ( " + (converter.getMap().getMapName() == null ? "no map loaded" : converter.getMap().getMapName()) + " )";
	    TreeItem<String> convertNode = new TreeItem<String>(converterName);
	    convertNode.setExpanded(true);

	    System.out.println("thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.updateTree():\t" + converterName);

	    if (converter.getMap() != null && converter.hasSelection()) {
		SelectedTiles selection = converter.getSelection();
		TreeItem<String> selectionNode = new TreeItem<String>("Selection: " + selection);
		treeItemMap.put(selectionNode, selection);
		convertNode.getChildren().add(selectionNode);

		System.out.println("thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.updateTree():\t\tselection:" + selection + ")");
	    }
	    if (converter.getSnippets() != null) {
		for (SelectedTiles snippet : converter.getSnippets().getSnippets()) {
		    String snippetName = ( snippet.getName() == null ? snippet.toString() : snippet.getName() );
		    TreeItem<String> snippetNode = new TreeItem<String>("Snippet: " + snippetName);
		    treeItemMap.put(snippetNode, snippet);
		    convertNode.getChildren().add(snippetNode);

		    System.out.println("thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.updateTree():\t\tsnippet:" + snippetName + ")");
		}
	    }
	    root.getChildren().add(convertNode);
	}
    }

    TreeItem<String> selectedItem = null;
    SelectedTiles selectedSnippet = null;

    void updateSnippetSelection() {
	selectedItem = null;
	selectedSnippet = null;

	System.out.println("thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.updateSnippetSelection(): deselected");

	mapViewer.getRenderer().resetCursorSize();
	mapViewer.getRenderer().setPlacementPreview(null);
	mapViewer.getRenderer().updateAuxCursorDisplay();
	mapViewer.getRenderer().moveWindow(0, 0);

	publish(TREE_UPDATED);
    }

    void updateSnippetSelection(TreeItem<String> item) {
	if (treeItemMap.containsKey(item)) {
	    selectedItem = item;
	    selectedSnippet = treeItemMap.get(item);

	    System.out.println("thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.updateSnippetSelection(): " + selectedSnippet);

	    // we might have to remap this snippet
	    SelectedTiles placedSelection = null;
	    if (selectedSnippet.getConverter() != null) {
		placedSelection = new SelectedTiles(selectedSnippet);
		MapTransformer transformer = new MapTransformer(selectedSnippet.getConverter());
		transformer.remapSnippet(placedSelection);
	    } else {
		placedSelection = selectedSnippet;
	    }

	    int width = placedSelection.getWidth();
	    int height = placedSelection.getHeight();

	    mapViewer.getRenderer().setCursorSize(width, height);
	    mapViewer.getRenderer().setPlacementPreview(placedSelection);
	    mapViewer.getRenderer().updateAuxCursorDisplay();
	    mapViewer.getRenderer().moveWindow(0, 0);
	} else {
	    selectedItem = null;
	    selectedSnippet = null;

	    System.out.println("thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.updateSnippetSelection(): deselected");

	    mapViewer.getRenderer().resetCursorSize();
	    mapViewer.getRenderer().setPlacementPreview(null);
	    mapViewer.getRenderer().updateAuxCursorDisplay();
	    mapViewer.getRenderer().moveWindow(0, 0);
	}
    }

    void placeSnippet() {
	if (selectedSnippet != null) {
	    SelectionPlacementOptions options = new SelectionPlacementOptions(
		    snippet_land.get(),
		    snippet_objects.get(),
		    snippet_structures.get(),
		    snippet_shadows.get(),
		    snippet_roofs.get(),
		    snippet_onRoof.get(),
		    snippet_land_floors.get(),
		    snippet_structures_walls.get()
	    );

	    // we might have to remap this snippet
	    SelectedTiles placedSelection = null;
	    if (selectedSnippet.getConverter() != null) {
		placedSelection = new SelectedTiles(selectedSnippet);
		MapTransformer transformer = new MapTransformer(selectedSnippet.getConverter());
		transformer.remapSnippet(placedSelection);
	    } else {
		placedSelection = selectedSnippet;
	    }

	    mapViewer.getRenderer().placeSelection(placedSelection, options);
	}
    }

    void copySnippet() {
	SelectedTiles selection = mapViewer.getRenderer().getSelection();
	if (selection != null) {
	    if (compositorScope.getLoadedSnippets() == null) {
		compositorScope.setLoadedSnippets(new MapSnippetScope());
	    }

	    compositorScope.getLoadedSnippets().getSnippets().add(selection);
	    updateTree();
	}
    }

    void saveMap(String path) {
	compositorScope.getMap().getMapData().saveMap(path);
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

    public BooleanProperty getSnippet_land_floors() {
	return snippet_land_floors;
    }

    public BooleanProperty getSnippet_structures_walls() {
	return snippet_structures_walls;
    }

}
