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
package thebob.assetloader.vfs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ini4j.Wini;
import thebob.assetloader.slf.SlfLoader;

public class VirtualFileSystem {

    public static final Map<String, Boolean> loadLibs = ImmutableMap.<String, Boolean>builder()
            .put("Ambient.slf", false)
            .put("Anims.slf", false)
            .put("BattleSNDS.slf", false)
            .put("BigItems.slf", true)
            .put("Binarydata.slf", true)
            .put("Cursors.slf", false)
            .put("Data.slf", false)
            .put("Faces.slf", false)
            .put("Fonts.slf", false)
            .put("German.slf", false)
            .put("Interface.slf", false)
            .put("Intro.slf", false)
            .put("Laptop.slf", false)
            .put("Loadscreens.slf", false)
            .put("Maps.slf", true)
            .put("Mercedt.slf", false)
            .put("Music.slf", false)
            .put("Npc_speech.slf", false)
            .put("Npcdata.slf", false)
            .put("Radarmaps.slf", true)
            .put("Russian.slf", false)
            .put("Sounds.slf", false)
            .put("Speech.slf", false)
            .put("Tilesets.slf", true)
            .build();

    public static final List<String> extraDataDirs = ImmutableList.<String>builder()
            .add("TABLEDATA")
            .add("SCRIPTS")
            .add("MAPS")
            .add("INTERFACE")	// TODO: check if this extra dir is needed after enabling Interface.slf. And check if that's a good idea. WTF was I thinking?
            .build();

    public static final List<String> extraFiles = ImmutableList.<String>builder()
            .add("JA2SET.DAT.XML")
            .add("JA2_OPTIONS.INI")
            .build();

    private static Map<String, SlfLoader> loadedLibraries = new HashMap<String, SlfLoader>();

    static boolean isLibraryLoaded(String path) {
        return loadedLibraries.containsKey(path);
    }

    static SlfLoader loadLibrary(String path) {
        String unifiedPath = Paths.get(path).toAbsolutePath().toString();

        if (loadedLibraries.containsKey(unifiedPath)) {
            return loadedLibraries.get(unifiedPath);
        }

        SlfLoader lib = new SlfLoader();
        if (lib.loadFile(unifiedPath)) {
            loadedLibraries.put(unifiedPath, lib);
            return lib;
        }

        return null;
    }

    private String baseDir;
    Map<String, VFSConfig> configs = new HashMap<String, VFSConfig>();

    public VirtualFileSystem(String baseDir) {
        this.baseDir = baseDir;
        scanBasedir();
    }

    protected void parseIniFile(Path ini_path) {
        System.out.println("thebob.assetloader.vfs.VirtualFileSystem.parseIniFile() loading " + ini_path.getFileName());
        VFSConfig conf = new VFSConfig(ini_path);
        configs.put(ini_path.getFileName().toString(), conf);
    }

    protected void scanBasedir() {
        try {
            //System.out.println("thebob.assetloader.vfs.VirtualFileSystem.scanBasedir(): " + baseDir);
            Files.newDirectoryStream(Paths.get(baseDir),
                    path -> path.toString().matches(".*vfs_config.+\\.ini"))
                    .forEach(v -> parseIniFile(v));
        } catch (IOException ex) {
            Logger.getLogger(VirtualFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Set<String> getConfigNames() {
        return configs.keySet();
    }
    
    public Collection<VFSConfig> getConfigs() {
        return configs.values();
    }

    public VFSConfig getConfig(int index) {
        Iterator<String> iter = configs.keySet().iterator();
        while (--index > 0) {
            iter.next();
        }
        return getConfig(iter.next());
    }

    public VFSConfig getConfig(String name) {
        VFSConfig config = configs.get(name);
        if (config == null) {
            System.out.println("thebob.assetloader.vfs.VirtualFileSystem.getConfig(): invalid config [" + name + "]");
            return null;
        }
        if (config.isLoaded() == false) {
            System.out.println("thebob.assetloader.vfs.VirtualFileSystem.getConfig(): loading config [" + name + "]");
            config.loadConfig();
        }
        return config;
    }

    public String getBaseDir() {
	return baseDir;
    }

}
