/*
 * The MIT License
 *
 * Copyright 2017 the_bob.
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
package thebob.ja2maptool.util.renderer.layers.placement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.renderer.base.TileLayer;
import thebob.ja2maptool.util.renderer.base.TileLayerGroup;
import thebob.ja2maptool.util.renderer.layers.cursor.MapCursor;

/**
 *
 * @author the_bob
 */
public class PlacementLayer extends TileLayerGroup {

    Map<Integer, SelectedTiles> placementContents = new HashMap<Integer, SelectedTiles>();
    Map<Integer, MapCursor> placements = new HashMap<Integer, MapCursor>();
    Map<Integer, Integer> placementRadius = new HashMap<Integer, Integer>();

    private List<TileLayer> layers = new ArrayList<>();
    private IndexedElement[][] placementLayer = null;
    private TileLayer placementLayerWrapper = null;

    @Override
    public Iterator<TileLayer> iterator() {
	return layers.iterator();
    }

    public void init(int mapRows, int mapCols, Tileset tileset) {
	setLayerSize(mapCols, mapRows);
	setTileset(tileset);

	placementLayer = new IndexedElement[mapSize][0];
	placementLayerWrapper = new TileLayer(true, 0, 0, placementLayer);
	layers.clear();
	layers.add(placementLayerWrapper);
    }

    public boolean togglePlacement(MapCursor placement, SelectedTiles previewTiles) {
	int placementCell = placement.getCell();

	if (placements.containsKey(placementCell)) {
	    System.out.println("thebob.ja2maptool.util.renderer.layers.placement.PlacementLayer.pinPlacement(): unpin");
	    placements.remove(placementCell);
	    placementContents.remove(placementCell);
	    bakePlacementLayer();
	    return false;
	} else {
	    System.out.println("thebob.ja2maptool.util.renderer.layers.placement.PlacementLayer.pinPlacement(): pin");
	    placements.put(placementCell, placement);
	    placementContents.put(placementCell, previewTiles);
	    bakePlacementLayer();
	    return true;
	}

    }
    
    public SelectedTiles pickPlacement(MapCursor cursor) {
	if (placements.containsKey(cursor.getCell())) {
	    System.out.println("thebob.ja2maptool.util.renderer.layers.placement.PlacementLayer.pinPlacement(): unpin");
	    
	    placements.remove(cursor.getCell());
	    SelectedTiles placement = placementContents.remove(cursor.getCell());
	    
	    bakePlacementLayer();
	    return placement;
	}
	return null;
    }
    
    //

    private void initPlacementLayer() {
	for (int i = 0; i < mapSize; i++) {
	    placementLayer[i] = new IndexedElement[0];
	}
	placementLayerWrapper.setTiles(placementLayer);
    }

    private void bakePlacementLayer() {
	initPlacementLayer();

	if (placements.size() > 0) {
	    for (MapCursor cursor : placements.values()) {
		placementLayer[cursor.getCell()] = cursor.getCursor();
	    }
	}
    }

}
