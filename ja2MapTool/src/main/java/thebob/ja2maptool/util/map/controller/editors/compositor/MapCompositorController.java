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
import java.util.Map;
import java.util.Observable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import thebob.ja2maptool.scopes.map.MapCompositorScope;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.compositor.SnippetPlacement;
import thebob.ja2maptool.util.map.component.cursor.MapCursorComponent;
import thebob.ja2maptool.util.map.component.cursor.base.IMapCursorComponent;
import thebob.ja2maptool.util.map.component.cursor.cursors.BasicCursorController;
import thebob.ja2maptool.util.map.component.cursor.cursors.PlacementCursorController;
import thebob.ja2maptool.util.map.component.cursor.cursors.SelectionCursorController;
import thebob.ja2maptool.util.map.component.interaction.IMapInteractionComponent;
import thebob.ja2maptool.util.map.component.interaction.MapInteractionComponent;
import thebob.ja2maptool.util.map.component.interaction.handle.IMapHandleComponent;
import thebob.ja2maptool.util.map.component.interaction.handle.MapHandleComponent;
import thebob.ja2maptool.util.map.component.placement.base.IMapPlacementComponent;
import thebob.ja2maptool.util.map.component.placement.clipboard.IMapClipboardComponent;
import thebob.ja2maptool.util.map.component.placement.clipboard.MapClipboardComponent;
import thebob.ja2maptool.util.map.component.placement.snippets.IMapSnippetPlacementComponent;
import thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementComponent;
import thebob.ja2maptool.util.map.component.placement.snippets.MapSnippetPlacementLayer;
import thebob.ja2maptool.util.map.component.selection.IMapSelectionComponent;
import thebob.ja2maptool.util.map.component.selection.MapSelectionComponent;
import thebob.ja2maptool.util.map.controller.base.MapControllerBase;
import thebob.ja2maptool.util.map.events.MapEvent;
import thebob.ja2maptool.util.map.events.MapPlacementEventPayload;
import thebob.ja2maptool.util.map.layers.cursor.CursorLayer;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.layers.preview.PreviewLayer;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;
import thebob.ja2maptool.util.map.renderer.renderlayer.OverlaySettings;

/**
 *
 * @author the_bob
 */
public class MapCompositorController extends MapControllerBase implements IMapCompositorController {

    protected MapCompositorScope scope;

    // display layers
    protected ICursorLayerManager cursorLayer = new CursorLayer();
    protected PreviewLayer previewLayer = new PreviewLayer();

    // Components
    protected IMapInteractionComponent cells = new MapInteractionComponent(getRenderer(), getMap());
	protected IMapCursorComponent cursors = new MapCursorComponent(getRenderer(), getMap(), cursorLayer, cells);
	protected IMapHandleComponent handles = new MapHandleComponent(getRenderer(), getMap(), cursorLayer, cells);
    
    protected IMapSnippetPlacementComponent placements = new MapSnippetPlacementComponent(getRenderer(), getMap(), cursorLayer, previewLayer, cells);
    protected IMapSelectionComponent selection = new MapSelectionComponent(getRenderer(), getMap(), cursorLayer, cells, placements);
    protected IMapClipboardComponent clipboard = new MapClipboardComponent(getRenderer(), getMap(), cursorLayer, previewLayer, selection);

    SelectedTiles preview = null;
    SelectionPlacementOptions snippetLayers = null;

    public MapCompositorController(ITileRendererManager renderer, IMapLayerManager map, MapCompositorScope compositor) {
        super(renderer, map);
        // remember parent window scope
        scope = compositor;

        // init cursor
        cursors.setCursor(new BasicCursorController());

        // setup observables
        cursors.addObserver(this);
        selection.addObserver(this);
        clipboard.addObserver(this);
        placements.addObserver(this);

        renderer.addRenderOverlay(previewLayer, new OverlaySettings(0.65, 0, 0, null)); // new Glow(1d)) /// new Shadow(2d, Color.BLACK)
        renderer.addRenderOverlay(cursorLayer, new OverlaySettings(1.0d, 0, 0, null)); // new Glow(1d) /// new Shadow(2d, Color.BLACK)
    }

    // ----------------------------
    // -- Notification handler
    // ----------------------------
    @Override
    public void update(Observable o, Object arg) {
        MapEvent message = (MapEvent) arg;

        if (message != null) {
            switch (message.getType()) {
                case MAP_LOADED:
                    // TODO: check for map dimension change and tell the placement component to recalculate cell ids / cull placements outside map bounds
                    cursorLayer.init(getMap().getMapRows(), getMap().getMapCols(), getMap().getTileset());
                    previewLayer.init(getMap().getMapRows(), getMap().getMapCols(), getMap().getTileset());
                    break;

                // --> placement events
                case PLACEMENT_ADDED: {
                    SnippetPlacement placement = ((MapPlacementEventPayload) message.getPayload()).getPlacement();
                    if (placements.hasContents() == false) {
                        // if the placement component doesn't have a payload set, it means this was a pick-and-drop rather than a simple placement addition
                        updateCursor();
                    }
                    scope.addPlacement(placement);
                }
                break;
                case PLACEMENT_DELETED: {
                    SnippetPlacement placement = ((MapPlacementEventPayload) message.getPayload()).getPlacement();
                    scope.deletePlacement(placement);
                }
                break;
                case PLACEMENT_CANCELED: {
                    updateCursor();
                    scope.cancelPlacement();
                }
                break;
                case PLACEMENT_PICKED: {
                    SnippetPlacement placement = ((MapPlacementEventPayload) message.getPayload()).getPlacement();
                    updateCursor();
                    scope.pickPlacement(placement);
                }
                break;
                case PLACEMENT_HOVERED: {
                    if (message.getPayload() != null) {
                        SnippetPlacement placement = ((MapPlacementEventPayload) message.getPayload()).getPlacement();
                        scope.hoverPlacement(placement);
                    } else {
                        scope.hoverPlacement(null);
                    }
                }
                break;
                case PLACEMENT_SELECTED: {
                    if (message.getPayload() != null) {
                        SnippetPlacement placement = ((MapPlacementEventPayload) message.getPayload()).getPlacement();
                        scope.selectPlacement(placement);
                    } else {
                        scope.selectPlacement(null);
                    }
                }
                break;
                case PLACEMENT_LIST_MOVED:
                    scope.updateList();
                    break;

                // --> placement layer events
                case PLACEMENT_LAYER_SWITCHED:
                    scope.layerSwitched();
                    break;
                case PLACEMENT_LAYER_MOVED:
                case PLACEMENT_LAYER_DELETED:
                case PLACEMENT_LAYER_ADDED:
                    scope.updateLayers();
                    break;
                default:

            }
        }
    }

    // ----------------------------
    // -- Cursor control
    // ----------------------------
    private void updateCursor() {
        if (clipboard.hasContents()) {
            cursors.setCursor(new PlacementCursorController((IMapPlacementComponent) clipboard));
        } else if (placements.hasContents()) {
            cursors.setCursor(new PlacementCursorController((IMapPlacementComponent) placements));
        } else {
            cursors.setCursor(new BasicCursorController());
        }
    }

    // ----------------------------
    // -- Event handler (and more cursor control)
    // ----------------------------
    @Override
    public void mouseEvent(MouseEvent e) {
        cursors.mouseEvent(e);
        if(e.isConsumed() == false && e.getEventType() == MouseEvent.MOUSE_CLICKED && e.getButton() == MouseButton.SECONDARY){
            //if( clipboard.hasContents() == false && placements.hasContents() == true ){                
            clipboard.setContents(null);
            placements.setContents(null);
            updateCursor();
        }
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (clipboard.hasContents()) {
            if (e.getEventType() == KeyEvent.KEY_PRESSED && e.getCode() == KeyCode.CONTROL) {
                cursors.setCursor(new PlacementCursorController((IMapPlacementComponent) placements));
            } else {
                updateCursor();
            }
        } else if (e.getEventType() == KeyEvent.KEY_PRESSED && e.getCode() == KeyCode.SHIFT) {
            cursors.setCursor(new SelectionCursorController(selection));
        } else if (e.getEventType() == KeyEvent.KEY_RELEASED && (e.getCode() == KeyCode.SHIFT || e.getCode() == KeyCode.CONTROL)) {
            updateCursor();
        }

        cursors.keyEvent(e);
    }

    // ----------------------------
    // -- Clipboard placement
    // ----------------------------
    @Override
    public SelectedTiles getSelection() {
        if (clipboard.canCopy()) {
            clipboard.copy();
            SelectedTiles stuff =  clipboard.getContents();
            clipboard.emptyContents();
            return stuff;
        }
        return null;
    }

    @Override
    public void placeSelection(SelectedTiles selection, SelectionPlacementOptions options) {
        clipboard.setContents(selection);
        clipboard.paste(options);
    }

    @Override
    public void setPlacementPreview(SelectedTiles preview) {
        clipboard.setContents(preview);
        placements.setContents(preview);
        selection.clearSelection();
        updateCursor();
    }

    // ----------------------------
    // -- Placement
    // ----------------------------
    @Override
    public void selectPlacement(SnippetPlacement selectedItem) {
        placements.selectPlacement(selectedItem);
    }

    @Override
    public void setPlacementVisibility(SelectionPlacementOptions snippetPlacementOptions) {
        snippetLayers = snippetPlacementOptions;
        placements.setPlacementVisibility(snippetLayers);
    }

    @Override
    public void commitChangesToMap() {
        placements.placeAll();
    }      

    // ----------------------------
    // -- Placement Layers
    // ----------------------------
    @Override
    public List<MapSnippetPlacementLayer> getLayers() {
        return placements.getLayers();
    }

    @Override
    public void setActiveLayer(MapSnippetPlacementLayer layer) {
        placements.setCurrentLayer(layer);
    }

    @Override
    public MapSnippetPlacementLayer getActiveLayer() {
        return placements.getCurrentLayer();
    }

    @Override
    public void updateVisibleLayers() {
        placements.updateVisibleLayers();
    }

    @Override
    public void appendPlacementsToCurrentLayer(Map<Integer, SnippetPlacement> placements) {
        this.placements.appendPlacementsToCurrentLayer(placements);
    }    

    // ----------------------------
    // -- Placement Layer management
    // ----------------------------
    public MapSnippetPlacementLayer addPlacementLayer(String name) {
        return placements.addPlacementLayer(name);
    }

    @Override
    public void deletePlacementLayer(MapSnippetPlacementLayer layer) {
        placements.deletePlacementLayer(layer);
    }

    @Override
    public void movePlacementLayer(MapSnippetPlacementLayer selectedItem, int i) {
        placements.movePlacementLayer(selectedItem, i);
    }

    @Override
    public void copyPlacementLayer(MapSnippetPlacementLayer selectedItem) {
        placements.copyPlacementLayer(selectedItem);
    }
    
    @Override
    public void setLayers(List<MapSnippetPlacementLayer> layers){
        placements.setLayers(layers);
    }

    // -------------    
    /**
     * TODO? The compositor controller probably shouldn't be disconnected anyway
     */
    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
