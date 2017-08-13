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
package thebob.ja2maptool.ui.dialogs.tileselect;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import thebob.assetloader.tileset.Tile;
import static thebob.ja2maptool.ui.dialogs.tileselect.TileSelectionDialogViewModel.UPDATE_SELECTION;
import static thebob.ja2maptool.ui.tabs.mapping.tileset.TilesetMappingTabView.getTableCellTileImage;

/**
 * FXML Controller class
 *
 * @author the_bob
 */
public class TileSelectionDialogView implements FxmlView<TileSelectionDialogViewModel>, Initializable {

    // FXML Bindings
    @FXML
    private DialogPane dialog;

    @FXML
    private ListView<String> type_list;

    @FXML
    private TableView<Tile> tile_table;

    @FXML
    private Button btn_cancel;

    @FXML
    private Button btn_ok;

    @FXML
    void btn_cancel_click(MouseEvent event) {
	showDialog.close();
    }

    @FXML
    void btn_ok_click(MouseEvent event) {
	Tile tile = tile_table.getSelectionModel().getSelectedItem();
	viewModel.updateTiles(tile.getType(), tile.getIndex());
	showDialog.close();
    }

    // FXML Bindings
    // MVVMFX inject
    @InjectViewModel
    private TileSelectionDialogViewModel viewModel;

    private Stage showDialog;

    public void setDisplayingStage(Stage showDialog) {
	this.showDialog = showDialog;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	type_list.setItems(viewModel.getTileCategories());
	type_list.getSelectionModel().select(viewModel.getType());

	type_list.setOnMouseClicked(event -> {
	    tile_table.setItems(viewModel.getTilesForType(type_list.getSelectionModel().getSelectedIndex()));
	});

	TableColumn<Tile, String> tc0 = new TableColumn<Tile, String>("#");
	TableColumn<Tile, Node> tc1 = new TableColumn<Tile, Node>("Tile");

	tc0.setCellValueFactory(new Callback<CellDataFeatures<Tile, String>, ObservableValue<String>>() {
	    public ObservableValue<String> call(CellDataFeatures<Tile, String> p) {
		Tile tile = p.getValue();
		return new ReadOnlyObjectWrapper(tile.getType() + "-" + tile.getIndex());
	    }
	});

	tc1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Tile, Node>, ObservableValue<Node>>() {
	    public ObservableValue<Node> call(TableColumn.CellDataFeatures<Tile, Node> p) {
		Tile tile = p.getValue();
		if (tile != null) {
		    Image tileImage = tile.getImage();

		    if (tileImage != null) {
			return new ReadOnlyObjectWrapper(getTableCellTileImage(tileImage, tile.getType(), tile.getIndex()));
		    }

		}
		return new ReadOnlyObjectWrapper("error");
	    }
	});

	tile_table.getColumns().setAll(
		tc0,
		tc1);

	tile_table.setItems(viewModel.getTilesForType(viewModel.getType()));

	viewModel.subscribe(UPDATE_SELECTION, (key, data) -> {
	    type_list.getSelectionModel().select(viewModel.getType());
	    type_list.scrollTo(viewModel.getType());
	    
	    tile_table.setItems(viewModel.getTilesForType(viewModel.getType()));
	    tile_table.getSelectionModel().select(viewModel.getIndex());
	    tile_table.scrollTo(viewModel.getIndex());
	});

	viewModel.subscribe(TileSelectionDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
	    showDialog.close();
	});
    }

}
