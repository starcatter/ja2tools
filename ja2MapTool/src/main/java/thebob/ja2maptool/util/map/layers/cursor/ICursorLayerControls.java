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

import thebob.assetloader.map.core.components.IndexedElement;
import thebob.ja2maptool.util.map.layers.cursor.CursorLayer.CursorFillMode;

/**
 * Basic cursor layer controls, supposed to be manipulated from outside of the
 * DisplayManager
 *
 * @author the_bob
 */
public interface ICursorLayerControls {

    void clearLayer(int layer);

    void clearLayers();

    void placeCursor(int layer, int cell, IndexedElement cursor);

    void placeCursor(int layer, int cursorX, int cursorY, IndexedElement cursor);

    /**
     * Draws a cursor spanning a rectangular area from start(x,y) to end(x,y), following map grid lines (diagonal)
     * @param layer
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param cursor
     * @param mode 
     */
    void placeCursorRect(int layer, int startX, int startY, int endX, int endY, IndexedElement cursor, CursorFillMode mode);
    
    /**
     * Draws a cursor spanning a rectangular area centered at cursor(x,y)
     * @param layer
     * @param cursorX
     * @param cursorY
     * @param cursorWidth
     * @param cursorHeight
     * @param cursor
     * @param mode 
     */
    void placeCursorCenterRect(int layer, int cursorX, int cursorY, int cursorWidth, int cursorHeight, IndexedElement cursor, CursorFillMode mode);

    int[] getCellNumbersForRadius(int cursorX, int cursorY, int cursorWidth, int cursorHeight, CursorFillMode mode);
    int[] getCellNumbersForRect(int startX, int startY, int endX, int endY, CursorFillMode mode);

}
