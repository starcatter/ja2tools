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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.inject.Inject;
import thebob.ja2maptool.ui.dialogs.mapselect.MapSelectionDialogView;
import thebob.ja2maptool.ui.dialogs.mapselect.MapSelectionDialogViewModel;
import static thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel.TREE_UPDATED;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabView;
import thebob.ja2maptool.util.DialogHelper;
import thebob.ja2maptool.util.compositor.SnippetPlacement;

/**
 *
 * @author the_bob
 */
public class CompositorTabView implements FxmlView<CompositorTabViewModel>, Initializable {


    @FXML
    private TreeView<String> snippet_list;

    @FXML
    private Button load_map_btn;
    
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

    // unused?
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
    // Placement tab
    

	// selected placement options
	
    @FXML
    private CheckBox placements_land;

    @FXML
    private CheckBox placement_objects;

    @FXML
    private CheckBox placement_structures;

    @FXML
    private CheckBox placement_shadows;

    @FXML
    private CheckBox placement_roofs;

    @FXML
    private CheckBox placement_onRoof;

    @FXML
    private CheckBox placement_land_floors;

    @FXML
    private CheckBox placement_structures_walls;

	// selected placement desc
	
    @FXML
    private Label placement_name;

    @FXML
    private Label placement_size;

    @FXML
    private Label placement_location;

	// placement list mgmt

    @FXML
    private ListView<SnippetPlacement> placement_list;
	
    @FXML
    private Button load_placements_btn;

    @FXML
    private Button save_placements_btn;

    @FXML
    private Button paste_placements_btn;
	
    @FXML
    void load_placements(ActionEvent event) {

    }

    @FXML
    void paste_placements(ActionEvent event) {

    }


    @FXML
    void save_placements(ActionEvent event) {

    }
    
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

        // setup snippet list
	snippet_list.setRoot(viewModel.getListRoot());
	snippet_list.setOnMouseClicked(event -> {
	    viewModel.updateSnippetSelection(snippet_list.getSelectionModel().getSelectedItem());
	});

        // setup snippet placement options
	snippet_land.selectedProperty().bindBidirectional(viewModel.getSnippet_land());
	snippet_objects.selectedProperty().bindBidirectional(viewModel.getSnippet_objects());
	snippet_structures.selectedProperty().bindBidirectional(viewModel.getSnippet_structures());
	snippet_shadows.selectedProperty().bindBidirectional(viewModel.getSnippet_shadows());
	snippet_roofs.selectedProperty().bindBidirectional(viewModel.getSnippet_roofs());
	snippet_onRoof.selectedProperty().bindBidirectional(viewModel.getSnippet_onRoof());
	
	snippet_land_floors.selectedProperty().bindBidirectional(viewModel.getSnippet_land_floors());	
	snippet_structures_walls.selectedProperty().bindBidirectional(viewModel.getSnippet_structures_walls());
	
        // hook up tree update
	viewModel.subscribe(TREE_UPDATED, (key,value)->{
	    viewModel.updateSnippetSelection(snippet_list.getSelectionModel().getSelectedItem());
	});
        
        // setup placement list
        placement_list.setItems( viewModel.getPlacementListContents() );
    }
}
