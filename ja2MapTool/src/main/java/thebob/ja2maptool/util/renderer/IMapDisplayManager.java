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

import javafx.beans.property.BooleanProperty;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.renderer.base.ITileRendererControls;
import thebob.ja2maptool.util.renderer.layers.cursor.ICursorLayerControls;
import thebob.ja2maptool.util.renderer.layers.map.IMapLayerControls;

/**
 * The interface that combines all of the map display functionality.
 *
 * @author the_bob
 */
public interface IMapDisplayManager extends ITileRendererControls, IMapLayerControls, ICursorLayerControls {

    // returns currently selected tiles
    SelectedTiles getSelection();

    // places the passed tiles at placement position
    void placeSelection(SelectedTiles selection, SelectionPlacementOptions options);

    // sets placement preview tiles
    public void setPlacementPreview(SelectedTiles selection);

    void sendClick(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown);

    void sendCursor(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown);

    public void setLayerButtons(BooleanProperty[] viewerButtons);

}
