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
package thebob.ja2maptool.util.map.controller.editors.compositor;

import java.util.Observable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import thebob.ja2maptool.scopes.map.MapCompositorScope;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.map.MapEvent;
import thebob.ja2maptool.util.map.controller.base.MapControllerBase;
import thebob.ja2maptool.util.map.controller.cursor.BasicCursorController;
import thebob.ja2maptool.util.map.controller.cursor.PlacementCursorController;
import thebob.ja2maptool.util.map.controller.cursor.SelectionCursorController;
import thebob.ja2maptool.util.map.controller.cursor.base.IMapCursorController;
import thebob.ja2maptool.util.map.controller.placement.base.IMapPlacementController;
import thebob.ja2maptool.util.map.controller.placement.clipboard.IMapClipboardController;
import thebob.ja2maptool.util.map.controller.placement.clipboard.MapClipboardController;
import thebob.ja2maptool.util.map.controller.placement.snippets.IMapSnippetPlacementController;
import thebob.ja2maptool.util.map.controller.placement.snippets.MapSnippetPlacementController;
import thebob.ja2maptool.util.map.controller.selection.IMapSelectionController;
import thebob.ja2maptool.util.map.controller.selection.MapSelectionController;
import thebob.ja2maptool.util.map.layers.cursor.CursorLayer;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.layers.preview.PreviewLayer;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;
import thebob.ja2maptool.util.map.renderer.renderlayer.OverlaySettings;

/**
 *
 * @author the_bob
 */
public class MapCompositorController extends MapControllerBase implements IMapCompositorController {

    protected MapCompositorScope scope;

    // display layers
    protected ICursorLayerManager cursorLayer = new CursorLayer();
    protected PreviewLayer previewLayer = new PreviewLayer();

    // controllers
    protected IMapCursorController cursors = new BasicCursorController(renderer, map, cursorLayer);
    protected IMapSelectionController selection = new MapSelectionController(renderer, map, cursorLayer);
    protected IMapClipboardController clipboard = new MapClipboardController(renderer, map, cursorLayer, previewLayer, selection);
    protected IMapSnippetPlacementController placements = new MapSnippetPlacementController(renderer, map, cursorLayer, previewLayer);

    SelectedTiles preview = null;

    public MapCompositorController(ITileRendererManager renderer, IMapLayerManager map, MapCompositorScope compositor) {
        super(renderer, map);

        scope = compositor;

        cursorLayer.subscribe(this);

        renderer.addRenderOverlay(previewLayer, new OverlaySettings(0.65, 0, 0, null)); // new Glow(1d)) /// new Shadow(2d, Color.BLACK)
        renderer.addRenderOverlay(cursorLayer, new OverlaySettings(1.0d, 0, 0, null)); // new Glow(1d) /// new Shadow(2d, Color.BLACK)
    }

    // -- Event handler
    @Override
    public void update(Observable o, Object arg) {
        MapEvent message = (MapEvent) arg;

        if (message != null) {
            switch (message.getType()) {
                case MAP_LOADED:
                    cursorLayer.init(map.getMapRows(), map.getMapCols(), map.getTileset());
                    previewLayer.init(map.getMapRows(), map.getMapCols(), map.getTileset());
                    break;
            }
        }
    }

    @Override
    public SelectedTiles getSelection() {
        if (clipboard.canCopy()) {
            clipboard.copy();
            return clipboard.getContents();
        }
        return null;
    }

    @Override
    public void placeSelection(SelectedTiles selection, SelectionPlacementOptions options) {
        clipboard.setContents(selection);
        clipboard.paste(options);
    }

    @Override
    public void setPlacementPreview(SelectedTiles preview) {
        clipboard.setContents(preview);
        placements.setContents(preview);
        updateCursor();
    }

    @Override
    public void mouseEvent(MouseEvent e) {
        cursors.mouseEvent(e);
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (clipboard.hasContents()) {
            if (e.getEventType() == KeyEvent.KEY_PRESSED && e.getCode() == KeyCode.CONTROL) {
                cursors = new PlacementCursorController(renderer, map, cursorLayer, (IMapPlacementController) placements).transferStateFrom(cursors);
            } else {
                updateCursor();
            }
        } else if (e.getEventType() == KeyEvent.KEY_PRESSED && e.getCode() == KeyCode.SHIFT) {
            cursors = new SelectionCursorController(renderer, map, cursorLayer, selection).transferStateFrom(cursors);
        } else if (e.getEventType() == KeyEvent.KEY_RELEASED && (e.getCode() == KeyCode.SHIFT || e.getCode() == KeyCode.CONTROL)) {
            updateCursor();
        }

        cursors.keyEvent(e);
    }

    private void updateCursor() {
        if (clipboard.hasContents()) {
            cursors = new PlacementCursorController(renderer, map, cursorLayer, (IMapPlacementController) clipboard).transferStateFrom(cursors);
        } else {
            cursors = new BasicCursorController(renderer, map, cursorLayer).transferStateFrom(cursors);
        }
    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
