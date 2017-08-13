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
package thebob.ja2maptool.util.map.component.placement.clipboard;

import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.map.component.placement.base.MapPlacementComponentBase;
import thebob.ja2maptool.util.map.component.selection.IMapSelectionComponent;
import thebob.ja2maptool.util.map.events.MapEvent;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.cursor.MapCursor;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.layers.preview.PreviewLayer;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author the_bob
 */
public class MapClipboardComponent extends MapPlacementComponentBase implements IMapClipboardComponent {

    private final ICursorLayerManager cursorLayer;
    private final PreviewLayer previewLayer;
    private final IMapSelectionComponent selection;

    public MapClipboardComponent(ITileRendererManager renderer, IMapLayerManager map, ICursorLayerManager cursorLayer, PreviewLayer previewLayer, IMapSelectionComponent selection) {
        super(renderer, map);
        this.cursorLayer = cursorLayer;
        this.previewLayer = previewLayer;
        this.selection = selection;
    }

    // -------------------------
    @Override
    public void setPlacementLocation(MapCursor placement) {
        super.setPlacementLocation(placement);
        updatePreview();

        notifyObservers(new MapEvent(MapEvent.ChangeType.CLIPBOARD_PLACED));
    }

    @Override
    public void setPayload(SelectedTiles payload) {
        super.setPayload(payload);
        updatePreview();

        notifyObservers(new MapEvent(MapEvent.ChangeType.CLIPBOARD_FILLED));
    }

    // -------------------------
    protected void updatePreview() {
        if (previewLayer == null) {
            return;
        }

        if (canPaste()) {
            previewLayer.setPreview(payload);
            previewLayer.placePreview(placementLocation);
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
        notifyObservers(new MapEvent(MapEvent.ChangeType.CLIPBOARD_EMPTIED));
    }

    @Override
    public boolean canPaste() {
        return getPayload() != null && getPlacementLocation() != null;
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
        if (getMap().getTilesForSelection(tiles) != null) {
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
        if (getMap().getTilesForSelection(tiles) != null) {
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

        getMap().appendTiles(getPlacementLocation(), getPayload(), options);

        return true;
    }

    @Override
    public Integer hoverPlacement(int placement) {
        return null;
    }

}
