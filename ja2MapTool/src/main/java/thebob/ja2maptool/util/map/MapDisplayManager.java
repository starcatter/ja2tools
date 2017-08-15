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
package thebob.ja2maptool.util.map;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import java.util.Observable;
import java.util.Observer;
import javafx.beans.property.BooleanProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.scopes.map.ConvertMapScope;
import thebob.ja2maptool.scopes.map.MapCompositorScope;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabViewModel;
import thebob.ja2maptool.util.map.controller.base.IMapController;
import thebob.ja2maptool.util.map.controller.editors.compositor.IMapCompositorController;
import thebob.ja2maptool.util.map.controller.editors.compositor.MapCompositorController;
import thebob.ja2maptool.util.map.controller.editors.converter.IMapConverterController;
import thebob.ja2maptool.util.map.controller.editors.converter.MapConverterController;
import thebob.ja2maptool.util.map.controller.viewer.MapBrowserViewerController;
import thebob.ja2maptool.util.map.controller.viewer.MapEditorViewerController;
import thebob.ja2maptool.util.map.controller.viewer.base.IMapViewerController;
import thebob.ja2maptool.util.map.events.MapEvent;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.layers.map.MapLayer;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;
import thebob.ja2maptool.util.map.renderer.TileRenderer;

/**
 * MapDisplayManager is the replacement for OldMapRenderer, intended to
 * structure its functionality a bit better.
 *
 * Currently its major components are:
 *
 * - the TileRenderer, responsible for moving around the view window and
 * displaying stuff
 *
 * - the MapLayer, responsible for loading and manipulating the map data
 *
 * MapDisplayManager uses controllers (IMapController) to provide access to most
 * of its functionality.
 *
 * @author the_bob
 */
public class MapDisplayManager implements IMapDisplayManager, Observer {

    private final ITileRendererManager renderer = new TileRenderer();
    private final IMapLayerManager map = new MapLayer();

    // keep track of the current Canvas, we need it to intercept mouse/keyboard events
    Canvas canvas = null;

    private final ClassToInstanceMap<IMapController> controllers = MutableClassToInstanceMap.create();

    public MapDisplayManager() {
        renderer.addObserver(this);
        map.subscribe(this);

        renderer.addRenderLayer(map);
    }

    // -------------------    
    @Override
    public void update(Observable o, Object arg) {
        if (arg == null || !(arg instanceof MapEvent)) {
            System.out.println("thebob.ja2maptool.util.map.MapDisplayManager.update() got weird message from " + o);
            return;
        }
    }

    // -- renderer controls
    @Override
    public void setCanvas(Canvas canvas) {
        if (canvas != this.canvas) {
            this.canvas = canvas;
            canvas.addEventFilter(MouseEvent.ANY, event -> {
                controllers.forEach((t, c) -> {
                    c.mouseEvent(event);
                });
            });
            canvas.addEventFilter(KeyEvent.ANY, event -> {
                controllers.forEach((t, c) -> {
                    c.keyEvent(event);
                });
            });
        }
        renderer.setCanvas(canvas);
    }

    // -- map layer controls
    @Override
    public void loadMap(MapData mapData) {
        map.loadMap(mapData);
    }

    @Override
    public void setMapTileset(Tileset tileset) {
        map.setMapTileset(tileset);
    }

    @Override
    public void setMapLayerButtons(BooleanProperty[] viewerButtons) {
        map.setMapLayerButtons(viewerButtons);
    }

    // -- controller access
    private <E extends IMapController> E registerController(E controller, Class controllerType) {
        if (controllers.containsKey(controllerType)) {
            controllers.remove(controllerType).disconnect();
        }

        controllers.put(controllerType, controller);
        return controller;
    }

    // viewers
    @Override
    public IMapViewerController connectBasicViewer(MapViewerTabViewModel viewWindow) {
        return registerController(new MapBrowserViewerController(renderer, map, viewWindow), IMapViewerController.class);
    }

    public IMapViewerController connectEditorViewer(MapViewerTabViewModel viewWindow) {
        return registerController(new MapEditorViewerController(renderer, map, viewWindow), IMapViewerController.class);
    }

    // editors
    @Override
    public IMapCompositorController connectCompositor(MapCompositorScope compositor) {
        return registerController(new MapCompositorController(renderer, map, compositor), IMapCompositorController.class);
    }

    @Override
    public IMapConverterController connectConverter(ConvertMapScope converter) {
        return registerController(new MapConverterController(renderer, map, converter), IMapConverterController.class);
    }

    @Override
    public void shutdown() {
        renderer.shutdown();
    }

}
