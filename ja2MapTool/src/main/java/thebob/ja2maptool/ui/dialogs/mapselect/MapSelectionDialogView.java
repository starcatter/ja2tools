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
package thebob.ja2maptool.ui.dialogs.mapselect;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import thebob.assetloader.vfs.VFSConfig;
import thebob.assetmanager.AssetManager;

/**
 * FXML Controller class
 *
 * @author the_bob
 */
public class MapSelectionDialogView implements FxmlView<MapSelectionDialogViewModel>, Initializable {

    @FXML
    private TabPane tabs;

    @FXML
    private Tab tab_vfs;

    @FXML
    private Tab tab_file;
    
    @FXML
    private Label file_name;

    @FXML
    private Button file_select;

    @FXML
    private ToggleGroup vfs_type;

    @FXML
    private RadioButton vfs_mode_local;

    @FXML
    private RadioButton vfs_mode_workspace;

    @FXML
    private ChoiceBox<AssetManager> vfs_picker_workspace;

    @FXML
    private ChoiceBox<VFSConfig> vfs_picker_local;

    @FXML
    void select_map_file(MouseEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Load map file");
	File defaultDirectory = new File(".");
	chooser.setInitialDirectory(defaultDirectory);
	File selectedDirectory = chooser.showOpenDialog(file_select.getScene().getWindow());
	viewModel.selectFile(selectedDirectory.getPath());
	// showDialog.close();
    }

    @FXML
    void load_file(MouseEvent event) {
	if (vfs_type.getSelectedToggle() == vfs_mode_local) {
	    viewModel.loadFileVFS(vfs_picker_local.getSelectionModel().getSelectedItem());
	} else {
	    viewModel.loadFile(vfs_picker_workspace.getSelectionModel().getSelectedItem());
	}
    }

    @FXML
    private TreeView<String> map_list;

    @FXML
    void canceled(MouseEvent event) {
	showDialog.close();
    }

    @FXML
    void load(MouseEvent event) {
	viewModel.loadVfs(map_list.getSelectionModel().getSelectedItem());
	showDialog.close();
    }

    // MVVMFX inject
    @InjectViewModel
    private MapSelectionDialogViewModel viewModel;

    private Stage showDialog;

    public void setDisplayingStage(Stage showDialog) {
	this.showDialog = showDialog;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	map_list.setRoot(viewModel.getMapListRoot());

	// if there's no VFS loaded, default to file tab
	if( viewModel.getVfs().getManagers().isEmpty() ){
	    tabs.getSelectionModel().select(tab_file);
	}
	
	viewModel.subscribe(MapSelectionDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
	    showDialog.close();
	});

	viewModel.subscribe(MapSelectionDialogViewModel.FILE_NAME_CHANGED, (key, payload) -> {
	    file_name.setText((String) payload[0]);
	});

	// setup workspace VFS picker
	vfs_mode_workspace.setDisable(true);
	vfs_picker_workspace.setDisable(true);
	
	vfs_picker_workspace.itemsProperty().addListener(event -> { // items are cloned from loaded asset manager list, so update status when list is swapped
	    vfs_mode_workspace.setDisable(vfs_picker_workspace.getItems().isEmpty());
	    vfs_picker_workspace.setDisable(vfs_picker_workspace.getItems().isEmpty());
	    Platform.runLater(() -> vfs_picker_workspace.getSelectionModel().selectFirst());
	});
	vfs_picker_workspace.setItems(viewModel.getLoadedVfsList());

	vfs_picker_workspace.setConverter( new StringConverter<AssetManager>() {
	    @Override
	    public String toString(AssetManager object) {
		return object.getVfs().getPath().getFileName().toString();
	    }

	    @Override
	    public AssetManager fromString(String string) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }
	});
	
	
	// setup map file local VFS picker
	vfs_mode_local.setDisable(true);
	vfs_picker_local.setDisable(true);
	
	vfs_picker_local.setItems(viewModel.getLocalVfsList());
	vfs_picker_local.getItems().addListener((ListChangeListener.Change<? extends VFSConfig> c) -> {	// bound to an actual observable list which may change
	    if (viewModel.getLocalVfsList().isEmpty()) {
		vfs_mode_local.setDisable(true);
		vfs_picker_local.setDisable(true);
		if (vfs_type.getSelectedToggle() == vfs_mode_local && vfs_mode_workspace.isDisabled() == false) {
		    vfs_type.selectToggle(vfs_mode_workspace);
		}
	    } else {
		vfs_mode_local.setDisable(false);
		vfs_picker_local.setDisable(false);
		if (vfs_type.getSelectedToggle() == vfs_mode_workspace && vfs_mode_workspace.isDisabled()) {
		    vfs_type.selectToggle(vfs_mode_local);
		}
	    }
	    
	    Platform.runLater(() -> vfs_picker_local.getSelectionModel().selectFirst());
	});

	vfs_picker_local.setConverter( new StringConverter<VFSConfig>() {
	    @Override
	    public String toString(VFSConfig object) {
		return object.getPath().getFileName().toString();
	    }

	    @Override
	    public VFSConfig fromString(String string) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	    }
	});
	
    }

}
