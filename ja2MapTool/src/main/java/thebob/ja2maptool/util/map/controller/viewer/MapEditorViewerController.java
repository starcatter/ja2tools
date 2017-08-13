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
package thebob.ja2maptool.util.map.controller.viewer;

import thebob.ja2maptool.util.map.controller.viewer.base.MapViewerControllerBase;
import java.util.Observable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabViewModel;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 * Handles input events for map scrolling, behaves in a way more befitting an editor rather than a map viewer
 *
 * @author the_bob
 */
public class MapEditorViewerController extends MapViewerControllerBase {

    public MapEditorViewerController(ITileRendererManager renderer, IMapLayerManager map, MapViewerTabViewModel viewWindow) {
	super(renderer, map, viewWindow);
    }

    public void update(Observable o, Object arg) {
	// TODO: toolbar add/remove events
    }

    @Override
    public void mouseEvent(MouseEvent e) {
        if(e.getEventType() == MouseEvent.MOUSE_ENTERED){
            viewWindow.focusWindow();
        }
	// TODO: edge scrolling
	// TODO: window lock
    }

    int scrollAmount = 1;

    private void moveUp() {
	moveWindow(-1 * scrollAmount, -1 * scrollAmount);
    }

    private void moveDown() {
	moveWindow(scrollAmount, scrollAmount);
    }

    private void moveLeft() {
	moveWindow(-1 * scrollAmount, scrollAmount);
    }

    private void moveRight() {
	moveWindow(scrollAmount, -scrollAmount);
    }
    
    
    @Override
    public void keyEvent(KeyEvent e) {
	if (e.getEventType() == KeyEvent.KEY_PRESSED) {
	    scrollAmount = e.isShiftDown() ? 10 : 1;

	    switch (e.getCode()) {

		case TAB:
		    viewWindow.toggleToolbars();
		    break;

		case SPACE:
		    getRenderer().centerWindow();
		    break;

		case A:
		case LEFT:
		    moveLeft();
		    break;
		case W:
		case UP:
		    moveUp();
		    break;
		case D:
		case RIGHT:
		    moveRight();
		    break;
		case S:
		case DOWN:
		    moveDown();
		    break;

		case MINUS:
		case SEMICOLON:
		case SUBTRACT:
		    zoom(-1 * scrollAmount);
		    break;
		case EQUALS:
		case ADD:
		case PLUS:
		    zoom(scrollAmount);
		    break;

		case DIGIT0:
		    break;
		case DIGIT1:
		    break;
		case DIGIT2:
		    break;
		case DIGIT3:
		    break;
		case DIGIT4:
		    break;
		case DIGIT5:
		    break;
		case DIGIT6:
		    break;
		case DIGIT7:
		    break;
		case DIGIT8:
		    break;
		case DIGIT9:
		    break;

		case F1:
		    break;
		case F2:
		    break;
		case F3:
		    break;
		case F4:
		    break;
		case F5:
		    break;
		case F6:
		    break;
		case F7:
		    break;
		case F8:
		    break;
		case F9:
		    break;
		case F10:
		    break;
		case F11:
		    break;
		case F12:
		    break;
	    }
	}
    }

    @Override
    public void disconnect() {
	System.out.println("thebob.ja2maptool.util.map.controller.viewer.MapEditorViewerController.disconnect()");
    }

}
