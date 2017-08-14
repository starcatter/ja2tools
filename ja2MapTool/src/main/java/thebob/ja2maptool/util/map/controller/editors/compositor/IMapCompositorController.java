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
package thebob.ja2maptool.util.map.controller.editors.compositor;

import java.util.List;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.compositor.SnippetPlacement;
import thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementLayer;
import thebob.ja2maptool.util.map.controller.base.IMapController;

/**
 *
 * @author the_bob
 */
public interface IMapCompositorController extends IMapController {

    /**
     * returns currently selected tiles
     *
     * @return selected tiles
     */
    SelectedTiles getSelection();

    /**
     * places the passed tiles at placement position
     *
     * @param selection
     * @param options
     */
    void placeSelection(SelectedTiles selection, SelectionPlacementOptions options);

    /**
     * sets placement preview tiles
     *
     * @param selection
     */
    public void setPlacementPreview(SelectedTiles selection);

    /**
     * Sets which layers will be pasted in added placements. Previously added
     * placements will not be affected. Layer visibility for content pasted from
     * clipboard is taken from current checkbox status.
     *
     * @param snippetPlacementOptions
     */
    public void setPlacementVisibility(SelectionPlacementOptions snippetPlacementOptions);

    // ----------------------------
    // placements
    // ----------------------------
    public void selectPlacement(SnippetPlacement selectedItem);

    // ----------------------------
    // placement layers
    // ----------------------------
    List<MapSnippetPlacementLayer> getLayers();

    void setActiveLayer(MapSnippetPlacementLayer layer);

    public MapSnippetPlacementLayer getActiveLayer();

    public void updateVisibleLayers();

    // ----------------------------
    // placement layer manipulation
    // ----------------------------
    MapSnippetPlacementLayer addPlacementLayer(String name);

    void deletePlacementLayer(MapSnippetPlacementLayer layer);

    public void movePlacementLayer(MapSnippetPlacementLayer selectedItem, int i);

    public void copyPlacementLayer(MapSnippetPlacementLayer selectedItem);

}
