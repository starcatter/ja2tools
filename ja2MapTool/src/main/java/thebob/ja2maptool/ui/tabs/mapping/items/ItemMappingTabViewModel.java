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
package thebob.ja2maptool.ui.tabs.mapping.items;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.controlsfx.control.PropertySheet;
import thebob.assetmanager.AssetManager;
import thebob.assetmanager.managers.items.Item;
import thebob.assetmanager.managers.items.categories.ItemCategory;
import thebob.ja2maptool.components.ItemMappingTreeItem;
import thebob.ja2maptool.scopes.MainScope;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.scopes.mapping.ItemMappingScope;
import thebob.ja2maptool.util.mapping.ItemMapping;
import thebob.ja2maptool.util.mapping.MappingIO;

public class ItemMappingTabViewModel implements ViewModel {

	public static final String SELECT_MAPPING_LIST = "SELECT_MAPPING_LIST";
	public static final String SELECT_MAPPING_RIGHT = "SELECT_MAPPING_RIGHT";
	public static final String SELECT_MAPPING_LEFT = "SELECT_MAPPING_LEFT";
	public static final String UPDATE_MAPPING = "UPDATE_MAPPING";
	public static final String UPDATE_PROPS_LEFT = "UPDATE_PROPS_LEFT";
	public static final String UPDATE_PROPS_RIGHT = "UPDATE_PROPS_RIGHT";

	@InjectScope
	VfsAssetScope vfsAssets;
	@InjectScope
	MainScope mainScreen;

	@InjectScope
	ItemMappingScope mappingScope;

	ObservableList<PropertySheet.Item> propsRight;
	ObservableList<PropertySheet.Item> propsLeft;

	Map<Item, TreeItem> itemNodesLeft = new HashMap<Item, TreeItem>();
	Map<Item, TreeItem> itemNodesRight = new HashMap<Item, TreeItem>();

	TreeItem<String> root_left;
	TreeItem<String> root_right;

	public void initialize() {
		initTreePanes();
	}

	private void initTreePanes() {
		root_left = new TreeItem<>("Source items");
		root_left.setExpanded(true);
		populateTreePane(root_left, mappingScope.getSourceAssets(), itemNodesLeft);
		root_right = new TreeItem<>("Target items");
		root_right.setExpanded(true);
		populateTreePane(root_right, mappingScope.getTargetAssets(), itemNodesRight);
	}

	private void populateTreePane(TreeItem<String> root, AssetManager assets, Map<Item, TreeItem> itemNodes) {
		ItemCategory categoryRoot = assets.getItems().getCategories().getRootNode();
		Iterator<ItemCategory> iterator = categoryRoot.categoryIterator();
		insertNodes(root, iterator, itemNodes);
	}

	private boolean insertNodes(TreeItem<String> root, Iterator<ItemCategory> iterator, Map<Item, TreeItem> itemNodes) {
		boolean displayThisTree = false;
		while (iterator.hasNext()) {
			boolean displayThisNode = false;
			ItemCategory category = iterator.next();

			TreeItem<String> categoryNode = new TreeItem<>(category.getName());

			if (category.totalItemCount() > 0) {
				displayThisNode = true;
				for (Iterator<Item> items = category.itemIterator(); items.hasNext();) {
					Item item = items.next();
					ItemMappingTreeItem itemNode = new ItemMappingTreeItem(item);
					categoryNode.getChildren().add(itemNode);
					if (itemNodes != null) {
						itemNodes.put(item, itemNode);
					}
				}
			}

			if (category.subCategoryCount() > 0) {
				Iterator<ItemCategory> subCategories = category.categoryIterator();

				boolean worthDisplaying = insertNodes(categoryNode, subCategories, itemNodes);
				displayThisNode = displayThisNode || worthDisplaying;
			}

			if (displayThisNode) {
				root.getChildren().add(categoryNode);
			}
			displayThisTree = displayThisTree || displayThisNode;
		}
		return displayThisTree;
	}

	// auto mapping
	void autoMapping() {
		mappingScope.getMapping().clear();
		
		ItemAutoMapper mapper = new ItemAutoMapper( mappingScope.getSourceAssets().getItems(),  mappingScope.getTargetAssets().getItems());
		Map<Item, Item> mapping = mapper.getMapping();
		mapping.forEach(mappingScope::mapItems);	

		publish(UPDATE_MAPPING);
	}

	// load/save
	void loadMapping(String path) {
		ItemMappingScope scope = ItemMappingScope.loadFromFile(path, vfsAssets);
		mainScreen.freeItemMappingScope(mappingScope);
		mainScreen.registerItemMappingScope(scope);
		mappingScope = scope;
	}

	void saveMapping(String path) {
		MappingIO.saveItemMapping(path, mappingScope);
	}

	// View bindings
	public ObservableList<PropertySheet.Item> getPropsRight() {
		return propsRight;
	}

	public void setPropsRight(ObservableList<PropertySheet.Item> propsRight) {
		this.propsRight = propsRight;
	}

	public ObservableList<PropertySheet.Item> getPropsLeft() {
		return propsLeft;
	}

	public void setPropsLeft(ObservableList<PropertySheet.Item> propsLeft) {
		this.propsLeft = propsLeft;
	}

	TreeItem<String> getRootLeft() {
		return root_left;
	}

	TreeItem<String> getRootRight() {
		return root_right;
	}

	void setItemMapping(Item itemLeft, Item itemRight, TreeItem node) {
		mappingScope.mapItems(itemLeft, itemRight);
		publish(UPDATE_MAPPING);
	}

	void selectedSource(Item item) {
		int srcId = item.getId();
		ItemMapping oldMapping = mappingScope.getMappingIndex().get(srcId);
		if (oldMapping == null) {
			return;
		}

		Integer oldMappingId = oldMapping.getDstItem().getId();
		if (oldMappingId != null) {
			Item mappedItem = mappingScope.getTargetAssets().getItems().getItem(oldMappingId);
			publish(SELECT_MAPPING_RIGHT, itemNodesRight.get(mappedItem));
			publish(SELECT_MAPPING_LIST, oldMapping);
		}
	}

	ObservableList<ItemMapping> getMappingList() {
		return mappingScope.getMapping();
	}

	void showMapping(ItemMapping selectedMapping) {
		if (selectedMapping == null) {
			return;
		}

		Item src = selectedMapping.getSrcItem();
		Item dst = selectedMapping.getDstItem();

		publish(SELECT_MAPPING_LEFT, itemNodesLeft.get(src));
		publish(SELECT_MAPPING_RIGHT, itemNodesRight.get(dst));
		publish(SELECT_MAPPING_LIST, selectedMapping);
	}
}
