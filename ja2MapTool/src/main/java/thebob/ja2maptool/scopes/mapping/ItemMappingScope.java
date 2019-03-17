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
import thebob.ja2maptool.util.mapping.item.IdMapping;
import thebob.ja2maptool.util.mapping.item.ItemMapping;
import thebob.ja2maptool.util.mapping.ItemMappingFileData;
import thebob.ja2maptool.util.mapping.MappingIO;
import thebob.ja2maptool.util.mapping.item.Mapping;

public class ItemMappingScope implements Scope {

	ObservableList<Mapping> mappingList = FXCollections.observableArrayList();
	Map<Integer, Mapping> mappingIndex = new HashMap<Integer, Mapping>();
	AssetManager sourceAssets;
	AssetManager targetAssets;

	public ObservableList<Mapping> getMapping() {
		return mappingList;
	}

	public Map<Integer, Mapping> getMappingIndex() {
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

	public void mapIds(Integer srcItem, String srcName, Integer dstItem, String dstName) {
		if(srcItem == null){
			System.err.println("Attempt to remap null item to " + (dstItem != null ? dstItem : "ANOTHER NULL ITEM WTF?"));
			return;
		}

		if (mappingIndex.containsKey(srcItem)) {
			Mapping mapping = mappingIndex.get(srcItem);
			mapping.setTargetId(dstItem);
			mapping.setTargetName(dstName);
		} else {
			Mapping mapping = new IdMapping(srcItem, srcName, dstItem, dstName);
			this.mappingList.add(mapping);
			mappingIndex.put(srcItem, mapping);
		}
	}

	public void mapItems(Item srcItem, Item dstItem) {
		if(srcItem == null){
			System.err.println("Attempt to remap null item to " + (dstItem != null ? dstItem.getId() : "ANOTHER NULL ITEM WTF?"));
			return;
		}

		if (mappingIndex.containsKey(srcItem.getId())) {
			Mapping mapping = mappingIndex.get(srcItem.getId());
			mapping.setTarget(dstItem);
		} else {
			Mapping mapping = new ItemMapping(srcItem, dstItem);
			this.mappingList.add(mapping);
			mappingIndex.put(srcItem.getId(), mapping);
		}

		//System.out.println("mapItems: " + srcItem.getName() + " -> " + dstItem.getName());
	}

	public Map<Integer, Integer> getMappingAsMap() {
		Map<Integer, Integer> mappingMap = new HashMap();
		for (Mapping mapping : mappingList) {
			mappingMap.put(mapping.getSourceId(), mapping.getTargetId());
		}
		return mappingMap;
	}

	public static ItemMappingScope loadFromFile(String path, VfsAssetScope vfsAssets) {
		ItemMappingFileData mappingData = MappingIO.loadItemMapping(path);

		AssetManager sourceAssets = vfsAssets.getOrLoadAssetManager(mappingData.getSrcConfDir(), mappingData.getSrcConf());
		AssetManager targetAssets = vfsAssets.getOrLoadAssetManager(mappingData.getDstConfDir(), mappingData.getDstConf());

		ItemMappingScope scope = new ItemMappingScope();

		if (sourceAssets != null && targetAssets != null) {
			scope.setSourceAssets(sourceAssets);
			scope.setTargetAssets(targetAssets);

			Map<Integer, Integer> mapping = mappingData.getMapping();
			for (Integer id : mapping.keySet()) {
				Integer targetId = mapping.get(id);

				Item sourceItem = sourceAssets.getItems().getItem(id);
				Item targetItem = targetAssets.getItems().getItem(targetId);

				if(sourceItem != null && targetItem != null) {
                    scope.mapItems(sourceItem, targetItem);
                } else {
				    scope.mapIds(id,
                            sourceItem != null ? sourceItem.getName() : "Unknown",
                            targetId,
                            targetItem != null ? targetItem.getName() : "Unknown");
                }
			}
		} else {
			// TODO: display a prompt here to either ignore this error or pick a directory!
			System.out.println("thebob.ja2maptool.scopes.ItemMappingScope.loadFromFile(): failed to laod assets");
		}

		return scope;
	}

    public void removeMapping(Mapping selectedMapping) {
		mappingIndex.remove(selectedMapping.getSourceId());
		mappingList.remove(selectedMapping);
    }
}
