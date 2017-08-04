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
package thebob.ja2maptool.util.renderer.base;

import thebob.ja2maptool.util.renderer.events.RendererEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.tileset.Tile;
import thebob.assetloader.tileset.Tileset;

/**
 *
 * @author the_bob
 */
public class TileRenderer extends Observable implements ITileRendererManager {

    // tile spacing
    final int xSpacing = 40;
    final int ySpacing = 20;

    // loaded map data
    private int mapCols = -1;
    private int mapRows = -1;
    private int mapSize = -1;

    // window position
    int windowOffsetX = -1;
    int windowOffsetY = -1;
    double scale = 1.0d;

    List<ITileLayerGroup> renderLayers = new ArrayList<>();

    // bound canvas
    Canvas canvas = null;
    GraphicsContext canvasGraphicsContext = null;

    int canvasX = 1280;
    int canvasY = 800;

    public int mapRowColToPos(int r, int c) {
	return ((r) * mapCols + (c));
    }

    // ----------------------------------------
    // Canvas assignment
    // ----------------------------------------
    @Override
    public void setCanvas(Canvas canvas) {
	this.canvas = canvas;
	canvasGraphicsContext = canvas.getGraphicsContext2D();

	canvasX = (int) canvas.getWidth();
	canvasY = (int) canvas.getHeight();

	setChanged();
	notifyObservers(new RendererEvent(RendererEvent.ChangeType.MAP_CANVAS_CHANGED));
    }

    @Override
    public int getCanvasX() {
	return canvasX;
    }

    @Override
    public int getCanvasY() {
	return canvasY;
    }

    // ----------------------------------------
    // Main render functions
    // ----------------------------------------
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

		    for (ITileLayerGroup layerGroup : renderLayers) {
			for (TileLayer layer : layerGroup) {
			    if (layer.isEnabled()) {
				displayCellLayers(layer.getTiles()[cell], displayX, displayY, layer.getDisplayOffsetX(), layer.getDisplayOffsetY(), layerGroup.getTileset());
			    }
			}
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

    protected void displayCellLayers(IndexedElement[] layers, int canvasX, int canvasY, int drawOffsetX, int drawOffsetY, Tileset tileset) {
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

    // ----------------------------------------
    // Map size getters/setters
    // ----------------------------------------
    public int getMapCols() {
	return mapCols;
    }

    public void setMapCols(int mapCols) {
	this.mapCols = mapCols;
	mapSize = mapCols * mapRows;
    }

    public int getMapRows() {
	return mapRows;
    }

    public void setMapRows(int mapRows) {
	this.mapRows = mapRows;
	mapSize = mapCols * mapRows;
    }

    // ----------------------------------------
    // View window controls
    // ----------------------------------------
    public void moveWindow(int x, int y) {
	windowOffsetX += x;
	windowOffsetY += y;

	if (canvas != null && canvasGraphicsContext != null) {
	    renderMap();
	}

	if (x != 0 || y != 0) {
	    setChanged();
	    notifyObservers(new RendererEvent(RendererEvent.ChangeType.MAP_WINDOW_MOVED));
	}
    }

    public void centerWindow() {
	windowOffsetX = mapCols / 2 - (canvasX / xSpacing) / 2;
	windowOffsetY = mapRows / 2 - (canvasY / ySpacing) / 2;

	setChanged();
	notifyObservers(new RendererEvent(RendererEvent.ChangeType.MAP_WINDOW_MOVED));
    }

    @Override
    public int getWindowOffsetX() {
	return windowOffsetX;
    }

    @Override
    public void setWindowOffsetX(int windowOffsetX) {
	this.windowOffsetX = windowOffsetX;
    }

    @Override
    public int getWindowOffsetY() {
	return windowOffsetY;
    }

    @Override
    public void setWindowOffsetY(int windowOffsetY) {
	this.windowOffsetY = windowOffsetY;
    }

    public double getScale() {
	return scale;
    }

    public void setScale(double scale) {
	this.scale = scale;
	setChanged();
	notifyObservers(new RendererEvent(RendererEvent.ChangeType.MAP_WINDOW_ZOOMED));
    }

    // ----------------------------------------
    // Unused getters?
    // ----------------------------------------
    public int getxSpacing() {
	return xSpacing;
    }

    public int getySpacing() {
	return ySpacing;
    }

    public int getMapSize() {
	return mapSize;
    }

    // ----------------------------------------
    // Render layer management
    // ----------------------------------------
    @Override
    public void addRenderLayer(ITileLayerGroup layer) {
	renderLayers.add(layer);
	layer.subscribe(this);
	updateLayerGroups();
    }

    @Override
    public void removeRenderLayer(ITileLayerGroup layer) {
	renderLayers.remove(layer);
	updateLayerGroups();
    }

    @Override
    public void removeRenderLayer(int index) {
	renderLayers.get(index).unsubscribe(this);
	renderLayers.remove(index);
	updateLayerGroups();
    }

    @Override
    public List<ITileLayerGroup> getRenderLayers() {
	return renderLayers;
    }

    private void updateLayerGroups() {
	mapCols = -1;
	mapRows = -1;
	mapSize = -1;

	for (ITileLayerGroup layer : renderLayers) {
	    if (layer.getMapCols() > mapCols || layer.getMapRows() > mapRows) {
		mapRows = layer.getMapRows();
		mapCols = layer.getMapCols();

		mapSize = mapCols * mapRows;
	    }
	}

	if (mapSize > 0 && (windowOffsetX > mapCols || windowOffsetY > mapRows || windowOffsetX < 0 || windowOffsetY < 0)) {
	    centerWindow();
	}

	System.out.println("thebob.ja2maptool.util.renderer.base.TileRenderer.updateLayerGroups(): " + toString());
    }

    // ----------------------------------------
    // Observer events
    // ----------------------------------------
    @Override
    public void update(Observable o, Object arg) {
	ITileLayerGroup caller = (ITileLayerGroup) o;
	RendererEvent message = (RendererEvent) arg;
	if (message != null) {
	    switch (message.getType()) {
		case MAP_LOADED:
		    updateLayerGroups();
		    moveWindow(0, 0);
		    break;
		case MAP_ALTERED:
		    updateLayerGroups();
		    moveWindow(0, 0);
		    break;
		case CURSOR_MOVED:
		    moveWindow(0, 0);
		    break;
		default:
		    throw new AssertionError(message.getType().name());

	    }
	}

    }

    @Override
    public String toString() {
	return "TileRenderer{" + "mapCols=" + mapCols + ", mapRows=" + mapRows + ", canvasX=" + canvasX + ", canvasY=" + canvasY + '}';
    }

}
