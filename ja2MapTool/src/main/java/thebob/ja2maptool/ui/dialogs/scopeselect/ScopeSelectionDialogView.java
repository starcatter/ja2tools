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
package thebob.ja2maptool.ui.dialogs.scopeselect;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.Scope;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import thebob.ja2maptool.scopes.mapping.ItemMappingScope;
import thebob.ja2maptool.scopes.mapping.TilesetMappingScope;

/**
 * FXML Controller class
 *
 * @author the_bob
 */
public class ScopeSelectionDialogView implements FxmlView<ScopeSelectionDialogViewModel>, Initializable {

    @FXML
    private TreeView<Scope> map_list;

    @FXML
    void canceled(MouseEvent event) {
	showDialog.close();
    }

    @FXML
    void load(MouseEvent event) {
	viewModel.loadScope(map_list.getSelectionModel().getSelectedItem().getValue());
	showDialog.close();
    }

    // MVVMFX inject
    @InjectViewModel
    private ScopeSelectionDialogViewModel viewModel;

    private Stage showDialog;

    public void setDisplayingStage(Stage showDialog) {
	this.showDialog = showDialog;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	map_list.setCellFactory(new Callback<TreeView<Scope>, TreeCell<Scope>>() {
	    @Override
	    public TreeCell<Scope> call(TreeView<Scope> tree) {
		return new TreeCell<Scope>() {
		    @Override
		    protected void updateItem(Scope item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
			    setText(null);
			    setGraphic(null);
			} else if (item == null) {
			    setText("Select mapping");
			} else if (item instanceof TilesetMappingScope) {
			    TilesetMappingScope scope = (TilesetMappingScope) item;
			    setText(
				    scope.getSourceAssets().getVfs().getPath().getFileName().toString()
				    + "/tileset"
				    + scope.getSourceTilesetId() + " (" + scope.getSourceTileset().getName() + ")"
				    + " -> "
				    + scope.getTargetAssets().getVfs().getPath().getFileName().toString()
				    + "/tileset"
				    + scope.getTargetTilesetId() + " (" + scope.getTargetTileset().getName() + ")"
			    );
			} else if (item instanceof ItemMappingScope) {
			    ItemMappingScope scope = (ItemMappingScope) item;
			    setText(
				    scope.getSourceAssets().getVfs().getPath().getFileName().toString()
				    + " -> "
				    + scope.getTargetAssets().getVfs().getPath().getFileName().toString()
				    + " ( " + scope.getMapping().size() + " mappings ) "
			    );
			}
		    }
		};
	    }
	});

	map_list.setRoot(viewModel.getScopeListRoot());

	viewModel.subscribe(ScopeSelectionDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
	    showDialog.close();
	});
    }

}
