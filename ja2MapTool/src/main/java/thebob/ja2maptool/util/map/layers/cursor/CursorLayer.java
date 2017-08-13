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
package thebob.ja2maptool.util.map.layers.cursor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.tileset.Tileset;
import static thebob.ja2maptool.util.map.MapUtils.screenXYtoCellX;
import static thebob.ja2maptool.util.map.MapUtils.screenXYtoCellY;
import thebob.ja2maptool.util.map.events.MapEvent;
import thebob.ja2maptool.util.map.layers.base.TileLayer;
import thebob.ja2maptool.util.map.layers.base.TileLayerGroup;

/**
 * Render layers dedicated for displaying cursors, action hints and editor state
 *
 * @author the_bob
 */
public class CursorLayer extends TileLayerGroup implements ICursorLayerManager {

    public static final int LAYER_CURSOR = 2;
    public static final int LAYER_ACTION = 1;
    public static final int LAYER_STATE = 0;
    public static final int LAYERS = 3;

    public enum CursorFillMode {
        Full,
        Border,
        Corners
    }

    protected List<TileLayer> layers = new ArrayList<>();

    @Override
    public void init(int mapRows, int mapCols, Tileset tileset) {
        super.init(mapRows, mapCols, tileset);

        layers.clear();
        for (int i = 0; i < LAYERS; i++) {
            layers.add(new TileLayer(true, 0, 0, new IndexedElement[mapSize][0]));
        }
        
        layers.get(LAYER_CURSOR).setOpacity(0.80);
        layers.get(LAYER_ACTION).setOpacity(0.45);
        layers.get(LAYER_STATE).setOpacity(0.75);
        
        System.out.println("thebob.ja2maptool.util.map.layers.cursor.CursorLayer.init()");
    }

    @Override
    public MapCursor getCursor(int x, int y, IndexedElement cursor) {
        return new MapCursor(this, x, y, cursor);
    }

    @Override
    public MapCursor getCursor(double x, double y, IndexedElement cursor) {
        return new MapCursor(this, screenXYtoCellX(x, y), screenXYtoCellY(x, y), cursor);
    }

    @Override
    public void clearLayer(int layer) {
        layers.get(layer).clearTiles();
        notifySubscribers(new MapEvent(MapEvent.ChangeType.LAYER_ALTERED));
    }

    @Override
    public void clearLayers() {
        for (TileLayer layer : layers) {
            layer.clearTiles();
        }
        notifySubscribers(new MapEvent(MapEvent.ChangeType.LAYER_ALTERED));
        System.out.println("thebob.ja2maptool.util.map.layers.cursor.CursorLayer.clearLayers()");
    }

    @Override
    public TileLayer getLayer(int n) {
        return layers.get(n);
    }

    // ----------------------------------------
    // Stuff for drawing cursors
    // ----------------------------------------
    private void placeCursorInternal(int layer, int cell, IndexedElement cursor) {
        if (isProperMapCell(cell)) {
            layers.get(layer).getTiles()[cell] = new IndexedElement[]{cursor};
        }
    }

    @Override
    public void placeCursor(int layer, int cell, IndexedElement cursor) {
        placeCursorInternal(layer, cell, cursor);
        notifySubscribers(new MapEvent(MapEvent.ChangeType.LAYER_ALTERED));
    }

    @Override
    public void placeCursor(int layer, int cursorX, int cursorY, IndexedElement cursor) {
        placeCursorInternal(layer, rowColToPos(cursorY, cursorX), cursor);
        notifySubscribers(new MapEvent(MapEvent.ChangeType.LAYER_ALTERED));
    }

    @Override
    public void placeCursorCenterRect(int layer, int cursorX, int cursorY, int cursorWidth, int cursorHeight, IndexedElement cursor, CursorFillMode mode) {
        int[] cells = getCellNumbersForRadius(cursorX, cursorY, cursorWidth, cursorHeight, mode);
        for (int cell : cells) {
            placeCursor(layer, cell, cursor);
        }
        notifySubscribers(new MapEvent(MapEvent.ChangeType.LAYER_ALTERED));
    }

    @Override
    public void placeCursorRect(int layer, int startX, int startY, int endX, int endY, IndexedElement cursor, CursorFillMode mode) {
        int[] cells = getCellNumbersForRect(startX, startY, endX, endY, mode);
        for (int cell : cells) {
            placeCursor(layer, cell, cursor);
        }
        notifySubscribers(new MapEvent(MapEvent.ChangeType.LAYER_ALTERED));
    }

    @Override
    public int[] getCellNumbersForRadius(int cursorX, int cursorY, int cursorWidth, int cursorHeight, CursorFillMode mode) {
        int startX = cursorX - cursorWidth / 2;
        int startY = cursorY - cursorHeight / 2;
        int endX = startX + cursorWidth - 1;
        int endY = startY + cursorHeight - 1;

        int size = (cursorWidth+1) * (cursorHeight+1);
        int[] cells = new int[size];

        int i = 0;
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if ((mode == CursorFillMode.Full)
                        || (mode == CursorFillMode.Border && (x == startX || x == endX || y == startY || y == endY))
                        || (mode == CursorFillMode.Corners && (x == startX && y == startY || x == endX && y == startY || x == startX && y == endY || x == endX && y == endY))) {
                    cells[i++] = rowColToPos(y, x);
                }
            }
        }
        return cells;
    }

    @Override
    public int[] getCellNumbersForRect(int startX, int startY, int endX, int endY, CursorFillMode mode) {
        int X1 = startX < endX ? startX : endX;
        int Y1 = startY < endY ? startY : endY;
        int X2 = startX > endX ? startX : endX;
        int Y2 = startY > endY ? startY : endY;

        int width = 1 + X2 - X1;
        int height = 1 + Y2 - Y1;

        int size = width * height;
        int[] cells = new int[size];

        int i = 0;
        for (int x = X1; x <= X2; x++) {
            for (int y = Y1; y <= Y2; y++) {
                if ((mode == CursorFillMode.Full)
                        || (mode == CursorFillMode.Border && (x == startX || x == endX || y == startY || y == endY))
                        || (mode == CursorFillMode.Corners && (x == startX && y == startY || x == endX && y == startY || x == startX && y == endY || x == endX && y == endY))) {
                    cells[i++] = rowColToPos(y, x);
                }
            }
        }

        return cells;
    }

    // ---------------
    private boolean isProperMapCell(int cell) {
        return (cell < mapSize && cell >= 0);
    }

    // ---------------
    @Override
    public Iterator<TileLayer> iterator() {
        return layers.iterator();
    }

    @Override
    public String toString() {
        return "CursorLayer{" + super.toString() + '}';
    }

}
