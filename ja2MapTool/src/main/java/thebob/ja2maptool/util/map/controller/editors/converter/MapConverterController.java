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
package thebob.ja2maptool.util.map.controller.editors.converter;

import java.util.Observable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import thebob.ja2maptool.scopes.map.ConvertMapScope;
import static thebob.ja2maptool.scopes.map.MapScope.SELECTION_UPDATED;
import thebob.ja2maptool.util.map.component.cursor.MapCursorComponent;
import thebob.ja2maptool.util.map.component.cursor.base.IMapCursorComponent;
import thebob.ja2maptool.util.map.component.cursor.cursors.BasicCursorController;
import thebob.ja2maptool.util.map.component.cursor.cursors.SelectionCursorController;
import thebob.ja2maptool.util.map.component.interaction.IMapInteractionComponent;
import thebob.ja2maptool.util.map.component.interaction.MapInteractionComponent;
import thebob.ja2maptool.util.map.component.placement.clipboard.IMapClipboardComponent;
import thebob.ja2maptool.util.map.component.placement.clipboard.MapClipboardComponent;
import thebob.ja2maptool.util.map.component.selection.IMapSelectionComponent;
import thebob.ja2maptool.util.map.component.selection.MapSelectionComponent;
import thebob.ja2maptool.util.map.controller.base.MapControllerBase;
import thebob.ja2maptool.util.map.events.MapEvent;
import thebob.ja2maptool.util.map.layers.cursor.CursorLayer;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;
import thebob.ja2maptool.util.map.renderer.renderlayer.OverlaySettings;

/**
 *
 * @author the_bob
 */
public class MapConverterController extends MapControllerBase implements IMapConverterController {

    ConvertMapScope scope;

    protected ICursorLayerManager cursorLayer = new CursorLayer();

    protected IMapInteractionComponent cells = new MapInteractionComponent(getRenderer(), getMap());
    protected IMapCursorComponent cursors = new MapCursorComponent(getRenderer(), getMap(), cursorLayer, cells);
    protected IMapSelectionComponent selection = new MapSelectionComponent(getRenderer(), getMap(), cursorLayer, cells);
    protected IMapClipboardComponent clipboard = new MapClipboardComponent(getRenderer(), getMap(), cursorLayer, null, selection);

    public MapConverterController(ITileRendererManager renderer, IMapLayerManager map, ConvertMapScope converter) {
        super(renderer, map);

        scope = converter;

        cursors.setCursor(new BasicCursorController());

        cursors.addObserver(this);
        selection.addObserver(this);
        clipboard.addObserver(this);

        renderer.addRenderOverlay(cursorLayer, new OverlaySettings(1.0d, 0, 0, null)); // new Glow(1d) /// new Shadow(2d, Color.BLACK)
    }

    @Override
    public void mouseEvent(MouseEvent e) {
        cursors.mouseEvent(e);
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (e.getEventType() == KeyEvent.KEY_PRESSED && e.getCode() == KeyCode.SHIFT) {
            cursors.setCursor(new SelectionCursorController(selection));
        } else if (e.getEventType() == KeyEvent.KEY_RELEASED && (e.getCode() == KeyCode.SHIFT)) {
            cursors.setCursor(new BasicCursorController());
        }

        cursors.keyEvent(e);
    }

    @Override

    public void update(Observable o, Object arg) {
        MapEvent message = (MapEvent) arg;

        if (message != null) {
            switch (message.getType()) {
                case MAP_LOADED:
                    cursorLayer.init(getMap().getMapRows(), getMap().getMapCols(), getMap().getTileset());
                    break;

                case SELECTION_CHANGED:
                    clipboard.copy();
                    break;
                case CLIPBOARD_FILLED:
                    scope.getMap().setSelection(clipboard.getContents());
                    scope.getMap().publish(SELECTION_UPDATED);
                    break;
                case SELECTION_CLEARED:
                    clipboard.emptyContents();
                    break;
                case CLIPBOARD_EMPTIED:
                    scope.getMap().setSelection(null);
                    scope.getMap().publish(SELECTION_UPDATED);
                    break;
            }
        }
    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
