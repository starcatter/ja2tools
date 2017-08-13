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
package thebob.ja2maptool.ui.main;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.Scope;
import de.saxsys.mvvmfx.ViewTuple;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.inject.Inject;
import thebob.assetloader.vfs.VFSConfig;
import thebob.ja2maptool.components.ScopeActionMenu;
import thebob.ja2maptool.components.VFSMenu;
import thebob.ja2maptool.scopes.map.ConvertMapScope;
import thebob.ja2maptool.scopes.mapping.ItemMappingScope;
import thebob.ja2maptool.scopes.MainScope;
import thebob.ja2maptool.scopes.map.MapScope;
import thebob.ja2maptool.scopes.view.StiViewerScope;
import thebob.ja2maptool.scopes.mapping.TilesetMappingScope;
import static thebob.ja2maptool.scopes.VfsAssetScope.BROWSE_CONFIG;
import static thebob.ja2maptool.scopes.VfsAssetScope.REFRESH_CONFIGS;
import thebob.ja2maptool.scopes.map.MapCompositorScope;
import thebob.ja2maptool.scopes.view.VfsBrowserScope;
import thebob.ja2maptool.ui.dialogs.mapselect.MapSelectionDialogView;
import thebob.ja2maptool.ui.dialogs.mapselect.MapSelectionDialogViewModel;
import thebob.ja2maptool.ui.tabs.compositor.CompositorTabView;
import thebob.ja2maptool.ui.tabs.convert.ConvertMapTabView;
import thebob.ja2maptool.ui.tabs.mapping.items.ItemMappingTabView;
import thebob.ja2maptool.ui.tabs.mapping.setup.MappingSetupTabView;
import thebob.ja2maptool.ui.tabs.mapping.tileset.TilesetMappingTabView;
import thebob.ja2maptool.ui.tabs.vfs.VfsSetupTabView;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabView;
import thebob.ja2maptool.ui.tabs.viewers.sti.StiViewerTabView;
import thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabView;
import thebob.ja2maptool.util.DialogHelper;

/**
 *
 * @author the_bob
 */
public class MainScreenView implements FxmlView<MainScreenViewModel>, Initializable {

    // ------------------ BEGIN FXML AUTOGEN
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TabPane main_tabs;

    @FXML
    private Tab tab_setup;

    @FXML
    private Tab tab_mapping;

    @FXML
    private Menu menu_vfs_configs;

    @FXML
    private Menu menu_mappings;

    @FXML
    private Menu menu_maps;

    @FXML
    private Menu menu_compositors;

    @FXML
    void menu_close(ActionEvent event) {
	Platform.exit();
    }

    // load mappings (open tabs)
    @FXML
    void menu_load_item_map(ActionEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Load item mapping");
	chooser.setInitialDirectory(new File("."));
	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("item mapping files", "*.itemmap"));
	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("all files", "*.*"));

	File selectedDirectory = chooser.showOpenDialog(main_tabs.getScene().getWindow());
	if (selectedDirectory != null) {
	    viewModel.loadItemMapping(selectedDirectory.getPath());
	}
    }

    @FXML
    void menu_load_tileset_map(ActionEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Load tileset mapping");
	chooser.setInitialDirectory(new File("."));
	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("tile mapping files", "*.tilemap"));
	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("all files", "*.*"));

	File selectedDirectory = chooser.showOpenDialog(main_tabs.getScene().getWindow());
	if (selectedDirectory != null) {
	    viewModel.loadTilesetMapping(selectedDirectory.getPath());
	}
    }

    // open file viewers
    @FXML
    void menu_open_map(ActionEvent event) {
	MapScope mapScope = new MapScope();
	ViewTuple<MapSelectionDialogView, MapSelectionDialogViewModel> selectorTouple = FluentViewLoader.fxmlView(MapSelectionDialogView.class)
		.context(context)
		.providedScopes(mapScope)
		.load();

	Parent content = selectorTouple.getView();
	Stage showDialog = DialogHelper.showDialog(content, primaryStage);

	MapSelectionDialogView selectorView = selectorTouple.getCodeBehind();
	selectorView.setDisplayingStage(showDialog);

	MapSelectionDialogViewModel selectorViewModel = selectorTouple.getViewModel();
	showDialog.setOnHidden(closeEvent -> {
	    if (mapScope.getMapData() != null) {
		addTab(MapViewerTabView.class, "Map", mapScope);
	    } else {
		System.out.println("thebob.ja2maptool.ui.main.MainScreenView.menu_open_map() no map?");
	    }
	});
    }

    @FXML
    void menu_open_slf(ActionEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Load SLF file");
	chooser.setInitialDirectory(new File("."));
	File selectedDirectory = chooser.showOpenDialog(main_tabs.getScene().getWindow());
	if (selectedDirectory != null) {
	    viewModel.loadSLF(selectedDirectory.getPath());
	}
    }

    @FXML
    void menu_open_sti(ActionEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Load SLF file");
	chooser.setInitialDirectory(new File("."));
	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("sti files", "*.sti"));
	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("all files", "*.*"));

	File selectedDirectory = chooser.showOpenDialog(main_tabs.getScene().getWindow());

	if (selectedDirectory != null) {
	    StiViewerScope scope = new StiViewerScope();
	    scope.setFilePath(selectedDirectory.getPath());
	    addTab(StiViewerTabView.class, "View STI", scope);
	}
    }

    // actions that open tabs
    @FXML
    void menu_vfs_setup(ActionEvent event) {
	gotoBasicTab(MainScope.TabTypes.TAB_VFS_SETUP);
    }

    @FXML
    void menu_create_mapping(ActionEvent event) {
	gotoBasicTab(MainScope.TabTypes.TAB_MAPPING_SETUP);
    }

    @FXML
    void menu_convert(ActionEvent event) {
	gotoBasicTab(MainScope.TabTypes.TAB_CONVERT);
    }

    @FXML
    void menu_new_compositor(ActionEvent event) {
	gotoBasicTab(MainScope.TabTypes.TAB_COMPOSITOR);
    }

    // ------------------ END FXML AUTOGEN    
    // MVVMFX inject
    @InjectViewModel
    private MainScreenViewModel viewModel;

    @Inject
    private Stage primaryStage;

    @InjectContext
    private Context context;

    public TabPane getTabPane() {
	return main_tabs;
    }

    private void gotoBasicTab(MainScope.TabTypes gotoTab) {
	Tab tab = null;

	switch (gotoTab) {
	    case TAB_VFS_SETUP:
		if (tab_setup == null) {
		    tab_setup = addTab(VfsSetupTabView.class, "VFS setup", null);
		}
		tab = tab_setup;
		break;
	    case TAB_MAPPING_SETUP:
		if (tab_mapping == null) {
		    tab_setup = addTab(MappingSetupTabView.class, "Create mapping", null);
		}
		tab = tab_mapping;
		break;
	    case TAB_CONVERT: {
		ConvertMapScope scope = new ConvertMapScope();
		tab = addTab(ConvertMapTabView.class, "Convert map", scope);
		viewModel.getMainScope().registerMapConversionScope(scope);
	    }
	    break;
	    case TAB_COMPOSITOR: {
		MapCompositorScope scope = new MapCompositorScope();
		tab = addTab(CompositorTabView.class, "Map compositor", scope);
		viewModel.getMainScope().registerMapCompositorScope(scope);
	    }
	    break;
	    default:
		throw new AssertionError(gotoTab.name());

	}

	if (tab != null) {
	    if (main_tabs.getTabs().indexOf(tab) == -1) {
		main_tabs.getTabs().add(tab);
	    }

	    main_tabs.getSelectionModel().select(tab);
	}
    }

    private void updateVfsMenu() {
	menu_vfs_configs.setDisable(false);
	menu_vfs_configs.getItems().clear();
	VFSMenu vfsMenu = new VFSMenu(viewModel.getVfsScope());
	menu_vfs_configs.getItems().addAll(vfsMenu.getItems());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	updateVfsMenu();

	viewModel.subscribe(viewModel.SHOW_TAB, (key, payload) -> {
	    MainScope.TabTypes gotoTab = (MainScope.TabTypes) payload[0];
	    gotoBasicTab(gotoTab);
	});

	viewModel.getVfsScope().subscribe(BROWSE_CONFIG, (key, payload) -> {
	    VFSConfig config = (VFSConfig) payload[0];
	    VfsBrowserScope scope = new VfsBrowserScope();
	    scope.setConfig(config);

	    addTab(VfsViewerTabView.class, "VFS browser", scope);
	});

	viewModel.getVfsScope().subscribe(REFRESH_CONFIGS, (key, payload) -> {
	    updateVfsMenu();
	});

	viewModel.getMainScope().subscribe(MainScope.UPDATE_SCOPES, (key, payload) -> {
	    MainScope mainScope = viewModel.getMainScope();

	    // mapping menu
	    menu_mappings.setDisable(mainScope.getActiveItemMappings().isEmpty() && mainScope.getActiveTilesetMappings().isEmpty());
	    menu_mappings.getItems().clear();

	    for (ItemMappingScope mapping : mainScope.getActiveItemMappings()) {
		Menu itemMapMenu = new ScopeActionMenu<ItemMappingScope>("Map items", mapping.getSourceAssets().getVfsConfigName() + " -> " + mapping.getTargetAssets().getVfsConfigName(), mapping, mainScope.getActiveItemMappings(), mainScope.getTabForScope(mapping), this, ItemMappingTabView.class);
		menu_mappings.getItems().add(itemMapMenu);
	    }

	    if (mainScope.getActiveItemMappings().isEmpty() == false && mainScope.getActiveTilesetMappings().isEmpty() == false) {
		menu_mappings.getItems().add(new SeparatorMenuItem());
	    }

	    for (TilesetMappingScope mapping : mainScope.getActiveTilesetMappings()) {
		Menu tilesetMapMenu = new ScopeActionMenu<TilesetMappingScope>("Map tileset", mapping.getSourceTilesetId() + " (" + mapping.getSourceAssets().getVfsConfigName() + ") -> " + mapping.getTargetTilesetId() + " (" + mapping.getTargetAssets().getVfsConfigName() + ")", mapping, mainScope.getActiveTilesetMappings(), mainScope.getTabForScope(mapping), this, TilesetMappingTabView.class);
		menu_mappings.getItems().add(tilesetMapMenu);
	    }

	    // convert map menu
	    menu_maps.getItems().clear();
	    menu_maps.setDisable(mainScope.getActiveMapConversions().isEmpty());

	    for (ConvertMapScope mapping : mainScope.getActiveMapConversions()) {
		String descStr = mapping.getMap().getMapData() != null ? mapping.getMap().getMapAssets().getVfsConfigName() + "::" + mapping.getMap().getMapName() : "(not set)";
		Menu convertMapMenu = new ScopeActionMenu<ConvertMapScope>("Convert map", descStr, mapping, mainScope.getActiveMapConversions(), mainScope.getTabForScope(mapping), this, ConvertMapTabView.class);
		menu_maps.getItems().add(convertMapMenu);
	    }

	    // compositors menu
	    menu_compositors.getItems().clear();
	    menu_compositors.setDisable(mainScope.getActiveMapCompositors().isEmpty());

	    for (MapCompositorScope mapping : mainScope.getActiveMapCompositors()) {
		String descStr = "";
		Menu compositorMenu = new ScopeActionMenu<MapCompositorScope>("Compositor", descStr, mapping, mainScope.getActiveMapCompositors(), mainScope.getTabForScope(mapping), this, CompositorTabView.class);
		menu_compositors.getItems().add(compositorMenu);
	    }

	});

	viewModel.subscribe(viewModel.ADD_TAB, (key, payload) -> {
	    Class<FxmlView> tabClass = (Class) payload[0];
	    String name = (String) payload[1];
	    Scope scope = (Scope) payload[2];

	    addTab(tabClass, name, scope);
	});

    }

    public Tab addTab(Class tabClass, String tabName, Scope tabScope) {
	ViewTuple tabTouple = FluentViewLoader.fxmlView(tabClass)
		.context(context) // VfsAssetScope, MainScope
		.providedScopes(tabScope)
		.load();

	Parent tabContent = tabTouple.getView();

	Tab tab = new Tab(tabName);
	tab.setContent(tabContent);

	main_tabs.getTabs().add(tab);
	main_tabs.getSelectionModel().select(tab);
	viewModel.registerScopeTab(tab, tabScope);
	return tab;
    }
}
