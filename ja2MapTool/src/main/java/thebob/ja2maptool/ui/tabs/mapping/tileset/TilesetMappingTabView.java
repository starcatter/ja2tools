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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.inject.Inject;
import thebob.ja2maptool.model.TileMapping;
import thebob.ja2maptool.ui.dialogs.tileselect.TileSelectionDialogView;
import thebob.ja2maptool.ui.dialogs.tileselect.TileSelectionDialogViewModel;
import thebob.ja2maptool.util.DialogHelper;

/**
 * FXML Controller class
 *
 * @author the_bob
 */
public class TilesetMappingTabView implements FxmlView<TilesetMappingTabViewModel>, Initializable {

    // FXML Bindings
    @FXML
    private ListView<String> mapping_list;

    @FXML
    private TableView<TileMapping> mapping_table;

    @FXML
    private Button mapping_save;

    @FXML
    private Button mapping_load;

    @FXML
    void mapping_load_click(MouseEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Load mapping");
	chooser.setInitialDirectory(new File("."));
	File selectedDirectory = chooser.showOpenDialog(mapping_save.getScene().getWindow());
	viewModel.loadMapping(selectedDirectory.getPath());
    }

    @FXML
    void mapping_save_click(MouseEvent event) {
	FileChooser chooser = new FileChooser();
	chooser.setTitle("Save mapping as...");
	chooser.setInitialDirectory(new File("."));
	File selectedDirectory = chooser.showSaveDialog(mapping_save.getScene().getWindow());
	viewModel.saveMapping(selectedDirectory.getPath());
    }

    @FXML
    void mapping_table_click(MouseEvent event) {

    }

    // FXML Bindings
    // MVVMFX inject
    @InjectViewModel
    private TilesetMappingTabViewModel viewModel;

    @InjectContext
    private Context context;

    @Inject
    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	initCategoryList();
	initMappingTable();

	
	viewModel.subscribe(viewModel.REBUILD_TABLE, (key, payload) -> {
	    mapping_table.setItems(viewModel.getMappingForType(mapping_list.getSelectionModel().getSelectedIndex()));
	    mapping_table.refresh();
	});	
	viewModel.subscribe(viewModel.REFRESH_TABLE, (key, payload) -> {
	    mapping_table.refresh();
	});
	
    }

    private void initCategoryList() {
	// init tile type list
	mapping_list.setItems(viewModel.getTileCategories());
	mapping_list.getSelectionModel().select(0);
	mapping_list.setOnMouseClicked(event -> {
	    if( event.isControlDown() ){
		viewModel.mapCurrentTypeTo(mapping_list.getSelectionModel().getSelectedIndex());
	    } else {
		mapping_table.setItems(viewModel.getMappingForType(mapping_list.getSelectionModel().getSelectedIndex()));
	    }
	});
    }

    private void initMappingTable() {
	TableColumn<TileMapping, String> col0 = new TableColumn<TileMapping, String>("Src #");
	TableColumn<TileMapping, Node> col1 = new TableColumn<TileMapping, Node>("Src Tile");
	TableColumn<TileMapping, String> col2 = new TableColumn<TileMapping, String>("Target #");
	TableColumn<TileMapping, Node> col3 = new TableColumn<TileMapping, Node>("Target Tile");

	col0.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, String>, ObservableValue<String>>() {
	    public ObservableValue<String> call(TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, String> p) {
		return new ReadOnlyObjectWrapper(p.getValue().getSourceType() + "-" + p.getValue().getSourceIndex());
	    }
	});

	col1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, Node>, ObservableValue<Node>>() {
	    public ObservableValue<Node> call(TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, Node> p) {
		thebob.ja2maptool.model.TileMapping tm = p.getValue();

		int index = tm.getSourceIndex() + 1;
		int type = tm.getSourceType();
		Image tile = viewModel.getSourceTileImage(type, index);

		if (tile != null) {
		    return new ReadOnlyObjectWrapper(new ImageView(tile));
		}

		return new ReadOnlyObjectWrapper("error");

	    }
	});

	col2.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, String>, ObservableValue<String>>() {
	    public ObservableValue<String> call(TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, String> p) {
		return new ReadOnlyObjectWrapper(p.getValue().getTargetType() + "-" + p.getValue().getTargetIndex());
	    }
	});

	col3.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, Node>, ObservableValue<Node>>() {
	    public ObservableValue<Node> call(TableColumn.CellDataFeatures<thebob.ja2maptool.model.TileMapping, Node> p) {
		TileMapping tm = p.getValue();

		int index = tm.getTargetIndex() + 1;
		int type = tm.getTargetType();
		Image tile = viewModel.getTargetTileImage(type, index);

		if (tile != null) {
		    return new ReadOnlyObjectWrapper(new ImageView(tile));
		}

		return new ReadOnlyObjectWrapper("error");
	    }
	});

	mapping_table.getColumns().setAll(
		col0, col1, col2, col3
	);

	// display first mapping type	
	viewModel.initTilesetMappingLists();
	mapping_table.setItems(viewModel.getMappingForType(0));

	mapping_table.setOnMouseClicked(event -> {
	    showTileSelector(mapping_list.getSelectionModel().getSelectedIndex(), mapping_table.getSelectionModel().getSelectedIndex());
	});
    }

    private void showTileSelector(int selectedType, int selectedIndex) {
	ViewTuple<TileSelectionDialogView,TileSelectionDialogViewModel> tabTouple = FluentViewLoader.fxmlView(TileSelectionDialogView.class)
		.context(context)
		.providedScopes(viewModel.getMappingScope())
		.load();

	Parent content = tabTouple.getView();
	Stage showDialog = DialogHelper.showDialog(content, primaryStage);
	
	TileSelectionDialogView selectorView = tabTouple.getCodeBehind();	
	selectorView.setDisplayingStage(showDialog);
	
	TileSelectionDialogViewModel selectorViewModel = tabTouple.getViewModel();	
	selectorViewModel.setInitialSelection(selectedType, selectedIndex);
    }
}
