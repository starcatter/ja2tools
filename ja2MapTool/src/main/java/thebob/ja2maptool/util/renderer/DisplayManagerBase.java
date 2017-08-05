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
package thebob.ja2maptool.util.renderer;

import thebob.ja2maptool.util.renderer.base.TileRenderer;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;
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

	renderer.addRenderLayer(map);
	renderer.addRenderLayer(cursors);
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
    public void updateAuxCursorDisplay() {
	cursors.updateAuxCursorDisplay();
    }

    @Override
    public void hideCursor() {
	cursors.hideCursor();
    }

    @Override
    public void resetCursorSize() {
	cursors.resetCursorSize();
    }

    @Override
    public double getScale() {
	return renderer.getScale();
    }

    @Override
    public void moveWindow(int x, int y) {
	renderer.moveWindow(x, y);
    }

    @Override
    public void setCanvas(Canvas canvas) {
	renderer.setCanvas(canvas);
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

    @Override
    public void setMapTileset(Tileset tileset) {
	map.setMapTileset(tileset);
    }
}
