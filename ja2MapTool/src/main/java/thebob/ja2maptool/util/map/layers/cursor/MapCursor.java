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
import thebob.ja2maptool.util.map.layers.base.TileLayerGroup;
import thebob.ja2maptool.util.map.old.OldMapRenderer;

/**
 *
 * @author the_bob
 */
public class MapCursor {

    TileLayerGroup parent = null;

    int cellX;
    int cellY;
    int cell;

    IndexedElement[] cursor = new IndexedElement[]{new IndexedElement(131, 14)};

    public MapCursor(MapCursor src, IndexedElement cursor) {
	this.parent = src.parent;

	this.cellX = src.cellX;
	this.cellY = src.cellY;
	this.cell = src.cell;

	this.cursor = new IndexedElement[]{cursor};
    }

    public MapCursor(TileLayerGroup parent, int x, int y, IndexedElement cursor) {
	this.parent = parent;
	cellX = x;
	cellY = y;

	cell = parent.rowColToPos(this.cellY, this.cellX);

	if (cursor != null) {
	    this.cursor = new IndexedElement[]{cursor};
	}
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

    public IndexedElement[] getCursor() {
	return cursor;
    }

    
    
    @Override
    public String toString() {
	return "MapCursor{" + "cellX=" + cellX + ", cellY=" + cellY + '}';
    }

}
