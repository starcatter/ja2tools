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

import java.util.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Shadow;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.renderer.layers.cursor.MapCursor;
import thebob.ja2maptool.util.renderer.events.RendererEvent;
import thebob.ja2maptool.util.renderer.layers.placement.PlacementLayer;
import thebob.ja2maptool.util.renderer.layers.preview.PreviewLayer;

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

    SelectedTiles previewTiles = null;
    PreviewLayer previewLayer = null;
    PlacementLayer placementLayer = null;

    public DisplayManager() {
	super();

	previewLayer = new PreviewLayer();
	placementLayer = new PlacementLayer();

	renderer.addRenderLayer(map);
	renderer.addRenderOverlay(previewLayer, new OverlaySettings(0.65, 0, 0, null)); // new Glow(1d)) /// new Shadow(2d, Color.BLACK)
	renderer.addRenderOverlay(placementLayer, new OverlaySettings(1.0d, 0, 0, new Glow(1d))); // new Glow(1d)) /// new Shadow(2d, Color.BLACK)
	renderer.addRenderOverlay(cursors, new OverlaySettings(0.85, 0, 0, null)); // new Glow(1d) /// new Shadow(2d, Color.BLACK)
    }

    // -------------------
    @Override
    public void update(Observable o, Object arg
    ) {

	RendererEvent message = (RendererEvent) arg;

	if (message != null) {
	    switch (message.getType()) {
		case MAP_LOADED:
		    mapRows = map.getMapRows();
		    mapCols = map.getMapCols();
		    mapSize = map.getMapSize();

		    previewLayer.init(mapRows, mapCols, map.getTileset());
		    placementLayer.init(mapRows, mapCols, map.getTileset());
		    initCursorLayer();
		    break;
		case MAP_ALTERED:
		    break;
		case CURSOR_MOVED:
		    break;
		// --------------    
		case PLACEMENT_CURSOR_ADDED:
		    previewLayer.setPreview(previewTiles);
		    break;
		case PLACEMENT_CURSOR_REMOVED:
		    previewLayer.hidePreview();
		    break;
		case PLACEMENT_CURSOR_MOVED:
		    MapCursor placement = cursors.getPlacementCursor();
		    previewLayer.placePreview(placement);
		    break;

		case PLACEMENT_PICK:
		    pickPlacement();
		    break;

		case PLACEMENT_TOGGLE:
		    pinPlacement();
		    break;

		// --------------
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

	    }
	}
    }

    @Override
    public SelectedTiles getSelection() {
	SelectedTiles selection = cursors.getSelection();
	if (selection != null) {
	    map.getTilesForSelection(selection);
	}
	return selection;
    }

    @Override
    public void placeSelection(SelectedTiles selection, SelectionPlacementOptions options) {
	MapCursor placement = cursors.getPlacementCursor();
	if (previewLayer != null) {
	    previewLayer.hidePreview();
	}
	if (placement != null) {
	    map.appendTiles(placement, selection, options);
	}
	renderer.moveWindow(0, 0);
    }

    @Override
    public void setPlacementPreview(SelectedTiles selection) {
	previewTiles = selection;
	cursors.setPlacementPreview(selection);
	if (previewLayer != null) {
	    previewLayer.setPreview(previewTiles);
	}
    }

    @Override
    public void sendClick(double dx, double dy, MouseButton button, boolean controlDown, boolean shiftDown, boolean altDown) {
	cursors.sendClick(dx, dy, button, controlDown, shiftDown, altDown);
    }

    @Override
    public void sendCursor(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown) {
	cursors.sendCursor(dx, dy, controlDown, shiftDown, altDown);
    }

    private void initCursorLayer() {
	cursors.init(mapRows, mapCols, map.getTileset());
	cursors.setWindow(renderer.getWindowOffsetX(), renderer.getWindowOffsetY(), renderer.getScale());
	cursors.setCanvasSize(renderer.getCanvasX(), renderer.getCanvasY());
    }

    @Override
    public void setLayerButtons(BooleanProperty[] viewerButtons) {
	map.setLayerButtons(viewerButtons);
    }

    private void pinPlacement() {
	MapCursor placement = cursors.getPlacementCursor();
	if (placementLayer.togglePlacement(placement, previewTiles)) {
	    previewLayer.addPlacement(placement.getCell(), previewTiles);
	} else {
	    previewLayer.removePlacement(placement.getCell());
	}
    }

    private void pickPlacement() {
	MapCursor cursor = cursors.getMainCursor();
	if (cursor != null) {
	    if (placementLayer.pickPlacement(cursor)) {
		previewLayer.removePlacement(cursor.getCell());
	    }
	}
    }

}
