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
package thebob.ja2maptool.ui.tabs.mapping.setup;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.scene.control.TreeItem;
import thebob.assetloader.tileset.Tileset;
import thebob.assetmanager.AssetManager;
import thebob.ja2maptool.components.TilesetMappingTreeItem;
import static thebob.ja2maptool.components.TilesetMappingTreeItem.MAP_ITEMS;
import thebob.ja2maptool.scopes.mapping.ItemMappingScope;
import thebob.ja2maptool.scopes.MainScope;
import thebob.ja2maptool.scopes.mapping.TilesetMappingScope;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.ui.tabs.mapping.items.ItemMappingTabView;
import thebob.ja2maptool.ui.tabs.mapping.tileset.TilesetMappingTabView;

public class MappingSetupTabViewModel implements ViewModel {

    @InjectScope
    VfsAssetScope vfsAssets;
    
    @InjectScope
    MainScope mainScreen;
    
   public void initialize() {
        vfsAssets.subscribe(VfsAssetScope.UPDATE_MAP_SCREEN, (key, payload) -> {
            populateMappingSetupScreen();
        });
	populateMappingSetupScreen();
    }
    
    TreeItem<String> mapLeftRoot = new TreeItem<String>("Mapping source");
    TreeItem<String> mapRightRoot = new TreeItem<String>("Mapping target");
    
    TreeItem<String> getMapLeftRoot() {
	return mapLeftRoot;
    }

    TreeItem<String> getMapRightRoot() {
	return mapRightRoot;
    }

    void populateMappingSetupScreen() {
	
	if (vfsAssets.getManagers().size() == 0) {
	    return;
	}

	populateMapSetupScreenNode(mapLeftRoot);
	populateMapSetupScreenNode(mapRightRoot);
    }

    void populateMapSetupScreenNode(TreeItem<String> node) {
	if (vfsAssets.getManagers().size() == 0) {
	    return;
	}

	node.getChildren().clear();

	for (AssetManager assets : vfsAssets.getManagers().values()) {
	    String managerName = assets.getVfs().getPath().getFileName().toString();
	    TreeItem<String> assetManagerNode = new TreeItem<String>(managerName);
	    assetManagerNode.setExpanded(true);

	    String itemNodeName = "Items (" + assets.getItems().getItemCount() + ")";
	    TreeItem<String> itemsNode = new TilesetMappingTreeItem(itemNodeName, MAP_ITEMS, assets);
	    assetManagerNode.getChildren().add(itemsNode);

	    for (Integer tid : assets.getTilesets().getTilesetIds()) {
		Tileset tileset = assets.getTilesets().getTileset(tid);
		String tilesetName = "tileset " + tid + ":\t" + tileset.getName();
		TreeItem<String> tilesetNode = new TilesetMappingTreeItem(tilesetName, tid, assets);

		assetManagerNode.getChildren().add(tilesetNode);
	    }

	    node.getChildren().add(assetManagerNode);	    
	}
	node.setExpanded(true);
    }

    public void createMappingTab(TreeItem<String> sourceItem, TreeItem<String> targetItem) {
	if (!(sourceItem instanceof TilesetMappingTreeItem) || !(targetItem instanceof TilesetMappingTreeItem)) {
	    return;
	}

	TilesetMappingTreeItem source = (TilesetMappingTreeItem) sourceItem;
	TilesetMappingTreeItem target = (TilesetMappingTreeItem) targetItem;

	Integer sourceId = source.getTilesetId();
	Integer targetId = target.getTilesetId();

	AssetManager sourceAssets = source.getManager();
	AssetManager targetAssets = target.getManager();
	
	if (sourceId == MAP_ITEMS && targetId == MAP_ITEMS) {
	    ItemMappingScope itemMappingScope = new ItemMappingScope();
	    
	    itemMappingScope.setSourceAssets(sourceAssets);
	    itemMappingScope.setTargetAssets(targetAssets);
	    
	    mainScreen.publish(MainScope.NEW_TAB, new Object[]{ItemMappingTabView.class,"Map items",itemMappingScope});
	    mainScreen.registerItemMappingScope(itemMappingScope);
	} else {
	    TilesetMappingScope tilesetMappingScope = new TilesetMappingScope();
	    
	    tilesetMappingScope.setSourceAssets(sourceAssets);
	    tilesetMappingScope.setSourceTilesetId(sourceId);

	    tilesetMappingScope.setTargetAssets(targetAssets);
	    tilesetMappingScope.setTargetTilesetId(targetId);

	    tilesetMappingScope.setSourceTileset(sourceAssets.getTilesets().getTileset(sourceId));
	    tilesetMappingScope.setTargetTileset(targetAssets.getTilesets().getTileset(targetId));

	    mainScreen.publish(MainScope.NEW_TAB, new Object[]{TilesetMappingTabView.class,"Map tileset",tilesetMappingScope});
	    mainScreen.registerTilesetMappingScope(tilesetMappingScope);
	}
    }
    
    
}