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
package thebob.ja2maptool.util.map.component.interaction.handle;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import thebob.ja2maptool.util.map.component.base.MapComponentBase;
import thebob.ja2maptool.util.map.component.interaction.IMapInteractionComponent;
import thebob.ja2maptool.util.map.component.interaction.handle.handles.base.MapHandle;
import thebob.ja2maptool.util.map.layers.cursor.ICursorLayerManager;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author starcatter
 */
public class MapHandleComponent extends MapComponentBase implements IMapHandleComponent {

	List<MapHandle> handles = new ArrayList<>();
	// cursor layer for drawing handles
	IMapInteractionComponent areas;
	ICursorLayerManager cursorLayer;

	public MapHandleComponent(ITileRendererManager renderer, IMapLayerManager map, ICursorLayerManager cursorLayer, IMapInteractionComponent areas) {
		super(renderer, map);
		this.areas = areas;
		this.cursorLayer = cursorLayer;
	}

	// -----------------
	@Override
	public void update(Observable o, Object arg) {
		// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
