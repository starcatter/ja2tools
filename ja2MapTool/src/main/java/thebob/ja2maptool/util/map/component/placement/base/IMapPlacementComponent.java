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
package thebob.ja2maptool.util.map.component.placement.base;

import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.map.controller.base.IMapController;
import thebob.ja2maptool.util.map.layers.cursor.MapCursor;

/**
 * The placement controller abstracts the concept of putting stuff on the map
 * via the cursor. Its primary functionality revolves around the payload, which
 * is the thing that's supposed to be placed on the map, and the placement,
 * which is the location on the map we're looking to put the thing.
 *
 * @see PlacementCursorController
 * <br>Is an example of where this is useful, it can identify the size of the
 * thing and communicate to the component when a placement has been chosen
 *
 * @author the_bob
 */
public interface IMapPlacementComponent extends IMapController {

    SelectedTiles getPayload();

    void setPayload(SelectedTiles payload);

    /**
     * Gets the placement currently setup in the placement component, assuming
     * the placement component is the kind for which a "current" placement makes
     * sense.
     *
     * @return the current placement location if set
     */
    MapCursor getPlacementLocation();

    /**
     * Tells the placement component the user wants to place the thing at this
     * location
     *
     * @param placement
     */
    void setPlacementLocation(MapCursor placement);

    /**
     * Asks the placement component if there's a thing placed over this cell.
     *
     * @param cell
     * @return the controller should return the placement "center" if found,
     * null if there's no placement covering the cell.
     */
    Integer hoverPlacement(int cell);
}
