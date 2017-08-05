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
package thebob.ja2maptool.scopes.map;

import de.saxsys.mvvmfx.Scope;
import java.util.ArrayList;
import java.util.List;
import thebob.ja2maptool.model.SnippetPlacement;
import thebob.ja2maptool.util.compositor.SelectedTiles;

public class MapCompositorScope implements Scope {
    MapScope map = new MapScope();
    MapSnippetScope loadedSnippets = null;
    List<SnippetPlacement> placedSnippets = new ArrayList<SnippetPlacement>();

    public MapScope getMap() {
	return map;
    }

    public void setMap(MapScope map) {
	this.map = map;
    }

    public MapSnippetScope getLoadedSnippets() {
	return loadedSnippets;
    }

    public void setLoadedSnippets(MapSnippetScope loadedSnippets) {
	this.loadedSnippets = loadedSnippets;
    }

    public List<SnippetPlacement> getPlacedSnippets() {
	return placedSnippets;
    }

    public void setPlacedSnippets(List<SnippetPlacement> placedSnippets) {
	this.placedSnippets = placedSnippets;
    }
    
    
}
