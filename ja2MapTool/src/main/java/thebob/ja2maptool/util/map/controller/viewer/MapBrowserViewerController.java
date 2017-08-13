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
package thebob.ja2maptool.util.map.controller.viewer;

import java.util.Observable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabViewModel;
import thebob.ja2maptool.util.map.events.MapEvent;
import thebob.ja2maptool.util.map.controller.viewer.base.MapViewerControllerBase;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 * Simple viewer implementation that scrolls the map toward click location
 * within the canvas window.
 *
 * @author the_bob
 */
public class MapBrowserViewerController extends MapViewerControllerBase {

    public MapBrowserViewerController(ITileRendererManager renderer, IMapLayerManager map, MapViewerTabViewModel viewWindow) {
        super(renderer, map, viewWindow);
    }

    public void update(Observable o, Object arg) {
        if (arg == null || !(arg instanceof MapEvent)) {
            System.out.println("thebob.ja2maptool.util.map.MapDisplayManager.update() got weird message from " + o);
            return;
        }
    }

    @Override
    public void mouseEvent(MouseEvent e) {
        if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
            // move the window toward the click location
            double dx = e.getX();
            double dy = e.getY();

            double wx = getRenderer().getCanvasX() / 2d;
            double wy = getRenderer().getCanvasY() / 2d;

            double deltaX = (dx - wx) / wx;
            double deltaY = (dy - wy) / wx;

            double transX = deltaX / 2 + deltaY / 2;
            double transY = deltaY - deltaX / 2;

            moveWindow((int) (transX * 10d), (int) (transY * 10d));
        }
    }

    @Override
    public void keyEvent(KeyEvent e) {
    }

    @Override
    public void disconnect() {
        System.out.println("thebob.ja2maptool.util.map.controller.viewer.MapBrowserViewerController.disconnect()");
    }

}
