/*
 * The MIT License
 *
 * Copyright 2017 maste_000.
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
package thebob.ja2maptool.util.map.controller.placement.snippets;

import java.util.HashMap;
import java.util.Map;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.map.controller.placement.base.MapPlacementControllerBase;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.cursor.MapCursor;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.layers.preview.PreviewLayer;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

public class MapSnippetPlacementController extends MapPlacementControllerBase implements IMapSnippetPlacementController {

    private final ICursorLayerManager cursorLayer;
    private final PreviewLayer previewLayer;

    Map<Integer, SelectedTiles> placements = new HashMap<Integer, SelectedTiles>();

    // -------------------------
    @Override
    public void setPlacement(MapCursor placement) {
        super.setPlacement(placement);

        if (getPayload() != null) {
            if (placements.containsKey(placement.getCell())) {
                previewLayer.removePlacement(placement.getCell());
                placements.remove(placement.getCell());
            } else {
                previewLayer.addPlacement(placement.getCell(), getPayload());
                placements.put(placement.getCell(), getPayload());
            }
        }

        //updatePreview();
    }

    @Override
    public void setPayload(SelectedTiles payload) {
        super.setPayload(payload);
        //updatePreview();
    }

    @Override
    public boolean hoverPlacement(int placement) {
        return placements.containsKey(placement);
    }

    // -------------------------
//    protected void updatePreview() {
//        if (getPayload() != null && getPlacement() != null) {
//            previewLayer.setPreview(payload);
//            previewLayer.placePreview(placement);
//        } else {
//            previewLayer.setPreview(null);
//            previewLayer.placePreview(null);
//        }
//    }
    public MapSnippetPlacementController(ITileRendererManager renderer, IMapLayerManager map, ICursorLayerManager cursorLayer, PreviewLayer previewLayer) {
        super(renderer, map);
        this.cursorLayer = cursorLayer;
        this.previewLayer = previewLayer;
    }

    @Override
    public void setContents(SelectedTiles preview) {
        setPayload(preview);
    }

}
