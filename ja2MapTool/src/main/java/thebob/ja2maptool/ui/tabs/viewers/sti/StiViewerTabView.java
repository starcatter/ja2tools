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
package thebob.ja2maptool.ui.tabs.viewers.sti;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author the_bob
 */
public class StiViewerTabView implements FxmlView<StiViewerTabViewModel>, Initializable {

    @FXML
    private Label sti_name;

    @FXML
    private Button prevFrameBtn;

    @FXML
    private Button nextFrameBtn;

    @FXML
    private Label sti_frame;

    @FXML
    private BorderPane main_pane;

    @FXML
    private GridPane palettePane;

    @FXML
    private ImageView main_image;

    @FXML
    private StackPane main_image_container;

    @FXML
    private TilePane prevPane;
    @FXML
    private AnchorPane prevPaneContainer;

    @FXML
    void nextFrame(Event event) {
	if (viewModel.nextImage()) {
	    updateImage(false);
	}
    }

    @FXML
    void prevFrame(Event event) {
	if (viewModel.prevImage()) {
	    updateImage(false);
	}
    }

    @FXML
    void togglePalette(Event event) {
	if (main_pane.getLeft() == null) {
	    initPalette();
	    main_pane.setLeft(palettePane);
	} else {
	    main_pane.setLeft(null);
	}
    }

    @FXML
    void togglePreview(Event event) {
	if (main_pane.getRight() == null) {
	    initPreview();
	    main_pane.setRight(prevPaneContainer);
	} else {
	    main_pane.setRight(null);
	}

    }

    double scale = 1.0d;
    double baseWidth = 0.0d;
    double baseHeight = 0.0d;

    private void updateImage(boolean scaleFit) {
	Image image = viewModel.getImage();
	
	
	baseWidth = image.getWidth();
	baseHeight = image.getHeight();

	if (scaleFit) {
	    //Platform.runLater(() -> {
		main_image_container.getParent().layout();
		double b = main_image_container.getWidth();
		scale = (b * 0.8d) / (double) baseWidth;

		System.out.println("thebob.ja2maptool.ui.tabs.viewers.sti.StiViewerTabView.updateImage() window width: " + b);
		System.out.println("thebob.ja2maptool.ui.tabs.viewers.sti.StiViewerTabView.updateImage() baseWidth: " + baseWidth);
		System.out.println("thebob.ja2maptool.ui.tabs.viewers.sti.StiViewerTabView.updateImage() inital scale: " + scale);

		scaleImage();
		main_image.setImage(image);
	   //});
	} else {
	    main_image.setImage(image);
	    scaleImage();
	}

    }

    private void scaleImage() {
	double w = baseWidth * scale;
	double h = baseHeight * scale;

	if (main_image_container.getWidth() - 10d < w) {
	    w = main_image_container.getWidth() - 10d;
	}
	if (main_image_container.getHeight() - 10d < h) {
	    h = main_image_container.getHeight() - 10d;
	}

	main_image.setFitWidth(w);
	main_image.setFitHeight(h);
    }

    // MVVMFX inject
    @InjectViewModel
    private StiViewerTabViewModel viewModel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	main_pane.setLeft(null);
	main_pane.setRight(null);

	sti_name.textProperty().bind(viewModel.getStiName());
	sti_frame.textProperty().bind(viewModel.getStiStatus());
	prevFrameBtn.disableProperty().bind(viewModel.getCurrentIndex().lessThan(1));
	nextFrameBtn.disableProperty().bind(viewModel.getCurrentIndex().greaterThanOrEqualTo(viewModel.getMaxIndex()));

	updateImage(true);

	main_image.setOnScroll(event -> {
	    double scrolled = event.getDeltaY() / event.getMultiplierY();
	    scale += scrolled * 0.1d;

	    if (scale > 4.0d) {
		scale = 4.0d;
	    }

	    if (scale < 0.5d) {
		scale = 0.5d;
	    }
	    scaleImage();
	});
    }

    int r = 0, g = 1, b = 2;

    private void initPalette() {
	byte[][] pal = viewModel.getPalette();
	palettePane.getChildren().clear();

	int row = 0;
	int col = 0;

	for (int i = 0; i < 256; i++) {
	    Color c = new Color((pal[r][i] & 0xff) / 255d, (pal[g][i] & 0xff) / 255d, (pal[b][i] & 0xff) / 255d, 1d);
	    Pane p = new Pane();
	    p.setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));
	    palettePane.getChildren().add(p);
	    palettePane.setRowIndex(p, row);
	    palettePane.setColumnIndex(p, col);

	    col++;
	    if (col >= 8) {
		col = 0;
		row++;
	    }
	}
    }

    private void initPreview() {
	prevPane.getChildren().clear();
	for (int i = 0; i < viewModel.getMaxIndex().get(); i++) {
	    prevPane.getChildren().add(new ImageView(viewModel.getImage(i)));
	}
    }

}
