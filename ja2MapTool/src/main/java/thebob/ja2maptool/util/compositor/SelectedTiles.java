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
import thebob.ja2maptool.scopes.map.ConvertMapScope;

/**
 *
 * @author the_bob
 */
public class SelectedTiles {

    String name = null;	// saved selections can be named :)
    int tilesetId;
    int width, height;

    // if there's a converter hooked up, this selection should be trnasformed before being handed off to the renderer
    ConvertMapScope converter = null;

    // used for moving selection between cursor and map layers
    int startX, endX, startCell;
    int startY, endY, endCell;

    short[] roomNumbers = null;

    int[] selectedCells = null;	// cell numbers of where selection cursors were placed
    IndexedElement[][][] layers = null;	// cell layers from the map layer

    // deep copy constructor for remapping selection contents
    public SelectedTiles(SelectedTiles source) {
	this.name = source.name;
        
	this.tilesetId = source.tilesetId;
	this.width = source.width;
	this.height = source.height;

	this.startX = source.startX;
	this.endX = source.endX;
	this.startCell = source.startCell;
	this.startY = source.startY;
	this.endY = source.endY;
	this.endCell = source.endCell;

	this.roomNumbers = source.roomNumbers; // no need to deep-copy room numbers since they're not going to be changed at any point, and even if they did, they are remapped on the fly.

	int selectionSize = source.layers[0].length;
	layers = new IndexedElement[6][selectionSize][];
	for (int i = 0; i < 6; i++) {
	    for (int j = 0; j < selectionSize; j++) {
		layers[i][j] = new IndexedElement[source.getLayers()[i][j].length];
		for (int k = 0; k < source.getLayers()[i][j].length; k++) {
		    layers[i][j][k] = new IndexedElement(source.getLayers()[i][j][k].type, source.getLayers()[i][j][k].index);
		}
	    }
	}
    }

    public SelectedTiles(int tilesetId, int width, int height, IndexedElement[][][] layers, short[] roomNumbers) {
	this.tilesetId = tilesetId;
	this.width = width;
	this.height = height;
	this.layers = layers;
	this.roomNumbers = roomNumbers;
    }

    public SelectedTiles(int tilesetId, int startX, int endX, int startCell, int startY, int endY, int endCell) {
	this.tilesetId = tilesetId;
	this.startX = startX;
	this.endX = endX;
	this.startCell = startCell;
	this.startY = startY;
	this.endY = endY;
	this.endCell = endCell;

	width = 1 + endX - startX;
	height = 1 + endY - startY;
    }

    public int getTilesetId() {
	return tilesetId;
    }

    public int getStartX() {
	return startX;
    }

    public int getEndX() {
	return endX;
    }

    public int getStartCell() {
	return startCell;
    }

    public int getStartY() {
	return startY;
    }

    public int getEndY() {
	return endY;
    }

    public int getEndCell() {
	return endCell;
    }

    public IndexedElement[][][] getLayers() {
	return layers;
    }

    public void setLayers(IndexedElement[][][] layers) {
	this.layers = layers;
    }

    public short[] getRoomNumbers() {
	return roomNumbers;
    }

    public void setRoomNumbers(short[] roomNumbers) {
	this.roomNumbers = roomNumbers;
    }

    public void setSelectedCells(int[] selectedCells) {
	this.selectedCells = selectedCells;
    }

    public int[] getSelectedCells() {
	return selectedCells;
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public ConvertMapScope getConverter() {
	return converter;
    }

    public void setConverter(ConvertMapScope converter) {
	this.converter = converter;
    }

    @Override
    public String toString() {
	return "SelectedTiles{ layers=" + (layers != null ? layers.length : "not set") + ", width=" + width + ", height=" + height + ", converter=" + (converter != null) + "}";
    }

}
