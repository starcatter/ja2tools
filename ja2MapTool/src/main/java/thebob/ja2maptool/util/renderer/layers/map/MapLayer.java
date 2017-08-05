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
package thebob.ja2maptool.util.renderer.layers.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.renderer.base.TileLayer;
import thebob.ja2maptool.util.renderer.base.TileLayerGroup;
import thebob.ja2maptool.util.renderer.layers.cursor.MapCursor;
import thebob.ja2maptool.util.renderer.events.RendererEvent;

/**
 *
 * @author the_bob
 */
public class MapLayer extends TileLayerGroup implements IMapLayerManager {

    MapData map;
    List<TileLayer> layers = new ArrayList<>();

    @Override
    public Iterator<TileLayer> iterator() {
	return layers.iterator();
    }

    @Override
    public void loadMap(MapData map) {
	setLayerSize(map.getSettings().iColSize, map.getSettings().iRowSize);
	loadMapLayers(map);
	notifySubscribers(new RendererEvent(RendererEvent.ChangeType.MAP_LOADED));
    }

    private void loadMapLayers(MapData map) {
	layers.clear();
	this.map = map;

	layers.add(new TileLayer(true, 0, 0, map.getLayers().landLayer));
	layers.add(new TileLayer(true, 0, 0, map.getLayers().objectLayer));
	layers.add(new TileLayer(true, 0, 0, map.getLayers().structLayer));
	layers.add(new TileLayer(true, 0, 0, map.getLayers().shadowLayer));
	layers.add(new TileLayer(false, 0, -50, map.getLayers().roofLayer));
	layers.add(new TileLayer(false, 0, -50, map.getLayers().onRoofLayer));
	
	bindLayerButtons();
    }

    @Override
    public void setMapTileset(Tileset tileset) {
	setTileset(tileset);
    }

    @Override
    public SelectedTiles getTilesForSelection(SelectedTiles selectedTiles) {

	int[] selectedCells = selectedTiles.getSelectedCells();
	int selectionSize = selectedCells.length;

	IndexedElement[][][] selectionLayers = new IndexedElement[layers.size()][selectionSize][];

	for (int L = 0; L < layers.size(); L++) {
	    TileLayer layer = layers.get(L);
	    IndexedElement[][] mapLayer = layer.getTiles();
	    for (int i = 0; i < selectionSize; i++) {
		selectionLayers[L][i] = mapLayer[selectedCells[i]];
	    }
	}

	short[] selectedRoomNumbers = new short[selectionSize];
	short[] mapRoomNunbers = map.getInfo().getGusWorldRoomInfo();
	for (int i = 0; i < selectionSize; i++) {
	    selectedRoomNumbers[i] = mapRoomNunbers[selectedCells[i]];
	}

	selectedTiles.setLayers(selectionLayers);
	selectedTiles.setRoomNumbers(selectedRoomNumbers);

	selectedTiles.setName("Selection, " + selectionSize + " cells.");

	return selectedTiles;
    }

    Short lastRoomNumber = null;

    private void scanRoomNumbers() {
	lastRoomNumber = Short.MIN_VALUE;
	short[] mapRoomNunbers = map.getInfo().getGusWorldRoomInfo();
	for (short n : mapRoomNunbers) {
	    if (lastRoomNumber < n) {
		lastRoomNumber = n;
		System.out.println("thebob.ja2maptool.util.renderer.map.MapLayer.scanRoomNumbers(): " + n);
	    }
	}
    }

    boolean isFloor(IndexedElement elem) {
	return elem.type == 60 || elem.type == 61 || elem.type == 62 || elem.type == 63;
    }

    boolean isWall(IndexedElement elem) {
	return elem.type == 36 || elem.type == 37 || elem.type == 38 || elem.type == 39;
    }

    boolean checkContentFilters(int L, SelectedTiles selection, SelectionPlacementOptions options) {
	final int LAND_LAYER = 0;
	final int STRUCT_LAYER = 2;

	if (L == LAND_LAYER && options.isPlace_land_floors()) {
	    System.out.println("thebob.ja2maptool.util.renderer.map.MapLayer.checkContentFilters() - leaving floors");

	    IndexedElement[][] remappedLayer = selection.getLayers()[L];

	    for (int j = 0; j < remappedLayer.length; j++) {
		IndexedElement[] tiles = remappedLayer[j];

		int savedTiles = 0;
		IndexedElement[] savedList = new IndexedElement[16];

		for (IndexedElement elem : tiles) {
		    if (isFloor(elem)) {
			savedList[savedTiles++] = elem;
		    }
		}

		if (savedTiles > 0) {
		    remappedLayer[j] = Arrays.copyOfRange(savedList, 0, savedTiles);
		} else {
		    remappedLayer[j] = null;
		}
	    }

	    return true;
	} else if (L == STRUCT_LAYER && options.isPlace_structures_walls()) {
	    System.out.println("thebob.ja2maptool.util.renderer.map.MapLayer.checkContentFilters() - leaving walls");

	    IndexedElement[][] remappedLayer = selection.getLayers()[L];

	    for (int j = 0; j < remappedLayer.length; j++) {
		IndexedElement[] tiles = remappedLayer[j];

		int savedTiles = 0;
		IndexedElement[] savedList = new IndexedElement[16];

		for (IndexedElement elem : tiles) {
		    if (isWall(elem)) {
			savedList[savedTiles++] = elem;
		    }
		}

		if (savedTiles > 0) {
		    remappedLayer[j] = Arrays.copyOfRange(savedList, 0, savedTiles);
		} else {
		    remappedLayer[j] = null;
		}
	    }

	    return true;
	} else {
	    return false;
	}
    }

    @Override
    public void appendTiles(MapCursor placement, SelectedTiles inputSelection, SelectionPlacementOptions options) {

	// clone this snippet in case we... do things to it
	SelectedTiles selection = new SelectedTiles(inputSelection);

	if (lastRoomNumber == null) {
	    scanRoomNumbers();
	}

	short[] selectedRoomNumbers = selection.getRoomNumbers();
	int selectionSize = selectedRoomNumbers.length;
	int[] targetCells = new int[selectionSize];

	int cursorWidth = selection.getWidth();
	int cursorHeight = selection.getHeight();

	int startX = placement.getCellX() - cursorWidth / 2;
	int startY = placement.getCellY() - cursorHeight / 2;

	boolean[] placementOptions = options.getAsArray();

	for (int L = 0; L < layers.size(); L++) {

	    if (placementOptions[L] == false) {
		if (checkContentFilters(L, selection, options) == false) {
		    continue;
		}
	    }

	    int i = 0;
	    for (int x = startX; x < startX + cursorWidth; x++) {
		for (int y = startY; y < startY + cursorHeight; y++) {
		    int targetCell = rowColToPos(y, x);
		    targetCells[i] = targetCell;

		    if (selection.getLayers()[L][i] != null) {
			layers.get(L).getTiles()[targetCell] = selection.getLayers()[L][i];
		    }

		    i++;
		}
	    }
	}

	// update room numbers in placed stuff
	short[] mapRoomNunbers = map.getInfo().getGusWorldRoomInfo();
	Map<Short, Short> numbersRemap = new HashMap<Short, Short>();

	for (int i = 0; i < selectionSize; i++) {
	    short selectedRoomNumber = selectedRoomNumbers[i];

	    if (selectedRoomNumber != 0) {
		if (numbersRemap.containsKey(selectedRoomNumber)) {
		    selectedRoomNumber = numbersRemap.get(selectedRoomNumber);
		} else {
		    numbersRemap.put(selectedRoomNumber, ++lastRoomNumber);
		    System.out.println("thebob.ja2maptool.util.renderer.map.MapLayer.appendTiles() mapped room nr " + selectedRoomNumber + " to " + lastRoomNumber);
		    selectedRoomNumber = lastRoomNumber;
		}
	    }

	    mapRoomNunbers[targetCells[i]] = selectedRoomNumber;
	}

    }

    BooleanProperty[] viewerButtons = null;

    @Override
    public void setLayerButtons(BooleanProperty[] viewerButtons) {
	this.viewerButtons = viewerButtons;
	bindLayerButtons();
    }

    void bindLayerButtons() {
	if (viewerButtons != null) {
	    for (int i = 0; i < layers.size(); i++) {
		viewerButtons[i].set( layers.get(i).isEnabled() );
		layers.get(i).getEnabledProperty().bindBidirectional(viewerButtons[i]);
	    }
	}
    }
;

}
