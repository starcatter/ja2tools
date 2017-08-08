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
package thebob.ja2maptool.util.map.controller.viewer;

import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabViewModel;
import thebob.ja2maptool.util.map.controller.base.MapControllerBase;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author the_bob
 */
public abstract class MapViewerControllerBase extends MapControllerBase implements IMapViewerController {

    MapViewerTabViewModel viewWindow;

    public MapViewerControllerBase(ITileRendererManager renderer, IMapLayerManager map, MapViewerTabViewModel viewWindow) {
	super(renderer, map);
	this.viewWindow = viewWindow;
    }

    protected void zoom(int amount) {
	System.out.println("thebob.ja2maptool.util.map.controller.viewer.MapViewerControllerBase.zoom()");
	zoom(amount / 100d);
    }

    protected void zoom(double amount) {
	renderer.setScale(renderer.getScale() + amount);
    }

    // -- stuff passed through to the renderer
    @Override
    public void moveWindow(int x, int y) {
	renderer.moveWindow(x, y);
    }

    @Override
    public double getScale() {
	return renderer.getScale();
    }

    @Override
    public void setScale(double scale) {
	renderer.setScale(scale);
    }

    @Override
    public void setWindowOffsetX(int oldX) {
	renderer.setWindowOffsetX(oldX);
    }

    @Override
    public void setWindowOffsetY(int oldY) {
	renderer.setWindowOffsetY(oldY);
    }

    @Override
    public int getWindowOffsetY() {
	return renderer.getWindowOffsetY();
    }

    @Override
    public int getWindowOffsetX() {
	return renderer.getWindowOffsetX();
    }

}
