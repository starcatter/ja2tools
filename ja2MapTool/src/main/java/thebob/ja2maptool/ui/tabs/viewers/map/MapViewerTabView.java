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
package thebob.ja2maptool.ui.tabs.viewers.map;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.inject.Inject;
import static thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabViewModel.VIEWER_MODE_SET;

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

    void toggleToolbars() {
        if (toolbarVisible == false) {
            preview_wrapper.setRight(preview_controls_right);
            toolbarVisible = true;
        } else {
            preview_wrapper.setRight(null);
            toolbarVisible = false;
        }
    }

    @FXML
    void prev_window_click(MouseEvent event) {

        prev_window.requestFocus();

        double dx = event.getX();
        double dy = event.getY();

        if (event.getButton() == MouseButton.MIDDLE) {
            toggleToolbars();
        }/* else if (event.getButton() == MouseButton.PRIMARY) {

	    // move the window toward the click location
	    double wx = prev_window.getWidth() / 2d;
	    double wy = prev_window.getHeight() / 2d;

	    double deltaX = (dx - wx) / wx;
	    double deltaY = (dy - wy) / wx;

	    double transX = deltaX / 2 + deltaY / 2;
	    double transY = deltaY - deltaX / 2;

	    viewModel.scrollPreview((int) (transX * 10d), (int) (transY * 10d));
	}
         */
    }

    @FXML
    void prev_window_scroll(ScrollEvent event) {

        double scrolled = event.getDeltaY() / event.getMultiplierY();
        double zoom = viewModel.getViewer().getScale();
        double newZoom = zoom + scrolled * zoomFactor;
        if (newZoom < minZoom) {
            newZoom = minZoom;
        }
        if (newZoom > maxZoom) {
            newZoom = maxZoom;
        }
        if (zoom != newZoom) {
            viewModel.getViewer().setScale(newZoom);
        }

    }

    @FXML
    void prev_window_context(ContextMenuEvent event) {

    }

    @FXML
    void prev_window_dragged(MouseEvent event) {

    }

    @FXML
    void prev_window_entered(MouseEvent event) {

    }

    @FXML
    void prev_window_exited(MouseEvent event) {

    }

    @FXML
    void prev_window_key_press(KeyEvent event) {
        event.consume();
    }

    @FXML
    void prev_window_key_release(KeyEvent event) {
        event.consume();
    }

    @FXML
    void prev_window_key_typed(KeyEvent event) {
        event.consume();
    }

    @FXML
    void prev_window_moved(MouseEvent event) {
        /*
	if (event.isControlDown()) {
	    if (!cursorInViewer) {
		prev_window.setCursor(Cursor.NONE);
		cursorInViewer = true;
	    }

	    double dx = event.getX();
	    double dy = event.getY();

	    viewModel.getRenderer().sendCursor(dx, dy, event.isControlDown(), event.isShiftDown(), event.isAltDown());
	    // viewModel.scrollPreview(0, 0); // <- renderer should update itself
	} else if (cursorInViewer) {
	    prev_window.setCursor(Cursor.MOVE);
	    viewModel.getRenderer().hideCursor();
	    // viewModel.scrollPreview(0, 0); // <- renderer should update itself

	    cursorInViewer = false;
	}	
         */
    }

    @FXML
    void prev_window_pressed(MouseEvent event) {

    }

    @FXML
    void prev_window_released(MouseEvent event) {

    }

    @FXML
    void prev_window_scroll_end(ScrollEvent event) {

    }

    @FXML
    void prev_window_scroll_start(ScrollEvent event) {

    }

    // MVVMFX inject
    @InjectViewModel
    private MapViewerTabViewModel viewModel;

    @Inject
    private Stage primaryStage;

    boolean cursorInViewer = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // setup canvas autosize
        StackPane parent = (StackPane) prev_window.getParent();
        prev_window.heightProperty().bind(parent.heightProperty().subtract(5));
        prev_window.widthProperty().bind(parent.widthProperty().subtract(5));

        prev_window.heightProperty().addListener(event -> {
            viewModel.getRenderer().setCanvas(prev_window);
        });

        prev_window.widthProperty().addListener(event -> {
            viewModel.getRenderer().setCanvas(prev_window);
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

        viewModel.setLayerButtons(viewerButtons);

        // events
        viewModel.subscribe(MapViewerTabViewModel.TOOLBAR_SWITCH, (key, value) -> {
            toggleToolbars();
        });

        viewModel.subscribe(MapViewerTabViewModel.FOCUS_WINDOW, (key, value) -> {
            prev_window.requestFocus();
        });

        viewModel.subscribe(VIEWER_MODE_SET, (key, value) -> {
            MapViewerTabViewModel.MapViewerMode mode = (MapViewerTabViewModel.MapViewerMode) value[0];
            switch (mode) {
                case Browser:
                    prev_window.setCursor(Cursor.MOVE);
                    break;
                case Editor:
                    prev_window.setCursor(Cursor.NONE);
                    break;
                default:
                    throw new AssertionError(mode.name());
            }
        });

        // ready to go!
        viewModel.updateRenderer(true);

        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            System.out.println("thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabView.initialize() shutdown renderer...");
            viewModel.shutdownRenderer();
        });
    }

    public MapViewerTabViewModel getViewModel() {
        return viewModel;
    }

}
