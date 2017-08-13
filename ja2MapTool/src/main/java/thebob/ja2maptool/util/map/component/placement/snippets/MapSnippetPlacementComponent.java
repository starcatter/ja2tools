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

import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SnippetPlacement;
import thebob.ja2maptool.util.map.component.interaction.IMapInteractionComponent;
import thebob.ja2maptool.util.map.component.interaction.data.MapInteractionData;
import thebob.ja2maptool.util.map.component.interaction.data.types.PlacementInteractionData;
import thebob.ja2maptool.util.map.component.interaction.layer.MapInteractionLayer;
import thebob.ja2maptool.util.map.component.interaction.target.IMapInteractiveComponent;
import thebob.ja2maptool.util.map.component.placement.base.MapPlacementComponentBase;
import thebob.ja2maptool.util.map.events.MapEvent;
import thebob.ja2maptool.util.map.events.MapPlacementEventPayload;
import thebob.ja2maptool.util.map.layers.cursor.CursorLayer.CursorFillMode;
import static thebob.ja2maptool.util.map.layers.cursor.CursorLayer.LAYER_ACTION;
import static thebob.ja2maptool.util.map.layers.cursor.CursorLayer.LAYER_STATE;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.cursor.MapCursor;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.layers.preview.PreviewLayer;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

public class MapSnippetPlacementComponent extends MapPlacementComponentBase implements IMapSnippetPlacementComponent, IMapInteractiveComponent {

    private static final IndexedElement PLACEMENT_TILES_CURSOR = new IndexedElement(131, 7);
    private static final IndexedElement PLACEMENT_TILES_ACTIVE_CURSOR = new IndexedElement(131, 2);
    private static final IndexedElement PLACEMENT_CURSOR = new IndexedElement(131, 16);

    public enum PlacementMode {
        Single, // can place the payload once
        Multi;  // must unset the payload manually
    }

    PlacementMode mode = PlacementMode.Multi;
    SnippetPlacement pickedPlacement = null;
    Integer hoveredPlacement = null;
    Integer x = null;
    Integer y = null;

    private final ICursorLayerManager cursorLayer;
    private final PreviewLayer previewLayer;
    private final IMapInteractionComponent activeCells;

    Map<Integer, SnippetPlacement> placements = new HashMap<Integer, SnippetPlacement>();

    public MapSnippetPlacementComponent(ITileRendererManager renderer, IMapLayerManager map, ICursorLayerManager cursorLayer, PreviewLayer previewLayer, IMapInteractionComponent cells) {
        super(renderer, map);
        this.cursorLayer = cursorLayer;
        this.previewLayer = previewLayer;
        this.activeCells = cells;
    }

    // -------------------------
    // IMapPlacementComponent
    // -------------------------
    @Override
    public void setPlacementLocation(MapCursor placementLocation) {
        super.setPlacementLocation(placementLocation);
        System.out.println("thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementComponent.setPlacementLocation()");

        if (getPayload() != null && placementLocation != null) {

            if (!placements.containsKey(placementLocation.getCell())) {
                put(new SnippetPlacement(placementLocation, getPayload()));
            } else if (pickedPlacement != null) {
                pick_cancel();
            }

        }
    }

    /**
     * uses the interaction component to provide hover feedback to the cursor
     */
    @Override
    public Integer hoverPlacement(int cell) {
        MapInteractionLayer activeCellLayer = activeCells.getLayer(this);
        if (activeCellLayer.isCellRegistered(cell)) {
            PlacementInteractionData placementData = (PlacementInteractionData) activeCellLayer.getCellData(cell);
            SnippetPlacement placement = placementData.getPlacement();
            return placement.getCell();
        }

        return null;
    }

    // -------------------------
    // IMapSnippetPlacementComponent
    // -------------------------
    @Override
    public void setContents(SelectedTiles preview) {
        setPayload(preview);
    }

    @Override
    public boolean hasContents() {
        return getPayload() != null;
    }

    // -------------------------
    // Internal action implementations
    // -------------------------
    private void pick(SnippetPlacement placement) {
        setContents(placement.getSnippet());
        setMode(PlacementMode.Single);
        pickedPlacement = placement;

        System.out.println("thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementComponent.pick()");

        //placements.remove(pickedPlacement.getCell());
        //previewLayer.removePlacement(pickedPlacement.getCell());
        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_PICKED, new MapPlacementEventPayload(placement)));
    }

    private void pick_cancel() {
        setContents(null);
        setMode(PlacementMode.Multi);
        pickedPlacement = null;

        System.out.println("thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementComponent.pick_cancel()");

        //placements.put(pickedPlacement.getCell(),pickedPlacement);
        //previewLayer.addPlacement(pickedPlacement.getCell(), pickedPlacement.getSnippet());        
        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_CANCELED));
    }

    private void put(SnippetPlacement added) {
        if (added == null) {
            System.err.println("thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementComponent.put(): SnippetPlacement is null!!!");
            updateStateLayer();
            return;
        }

        placements.put(added.getCell(), added);

        // add snippet display
        previewLayer.addPlacement(added.getCell(), getPayload());

        if (pickedPlacement != null) {
            remove(pickedPlacement);
        }
        if (mode == PlacementMode.Single) {
            setContents(null);
            setMode(PlacementMode.Multi);
        }

        updateStateLayer();
        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_ADDED, new MapPlacementEventPayload(added)));
    }

    private void remove(SnippetPlacement deleted) {
        if (deleted == null) {
            System.err.println("thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementComponent.remove(): SnippetPlacement is null!!!");
            updateStateLayer();
            return;
        }

        remove(deleted.getCell());
    }

    private void remove(int cell) {
        SnippetPlacement deleted = placements.remove(cell);

        // remove preview
        if (deleted != null) {
            previewLayer.removePlacement(deleted.getCell());

            updateStateLayer();
            notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_DELETED, new MapPlacementEventPayload(deleted)));
        }
    }

    // -------------------------
    // updates placement display and interaction status
    // -------------------------
    private void updateStateLayer() {
        MapInteractionLayer activeCellLayer = activeCells.getLayer(this);

        cursorLayer.clearLayer(LAYER_STATE);
        activeCellLayer.clear();

        placements.forEach((cell, placement) -> {
            cursorLayer.placeCursorCenterRect(LAYER_STATE, placement.getCellX(), placement.getCellY(), placement.getSnippet().getWidth(), placement.getSnippet().getHeight(), PLACEMENT_TILES_CURSOR, CursorFillMode.Corners);

            int[] cells = cursorLayer.getCellNumbersForRadius(placement.getCellX(), placement.getCellY(), placement.getSnippet().getWidth() + 2, placement.getSnippet().getHeight() + 2, CursorFillMode.Full);
            activeCellLayer.registerCells(cells, new PlacementInteractionData(placement));
        });

        activeCells.refreshLayers();
    }

    // --------------------------
    // interaction component interface
    // --------------------------
    @Override
    public boolean hoverCell(int cell, MapInteractionData data) {
        PlacementInteractionData placementData = (PlacementInteractionData) data.getUserdata();
        SnippetPlacement activePlacement = placementData.getPlacement();

        // changed hovered placement?
        if (hoveredPlacement == null || activePlacement.getCell() != hoveredPlacement) {
            cursorLayer.clearLayer(LAYER_ACTION);
            cursorLayer.placeCursorCenterRect(LAYER_ACTION, activePlacement.getCellX(), activePlacement.getCellY(), activePlacement.getSnippet().getWidth(), activePlacement.getSnippet().getHeight(), PLACEMENT_TILES_ACTIVE_CURSOR, CursorFillMode.Full);
            notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_HOVERED, new MapPlacementEventPayload(activePlacement)));
            hoveredPlacement = activePlacement.getCell();

            x = null;
            y = null;
        }

        if (x != null && y != null) {
            int deltaX = data.getMouseCellX() - x;
            int deltaY = data.getMouseCellY() - y;

            // are we trying to drag stuff?
            if (data.getButton() == MouseButton.PRIMARY && (deltaX != 0 || deltaY != 0)) {

                previewLayer.removePlacement(activePlacement.getCell());
                placements.remove(activePlacement.getCell());

                activePlacement.setCellX(activePlacement.getCellX() + deltaX);
                activePlacement.setCellY(activePlacement.getCellY() + deltaY);
                activePlacement.setCell(cursorLayer.rowColToPos(activePlacement.getCellY(), activePlacement.getCellX()));

                hoveredPlacement = activePlacement.getCell();
                placements.put(activePlacement.getCell(), activePlacement);

                Platform.runLater(() -> {
                    updateStateLayer();

                    previewLayer.addPlacement(activePlacement.getCell(), activePlacement.getSnippet());

                    cursorLayer.clearLayer(LAYER_ACTION);
                    cursorLayer.placeCursorCenterRect(LAYER_ACTION, activePlacement.getCellX(), activePlacement.getCellY(), activePlacement.getSnippet().getWidth(), activePlacement.getSnippet().getHeight(), PLACEMENT_TILES_ACTIVE_CURSOR, CursorFillMode.Full);
                });
            }
        }

        if (data.getButton() == MouseButton.PRIMARY) {
            x = data.getMouseCellX();
            y = data.getMouseCellY();
        }

        return true;
    }

    @Override
    public boolean activateCell(int cell, MapInteractionData data) {
        PlacementInteractionData placementData = (PlacementInteractionData) data.getUserdata();
        SnippetPlacement activePlacement = placementData.getPlacement();

        if (data.getButton() == MouseButton.PRIMARY && hasContents() == false) {
            pick(activePlacement);
            return true;
        } else if (data.getButton() == MouseButton.SECONDARY) {
            remove(activePlacement.getCell());
            return true;
        }
        
        return false;
    }

    @Override
    public void hoverOff() {
        if (hoveredPlacement != null) {
            notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_HOVERED, null));
            cursorLayer.clearLayer(LAYER_ACTION);
            hoveredPlacement = null;
        }
    }

    public PlacementMode getMode() {
        return mode;
    }

    public void setMode(PlacementMode mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "MapSnippetPlacementComponent{" + "placements=" + placements.size() + ", hoveredPlacement=" + hoveredPlacement + '}';
    }

}
