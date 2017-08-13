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
package thebob.ja2maptool.ui.tabs.mapping.setup;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import thebob.ja2maptool.ui.main.MainScreenViewModel;
import thebob.ja2maptool.ui.tabs.mapping.tileset.TilesetMappingTabView;
import thebob.ja2maptool.ui.tabs.mapping.tileset.TilesetMappingTabViewModel;
import thebob.ja2maptool.ui.tabs.vfs.VfsSetupTabViewModel;

public class MappingSetupTabView implements FxmlView<MappingSetupTabViewModel>, Initializable {

    @FXML
    private TreeView<String> map_left;

    @FXML
    private TreeView<String> map_right;

    @FXML
    private Button map_create;

    @FXML
    void map_create_click(MouseEvent event) {	
	viewModel.createMappingTab(map_left.getSelectionModel().getSelectedItem(), map_right.getSelectionModel().getSelectedItem());
    }
    
    // MVVMFX inject
    @InjectViewModel
    private MappingSetupTabViewModel viewModel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
	map_left.setRoot(viewModel.getMapLeftRoot());
	map_right.setRoot(viewModel.getMapRightRoot());	
    }

}