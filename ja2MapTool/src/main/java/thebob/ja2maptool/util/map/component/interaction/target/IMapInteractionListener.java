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
package thebob.ja2maptool.util.map.component.interaction.target;

import thebob.ja2maptool.util.map.component.interaction.eventdata.MapInteractionData;

/**
 * An interface for registering components as interactive on the map surface,
 * implementing this interface allows a map component to register with the
 * interaction component and define interactive cells on the map.
 *
 * @author starcatter
 */
public interface IMapInteractionListener {

    /**
     * triggers a hover event over this cell
     *
     * @param cell cell number
     * @param data optional userdata assigned to this cell
     * @return true if the event was consumed
     */
    boolean hoverCell(int cell, MapInteractionData data);
    
    /**
     * Informs the component the cursor left its registered cells
     * @return 
     */
    void hoverOff();

    /**
     * triggers an activation event (probably a click) over this cell
     *
     * @param cell cell number
     * @param data optional userdata assigned to this cell
     * @return true if the event was consumed
     */
    boolean activateCell(int cell, MapInteractionData data);
}
