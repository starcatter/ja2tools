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
package thebob.ja2maptool.util.map.controller.selection;

import java.util.Observable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import thebob.ja2maptool.util.map.controller.base.MapControllerBase;
import thebob.ja2maptool.util.map.controller.converter.IMapConverterController;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author the_bob
 */
public class MapSelectionController extends MapControllerBase implements IMapSelectionController {

    /*
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
	} else if (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) {

	    if (event.isControlDown()) {
		// send cursor location to the renderer
		viewModel.getRenderer().mouseEvent(dx, dy, event.getButton(), event.isControlDown(), event.isShiftDown(), event.isAltDown());

		// if shift was clicked, try to get the selection
		if (event.isShiftDown()) {
		    viewModel.getSelection();
		} else {
		    viewModel.clearSelection();
		}

		// viewModel.scrollPreview(0, 0); // <- renderer should update itself
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
     */
    public MapSelectionController(ITileRendererManager renderer, IMapLayerManager map) {
	super(renderer, map);
    }

    @Override
    public void update(Observable o, Object arg) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEvent(MouseEvent e) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyEvent(KeyEvent e) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void disconnect() {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
