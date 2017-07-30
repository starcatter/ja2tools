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
package thebob.ja2maptool.ui.tabs.viewers.map;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author the_bob
 */
public class MapViewerTabView implements FxmlView<MapViewerTabViewModel>, Initializable {

    double zoomFactor = 0.05;

    double minZoom = 0.25d;
    double maxZoom = 2.5d;

    @FXML
    private Label map_name;

    @FXML
    private Canvas prev_window;

    @FXML
    void prev_window_click(MouseEvent event) {
	double wx = prev_window.getWidth() / 2d;
	double wy = prev_window.getHeight() / 2d;

	double dx = event.getX();
	double dy = event.getY();

	double deltaX = (dx - wx) / wx;
	double deltaY = (dy - wy) / wx;

	double transX = deltaX / 2 + deltaY / 2;
	double transY = deltaY - deltaX / 2;

	System.out.println("thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabView.prev_window_click(): " + deltaX + " / " + deltaY);

	viewModel.scrollPreview((int) (transX * 10d), (int) (transY * 10d));
    }

    @FXML
    void prev_window_scroll(ScrollEvent event) {

	double scrolled = event.getDeltaY() / event.getMultiplierY();
	double zoom = viewModel.getRenderer().getScale();
	double newZoom = zoom + scrolled * zoomFactor;
	if (newZoom < minZoom) {
	    newZoom = minZoom;
	}
	if (newZoom > maxZoom) {
	    newZoom = maxZoom;
	}
	if (zoom != newZoom) {
	    viewModel.getRenderer().setScale(newZoom);
	    viewModel.scrollPreview(0, 0);
	}

    }

    // MVVMFX inject
    @InjectViewModel
    private MapViewerTabViewModel viewModel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	// setup canvas autosize
	HBox parent = (HBox) prev_window.getParent();
	prev_window.heightProperty().bind(parent.heightProperty().subtract(5));
	prev_window.widthProperty().bind(parent.widthProperty().subtract(5));

	prev_window.heightProperty().addListener(event -> {
	    viewModel.getRenderer().setCanvas(prev_window);
	    viewModel.scrollPreview(0, 0);
	});

	prev_window.widthProperty().addListener(event -> {
	    viewModel.getRenderer().setCanvas(prev_window);
	    viewModel.scrollPreview(0, 0);
	});

	// setup renderer
	viewModel.getRenderer().setCanvas(prev_window);

	map_name.textProperty().bind( viewModel.getMapNameProperty() );
	viewModel.updateRenderer(true);
    }

    public MapViewerTabViewModel getViewModel() {
	return viewModel;
    }

}
