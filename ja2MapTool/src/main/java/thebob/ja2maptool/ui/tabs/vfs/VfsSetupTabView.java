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
package thebob.ja2maptool.ui.tabs.vfs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import static jdk.nashorn.internal.objects.NativeFunction.bind;
import org.controlsfx.control.CheckTreeView;

/**
 * FXML Controller class
 *
 * @author the_bob
 */
public class VfsSetupTabView implements FxmlView<VfsSetupTabViewModel>, Initializable {

    @FXML
    private Button config_add_dir;

    @FXML
    private Button config_load;

    @FXML
    private ProgressBar progress;

    @FXML
    private CheckTreeView<String> config_main;

    @FXML
    void config_add_dir_click(MouseEvent event) {
	DirectoryChooser chooser = new DirectoryChooser();
	chooser.setTitle("Select directory containing VFS configs");
	File defaultDirectory = new File(".");
	chooser.setInitialDirectory(defaultDirectory);
	File selectedDirectory = chooser.showDialog(config_add_dir.getScene().getWindow());
	viewModel.addConfig(selectedDirectory.getPath());
    }

    @FXML
    void config_load_click(MouseEvent event) {
	//viewModel.loadSelectedConfigs();
	viewModel.getLoadSelectedConfigsCommand().execute();
    }

    // MVVMFX inject
    @InjectViewModel
    private VfsSetupTabViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	config_main.setRoot(viewModel.getConfigTreeRoot());

	config_load.disableProperty().bind(viewModel.getLoadSelectedConfigsCommand().executableProperty().not());
	progress.visibleProperty().bind(viewModel.getLoadSelectedConfigsCommand().runningProperty());
	//progress.progressProperty().bind(viewModel.getLoadSelectedConfigsCommand().progressProperty());
	progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
	
	config_main.getCheckModel().getCheckedItems().addListener( new ListChangeListener<TreeItem<String>>(){
	    @Override
	    public void onChanged(ListChangeListener.Change<? extends TreeItem<String>> c) {
		viewModel.configSelected( config_main.getCheckModel().getCheckedItems().size() > 0 );
	    }	    
	});
    }

}
