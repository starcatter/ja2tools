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

import java.util.Observable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.map.component.base.MapComponentBase;
import thebob.ja2maptool.util.map.layers.cursor.MapCursor;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 * Base class for controllers putting stuff on the map.
 * @author the_bob
 */
public abstract class MapPlacementComponentBase extends MapComponentBase implements IMapPlacementComponent {

    protected SelectedTiles payload = null;
    protected MapCursor placementLocation = null;
    
    // -------------------------------------------
    
    public MapPlacementComponentBase(ITileRendererManager renderer, IMapLayerManager map) {
        super(renderer, map);
    }

    // -------------------------------------------

    public SelectedTiles getPayload() {
        return payload;
    }

    public void setPayload(SelectedTiles payload) {
        this.payload = payload;
    }

    public MapCursor getPlacementLocation() {
        return placementLocation;
    }

    public void setPlacementLocation(MapCursor placementLocation) {
        this.placementLocation = placementLocation;
    }      
    
    // -------------------------------------------
    
    @Override
    public void mouseEvent(MouseEvent e) {
        
    }

    @Override
    public void keyEvent(KeyEvent e) {
        
    }

    @Override
    public void update(Observable o, Object arg) {
        
    }

}
