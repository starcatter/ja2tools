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
package thebob.assetmanager;

import thebob.assetloader.vfs.VFSConfig;
import thebob.assetloader.vfs.VirtualFileSystem;
import thebob.assetloader.xml.XmlLoader;
import thebob.assetmanager.managers.ItemManager;
import thebob.assetmanager.managers.MapManager;
import thebob.assetmanager.managers.TilesetManager;
import thebob.assetmanager.managers.VFSContextBoundManager;

/**
 *
 * This is the part that manages all assets abstracted by VFS In particular, it should support: - maps - xml data - tilesets - graphics
 *
 *
 */
public class AssetManager {

    protected final VFSConfig vfs;
    // 
    protected MapManager maps = new MapManager(this);
    protected TilesetManager tilesets = new TilesetManager(this);
    protected ItemManager items = new ItemManager(this);
    //
    protected XmlLoader xml;

    //
    public AssetManager(VFSConfig vfs) {
        this.vfs = vfs;
        xml = new XmlLoader(this.vfs);

        for (VFSContextBoundManager manager : new VFSContextBoundManager[]{maps, tilesets, items}) {
            // System.out.println("AssetManager loading: "+manager+'\n');
	    if (!manager.init()) {
            System.err.println("VFSContext failed to init manager: " + manager);
                throw new RuntimeException("VFSContext failed to init manager: " + manager);
            }
	    // System.out.println("\nAssetManager done: "+manager);
        }
    }

    public final XmlLoader getXml() {
        return xml;
    }

    public VFSConfig getVfs() {
        return vfs;
    }

    public MapManager getMaps() {
        return maps;
    }

    public TilesetManager getTilesets() {
        return tilesets;
    }

    public ItemManager getItems() {
        return items;
    }

    public String getVfsConfigName(){
	    return getVfs().getPath().getFileName().toString();
    }
    
    public static void main(String[] args) {
        VirtualFileSystem vfs = new VirtualFileSystem("../../JA113.data/gameData");
        //for (String configName : vfs.getConfigNames()) {
            //VFSConfig config = vfs.getConfig("vfs_config.JA2113-Metavira.ini");
            VFSConfig config = vfs.getConfig("vfs_config.JA2Vanilla.ini");
            //System.out.println("\n\n\nthebob.assetmanager.AssetManager.main() LOADING: " + configName + "\n");
            AssetManager am = new AssetManager(config);
            System.out.println( am.toString() );
            return;
        //}
    }
}
