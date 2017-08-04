/*
 * Copyright (C) 2017 the_bob.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
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
