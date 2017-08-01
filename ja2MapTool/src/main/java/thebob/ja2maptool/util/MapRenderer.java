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
package thebob.ja2maptool.util;

import java.util.Arrays;
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

public class MapRenderer {

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
    private IndexedElement[][] landLayer;
    private IndexedElement[][] structLayer;
    private IndexedElement[][] objectLayer;
    private IndexedElement[][] shadowLayer;

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
	    if (overheadMap) {
		renderOverheadMap(windowOffsetX, windowOffsetY, 0, 0, canvasX, canvasY);
	    } else {
		renderMap();
	    }
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

    public void loadMap(MapData map) {
	mapCols = map.getSettings().iColSize;
	mapRows = map.getSettings().iRowSize;
	mapSize = mapCols * mapRows;

	landLayer = map.getLayers().landLayer;
	objectLayer = map.getLayers().objectLayer;
	structLayer = map.getLayers().structLayer;
	shadowLayer = map.getLayers().shadowLayer;

	// set view window in map center
	windowOffsetX = mapCols / 2 - (canvasX / xSpacing) / 2;
	windowOffsetY = mapRows / 2 - (canvasY / ySpacing) / 2;

	// update display
	moveWindow(0, 0);
    }

    public void applyRemapping(Map<Integer, TileCategoryMapping> mappingList) {
	landLayer = remapLayer(landLayer, mappingList);
	objectLayer = remapLayer(objectLayer, mappingList);
	structLayer = remapLayer(structLayer, mappingList);
	//shadowLayer = remapLayer(shadowLayer, mappingList);
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
    

    protected void displayCellLayers(IndexedElement[] layers, int canvasX, int canvasY) {
	for (IndexedElement layer : layers) {
	    Tile tile = tileset.getTile(layer);

	    if (tile != null) {
		Image tileImage = tile.getImage();
		int offsetX = tile.getOffsetX();
		int offxetY = tile.getOffsetY();

		canvasGraphicsContext.drawImage(tileImage, (canvasX + offsetX) * scale, (canvasY + offxetY) * scale, tile.getWidth() * scale, tile.getHeight() * scale);
	    }
	}
    }

    protected int mapRowColToPos(int r, int c) {
	return ((r) * mapCols + (c));
    }

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
		if (cell >= 0 && cell < (mapRows * mapCols)) {

		    //System.out.println("col(X)=" + col + ", row(Y)=" + row + ", grid=" + new GridPos(cell));
		    displayCellLayers(landLayer[cell], displayX, displayY);
		    displayCellLayers(objectLayer[cell], displayX, displayY);
		    displayCellLayers(structLayer[cell], displayX, displayY);
		    displayCellLayers(shadowLayer[cell], displayX, displayY);
		}
		mapPosX++;
		mapPosY--;

		displayX += xSpacing;

	    } while (displayX*scale < canvasX);

	    if (bXOddFlag) {
		windowStartOffsetY++;
	    } else {
		windowStartOffsetX++;
	    }

	    bXOddFlag = !bXOddFlag;
	    canvasStartOffsetY += ySpacing / 2;

	} while (displayY*scale < canvasY);

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
		    IndexedElement[] layers = landLayer[usTileIndex];
		    for (IndexedElement layer : layers) {
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

}
