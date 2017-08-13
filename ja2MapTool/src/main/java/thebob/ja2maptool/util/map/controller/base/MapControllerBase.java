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
package thebob.ja2maptool.util.map.controller.base;

import java.util.Observable;
import java.util.Observer;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author the_bob
 */
public abstract class MapControllerBase extends Observable implements IMapController, Observer {

    private ITileRendererManager renderer;
    private IMapLayerManager map;

    public MapControllerBase(ITileRendererManager renderer, IMapLayerManager map) {
        this.renderer = renderer;
        this.map = map;

        renderer.addObserver(this);
        map.subscribe(this);

        System.out.println("thebob.ja2maptool.util.map.controller.base.MapControllerBase.<init>(): created " + this.getClass().getSimpleName());
    }

    @Override
    public void disconnect() {
        getRenderer().deleteObserver(this);
        getMap().unsubscribe(this);

        System.out.println("thebob.ja2maptool.util.map.controller.base.MapControllerBase.disconnect(): " + this.getClass().getSimpleName());
    }

    // no need to spam setChanged() all over the controller classes, it's a simple observer implementation here 
    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }
    
    // ----------

    /**
     * @return the renderer
     */
    public ITileRendererManager getRenderer() {
        return renderer;
    }

    /**
     * @return the map
     */
    public IMapLayerManager getMap() {
        return map;
    }
    
    
}
