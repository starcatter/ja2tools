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
package thebob.ja2maptool.scopes.mapping;

import de.saxsys.mvvmfx.Scope;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thebob.assetmanager.AssetManager;
import thebob.assetmanager.managers.items.Item;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.util.mapping.ItemMapping;
import thebob.ja2maptool.util.mapping.ItemMappingFileData;
import thebob.ja2maptool.util.mapping.MappingIO;

public class ItemMappingScope implements Scope {

	ObservableList<ItemMapping> mappingList = FXCollections.observableArrayList();
	Map<Integer, ItemMapping> mappingIndex = new HashMap<Integer, ItemMapping>();
	AssetManager sourceAssets;
	AssetManager targetAssets;

	public ObservableList<ItemMapping> getMapping() {
		return mappingList;
	}

	public Map<Integer, ItemMapping> getMappingIndex() {
		return mappingIndex;
	}

	public AssetManager getSourceAssets() {
		return sourceAssets;
	}

	public void setSourceAssets(AssetManager sourceAssets) {
		this.sourceAssets = sourceAssets;
	}

	public AssetManager getTargetAssets() {
		return targetAssets;
	}

	public void setTargetAssets(AssetManager targetAssets) {
		this.targetAssets = targetAssets;
	}

	public void mapItems(Item srcItem, Item dstItem) {
		if (mappingIndex.containsKey(srcItem.getId())) {
			ItemMapping mapping = mappingIndex.get(srcItem.getId());
			mapping.setDstItem(dstItem);
		} else {
			ItemMapping mapping = new ItemMapping(srcItem, dstItem);
			this.mappingList.add(mapping);
			mappingIndex.put(srcItem.getId(), mapping);
		}
		//System.out.println("mapItems: " + srcItem.getName() + " -> " + dstItem.getName());
	}

	public Map<Integer, Integer> getMappingAsMap() {
		Map<Integer, Integer> mappingMap = new HashMap();
		for (ItemMapping mapping : mappingList) {
			mappingMap.put(mapping.getSrcItem().getId(), mapping.getDstItem().getId());
		}
		return mappingMap;
	}

	public static ItemMappingScope loadFromFile(String path, VfsAssetScope vfsAssets) {
		ItemMappingFileData mappingData = MappingIO.loadItemMapping(path);

		AssetManager sourceAssets = vfsAssets.getOrLoadAssetManager(mappingData.getSrcConfDir(), mappingData.getSrcConf());
		AssetManager targetAssets = vfsAssets.getOrLoadAssetManager(mappingData.getDstConfDir(), mappingData.getDstConf());

		ItemMappingScope scope = new ItemMappingScope();

		if (sourceAssets != null && targetAssets != null) {
			scope.setTargetAssets(targetAssets);
			scope.setSourceAssets(sourceAssets);

			Map<Integer, Integer> mapping = mappingData.getMapping();
			for (Integer id : mapping.keySet()) {
				Integer targetId = mapping.get(id);

				Item sourceItem = sourceAssets.getItems().getItem(id);
				Item tatgetItem = targetAssets.getItems().getItem(targetId);

				scope.mapItems(sourceItem, tatgetItem);
			}
		} else {
			// TODO: display a prompt here to either ignore this error or pick a directory!
			System.out.println("thebob.ja2maptool.scopes.ItemMappingScope.loadFromFile(): failed to laod assets");
		}

		return scope;
	}
}
