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
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author the_bob
 */
public class MapViewerTabView implements FxmlView<MapViewerTabViewModel>, Initializable {

    // viewer controls
    @FXML
    private BorderPane preview_wrapper;

    @FXML
    private ToolBar preview_controls_right;

    @FXML
    private ToggleButton layer_land;

    @FXML
    private ToggleButton layer_object;

    @FXML
    private ToggleButton layer_struct;

    @FXML
    private ToggleButton layer_shadow;

    @FXML
    private ToggleButton layer_roof;

    @FXML
    private ToggleButton layer_onroof;

    // -------------
    double zoomFactor = 0.05;

    double minZoom = 0.25d;
    double maxZoom = 2.5d;

    @FXML
    private Label map_name;

    @FXML
    private Canvas prev_window;

    boolean toolbarVisible = false;

    @FXML
    void prev_window_click(MouseEvent event) {

	double dx = event.getX();
	double dy = event.getY();

	if (event.getButton() == MouseButton.MIDDLE) {
	    if (toolbarVisible == false) {
		preview_wrapper.setRight(preview_controls_right);
		toolbarVisible = true;
	    } else {
		preview_wrapper.setRight(null);
		toolbarVisible = false;
	    }
	} else if (event.getButton() == MouseButton.SECONDARY) {
	    // TODO tile mapping aid
	} else if (event.getButton() == MouseButton.PRIMARY) {

	    if (event.isControlDown()) {
		// send cursor location to the renderer
		viewModel.getRenderer().sendClick(dx, dy, event.isControlDown(), event.isShiftDown(), event.isAltDown());

		// if shift was clicked, try to get the selection
		if (event.isShiftDown()) {
		    viewModel.getSelection();
		} else {
		    viewModel.clearSelection();
		}

		viewModel.scrollPreview(0, 0);
	    } else {
		// move the window toward the click location
		double wx = prev_window.getWidth() / 2d;
		double wy = prev_window.getHeight() / 2d;

		double deltaX = (dx - wx) / wx;
		double deltaY = (dy - wy) / wx;

		double transX = deltaX / 2 + deltaY / 2;
		double transY = deltaY - deltaX / 2;

		viewModel.getRenderer().hideCursor();
		viewModel.clearSelection();
		viewModel.scrollPreview((int) (transX * 10d), (int) (transY * 10d));
	    }
	}
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

    boolean cursorInViewer = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	// setup canvas autosize
	StackPane parent = (StackPane) prev_window.getParent();
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

	// mouse movement handler TODO: move to FXML event
	prev_window.setOnMouseMoved(event -> {
	    if (event.isControlDown()) {
		if (!cursorInViewer) {
		    prev_window.setCursor(Cursor.NONE);
		    cursorInViewer = true;
		}

		double dx = event.getX();
		double dy = event.getY();

		viewModel.getRenderer().sendCursor(dx, dy, event.isControlDown(), event.isShiftDown(), event.isAltDown());
		viewModel.scrollPreview(0, 0);
	    } else if (cursorInViewer) {
		prev_window.setCursor(Cursor.MOVE);
		viewModel.getRenderer().hideCursor();
		viewModel.scrollPreview(0, 0);

		cursorInViewer = false;
	    }
	});

	// hide right toolbar
	preview_wrapper.setRight(null);
	
	// setup renderer
	viewModel.getRenderer().setCanvas(prev_window);

	// bind map name
	map_name.textProperty().bind(viewModel.getMapNameProperty());
	
	// layer buttons
	BooleanProperty[] viewerButtons = new BooleanProperty[]{
	    layer_land.selectedProperty(),
	    layer_object.selectedProperty(),
	    layer_struct.selectedProperty(),
	    layer_shadow.selectedProperty(),
	    layer_roof.selectedProperty(),
	    layer_onroof.selectedProperty()
	};

	for (BooleanProperty button : viewerButtons) {
	    button.addListener(event -> {
		Platform.runLater(() -> {
		    viewModel.scrollPreview(0, 0);
		});

	    });
	}

	viewModel.setLayerButtons(viewerButtons);
	
	// ready to go!
	viewModel.updateRenderer(true);
    }

    public MapViewerTabViewModel getViewModel() {
	return viewModel;
    }

}
