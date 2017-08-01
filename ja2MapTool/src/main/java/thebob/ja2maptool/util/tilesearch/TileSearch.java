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
package thebob.ja2maptool.util.tilesearch;

import thebob.assetloader.tileset.Tile;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.tilesearch.histogram.TileHistogramComparator;

/**
 *
 * @author the_bob
 */
public class TileSearch {
    
    Tileset tileset;
    int files;
    SearchMethod method;
    TileComparator comparator;

    public TileSearch(Tileset tileset, SearchMethod method) {
	this.tileset = tileset;
	this.files = tileset.getFileCount();
	
	switch(method){
	    case Simple:
		break;
	    case HistogramSimple:
		comparator = new TileHistogramComparator(tileset, TileHistogramComparator.ComparatorType.Simple, 32);
		break;
	    case HistogramEMD:
		comparator = new TileHistogramComparator(tileset, TileHistogramComparator.ComparatorType.EMD, 8);
		break;
	    default:
		throw new AssertionError(method.name());
	
	}
    }

    public TileSearchResult tileSearch(Tile source) {
	return comparator.search(source);
    }
    
    public enum SearchMethod{
	Simple,
	HistogramSimple,
	HistogramEMD,
    }

}
