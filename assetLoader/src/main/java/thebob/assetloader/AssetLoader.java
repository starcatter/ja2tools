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
package thebob.assetloader;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.scene.control.TreeItem;
import thebob.assetloader.slf.SlfLoader;
import thebob.assetloader.sti.StiLoader;
import thebob.assetloader.vfs.VFSConfig;
import thebob.assetloader.vfs.VirtualFileSystem;
import thebob.assetloader.vfs.accessors.VFSAccessor;

/**
 *
 * @author the_bob
 */
public class AssetLoader {

    public SlfLoader loadLibrary(String fileName) {
	SlfLoader libLoader = new SlfLoader();
	libLoader.loadFile(fileName);
	return libLoader;
    }

    protected static boolean testAssets() {
	SlfLoader libLoader = new SlfLoader();
	libLoader.loadFile("Tilesets.slf");

	StiLoader stiLoader = new StiLoader();

	for (int i = 0; i < libLoader.getAssetCount(); i++) {
	    ByteBuffer buffer = libLoader.getAsset(i);
	    if (buffer != null) {
		stiLoader.loadAsset(buffer);
	    } else {
		System.out.println("thebob.assetloader.AssetLoader.main() cant get to asset " + i);
	    }
	}
	return true;
    }

    protected static void printVFSFileVariants(VirtualFileSystem vfs, String filePath) {
	for (String configName : vfs.getConfigNames()) {
	    VFSConfig config = vfs.getConfig(configName);

	    LinkedList<VFSAccessor> variants = config.getFileVariants(filePath);
	    System.out.println("\nthebob.assetloader.AssetLoader.testVFS() config " + configName);
	    if (variants == null) {
		System.out.println("\t-> no file (" + filePath + ")");
	    } else {
		for (VFSAccessor variant : variants) {
		    System.out.println("\t-> " + variant);
		}
	    }
	}
    }

    protected static void dumpVFSMapList(VirtualFileSystem vfs) {
	for (String configName : vfs.getConfigNames()) {
	    dumpVFSMapList(vfs, configName);
	}
    }

    protected static void dumpVFSMapList(VirtualFileSystem vfs, String configName) {
	VFSConfig config = vfs.getConfig(configName);
	System.out.println(configName);

	for (String profile : config.getProfiles().keySet()) {
	    System.out.println("\t" + profile);

	    ArrayList<VFSAccessor> files = config.getProfiles().get(profile);

	    System.out.println("\t\t" + files.stream().filter(f -> f.getVFSPath().startsWith("\\MAPS\\")).count() + " maps");

	    /*
	    files.stream().filter(f -> f.startsWith("\\MAPS\\")).sorted().forEach(mapName -> {
		System.out.println("\t\t" + (mapName.replace("\\MAPS\\", "")));
	    });
	     */
	}
    }

    protected static boolean testVFS() {
	try {
	    VirtualFileSystem vfs = new VirtualFileSystem("../../JA113.data/gameData");
	    //dumpVFSMapList(vfs, "vfs_config.JA2113AIMNAS.ini");
	    dumpVFSMapList(vfs, "vfs_config.JA2113-Metavira.ini");

//            printVFSFileVariants(vfs, "\\MAPS\\A9.DAT");
//            printVFSFileVariants(vfs, "\\TABLEDATA\\ITEMS\\ITEMS.XML");
//	    printVFSFileVariants(vfs, "\\Ja2Set.dat.xml");
//	    printVFSFileVariants(vfs, "\\Ja2_Options.INI");
//	    printVFSFileVariants(vfs, "\\BinaryData\\JA2set.dat");
//            printVFSFileVariants(vfs, "\\TILESETS\\0\\BUILD_01.STI");            
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static void main(String[] args) {
	testVFS();
    }

}
