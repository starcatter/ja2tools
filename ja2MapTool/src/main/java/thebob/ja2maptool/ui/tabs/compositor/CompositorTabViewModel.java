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
package thebob.ja2maptool.ui.tabs.compositor;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.controlsfx.control.IndexedCheckModel;
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
import thebob.ja2maptool.util.compositor.PlacementIO;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.compositor.SnippetPlacement;
import thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementLayer;
import thebob.ja2maptool.util.map.controller.editors.compositor.IMapCompositorController;

@ScopeProvider(scopes = {MapScope.class})   // we need to provide the scope for the map viewer to load, it will be replaced once setPreviewModel() is called
public class CompositorTabViewModel implements ViewModel {

    public static final String TREE_UPDATED = "TREE_UPDATED";

    @InjectScope
    protected VfsAssetScope vfsAssets;

    @InjectScope
    protected MainScope mainScreen;

    @InjectScope
    protected MapCompositorScope compositorScope;

    // observed converters
    protected Set<ConvertMapScope> subbedScopes = new HashSet<ConvertMapScope>();	// keep track of what we're subscribed to, it's easier than dealing with unsubscribing

    protected TreeItem<String> selectedItem = null;
    protected SelectedTiles selectedSnippet = null;

    // map window
    protected MapViewerTabViewModel mapViewer = null;
    // compositor backend
    protected IMapCompositorController compositor = null;

    // snippet list root
    protected TreeItem<String> root = new TreeItem<String>("Snippet sources");
    // snippet list map to snippets
    protected Map<TreeItem<String>, SelectedTiles> treeItemMap = new HashMap<TreeItem<String>, SelectedTiles>();

    // snippet checkboxes
    protected BooleanProperty snippet_land = new SimpleBooleanProperty(true);
    protected BooleanProperty snippet_objects = new SimpleBooleanProperty(true);
    protected BooleanProperty snippet_structures = new SimpleBooleanProperty(true);
    protected BooleanProperty snippet_shadows = new SimpleBooleanProperty(true);
    protected BooleanProperty snippet_roofs = new SimpleBooleanProperty(true);
    protected BooleanProperty snippet_onRoof = new SimpleBooleanProperty(true);
    protected BooleanProperty snippet_land_floors = new SimpleBooleanProperty(true);
    protected BooleanProperty snippet_structures_walls = new SimpleBooleanProperty(true);

    // snippet checkbox change listener
    protected ChangeListener<Boolean> snippetVisibilityListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            updateSnippetVisibility();
        }
    };

    // placement layers
    protected ObservableList<MapSnippetPlacementLayer> layersList = FXCollections.observableArrayList();

    // placement checkboxes
    protected BooleanProperty placement_land = new SimpleBooleanProperty(true);
    protected BooleanProperty placement_objects = new SimpleBooleanProperty(true);
    protected BooleanProperty placement_structures = new SimpleBooleanProperty(true);
    protected BooleanProperty placement_shadows = new SimpleBooleanProperty(true);
    protected BooleanProperty placement_roofs = new SimpleBooleanProperty(true);
    protected BooleanProperty placement_onRoof = new SimpleBooleanProperty(true);
    protected BooleanProperty placement_land_floors = new SimpleBooleanProperty(true);
    protected BooleanProperty placement_structures_walls = new SimpleBooleanProperty(true);

    // snippet checkbox change listener
    protected ChangeListener<Boolean> placementVisibilityListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            updatePlacementVisibility();
        }
    };

    // placement texts
    protected StringProperty placement_name = new SimpleStringProperty("not selected");
    protected StringProperty placement_location = new SimpleStringProperty("not selected");
    protected StringProperty placement_size = new SimpleStringProperty("not selected");

    public void initialize() {
        // hook up all of the current converters
        updateConverterSubscriptions();

        // and subscribe to hook up all the future ones
        mainScreen.getActiveMapConversions().addListener((ListChangeListener.Change<? extends ConvertMapScope> c) -> {
            updateConverterSubscriptions();
        });

        // hook up the map scope (map is not loaded at this time but the scope should be ready for setup)
        compositorScope.setViewModel(this);
        compositorScope.getMap().subscribe(MapScope.MAP_UPDATED, (key, values) -> {
            updateRenderer(true);
            mainScreen.publish(UPDATE_SCOPES);
        });

        addPropertyListeners();
    }

    private void addPropertyListeners() {
        // snippet checkboxes
        snippet_land.addListener(snippetVisibilityListener);
        snippet_objects.addListener(snippetVisibilityListener);
        snippet_structures.addListener(snippetVisibilityListener);
        snippet_shadows.addListener(snippetVisibilityListener);
        snippet_roofs.addListener(snippetVisibilityListener);
        snippet_onRoof.addListener(snippetVisibilityListener);
        snippet_land_floors.addListener(snippetVisibilityListener);
        snippet_structures_walls.addListener(snippetVisibilityListener);

        // placement checkboxes
        placement_land.addListener(placementVisibilityListener);
        placement_objects.addListener(placementVisibilityListener);
        placement_structures.addListener(placementVisibilityListener);
        placement_shadows.addListener(placementVisibilityListener);
        placement_roofs.addListener(placementVisibilityListener);
        placement_onRoof.addListener(placementVisibilityListener);
        placement_land_floors.addListener(placementVisibilityListener);
        placement_structures_walls.addListener(placementVisibilityListener);
    }

    /**
     * Subscribes to every ConvertMapScope's snippet list Also keeps track of
     * who we've already subscribed to, otherwise we'd get their events twice!
     */
    void updateConverterSubscriptions() {
        for (ConvertMapScope converter : mainScreen.getActiveMapConversions()) {
            if (!subbedScopes.contains(converter)) {
                subbedScopes.add(converter);

                converter.subscribe(ConvertMapScope.SNIPPETS_UPDATED, (key, value) -> {
                    updateTree();
                    updateSnippetSelection();
                });
            }
        }
        updateTree();
    }

    /**
     * Connects this viewModel to the viewmodel of the map viewer we're using.
     * Also injects our mapScope and registers the compositor component with the
     * DisplayManager
     *
     * @param viewModel
     */
    void setPreviewModel(MapViewerTabViewModel viewModel) {
        mapViewer = viewModel;
        mapViewer.setMapScope(compositorScope.getMap());
        mapViewer.setViewerMode(MapViewerTabViewModel.MapViewerMode.Editor);
        mapViewer.initialize();

        // registers the map compositor interface with the renderer
        compositor = mapViewer.getRenderer().connectCompositor(compositorScope);
        updateSnippetVisibility();
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

    MapCompositorScope getMapCompositorScope() {
        return compositorScope;
    }

    // -------------------------
    // save map
    // -------------------------
    void saveMap(String path) {
        compositorScope.getMap().getMapData().saveMap(path);
    }

    // -------------------------------------------------------------------------
    // -- Snippets tab
    // -------------------------------------------------------------------------
    protected void updateSnippetVisibility() {
        compositor.setPlacementVisibility(getSnippetPlacementOptions());
    }

    public SelectionPlacementOptions getSnippetPlacementOptions() {
        return new SelectionPlacementOptions(
                snippet_land.get(),
                snippet_objects.get(),
                snippet_structures.get(),
                snippet_shadows.get(),
                snippet_roofs.get(),
                snippet_onRoof.get(),
                snippet_land_floors.get(),
                snippet_structures_walls.get()
        );
    }

    // -------------------------
    // snippet list
    // -------------------------
    TreeItem<String> getListRoot() {
        return root;
    }

    public void updateTree() {
        root.getChildren().clear();
        root.setExpanded(true);
        treeItemMap.clear();

        // stuff obtained via clipboard
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

        // stuff loaded with snippet placement lists/groups
        if (compositorScope.getLoadedSnippetLibs() != null && compositorScope.getLoadedSnippetLibs().size() > 0) {
            TreeItem<String> copyNode = new TreeItem<String>("Loaded libraries");
            copyNode.setExpanded(true);

            compositorScope.getLoadedSnippetLibs().asMap().forEach((source, snippets) -> {
                TreeItem<String> copyNode2 = new TreeItem<String>(source);
                for (SelectedTiles snippet : snippets) {
                    TreeItem<String> snippetNode = new TreeItem<String>(snippet.getName() + " (" + snippet.getWidth() + "x" + snippet.getHeight() + ")");
                    treeItemMap.put(snippetNode, snippet);
                    copyNode2.getChildren().add(snippetNode);
                }
                copyNode.getChildren().add(copyNode2);
            });

            root.getChildren().add(copyNode);
        }

        // stuff from converters
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
                    String snippetName = (snippet.getName() == null ? snippet.toString() : snippet.getName());
                    TreeItem<String> snippetNode = new TreeItem<String>("Snippet: " + snippetName);
                    treeItemMap.put(snippetNode, snippet);
                    convertNode.getChildren().add(snippetNode);

                    System.out.println("thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.updateTree():\t\tsnippet:" + snippetName + ")");
                }
            }
            root.getChildren().add(convertNode);
        }
    }

    void updateSnippetSelection() {
        selectedItem = null;
        selectedSnippet = null;

        System.out.println("thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.updateSnippetSelection(): deselected");

        compositor.setPlacementPreview(null);

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

            compositor.setPlacementPreview(placedSelection);
        } else {
            selectedItem = null;
            selectedSnippet = null;

            System.out.println("thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.updateSnippetSelection(): deselected");

            compositor.setPlacementPreview(null);
        }
    }

    void placeSnippet() {
        if (selectedSnippet != null) {
            SelectionPlacementOptions options = getSnippetPlacementOptions();

            // we might have to remap this snippet
            SelectedTiles placedSelection = null;
            if (selectedSnippet.getConverter() != null) {
                placedSelection = new SelectedTiles(selectedSnippet);
                MapTransformer transformer = new MapTransformer(selectedSnippet.getConverter());
                transformer.remapSnippet(placedSelection);
            } else {
                placedSelection = selectedSnippet;
            }

            compositor.placeSelection(placedSelection, options);
        }
    }

    void copySnippet() {
        SelectedTiles selection = compositor.getSelection();
        if (selection != null) {
            if (compositorScope.getLoadedSnippets() == null) {
                compositorScope.setLoadedSnippets(new MapSnippetScope());
            }

            compositorScope.getLoadedSnippets().getSnippets().add(selection);
            updateTree();
        }
    }

    // -------------------------------------------------------------------------
    // -- Placement tab
    // -------------------------------------------------------------------------
    public SelectionPlacementOptions getPlacementLayerOptions() {
        return new SelectionPlacementOptions(
                placement_land.get(),
                placement_objects.get(),
                placement_structures.get(),
                placement_shadows.get(),
                placement_roofs.get(),
                placement_onRoof.get(),
                placement_land_floors.get(),
                placement_structures_walls.get()
        );
    }

    protected void updatePlacementVisibility() {
        if (selectedPlacement != null) {
            selectedPlacement.setEnabledLayers(getPlacementLayerOptions());
        }
        compositor.updateVisibleLayers();
    }

    // --------------------------------------------------
    // Placement layers
    // --------------------------------------------------
    IndexedCheckModel<MapSnippetPlacementLayer> checkModel = null;

    void registerLayerCheckboxes(IndexedCheckModel<MapSnippetPlacementLayer> checkModel) {
        this.checkModel = checkModel;
    }

    ObservableList<MapSnippetPlacementLayer> getPlacementLayerListContents() {
        return layersList;
    }

    void updateLayersList() {
        List<MapSnippetPlacementLayer> layers = compositor.getLayers();
        List<Integer> visible = new ArrayList<>();        
                
        layersList.clear();
        for (int i = 0; i < layers.size(); i++) {
            MapSnippetPlacementLayer layer = layers.get(i);
            layersList.add(layer);
            if (layer.isVisible()) {
                visible.add(i);
            }
        }

        checkModel.checkIndices(visible.stream().mapToInt(i->i).toArray());               
        updatePlacementList();
    }

    void updateVisibleLayers(ObservableList<MapSnippetPlacementLayer> checkedItems) {
        for (MapSnippetPlacementLayer layer : compositor.getLayers()) {
            layer.setVisible( checkModel.isChecked(layer) );
        }
        compositor.updateVisibleLayers();
    }

    void selectPlacementLayer(MapSnippetPlacementLayer selectedItem) {
        compositor.setActiveLayer(selectedItem);
    }

    // -------------------------
    // load/save layers
    // -------------------------
    void loadPlacementLayers(String path) {
        List<MapSnippetPlacementLayer> layers = PlacementIO.loadPlacementListGroup(path);

        // add the snippets from this filr to the snippet tree
        Set<SelectedTiles> snippetLib = new HashSet<>();
        for (MapSnippetPlacementLayer layer : layers) {
            for (SnippetPlacement placement : layer.getPlacements().values()) {
                snippetLib.add(placement.getSnippet());
            }
        }
        compositorScope.getLoadedSnippetLibs().putAll(Paths.get(path).getFileName().toString(), snippetLib);
        updateTree();

        compositor.setLayers(layers);
        updateLayersList();
    }

    void savePlacementLayers(String path) {
        PlacementIO.savePlacementListGroup(path, compositor.getLayers());
    }

    // -------------------------
    // add/del/copy/move layers
    // -------------------------
    void deletePlacementLayer(MapSnippetPlacementLayer selectedItem) {
        compositor.deletePlacementLayer(selectedItem);
    }

    void addPlacementLayer() {
        compositor.addPlacementLayer("Layer");
    }

    void movePlacementLayer(MapSnippetPlacementLayer selectedItem, int i) {
        compositor.movePlacementLayer(selectedItem, i);
    }

    void copyPlacementLayer(MapSnippetPlacementLayer selectedItem) {
        compositor.copyPlacementLayer(selectedItem);
    }

    // --------------------------------------------------
    // Placements
    // --------------------------------------------------
    ObservableList<SnippetPlacement> getPlacementListContents() {
        return compositorScope.getPlacedSnippets();
    }

    SnippetPlacement selectedPlacement = null;

    void updatePlacementList() {
        MapSnippetPlacementLayer layer = compositor.getActiveLayer();
        ObservableList<SnippetPlacement> list = compositorScope.getPlacedSnippets();
        list.clear();
        layer.getPlacements().forEach((cell, placement) -> {
            list.add(placement);
        });
    }

    void selectPlacement(SnippetPlacement selectedItem) {
        if (selectedItem == null) {
            selectedPlacement = null;
            return;
        }

        selectedPlacement = selectedItem;

        SelectedTiles snip = selectedItem.getSnippet();
        placement_name.set(snip.getName());
        placement_location.set("(" + selectedItem.getCellX() + "," + selectedItem.getCellY() + "), cell " + selectedItem.getCell());
        placement_size.set(snip.getWidth() + "x" + snip.getHeight() + " (" + (snip.getWidth() * snip.getHeight()) + " tiles)");
        if (selectedItem.getEnabledLayers() != null) {
            setPlacementProperties(selectedItem.getEnabledLayers());
        }

        compositor.selectPlacement(selectedItem);
    }

    void setPlacementProperties(SelectionPlacementOptions options) {
        boolean[] layerOpts = options.getAsArray();
        placement_land.set(layerOpts[0]);
        placement_objects.set(layerOpts[1]);
        placement_structures.set(layerOpts[2]);
        placement_shadows.set(layerOpts[3]);
        placement_roofs.set(layerOpts[4]);
        placement_onRoof.set(layerOpts[5]);
        placement_land_floors.set(options.isPlace_land_floors());
        placement_structures_walls.set(options.isPlace_structures_walls());
    }

    // -------------------------
    // load/save placements
    // -------------------------
    void loadPlacements(String path) {
        Map<Integer, SnippetPlacement> placements = PlacementIO.loadPlacementList(path);

        // add the snippets from this filr to the snippet tree
        Set<SelectedTiles> snippetLib = new HashSet<>();
        for (SnippetPlacement placement : placements.values()) {
            snippetLib.add(placement.getSnippet());
        }
        compositorScope.getLoadedSnippetLibs().putAll(Paths.get(path).getFileName().toString(), snippetLib);
        updateTree();

        compositor.appendPlacementsToCurrentLayer(placements);
        updatePlacementList();
    }

    void savePlacements(String path) {
        MapSnippetPlacementLayer layer = compositor.getActiveLayer();
        PlacementIO.savePlacementList(path, layer.getPlacements());
    }

    // -------------------------
    // commit changes to map
    // -------------------------
    void pastePlacements() {
        compositor.commitChangesToMap();
    }

    // -------------------------
    // snippet placement props
    // -------------------------
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

    // -------------------------
    // placement props
    // -------------------------
    public BooleanProperty getPlacement_land() {
        return placement_land;
    }

    public BooleanProperty getPlacement_objects() {
        return placement_objects;
    }

    public BooleanProperty getPlacement_structures() {
        return placement_structures;
    }

    public BooleanProperty getPlacement_shadows() {
        return placement_shadows;
    }

    public BooleanProperty getPlacement_roofs() {
        return placement_roofs;
    }

    public BooleanProperty getPlacement_onRoof() {
        return placement_onRoof;
    }

    public BooleanProperty getPlacement_land_floors() {
        return placement_land_floors;
    }

    public BooleanProperty getPlacement_structures_walls() {
        return placement_structures_walls;
    }

    // -------------------------
    // placement text props
    // -------------------------
    public StringProperty getPlacement_name() {
        return placement_name;
    }

    public StringProperty getPlacement_location() {
        return placement_location;
    }

    public StringProperty getPlacement_size() {
        return placement_size;
    }

}
