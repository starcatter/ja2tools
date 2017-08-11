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
import javafx.scene.input.MouseEvent;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.map.controller.base.MapControllerBase;
import thebob.ja2maptool.util.map.layers.cursor.CursorLayer;
import static thebob.ja2maptool.util.map.layers.cursor.CursorLayer.LAYER_ACTION;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author the_bob
 */
public class MapSelectionController extends MapControllerBase implements IMapSelectionController {

    protected static final IndexedElement SELECTED_TILES_CURSOR = new IndexedElement(131, 10);

    protected ICursorLayerManager cursorLayer;

    protected Integer selectionStartX = null;
    protected Integer selectionStartY = null;
    protected Integer selectionStartCell = null;

    protected Integer selectionEndX = null;
    protected Integer selectionEndY = null;
    protected Integer selectionEndCell = null;

    SelectionMode selMode = SelectionMode.CellRect;

    public MapSelectionController(ITileRendererManager renderer, IMapLayerManager map, ICursorLayerManager cursorLayer) {
        super(renderer, map);
        this.cursorLayer = cursorLayer;
    }

    @Override
    public Integer getSelectionStart() {
        return selectionStartCell;
    }

    @Override
    public Integer getSelectionEnd() {
        return selectionEndCell;
    }

    @Override
    public boolean hasSelection() {
        return selectionStartCell != null && selectionEndCell != null;
    }

    /**
     *  TODO: fire notification event
     */
    @Override
    public void placeMarker(int mouseCellX, int mouseCellY) {
        if (selectionStartCell == null) {
            selectionStartX = mouseCellX;
            selectionStartY = mouseCellY;
            selectionStartCell = cursorLayer.rowColToPos(selectionStartY, selectionStartX);
        } else if (selectionEndCell == null || (mouseCellX != selectionEndX || mouseCellY != selectionEndY)) {
            selectionEndX = mouseCellX;
            selectionEndY = mouseCellY;
            selectionEndCell = cursorLayer.rowColToPos(selectionEndY, selectionEndX);
            updateSelectionGrids(selMode);
        } else if (selectionEndCell != null && mouseCellX == selectionEndX && mouseCellY == selectionEndY) {
            //selMode = selMode.getNext();
            //updateSelectionGrids(selMode);
        }
    }

    @Override
    public SelectedTiles getSelection() {
        int startX = selectionStartX < selectionEndX ? selectionStartX : selectionEndX;
        int endX = selectionStartX > selectionEndX ? selectionStartX : selectionEndX;
        int startY = selectionStartY < selectionEndY ? selectionStartY : selectionEndY;
        int endY = selectionStartY > selectionEndY ? selectionStartY : selectionEndY;

        SelectedTiles selectedTiles = new SelectedTiles(map.getTileset().getIndex(), startX, endX, selectionStartCell, startY, endY, selectionEndCell);

        switch (selMode) {
            case CellRect:
                selectedTiles.setSelectedCells(cursorLayer.getCellNumbersForRect(LAYER_ACTION, startX, startY, endX, endY, CursorLayer.CursorFillMode.Full));
                break;

            case CellRectRadius:
                selectedTiles.setSelectedCells(cursorLayer.getCellNumbersForRadius(LAYER_ACTION, startX, startY, Math.abs(endX - startX) * 2, Math.abs(endY - startY) * 2, CursorLayer.CursorFillMode.Full));
                break;
        }

        return selectedTiles;
    }

    @Override
    public void clearSelection() {
        selectionStartX = null;
        selectionStartY = null;
        selectionStartCell = null;

        selectionEndX = null;
        selectionEndY = null;
        selectionEndCell = null;
        
        cursorLayer.clearLayer(LAYER_ACTION);
    }

    // --------------------------------
    private void updateSelectionGrids(SelectionMode mode) {
        if (selectionStartCell != null && selectionEndCell != null) {
            cursorLayer.clearLayer(LAYER_ACTION);

            switch (mode) {
                case CellRect:
                    cursorLayer.placeCursorRect(LAYER_ACTION, selectionStartX, selectionStartY, selectionEndX, selectionEndY, SELECTED_TILES_CURSOR, CursorLayer.CursorFillMode.Full);
                    break;

                case CellRectRadius:
                    cursorLayer.placeCursorCenterRect(LAYER_ACTION, selectionStartX, selectionStartY, Math.abs(selectionEndX - selectionStartX) * 2, Math.abs(selectionEndY - selectionStartY) * 2, SELECTED_TILES_CURSOR, CursorLayer.CursorFillMode.Full);
                    break;
            }
        }
    }

    // --------------------------------
    @Override
    public void mouseEvent(MouseEvent e) {
        // nothing to do?
    }

    @Override
    public void keyEvent(KeyEvent e) {
        // nothing to do?
    }

    @Override
    public void disconnect() {
        // nothing to do?
    }

    @Override
    public void update(Observable o, Object arg) {
        // nothing to do?
    }

    // ---------------------------
    public enum SelectionMode {
        CellRect,
        // ScreenRect, TODO
        CellRectRadius;

        // http://siliconsparrow.com/how-to-cycle-through-the-values-of-an-enum-in-java/
        public SelectionMode getNext() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

}
