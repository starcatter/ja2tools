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
package thebob.ja2maptool.components;

import com.sun.javafx.scene.control.skin.TabPaneSkin;
import de.saxsys.mvvmfx.Scope;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import thebob.assetloader.vfs.VFSConfig;
import thebob.assetloader.vfs.VirtualFileSystem;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.ui.main.MainScreenView;

/**
 *
 * @author the_bob
 */
public class VFSMenu extends Menu {

    VfsAssetScope scope;

    public VFSMenu(VfsAssetScope scope) {
	super("VFS configs");
	this.scope = scope;
	for (VirtualFileSystem vfs : scope.getConfigs().values()) {
	    getItems().add(new VFSActionMenu(vfs));
	}
    }

    public class VFSActionMenu extends Menu {

	public VFSActionMenu(VirtualFileSystem vfs) {
	    super(vfs.getBaseDir());

	    for (VFSConfig config : vfs.getConfigs()) {
		getItems().add(new VFSConfigActionMenu(config));
	    }
	}

	public class VFSConfigActionMenu extends Menu {

	    VFSConfig config;

	    MenuItem load = new MenuItem("Load");
	    MenuItem unload = new MenuItem("Unload");
	    MenuItem browse = new MenuItem("Browse");

	    @Override
	    public void show() {
		super.show();

		if (scope.getManagers().get(config.getPath().toString()) == null) {
		    load.setDisable(false);
		    unload.setDisable(true);
		    browse.setDisable(true);
		} else {
		    load.setDisable(true);
		    unload.setDisable(false);
		    browse.setDisable(false);
		}
	    }

	    public VFSConfigActionMenu(VFSConfig config) {
		super(config.getPath().getFileName().toString());
		this.config = config;

		getItems().add(load);
		getItems().add(unload);
		getItems().add(browse);

		load.setOnAction(event -> {
		    scope.getOrLoadAssetManager(config.getPath().getParent().toString(), config.getPath().getFileName().toString());
		});

		unload.setOnAction(event -> {
		    scope.unloadConfig(config.getPath().toString());
		});

		browse.setOnAction(event -> {
		    if (!config.isLoaded()) {
			config.loadConfig();
		    }
		    scope.publish(VfsAssetScope.BROWSE_CONFIG, config);
		});
	    }

	}
	// end class VFSConfigActionMenu
    }

    // end class VFSActionMenu
}
