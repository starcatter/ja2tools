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
package thebob.ja2maptool.ui.tabs.intro;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.inject.Inject;
import thebob.ja2maptool.scopes.view.StiViewerScope;
import thebob.ja2maptool.ui.tabs.viewers.sti.StiViewerTabView;
import thebob.ja2maptool.ui.tabs.viewers.sti.StiViewerTabViewModel;

public class IntroTabView implements FxmlView<IntroTabViewModel>, Initializable {

    @FXML
    private AnchorPane intro_pane;

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

    private void showLogo() {
	intro_pane.getChildren().clear();

	StiViewerScope scope = new StiViewerScope();
	scope.setFilePath("..\\..\\JA113.data\\gameData\\Data\\Interface\\SIRTECHSPLASH.STI");

	ViewTuple<StiViewerTabView, StiViewerTabViewModel> selectorTouple = FluentViewLoader.fxmlView(StiViewerTabView.class)
		.context(context)
		.providedScopes(scope)
		.load();

	intro_pane.getChildren().add(
		selectorTouple.getView()
	);
    }

    // MVVMFX inject
    @InjectViewModel
    private IntroTabViewModel viewModel;

    @Inject
    private Stage primaryStage;

    @InjectContext
    private Context context;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	//showLogo();
    }

}
