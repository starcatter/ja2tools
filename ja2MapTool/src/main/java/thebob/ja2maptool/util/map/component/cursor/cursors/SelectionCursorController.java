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
import static thebob.ja2maptool.util.map.layers.cursor.CursorLayer.LAYER_CURSOR;
import thebob.ja2maptool.util.map.component.selection.IMapSelectionComponent;

/**
 *
 * @author the_bob
 */
public class SelectionCursorController extends CursorControllerBase {

    private static final IndexedElement SELECTION_CURSOR = new IndexedElement(131, 15);
    private static final IndexedElement SELECTION_START_CURSOR = new IndexedElement(131, 20);
    private static final IndexedElement SELECTION_END_CURSOR = new IndexedElement(131, 19);

    IMapSelectionComponent selection;

    public SelectionCursorController(IMapSelectionComponent selection) {
        this.selection = selection;
    }

    @Override
    public void mouseEvent(MouseEvent e) {
        super.mouseEvent(e);
        if (e.getEventType() == MouseEvent.MOUSE_CLICKED || e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            if (e.getButton() == MouseButton.PRIMARY) {
                selection.placeMarker(getMouseCellX(), getMouseCellY());
            } else if (e.getButton() == MouseButton.SECONDARY) {
                selection.clearSelection();
            } else if (e.getButton() == MouseButton.MIDDLE) {
                // TODO: change selection mode
            }
            updateCursor();
        }
    }

    @Override
    public void updateCursor() {
        getCursors().clearLayer(LAYER_CURSOR);
        getCursors().placeCursor(LAYER_CURSOR, getMouseCell(), SELECTION_CURSOR);
        placeSelectionCursors();
    }

    private void placeSelectionCursors() {
        if (selection.getSelectionStart() != null) {
            getCursors().placeCursor(LAYER_CURSOR, selection.getSelectionStart(), SELECTION_START_CURSOR);
        }
        if (selection.getSelectionEnd() != null) {
            getCursors().placeCursor(LAYER_CURSOR, selection.getSelectionEnd(), SELECTION_END_CURSOR);
        }
    }

}
