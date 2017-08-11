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
package thebob.ja2maptool.util.map.controller.cursor.base;

import java.util.Observable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.ja2maptool.util.map.MapEvent;
import static thebob.ja2maptool.util.map.MapUtils.screenXYtoCellX;
import static thebob.ja2maptool.util.map.MapUtils.screenXYtoCellY;
import thebob.ja2maptool.util.map.controller.base.MapControllerBase;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.cursor.MapCursor;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author the_bob
 */
public abstract class MapCursorControllerBase extends MapControllerBase implements IMapCursorController {

    protected ICursorLayerManager cursors;

    // ----------------------------------------
    // Raw input state
    // ----------------------------------------
    protected double lastCursorX = 0;   // cursor pos relative to canvas window
    protected double lastCursorY = 0;   // cursor pos relative to canvas window
    protected boolean controlDown = false;  // state during last input event
    protected boolean shiftDown = false;  // state during last input event
    protected boolean altDown = false;  // state during last input event

    // ----------------------------------------
    // Porcessed input state
    // ----------------------------------------
    protected int windowScreenX;    // view window distance from map edge
    protected int windowScreenY;    // view window distance from map edge

    protected double mouseScreenX;  // cursor pos relative to view window
    protected double mouseScreenY;  // cursor pos relative to view window

    protected int mouseCellX;   // map cell X under cursor
    protected int mouseCellY;   // map cell Y under cursor
    protected int mouseCell;   // map cell under cursor

    public MapCursorControllerBase(ITileRendererManager renderer, IMapLayerManager map, ICursorLayerManager cursors) {
        super(renderer, map);
        this.cursors = cursors;
        System.out.println("thebob.ja2maptool.util.map.controller.cursor.base.MapCursorControllerBase.<init>()");
    }

    // ----------------------------------------
    // Cursor creation methods
    // ----------------------------------------
    @Override
    public MapCursor getCursor() {
        return cursors.getCursor(mouseCellX, mouseCellY, null);
    }

    @Override
    public MapCursor getCursor(int x, int y, IndexedElement cursor) {
        return cursors.getCursor(x, y, cursor);
    }

    @Override
    public MapCursor getCursor(double x, double y, IndexedElement cursor) {
        return cursors.getCursor(x, y, cursor);
    }

    // ----------------------------------------
    // Persist input state
    // ----------------------------------------
    private void saveCursorEventData(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown) {
        this.lastCursorX = dx;
        this.lastCursorY = dy;

        this.controlDown = controlDown;
        this.shiftDown = shiftDown;
        this.altDown = altDown;
    }

    /**
     * Translates current mouse position to map cell position. Only returns true
     * if the current mouse cell has changed. Cell size depends on zoom level,
     * small mouse position change might not select the next cell, so no need to
     * refresh cursor state yet.
     *
     * @return returns true if the mouse cell position changed.
     */
    protected boolean processCursorEventData() {
        // translate screen position to current map window position
        windowScreenX = (2 * renderer.getWindowOffsetX()) - (2 * renderer.getWindowOffsetY());
        windowScreenY = renderer.getWindowOffsetX() + renderer.getWindowOffsetY();
        // apply current scale
        double scaledCursorX = lastCursorX / renderer.getScale();
        double scaledCursorY = lastCursorY / renderer.getScale();

        // calculate pixel position within view window
        mouseScreenX = (windowScreenX + (scaledCursorX / 10) + 2);
        mouseScreenY = (windowScreenY + (scaledCursorY / 10) + 1);

        // translate window pixel position to cell x/y
        mouseCellX = screenXYtoCellX(mouseScreenX, mouseScreenY);
        mouseCellY = screenXYtoCellY(mouseScreenX, mouseScreenY);

        int newMouseCell = cursors.rowColToPos(mouseCellY, mouseCellX);

        if (mouseCell == newMouseCell || cursors.getMapSize() < 1) {
            return false;
        }

        mouseCell = newMouseCell;

        return true;
    }

    @Override
    public String toString() {
        return "MapCursorControllerBase{" + "cursors=" + cursors + ", mouseCellX=" + mouseCellX + ", mouseCellY=" + mouseCellY + ", mouseCell=" + mouseCell + ", mouseScreenX=" + mouseScreenX + ", mouseScreenY=" + mouseScreenY + '}';
    }

    // ----------------------------------------
    // Input state update
    // ----------------------------------------
    protected void updateState(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown) {
        saveCursorEventData(dx, dy, controlDown, shiftDown, altDown);
        if (processCursorEventData()) {
            updateCursor();
        }
    }

    // ----------------------------------------
    // Cursor update
    // ----------------------------------------
    protected abstract void updateCursor();

    // ----------------------------------------
    // Input event handlers
    // ----------------------------------------
    /**
     * By default the cursor controllers will ignore all keyboard events, higher
     * level controllers will change cursors based on key state
     *
     * @param e
     */
    @Override
    public void keyEvent(KeyEvent e) {
    }

    /**
     * Default mouse movement handler.
     *
     * @param e
     */
    @Override
    public void mouseEvent(MouseEvent e) {
        if (e.getEventType() == MouseEvent.MOUSE_MOVED || e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            updateState(e.getX(), e.getY(), e.isControlDown(), e.isShiftDown(), e.isAltDown());
        }
    }

    // ----------------------------------------
    // Notification handler
    // ----------------------------------------    
    @Override
    public void update(Observable o, Object arg) {
        MapEvent message = (MapEvent) arg;

        if (message != null) {
            switch (message.getType()) {
                case MAP_LOADED:
                    if (processCursorEventData()) {
                        updateCursor();
                    }
                    break;
                case LAYER_ALTERED:

                    break;
                case MAP_ALTERED:
                    break;
                case MAP_WINDOW_MOVED:
                    if (processCursorEventData()) {
                        updateCursor();
                    }
                    break;
                case MAP_WINDOW_ZOOMED:
                    if (processCursorEventData()) {
                        updateCursor();
                    }
                    break;
                case MAP_CANVAS_CHANGED:
                    if (processCursorEventData()) {
                        updateCursor();
                    }
                    break;
                case CURSOR_MOVED:
                    break;
                case PLACEMENT_CURSOR_ADDED:
                    break;
                case PLACEMENT_CURSOR_MOVED:
                    break;
                case PLACEMENT_CURSOR_REMOVED:
                    break;
                case PLACEMENT_TOGGLE:
                    break;
                case PLACEMENT_PICK:
                    break;
                case PLACEMENT_DELETE:
                    break;
                default:
                    throw new AssertionError(message.getType().name());
            }
        }
    }

    @Override
    public int getMouseCellX() {
        return mouseCellX;
    }

    @Override
    public int getMouseCellY() {
        return mouseCellY;
    }

    @Override
    public int getMouseCell() {
        return mouseCell;
    }

    @Override
    public double getLastCursorX() {
        return lastCursorX;
    }

    @Override
    public double getLastCursorY() {
        return lastCursorY;
    }

    @Override
    public boolean isControlDown() {
        return controlDown;
    }

    @Override
    public boolean isShiftDown() {
        return shiftDown;
    }

    @Override
    public boolean isAltDown() {
        return altDown;
    }

    @Override
    public IMapCursorController transferStateFrom(IMapCursorController oldController) {
        oldController.disconnect();
        updateState(oldController.getLastCursorX(), oldController.getLastCursorY(), oldController.isControlDown(), oldController.isShiftDown(), oldController.isAltDown());
        return this;
    }

}
