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
package thebob.ja2maptool.util.map.renderer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.tileset.Tile;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.map.events.MapEvent;
import thebob.ja2maptool.util.map.layers.base.ITileLayerGroup;
import thebob.ja2maptool.util.map.layers.base.TileLayer;
import thebob.ja2maptool.util.map.renderer.renderlayer.OverlaySettings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * @author the_bob
 */
public class TileRenderer extends Observable implements ITileRendererManager {

    {
        BufferedImage img = new BufferedImage(1024, 768, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics graphics = img.getGraphics();
    }

    class TileRendererTarget {

        OverlaySettings settings;

        Canvas targetCanvas = new Canvas();
        GraphicsContext canvasGraphicsContext = targetCanvas.getGraphicsContext2D();

        public void bind(Canvas parent) {
            //System.out.println("thebob.ja2maptool.util.renderer.base.TileRenderer.TileRendererTarget.bind()");
            StackPane canvasParent = (StackPane) parent.getParent();    // hopefully the canvas is in a stackpane, like it should
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
                //System.out.println("thebob.ja2maptool.util.renderer.base.TileRenderer.TileRendererTarget.<init>() BINDING");
                bind(parent);
            }
        }

        private void cleanup() {
            canvasGraphicsContext.clearRect(0, 0, canvasX, canvasY);
            targetCanvas.toBack();
            targetCanvas.setVisible(false);
            targetCanvas.setDisable(true);

            StackPane canvasParent = (StackPane) targetCanvas.getParent();    // hopefully the canvas is in a stackpane, like it should
            canvasParent.getChildren().remove(targetCanvas);
            targetCanvas = null;
            canvasGraphicsContext = null;
        }

        private void applyEffect() {
            if (settings.getEffect() != null) {
                canvasGraphicsContext.applyEffect(settings.getEffect());
            }
        }

        private void clear() {
            canvasGraphicsContext.clearRect(0, 0, targetCanvas.getWidth(), targetCanvas.getHeight());
        }

        public void drawImage(Image tileImage, double tileX, double tileY, double tileWidth, double tileHeight) {
            canvasGraphicsContext.drawImage(tileImage, tileX, tileY, tileWidth, tileHeight);
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
    // ListMultimap<ITileLayerGroup, TileRendererTarget> renderTargets = ArrayListMultimap.create();

    // bound canvas
    Canvas canvas = null;
    GraphicsContext canvasGraphicsContext = null;
    TileRendererTarget defaultTarget = new TileRendererTarget(null, new OverlaySettings(1.0d, 0, 0, null));

    int canvasX = 1280;
    int canvasY = 800;

    public int mapRowColToPos(int r, int c) {
        if (r < 0 || r > mapRows || c < 0 || c > mapCols) {
            return -1;
        } else {
            return ((r) * mapCols + (c));
        }
    }

    public int mapRowColToPos(int r, int c, ITileLayerGroup layerGroup) {
        if (    // check limits
                (layerGroup.limitDrawArea() && (
                        r < 0
                        || c < 0
                        || r > mapRows
                        || c > mapCols
                )) || (layerGroup.trimEdges() && (
                        // trim edges
                        r + c <= mapRows / 2
                        || r - c >= mapRows / 2
                        || c - r >= mapRows / 2
                        || r + c >= mapRows * 1.5
                )) || (
                        (c==windowOffsetX-2||c==windowOffsetX-1||c==windowOffsetX||c==windowOffsetX+1||c==windowOffsetX+2) && (r==windowOffsetY-2 || r==windowOffsetY-1 || r==windowOffsetY || r==windowOffsetY+1 || r==windowOffsetY+2 )
                )
                ) {
            return -1;
        }

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

        //renderMap();
        centerWindow();

        setChanged();
        notifyObservers(new MapEvent(MapEvent.ChangeType.MAP_CANVAS_CHANGED));
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
        if (canvas != null && renderLayers.isEmpty() == false) {
            renderMapLayers();
        }
    }

    protected void renderMapLayers() {
        renderLayers.forEach(layer -> {
            renderLayerGroup(layer);
        });
    }


    public void shutdown() {
        // nothing to do!
    }

    protected void renderLayerGroup(ITileLayerGroup layerGroup) {
        int cell = 0;

        int displayX = 0;
        int displayY = 0;

        int windowStartOffsetX = windowOffsetX;
        int windowStartOffsetY = windowOffsetY;

        int canvasStartOffsetX = -1 * xSpacing;
        int canvasStartOffsetY = -1 * ySpacing;

        boolean bXOddFlag = false;

        // clear the layer
        renderTargets.get(layerGroup).clear();

        do {
            displayX = canvasStartOffsetX;
            displayY = canvasStartOffsetY;

            if (bXOddFlag) {
                displayX += xSpacing / 2;
            }

            int mapPosX = windowStartOffsetX;
            int mapPosY = windowStartOffsetY;

            do {

                cell = mapRowColToPos(mapPosY, mapPosX, layerGroup);
                if (cell >= 0 && cell < mapSize) {
                    displayCellLayers(cell, displayX, displayY, layerGroup);
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

        renderTargets.get(layerGroup).applyEffect();
    }

    private void displayCellLayers(int cell, int displayX, int displayY, ITileLayerGroup layerGroup) {
        TileRendererTarget layerTarget = renderTargets.get(layerGroup);
        GraphicsContext layerTargetContext = layerTarget.canvasGraphicsContext;

        Tileset tileset = layerGroup.getTileset();

        for (TileLayer tileLayer : layerGroup) {
            if (tileLayer.isEnabled() == false) {
                continue;
            }

            layerTargetContext.setGlobalAlpha(tileLayer.getOpacity());
            int layerOffsetX = tileLayer.getDisplayOffsetX();
            int layerOffsetY = tileLayer.getDisplayOffsetY();

            IndexedElement[] tileStack = tileLayer.getTiles()[cell];

            for (IndexedElement tileDef : tileStack) {
                Tile tile = tileset.getTile(tileDef);

                if (tile != null) {
                    Image tileImage = tile.getImage();
                    int offsetX = tile.getOffsetX();
                    int offsetY = tile.getOffsetY();

                    double tileX = (displayX + offsetX + layerOffsetX) * scale;
                    double tileY = (displayY + offsetY + layerOffsetY) * scale;
                    double tileWidth = tile.getWidth() * scale;
                    double tileHeight = tile.getHeight() * scale;

                    layerTarget.drawImage(tileImage, tileX, tileY, tileWidth, tileHeight);
                }
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
        centerWindow();
    }

    public int getMapRows() {
        return mapRows;
    }

    public void setMapRows(int mapRows) {
        this.mapRows = mapRows;
        mapSize = mapCols * mapRows;
        centerWindow();
    }

    // ----------------------------------------
    // View window controls
    // ----------------------------------------
    public void moveWindow(int x, int y) {
        if (x == 0 && y == 0) {
            System.err.println("thebob.ja2maptool.util.renderer.base.TileRenderer.moveWindow(): renderer should know when to refresh itself!");
        }

        windowOffsetX += x;
        windowOffsetY += y;

        renderMap();

        if (x != 0 || y != 0) {
            setChanged();
            notifyObservers(new MapEvent(MapEvent.ChangeType.MAP_WINDOW_MOVED));
        }
    }

    public void centerWindow() {

        double width = ((mapCols+2) * xSpacing)/2;
        double height = ((mapRows+2) * ySpacing)/2;

        if(width > 0 && height > 0 && canvasX > 0 && canvasY > 0){
            double xRatio = canvasX/width;
            double yRatio = canvasY/height;

            if(xRatio < yRatio){
                scale = xRatio;
            } else {
                scale = yRatio;
            }

            double scaledWidth = width * scale;
            double scaledHeight = height * scale;

            int hSpace = (int) Math.abs(canvasX - scaledWidth);
            int vSpace = (int) Math.abs(canvasY - scaledHeight);

            double hSpaceTiles = hSpace/(xSpacing*scale);
            double vSpaceTiles = vSpace/(ySpacing*scale);

            int hOffset = (int)Math.ceil(hSpaceTiles)/2; // -1 / -2
            int vOffset = (int)Math.ceil(vSpaceTiles)/2; // -32

            // move map to top left
            windowOffsetX = -2;
            windowOffsetY = mapRows/2;

            // center horizontal
            windowOffsetX -= hOffset;
            windowOffsetY += hOffset;

            // center vertical
            windowOffsetX -= vOffset;
            windowOffsetY -= vOffset;

        } else {
            scale=1d;
            windowOffsetX = mapCols / 2 - (canvasX / xSpacing) / 2;
            windowOffsetY = mapRows / 2 - (canvasY / ySpacing) / 2;
            }

        renderMap();

        setChanged();
        notifyObservers(new MapEvent(MapEvent.ChangeType.MAP_WINDOW_MOVED));
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

        renderMap();

        setChanged();
        notifyObservers(new MapEvent(MapEvent.ChangeType.MAP_WINDOW_ZOOMED));
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
        System.out.println("thebob.ja2maptool.util.map.renderer.TileRenderer.addRenderLayer() " + layer);
        renderTargets.put(layer, defaultTarget);
        renderLayers.add(layer);

        layer.subscribe(this);
        updateLayerGroups();
    }

    @Override
    public void addRenderOverlay(ITileLayerGroup layer, OverlaySettings settings) {
        System.out.println("thebob.ja2maptool.util.map.renderer.TileRenderer.addRenderOverlay() " + layer);
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
        } else {
            renderMap();
        }

        System.out.println("thebob.ja2maptool.util.renderer.base.TileRenderer.updateLayerGroups(): " + toString());
    }

    // ----------------------------------------
    // Observer events
    // ----------------------------------------
    @Override
    public void update(Observable o, Object arg) {
        ITileLayerGroup caller = (ITileLayerGroup) o;
        MapEvent message = (MapEvent) arg;
        if (message != null) {
            switch (message.getType()) {
                case LAYER_ALTERED:
                    renderLayerGroup(caller);
                    break;
                case MAP_LOADED:
                    updateLayerGroups();
                    break;
                case MAP_ALTERED:
                    updateLayerGroups();
                    break;
                default:

            }
        }

    }

    @Override
    public String toString() {
        return "TileRenderer{" + "mapCols=" + mapCols + ", mapRows=" + mapRows + ", canvasX=" + canvasX + ", canvasY=" + canvasY + '}';
    }

}
