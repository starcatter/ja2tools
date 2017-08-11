/* 
 * The MIT License
 *
 * Copyright 2017 the_bob.
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
package thebob.ja2maptool.util.map;

import javafx.beans.property.BooleanProperty;
import javafx.scene.input.MouseButton;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.scopes.map.ConvertMapScope;
import thebob.ja2maptool.scopes.map.MapCompositorScope;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabViewModel;
import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.map.renderer.ITileRendererControls;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerControls;
import thebob.ja2maptool.util.map.layers.map.IMapLayerControls;
import thebob.ja2maptool.util.map.controller.editors.compositor.IMapCompositorController;
import thebob.ja2maptool.util.map.controller.editors.converter.IMapConverterController;
import thebob.ja2maptool.util.map.controller.viewer.base.IMapViewerController;

/**
 * The interface that combines all of the map display functionality.
 *
 * @author the_bob
 */
public interface IMapDisplayManager extends ITileRendererControls, IMapLayerControls {

    /**
     * Returns the viewer controller, which provides functionality for scrolling and scaling the map window
     * @return 
     */
    IMapViewerController connectBasicViewer(MapViewerTabViewModel viewWindow);
    IMapViewerController connectEditorViewer(MapViewerTabViewModel viewWindow);
    
    /**
     * Returns the compositor controller, which is meant to give the compositor UI component access to necessary functionality
     * @param compositor the scope will be used for passing events between the controller and the view model
     * @return 
     */
    IMapCompositorController connectCompositor(MapCompositorScope compositor);
    
    /**
     * Returns the converter controller, which is meant to give the converter UI component access to necessary functionality
     * @param converter the scope will be used for passing events between the controller and the view model
     * @return 
     */
    IMapConverterController connectConverter(ConvertMapScope converter);

}
