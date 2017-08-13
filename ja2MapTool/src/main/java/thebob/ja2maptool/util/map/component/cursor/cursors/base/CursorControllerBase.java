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
public abstract class CursorControllerBase implements ICursorController {

    protected MapCursorComponent controller = null;

    @Override
    public void setController(MapCursorComponent controller) {
        this.controller = controller;
    }

    @Override
    public void mouseEvent(MouseEvent e) {
    }

    // ----------------------------------------
    // Cursor update
    // ----------------------------------------
    public abstract void updateCursor();

    // ------------------------    
    @Override
    public MapCursor getCursor() {
        return controller.getMapCursor();
    }

    @Override
    public MapCursor getCursor(int x, int y, IndexedElement cursor) {
        return controller.getMapCursor(x, y, cursor);
    }

    @Override
    public MapCursor getCursor(double x, double y, IndexedElement cursor) {
        return controller.getMapCursor(x, y, cursor);
    }

    @Override
    public int getMouseCellX() {
        return controller.getMouseCellX();
    }

    @Override
    public int getMouseCellY() {
        return controller.getMouseCellY();
    }

    @Override
    public int getMouseCell() {
        return controller.getMouseCell();
    }

    @Override
    public double getLastCursorX() {
        return controller.getLastCursorX();
    }

    @Override
    public double getLastCursorY() {
        return controller.getLastCursorY();
    }

    @Override
    public boolean isControlDown() {
        return controller.isControlDown();
    }

    @Override
    public boolean isShiftDown() {
        return controller.isShiftDown();
    }

    @Override
    public boolean isAltDown() {
        return controller.isAltDown();
    }

    @Override
    public int getWindowScreenX() {
        return controller.getWindowScreenX();
    }

    @Override
    public int getWindowScreenY() {
        return controller.getWindowScreenY();
    }

    @Override
    public double getMouseScreenX() {
        return controller.getMouseScreenX();
    }

    @Override
    public double getMouseScreenY() {
        return controller.getMouseScreenY();
    }

    @Override
    public ICursorLayerManager getCursors() {
        return controller.getCursors();
    }

    @Override
    public ITileRendererManager getRenderer() {
        return controller.getRenderer();
    }

    @Override
    public IMapLayerManager getMap() {
        return controller.getMap();
    }

}
