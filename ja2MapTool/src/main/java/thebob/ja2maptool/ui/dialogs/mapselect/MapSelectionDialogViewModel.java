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
package thebob.ja2maptool.ui.dialogs.mapselect;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.vfs.VFSConfig;
import thebob.assetloader.vfs.VirtualFileSystem;
import thebob.assetloader.vfs.accessors.VFSAccessor;
import thebob.assetmanager.AssetManager;
import thebob.ja2maptool.components.MapSelectionTreeItem;
import thebob.ja2maptool.components.TilesetMappingTreeItem;
import thebob.ja2maptool.scopes.map.MapScope;
import thebob.ja2maptool.scopes.VfsAssetScope;
import static thebob.ja2maptool.scopes.VfsAssetScope.REFRESH_CONFIGS;

/**
 *
 * @author the_bob
 */
public class MapSelectionDialogViewModel implements ViewModel {

    public static final String CLOSE_DIALOG_NOTIFICATION = "CLOSE_DIALOG_NOTIFICATION";
    public static final String VFS_LOCAL_CHANGED = "VFS_NAME_CHANGED";// TODO: handle or get rid of this
    public static final String FILE_NAME_CHANGED = "FILE_NAME_CHANGED";

    ObservableList<VFSConfig> localConfigs = FXCollections.observableArrayList();
    private String mapFileName;
    private String mapFilePath;

    ObservableList<VFSConfig> getLocalVfsList() {
	return localConfigs;
    }

    ObservableList<AssetManager> getLoadedVfsList() {
	return FXCollections.observableArrayList(vfsAssets.getManagers().values());
    }

    VfsAssetScope getVfs() {
	return vfsAssets;
    }

    public enum VfsMode {
	Local, Workspace
    }

    @InjectScope
    VfsAssetScope vfsAssets;
    @InjectScope
    MapScope mapScope;

    void selectFile(String path) {
	Path filePath = Paths.get(path);
	mapFilePath = filePath.getParent().toString();
	mapFileName = filePath.getFileName().toString();
	publish(FILE_NAME_CHANGED, mapFileName);

	Path possibleRootDir = null;
	try {
	    possibleRootDir = filePath.getParent().getParent().getParent();
	} catch (NullPointerException e) {

	}

	if (possibleRootDir != null) {
	    VirtualFileSystem vfs = new VirtualFileSystem(possibleRootDir.toString());
	    if (vfs.getConfigNames().size() > 0) {
		/*
		for( VFSConfig config : vfs.getConfigs()){		    
		    // only add configs that contain this map (in some version)
		    if( config.getFileVariants( ("\\MAPS\\" + mapFileName).toUpperCase() ).isEmpty() == false ){
			localConfigs.add(config);
		    }		    
		}
		 */  // then again, most mods contain all map files via VFS so we need better criteria

		localConfigs.addAll(vfs.getConfigs());
		return;
	    }
	}

	// TODO: handle or get rid of this
	publish(VFS_LOCAL_CHANGED, "VFS not found", false);
    }

    TreeItem<String> getMapListRoot() {
	TreeItem<String> root = new TreeItem<String>("Loaded VFS configs");
	root.setExpanded(true);

	for (AssetManager assets : vfsAssets.getManagers().values()) {
	    String managerName = assets.getVfs().getPath().getFileName().toString();
	    TreeItem<String> assetManagerNode = new TilesetMappingTreeItem(managerName, null, assets);
	    assetManagerNode.setExpanded(true);

	    for (String profile : assets.getVfs().getProfiles().keySet()) {
		TreeItem<String> profileNode = new TreeItem<String>(profile);
		profileNode.setExpanded(true);

		ArrayList<VFSAccessor> files = assets.getVfs().getProfiles().get(profile);
		files.stream().filter(f -> f.getVFSPath().startsWith("\\MAPS\\")).sorted().forEach(mapAccessor -> {
		    TreeItem<String> mapNode = new MapSelectionTreeItem(mapAccessor.getVFSPath().replace("\\MAPS\\", ""), mapAccessor, assets);;
		    profileNode.getChildren().add(mapNode);
		});

		assetManagerNode.getChildren().add(profileNode);		
	    }

	    Collections.reverse(assetManagerNode.getChildren());
	    root.getChildren().add(assetManagerNode);
	}

	return root;
    }

    // Load map file using a loaded asset manager
    void loadFile(AssetManager manager) {
	String mapAssetPath = mapFilePath + "/" + mapFileName;
	MapData data = manager.getMaps().loadMapFile(mapAssetPath);

	if (data != null) {
	    mapScope.setMapName(mapFileName);
	    mapScope.setMapAssetPath(mapAssetPath);
	    mapScope.setLoadMode(MapScope.MapLoadMode.From_File);

	    mapScope.setMapData(data);

	    mapScope.setTilesetId(data.getSettings().iTilesetID);
	    mapScope.setTileset(manager.getTilesets().getTileset(data.getSettings().iTilesetID));
	    mapScope.setMapAssets(manager);

	    mapScope.publish(MapScope.MAP_UPDATED);
	    publish(CLOSE_DIALOG_NOTIFICATION);
	    return;
	}

	System.err.println("thebob.ja2maptool.ui.dialogs.mapselect.MapSelectionDialogViewModel.loadFile(): error loading map");
    }

    // Load map file using a VFS config from parent dir
    void loadFileVFS(VFSConfig selectedConfig) {
	selectedConfig.loadConfig();
	AssetManager manager = new AssetManager(selectedConfig);

	if (manager != null) {
	    String mapAssetPath = "\\MAPS\\" + mapFileName;

	    MapData data = manager.getMaps().loadMap(mapAssetPath);
	    if (data != null) {
		mapScope.setMapName(mapFileName);
		mapScope.setMapAssetPath(mapAssetPath);
		mapScope.setLoadMode(MapScope.MapLoadMode.From_VFS);

		mapScope.setMapData(data);

		mapScope.setTilesetId(data.getSettings().iTilesetID);
		mapScope.setTileset(manager.getTilesets().getTileset(data.getSettings().iTilesetID));
		mapScope.setMapAssets(manager);

		vfsAssets.getConfigs().put(selectedConfig.getPath().getParent().toString(), new VirtualFileSystem(selectedConfig.getPath().getParent().toString()));
		vfsAssets.getManagers().put(selectedConfig.getPath().toString(), manager);
		vfsAssets.publish(REFRESH_CONFIGS);

		mapScope.publish(MapScope.MAP_UPDATED);

		publish(CLOSE_DIALOG_NOTIFICATION);
		return;
	    }
	}

	System.err.println("thebob.ja2maptool.ui.dialogs.mapselect.MapSelectionDialogViewModel.loadFileVFS(): error loading map");
    }

    void loadVfs(TreeItem<String> selectedItem) {
	MapSelectionTreeItem item = (MapSelectionTreeItem) selectedItem;
	AssetManager manager = item.getManager();
	VFSAccessor accessor = item.getAccessor();
	String mapFileName = item.getValue();

	MapData data = manager.getMaps().loadMapData(accessor.getBytes());
	if (data != null) {
	    mapScope.setMapName(mapFileName);
	    mapScope.setMapAssetPath(accessor.getVFSPath());
	    mapScope.setLoadMode(MapScope.MapLoadMode.From_VFS);

	    mapScope.setMapData(data);

	    mapScope.setTilesetId(data.getSettings().iTilesetID);
	    mapScope.setTileset(manager.getTilesets().getTileset(data.getSettings().iTilesetID));
	    mapScope.setMapAssets(manager);

	    mapScope.publish(MapScope.MAP_UPDATED);
	} else {
	    System.err.println("thebob.ja2maptool.ui.dialogs.mapselect.MapSelectionDialogViewModel.loadVfs(): error loading map");
	}
    }
}
