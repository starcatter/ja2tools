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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
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
    private SelectionPlacementOptions snippetPlacementOptions = new SelectionPlacementOptions(true,true,true,true,true,true,true,true);

    public enum PlacementMode {
        Single, // can place the payload once
        Multi;  // must unset the payload manually
    }

    private PlacementMode mode = PlacementMode.Multi;
    private SnippetPlacement pickedPlacement = null;
    private Integer hoveredPlacement = null;
    private Integer x = null;
    private Integer y = null;

    private final ICursorLayerManager cursorLayer;
    private final PreviewLayer previewLayer;
    private final IMapInteractionComponent activeCells;

    // initialize the default snippet layer
    private MapSnippetPlacementLayer currentLayer = new MapSnippetPlacementLayer("Default");
    private Map<Integer, SnippetPlacement> placements = currentLayer.getPlacements();
    private List<MapSnippetPlacementLayer> layers = new ArrayList<MapSnippetPlacementLayer>();

    public MapSnippetPlacementComponent(ITileRendererManager renderer, IMapLayerManager map, ICursorLayerManager cursorLayer, PreviewLayer previewLayer, IMapInteractionComponent cells) {
        super(renderer, map);
        this.cursorLayer = cursorLayer;
        this.previewLayer = previewLayer;
        this.activeCells = cells;

        layers.add(currentLayer);
    }

    public MapSnippetPlacementLayer addPlacementLayer(String name) {
        MapSnippetPlacementLayer newLayer = new MapSnippetPlacementLayer(name);
        layers.add(newLayer);
        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_LAYER_ADDED));
        return newLayer;
    }

    @Override
    public void movePlacementLayer(MapSnippetPlacementLayer selectedItem, int i) {
        int index = layers.indexOf(selectedItem);
        int targetIndex = index + i;
        if (targetIndex > 0 && targetIndex < layers.size()) {
            Collections.swap(layers, index, targetIndex);
        }
        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_LAYER_MOVED));
    }

    @Override
    public void copyPlacementLayer(MapSnippetPlacementLayer selectedItem) {
        MapSnippetPlacementLayer copy = new MapSnippetPlacementLayer(selectedItem);
        layers.add(layers.indexOf(selectedItem) + 1, copy);
        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_LAYER_ADDED));
    }

    @Override
    public void deletePlacementLayer(MapSnippetPlacementLayer layer) {
        if (layers.size() < 2) {
            return;
        }
        if (currentLayer == layer) {
            if (layers.get(0) != layer) {
                setCurrentLayer(layers.get(0));
            } else {
                setCurrentLayer(layers.get(1));
            }
        }

        // clean up the old layer
        if (layer.isVisible()) {
            layer.getPlacements().forEach((cell, placement) -> {
                previewLayer.removePlacement(cell);
            });
        }
        layers.remove(layer);

        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_LAYER_DELETED));
    }

    @Override
    public List<MapSnippetPlacementLayer> getLayers() {
        return layers;
    }

    @Override
    public void setLayers(List<MapSnippetPlacementLayer> newLayers) {
        for (MapSnippetPlacementLayer layer : layers) {
            layer.getPlacements().forEach((cell, placement) -> {
                previewLayer.removePlacement(cell);
            });
        }

        layers.clear();
        placements.clear();

        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_LAYER_DELETED));
        layers.addAll(newLayers);
        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_LAYER_ADDED));

        setCurrentLayer(layers.get(0));
    }

    @Override
    public void setCurrentLayer(MapSnippetPlacementLayer layer) {
        if (layer == null) {
            return;
        }
        // clean up the old layer
        placements.forEach((cell, placement) -> {
            previewLayer.removePlacement(cell);
        });

        // switch layers
        currentLayer = layer;
        placements = currentLayer.getPlacements();

        if (!currentLayer.getPlacements().isEmpty()) // add previews
        {
            placements.forEach((cell, placement) -> {
                previewLayer.addPlacement(cell, placement);
            });
        }

        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_LAYER_SWITCHED));

        // update state
        updateStateLayer();
        updateVisibleLayers();
    }

    @Override
    public void updateVisibleLayers() {
        layers.forEach(layer -> {
            if (layer.isVisible()) {
                layer.getPlacements().forEach((cell, placement) -> {
                    previewLayer.addPlacement(cell, placement);
                });
            } else {
                layer.getPlacements().forEach((cell, placement) -> {
                    previewLayer.removePlacement(cell);
                });
            }
        });
    }

    @Override
    public MapSnippetPlacementLayer getCurrentLayer() {
        return currentLayer;
    }

    // --------------------------------------------------
    // IMapPlacementComponent
    // --------------------------------------------------
    @Override
    public void setPlacementLocation(MapCursor placementLocation) {
        super.setPlacementLocation(placementLocation);
        System.out.println("thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementComponent.setPlacementLocation()");

        if (getPayload() != null && placementLocation != null) {

            if (!placements.containsKey(placementLocation.getCell())) {
                SelectionPlacementOptions options = pickedPlacement != null ? pickedPlacement.getEnabledLayers() : snippetPlacementOptions;
                put(new SnippetPlacement(placementLocation, getPayload(), options));
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

    // --------------------------------------------------
    // IMapSnippetPlacementComponent
    // --------------------------------------------------
    @Override
    public void placeAll() {
        for (MapSnippetPlacementLayer layer : layers) {
            //System.out.println("Placing layer " + layer.getName());
            layer.getPlacements().forEach((cell, placement) -> {
                MapCursor placementCursor = cursorLayer.getCursor(placement.getCellX(), placement.getCellY(), null);
                SelectedTiles snippet = placement.getSnippet();
                SelectionPlacementOptions options = placement.getEnabledLayers();

                //System.out.println("\t" + placement);
                getMap().appendTiles(placementCursor, snippet, options);
            });
            layer.setVisible(false);
        }

        updateVisibleLayers();
    }

    @Override
    public void setContents(SelectedTiles preview) {
        if (pickedPlacement != null) {
            pick_cancel();
        }
        setPayload(preview);
    }

    @Override
    public boolean hasContents() {
        return getPayload() != null;
    }

    @Override
    public void selectPlacement(SnippetPlacement selectedItem) {
        select(selectedItem);
    }

    @Override
    public Map<Integer, SnippetPlacement> getPlacements() {
        return placements;
    }

    @Override
    public void setPlacementVisibility(SelectionPlacementOptions snippetLayers) {
        snippetPlacementOptions = snippetLayers;
    }

    @Override
    public void appendPlacementsToCurrentLayer(Map<Integer, SnippetPlacement> placements) {
        this.placements.putAll(placements);
    }

    // --------------------------------------------------
    // Internal action implementations
    // --------------------------------------------------
    SnippetPlacement selectedPlacement = null;

    private void deselect() {
        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_DESELECTED, new MapPlacementEventPayload(selectedPlacement)));
        selectedPlacement = null;
    }

    private void select(SnippetPlacement placement) {
        if (selectedPlacement == placement) {
            return;
        }
        if (selectedPlacement != null) {
            deselect();
        }
        selectedPlacement = placement;
        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_SELECTED, new MapPlacementEventPayload(selectedPlacement)));
        updateStateLayer();
    }

    private void pick(SnippetPlacement placement) {
        setContents(placement.getSnippet());
        setMode(PlacementMode.Single);
        pickedPlacement = placement;

        // System.out.println("thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementComponent.pick()");
        //placements.remove(pickedPlacement.getCell());
        //previewLayer.removePlacement(pickedPlacement.getCell());
        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_PICKED, new MapPlacementEventPayload(placement)));
    }

    private void pick_cancel() {
        pickedPlacement = null;        
        setMode(PlacementMode.Multi);
        setContents(null);  // make sure to set pickedPlacement to null first!
        

        // System.out.println("thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementComponent.pick_cancel()");
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
        previewLayer.addPlacement(added.getCell(), added);

        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_ADDED, new MapPlacementEventPayload(added)));
        
        if (pickedPlacement != null) {
            remove(pickedPlacement);
        }
        if (mode == PlacementMode.Single) {
            setContents(null);
            setMode(PlacementMode.Multi);            
        }

        //updateStateLayer();   // just select the new thing, it will update the state
        select(added);        
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

    // --------------------------------------------------
    // updates placement display and interaction status
    // --------------------------------------------------
    private void updateStateLayer() {
        final int marginX = 0;
        final int marginY = 0;

        MapInteractionLayer activeCellLayer = activeCells.getLayer(this);

        cursorLayer.clearLayer(LAYER_STATE);
        activeCellLayer.clear();

        placements.forEach((cell, placement) -> {
            // int marginX = placement.getSnippet().getWidth()< 3 ? 2 : 0;
            // int marginY = placement.getSnippet().getHeight() < 3 ? 2 : 0;

            if (placement != selectedPlacement) {
                cursorLayer.placeCursorCenterRect(LAYER_STATE, placement.getCellX(), placement.getCellY(), placement.getSnippet().getWidth(), placement.getSnippet().getHeight(), PLACEMENT_TILES_CURSOR, CursorFillMode.Corners);
            } else {
                cursorLayer.placeCursorCenterRect(LAYER_STATE, placement.getCellX(), placement.getCellY(), placement.getSnippet().getWidth(), placement.getSnippet().getHeight(), PLACEMENT_TILES_CURSOR, CursorFillMode.Border);
            }

            int[] cells = cursorLayer.getCellNumbersForRadius(placement.getCellX(), placement.getCellY(), placement.getSnippet().getWidth() + marginX, placement.getSnippet().getHeight() + marginY, CursorFillMode.Full);
            activeCellLayer.registerCells(cells, new PlacementInteractionData(placement));
        });

        activeCells.refreshLayers();
    }

    public void movePlacementList(List<SnippetPlacement> selectedPlacements, int deltaX, int deltaY) {
        for (SnippetPlacement placement : selectedPlacements) {
            movePlacement(placement.getCell(), deltaX, deltaY);
        }
        Platform.runLater(() -> {
            updateStateLayer();
        });

        notifyObservers(new MapEvent(MapEvent.ChangeType.PLACEMENT_LIST_MOVED));
    }

    public void movePlacement(int placementCell, int deltaX, int deltaY) {
        SnippetPlacement activePlacement = placements.get(placementCell);
        if (activePlacement != null) {
            previewLayer.removePlacement(activePlacement.getCell());
            placements.remove(activePlacement.getCell());

            activePlacement.setCellX(activePlacement.getCellX() + deltaX);
            activePlacement.setCellY(activePlacement.getCellY() + deltaY);
            activePlacement.setCell(cursorLayer.rowColToPos(activePlacement.getCellY(), activePlacement.getCellX()));

            hoveredPlacement = activePlacement.getCell();
            placements.put(activePlacement.getCell(), activePlacement);

            previewLayer.addPlacement(activePlacement.getCell(), activePlacement);

            cursorLayer.clearLayer(LAYER_ACTION);
            cursorLayer.placeCursorCenterRect(LAYER_ACTION, activePlacement.getCellX(), activePlacement.getCellY(), activePlacement.getSnippet().getWidth(), activePlacement.getSnippet().getHeight(), PLACEMENT_TILES_ACTIVE_CURSOR, CursorFillMode.Full);
        }
    }

    // ---------------------------------------------------
    // interaction component interface
    // ---------------------------------------------------
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

        // drag&drop initiated?
        if (x != null && y != null) {
            int deltaX = data.getMouseCellX() - x;
            int deltaY = data.getMouseCellY() - y;

            // are we trying to drag stuff?
            if (data.getButton() == MouseButton.PRIMARY && (deltaX != 0 || deltaY != 0)) {

                movePlacement(activePlacement.getCell(), deltaX, deltaY);
                selectedPlacement = null;

                Platform.runLater(() -> {
                    updateStateLayer();
                });
            }
        }

        // initiate drag&drop
        if (data.getButton() == MouseButton.PRIMARY) {
            x = data.getMouseCellX();
            y = data.getMouseCellY();
        }

        return true; // consume hover event!
    }

    @Override
    public boolean activateCell(int cell, MapInteractionData data) {
        PlacementInteractionData placementData = (PlacementInteractionData) data.getUserdata();
        SnippetPlacement activePlacement = placementData.getPlacement();

        if (data.getButton() == MouseButton.PRIMARY && hasContents() == false) {

            if (selectedPlacement == null || selectedPlacement != activePlacement) {
                select(activePlacement);
            } else {
                pick(activePlacement);
            }

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

    // --------------------------------------------------
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
