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
package thebob.ja2maptool.ui.tabs.intro;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import thebob.ja2maptool.ui.tabs.convert.IntroTabViewModel;

public class IntroTabView implements FxmlView<IntroTabViewModel>, Initializable {

    @FXML
    private Text intro_config;

    @FXML
    private Text intro_map;

    @FXML
    private Text intro_convert;

    @FXML
    void intro_config_click(MouseEvent event) {
	viewModel.goToConfigSetupTab();
    }

    @FXML
    void intro_convert_click(MouseEvent event) {
	viewModel.goToConvertSetupTab();
    }

    @FXML
    void intro_map_click(MouseEvent event) {
	viewModel.goToMapSetupTab();
    }
    
    // MVVMFX inject
    @InjectViewModel
    private IntroTabViewModel viewModel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
	
    }

}