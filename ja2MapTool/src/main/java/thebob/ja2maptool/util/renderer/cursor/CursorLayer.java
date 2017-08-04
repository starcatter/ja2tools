/*
 * Copyright (C) 2017 the_bob.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package thebob.ja2maptool.util.renderer.cursor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import static thebob.ja2maptool.util.renderer.MapUtils.screenXYtoCellX;
import static thebob.ja2maptool.util.renderer.MapUtils.screenXYtoCellY;
import thebob.ja2maptool.util.renderer.base.TileLayer;
import thebob.ja2maptool.util.renderer.base.TileLayerGroup;

/**
 *
 * @author the_bob
 */
public class CursorLayer extends TileLayerGroup implements ICursorLayerManager {

    List<TileLayer> layers = new ArrayList<>();

    private IndexedElement[][] cursorLayer = null;
    private TileLayer cursorLayerWrapper = null;

    private Integer cursorWidth = null;
    private Integer cursorHeight = null;
    private double scale;
    private int windowOffsetY;
    private int windowOffsetX;

    MapCursor[] cursors = null;
    MapCursor[] selectionCursors = null;

    MapCursor selStart = null;
    MapCursor selEnd = null;
    MapCursor placementLocation = null;

    SelectedTiles preview = null;
    private int canvasX;
    private int canvasY;

    public MapCursor getCursor(int x, int y, IndexedElement cursor) {
	return new MapCursor(this, x, y, cursor);
    }

    public MapCursor getCursor(double x, double y, IndexedElement cursor) {
	return new MapCursor(this, screenXYtoCellX(x, y), screenXYtoCellY(x, y), cursor);
    }

    /**
     * Initialize the cursor layer group, this is intended to be called once during renderer setup, use other init/deinit methods to reset the state
     *
     * @param mapRows
     * @param mapCols
     * @param tileset
     */
    @Override
    public void init(int mapRows, int mapCols, Tileset tileset) {
	setLayerSize(mapCols, mapRows);
	cursorLayer = new IndexedElement[mapSize][];
	cursorLayerWrapper = new TileLayer(true, 0, 0, cursorLayer);
	layers.add(cursorLayerWrapper);

	System.out.println("thebob.ja2maptool.util.renderer.cursor.CursorLayer.init()");
	
	initCursorLayer();
    }

    /**
     * Show the cursor layer in the renderer and start a bunch of cursors
     */
    void initCursors() {
	cursorLayerWrapper.setEnabled(true);
	cursors = new MapCursor[5];
    }

    /**
     * Reset cursor layer array to empty elements
     */
    protected void initCursorLayer() {
	for (int i = 0; i < mapSize; i++) {
	    cursorLayer[i] = new IndexedElement[]{};
	}
	cursorLayerWrapper.setTiles(cursorLayer);
    }

    /**
     * Hides the layer and resets the cursor state
     */
    @Override
    public void hideCursor() {
	cursorLayerWrapper.setEnabled(false);
	//cursorLayer = null;

	cursors = null;
	selectionCursors = null;	
	placementLocation = null;
	selStart = null;
	selEnd = null;
    }

    /**
     * *
     * Sets cursor size, cursor being the center of the square
     *
     * @param x
     * @param y
     */
    @Override
    public void setCursorSize(int x, int y) {
	cursorWidth = x;
	cursorHeight = y;
    }

    @Override
    public void resetCursorSize() {
	cursorWidth = null;
	cursorHeight = null;
    }

    // ----------------------------------------
    // Mouse movement handler
    // ----------------------------------------
    @Override
    public void sendCursor(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown) {
	if (cursors == null) {
	    initCursors();
	}

	double psScreenX = (2 * windowOffsetX) - (2 * windowOffsetY);
	double psScreenY = windowOffsetX + windowOffsetY;

	dx /= scale;
	dy /= scale;

	double cursorPosX = psScreenX + (dx / 10) + 2;
	double cursorPosY = psScreenY + (dy / 10) + 1;

	MapCursor cursor = getCursor(cursorPosX, cursorPosY, null);
	cursors[0] = cursor;

	updateAuxCursorDisplay();
	bakeCursorLayer();
    }

    // ----------------------------------------
    // Mouse click handler
    // ----------------------------------------
    @Override
    public void sendClick(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown) {
	if (cursors == null) {
	    return;
	}

	if (shiftDown) {
	    placementLocation = null;

	    if (selStart == null) {
		selStart = new MapCursor(cursors[0], new IndexedElement(131, 19));
	    } else {
		selEnd = new MapCursor(cursors[0], new IndexedElement(131, 19));

		selectTiles(selStart, selEnd);
	    }
	} else {
	    selStart = null;
	    selEnd = null;
	    selectionCursors = null;

	    if (preview != null) {
		placementLocation = new MapCursor(cursors[0], new IndexedElement(131, 16));
	    } else {
		placementLocation = null;
	    }
	}

	bakeCursorLayer();
    }

    // ----------------------------------------
    // Selection handlers
    // ----------------------------------------
    private void selectTiles(MapCursor selStart, MapCursor selEnd) {

	int startX = selStart.getCellX() < selEnd.getCellX() ? selStart.getCellX() : selEnd.getCellX();
	int endX = selStart.getCellX() > selEnd.getCellX() ? selStart.getCellX() : selEnd.getCellX();
	int startY = selStart.getCellY() < selEnd.getCellY() ? selStart.getCellY() : selEnd.getCellY();
	int endY = selStart.getCellY() > selEnd.getCellY() ? selStart.getCellY() : selEnd.getCellY();

	int size = (endX - startX + 1) * (endY - startY + 1);

	selectionCursors = new MapCursor[size];

	IndexedElement ctyp = new IndexedElement(131, 6);	// aux cursor type 	

	int i = 0;
	for (int x = startX; x <= endX; x++) {
	    for (int y = startY; y <= endY; y++) {
		selectionCursors[i] = getCursor(x, y, ctyp);
		i++;
	    }
	}
    }

    public SelectedTiles getSelection() {
	if (selectionCursors == null || selectionCursors.length == 0 || selStart == null) {
	    return null;
	}

	MapCursor start = selectionCursors[0];
	MapCursor end = selectionCursors[selectionCursors.length - 1];
	
	int startX = selStart.getCellX() < selEnd.getCellX() ? selStart.getCellX() : selEnd.getCellX();
	int endX = selStart.getCellX() > selEnd.getCellX() ? selStart.getCellX() : selEnd.getCellX();
	int startY = selStart.getCellY() < selEnd.getCellY() ? selStart.getCellY() : selEnd.getCellY();
	int endY = selStart.getCellY() > selEnd.getCellY() ? selStart.getCellY() : selEnd.getCellY();

	SelectedTiles selectedTiles = new SelectedTiles(tileset.getIndex(), startX, endX, start.getCell(), startY, endY, end.getCell());
	
	// save the cell numbers the selection cursors were over, later we'll retrieve the contents of these cells from the map layer
	int[] selectedCells = new int[selectionCursors.length];
	for (int i = 0; i < selectionCursors.length; i++) {
	    selectedCells[i] = selectionCursors[i].cell;
	}
	
	selectedTiles.setSelectedCells(selectedCells);

	return selectedTiles;
    }

    public void setPlacementPreview(SelectedTiles selection) {
	preview = selection;
    }
    
    // ----------------------------------------
    // Stuff for drawing fancy cursors
    // ----------------------------------------
    @Override
    public void updateAuxCursorDisplay() {
	if (cursors == null) {
	    return;
	}
	MapCursor cursor = cursors[0];

	double scaledCanvasX = canvasX / scale;
	double scaledCanvasY = canvasY / scale;

	double psScreenX = (2 * windowOffsetX) - (2 * windowOffsetY);
	double psScreenY = windowOffsetX + windowOffsetY;

	if (cursorHeight != null && cursorWidth != null) {
	    IndexedElement ctyp = new IndexedElement(131, 7);	// aux cursor type 	
	    // cursors show placement size
	    cursors[1] = getCursor(cursor.getCellX() - cursorWidth / 2, cursor.getCellY() - cursorHeight / 2, ctyp);
	    cursors[2] = getCursor(cursor.getCellX() + cursorWidth / 2, cursor.getCellY() - cursorHeight / 2, ctyp);
	    cursors[3] = getCursor(cursor.getCellX() + cursorWidth / 2, cursor.getCellY() + cursorHeight / 2, ctyp);
	    cursors[4] = getCursor(cursor.getCellX() - cursorWidth / 2, cursor.getCellY() + cursorHeight / 2, ctyp);
	} else {
	    IndexedElement ctyp = new IndexedElement(131, 8);	// aux cursor type 	
	    // cursors at view window corners
	    cursors[1] = getCursor(psScreenX + 4, psScreenY + 2, ctyp);
	    cursors[2] = getCursor(psScreenX + 4, psScreenY + (scaledCanvasY / 10) - 1, ctyp);
	    cursors[3] = getCursor(psScreenX + (scaledCanvasX / 10) - 1, psScreenY + (scaledCanvasY / 10) - 1, ctyp);
	    cursors[4] = getCursor(psScreenX + (scaledCanvasX / 10) - 1, psScreenY + 2, ctyp);
	}
    }

    // ----------------------------------------
    // Cursor layer "renderer"
    // ----------------------------------------
    void bakeCursorLayer() {
	initCursorLayer();

	if (selectionCursors != null) {
	    for (MapCursor cursor : selectionCursors) {
		cursorLayer[cursor.getCell()] = cursor.getCursor();
	    }
	}

	if (selStart != null) {
	    cursorLayer[selStart.getCell()] = selStart.getCursor();
	}

	if (selEnd != null) {
	    cursorLayer[selEnd.getCell()] = selEnd.getCursor();
	}

	// TODO: make a proper preview layer
	// place the previewed selection in the cursor layer
	if (preview != null && placementLocation != null && cursorWidth != null && cursorHeight != null) {

	    int startX = placementLocation.getCellX() - cursorWidth / 2;
	    int startY = placementLocation.getCellY() - cursorHeight / 2;

	    int i = 0;
	    for (int x = startX; x < startX + cursorWidth; x++) {
		for (int y = startY; y < startY + cursorHeight; y++) {
		    // cell in the cursor layer
		    int targetCell = rowColToPos(y, x);

		    // count how many tiles are to be copied over
		    int layerCount = 0;
		    for (int n = 0; n < preview.getLayers().length; n++) {
			if (preview.getLayers()[n] != null) {
			    layerCount += preview.getLayers()[n][i].length;
			}
		    }

		    cursorLayer[targetCell] = new IndexedElement[layerCount];

		    // place the tiles in the cursor layer
		    int placementCount = 0;
		    for (int n = 0; n < preview.getLayers().length; n++) {
			if (preview.getLayers()[n] != null) {
			    for (IndexedElement tile : preview.getLayers()[n][i]) {
				cursorLayer[targetCell][placementCount++] = tile;
			    }
			}
		    }

		    i++;
		}
	    }

	}

	if (cursors != null) {
	    for (MapCursor cursor : cursors) {
		cursorLayer[cursor.getCell()] = cursor.getCursor();
	    }
	}

	// cursorLayerWrapper.setTiles(cursorLayer);
    }

    // ----------------------------------------
    // Methods to keep track of renderer's view window
    // ----------------------------------------
    @Override
    public void setWindow(int windowOffsetX, int windowOffsetY, double scale) {
	this.windowOffsetX = windowOffsetX;
	this.windowOffsetY = windowOffsetY;
	this.scale = scale;
    }

    @Override
    public void setCanvasSize(int canvasX, int canvasY) {
	this.canvasX = canvasX;
	this.canvasY = canvasY;
    }

    @Override
    public Iterator<TileLayer> iterator() {
	return layers.iterator();
    }

    @Override
    public MapCursor getPlacementCursor() {
	return placementLocation;
    }
}
