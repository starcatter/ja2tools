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
package thebob.ja2maptool.util.renderer.events;

/**
 *
 * @author the_bob
 */
public class RendererEvent {
    public enum ChangeType{
	// MapLayer events
	MAP_LOADED,
	MAP_ALTERED,
	// TileRenderer events
	MAP_WINDOW_MOVED,
	MAP_WINDOW_ZOOMED,
	MAP_CANVAS_CHANGED,
	// CursorLayer events
	CURSOR_MOVED, 
    }
    
    ChangeType type;

    public RendererEvent(ChangeType type) {
	this.type = type;
    }

    public ChangeType getType() {
	return type;
    }

    @Override
    public String toString() {
	return "TileLayerGroupChange{" + "type=" + type + '}';
    }
    
}
