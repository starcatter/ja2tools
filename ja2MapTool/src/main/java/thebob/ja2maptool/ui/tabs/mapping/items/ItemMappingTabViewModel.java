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
package thebob.ja2maptool.ui.tabs.mapping.items;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import ja2.xml.items.armor.ARMOURLIST;
import ja2.xml.items.explosives.EXPLOSIVELIST;
import ja2.xml.items.lbe.LOADBEARINGEQUIPMENTLIST;
import ja2.xml.items.magazines.MAGAZINELIST;
import ja2.xml.items.weapons.WEAPONLIST;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import org.controlsfx.control.PropertySheet;
import thebob.assetmanager.AssetManager;
import thebob.assetmanager.managers.items.Item;
import thebob.assetmanager.managers.items.categories.ItemCategory;
import thebob.assetmanager.managers.items.categories.ItemCategoryModel;
import thebob.assetmanager.managers.items.categories.ItemClassEnum;
import static thebob.assetmanager.managers.items.categories.ItemClassEnum.ItemClassMap;
import thebob.ja2maptool.components.ItemMappingTreeItem;
import thebob.ja2maptool.scopes.ItemMappingScope;
import thebob.ja2maptool.scopes.MainScope;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.util.ItemMapping;
import thebob.ja2maptool.util.MappingIO;

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

	    if (category.itemCount() > 0) {
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
	for (Item item : mappingScope.getSourceAssets().getItems().getItems().values()) {
	    int id = item.getId();
	    long classId = item.getItemType();

	    ItemCategory targetClass = null;
	    ItemCategoryModel categoryModel = mappingScope.getTargetAssets().getItems().getCategories();

	    ItemClassEnum itemClass = ItemClassMap.get(classId);
	    if (itemClass != null) {
		switch (itemClass) {
		    case Ammo:
			MAGAZINELIST.MAGAZINE mag = item.getMagazineData();
			if (mag != null) {
			    HashMap<Integer, ItemCategory> magMap = categoryModel.getMagCaliberMap().get((int) mag.getUbMagType());
			    if (magMap != null) {
				targetClass = magMap.get((int) mag.getUbCalibre());
			    }
			}
			break;
		    case Gun:
			WEAPONLIST.WEAPON gun = item.getWeaponData();
			if (gun != null) {
			    Short gunType = gun.getUbWeaponType();
			    if (gunType != null) {
				HashMap<Integer, ItemCategory> gunMap = categoryModel.getGunCaliberMap().get((int) gunType);
				if (gunMap != null) {
				    targetClass = gunMap.get((int) gun.getUbCalibre());
				}
			    }
			}
			break;

		    case Grenade:
			targetClass = categoryModel.getExplosionTypeMap().get((int) item.getExplosiveData().getUbType());
			break;

		    case Bomb:
			targetClass = categoryModel.getExplosionTypeMap().get((int) item.getExplosiveData().getUbType());
			break;

		    case Armour:
			targetClass = categoryModel.getArmorClassMap().get((int) item.getArmorData().getUbArmourClass());
			break;

		    case Load_Bearing_Equipment:
			targetClass = categoryModel.getLbeClassMap().get((int) item.getLbeData().getLbeClass());
			break;

		    case Kit:
		    case Face_Item:
		    case Misc:
			if (item.getFoodType() > 0) {
			    targetClass = categoryModel.getFood();
			} else if (item.getDrugType() > 0) {
			    targetClass = categoryModel.getDrugs();
			} else if (item.getClothesType() > 0) {
			    targetClass = categoryModel.getClothes();
			}
			break;

		    default:
			targetClass = categoryModel.getItemClassMap().get(classId);
		}

		// System.out.println("thebob.ja2maptool.ui.tabs.mapping.items.ItemMappingTabViewModel.autoMapping() " + item.getName());
		// System.out.println("thebob.ja2maptool.ui.tabs.mapping.items.ItemMappingTabViewModel.autoMapping() " + targetClass);
		if (targetClass != null) {
		    ImmutableListMultimap<Integer, Item> itemsByCoolness = Multimaps.index(targetClass.itemIterator(), candidate -> {
			return candidate.getCoolness();
		    });

		    for (int i = 0; i < 25; i++) {
			if (itemsByCoolness.get(item.getCoolness() + i) != null && itemsByCoolness.get(item.getCoolness() + i).size() > 0) {
			    ImmutableList<Item> candidateList = itemsByCoolness.get(item.getCoolness() + i);
			    mappingScope.mapItems(item, candidateList.get(0));
			    break;
			} else if (itemsByCoolness.get(item.getCoolness() - i) != null && itemsByCoolness.get(item.getCoolness() - i).size() > 0) {
			    ImmutableList<Item> candidateList = itemsByCoolness.get(item.getCoolness() - i);
			    mappingScope.mapItems(item, candidateList.get(0));
			    break;
			}
		    }
		}
	    }
	}
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
