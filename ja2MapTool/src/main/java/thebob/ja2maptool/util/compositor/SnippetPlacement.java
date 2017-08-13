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
package thebob.ja2maptool.util.compositor;

import thebob.ja2maptool.util.map.layers.cursor.MapCursor;

/**
 *
 * @author the_bob
 */
public class SnippetPlacement {

    int cellX;
    int cellY;
    int cell;
    SelectedTiles snippet;
    boolean[] enabledLayers;

    public SnippetPlacement(MapCursor placement, SelectedTiles source) {
        this.cellX = placement.getCellX();
        this.cellY = placement.getCellY();
        this.cell = placement.getCell();
        this.snippet = source;
        this.enabledLayers = null;
    }

    public SnippetPlacement(int cellX, int cellY, int cell, SelectedTiles snippet, boolean[] enabledLayers) {
        this.cellX = cellX;
        this.cellY = cellY;
        this.cell = cell;
        this.snippet = snippet;
        this.enabledLayers = enabledLayers;
    }

    public int getCellX() {
        return cellX;
    }

    public int getCellY() {
        return cellY;
    }

    public int getCell() {
        return cell;
    }

    public void setCellX(int cellX) {
        this.cellX = cellX;
    }

    public void setCellY(int cellY) {
        this.cellY = cellY;
    }

    public void setCell(int cell) {
        this.cell = cell;
    }

    public SelectedTiles getSnippet() {
        return snippet;
    }

    public boolean[] getEnabledLayers() {
        return enabledLayers;
    }

    @Override
    public String toString() {
        return "SnippetPlacement{" + "cellX=" + cellX + ", cellY=" + cellY + ", snippet=" + snippet + '}';
    }

}
