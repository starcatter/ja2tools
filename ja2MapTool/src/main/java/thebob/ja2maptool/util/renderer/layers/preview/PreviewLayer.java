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
package thebob.ja2maptool.util.renderer.layers.preview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.renderer.base.TileLayer;
import thebob.ja2maptool.util.renderer.base.TileLayerGroup;
import thebob.ja2maptool.util.renderer.layers.cursor.MapCursor;

/**
 * TODO: decide if this layer needs interfaces, like map/cursor.
 *
 * So far it seems it should be internal to the renderer, so no real need for organizing access, but a *Manager interface might be nice regardless
 *
 * @author the_bob
 */
public class PreviewLayer extends TileLayerGroup {

    SelectedTiles previewTiles = null;
    MapCursor lastPlacement = null;
    List<TileLayer> layers = new ArrayList<>();

    @Override
    public Iterator<TileLayer> iterator() {
	return layers.iterator();
    }

    public SelectedTiles getPreviewTiles() {
	return previewTiles;
    }

    public void setPreviewTiles(SelectedTiles previewTiles) {
	this.previewTiles = previewTiles;
	if (lastPlacement != null && previewTiles != null) {
	    placePreview(lastPlacement);
	}
    }

    public PreviewLayer(SelectedTiles source, int mapCols, int mapRows) {
	this.previewTiles = source;
	setLayerSize(mapCols, mapRows);

	// create empty space	
	layers.add(new TileLayer(true, 0, 0, new IndexedElement[mapSize][0]));
	layers.add(new TileLayer(true, 0, 0, new IndexedElement[mapSize][0]));
	layers.add(new TileLayer(true, 0, 0, new IndexedElement[mapSize][0]));
	layers.add(new TileLayer(true, 0, 0, new IndexedElement[mapSize][0]));
	layers.add(new TileLayer(true, 0, -50, new IndexedElement[mapSize][0]));
	layers.add(new TileLayer(true, 0, -50, new IndexedElement[mapSize][0]));
    }

    public void hidePreview() {
	for (TileLayer layer : layers) {
	    layer.setTiles(new IndexedElement[mapSize][0]);
	}
    }

    public void placePreview(MapCursor placement) {
	lastPlacement = placement;
	// clear previous preview stuff

	for (TileLayer layer : layers) {
	    layer.setTiles(new IndexedElement[mapSize][0]);
	}

	short[] selectedRoomNumbers = previewTiles.getRoomNumbers();
	int selectionSize = selectedRoomNumbers.length;
	int[] targetCells = new int[selectionSize];

	int cursorWidth = previewTiles.getWidth();
	int cursorHeight = previewTiles.getHeight();

	int startX = placement.getCellX() - cursorWidth / 2;
	int startY = placement.getCellY() - cursorHeight / 2;

	for (int L = 0; L < layers.size(); L++) {

	    int i = 0;
	    for (int x = startX; x < startX + cursorWidth; x++) {
		for (int y = startY; y < startY + cursorHeight; y++) {
		    int targetCell = rowColToPos(y, x);
		    targetCells[i] = targetCell;

		    if (previewTiles.getLayers()[L][i] != null) {
			layers.get(L).getTiles()[targetCell] = previewTiles.getLayers()[L][i];
		    }

		    i++;
		}
	    }
	}

    }

}
