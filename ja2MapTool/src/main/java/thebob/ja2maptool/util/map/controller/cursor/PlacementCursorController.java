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
package thebob.ja2maptool.util.map.controller.cursor;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.ja2maptool.util.map.controller.cursor.base.MapCursorControllerBase;
import thebob.ja2maptool.util.map.controller.placement.base.IMapPlacementController;
import thebob.ja2maptool.util.map.layers.cursor.CursorLayer;
import static thebob.ja2maptool.util.map.layers.cursor.CursorLayer.LAYER_CURSOR;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author the_bob
 */
public class PlacementCursorController extends MapCursorControllerBase {

    private static final IndexedElement PLACEMENT_CURSOR = new IndexedElement(131, 17);
    private static final IndexedElement PLACEMENT_CURSOR2 = new IndexedElement(131, 19);
    private static final IndexedElement PLACEMENT_TILES_CURSOR = new IndexedElement(131, 6);
    private static final IndexedElement PLACEMENT_TILES_CURSOR2 = new IndexedElement(131, 8);
    private final IMapPlacementController placementController;

    public PlacementCursorController(ITileRendererManager renderer, IMapLayerManager map, ICursorLayerManager cursors, IMapPlacementController placementController) {
        super(renderer, map, cursors);
        this.placementController = placementController;
    }

    @Override
    public void mouseEvent(MouseEvent e) {
        super.mouseEvent(e);
        if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (e.getButton() == MouseButton.PRIMARY) {
                placementController.setPlacement(cursors.getCursor(mouseCellX, mouseCellY, PLACEMENT_CURSOR));
            } else if (e.getButton() == MouseButton.SECONDARY) {
                placementController.setPlacement(null);
            }
            updateCursor();
        }
    }

    @Override
    protected void updateCursor() {
        cursors.clearLayer(LAYER_CURSOR);

        if (placementController.hoverPlacement(mouseCell)) {
            cursors.placeCursor(LAYER_CURSOR, mouseCell, PLACEMENT_CURSOR2);
        } else {
            cursors.placeCursor(LAYER_CURSOR, mouseCell, PLACEMENT_CURSOR);

            if (placementController.getPayload() != null) {
                int width = placementController.getPayload().getWidth();
                int height = placementController.getPayload().getHeight();
                cursors.placeCursorCenterRect(LAYER_CURSOR, mouseCellX, mouseCellY, width, height, PLACEMENT_TILES_CURSOR, CursorLayer.CursorFillMode.Border);
            }
        }

    }

}
