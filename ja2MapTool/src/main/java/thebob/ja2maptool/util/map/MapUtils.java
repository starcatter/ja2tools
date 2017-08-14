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
package thebob.ja2maptool.util.map;

import java.util.Arrays;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;

/**
 *
 * @author the_bob
 */
public class MapUtils {

    public static int screenXYtoCellX(double screenX, double screenY) {
        return (int) ((screenX + (2 * screenY) + 2) / 4);
    }

    public static int screenXYtoCellY(double screenX, double screenY) {
        return (int) (((2 * screenY) - screenX + 2) / 4);
    }

    public static int FromCellToScreenCoordinatesX(int sCellX, int sCellY) {
        return (2 * sCellX) - (2 * sCellY);
    }

    public static int FromCellToScreenCoordinatesY(int sCellX, int sCellY) {
        return sCellX + sCellY;
    }

    public static boolean isFloor(IndexedElement elem) {
	return elem.type == 60 || elem.type == 61 || elem.type == 62 || elem.type == 63;
    }

    public static boolean isWall(IndexedElement elem) {
	return elem.type == 36 || elem.type == 37 || elem.type == 38 || elem.type == 39;
    }
    
    public static boolean checkContentFilters(int layer, SelectedTiles selection, SelectionPlacementOptions options) {
	final int LAND_LAYER = 0;
	final int STRUCT_LAYER = 2;

	if (layer == LAND_LAYER && options.isPlace_land_floors()) {
	    System.out.println("thebob.ja2maptool.util.renderer.map.MapLayer.checkContentFilters() - leaving floors");

	    IndexedElement[][] remappedLayer = selection.getLayers()[layer];

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
	} else if (layer == STRUCT_LAYER && options.isPlace_structures_walls()) {
	    System.out.println("thebob.ja2maptool.util.renderer.map.MapLayer.checkContentFilters() - leaving walls");

	    IndexedElement[][] remappedLayer = selection.getLayers()[layer];

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
    
}
