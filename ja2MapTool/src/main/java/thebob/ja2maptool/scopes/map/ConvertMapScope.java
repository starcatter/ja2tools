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

import thebob.ja2maptool.scopes.mapping.ItemMappingScope;
import thebob.ja2maptool.scopes.mapping.TilesetMappingScope;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import thebob.ja2maptool.util.MapTransformer;
import thebob.ja2maptool.util.compositor.SelectedTiles;

/**
 *
 * @author the_bob
 */
public class ConvertMapScope implements Scope {

    public static final String SCOPE_SELECTED = "SCOPE_SELECTED";
    public static final String MAP_UPDATED = "MAP_UPDATED";
    public static final String SNIPPETS_UPDATED = "SNIPPETS_UPDATED";

    // loaded map
    MapScope map = new MapScope();

    // selected parts of the map
    MapSnippetScope snippets = null;

    // Tileset mapping data
    TilesetMappingScope tilesetMapping = null;

    // Item mapping data
    ItemMappingScope itemMapping = null;

    // compositor checkboxes 2
    BooleanProperty remap_in = new SimpleBooleanProperty(false);
    BooleanProperty remap_out = new SimpleBooleanProperty(true);
    BooleanProperty remap_sel = new SimpleBooleanProperty(true);

    public MapScope getMap() {
	return map;
    }

    public void setMap(MapScope map) {
	this.map = map;
    }

    public TilesetMappingScope getTilesetMapping() {
	return tilesetMapping;
    }

    public void setTilesetMapping(TilesetMappingScope tilesetMapping) {
	this.tilesetMapping = tilesetMapping;
    }

    public ItemMappingScope getItemMapping() {
	return itemMapping;
    }

    public void setItemMapping(ItemMappingScope itemMapping) {
	this.itemMapping = itemMapping;
    }

    public MapSnippetScope getSnippets() {
	return snippets;
    }

    public void setSnippets(MapSnippetScope snippets) {
	this.snippets = snippets;
    }

    @Override
    public String toString() {
	return "ConvertMapScope{" + "map=" + map + ", snippets=" + snippets + ", tilesetMapping=" + tilesetMapping + ", itemMapping=" + itemMapping + '}';
    }

    public boolean hasSelection() {
	return map.getSelection() != null;
    }

    public BooleanProperty getRemap_in() {
	return remap_in;
    }

    public BooleanProperty getRemap_out() {
	return remap_out;
    }

    public BooleanProperty getRemap_sel() {
	return remap_sel;
    }

    public SelectedTiles getSelection() {
	if (map.getSelection() != null) {
	    if (remap_sel.get() && tilesetMapping != null) {
		SelectedTiles selection = new SelectedTiles(map.getSelection());
		MapTransformer transformer = new MapTransformer(this);
		transformer.remapSnippet(selection);
		return selection;
	    } else {
		return map.getSelection();
	    }

	}
	return null;
    }

}
