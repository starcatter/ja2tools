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
package thebob.ja2maptool.util.renderer.base;

import thebob.ja2maptool.util.renderer.events.RendererEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Blend;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.tileset.Tile;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.renderer.OverlaySettings;

/**
 *
 * @author the_bob
 */
public class TileRenderer extends Observable implements ITileRendererManager {

    class TileRendererTarget {

	OverlaySettings settings;

	Canvas targetCanvas = new Canvas();
	GraphicsContext canvasGraphicsContext = targetCanvas.getGraphicsContext2D();

	public void bind(Canvas parent) {
	    System.out.println("thebob.ja2maptool.util.renderer.base.TileRenderer.TileRendererTarget.bind()");
	    StackPane canvasParent = (StackPane) parent.getParent();	// hopefully the canvas is in a stackpane, like it should
	    if (canvasParent.getChildren().indexOf(targetCanvas) < 0) {
		canvasParent.getChildren().add(targetCanvas);
	    }

	    targetCanvas.setOpacity(settings.getOpacity());
	    targetCanvas.setTranslateX(settings.getOffsetX());
	    targetCanvas.setTranslateY(settings.getOffsetY());
	    //canvasGraphicsContext.setEffect(settings.getEffect());

	    targetCanvas.setWidth(parent.getWidth());
	    targetCanvas.setHeight(parent.getHeight());

	    parent.toFront();
	}

	TileRendererTarget(Canvas parent, OverlaySettings settings) {
	    this.settings = settings;

	    if (parent != null) {
		System.out.println("thebob.ja2maptool.util.renderer.base.TileRenderer.TileRendererTarget.<init>() BINDING");
		bind(parent);
	    }
	}

	private void cleanup() {
	    canvasGraphicsContext.clearRect(0, 0, canvasX, canvasY);
	    targetCanvas.toBack();
	    targetCanvas.setVisible(false);
	    targetCanvas.setDisable(true);

	    StackPane canvasParent = (StackPane) targetCanvas.getParent();	// hopefully the canvas is in a stackpane, like it should
	    canvasParent.getChildren().remove(targetCanvas);
	    targetCanvas = null;
	    canvasGraphicsContext = null;
	}
    }

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

    Map<ITileLayerGroup, TileRendererTarget> renderTargets = new HashMap<ITileLayerGroup, TileRendererTarget>();

    // bound canvas
    Canvas canvas = null;
    GraphicsContext canvasGraphicsContext = null;
    TileRendererTarget defaultTarget = new TileRendererTarget(null, new OverlaySettings(1.0d, 0, 0, null));

    int canvasX = 1280;
    int canvasY = 800;

    {
	System.out.println("thebob.ja2maptool.util.renderer.base.TileRenderer(): init");
    }

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

	defaultTarget.bind(canvas);
	for (TileRendererTarget target : renderTargets.values()) {
	    target.bind(canvas);
	}

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
	//canvasGraphicsContext.clearRect(0, 0, canvasX, canvasY);

	//defaultTarget.canvasGraphicsContext.clearRect(0, 0, canvasX, canvasY);
	for (TileRendererTarget target : renderTargets.values()) {
	    target.canvasGraphicsContext.clearRect(0, 0, canvasX, canvasY);
	}

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
			GraphicsContext layerGC = renderTargets.get(layerGroup).canvasGraphicsContext;
			for (TileLayer layer : layerGroup) {
			    if (layer.isEnabled()) {
				displayCellLayers(layer.getTiles()[cell], displayX, displayY, layer.getDisplayOffsetX(), layer.getDisplayOffsetY(), layerGroup.getTileset(), layerGC);
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

	for (TileRendererTarget target : renderTargets.values()) {
	    target.canvasGraphicsContext.applyEffect(target.settings.getEffect());
	}

    }

    protected void displayCellLayers(IndexedElement[] layers, int canvasX, int canvasY, int drawOffsetX, int drawOffsetY, Tileset tileset, GraphicsContext canvasGraphicsContext) {
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

	if (canvas != null) {
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
	renderTargets.put(layer, defaultTarget);
	renderLayers.add(layer);

	layer.subscribe(this);
	updateLayerGroups();
    }

    @Override
    public void addRenderOverlay(ITileLayerGroup layer, OverlaySettings settings) {
	renderTargets.put(layer, new TileRendererTarget(canvas, settings));
	renderLayers.add(layer);

	layer.subscribe(this);
	updateLayerGroups();
    }

    @Override
    public void removeRenderLayer(ITileLayerGroup layer) {
	renderLayers.remove(layer);
	
	TileRendererTarget target = renderTargets.get(layer);
	if (target != null && target != defaultTarget) {	    
	    target.cleanup();
	    renderTargets.remove(layer);
	}

	updateLayerGroups();
    }

    @Override
    public void removeRenderLayer(int index) {
	removeRenderLayer(renderLayers.get(index));
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
	    }
	}

    }

    @Override
    public String toString() {
	return "TileRenderer{" + "mapCols=" + mapCols + ", mapRows=" + mapRows + ", canvasX=" + canvasX + ", canvasY=" + canvasY + '}';
    }

}
