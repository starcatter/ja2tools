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
