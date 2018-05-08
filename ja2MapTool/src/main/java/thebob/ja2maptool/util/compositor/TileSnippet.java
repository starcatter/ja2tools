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

import thebob.assetloader.map.core.components.IndexedElement;

/**
 *
 * @author starcatter
 */
public class TileSnippet {

	final int width;
	final int height;
	final int size;
	final IndexedElement[] tiles;

	public TileSnippet(int width, int height) {
		this.width = width;
		this.height = height;
		size = width * height;
		this.tiles = new IndexedElement[size];
	}

	// ---------------------------------
	// set/get contents
	// ---------------------------------
	public void setXY(int x, int y, IndexedElement tile) {
		tiles[xyToGrid(x, y)] = tile;
	}

	public IndexedElement getXY(int x, int y) {
		return tiles[xyToGrid(x, y)];
	}

	// ---------------------------------
	// Tile coordinate transformations
	// ---------------------------------
	public int xyToGrid(int x, int y) {
		return ((y) * width + (x));
	}

	public int gridToX(int gridNo) {
		return gridNo - ((gridNo / width) * width); // so... gridNo%width ?
	}

	public int gridToY(int gridNo) {
		return (gridNo / width);
	}
	
	// ---------------------------------

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	

}
