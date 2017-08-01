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
import thebob.assetloader.vfs.accessors.FileSystemAccessor;
import thebob.assetloader.vfs.accessors.SLFAccessor;
import thebob.assetloader.vfs.accessors.VFSAccessor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.ini4j.Wini;
import thebob.assetloader.slf.SlfLoader;

/**
 *
 * @author the_bob
 */
public class VFSConfig {

    Path ini_path;
    String basePath;
    private Map<String, SlfLoader> loadedLibraries = new HashMap<String, SlfLoader>();
    private Map<String, LinkedList<VFSAccessor>> files = new HashMap<String, LinkedList<VFSAccessor>>();
    private List<String> libraries = new ArrayList<String>();
    private List<String> libraryDirs = new ArrayList<String>();
    private boolean loaded = false;
    private Boolean useXmlTileset = true;

    public Path getPath() {
	return ini_path;
    }

    public boolean isLoaded() {
        return loaded;
    }

    boolean isLibraryLoaded(String path) {
        return loadedLibraries.containsKey(path);
    }

    SlfLoader loadLibrary(String path) {
        if (loadedLibraries.containsKey(path)) {
            return loadedLibraries.get(path);
        }
        SlfLoader lib = VirtualFileSystem.loadLibrary(path);
        if (lib != null) {
            loadedLibraries.put(path, lib);
            return lib;
        }
        return null;
    }

    public VFSConfig(Path ini_path) {
        this.ini_path = ini_path;
        basePath = ini_path.toAbsolutePath().getParent().toString();
    }

    public void dumpFileList() {
        System.out.println(" ---------------------------- ");
        for (Map.Entry<String, LinkedList<VFSAccessor>> file : files.entrySet()) {
            System.out.println(file.getKey() + ": " + file.getValue().size() + " accessors, " + file.getValue());
        }
        System.out.println(" ---------------------------- ");
    }

    public void loadConfig() {
        try {
            Wini ini = new Wini(new File(ini_path.toString()));
            String vfs_config_str = ini.get("vfs_config", "PROFILES", String.class);
            //System.out.println("thebob.assetloader.vfs.VirtualFileSystem.parseIniFile(): " + ini_path);
            for (String profile : vfs_config_str.split(", ")) {
                String name = ini.get("PROFILE_" + profile, "NAME", String.class);
                String loacations = ini.get("PROFILE_" + profile, "LOCATIONS", String.class);
                String root = ini.get("PROFILE_" + profile, "PROFILE_ROOT", String.class);
                //System.out.println("thebob.assetloader.vfs.VirtualFileSystem.parseIniFile(): \t" + profile);
                //System.out.println("thebob.assetloader.vfs.VirtualFileSystem.parseIniFile(): \t\t" + name);
                //System.out.println("thebob.assetloader.vfs.VirtualFileSystem.parseIniFile(): \t\t" + loacations);
                for (String location : loacations.split(", ")) {
                    String type = ini.get("LOC_" + location, "TYPE", String.class);
                    String path = ini.get("LOC_" + location, "PATH", String.class);
                    String vfs_path = ini.get("LOC_" + location, "VFS_PATH", String.class);

                    // check if we're supposed to skip the library
                    if (vfs_path != null && VirtualFileSystem.loadLibs.get(vfs_path) != null && VirtualFileSystem.loadLibs.get(vfs_path) == false) {
                        continue;
                    }

                    //System.out.println("thebob.assetloader.vfs.VirtualFileSystem.parseIniFile(): \t\t\t" + location);
                    //System.out.println("thebob.assetloader.vfs.VirtualFileSystem.parseIniFile(): \t\t\t\t" + type);
                    //System.out.println("thebob.assetloader.vfs.VirtualFileSystem.parseIniFile(): \t\t\t\t" + path);
                    if (root != null) {
                        path = basePath + "/" + root + "/" + path;
                    } else {
                        path = basePath + "/" + path;
                    }
                    if (type != null) {
                        switch (type) {
                            case "LIBRARY":
                                libraries.add(vfs_path);
                                libraryDirs.add(vfs_path.replace(".slf", "").toUpperCase());
                                scanLibrary(path, vfs_path.replace(".slf", "").toUpperCase());
                                break;
                            case "DIRECTORY":
                                scanDirectory(path);
                                break;
                            default:
                                System.out.println("thebob.assetloader.vfs.VFSConfig.loadConfig(): unknown VFS location type!");
                        }
                    } else {
                        System.out.println("thebob.assetloader.vfs.VFSConfig.loadConfig(): failed reading VFS location type!");
                    }
                }
            }
	    processOptionsIni();
            loaded = true;
        } catch (IOException ex) {
            Logger.getLogger(VirtualFileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void scanLibrary(String path, String VFSName) {
        // System.out.println("thebob.assetloader.vfs.VFSConfig.scanLibrary() " + path);
        if (isLibraryLoaded(path)) {
            return;
        }
        if (!Files.exists(Paths.get(path))) {
            System.out.println("thebob.assetloader.vfs.VFSConfig.scanLibrary() File missing: " + path);
            return;
        }
        SlfLoader lib = loadLibrary(path);
        for (String fileName : lib.getFiles()) {
            String storedName = "\\" + VFSName + "\\" + fileName.toUpperCase();
            LinkedList<VFSAccessor> accessList = files.get(storedName);
            if (accessList == null) {
                accessList = new LinkedList<>();
                files.put(storedName, accessList);
            }
            accessList.add(new SLFAccessor(lib, fileName));
        }
    }

    private void scanDirectory(String dirPath) {
        // System.out.println("thebob.assetloader.vfs.VFSConfig.scanDirectory() " + dirPath);
        if (!Files.exists(Paths.get(dirPath))) {
            System.out.println("thebob.assetloader.vfs.VFSConfig.scanLibrary() Dir missing: " + dirPath);
            return;
        }

        try {
            // first scan the dir for libraries and see if they contain files overriding what we have
            Files.newDirectoryStream(Paths.get(dirPath), (Path path) -> path.toString().endsWith(".slf")).forEach(
                    (Path filePath) -> {
                        String libraryFile = filePath.getFileName().toString();
                        if (libraries.contains(libraryFile)) {
                            scanLibrary(filePath.toString(), libraryFile.replace(".slf", "").toUpperCase());
                        }
                    });

            // then scan for directories named after the libraries we know of (or extra dirs that never were libraries)
            Files.newDirectoryStream(Paths.get(dirPath), (Path path) -> path.toFile().isDirectory()).forEach(
                    (Path dir) -> {
                        String dirName = dir.getFileName().toString().toUpperCase();
                        if (libraryDirs.contains(dirName) || VirtualFileSystem.extraDataDirs.contains(dirName)) {
                            scanSubDirForFiles(dir);
                        }
                    });

            // finally, scan for extra files if any were specified
            Files.newDirectoryStream(Paths.get(dirPath), (Path path) -> path.toFile().isFile()).forEach(
                    (Path filePath) -> {
                        String fileName = filePath.getFileName().toString().toUpperCase();
                        if (VirtualFileSystem.extraFiles.contains(fileName)) {
                            addFileToList("\\" + fileName, filePath.toString());
                        }
                    });

        } catch (IOException ex) {
            Logger.getLogger(VFSConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void scanSubDirForFiles(Path dirPath) {
        scanSubDirForFiles(dirPath, 0);
    }

    private void scanSubDirForFiles(Path dirPath, int depth) {
        // System.out.println("thebob.assetloader.vfs.VFSConfig.scanSubDirForFiles():" + dirPath);
        try {
            Files.newDirectoryStream(dirPath).forEach((Path dir) -> {
                if (dir.toFile().isDirectory()) {
                    scanSubDirForFiles(dir, depth + 1);
                } else {
                    String libPath = null;
                    if (depth > 0) {
                        StringBuilder libPathBuilder = new StringBuilder();
                        int nameParts = dir.getNameCount();
                        for (int i = nameParts - depth - 2; i < nameParts; i++) {
                            libPathBuilder.append('\\');
                            libPathBuilder.append(dir.getName(i).toString());
                        }
                        libPath = libPathBuilder.toString();
                    } else {
                        libPath = "\\" + dir.getName(dir.getNameCount() - 2) + "\\" + dir.getFileName().toString();
                    }

                    addFileToList(libPath.toUpperCase(), dir.toString());
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(VFSConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addFileToList(String libPath, String filePath) {
        LinkedList<VFSAccessor> accessList = files.get(libPath);
        if (accessList == null) {
            accessList = new LinkedList<>();
            files.put(libPath, accessList);
        }
        accessList.add(new FileSystemAccessor(filePath));
    }

    protected VFSAccessor getAccessorForPath(String path) {
        LinkedList<VFSAccessor> list = files.get(path.toUpperCase());
        return list == null ? null : list.getLast();
    }

    public ByteBuffer getFile(String path) {
        VFSAccessor accessor = getAccessorForPath(path);
        return accessor == null ? null : accessor.getBytes();
    }

    public VFSAccessor getFileAccess(String path) {
        return getAccessorForPath(path);
    }

    public FileInputStream getFileStream(String path) {
        VFSAccessor accessor = getAccessorForPath(path);
        return accessor == null ? null : accessor.getStream();
    }

    public LinkedList<VFSAccessor> getFileVariants(String path) {
        return files.get(path.toUpperCase());
    }

    public Set<String> getFileList() {
        return files.keySet();
    }

    private void processOptionsIni() {
	FileInputStream optsStream = getFileStream("\\Ja2_Options.INI".toUpperCase());
	
	if( optsStream != null ){
	    try {
		Wini ini = new Wini(optsStream);
		useXmlTileset = ini.get("Data File Settings", "USE_XML_TILESETS", Boolean.class);
		System.out.println("thebob.assetloader.vfs.VFSConfig.processOptionsIni(): "+useXmlTileset);
	    } catch (IOException ex) {
		Logger.getLogger(VFSConfig.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

    public Boolean getUseXmlTileset() {
	return useXmlTileset;
    }

    
}
