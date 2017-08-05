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
import javafx.scene.paint.Color;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.renderer.layers.cursor.MapCursor;
import thebob.ja2maptool.util.renderer.events.RendererEvent;
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

    // -------------------
    @Override
    public void update(Observable o, Object arg) {

	RendererEvent message = (RendererEvent) arg;

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
		// --------------    
		case PLACEMENT_CURSOR_ADDED:
		    previewLayer = new PreviewLayer(previewTiles, map.getMapRows(), map.getMapCols());
		    previewLayer.setTileset(map.getTileset());
		    renderer.addRenderOverlay(previewLayer, new OverlaySettings(0.5, 0, 0, null) ); // new Glow(1d)) /// new Shadow(2d, Color.BLACK)
		    break;
		case PLACEMENT_CURSOR_REMOVED:
		    renderer.removeRenderLayer(previewLayer);
		    previewLayer = null;
		    break;
		case PLACEMENT_CURSOR_MOVED:
		    MapCursor placement = cursors.getPlacementCursor();
		    previewLayer.placePreview(placement);
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
		default:
		    throw new AssertionError(message.getType().name());

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
	if( previewLayer != null ){
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
	if( previewLayer != null ){
	    previewLayer.setPreviewTiles(previewTiles);
	}
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

    @Override
    public void setLayerButtons(BooleanProperty[] viewerButtons) {
	map.setLayerButtons(viewerButtons);
    }

}
