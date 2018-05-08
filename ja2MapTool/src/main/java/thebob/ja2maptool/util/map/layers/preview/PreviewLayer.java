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
package thebob.ja2maptool.util.map.layers.preview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.compositor.SnippetPlacement;
import static thebob.ja2maptool.util.map.MapUtils.checkContentFilters;
import thebob.ja2maptool.util.map.events.MapEvent;
import thebob.ja2maptool.util.map.layers.base.TileLayer;
import thebob.ja2maptool.util.map.layers.base.TileLayerGroup;
import thebob.ja2maptool.util.map.layers.cursor.MapCursor;

/**
 * TODO: decide if this layer needs interfaces, like map/cursor.
 *
 * So far it seems it should be internal to the renderer, so no real need for
 * organizing access, but a *Manager interface might be nice regardless
 *
 * @author the_bob
 */
public class PreviewLayer extends TileLayerGroup {

    SnippetPlacement previewTiles = null;
    MapCursor lastPlacement = null;

    List<TileLayer> layers = new ArrayList<>();
    Map<Integer, SnippetPlacement> placements = new HashMap<Integer, SnippetPlacement>();

    @Override
    public Iterator<TileLayer> iterator() {
        return layers.iterator();
    }

    public PreviewLayer() {
    }

    public void init(int mapRows, int mapCols, Tileset tileset) {
        setLayerSize(mapCols, mapRows);
        setTileset(tileset);

        layers.clear();
        // create empty space	
        layers.add(new TileLayer(true, 0, 0, new IndexedElement[mapSize][0]));
        layers.add(new TileLayer(true, 0, 0, new IndexedElement[mapSize][0]));
        layers.add(new TileLayer(true, 0, 0, new IndexedElement[mapSize][0]));
        layers.add(new TileLayer(true, 0, 0, new IndexedElement[mapSize][0]));
        layers.add(new TileLayer(true, 0, -50, new IndexedElement[mapSize][0]));
        layers.add(new TileLayer(true, 0, -50, new IndexedElement[mapSize][0]));
    }

    @Override
    public boolean limitDrawArea() {
        return true;
    }

    @Override
    public boolean trimEdges() {
        return true;
    }

    @Override
    public void setBatchMode(boolean batchMode) {
        this.batchMode = batchMode;
        if (batchMode == false) {
            bakePreviewLayer();
        }
        super.setBatchMode(batchMode);
    }

    public void hidePreview() {
        /*
	for (TileLayer layer : layers) {
	    layer.setTiles(new IndexedElement[mapSize][0]);
	}*/

        previewTiles = null;
        lastPlacement = null;
    }

    // manipulation methods
    /**
     * Adds a persistent placement
     *
     * @param cell
     * @param tiles
     */
    public void addPlacement(int cell, SnippetPlacement tiles) {
        placements.put(cell, tiles);
        bakePreviewLayer();
    }

    public void removePlacement(int cell) {
        placements.remove(cell);
        bakePreviewLayer();
    }

    /**
     * sets the preview contents
     *
     * @param previewTiles
     */
    public void setPreview(SelectedTiles previewTiles) {
        if (previewTiles != null) {
            this.previewTiles = new SnippetPlacement(0, 0, 0, previewTiles, null);
        } else {
            this.previewTiles = null;
        }

        bakePreviewLayer();
    }

    /**
     * sets the preview location
     *
     * @param placement
     */
    public void placePreview(MapCursor placement) {
        lastPlacement = placement;
        bakePreviewLayer();
    }

    // -----------------
    // "rendering" methods
    private void bakePlacements() {
        for (int cell : placements.keySet()) {
            addPreviewToLayers(placements.get(cell), cell);
        }
    }

    private void bakePreview() {
        addPreviewToLayers(previewTiles, lastPlacement.getCell());
    }

    private void bakePreviewLayer() {
        if (batchMode) {
            return;
        }

        for (TileLayer layer : layers) {
            layer.setTiles(new IndexedElement[mapSize][0]);
        }

        if (lastPlacement != null && previewTiles != null) {
            bakePreview();
        }

        if (placements.size() > 0) {
            bakePlacements();
        }

        notifySubscribers(new MapEvent(MapEvent.ChangeType.LAYER_ALTERED));
    }

    private void addPreviewToLayers(SnippetPlacement previewTilesPlacement, int cell) {
        SelectedTiles previewTiles = previewTilesPlacement.getSnippet();
        int cellX = gridNoToCellX(cell);
        int cellY = gridNoToCellY(cell);

        int cursorWidth = previewTiles.getWidth();
        int cursorHeight = previewTiles.getHeight();

        int selectionSize = cursorWidth * cursorHeight;
        int[] targetCells = new int[selectionSize];

        int startX = cellX - cursorWidth / 2;
        int startY = cellY - cursorHeight / 2;

        if (previewTilesPlacement.getEnabledLayers() != null) {

            SelectedTiles previewTilesCopy = new SelectedTiles(previewTiles);

            SelectionPlacementOptions options = previewTilesPlacement.getEnabledLayers();
            boolean[] placementOptions = options.getAsArray();

            for (int L = 0; L < layers.size(); L++) {

                if (placementOptions[L] == false) {
                    if (checkContentFilters(L, previewTilesCopy, options) == false) {
                        continue;
                    }
                }

                int i = 0;
                for (int x = startX; x < startX + cursorWidth; x++) {
                    for (int y = startY; y < startY + cursorHeight; y++) {
                        int targetCell = rowColToPos(y, x);
                        targetCells[i] = targetCell;

                        if (previewTilesCopy.getLayers()[L][i] != null) {
                            layers.get(L).getTiles()[targetCell] = previewTilesCopy.getLayers()[L][i];
                        }

                        i++;
                    }
                }
            }

        } else {
            // simply paste the layers over
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

    @Override
    public String toString() {
        return "PreviewLayer{" + super.toString() + '}';
    }

}
