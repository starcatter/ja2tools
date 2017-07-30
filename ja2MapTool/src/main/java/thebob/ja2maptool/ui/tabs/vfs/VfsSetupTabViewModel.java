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
package thebob.ja2maptool.ui.tabs.vfs;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import java.nio.file.Paths;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import thebob.assetloader.vfs.VFSConfig;
import thebob.assetloader.vfs.VirtualFileSystem;
import thebob.assetmanager.AssetManager;
import thebob.ja2maptool.scopes.VfsAssetScope;


/**
 *
 * @author the_bob
 */
public class VfsSetupTabViewModel implements ViewModel {

    @InjectScope
    VfsAssetScope vfsAssets;
    
    CheckBoxTreeItem configListRoot = new CheckBoxTreeItem<String>("VFS configs");

    private Command loadSelectedConfigsCommand;
    private final BooleanProperty precondition = new SimpleBooleanProperty();
    private DoubleProperty progress = new SimpleDoubleProperty();
    
    public void initialize() {
        vfsAssets.subscribe(VfsAssetScope.REFRESH_CONFIGS, (key, payload) -> { 
            refreshConfigRoot();
        });
	
        loadSelectedConfigsCommand = new DelegateCommand(() -> new Action() {
			@Override
			protected void action() throws Exception {
			    loadSelectedConfigs();
			    vfsAssets.publish(VfsAssetScope.REFRESH_CONFIGS);
			}
        }, precondition, true); //Async
        
	addConfig(".");
	addConfig("../gameData");
    }

    public Command getLoadSelectedConfigsCommand() {
	return loadSelectedConfigsCommand;
    }
        
    private boolean addConfigDir(String dir) {
	String dirUnified = Paths.get(dir).toAbsolutePath().normalize().toString();
	System.out.println("thebob.ja2maptool.MainScreenViewModel.addConfigDir() " + dir + " -> " + dirUnified);
	if (vfsAssets.getConfigs().containsKey(dirUnified)) {
	    return false;
	}
	VirtualFileSystem vfs = new VirtualFileSystem(dirUnified);
	if (vfs.getConfigNames().size() > 0) {
	    vfsAssets.getConfigs().put(dirUnified, vfs);
	    return true;
	} else {
	    return false;
	}
    }

    public boolean addConfig(String dir) {
	if (addConfigDir(dir)) {
	    vfsAssets.publish(VfsAssetScope.REFRESH_CONFIGS);
	    return true;
	} else {
	    return false;
	}
    }

    public void refreshConfigRoot() {
	configListRoot.getChildren().clear();
	configListRoot.setExpanded(true);

	for (String vfsDir : vfsAssets.getConfigs().keySet()) {
	    CheckBoxTreeItem confNode = new CheckBoxTreeItem<String>(vfsDir);
	    confNode.setExpanded(true);
	    configListRoot.getChildren().add(confNode);

	    VirtualFileSystem vfs = vfsAssets.getConfigs().get(vfsDir);
	    for (String config : vfs.getConfigNames()) {
		CheckBoxTreeItem confNodeConfig = new CheckBoxTreeItem<String>(config);
		confNode.getChildren().add(confNodeConfig);
		
		String path = Paths.get( vfs.getBaseDir() + "/" + config ).toAbsolutePath().normalize().toString();
		confNodeConfig.setSelected(vfsAssets.getManagers().get( path ) != null);
	    }
	}
    }

    public CheckBoxTreeItem<String> getConfigTreeRoot() {
	return configListRoot;
    }
    
    void loadSelectedConfigs() {
	vfsAssets.getManagers().clear();

	for (Object vfsPathObject : configListRoot.getChildren()) {
	    CheckBoxTreeItem<String> vfsPathNode = (CheckBoxTreeItem<String>) vfsPathObject;

	    for (TreeItem<String> vfsConfigItem : vfsPathNode.getChildren()) {
		CheckBoxTreeItem<String> vfsConfigNode = (CheckBoxTreeItem<String>) vfsConfigItem;
		if (vfsConfigNode.isSelected()) {
		    String vfsName = vfsPathNode.getValue();
		    String configName = vfsConfigNode.getValue();

		    VirtualFileSystem vfs = vfsAssets.getConfigs().get(vfsName);
		    VFSConfig config = vfs.getConfig(configName);
		    AssetManager assets = new AssetManager(config);

		    vfsAssets.getManagers().put(assets.getVfs().getPath().toAbsolutePath().toString(), assets);
		}
	    }

	}

	vfsAssets.publish(VfsAssetScope.UPDATE_MAP_SCREEN, null);
    }

    void configSelected(boolean b) {
	precondition.set(b);
    }
    
}
