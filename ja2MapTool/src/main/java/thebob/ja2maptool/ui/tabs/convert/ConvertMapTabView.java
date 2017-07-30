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

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.inject.Inject;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;
import thebob.ja2maptool.components.SimplePropertyItem;
import thebob.ja2maptool.ui.dialogs.mapselect.MapSelectionDialogView;
import thebob.ja2maptool.ui.dialogs.mapselect.MapSelectionDialogViewModel;
import thebob.ja2maptool.ui.dialogs.scopeselect.ScopeSelectionDialogView;
import thebob.ja2maptool.ui.dialogs.scopeselect.ScopeSelectionDialogViewModel;
import thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.PreviewMode;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabView;
import thebob.ja2maptool.util.DialogHelper;

public class ConvertMapTabView implements FxmlView<ConvertMapTabViewModel>, Initializable {

    @FXML
    private PropertySheet tileset_status;

    @FXML
    private PropertySheet item_status;

    @FXML
    private PropertySheet map_status;

    @FXML
    private ToggleGroup previewMode;

    @FXML
    private RadioButton prev_original;

    @FXML
    private RadioButton prev_remap;

    @FXML
    private RadioButton prev_direct;

    @FXML
    // Inject the Code behind instance of the MapViewerTabView by using the name convention ...Controller
    private MapViewerTabView mapViewController;

    @FXML
    void export_map(MouseEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Save converted map as...");
	chooser.setInitialDirectory(new File("."));
	File selectedDirectory = chooser.showSaveDialog(prev_remap.getScene().getWindow());
	if (selectedDirectory != null) {
	    viewModel.saveMap(selectedDirectory.getPath());
	}
    }

    @FXML
    void load_tiles(MouseEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Load tileset mapping");
	chooser.setInitialDirectory(new File("."));
	File selectedDirectory = chooser.showOpenDialog(prev_remap.getScene().getWindow());
	if (selectedDirectory != null) {
	    viewModel.loadTileMapping(selectedDirectory.getPath());
	}
    }

    @FXML
    void select_tiles(MouseEvent event) {
	ViewTuple<ScopeSelectionDialogView, ScopeSelectionDialogViewModel> selectorTouple = FluentViewLoader.fxmlView(ScopeSelectionDialogView.class)
		.context(context)
		.providedScopes(viewModel.getConvertMapScope())
		.load();

	ScopeSelectionDialogViewModel selectorViewModel = selectorTouple.getViewModel();
	selectorViewModel.setSelectorType(ScopeSelectionDialogViewModel.ScopeSelectorType.SCOPE_MAP_TILES);

	Parent content = selectorTouple.getView();
	Stage showDialog = DialogHelper.showDialog(content, primaryStage);

	ScopeSelectionDialogView selectorView = selectorTouple.getCodeBehind();
	selectorView.setDisplayingStage(showDialog);
    }

    @FXML
    void load_items(MouseEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Load item mapping");
	chooser.setInitialDirectory(new File("."));
	File selectedDirectory = chooser.showOpenDialog(prev_remap.getScene().getWindow());
	if (selectedDirectory != null) {
	    viewModel.loadItemMapping(selectedDirectory.getPath());
	}
    }

    @FXML
    void select_items(MouseEvent event) {
	ViewTuple<ScopeSelectionDialogView, ScopeSelectionDialogViewModel> selectorTouple = FluentViewLoader.fxmlView(ScopeSelectionDialogView.class)
		.context(context)
		.providedScopes(viewModel.getConvertMapScope())
		.load();

	ScopeSelectionDialogViewModel selectorViewModel = selectorTouple.getViewModel();
	selectorViewModel.setSelectorType(ScopeSelectionDialogViewModel.ScopeSelectorType.SCOPE_MAP_ITEMS);

	Parent content = selectorTouple.getView();
	Stage showDialog = DialogHelper.showDialog(content, primaryStage);

	ScopeSelectionDialogView selectorView = selectorTouple.getCodeBehind();
	selectorView.setDisplayingStage(showDialog);
    }

    @FXML
    void load_map(MouseEvent event) {
	ViewTuple<MapSelectionDialogView, MapSelectionDialogViewModel> selectorTouple = FluentViewLoader.fxmlView(MapSelectionDialogView.class)
		.context(context)
		.providedScopes(viewModel.getMapScope())
		.load();

	Parent content = selectorTouple.getView();
	Stage showDialog = DialogHelper.showDialog(content, primaryStage);

	MapSelectionDialogView selectorView = selectorTouple.getCodeBehind();
	selectorView.setDisplayingStage(showDialog);

	MapSelectionDialogViewModel selectorViewModel = selectorTouple.getViewModel();
    }

    @FXML
    void prev_direct_click(MouseEvent event) {

    }

    @FXML
    void prev_original_click(MouseEvent event) {

    }

    @FXML
    void prev_remap_click(MouseEvent event) {

    }

    @FXML
    void unload_items(MouseEvent event) {
	viewModel.unloadImageMapping();
    }

    @FXML
    void unload_tiles(MouseEvent event) {
	viewModel.unloadTileMapping();
    }

    // MVVMFX inject
    @InjectViewModel
    private ConvertMapTabViewModel viewModel;

    @InjectContext
    private Context context;

    @Inject
    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

	// setup renderer
	viewModel.setPreviewModel( mapViewController.getViewModel() );

	// setup radio buttons
	prev_original.setUserData(PreviewMode.ORIGINAL);
	prev_remap.setUserData(PreviewMode.REMAPPED);
	prev_direct.setUserData(PreviewMode.DIRECT);

	updateRendererMode();

	previewMode.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
	    public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
		if (previewMode.getSelectedToggle() != null) {
		    updateRendererMode();
		    viewModel.updateRenderer(false);
		}
	    }
	});

	// setup prop sheets
	ObservableList<PropertySheet.Item> map_props = map_status.getItems();
	map_props.add(new SimplePropertyItem("Loaded", "No", "", ""));

	ObservableList<PropertySheet.Item> tile_props = tileset_status.getItems();
	tile_props.add(new SimplePropertyItem("Loaded", "No", "", ""));

	ObservableList<PropertySheet.Item> item_props = item_status.getItems();
	item_props.add(new SimplePropertyItem("Loaded", "No", "", ""));

	// setup prop sheets display
	map_status.setPropertyEditorFactory(item -> {
	    return new PropertyEditor() {
		@Override
		public Node getEditor() {
		    return new Label(item.getValue().toString());
		}

		@Override
		public Object getValue() {
		    return item;
		}

		@Override
		public void setValue(Object value) {

		}
	    };
	});
	tileset_status.setPropertyEditorFactory(item -> {
	    return new PropertyEditor() {
		@Override
		public Node getEditor() {
		    return new Label(item.getValue().toString());
		}

		@Override
		public Object getValue() {
		    return item;
		}

		@Override
		public void setValue(Object value) {

		}
	    };
	});
	item_status.setPropertyEditorFactory(item -> {
	    return new PropertyEditor() {
		@Override
		public Node getEditor() {
		    return new Label(item.getValue().toString());
		}

		@Override
		public Object getValue() {
		    return item;
		}

		@Override
		public void setValue(Object value) {

		}
	    };
	});

	// setup events
	viewModel.subscribe(ConvertMapTabViewModel.MAP_LOADED, (key, value) -> {
	    viewModel.updateMapProps(map_status.getItems());
	});
	viewModel.subscribe(ConvertMapTabViewModel.TILE_MAPPING_LOADED, (key, value) -> {
	    viewModel.updateTileMappingProps(tileset_status.getItems());
	});
	viewModel.subscribe(ConvertMapTabViewModel.ITEM_MAPPING_LOADED, (key, value) -> {
	    viewModel.updateItemMappingProps(item_status.getItems());
	});
    }

    private void updateRendererMode() {
	viewModel.setRendererMode((PreviewMode) previewMode.getSelectedToggle().getUserData());
    }

}
