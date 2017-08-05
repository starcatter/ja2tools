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
package thebob.ja2maptool.util.renderer.base;

import java.util.Iterator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import thebob.assetloader.map.core.components.IndexedElement;

/**
 *
 * @author the_bob
 */
public class TileLayer {

    BooleanProperty enabled = new SimpleBooleanProperty(true);
    int displayOffsetX;
    int displayOffsetY;
    
    IndexedElement tiles[][];

    public TileLayer(boolean displayLayer, int displayOffsetX, int displayOffsetY, IndexedElement[][] tiles) {
	this.enabled.set(displayLayer);
	this.displayOffsetX = displayOffsetX;
	this.displayOffsetY = displayOffsetY;
	this.tiles = tiles;
    }

    public BooleanProperty getEnabledProperty() {
	return enabled;
    }
    
    
    public boolean isEnabled() {
	return enabled.get();
    }

    public void setEnabled(boolean enabled) {
	this.enabled.set(enabled);
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
