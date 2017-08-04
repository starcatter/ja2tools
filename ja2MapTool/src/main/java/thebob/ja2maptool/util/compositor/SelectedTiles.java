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
