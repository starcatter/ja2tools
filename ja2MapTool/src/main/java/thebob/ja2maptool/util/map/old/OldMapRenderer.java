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
package thebob.ja2maptool.util.map.old;

import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.tileset.Tile;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.model.TileCategoryMapping;
import thebob.ja2maptool.model.TileMapping;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import static thebob.ja2maptool.util.map.MapUtils.screenXYtoCellX;
import static thebob.ja2maptool.util.map.MapUtils.screenXYtoCellY;

public class OldMapRenderer {

    // tile spacing
    final int xSpacing = 40;
    final int ySpacing = 20;

    // loaded map data
    private int mapCols = -1;
    private int mapRows = -1;
    private int mapSize = -1;
    private int tilesetId = -1;

    // window position
    int windowOffsetX = 0;
    int windowOffsetY = 0;

    // display mode
    boolean overheadMap = false;

    // map layer info
    private IndexedElement[][][] layers;

    private IndexedElement[][] cursorLayer = null;

    // current tileset
    Tileset tileset = null;

    // bound canvas
    Canvas canvas = null;
    GraphicsContext canvasGraphicsContext = null;

    int canvasX = 1280;
    int canvasY = 800;

    public void moveWindow(int x, int y) {
	windowOffsetX += x;
	windowOffsetY += y;

	if (canvas != null && canvasGraphicsContext != null && tileset != null) {
	    renderMap();
	}
    }

    public void setCanvas(Canvas canvas) {
	this.canvas = canvas;
	canvasGraphicsContext = canvas.getGraphicsContext2D();

	canvasX = (int) canvas.getWidth();
	canvasY = (int) canvas.getHeight();
    }

    public void setTileset(Tileset tileset) {
	this.tileset = tileset;
    }

    public void loadMap(MapData map) {;
	mapCols = map.getSettings().iColSize;
	mapRows = map.getSettings().iRowSize;
	mapSize = mapCols * mapRows;

	layers = new IndexedElement[6][][];

	layers[0] = map.getLayers().landLayer;
	layers[1] = map.getLayers().objectLayer;
	layers[2] = map.getLayers().structLayer;
	layers[3] = map.getLayers().shadowLayer;
	layers[4] = map.getLayers().roofLayer;
	layers[5] = map.getLayers().onRoofLayer;

	// set view window in map center
	windowOffsetX = mapCols / 2 - (canvasX / xSpacing) / 2;
	windowOffsetY = mapRows / 2 - (canvasY / ySpacing) / 2;

	// update display
	moveWindow(0, 0);
    }

    private IndexedElement[][] remapLayer(IndexedElement[][] layerType, Map<Integer, TileCategoryMapping> mappingList) {
	IndexedElement[][] newLayer = new IndexedElement[layerType.length][];

	for (int i = 0; i < layerType.length; i++) {
	    IndexedElement[] layers = layerType[i];
	    newLayer[i] = new IndexedElement[layers.length];

	    for (int j = 0; j < layers.length; j++) {
		IndexedElement tile = layers[j];

		ObservableList<TileMapping> mappingType = mappingList.get(tile.type).getMappings();
		if (mappingType.size() >= tile.index) {
		    TileMapping mapping = mappingType.get(tile.index - 1);
		    newLayer[i][j] = new IndexedElement(mapping.getTargetType(), mapping.getTargetIndex() + 1);
		} else {
		    newLayer[i][j] = tile;
		}

	    }
	}
	return newLayer;
    }

    public void setWindowOffsetX(int windowOffsetX) {
	this.windowOffsetX = windowOffsetX;
    }

    public void setWindowOffsetY(int windowOffsetY) {
	this.windowOffsetY = windowOffsetY;
    }

    public int getWindowOffsetX() {
	return windowOffsetX;
    }

    public int getWindowOffsetY() {
	return windowOffsetY;
    }

    // ==============================
    // Rendering methods
    double scale = 1.0d;

    public double getScale() {
	return scale;
    }

    public void setScale(double scale) {
	this.scale = scale;
    }

    protected void displayCellLayers(IndexedElement[] layers, int canvasX, int canvasY, int drawOffsetX, int drawOffsetY) {
	for (IndexedElement layer : layers) {
	    Tile tile = tileset.getTile(layer);

	    if (tile != null) {
		Image tileImage = tile.getImage();
		int offsetX = tile.getOffsetX();
		int offxetY = tile.getOffsetY();

		canvasGraphicsContext.drawImage(tileImage, (canvasX + offsetX + drawOffsetX) * scale, (canvasY + offxetY + drawOffsetY) * scale, tile.getWidth() * scale, tile.getHeight() * scale);
	    }
	}
    }

    public int mapRowColToPos(int r, int c) {
	return ((r) * mapCols + (c));
    }

    class LayerSettings {

	boolean display;
	int xOffset;
	int yPffset;

	public LayerSettings(boolean display, int xOffset, int yPffset) {
	    this.display = display;
	    this.xOffset = xOffset;
	    this.yPffset = yPffset;
	}
    }

    LayerSettings[] layerSettings = new LayerSettings[]{
	new LayerSettings(true, 0, 0),
	new LayerSettings(true, 0, 0),
	new LayerSettings(true, 0, 0),
	new LayerSettings(true, 0, 0),
	new LayerSettings(false, 0, -50),
	new LayerSettings(false, 0, -50)
    };

    protected void renderMap() {
	canvasGraphicsContext.clearRect(0, 0, canvasX, canvasY);

	int cell = 0;

	int displayX = 0;
	int displayY = 0;

	int windowStartOffsetX = windowOffsetX;
	int windowStartOffsetY = windowOffsetY;

	int canvasStartOffsetX = -1 * xSpacing;
	int canvasStartOffsetY = -1 * ySpacing;

	boolean bXOddFlag = false;

	do {
	    displayX = canvasStartOffsetX;
	    displayY = canvasStartOffsetY;

	    if (bXOddFlag) {
		displayX += xSpacing / 2;
	    }

	    int mapPosX = windowStartOffsetX;
	    int mapPosY = windowStartOffsetY;

	    do {

		cell = mapRowColToPos(mapPosY, mapPosX);
		if (cell >= 0 && cell < mapSize) {
		    for (int i = 0; i < layers.length; i++) {
			if (layerSettings[i].display) {
			    displayCellLayers(layers[i][cell], displayX, displayY, layerSettings[i].xOffset, layerSettings[i].yPffset);
			}
		    }

		    if (cursorLayer != null) {
			displayCellLayers(cursorLayer[cell], displayX, displayY, 0, 0);
		    }
		}
		mapPosX++;
		mapPosY--;

		displayX += xSpacing;

	    } while (displayX * scale < canvasX);

	    if (bXOddFlag) {
		windowStartOffsetY++;
	    } else {
		windowStartOffsetX++;
	    }

	    bXOddFlag = !bXOddFlag;
	    canvasStartOffsetY += ySpacing / 2;

	} while (displayY * scale < canvasY);

    }

    void renderOverheadMap(int sStartPointX_M, int sStartPointY_M, int sStartPointX_S, int sStartPointY_S, int sEndXS, int sEndYS) {
	boolean bXOddFlag = false;
	int sAnchorPosX_M, sAnchorPosY_M;
	int sAnchorPosX_S, sAnchorPosY_S;
	int sTempPosX_M, sTempPosY_M;
	int sTempPosX_S, sTempPosY_S;
	boolean fEndRenderRow = false, fEndRenderCol = false;
	int usTileIndex;
	int sX, sY;

	int sHeight = 0;
	int gsRenderHeight = 0;

	int StartX_M_Offset = 12;
	int EndXS_Offset = 128;
	int EndYS_Offset = 64;

	sStartPointX_M -= StartX_M_Offset;
	sEndXS += EndXS_Offset;
	sEndYS += EndYS_Offset;

	// Begin Render Loop
	sAnchorPosX_M = sStartPointX_M;
	sAnchorPosY_M = sStartPointY_M;

	// sStartPointX_S ..... Start point of the overhead map
	sAnchorPosX_S = sStartPointX_S;
	sAnchorPosY_S = sStartPointY_S;

	canvasGraphicsContext.clearRect(0, 0, canvasX, canvasY);

	// Nur Karte und position der gebäude
	do {
	    fEndRenderRow = false;

	    sTempPosX_M = sAnchorPosX_M;
	    sTempPosY_M = sAnchorPosY_M;
	    sTempPosX_S = sAnchorPosX_S;
	    sTempPosY_S = sAnchorPosY_S;

	    if (bXOddFlag) {
		sTempPosX_S += 4;
	    }

	    do {
		usTileIndex = mapRowColToPos(sTempPosY_M, sTempPosX_M);

		if (usTileIndex >= 0 && usTileIndex < mapSize)//dnl ch82 081213
		{
		    IndexedElement[] landLayers = layers[0][usTileIndex];
		    for (IndexedElement layer : landLayers) {
			Tile tile = tileset.getTile(layer);

			if (tile != null) {
			    Image tileImage = tile.getImage();
			    int offsetX = tile.getOffsetX();
			    int offxetY = tile.getOffsetY();

			    sX = sTempPosX_S;
			    sY = sTempPosY_S - sHeight + (gsRenderHeight / 5);

			    canvasGraphicsContext.drawImage(tileImage, sX + offsetX, sY + offxetY);
			}
		    }
		}

		sTempPosX_S += 8;
		sTempPosX_M++;
		sTempPosY_M--;

		if (sTempPosX_S >= sEndXS) {
		    fEndRenderRow = true;
		}

	    } while (!fEndRenderRow);

	    if (bXOddFlag) {
		sAnchorPosY_M++;
	    } else {
		sAnchorPosX_M++;
	    }

	    bXOddFlag = !bXOddFlag;
	    sAnchorPosY_S += 2;

	    if (sAnchorPosY_S >= sEndYS) {
		fEndRenderCol = true;
	    }

	} while (!fEndRenderCol);

    }

    OldMapCursor[] cursors = null;
    OldMapCursor[] selection = null;

    OldMapCursor selStart = null;
    OldMapCursor selEnd = null;
    OldMapCursor placementLocation = null;

    Integer cursorWidth = null;
    Integer cursorHeight = null;

    public void setCursorSize(int x, int y) {
	cursorWidth = x;
	cursorHeight = y;
    }

    public void resetCursorSize() {
	cursorWidth = null;
	cursorHeight = null;
    }

    public void sendClick(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown) {
	if (cursors == null) {
	    return;
	}

	if (shiftDown) {
	    placementLocation = null;

	    if (selStart == null) {
		selStart = new OldMapCursor(cursors[0], new IndexedElement(131, 19));
	    } else {
		selEnd = new OldMapCursor(cursors[0], new IndexedElement(131, 19));

		selectTiles(selStart, selEnd);
	    }
	} else {
	    selStart = null;
	    selEnd = null;
	    selection = null;

	    if (preview != null) {
		placementLocation = new OldMapCursor(cursors[0], new IndexedElement(131, 16));
	    } else {
		placementLocation = null;
	    }
	}

	bakeCursorLayer();
    }

    void initCursors() {
	cursors = new OldMapCursor[5];

	cursorLayer = new IndexedElement[mapSize][];
	for (int i = 0; i < cursorLayer.length; i++) {
	    cursorLayer[i] = new IndexedElement[0];
	}
    }

    // put together an crude overlay
    void bakeCursorLayer() {
	for (int i = 0; i < cursorLayer.length; i++) {
	    cursorLayer[i] = new IndexedElement[0];
	}

	if (selection != null) {
	    for (OldMapCursor cursor : selection) {
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
		    int targetCell = mapRowColToPos(y, x);

		    // count how many tiles are to be copied over
		    int layerCount = 0;
		    for (int n = 0; n < preview.getLayers().length; n++) {
			if (preview.getLayers()[n] != null) {
			    layerCount += preview.getLayers()[n][i].length;
			}
		    }

		    System.out.println("thebob.ja2maptool.util.MapRenderer.bakeCursorLayer(): (" + x + "," + y + ") -> " + layerCount + " layers");
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
	    for (OldMapCursor cursor : cursors) {
		cursorLayer[cursor.getCell()] = cursor.getCursor();
	    }
	}
    }

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

	OldMapCursor cursor = getCursor(cursorPosX, cursorPosY, null);
	cursors[0] = cursor;

	updateAuxCursorDisplay();
	bakeCursorLayer();
    }

    public void updateAuxCursorDisplay() {
	if (cursors == null) {
	    return;
	}
	OldMapCursor cursor = cursors[0];

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

    public void hideCursor() {
	cursors = null;
	selection = null;
	cursorLayer = null;

	preview = null;
	placementLocation = null;
	selStart = null;
	selEnd = null;
    }

    private void selectTiles(OldMapCursor selStart, OldMapCursor selEnd) {

	int startX = selStart.getCellX() < selEnd.getCellX() ? selStart.getCellX() : selEnd.getCellX();
	int endX = selStart.getCellX() > selEnd.getCellX() ? selStart.getCellX() : selEnd.getCellX();
	int startY = selStart.getCellY() < selEnd.getCellY() ? selStart.getCellY() : selEnd.getCellY();
	int endY = selStart.getCellY() > selEnd.getCellY() ? selStart.getCellY() : selEnd.getCellY();

	int size = (endX - startX + 1) * (endY - startY + 1);

	selection = new OldMapCursor[size];

	IndexedElement ctyp = new IndexedElement(131, 6);	// aux cursor type 	

	int i = 0;
	for (int x = startX; x <= endX; x++) {
	    for (int y = startY; y <= endY; y++) {
		selection[i] = getCursor(x, y, ctyp);
		i++;
	    }
	}
    }

    public SelectedTiles getSelection() {
	if (selection == null || selection.length == 0 || selStart == null) {
	    return null;
	}

	OldMapCursor start = selection[0];
	OldMapCursor end = selection[selection.length - 1];

	int startX = start.getCellX();
	int endX = end.getCellX();
	int startY = start.getCellY();
	int endY = end.getCellY();

	SelectedTiles selectedTiles = new SelectedTiles(tileset.getIndex(), startX, endX, start.getCell(), startY, endY, end.getCell());

	IndexedElement[][][] selectionLayers = new IndexedElement[6][selection.length][];

	for (int L = 0; L < layers.length; L++) {
	    for (int i = 0; i < selection.length; i++) {
		selectionLayers[L][i] = layers[L][selection[i].getCell()];
	    }
	}

	selectedTiles.setLayers(selectionLayers);

	return selectedTiles;
    }

    public void placeSelection(SelectedTiles selection) {
	OldMapCursor cursor = placementLocation;

	int startX = cursor.getCellX() - cursorWidth / 2;
	int startY = cursor.getCellY() - cursorHeight / 2;

	for (int L = 0; L < layers.length; L++) {
	    int i = 0;
	    for (int x = startX; x < startX + cursorWidth; x++) {
		for (int y = startY; y < startY + cursorHeight; y++) {
		    int targetCell = mapRowColToPos(y, x);

		    layers[L][targetCell] = selection.getLayers()[L][i];

		    i++;
		}
	    }
	}
    }

    SelectedTiles preview = null;

    public void setPlacementPreview(SelectedTiles selection) {
	preview = selection;
    }

    public OldMapCursor getCursor(int x, int y, IndexedElement cursor) {
	return new OldMapCursor(this, x, y, cursor);
    }

    public OldMapCursor getCursor(double x, double y, IndexedElement cursor) {
	return new OldMapCursor(this, screenXYtoCellX(x, y), screenXYtoCellY(x, y), cursor);
    }

}
