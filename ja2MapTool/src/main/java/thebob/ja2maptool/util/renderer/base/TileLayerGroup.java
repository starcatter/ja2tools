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

import thebob.ja2maptool.util.renderer.events.RendererEvent;
import java.util.Observable;
import java.util.Observer;
import thebob.assetloader.tileset.Tileset;

/**
 * The TileLayerGroup is meant to be a wrapper around content displayed in the renderer - maps, cursors, previews, overlays.
 *
 * Layer groups are supposed to be supplying the content - the map layer getting it from map files while the cursor layer generates its content based on mouse input and whatever settings it gets.
 *
 * @author the_bob
 */
public abstract class TileLayerGroup extends Observable implements ITileLayerGroup {

    protected int mapCols;
    protected int mapRows;
    protected int mapSize;
    protected Tileset tileset;

    public int rowColToPos(int y, int x) {
	return ((y) * mapCols + (x));
    }

    protected void setLayerSize(int mapCols, int mapRows) {
	this.mapCols = mapCols;
	this.mapRows = mapRows;
	mapSize = mapCols * mapRows;
    }

    @Override
    public int getMapCols() {
	return mapCols;
    }

    @Override
    public int getMapRows() {
	return mapRows;
    }

    @Override
    public int getMapSize() {
	return mapSize;
    }

    @Override
    public Tileset getTileset() {
	return tileset;
    }

    @Override
    public void setTileset(Tileset tileset) {
	this.tileset = tileset;
    }

    // basic observable functionality
    @Override
    public <T extends RendererEvent> void notifySubscribers(T message) {		
	setChanged();
	notifyObservers(message);
    }

    @Override
    public synchronized void unsubscribe(Observer o) {
	deleteObserver(o);
    }

    @Override
    public synchronized void subscribe(Observer o) {
	addObserver(o);
    }

    
    
}
