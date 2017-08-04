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
package thebob.ja2maptool.util.renderer.base;

import java.util.Iterator;
import thebob.assetloader.map.core.components.IndexedElement;

/**
 *
 * @author the_bob
 */
public class TileLayer {

    boolean enabled;
    int displayOffsetX;
    int displayOffsetY;
    
    IndexedElement tiles[][];

    public TileLayer(boolean displayLayer, int displayOffsetX, int displayOffsetY, IndexedElement[][] tiles) {
	this.enabled = displayLayer;
	this.displayOffsetX = displayOffsetX;
	this.displayOffsetY = displayOffsetY;
	this.tiles = tiles;
    }

    public boolean isEnabled() {
	return enabled;
    }

    public void setEnabled(boolean enabled) {
	this.enabled = enabled;
    }

    public int getDisplayOffsetX() {
	return displayOffsetX;
    }

    public void setDisplayOffsetX(int displayOffsetX) {
	this.displayOffsetX = displayOffsetX;
    }

    public int getDisplayOffsetY() {
	return displayOffsetY;
    }

    public void setDisplayOffsetY(int displayOffsetY) {
	this.displayOffsetY = displayOffsetY;
    }

    public IndexedElement[][] getTiles() {
	return tiles;
    }

    public void setTiles(IndexedElement[][] tiles) {
	this.tiles = tiles;
    }
        
}
