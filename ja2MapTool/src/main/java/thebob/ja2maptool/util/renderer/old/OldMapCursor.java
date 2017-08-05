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
package thebob.ja2maptool.util.renderer.old;

import thebob.assetloader.map.core.components.IndexedElement;
import thebob.ja2maptool.util.renderer.base.TileLayerGroup;
import thebob.ja2maptool.util.renderer.old.OldMapRenderer;

/**
 *
 * @author the_bob
 */
public class OldMapCursor {

    OldMapRenderer parent = null;

    int cellX;
    int cellY;
    int cell;

    IndexedElement[] cursor = new IndexedElement[]{new IndexedElement(131, 14)};

    public OldMapCursor(OldMapCursor src, IndexedElement cursor) {
	this.parent = src.parent;

	this.cellX = src.cellX;
	this.cellY = src.cellY;
	this.cell = src.cell;

	this.cursor = new IndexedElement[]{cursor};
    }

    public OldMapCursor(OldMapRenderer parent, int x, int y, IndexedElement cursor) {
	this.parent = parent;
	cellX = x;
	cellY = y;

	cell = parent.mapRowColToPos(this.cellY, this.cellX);

	if (cursor != null) {
	    this.cursor = new IndexedElement[]{cursor};
	}
    }

    public int getCellX() {
	return cellX;
    }

    public int getCellY() {
	return cellY;
    }

    public int getCell() {
	return cell;
    }

    public IndexedElement[] getCursor() {
	return cursor;
    }

    
    
    @Override
    public String toString() {
	return "MapCursor{" + "cellX=" + cellX + ", cellY=" + cellY + '}';
    }

}
