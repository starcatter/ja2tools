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
package thebob.ja2maptool.util.map.component.cursor.cursors;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.ja2maptool.util.map.component.cursor.cursors.base.CursorControllerBase;
import thebob.ja2maptool.util.map.component.placement.base.IMapPlacementComponent;
import thebob.ja2maptool.util.map.layers.cursor.CursorLayer;
import static thebob.ja2maptool.util.map.layers.cursor.CursorLayer.LAYER_CURSOR;

/**
 *
 * @author the_bob
 */
public class PlacementCursorController extends CursorControllerBase {

    private static final IndexedElement PLACEMENT_CURSOR = new IndexedElement(131, 17);
    private static final IndexedElement PLACEMENT_CURSOR_ACTIVE = new IndexedElement(131, 19);
    private static final IndexedElement PLACEMENT_TILES_CURSOR = new IndexedElement(131, 6);
    private static final IndexedElement PLACEMENT_CORNERS_CURSOR = new IndexedElement(131, 8);
    private static final IndexedElement PLACEMENT_MARKERS_CURSOR = new IndexedElement(131, 12);
    private final IMapPlacementComponent placementController;

    public PlacementCursorController(IMapPlacementComponent placementController) {
        this.placementController = placementController;
    }

    @Override
    public void mouseEvent(MouseEvent e) {
        super.mouseEvent(e);
        if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (e.getButton() == MouseButton.PRIMARY) {
                placementController.setPlacementLocation(getCursors().getCursor(getMouseCellX(), getMouseCellY(), PLACEMENT_CURSOR));
            } else if (e.getButton() == MouseButton.SECONDARY) {
                placementController.setPlacementLocation(null);
            }
            updateCursor();
        }
    }

    @Override
    public void updateCursor() {
        getCursors().clearLayer(LAYER_CURSOR);

        if (placementController.getPayload() != null) {
            int width = placementController.getPayload().getWidth();
            int height = placementController.getPayload().getHeight();

            Integer placementCell = placementController.hoverPlacement(getMouseCell());
            if (placementCell != null) {    // different cursor for hovering over a placement
                getCursors().placeCursor(LAYER_CURSOR, getMouseCell(), PLACEMENT_CURSOR_ACTIVE);
            } else {
                getCursors().placeCursorCenterRect(LAYER_CURSOR, getMouseCellX(), getMouseCellY(), width, height, PLACEMENT_TILES_CURSOR, CursorLayer.CursorFillMode.Border);
                getCursors().placeCursorCenterRect(LAYER_CURSOR, getMouseCellX(), getMouseCellY(), width + 2, height + 2, PLACEMENT_CORNERS_CURSOR, CursorLayer.CursorFillMode.Corners);
            }
        } else {
            getCursors().placeCursor(LAYER_CURSOR, getMouseCell(), PLACEMENT_CURSOR);
        }

    }

}
