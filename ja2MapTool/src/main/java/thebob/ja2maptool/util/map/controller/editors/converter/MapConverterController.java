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
package thebob.ja2maptool.util.map.controller.editors.converter;

import java.util.Observable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import thebob.ja2maptool.scopes.map.ConvertMapScope;
import thebob.ja2maptool.util.map.controller.base.MapControllerBase;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author the_bob
 */
public class MapConverterController extends MapControllerBase implements IMapConverterController {

    protected ICursorLayerManager cursors = null;
    ConvertMapScope scope;

    public MapConverterController(ITileRendererManager renderer, IMapLayerManager map, ConvertMapScope converter) {
	super(renderer, map);

	scope = converter;
    }

    @Override
    public void mouseEvent(MouseEvent e) {
	if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
//	    cursors.sendClick(e.getX(), e.getY(), e.getButton(), e.isControlDown(), e.isShiftDown(), e.isAltDown());
	} else if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
//	    cursors.sendCursor(e.getX(), e.getY(), e.isControlDown(), e.isShiftDown(), e.isAltDown());
	}
    }

    @Override
    public void keyEvent(KeyEvent e) {

    }

    @Override
    public void update(Observable o, Object arg) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void disconnect() {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
