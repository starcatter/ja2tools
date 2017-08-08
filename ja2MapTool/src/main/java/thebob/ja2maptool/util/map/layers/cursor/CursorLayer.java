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
package thebob.ja2maptool.util.map.layers.cursor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.scene.input.MouseButton;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import static thebob.ja2maptool.util.map.MapUtils.screenXYtoCellX;
import static thebob.ja2maptool.util.map.MapUtils.screenXYtoCellY;
import thebob.ja2maptool.util.map.layers.base.TileLayer;
import thebob.ja2maptool.util.map.layers.base.TileLayerGroup;
import thebob.ja2maptool.util.map.MapEvent;

/**
 *
 * @author the_bob
 */
public class CursorLayer extends TileLayerGroup implements ICursorLayerManager {

    List<TileLayer> layers = new ArrayList<>();

    // cursor tile numbers (index +1)
    private static final IndexedElement STD_CURSOR = new IndexedElement(131, 1);
    private static final IndexedElement SELECTION_CURSOR = new IndexedElement(131, 15);
    private static final IndexedElement PLACEMENT_CURSOR = new IndexedElement(131, 17);

    private static final IndexedElement SELECTION_START_CURSOR = new IndexedElement(131, 20);
    private static final IndexedElement SELECTION_END_CURSOR = new IndexedElement(131, 19);

    private static final IndexedElement SELECTED_TILES_CURSOR = new IndexedElement(131, 10);
    private static final IndexedElement PLACEMENT_TILES_CURSOR = new IndexedElement(131, 6);
    private static final IndexedElement VIEW_EDGE_TILES_CURSOR = new IndexedElement(131, 8);

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
	setTileset(tileset);

	cursorLayer = new IndexedElement[mapSize][];
	cursorLayerWrapper = new TileLayer(true, 0, 0, cursorLayer);
	layers.clear();
	layers.add(cursorLayerWrapper);

	placementLocation = null;
	selectionCursors = null;
	selStart = null;
	selEnd = null;

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
	//cursorLayerWrapper.setEnabled(false);
	//cursorLayer = null;

	cursors = null;
	bakeCursorLayer();
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
    double lastCursorX = 0;
    double lastCursorY = 0;
    boolean controlDown = false;
    boolean shiftDown = false;
    boolean altDown = false;

    private void saveCursorEventData(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown) {
	this.lastCursorX = dx;
	this.lastCursorY = dy;

	this.controlDown = controlDown;
	this.shiftDown = shiftDown;
	this.altDown = altDown;
    }

    @Override
    public void sendCursor(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown) {
	saveCursorEventData(dx, dy, controlDown, shiftDown, altDown);
	updateCursor();
    }

    // ----------------------------------------
    // Mouse click handler
    // ----------------------------------------
    @Override
    public void sendClick(double dx, double dy, MouseButton button, boolean controlDown, boolean shiftDown, boolean altDown) {
	if (cursors == null) {
	    return;
	}

	if (shiftDown) {
	    clearPlacementCursor();

	    if (selStart == null) {
		selStart = new MapCursor(cursors[0], SELECTION_START_CURSOR);
	    } else {
		selEnd = new MapCursor(cursors[0], SELECTION_END_CURSOR);

		selectTiles(selStart, selEnd);
	    }

	} else {
	    clearSelection();

	    if (preview != null) {
		if (button == MouseButton.PRIMARY) {
		    notifySubscribers(new MapEvent(MapEvent.ChangeType.PLACEMENT_TOGGLE));
		}
		if (button == MouseButton.SECONDARY) {
		    movePlacementCursor();
		}
	    } else {
		clearPlacementCursor();

		if (button == MouseButton.PRIMARY) {
		    notifySubscribers(new MapEvent(MapEvent.ChangeType.PLACEMENT_PICK));
		}
		if (button == MouseButton.SECONDARY) {
		    notifySubscribers(new MapEvent(MapEvent.ChangeType.PLACEMENT_DELETE));
		}

	    }
	}

	//bakeCursorLayer();
	saveCursorEventData(dx, dy, controlDown, shiftDown, altDown);
	updateCursor();
    }

    // ----------------------------------------
    // Rebuild the cursor tables, based on the cursor position relative to view window
    // ----------------------------------------
    public void updateCursor() {
	if (mapSize < 1) {
	    return;
	}
	if (cursors == null) {
	    initCursors();
	}

	double psScreenX = (2 * windowOffsetX) - (2 * windowOffsetY);
	double psScreenY = windowOffsetX + windowOffsetY;

	lastCursorX /= scale;
	lastCursorY /= scale;

	double cursorPosX = psScreenX + (lastCursorX / 10) + 2;
	double cursorPosY = psScreenY + (lastCursorY / 10) + 1;

	MapCursor cursor = getCursor(cursorPosX, cursorPosY, null);

	if (shiftDown) {
	    cursor.cursor[0] = SELECTION_CURSOR;
	} else {
	    cursor.cursor[0] = STD_CURSOR;
	}

	cursors[0] = cursor;

	updateAuxCursorDisplay();
	bakeCursorLayer();
    }

    private void movePlacementCursor() {
	if (placementLocation == null) {
	    notifySubscribers(new MapEvent(MapEvent.ChangeType.PLACEMENT_CURSOR_ADDED));
	}
	placementLocation = new MapCursor(cursors[0], PLACEMENT_CURSOR);
	notifySubscribers(new MapEvent(MapEvent.ChangeType.PLACEMENT_CURSOR_MOVED));

    }

    private void clearSelection() {
	selStart = null;
	selEnd = null;
	selectionCursors = null;
    }

    private void clearPlacementCursor() {
	if (placementLocation != null) {
	    notifySubscribers(new MapEvent(MapEvent.ChangeType.PLACEMENT_CURSOR_REMOVED));
	}
	placementLocation = null;
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

	int i = 0;
	for (int x = startX; x <= endX; x++) {
	    for (int y = startY; y <= endY; y++) {
		selectionCursors[i] = getCursor(x, y, SELECTED_TILES_CURSOR);
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
	if (preview != null) {
	    setCursorSize(preview.getWidth(), preview.getHeight());
	} else {
	    resetCursorSize();
	}
	bakeCursorLayer();
    }

    // ----------------------------------------
    // Stuff for drawing fancy cursors
    // ----------------------------------------
    private void updateAuxCursorDisplay() {
	if (cursors == null) {
	    return;
	}
	MapCursor cursor = cursors[0];

	double scaledCanvasX = canvasX / scale;
	double scaledCanvasY = canvasY / scale;

	double psScreenX = (2 * windowOffsetX) - (2 * windowOffsetY);
	double psScreenY = windowOffsetX + windowOffsetY;

	if (preview != null) {

	    MapCursor mainCursor = cursors[0];
	    cursors = new MapCursor[1 + 4 + (preview.getWidth() * preview.getHeight())];
	    cursors[0] = mainCursor;

	    int startX = cursor.getCellX() - cursorWidth / 2;
	    int startY = cursor.getCellY() - cursorHeight / 2;

	    // cursors show placement size
	    int i = 5;
	    for (int x = startX; x < startX + cursorWidth; x++) {
		for (int y = startY; y < startY + cursorHeight; y++) {
		    // cell in the cursor layer
		    //int targetCell = rowColToPos(y, x);

		    cursors[i] = getCursor(x, y, PLACEMENT_TILES_CURSOR);
		    i++;
		}
	    }
	}

	if (cursorHeight != null && cursorWidth != null) {
	    IndexedElement ctyp = new IndexedElement(131, 7);	// aux cursor type 	
	    // cursors show placement size
	    int startX = (cursor.getCellX() - cursorWidth / 2) - 1;
	    int startY = (cursor.getCellY() - cursorHeight / 2) - 1;

	    int endX = startX + cursorWidth + 1;
	    int endY = startY + cursorHeight + 1;

	    cursors[1] = getCursor(startX, startY, VIEW_EDGE_TILES_CURSOR);
	    cursors[2] = getCursor(endX, startY, VIEW_EDGE_TILES_CURSOR);
	    cursors[3] = getCursor(endX, endY, VIEW_EDGE_TILES_CURSOR);
	    cursors[4] = getCursor(startX, endY, VIEW_EDGE_TILES_CURSOR);
	} else {
	    // cursors at view window corners
	    cursors[1] = getCursor(psScreenX + 4, psScreenY + 2, VIEW_EDGE_TILES_CURSOR);
	    cursors[2] = getCursor(psScreenX + 4, psScreenY + (scaledCanvasY / 10) - 1, VIEW_EDGE_TILES_CURSOR);
	    cursors[3] = getCursor(psScreenX + (scaledCanvasX / 10) - 1, psScreenY + (scaledCanvasY / 10) - 1, VIEW_EDGE_TILES_CURSOR);
	    cursors[4] = getCursor(psScreenX + (scaledCanvasX / 10) - 1, psScreenY + 2, VIEW_EDGE_TILES_CURSOR);
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

	if (cursors != null) {
	    for (MapCursor cursor : cursors) {
		if (cursor != null && isProperMapCell(cursor.getCell())) {
		    cursorLayer[cursor.getCell()] = cursor.getCursor();
		}
	    }
	    // place the main cursor again in case it got obscured by some fancy placement cursor
	    if (preview == null && isProperMapCell(cursors[0].getCell())) {
		cursorLayer[cursors[0].getCell()] = cursors[0].getCursor();
	    }
	}

	notifySubscribers(new MapEvent(MapEvent.ChangeType.LAYER_ALTERED));
    }

    private boolean isProperMapCell(int cell) {
	return (cell < mapSize && cell >= 0);
    }

    ;

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

    @Override
    public MapCursor getMainCursor() {
	return cursors != null ? cursors[0] : null;
    }

    @Override
    public String toString() {
	return "CursorLayer{" + super.toString() + '}';
    }

}
