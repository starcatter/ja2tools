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
package thebob.ja2maptool.util.map.component.cursor.cursors.base;

import javafx.scene.input.MouseEvent;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.ja2maptool.util.map.component.cursor.MapCursorComponent;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.cursor.MapCursor;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author the_bob
 */
public interface ICursorController {

    public void updateCursor();

    // ------------------------
    MapCursor getCursor();

    MapCursor getCursor(int x, int y, IndexedElement cursor);

    MapCursor getCursor(double x, double y, IndexedElement cursor);

    ICursorLayerManager getCursors();

    double getLastCursorX();

    double getLastCursorY();

    IMapLayerManager getMap();

    int getMouseCell();

    int getMouseCellX();

    int getMouseCellY();

    double getMouseScreenX();

    double getMouseScreenY();

    ITileRendererManager getRenderer();

    int getWindowScreenX();

    int getWindowScreenY();

    boolean isAltDown();

    boolean isControlDown();

    boolean isShiftDown();

    void mouseEvent(MouseEvent e);

    void setController(MapCursorComponent controller);

}
