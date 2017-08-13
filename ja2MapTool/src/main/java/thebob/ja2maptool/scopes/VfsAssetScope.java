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
package thebob.ja2maptool.scopes;

import de.saxsys.mvvmfx.Scope;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import thebob.assetloader.vfs.VFSConfig;
import thebob.assetloader.vfs.VirtualFileSystem;
import thebob.assetmanager.AssetManager;

/**
 *
 * @author the_bob
 */
public class VfsAssetScope implements Scope {

    public static final String UPDATE_MAP_SCREEN = "UPDATE_MAP_SCREEN";
    public static final String SHOW_MAPPING_SETUP_TAB = "SHOW_MAPPING_SETUP_TAB";
    public static final String SHOW_CONVERT_TAB = "SHOW_CONVERT_TAB";
    public static final String REFRESH_CONFIGS = "REFRESH_CONFIGS";
    public static final String BROWSE_CONFIG = "BROWSE_CONFIG";

    Map<String, VirtualFileSystem> configs = new HashMap<String, VirtualFileSystem>();
    Map<String, AssetManager> managers = new HashMap<String, AssetManager>();

    public Map<String, VirtualFileSystem> getConfigs() {
	return configs;
    }

    public Map<String, AssetManager> getManagers() {
	return managers;
    }

    public AssetManager getOrLoadAssetManager(String vfsDir, String configName) {
	String vfsPath = Paths.get(vfsDir).toAbsolutePath().normalize().toString();
	String managerPath = Paths.get(vfsPath + "\\" + configName).toAbsolutePath().normalize().toString();
	
	// check if the assed manager is already loaded
	AssetManager assets = getManagers().get(managerPath);
	if (assets == null) {
	    // looks like it wasn't not loaded. Try to get its VFS root
	    VirtualFileSystem assetRoot = getConfigs().get(vfsPath);
	    if (assetRoot == null) {
		// looks like the root isn't loaded either! Try to load it.
		assetRoot = new VirtualFileSystem(vfsPath);
	    }

	    if (assetRoot != null && assetRoot.getConfigNames().size() > 0) { // assetRoot shouldn't be null at this point anyway, but we still need it to contain configs!
		VFSConfig assetConfig = assetRoot.getConfig(configName);
		if (assetConfig != null) {
		    assets = new AssetManager(assetConfig);

		    configs.put(assetConfig.getPath().getParent().toString(), new VirtualFileSystem(assetConfig.getPath().getParent().toString()));
		    managers.put(managerPath, assets);
		}
	    }
	}

	publish(REFRESH_CONFIGS);
	publish(UPDATE_MAP_SCREEN);
	System.out.println("thebob.ja2maptool.scopes.VfsAssetScope.getOrLoadAssetManager(): " + managerPath + " " + (assets != null ? "LOADED" : "NOT LOADED"));

	return assets;
    }

    public void unloadConfig(String toString) {
	managers.remove(toString);
    }
}
