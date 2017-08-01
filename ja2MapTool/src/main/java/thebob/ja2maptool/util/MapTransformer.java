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
package thebob.ja2maptool.util;

import java.util.Map;
import javafx.collections.ObservableList;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.map.wrappers.WorldItemStack;
import thebob.ja2maptool.model.TileCategoryMapping;
import thebob.ja2maptool.model.TileMapping;
import thebob.ja2maptool.scopes.ConvertMapScope;

/**
 *
 * @author the_bob
 */
public class MapTransformer {

    MapData map = null;
    Map<Integer, TileCategoryMapping> tileMapping = null;
    Map<Integer, Integer> itemMapping = null;
    Integer tileset = null;

    public MapTransformer(ConvertMapScope convertMapScope) {
	map = convertMapScope.getMap().getMapData();
	tileMapping = convertMapScope.getTilesetMapping() != null ? convertMapScope.getTilesetMapping().getMappingList() : null;
	itemMapping = convertMapScope.getItemMapping() != null ? convertMapScope.getItemMapping().getMappingAsMap() : null;
	tileset = convertMapScope.getTilesetMapping() != null ? convertMapScope.getTilesetMapping().getTargetTilesetId() : map.getSettings().iTilesetID;
    }

    public MapData getMap() {
	return map;
    }

    public void setMap(MapData map) {
	this.map = map;
    }

    public Map<Integer, TileCategoryMapping> getTileMapping() {
	return tileMapping;
    }

    public void setTileMapping(Map<Integer, TileCategoryMapping> mapping) {
	this.tileMapping = mapping;
    }

    public Map<Integer, Integer> getItemMapping() {
	return itemMapping;
    }

    public void setItemMapping(Map<Integer, Integer> itemMapping) {
	this.itemMapping = itemMapping;
    }

    public Integer getTileset() {
	return tileset;
    }

    public void setTileset(Integer tileset) {
	this.tileset = tileset;
    }

    public void saveTo(String path) {
	if (map == null) {
	    return;
	}

	if (tileset != null) {
	    map.getSettings().iTilesetID = tileset;
	}

	if (tileMapping != null) {
	    applyTileRemapping();
	}

	if (itemMapping != null) {
	    applyItemRemapping();
	}

	map.saveMap(path);
    }

    public void applyTileRemapping() {
	remapLayer(map.getLayers().landLayer);
	remapLayer(map.getLayers().objectLayer);
	remapLayer(map.getLayers().structLayer);
	remapLayer(map.getLayers().shadowLayer);
	remapLayer(map.getLayers().roofLayer);
	remapLayer(map.getLayers().onRoofLayer);
    }

    private void remapLayer(IndexedElement[][] layerType) {
	for (int i = 0; i < layerType.length; i++) {
	    IndexedElement[] layers = layerType[i];

	    for (int j = 0; j < layers.length; j++) {
		IndexedElement tile = layers[j];
		ObservableList<TileMapping> mappingType = tileMapping.get(tile.type).getMappings();

		if (mappingType.size() >= tile.index) {
		    TileMapping mapping = mappingType.get(tile.index - 1);

		    tile.type = mapping.getTargetType();
		    tile.index = mapping.getTargetIndex() + 1;

		} else {
		    // TODO: fix dis here
		}

	    }
	}
    }

    private void applyItemRemapping() {
	for (WorldItemStack stack : map.getActors().getItems()) {
	    int itemId = stack.getStack().getObject().usItem.get();
	    Integer newId = itemMapping.get(itemId);
	    if (newId == null) {
		System.out.println("thebob.ja2maptool.util.MapTransformer.applyItemRemapping() remove " + itemId);
		stack.getItem().fExists.set(false);
		stack.getItem().ubNonExistChance.set((short) 100);
		stack.getStack().getObject().usItem.set(0);
		stack.getStack().getObject().ubNumberOfObjects.set((short) 0);
	    } else {
		System.out.println("thebob.ja2maptool.util.MapTransformer.applyItemRemapping() remap " + itemId + " -> " + newId);
		stack.getStack().getObject().usItem.set(newId);
	    }

	}
    }

}
