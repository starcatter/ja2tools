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
package thebob.ja2maptool.util.map.component.interaction.layer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import thebob.ja2maptool.util.map.component.interaction.eventdata.MapInteractionData;
import thebob.ja2maptool.util.map.component.interaction.eventdata.MapInteractionUserdata;
import thebob.ja2maptool.util.map.component.interaction.target.IMapInteractionListener;

/**
 * An interaction layer linked to a particular map component.
 *
 * @author starcatter
 */
public class MapInteractionLayer {

    // keys define active cells, values are optional userdata. Null is fine too.
    Map<Integer, MapInteractionUserdata> cells = new HashMap<Integer, MapInteractionUserdata>();

    IMapInteractionListener component;
    boolean layerActive = true;
    boolean hovered = true;

    public MapInteractionLayer(IMapInteractionListener component) {
        this.component = component;
    }

    // -------------------------
    // active cell manipulation
    // -------------------------
    public void registerCells(int[] cells, MapInteractionUserdata userdata) {
        for (int placementCell : cells) {
            this.cells.put(placementCell, userdata);
        };
        //System.out.println("thebob.ja2maptool.util.map.component.interaction.layer.MapInteractionLayer.registerCells(): " + cells.length + " cells, " + component);
    }

    public void clearCells(int[] cells) {
        for (int placementCell : cells) {
            this.cells.remove(placementCell);
        };
        //System.out.println("thebob.ja2maptool.util.map.component.interaction.layer.MapInteractionLayer.clearCells(): " + cells.length + " cells, " + component);
    }

    public void clear() {
        this.cells.clear();
        //System.out.println("thebob.ja2maptool.util.map.component.interaction.layer.MapInteractionLayer.clear() " + component);
    }

    public Set<Integer> getCells() {
        return cells.keySet();
    }

    // -------------------------
    // interaction methods
    // -------------------------
    public boolean hoverCell(int cell, MapInteractionData data) {
        data.setUserdata(cells.get(cell));
        setHovered(true);
        return component.hoverCell(cell, data);
    }

    public boolean activateCell(int cell, MapInteractionData data) {
        data.setUserdata(cells.get(cell));
        return component.activateCell(cell, data);
    }

    public void hoverOff() {
        if (isHovered()) {
            setHovered(false);
            component.hoverOff();
        }
    }

    // -------------------------
    // query methods
    // -------------------------
    public boolean isCellRegistered(int cell) {
        return cells.containsKey(cell);
    }

    public MapInteractionUserdata getCellData(int cell) {
        return cells.get(cell);
    }

    // -------------------------
    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

}
