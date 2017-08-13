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
package thebob.ja2maptool.util.map.renderer;

import java.util.List;
import java.util.Observer;
import thebob.ja2maptool.util.map.controller.base.MapControllerBase;
import thebob.ja2maptool.util.map.renderer.renderlayer.OverlaySettings;
import thebob.ja2maptool.util.map.layers.base.ITileLayerGroup;

/**
 * Extended tile renderer interface including methods for managing its state
 *
 * @author the_bob
 */
public interface ITileRendererManager extends ITileRendererControls, Observer {

    /*
     * Direct access to render layer group data. Note that changes done this way might not properly update all the renderer settings.
     * Use the add/remove methods to for manipulating the layer groups.
     */
    List<ITileLayerGroup> getRenderLayers();

    /**
     * Adds an overlay to the renderer - the passed layerGroup will get its own canvas placed over the current render window.
     * @param layer
     * @param settings 
     */
    void addRenderOverlay(ITileLayerGroup layer, OverlaySettings settings);
    
    /**
     * Adds a layer to the renderer. This layer will be placed on top of existing layers and rendered on the base canvas.
     * @param layer 
     */
    void addRenderLayer(ITileLayerGroup layer);    

    void removeRenderLayer(ITileLayerGroup layer);

    void removeRenderLayer(int index);

    public int getCanvasX();

    public int getCanvasY();
    
    // -- view window manipulation methods
    
    /*
     *
     * moves the view window by specified offsets and redraws the screen. Call with 0,0 to force screen refresh. note that the parameters are offsets to move the window by, not coordinates to move the window to. *
     */
    void moveWindow(int x, int y);
    
    void centerWindow();
    
    /**
     * Sets the renderer scale and updated the view
     * @param scale 
     */
    void setScale(double scale);
    /**
     * Gets the current rendering scale
     * @return 
     */
    double getScale();

    /*
     *
     * use this to move the window to specific coordinates, followed by moveWindow(0,0) *
     */
    public void setWindowOffsetX(int oldX);

    /*
     *
     * use this to move the window to specific coordinates, followed by moveWindow(0,0) *
     */
    void setWindowOffsetY(int oldY);

    int getWindowOffsetY();

    int getWindowOffsetX();    

    public void addObserver(Observer aThis);
    public void deleteObserver(Observer aThis);
}
