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
package thebob.ja2maptool.util.map.component.placement.snippets;

import java.util.List;
import java.util.Map;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.compositor.SnippetPlacement;
import thebob.ja2maptool.util.map.controller.base.IMapController;

/**
 *
 * @author the_bob
 */
public interface IMapSnippetPlacementComponent extends IMapController {

    /**
     * Sets the currently placed snippet. Selection functionality is disabled
     * while a snippet is loaded.
     *
     * @param preview
     */
    void setContents(SelectedTiles preview);

    /**
     *
     * @return true if a snippet is selected for placement
     */
    boolean hasContents();

    /**
     * Returns the placement map for the active placement layer. The keys are
     * cell IDs where the placements are pinned, the values are the placements
     * themselves. It is not recommended to manipulate this list directly.
     *
     * @return the currently selected placements map.
     */
    Map<Integer, SnippetPlacement> getPlacements();

    // ----------------------------
    // placement actions
    // ----------------------------
    /**
     * Sets a placement as selected and makes it look that way in the map
     * window.
     *
     * @param selectedItem
     */
    public void selectPlacement(SnippetPlacement selectedItem);

    public void setPlacementVisibility(SelectionPlacementOptions snippetLayers);

    // ----------------------------
    // placement movement
    // ----------------------------
    /**
     * Moves the placement at this cell by specified number of cells. Doesn't
     * refresh screen state.
     *
     * @param placementCell
     * @param deltaX
     * @param deltaY
     */
    void movePlacement(int placementCell, int deltaX, int deltaY);

    /**
     * Moves all placements in the list by specified number of cells then
     * rebuilds the interaction areas and cursor layers
     *
     * @param selectedPlacements
     * @param deltaX
     * @param deltaY
     */
    void movePlacementList(List<SnippetPlacement> selectedPlacements, int deltaX, int deltaY);

    // ----------------------------
    // layers
    // ----------------------------
    MapSnippetPlacementLayer addPlacementLayer(String name);

    List<MapSnippetPlacementLayer> getLayers();

    void setCurrentLayer(MapSnippetPlacementLayer layer);

    MapSnippetPlacementLayer getCurrentLayer();

    public void updateVisibleLayers();
    // ----------------------------
    // layer manipulation
    // ----------------------------

    public void deletePlacementLayer(MapSnippetPlacementLayer layer);

    public void movePlacementLayer(MapSnippetPlacementLayer selectedItem, int i);

    public void copyPlacementLayer(MapSnippetPlacementLayer selectedItem);

}
