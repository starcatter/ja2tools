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
package thebob.ja2maptool.util.map.component.cursor.base;

import java.util.Observable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import thebob.assetloader.map.core.components.IndexedElement;
import static thebob.ja2maptool.util.map.MapUtils.screenXYtoCellX;
import static thebob.ja2maptool.util.map.MapUtils.screenXYtoCellY;
import thebob.ja2maptool.util.map.component.interaction.IMapInteractionComponent;
import thebob.ja2maptool.util.map.component.interaction.eventdata.MapInteractionData;
import thebob.ja2maptool.util.map.component.base.MapComponentBase;
import thebob.ja2maptool.util.map.events.MapEvent;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.cursor.MapCursor;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author the_bob
 */
public abstract class MapCursorComponentBase extends MapComponentBase implements IMapCursorComponent {

    private ICursorLayerManager cursors;
    private IMapInteractionComponent cells;

    // ----------------------------------------
    // Raw input state
    // ----------------------------------------
    private double lastCursorX = 0;   // cursor pos relative to canvas window
    private double lastCursorY = 0;   // cursor pos relative to canvas window
    private boolean controlDown = false;  // state during last input event
    private boolean shiftDown = false;  // state during last input event
    private boolean altDown = false;  // state during last input event
    private MouseButton button = MouseButton.NONE;  // state during last input event

    // ----------------------------------------
    // Porcessed input state
    // ----------------------------------------
    private int windowScreenX;    // view window distance from map edge
    private int windowScreenY;    // view window distance from map edge

    private double mouseScreenX;  // cursor pos relative to view window
    private double mouseScreenY;  // cursor pos relative to view window

    private int mouseCellX;   // map cell X under cursor
    private int mouseCellY;   // map cell Y under cursor
    private int mouseCell;   // map cell under cursor

    public MapCursorComponentBase(ITileRendererManager renderer, IMapLayerManager map, ICursorLayerManager cursors, IMapInteractionComponent cells) {
        super(renderer, map);
        this.cursors = cursors;
        this.cells = cells;
        System.out.println("thebob.ja2maptool.util.map.controller.cursor.base.MapCursorControllerBase.<init>()");
    }

    // ----------------------------------------
    // Cursor creation methods
    // ----------------------------------------
    @Override
    public MapCursor getMapCursor() {
        return getCursors().getCursor(getMouseCellX(), getMouseCellY(), null);
    }

    @Override
    public MapCursor getMapCursor(int x, int y, IndexedElement cursor) {
        return getCursors().getCursor(x, y, cursor);
    }

    @Override
    public MapCursor getMapCursor(double x, double y, IndexedElement cursor) {
        return getCursors().getCursor(x, y, cursor);
    }

    // ----------------------------------------
    // Persist input state
    // ----------------------------------------
    private void saveCursorEventData(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown, MouseButton button) {
        this.lastCursorX = dx;
        this.lastCursorY = dy;

        this.controlDown = controlDown;
        this.shiftDown = shiftDown;
        this.altDown = altDown;
        this.button = button;
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
        windowScreenX = (2 * getRenderer().getWindowOffsetX()) - (2 * getRenderer().getWindowOffsetY());
        windowScreenY = getRenderer().getWindowOffsetX() + getRenderer().getWindowOffsetY();
        // apply current scale
        double scaledCursorX = getLastCursorX() / getRenderer().getScale();
        double scaledCursorY = getLastCursorY() / getRenderer().getScale();

        // calculate pixel position within view window
        mouseScreenX = (getWindowScreenX() + (scaledCursorX / 10) + 2);
        mouseScreenY = (getWindowScreenY() + (scaledCursorY / 10) + 1);

        // translate window pixel position to cell x/y
        mouseCellX = screenXYtoCellX(getMouseScreenX(), getMouseScreenY());
        mouseCellY = screenXYtoCellY(getMouseScreenX(), getMouseScreenY());

        int newMouseCell = getCursors().rowColToPos(getMouseCellY(), getMouseCellX());

        if (getMouseCell() == newMouseCell || getCursors().getMapSize() < 1) {
            return false;
        }

        mouseCell = newMouseCell;

        return true;
    }

    @Override
    public String toString() {
        return "MapCursorControllerBase{" + "cursors=" + getCursors() + ", mouseCellX=" + getMouseCellX() + ", mouseCellY=" + getMouseCellY() + ", mouseCell=" + getMouseCell() + ", mouseScreenX=" + getMouseScreenX() + ", mouseScreenY=" + getMouseScreenY() + '}';
    }

    // ----------------------------------------
    // Input state update
    // ----------------------------------------
    protected void updateState(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown, MouseButton button) {
        saveCursorEventData(dx, dy, controlDown, shiftDown, altDown, button);
        if (processCursorEventData()) {
            cursors.setBatchMode(true);
            updateCursor();
            cells.hoverCell(getMouseCell(), new MapInteractionData(getMouseCellX(), getMouseCellY(), getMouseCell(), isShiftDown(), isControlDown(), isAltDown(), getButton()));
            cursors.setBatchMode(false);
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
            updateState(e.getX(), e.getY(), e.isControlDown(), e.isShiftDown(), e.isAltDown(), e.getButton());
        } else if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (cells.activateCell(getMouseCell(), new MapInteractionData(getMouseCellX(), getMouseCellY(), getMouseCell(), e))) {
                e.consume();
            }
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

    /**
     * @return the windowScreenX
     */
    public int getWindowScreenX() {
        return windowScreenX;
    }

    /**
     * @return the windowScreenY
     */
    public int getWindowScreenY() {
        return windowScreenY;
    }

    /**
     * @return the mouseScreenX
     */
    public double getMouseScreenX() {
        return mouseScreenX;
    }

    /**
     * @return the mouseScreenY
     */
    public double getMouseScreenY() {
        return mouseScreenY;
    }

    public MouseButton getButton() {
        return button;
    }

    /**
     * @return the cursors
     */
    public ICursorLayerManager getCursors() {
        return cursors;
    }

}
