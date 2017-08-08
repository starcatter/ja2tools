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
package thebob.ja2maptool.util.map.controller.compositor;

import thebob.ja2maptool.util.map.controller.base.MapControllerBase;
import java.util.Observable;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import thebob.ja2maptool.scopes.map.MapCompositorScope;
import static thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.MAP_LOADED;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.map.renderer.renderlayer.OverlaySettings;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;
import thebob.ja2maptool.util.map.MapEvent;
import static thebob.ja2maptool.util.map.MapEvent.ChangeType.CURSOR_MOVED;
import static thebob.ja2maptool.util.map.MapEvent.ChangeType.MAP_ALTERED;
import static thebob.ja2maptool.util.map.MapEvent.ChangeType.MAP_CANVAS_CHANGED;
import static thebob.ja2maptool.util.map.MapEvent.ChangeType.MAP_WINDOW_MOVED;
import static thebob.ja2maptool.util.map.MapEvent.ChangeType.MAP_WINDOW_ZOOMED;
import static thebob.ja2maptool.util.map.MapEvent.ChangeType.PLACEMENT_CURSOR_ADDED;
import static thebob.ja2maptool.util.map.MapEvent.ChangeType.PLACEMENT_CURSOR_MOVED;
import static thebob.ja2maptool.util.map.MapEvent.ChangeType.PLACEMENT_CURSOR_REMOVED;
import static thebob.ja2maptool.util.map.MapEvent.ChangeType.PLACEMENT_DELETE;
import static thebob.ja2maptool.util.map.MapEvent.ChangeType.PLACEMENT_PICK;
import static thebob.ja2maptool.util.map.MapEvent.ChangeType.PLACEMENT_TOGGLE;
import thebob.ja2maptool.util.map.layers.cursor.CursorLayer;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.cursor.MapCursor;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.layers.placement.PlacementLayer;
import thebob.ja2maptool.util.map.layers.preview.PreviewLayer;

/**
 *
 * @author the_bob
 */
public class MapCompositorController extends MapControllerBase implements IMapCompositorController {

    int mapRows = -1;
    int mapCols = -1;
    int mapSize = -1;

    MapCompositorScope scope;

    protected ICursorLayerManager cursors = new CursorLayer();
    protected PreviewLayer previewLayer = new PreviewLayer();
    protected PlacementLayer placementLayer = new PlacementLayer();
    
    SelectedTiles previewTiles = null;   

    public MapCompositorController(ITileRendererManager renderer, IMapLayerManager map, MapCompositorScope compositor) {
	super(renderer, map);

	scope = compositor;

	cursors.subscribe(this);

	renderer.addRenderOverlay(previewLayer, new OverlaySettings(0.65, 0, 0, null)); // new Glow(1d)) /// new Shadow(2d, Color.BLACK)
	renderer.addRenderOverlay(placementLayer, new OverlaySettings(1.0d, 0, 0, new Glow(1d))); // new Glow(1d)) /// new Shadow(2d, Color.BLACK)
	renderer.addRenderOverlay(cursors, new OverlaySettings(0.85, 0, 0, null)); // new Glow(1d) /// new Shadow(2d, Color.BLACK)
    }

    // -- Event handler
    @Override
    public void update(Observable o, Object arg) {
	MapEvent message = (MapEvent) arg;

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

		case PLACEMENT_DELETE:
		    deletePlacement();
		    break;

		// --------------
		case MAP_WINDOW_MOVED:
		    cursors.setWindow(renderer.getWindowOffsetX(), renderer.getWindowOffsetY(), renderer.getScale());
		    cursors.updateCursor();
		    break;
		case MAP_WINDOW_ZOOMED:
		    cursors.setWindow(renderer.getWindowOffsetX(), renderer.getWindowOffsetY(), renderer.getScale());
		    cursors.updateCursor();
		    break;
		case MAP_CANVAS_CHANGED:
		    cursors.setWindow(renderer.getWindowOffsetX(), renderer.getWindowOffsetY(), renderer.getScale());
		    cursors.setCanvasSize(renderer.getCanvasX(), renderer.getCanvasY());
		    cursors.updateCursor();
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
	// renderer.moveWindow(0, 0);  // <- renderer should update itself
    }

    @Override
    public void setPlacementPreview(SelectedTiles selection) {
	previewTiles = selection;
	cursors.setPlacementPreview(selection);
	previewLayer.setPreview(previewTiles);

	placementPicked = false; // reset this here in case the picked placement was switched
    }

    @Override
    public void mouseEvent(MouseEvent e) {
	if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
	    cursors.sendClick(e.getX(), e.getY(), e.getButton(), e.isControlDown(), e.isShiftDown(), e.isAltDown());
	} else if (e.getEventType() == MouseEvent.MOUSE_MOVED) {
	    cursors.sendCursor(e.getX(), e.getY(), e.isControlDown(), e.isShiftDown(), e.isAltDown());
	}
    }

    @Override
    public void keyEvent(KeyEvent e) {

    }

    private void initCursorLayer() {
	System.out.println("thebob.ja2maptool.util.map.controller.compositor.MapCompositorController.initCursorLayer()");
	cursors.init(mapRows, mapCols, map.getTileset());
	cursors.setWindow(renderer.getWindowOffsetX(), renderer.getWindowOffsetY(), renderer.getScale());
	cursors.setCanvasSize(renderer.getCanvasX(), renderer.getCanvasY());
    }

    boolean placementPicked = false;

    private void pinPlacement() {
	MapCursor placement = cursors.getMainCursor();
	if (placementLayer.togglePlacement(placement, previewTiles)) {
	    previewLayer.addPlacement(placement.getCell(), previewTiles);
	    if (placementPicked) {
		setPlacementPreview(null);
	    }
	} else {
	    previewLayer.removePlacement(placement.getCell());
	}
    }

    private void pickPlacement() {
	MapCursor cursor = cursors.getMainCursor();
	if (cursor != null) {
	    SelectedTiles placement = placementLayer.pickPlacement(cursor);
	    if (placement != null) {
		previewLayer.removePlacement(cursor.getCell());
		setPlacementPreview(placement);
		placementPicked = true;
	    }
	}
    }

    private void deletePlacement() {
	MapCursor cursor = cursors.getMainCursor();
	if (cursor != null) {
	    if (placementLayer.pickPlacement(cursor) != null) {
		previewLayer.removePlacement(cursor.getCell());
	    }
	}
    }

    @Override
    public void disconnect() {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
