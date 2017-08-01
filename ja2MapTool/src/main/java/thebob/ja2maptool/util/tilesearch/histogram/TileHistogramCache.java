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
package thebob.ja2maptool.util.tilesearch.histogram;

import java.util.HashMap;
import java.util.Map;
import thebob.assetloader.tileset.Tile;

/**
 *
 * @author the_bob
 */
public class TileHistogramCache {

    Map<Integer, double[][]> tileHistograms = new HashMap<Integer, double[][]>();
    Map<Integer, Tile> tiles = new HashMap<Integer, Tile>();

    public TileHistogramCache(Tile[] tiles, int histogramSize) {
	
	for (Tile tile : tiles) {
	    tileHistograms.put(tile.getIndex(), TileHistogramComparator.getHistogram(tile, histogramSize));
	    this.tiles.put(tile.getIndex(),tile);
	}
    }

    public Map<Integer, double[][]> getHistograms() {
	return tileHistograms;
    }

    public Tile getTile(int index) {
	return tiles.get(index);
    }
}
