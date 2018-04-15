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
package thebob.ja2maptool.util.map.component.interaction;

import thebob.ja2maptool.util.map.component.interaction.eventdata.MapInteractionData;
import thebob.ja2maptool.util.map.component.interaction.layer.MapInteractionLayer;
import thebob.ja2maptool.util.map.component.interaction.target.IMapInteractionListener;

/**
 *
 * @author starcatter
 */
public interface IMapInteractionComponent {

    /**
     * Returns an interaction layer mapped to the component passed to the
     * method. If no layer exists yet it will be created. If no longer needed,
     * the layer can be cleared to effectively disable it.
     *
     * @param self the component to be notified if user interacts with cells on
     * this layer
     * @return interaction layer for the component.
     */
    MapInteractionLayer getLayer(IMapInteractionListener self);

    /**
     * Call this after performing any changes on interaction layers, this method
     * rebuilds the main index of interactive cells for all layers
     */
    void refreshLayers();

    /**
     * Called by the map cursor component whenever the cursor is moved or
     * dragged. Check the interaction data for pressed buttons to identify
     * dragging.
     *
     * @param cell
     * @param data
     */
    void hoverCell(int cell, MapInteractionData data);

    /**
     * * Called by the map cursor component on mouse click
     *
     * @param cell
     * @param data
     */
    boolean activateCell(int cell, MapInteractionData data);
}
