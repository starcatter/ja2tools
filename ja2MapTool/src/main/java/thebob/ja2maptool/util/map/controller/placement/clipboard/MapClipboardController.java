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
package thebob.ja2maptool.util.map.controller.placement.clipboard;

import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.map.controller.placement.base.MapPlacementControllerBase;
import thebob.ja2maptool.util.map.controller.selection.IMapSelectionController;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.cursor.MapCursor;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.layers.preview.PreviewLayer;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author maste_000
 */
public class MapClipboardController extends MapPlacementControllerBase implements IMapClipboardController {

    private final ICursorLayerManager cursorLayer;
    private final PreviewLayer previewLayer;
    private final IMapSelectionController selection;

    public MapClipboardController(ITileRendererManager renderer, IMapLayerManager map, ICursorLayerManager cursorLayer, PreviewLayer previewLayer, IMapSelectionController selection) {
        super(renderer, map);
        this.cursorLayer = cursorLayer;
        this.previewLayer = previewLayer;
        this.selection = selection;
    }

    // -------------------------
    @Override
    public void setPlacement(MapCursor placement) {
        super.setPlacement(placement);
        updatePreview();
    }

    @Override
    public void setPayload(SelectedTiles payload) {
        super.setPayload(payload);
        updatePreview();
    }

    // -------------------------
    
    protected void updatePreview() {
        if (canPaste()) {
            previewLayer.setPreview(payload);
            previewLayer.placePreview(placement);
        } else {
            previewLayer.setPreview(null);
            previewLayer.placePreview(null);
        }
    }

    // -------------------------
    @Override
    public boolean hasContents() {
        return getPayload() != null;
    }

    @Override
    public SelectedTiles getContents() {
        return getPayload();
    }

    @Override
    public void setContents(SelectedTiles clipboardContents) {
        setPayload(clipboardContents);
    }

    @Override
    public void emptyContents() {
        setPayload(null);
    }

    @Override
    public boolean canPaste() {
        return getPayload() != null && getPlacement() != null;
    }

    @Override
    public boolean canCopy() {
        return selection.hasSelection();
    }

    @Override
    public boolean canCut() {
        return selection.hasSelection();
    }

    @Override
    public boolean copy() {
        if (!canCopy()) {
            return false;
        }

        SelectedTiles tiles = selection.getSelection();
        if (map.getTilesForSelection(tiles) != null) {
            setPayload(tiles);
            return true;
        } else {
            return false;
        }
    }

    // TODO: cut
    @Override
    public boolean cut() {
        if (!canCut()) {
            return false;
        }

        SelectedTiles tiles = selection.getSelection();
        if (map.getTilesForSelection(tiles) != null) {
            setPayload(tiles);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean paste(SelectionPlacementOptions options) {
        if (!canPaste()) {
            return false;
        }

        map.appendTiles(getPlacement(), getPayload(), options);

        return true;
    }

    @Override
    public boolean hoverPlacement(int placement) {
        return false;
    }

}
