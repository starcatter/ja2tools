/*
 * Copyright (C) 2017 the_bob.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package thebob.ja2maptool.components;

import javafx.scene.control.TreeItem;
import thebob.assetloader.vfs.accessors.VFSAccessor;
import thebob.assetmanager.AssetManager;

/**
 *
 * @author the_bob
 */
public class MapSelectionTreeItem extends TreeItem<String> {    
    
    VFSAccessor accessor;
    AssetManager manager;

    public MapSelectionTreeItem(String value, VFSAccessor accessor, AssetManager manager) {
	super(value);
	this.accessor = accessor;
	this.manager = manager;
    }

    public VFSAccessor getAccessor() {
	return accessor;
    }

    public AssetManager getManager() {
	return manager;
    }
    
}
