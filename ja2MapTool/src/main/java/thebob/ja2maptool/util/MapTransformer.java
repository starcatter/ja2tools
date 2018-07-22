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
package thebob.ja2maptool.util;

import javafx.collections.ObservableList;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.map.core.components.IndexedElement;
import thebob.assetloader.map.wrappers.ObjectStack;
import thebob.assetloader.map.wrappers.SoldierCreate;
import thebob.assetloader.map.wrappers.WorldItemStack;
import thebob.assetmanager.AssetManager;
import thebob.assetmanager.managers.items.Item;
import thebob.ja2maptool.model.TileCategoryMapping;
import thebob.ja2maptool.model.TileMapping;
import thebob.ja2maptool.scopes.map.ConvertMapScope;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.mapping.ItemMappingFileData;
import thebob.ja2maptool.util.mapping.TileMappingFileData;

import java.util.List;
import java.util.Map;

import static thebob.ja2maptool.util.MapTransformer.UnmappedAction.RemoveIfMissing;

/**
 * @author the_bob
 */
public class MapTransformer {

    public enum UnmappedAction {
        Remove,
        RemoveIfMissing,
        Ignore
    }

    AssetManager mapAssets = null;
    AssetManager targetAssets = null;

    MapData map = null;
    Map<Integer, TileCategoryMapping> tileMapping = null;
    Map<Integer, Integer> itemMapping = null;
    Integer tileset = null;

    UnmappedAction unmappedAction = RemoveIfMissing;

    StringBuilder remapLog = new StringBuilder();

    private void log(String text){
        remapLog.append(text);
        remapLog.append("\n");
    }

    public MapTransformer(ConvertMapScope convertMapScope) {
        mapAssets = convertMapScope.getMap().getMapAssets();
        targetAssets = convertMapScope.getItemMapping().getTargetAssets();

        map = convertMapScope.getMap().getMapData();
        tileMapping = convertMapScope.getTilesetMapping() != null ? convertMapScope.getTilesetMapping().getMappingList() : null;
        itemMapping = convertMapScope.getItemMapping() != null ? convertMapScope.getItemMapping().getMappingAsMap() : null;
        tileset = convertMapScope.getTilesetMapping() != null ? convertMapScope.getTilesetMapping().getTargetTilesetId() : map.getSettings().iTilesetID;
    }

    public MapTransformer(AssetManager sourceAssets, AssetManager targetAssets, TileMappingFileData tileMappingFileData, ItemMappingFileData itemMappingFileData, MapData mapData) {
        mapAssets = sourceAssets;
        this.targetAssets = targetAssets;

        map = mapData;

        tileMapping = tileMappingFileData != null
                ? tileMappingFileData.getMappingList()
                : null;

        itemMapping = itemMappingFileData != null
                ? itemMappingFileData.getMapping()
                : null;

        tileset = tileMappingFileData != null
                ? tileMappingFileData.getTargetTilesetId()
                : mapData.getSettings().iTilesetID;
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

    public String saveTo(String path) {
        getRemappedData(true).saveMap(path);
        return remapLog.toString();
    }

    public void remapSnippet(SelectedTiles snippet) {
        if (tileMapping != null) {
            for (IndexedElement[][] layer : snippet.getLayers()) {
                remapLayer(layer);
            }
        }
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
        log("thebob.ja2maptool.util.MapTransformer.applyItemRemapping() - WORLD ITEMS:");

        for (WorldItemStack stack : map.getActors().getItems()) {
            remapWorldItemStack(stack);
        }

        log("thebob.ja2maptool.util.MapTransformer.applyItemRemapping() - SOLDIERS:");
        List<SoldierCreate> soldierPlacements = map.getActors().getSoldierPlacements();
        for (SoldierCreate soldierPlacement : soldierPlacements) {
            if (soldierPlacement.isDetailed()) {
                log("== Profile: " + soldierPlacement.getDetailedPlacementInfo().ubProfile.get() + ", Team " + soldierPlacement.getDetailedPlacementInfo().bTeam.get());
                soldierPlacement.getDetailedPlacementInventory().forEach((ObjectStack stack) -> {
                    if (stack.getObject().usItem.get() != 0) {
                        remapObjectStack(stack);
                    }
                });
            }
        }

    }

    private Integer getRemappedItemId(int itemId) {
        Item item = mapAssets.getItems().getItem(itemId);

        String itemName = item != null
                ? item.getName()
                : "[UNKNOWN ITEM]";

        Integer newId = itemMapping.get(itemId);
        String newItemName = newId != null
                ? targetAssets.getItems().getItem(newId).getName()
                : "NULL";

        if (newId == null) {
            Item targetItem = targetAssets.getItems().getItem(itemId);

            switch (unmappedAction) {
                case Remove:
                    log("\t [remove] => " + itemId + "(" + itemName + ") -> [NULL]");
                    return null;

                case RemoveIfMissing:
                    if (targetItem != null) {
                        log("\t [skip] => " + itemId + "(" + itemName + ") -> " + itemId + " (" + targetItem.getName() + ")");
                        return itemId;
                    } else {
                        log("\t [remove] => " + itemId + "(" + itemName + ") -> [NULL]");
                        return null;
                    }

                case Ignore:
                    if (targetItem != null) {
                        log("\t [skip] => " + itemId + "(" + itemName + ") -> " + itemId + " (" + targetItem.getName() + ")");
                        return itemId;
                    } else {
                        log("\t [skip] => " + itemId + "(" + itemName + ") -> " + itemId + " (ERROR)");
                        return null;
                    }
                default:
                    return null;
            }
        } else {
            log("\t [map] => " + itemId + "(" + itemName + ") -> " + newId + "(" + newItemName + ")");
            return newId;
        }
    }

    private void remapObjectStack(ObjectStack stack) {
        int itemId = stack.getObject().usItem.get();
        Integer newId = getRemappedItemId(itemId);

        if (newId == null) {
            stack.getObject().usItem.set(0);
            stack.getObject().ubNumberOfObjects.set((short) 0);
        } else {
            stack.getObject().usItem.set(newId);
        }
    }

    private void remapWorldItemStack(WorldItemStack stack) {
        int itemId = stack.getStack().getObject().usItem.get();
        Integer newId = getRemappedItemId(itemId);

        if (newId == null) {
            stack.getItem().fExists.set(false);
            stack.getItem().ubNonExistChance.set((short) 100);
            stack.getStack().getObject().usItem.set(0);
            stack.getStack().getObject().ubNumberOfObjects.set((short) 0);
        } else {
            stack.getStack().getObject().usItem.set(newId);
        }
    }


    public MapData getRemappedData(boolean preview) {
        if (map == null) {
            return null;
        }

        if (preview) { // loads a copy of the input map and does the remapping there
            map.getByteBuffer().rewind();
            map = mapAssets.getMaps().loadMapData(map.getByteBuffer());
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

        return map;
    }

}
