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
package thebob.ja2maptool.util.map.component.placement.snippets;

import java.util.HashMap;
import java.util.Map;
import thebob.ja2maptool.util.compositor.SnippetPlacement;

/**
 *
 * @author starcatter
 */
public class MapSnippetPlacementLayer {

    Map<Integer, SnippetPlacement> placements = new HashMap<Integer, SnippetPlacement>();

    String name;
    boolean visible = true;

    public MapSnippetPlacementLayer(MapSnippetPlacementLayer cpy) {
        this.name = cpy.name + " (copy)";
        visible = cpy.visible;
        cpy.placements.forEach((cell, placement) -> {
            placements.put(cell, new SnippetPlacement(placement));
        });
    }

    public MapSnippetPlacementLayer(String name) {
        this.name = name;
    }

    public Map<Integer, SnippetPlacement> getPlacements() {
        return placements;
    }

    public void setPlacements(Map<Integer, SnippetPlacement> placements) {
        this.placements = placements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return name;
    }

}
