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

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.inject.Inject;
import thebob.ja2maptool.ui.dialogs.mapselect.MapSelectionDialogView;
import thebob.ja2maptool.ui.dialogs.mapselect.MapSelectionDialogViewModel;
import static thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.TREE_UPDATED;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabView;
import thebob.ja2maptool.util.DialogHelper;

/**
 *
 * @author the_bob
 */
public class CompositorTabView implements FxmlView<CompositorTabViewModel>, Initializable {

    @FXML
    private Button load_map_btn;

    @FXML
    private TreeView<String> snippet_list;

    @FXML
    private Button load_snippet_btn;

    @FXML
    private Button copy_snippet_btn;

    @FXML
    private Button place_snippet_btn;

    @FXML
    private Button save_map_btn;

    @FXML
    void load_map(ActionEvent event) {
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
    void load_snippet(ActionEvent event) {

    }

    @FXML
    void place_snippet(ActionEvent event) {
	viewModel.placeSnippet();
    }

    @FXML
    void save_map(ActionEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Save converted map as...");
	chooser.setInitialDirectory(new File("."));
	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("map files", "*.dat"));		
	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("all files", "*.*"));
	File selectedDirectory = chooser.showSaveDialog(save_map_btn.getScene().getWindow());
	if (selectedDirectory != null) {
	    viewModel.saveMap(selectedDirectory.getPath());
	}
    }

    @FXML
    void copy_snippet(ActionEvent event) {
	viewModel.copySnippet();
    }

    // ---------------------------------------
    // checkbox
    @FXML
    private CheckBox snippet_land;

    @FXML
    private CheckBox snippet_objects;

    @FXML
    private CheckBox snippet_structures;

    @FXML
    private CheckBox snippet_shadows;

    @FXML
    private CheckBox snippet_roofs;

    @FXML
    private CheckBox snippet_onRoof;

    @FXML
    private CheckBox snippet_structures_walls;

    @FXML
    private CheckBox snippet_land_floors;

    // ---------------------------------------       
    // MVVMFX inject
    @InjectViewModel
    private CompositorTabViewModel viewModel;

    @InjectContext
    private Context context;

    @Inject
    private Stage primaryStage;

    @FXML
    // Inject the Code behind instance of the MapViewerTabView by using the name convention ...Controller
    private MapViewerTabView mapViewController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

	// setup renderer
	viewModel.setPreviewModel(mapViewController.getViewModel());

	snippet_list.setRoot(viewModel.getListRoot());
	snippet_list.setOnMouseClicked(event -> {
	    viewModel.updateSnippetSelection(snippet_list.getSelectionModel().getSelectedItem());
	});

	snippet_land.selectedProperty().bindBidirectional(viewModel.getSnippet_land());
	snippet_objects.selectedProperty().bindBidirectional(viewModel.getSnippet_objects());
	snippet_structures.selectedProperty().bindBidirectional(viewModel.getSnippet_structures());
	snippet_shadows.selectedProperty().bindBidirectional(viewModel.getSnippet_shadows());
	snippet_roofs.selectedProperty().bindBidirectional(viewModel.getSnippet_roofs());
	snippet_onRoof.selectedProperty().bindBidirectional(viewModel.getSnippet_onRoof());
	
	snippet_land_floors.selectedProperty().bindBidirectional(viewModel.getSnippet_land_floors());	
	snippet_structures_walls.selectedProperty().bindBidirectional(viewModel.getSnippet_structures_walls());
	
	viewModel.subscribe(TREE_UPDATED, (key,value)->{
	    viewModel.updateSnippetSelection(snippet_list.getSelectionModel().getSelectedItem());
	});
    }
}
