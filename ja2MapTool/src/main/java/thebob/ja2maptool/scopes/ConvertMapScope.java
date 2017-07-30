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
package thebob.ja2maptool.scopes;

import de.saxsys.mvvmfx.Scope;

/**
 *
 * @author the_bob
 */
public class ConvertMapScope implements Scope {

    public static final String SCOPE_SELECTED = "SCOPE_SELECTED";
    public static final String MAP_UPDATED = "MAP_UPDATED";

    // loaded map
    MapScope map = new MapScope();

    // Tileset mapping data
    TilesetMappingScope tilesetMapping = null;

    // Item mapping data
    ItemMappingScope itemMapping = null;

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
    
    
}