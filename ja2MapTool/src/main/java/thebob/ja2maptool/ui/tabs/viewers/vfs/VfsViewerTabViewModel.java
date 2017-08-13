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
package thebob.ja2maptool.ui.tabs.viewers.vfs;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TreeItem;
import thebob.assetloader.vfs.VFSConfig;
import thebob.assetloader.vfs.accessors.SLFAccessor;
import thebob.assetloader.vfs.accessors.VFSAccessor;
import thebob.assetmanager.AssetManager;
import thebob.ja2maptool.scopes.map.MapScope;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.scopes.view.VfsBrowserScope;

/**
 *
 * @author the_bob
 */
public class VfsViewerTabViewModel implements ViewModel {

    public static final String PREVIEW_OPTIONS_UPDATED = "PREVIEW_OPTIONS_UPDATED";
    public static final String PREVIEW_REQUESTED = "PREVIEW_REQUESTED";

    @InjectScope
    VfsAssetScope vfsAssets;
    
    @InjectScope
    VfsBrowserScope scope;

    VFSConfig config;
    AssetManager assets;
    TreeItem<String> rootItem = new TreeItem<String>("root?");

    ObservableList<VFSAccessor> variantsList = FXCollections.observableArrayList();

    public void initialize() {
	System.out.println("thebob.ja2Vfstool.ui.tabs.viewers.vfs.VfsViewerTabViewModel.initialize()");
	config = scope.getConfig();
	assets = vfsAssets.getOrLoadAssetManager(config.getPath().getParent().toString(), config.getPath().getFileName().toString());	// should be loaded already
	rootItem.setExpanded(true);
	rootItem.setValue(config.getPath().toString());
    }

    public VFSConfig getConfig() {
	return config;
    }

    public void setConfig(VFSConfig config) {
	this.config = config;
    }

    TreeItem<String> getListRoot() {
	return rootItem;
    }

    String getConfigName() {
	return config.getPath().getFileName().toString();
    }

    void populateVariants(TreeItem<String> selectedItem) {
	variantsList.clear();
	//publish(PREVIEW_OPTIONS_UPDATED, false, false, false);

	if (selectedItem != null && selectedItem.isLeaf()) {
	    String path = getLeafPath(selectedItem);
	    System.out.println("thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabViewModel.populateVariants(): " + selectedItem);
	    VFSAccessor lastVariant = null;
	    
	    for (VFSAccessor variant : config.getFileVariants(path)) {
		variantsList.add(variant);
		lastVariant = variant;
	    }	    	    
	}
    }

    ObservableList<VFSAccessor> getVariantsList() {
	return variantsList;
    }

    enum DetectedAssetType {
	STI,
	SLF,
	Map,
	Text,
	Unknown;
    }

    DetectedAssetType getAssetType(VFSAccessor selectedItem) {
	if (selectedItem != null) {
	    String resourcePath;
	    if (selectedItem instanceof SLFAccessor) {
		SLFAccessor slf = (SLFAccessor) selectedItem;
		resourcePath = slf.getLoader().getFileName().toLowerCase() + selectedItem.getPath().toLowerCase();
	    } else {
		resourcePath = selectedItem.getPath().toLowerCase();
	    }

	    System.out.println("thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabViewModel.populatePreview(): " + resourcePath);

	    if (resourcePath.contains("maps") && resourcePath.endsWith(".dat")) {
		System.out.println("thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabViewModel.populatePreview(): looks like a map");
		return DetectedAssetType.Map;
	    }

	    if (resourcePath.endsWith(".sti")) {
		System.out.println("thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabViewModel.populatePreview(): looks like a sti");
		return DetectedAssetType.STI;
	    }

	    // I don't think slf flies can actually appear inside VFS tree...
	    if (resourcePath.endsWith(".slf")) {
		System.out.println("thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabViewModel.populatePreview(): looks like a slf");
		return DetectedAssetType.SLF;
	    }

	    if (resourcePath.endsWith(".ini") || resourcePath.endsWith(".xml") || resourcePath.endsWith(".lua") || resourcePath.endsWith(".txt")) {
		System.out.println("thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabViewModel.populatePreview(): looks like a text file");
		return DetectedAssetType.Text;
	    }
	}
	return DetectedAssetType.Unknown;
    }

    void populatePreview(VFSAccessor selectedItem) {

	switch (getAssetType(selectedItem)) {
	    case STI:
		publish(PREVIEW_OPTIONS_UPDATED, true, true, true);
		preview(selectedItem);
		break;
	    case SLF:
		publish(PREVIEW_OPTIONS_UPDATED, false, true, false);
		break;
	    case Map:
		publish(PREVIEW_OPTIONS_UPDATED, true, true, true);
		preview(selectedItem);
		break;
	    case Text:
		publish(PREVIEW_OPTIONS_UPDATED, true, false, true);
		break;
	    case Unknown:
		publish(PREVIEW_OPTIONS_UPDATED, false, false, false);
		break;
	    default:
		throw new AssertionError(getAssetType(selectedItem).name());
	}
		

    }

    void preview(VFSAccessor selectedItem) {
	DetectedAssetType assetType = getAssetType(selectedItem);
	switch (assetType) {
	    case STI:
		publish(PREVIEW_REQUESTED, assetType);
		break;
	    case Map:
		publish(PREVIEW_REQUESTED, assetType);
		break;
	    case Text:
		break;
	    case Unknown:
		break;
	    default:
		throw new AssertionError(getAssetType(selectedItem).name());
	}
    }

    class VfsDir {

	String name;
	TreeItem<String> node;
	Map<String, VfsDir> children = new HashMap<String, VfsDir>();
	List<String> files = new ArrayList<String>();

	public VfsDir(String name, TreeItem<String> node) {
	    this.name = name;
	    this.node = node;
	}

	public VfsDir(String name) {
	    this.name = name;
	    node = new TreeItem<>(name);
	}

	public Map<String, VfsDir> getChildren() {
	    return children;
	}

	public List<String> getFiles() {
	    return files;
	}

	public void addFile(String fileName) {
	    files.add(name);
	    node.getChildren().add(new TreeItem<>(name));
	}

	public VfsDir getOrAddDir(String name) {
	    if (children.containsKey(name)) {
		return children.get(name);
	    } else if (name.trim().isEmpty()) {
		return this;
	    } else {
		VfsDir newDir = new VfsDir(name);
		children.put(name, newDir);
		node.getChildren().add(newDir.getNode());
		return newDir;
	    }
	}

	public TreeItem<String> getNode() {
	    return node;
	}

    }

    void populateTree() {

	VfsDir rootDir = new VfsDir("root", rootItem);
	for (String file : config.getFileList()) {
	    String[] fileParts = file.split("\\\\");
	    int filePos = fileParts.length;
	    VfsDir dir = rootDir;
	    for (int i = 0; i < fileParts.length; i++) {
		String part = fileParts[i];
		if (i == filePos) {
		    dir.getFiles().add(part);
		} else {
		    dir = dir.getOrAddDir(part);
		}
	    }
	}

	sortTree(rootItem);
	//getFileVariants(rootItem);
    }

    void sortTree(TreeItem<String> item) {
	if (item.getChildren().isEmpty() == false) {

	    item.getChildren().sort((e1, e2) -> {
		if (e1.getChildren().isEmpty() == e2.getChildren().isEmpty()) {
		    return e1.getValue().compareTo(e2.getValue());
		} else if (e1.getChildren().isEmpty()) {
		    return 1;
		} else {
		    return -1;
		}
	    });

	    for (TreeItem<String> treeItem : item.getChildren()) {
		sortTree(treeItem);
	    }
	}
    }

    private String getLeafPath(TreeItem<String> item) {
	StringBuilder sb = new StringBuilder("\\");
	TreeItem<String> itemRoot = item;
	while (itemRoot.getParent() != null) {
	    itemRoot = itemRoot.getParent();
	    if (itemRoot != rootItem) {
		sb.insert(0, itemRoot.getValue());
		sb.insert(0, "\\");
	    } else {
		break;
	    }
	}

	sb.append(item.getValue());
	return sb.toString();
    }

    // OTOH let's not populate the entire tree with file variants
    private void getFileVariants(TreeItem<String> item) {
	if (item.isLeaf()) {
	    StringBuilder sb = new StringBuilder("\\");

	    TreeItem<String> itemRoot = item;
	    while (itemRoot.getParent() != null) {
		itemRoot = itemRoot.getParent();
		if (itemRoot != rootItem) {
		    sb.insert(0, itemRoot.getValue());
		    sb.insert(0, "\\");
		} else {
		    break;
		}
	    }

	    sb.append(item.getValue());
	    String filePath = sb.toString();

	    System.out.println("thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabViewModel.getFileVariants(): " + filePath);

	    for (VFSAccessor variant : config.getFileVariants(filePath)) {
		item.getChildren().add(new TreeItem<>(variant.toString()));
	    }

	} else {
	    for (TreeItem<String> subItem : item.getChildren()) {
		getFileVariants(subItem);
	    }
	}
    }

    public AssetManager getAssets() {
	return assets;
    }

}
