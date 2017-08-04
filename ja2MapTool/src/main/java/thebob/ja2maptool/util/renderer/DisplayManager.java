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

import java.util.Observable;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.renderer.cursor.MapCursor;
import thebob.ja2maptool.util.renderer.events.RendererEvent;

/**
 * DisplayManager is the replacement for OldMapRenderer, intended to structure its functionality a bit better.
 *
 * Currently its major components are: - the TileRenderer, responsible for moving around the view window and displaying stuff - the MapLayer, responsible for loading and manipulating the map data - the CursorLayer, responsible for generating the cursor overlay for the renderer
 *
 * @author the_bob
 */
public class DisplayManager extends DisplayManagerBase {

    int mapRows = -1;
    int mapCols = -1;
    int mapSize = -1;

    // -------------------
    @Override
    public void update(Observable o, Object arg) {

	RendererEvent message = (RendererEvent) arg;
	//System.out.println("thebob.ja2maptool.util.renderer.DisplayManager.update(): " + o + " / " + message);

	if (message != null) {
	    switch (message.getType()) {
		case MAP_LOADED:
		    mapRows = map.getMapRows();
		    mapCols = map.getMapCols();
		    mapSize = map.getMapSize();

		    initCursorLayer();
		    break;
		case MAP_ALTERED:
		    break;
		case CURSOR_MOVED:
		    break;

		case MAP_WINDOW_MOVED:
		    cursors.setWindow(renderer.getWindowOffsetX(), renderer.getWindowOffsetY(), renderer.getScale());
		    break;
		case MAP_WINDOW_ZOOMED:
		    cursors.setWindow(renderer.getWindowOffsetX(), renderer.getWindowOffsetY(), renderer.getScale());
		    break;
		case MAP_CANVAS_CHANGED:
		    cursors.setWindow(renderer.getWindowOffsetX(), renderer.getWindowOffsetY(), renderer.getScale());
		    cursors.setCanvasSize(renderer.getCanvasX(), renderer.getCanvasY());
		    break;
		default:
		    throw new AssertionError(message.getType().name());

	    }
	}
    }

    @Override
    public SelectedTiles getSelection() {
	SelectedTiles selection = cursors.getSelection();
	if (selection != null) {
	    System.out.println("thebob.ja2maptool.util.renderer.DisplayManager.getSelection() BEFORE :" + selection);

	    map.getTilesForSelection(selection);

	    System.out.println("thebob.ja2maptool.util.renderer.DisplayManager.getSelection() AFTER :" + selection);
	}
	return selection;
    }

    @Override
    public void placeSelection(SelectedTiles selection, SelectionPlacementOptions options) {
	MapCursor placement = cursors.getPlacementCursor();
	if (placement != null) {
	    map.appendTiles(placement, selection, options);
	}
    }

    @Override
    public void setPlacementPreview(SelectedTiles selection) {
	cursors.setPlacementPreview(selection);
    }

    @Override
    public void sendClick(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown) {
	cursors.sendClick(dx, dy, controlDown, shiftDown, altDown);
    }

    @Override
    public void sendCursor(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown) {
	cursors.sendCursor(dx, dy, controlDown, shiftDown, altDown);
    }

    private void initCursorLayer() {
	cursors.init(mapRows, mapCols, map.getTileset());
	cursors.setWindow(renderer.getWindowOffsetX(), renderer.getWindowOffsetY(), renderer.getScale());
	cursors.setCanvasSize(renderer.getCanvasX(), renderer.getCanvasY());
	cursors.setTileset(map.getTileset());
    }

}
