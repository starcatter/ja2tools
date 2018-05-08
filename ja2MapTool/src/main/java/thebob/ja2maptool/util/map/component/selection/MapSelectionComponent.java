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
package thebob.ja2maptool.util.map.component.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SnippetPlacement;
import thebob.ja2maptool.util.map.component.base.MapComponentBase;
import thebob.ja2maptool.util.map.component.interaction.IMapInteractionComponent;
import thebob.ja2maptool.util.map.component.interaction.eventdata.MapInteractionData;
import thebob.ja2maptool.util.map.component.interaction.layer.MapInteractionLayer;
import thebob.ja2maptool.util.map.component.placement.snippets.IMapSnippetPlacementComponent;
import thebob.ja2maptool.util.map.events.MapEvent;
import thebob.ja2maptool.util.map.layers.cursor.CursorLayer;
import static thebob.ja2maptool.util.map.layers.cursor.CursorLayer.LAYER_ACTION;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;
import thebob.ja2maptool.util.map.component.interaction.target.IMapInteractionListener;

/**
 *
 * @author the_bob
 */
public class MapSelectionComponent extends MapComponentBase implements IMapSelectionComponent, IMapInteractionListener {

    protected static final IndexedElement SELECTED_TILES_CURSOR = new IndexedElement(131, 10);
    protected static final IndexedElement SELECTED_TILES_HOVER_CURSOR = new IndexedElement(131, 8);

    protected ICursorLayerManager cursorLayer;
    private final IMapInteractionComponent cells;
    private final IMapSnippetPlacementComponent placements;

    // selction start marker
    protected Integer selectionStartX = null;
    protected Integer selectionStartY = null;
    protected Integer selectionStartCell = null;

    // selction end marker
    protected Integer selectionEndX = null;
    protected Integer selectionEndY = null;
    protected Integer selectionEndCell = null;

    // TODO: fix selectiom mode change
    SelectionMode selMode = SelectionMode.CellRect;

    // selection rect (tramsformed start/end coordinates, x1/y1 is upper left corner)
    private Integer rectStartX;
    private Integer rectEndX;
    private Integer rectStartY;
    private Integer rectEndY;

    public MapSelectionComponent(ITileRendererManager renderer, IMapLayerManager map, ICursorLayerManager cursorLayer, IMapInteractionComponent cells) {
        super(renderer, map);
        this.cursorLayer = cursorLayer;
        this.cells = cells;
        placements = null;
    }

    public MapSelectionComponent(ITileRendererManager renderer, IMapLayerManager map, ICursorLayerManager cursorLayer, IMapInteractionComponent cells, IMapSnippetPlacementComponent placements) {
        super(renderer, map);
        this.cursorLayer = cursorLayer;
        this.cells = cells;
        this.placements = placements;
    }

    // --------------------------------
    // Component communication methods
    // --------------------------------
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

    @Override
    public SelectedTiles getSelection() {
        SelectedTiles selectedTiles = new SelectedTiles(getMap().getTileset().getIndex(), rectStartX, rectEndX, selectionStartCell, rectStartY, rectEndY, selectionEndCell);

        switch (selMode) {
            case CellRect:
                selectedTiles.setSelectedCells(cursorLayer.getCellNumbersForRect(rectStartX, rectStartY, rectEndX, rectEndY, CursorLayer.CursorFillMode.Full));
                break;

            case CellRectRadius:
                selectedTiles.setSelectedCells(cursorLayer.getCellNumbersForRadius(rectStartX, rectStartY, Math.abs(rectEndX - rectStartX) * 2, Math.abs(rectEndY - rectStartY) * 2, CursorLayer.CursorFillMode.Full));
                break;
        }

        return selectedTiles;
    }

    // --------------------------------
    // Place/remove selection
    // --------------------------------
    @Override
    public void placeMarker(int mouseCellX, int mouseCellY) {
        if (selectionStartCell == null) {
            selectionStartX = mouseCellX;
            selectionStartY = mouseCellY;
            selectionStartCell = cursorLayer.rowColToPos(selectionStartY, selectionStartX);

            notifyObservers(new MapEvent(MapEvent.ChangeType.SELECTION_STARTED));
        } else if (selectionEndCell == null || (mouseCellX != selectionEndX || mouseCellY != selectionEndY)) {
            selectionEndX = mouseCellX;
            selectionEndY = mouseCellY;
            selectionEndCell = cursorLayer.rowColToPos(selectionEndY, selectionEndX);
            updateSelectionGrids(selMode);

            notifyObservers(new MapEvent(MapEvent.ChangeType.SELECTION_CHANGED));
        } else if (mouseCellX == selectionEndX && mouseCellY == selectionEndY) {
            //selMode = selMode.getNext();
            //updateSelectionGrids(selMode);
        }
    }

    @Override
    public void clearSelection() {
        selectionStartX = null;
        selectionStartY = null;
        selectionStartCell = null;

        selectionEndX = null;
        selectionEndY = null;
        selectionEndCell = null;

        rectStartX = null;
        rectEndX = null;
        rectStartY = null;
        rectEndY = null;

        cursorLayer.clearLayer(LAYER_ACTION);
        cells.getLayer(this).clear();
        cells.refreshLayers();

        notifyObservers(new MapEvent(MapEvent.ChangeType.SELECTION_CLEARED));
    }

    // --------------------------------
    // Methods for updating state
    // --------------------------------
    List<SnippetPlacement> selectedPlacements = new ArrayList<SnippetPlacement>();

    // remember last selected cells. Looks like something is redrawing the selection every pixel moved instrad of every cell.
    Integer lastSelectionStartCell = null;
    Integer lastSelectionEndCell = null;

    private void updateSelectionGrids(SelectionMode mode) {
        if (    (selectionStartCell != null)
             && (selectionEndCell != null)
             && (       (!Objects.equals(lastSelectionStartCell, selectionStartCell))
                     || (!Objects.equals(lastSelectionEndCell, selectionEndCell))
                     || dragging)
                ) {

            lastSelectionStartCell = selectionStartCell;
            lastSelectionEndCell = selectionEndCell;

            rectStartX = selectionStartX < selectionEndX ? selectionStartX : selectionEndX;
            rectEndX = selectionStartX > selectionEndX ? selectionStartX : selectionEndX;
            rectStartY = selectionStartY < selectionEndY ? selectionStartY : selectionEndY;
            rectEndY = selectionStartY > selectionEndY ? selectionStartY : selectionEndY;

            refreshSelectionGridDisplay(mode);
            refreshInteractionGrid(mode);

            // placement selection
            if (placements != null) {
                if (!dragging) {
                    selectedPlacements.clear();
                    int[] cells = cursorLayer.getCellNumbersForRect(rectStartX, rectStartY, rectEndX, rectEndY, CursorLayer.CursorFillMode.Full);
                    for (int cell : cells) {
                        SnippetPlacement placement = placements.getPlacements().get(cell);
                        if (placement != null) {
                            selectedPlacements.add(placement);
                        }
                    }
                }
            }
        }
    }

    private void refreshInteractionGrid(SelectionMode mode) {
        MapInteractionLayer activeLayer = cells.getLayer(this);
        activeLayer.clear();

        switch (mode) {
            case CellRect:
                activeLayer.registerCells(cursorLayer.getCellNumbersForRect(rectStartX - 1, rectStartY - 1, rectEndX + 1, rectEndY + 1, CursorLayer.CursorFillMode.Full), null);
                break;

            case CellRectRadius:
                activeLayer.registerCells(cursorLayer.getCellNumbersForRadius(selectionStartX, selectionStartY, (rectEndX - rectStartX) * 2, (rectEndY - rectStartY) * 2, CursorLayer.CursorFillMode.Full), null);
                break;
        }
        cells.refreshLayers();
    }

    private void refreshSelectionGridDisplay(SelectionMode mode) {
        cursorLayer.clearLayer(LAYER_ACTION);

        CursorLayer.CursorFillMode fillMode = dragging ? CursorLayer.CursorFillMode.Border : CursorLayer.CursorFillMode.Full;

        switch (mode) {
            case CellRect:
                cursorLayer.placeCursorRect(LAYER_ACTION, selectionStartX, selectionStartY, selectionEndX, selectionEndY, SELECTED_TILES_CURSOR, fillMode);
                break;

            case CellRectRadius:
                cursorLayer.placeCursorCenterRect(LAYER_ACTION, selectionStartX, selectionStartY, Math.abs(selectionEndX - selectionStartX) * 2, Math.abs(selectionEndY - selectionStartY) * 2, SELECTED_TILES_CURSOR, fillMode);
                break;
        }
    }

    // --------------------------------
    // mouse interaction support (drag selection)
    // --------------------------------
    int x = 0, y = 0;

    int deltaXSum = 0;
    int deltaYSum = 0;

    boolean hovered = false;
    boolean dragging = false;

    @Override
    public boolean hoverCell(int cell, MapInteractionData data) {
        if (rectStartX == null || rectEndX == null || rectStartY == null || rectEndY == null) {
            return false;
        }

        hovered = true;

        int deltaX = 0;
        int deltaY = 0;

        if (x > rectStartX - 1 && x < rectEndX + 1 && y > rectStartY - 1 && y < rectEndY + 1) {
            deltaX = data.getMouseCellX() - x;
            deltaY = data.getMouseCellY() - y;
        }

        x = data.getMouseCellX();
        y = data.getMouseCellY();

        if (data.getButton() == MouseButton.PRIMARY) {
            if (deltaX != 0 || deltaY != 0) {
                if (!dragging) {
                    dragStart();
                }

                selectionStartX += deltaX;
                selectionStartY += deltaY;

                selectionEndX += deltaX;
                selectionEndY += deltaY;

                selectionStartCell = cursorLayer.rowColToPos(selectionStartY, selectionStartX);
                selectionEndCell = cursorLayer.rowColToPos(selectionEndY, selectionEndX);

                deltaXSum += deltaX;
                deltaYSum += deltaY;

                notifyObservers(new MapEvent(MapEvent.ChangeType.SELECTION_CHANGED));
            }
        }

        final int deltaXf = deltaX;
        final int deltaYf = deltaY;

        Platform.runLater(() -> {
            if (deltaXf != 0 || deltaYf != 0) {
                updateSelectionGrids(selMode);
            }
            if (x > rectStartX - 1 && x < rectEndX + 1 && y > rectStartY - 1 && y < rectEndY + 1) {
                cursorLayer.placeCursorRect(LAYER_ACTION, rectStartX - 1, rectStartY - 1, rectEndX + 1, rectEndY + 1, SELECTED_TILES_HOVER_CURSOR, CursorLayer.CursorFillMode.Corners);
            }
        });

        return true;
    }

    void dragStart() {
        dragging = true;
        deltaXSum = 0;
        deltaYSum = 0;
    }

    void dragEnd() {
        dragging = false;
        if (placements != null && selectedPlacements.isEmpty() == false) {
            placements.movePlacementList(selectedPlacements, deltaXSum, deltaYSum);
        }
    }

    @Override
    public void hoverOff() {
        if (hovered) {
            updateSelectionGrids(selMode);
            hovered = false;
        }
    }

    @Override
    public boolean activateCell(int cell, MapInteractionData data) {
        x = data.getMouseCellX();
        y = data.getMouseCellY();
        if (dragging) {
            dragEnd();
        }
        return true;
    }

    // --------------------------------
    // map controller methods, to be deleted?
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

    @Override
    public String toString() {
        return "MapSelectionComponent{" + "rectStartX=" + rectStartX + ", rectEndX=" + rectEndX + ", rectStartY=" + rectStartY + ", rectEndY=" + rectEndY + ", hovered=" + hovered + '}';
    }

}
