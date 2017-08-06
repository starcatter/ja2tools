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
package thebob.ja2maptool.util.renderer;

import thebob.ja2maptool.util.renderer.base.TileRenderer;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;
import javafx.beans.property.BooleanProperty;
import javafx.scene.canvas.Canvas;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.renderer.layers.cursor.CursorLayer;
import thebob.ja2maptool.util.renderer.layers.map.MapLayer;
import thebob.ja2maptool.util.renderer.base.ITileLayerGroup;
import thebob.ja2maptool.util.renderer.base.ITileRendererControls;
import thebob.ja2maptool.util.renderer.base.ITileRendererManager;
import thebob.ja2maptool.util.renderer.layers.cursor.ICursorLayerControls;
import thebob.ja2maptool.util.renderer.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.renderer.layers.map.IMapLayerControls;
import thebob.ja2maptool.util.renderer.layers.map.IMapLayerManager;

/**
 * Base class for DisplayManager, intended to patch through (delegate) access to all of its major components.
 * All of the interesting logic and state management should be in the DisplayManager class.
 * @author the_bob
 */
public abstract class DisplayManagerBase implements IMapDisplayManager, Observer {

    protected ITileRendererManager renderer = null;
    protected IMapLayerManager map = null;
    protected ICursorLayerManager cursors = null;

    DisplayManagerBase() {
	TileRenderer renderer = new TileRenderer();
	MapLayer map = new MapLayer();
	CursorLayer cursors = new CursorLayer();

	renderer.addObserver(this);
	map.subscribe(this);
	cursors.subscribe(this);
	
	this.renderer = renderer;
	this.map = map;
	this.cursors = cursors;
	
    }

    // -------------------
    @Override
    public void loadMap(MapData mapData) {
	map.loadMap(mapData);
    }
    
    @Override
    public void setCursorSize(int x, int y) {
	cursors.setCursorSize(x, y);
    }

    @Override
    public void hideCursor() {
	cursors.hideCursor();
    }

    @Override
    public void resetCursorSize() {
	cursors.resetCursorSize();
    }

    //

    @Override
    public void moveWindow(int x, int y) {
	renderer.moveWindow(x, y);
    }

    @Override
    public void setCanvas(Canvas canvas) {
	renderer.setCanvas(canvas);
    }

    @Override
    public double getScale() {
	return renderer.getScale();
    }
    
    @Override
    public void setScale(double scale) {
	renderer.setScale(scale);
    }

    @Override
    public void setWindowOffsetX(int oldX) {
	renderer.setWindowOffsetX(oldX);
    }

    @Override
    public void setWindowOffsetY(int oldY) {
	renderer.setWindowOffsetY(oldY);
    }

    @Override
    public int getWindowOffsetY() {
	return renderer.getWindowOffsetY();
    }

    @Override
    public int getWindowOffsetX() {
	return renderer.getWindowOffsetX();
    }

    // map
    
    @Override
    public void setMapTileset(Tileset tileset) {
	map.setMapTileset(tileset);
    }
    
    @Override
    public void setMapLayerButtons(BooleanProperty[] viewerButtons) {
	map.setMapLayerButtons(viewerButtons);
    }
    
}
