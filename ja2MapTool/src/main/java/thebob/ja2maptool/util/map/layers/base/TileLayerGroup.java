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
package thebob.ja2maptool.util.map.layers.base;

import java.util.Observable;
import java.util.Observer;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.map.events.MapEvent;

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

    // ------------------------------
    // Tile coordinate transformation
    // ------------------------------
    
    @Override
    public int rowColToPos(int y, int x) {
	return ((y) * mapCols + (x));
    }
    
    @Override
    public int GridNoToCellX(int sGridNo) {
	int sYPos = (sGridNo / mapCols);
	int sXPos = sGridNo - (sYPos * mapCols);

	return sXPos;
    }

    @Override
    public int GridNoToCellY(int sGridNo) {
	int sYPos = (sGridNo / mapCols);

	return sYPos;
    }    

    protected void setLayerSize(int mapCols, int mapRows) {
	this.mapCols = mapCols;
	this.mapRows = mapRows;
	mapSize = mapCols * mapRows;
    }

    @Override
    public void init(int mapRows, int mapCols, Tileset tileset) {
        setLayerSize(mapCols, mapRows);
        setTileset(tileset);        
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
    // not sure why not just use the standard observable, considering we can't easily hide all the methods anyway
    @Override
    public <T extends MapEvent> void notifySubscribers(T message) {		
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

    @Override
    public String toString() {
	return "TileLayerGroup{" + "mapCols=" + mapCols + ", mapRows=" + mapRows + ", tileset=" + tileset + '}';
    }    
    
}
