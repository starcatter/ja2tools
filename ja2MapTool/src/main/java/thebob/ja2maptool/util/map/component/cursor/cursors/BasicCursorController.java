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

import thebob.assetloader.map.core.components.IndexedElement;
import static thebob.ja2maptool.util.map.MapUtils.screenXYtoCellX;
import static thebob.ja2maptool.util.map.MapUtils.screenXYtoCellY;
import thebob.ja2maptool.util.map.component.cursor.cursors.base.CursorControllerBase;
import static thebob.ja2maptool.util.map.layers.cursor.CursorLayer.LAYER_CURSOR;

/**
 * The basic cursor - simple square that follows the mouse + markers placed in
 * screen corners. The markers are there to verify the cursor code didn't go
 * crazy.
 *
 * @author the_bob
 */
public class BasicCursorController extends CursorControllerBase {

    private static final IndexedElement STD_CURSOR = new IndexedElement(131, 1);
    private static final IndexedElement VIEW_EDGE_TILES_CURSOR = new IndexedElement(131, 8);

    private void updateCorners() {
        double scaledCanvasX = getRenderer().getCanvasX() / getRenderer().getScale();
        double scaledCanvasY = getRenderer().getCanvasY() / getRenderer().getScale();

        int width = (int) (scaledCanvasX / 10);
        int height = (int) (scaledCanvasY / 10);

        int sX1 = getWindowScreenX() + 4;
        int sY1 = getWindowScreenY() + 2;
        int sX2 = sX1 + width - 3;
        int sY2 = sY1 + height - 1;
        
        getCursors().placeCursor(LAYER_CURSOR, screenXYtoCellX(sX1, sY1), screenXYtoCellY(sX1, sY1), VIEW_EDGE_TILES_CURSOR);
        getCursors().placeCursor(LAYER_CURSOR, screenXYtoCellX(sX1, sY2), screenXYtoCellY(sX1, sY2), VIEW_EDGE_TILES_CURSOR);
        getCursors().placeCursor(LAYER_CURSOR, screenXYtoCellX(sX2, sY2), screenXYtoCellY(sX2, sY2), VIEW_EDGE_TILES_CURSOR);
        getCursors().placeCursor(LAYER_CURSOR, screenXYtoCellX(sX2, sY1), screenXYtoCellY(sX2, sY1), VIEW_EDGE_TILES_CURSOR);
    }

    @Override
    public void updateCursor() {
        getCursors().clearLayer(LAYER_CURSOR);
        getCursors().placeCursor(LAYER_CURSOR, getMouseCell(), STD_CURSOR);
        updateCorners();
    }

}
