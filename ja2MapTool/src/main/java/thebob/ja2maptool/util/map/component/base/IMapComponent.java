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
package thebob.ja2maptool.util.map.component.base;

import java.util.Observer;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 *
 * @author starcatter
 */
public interface IMapComponent {

	/**
	 * Disconnects the component from the parent map controller and other components it relies on.
	 */
	void disconnect();

	/**
	 * @return the map
	 */
	IMapLayerManager getMap();

	/**
	 * @return the renderer
	 */
	ITileRendererManager getRenderer();

	/**
	 * Adds an observer to the set of observers for this object, provided that it is not the same as some observer
	 * already in the set. The order in which notifications will be delivered to multiple observers is not specified.
	 * See the class comment.
	 *
	 * @param o an observer to be added.
	 * @throws NullPointerException if the parameter o is null.
	 */
	void addObserver(Observer o);

	/**
	 * Deletes an observer from the set of observers of this object. Passing <CODE>null</CODE> to this method will have
	 * no effect.
	 *
	 * @param o the observer to be deleted.
	 */
	void deleteObserver(Observer o);

}
