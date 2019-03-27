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
package thebob.ja2maptoolpackage;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.guice.MvvmfxGuiceApplication;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.vfs.VFSConfig;
import thebob.assetloader.vfs.VirtualFileSystem;
import thebob.assetmanager.AssetManager;
import thebob.ja2maptool.ui.main.MainScreenView;
import thebob.ja2maptool.util.MapTransformer;
import thebob.ja2maptool.util.mapping.ItemMappingFileData;
import thebob.ja2maptool.util.mapping.TileMappingFileData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


import static thebob.ja2maptool.util.mapping.MappingIO.loadItemMapping;
import static thebob.ja2maptool.util.mapping.MappingIO.loadTilesetMapping;


public class MainApp extends MvvmfxGuiceApplication {

    private static final String outDir = "./out";

    public static void main(final String[] args) {
        if (args.length == 0) {
            Application.launch(args);
        } else if (args.length == 5) {
            String sourceAssetsPath = args[0];
            String targetAssetsPath = args[1];
            String tileMapPath = args[2];
            String itemMapPath = args[3];
            String maps = args[4];

            batchProcessMaps(sourceAssetsPath, targetAssetsPath, tileMapPath, itemMapPath, maps);

            System.exit(0);
        } else {
            System.out.println("Invalid arguments, syntax is:");
            System.out.println("Source vfs file (full path)");
            System.out.println("Target vfs file (full path)");
            System.out.println("Tile mapping file (\"none\" to skip)");
            System.out.println("Item mapping file (\"none\" to skip)");
            System.out.println("Map file name, or comma separated list of map names, without spaces");

            System.exit(1);
        }
    }

    private static void batchProcessMaps(String sourceAssetsPath, String targetAssetsPath, String tileMapPath, String itemMapPath, String maps) {
        AssetManager sourceAssets = getOrLoadAssetManager(sourceAssetsPath);
        AssetManager targetAssets = getOrLoadAssetManager(targetAssetsPath);

        TileMappingFileData tileMappingFileData = !tileMapPath.equals("none")
                ? loadTilesetMapping(tileMapPath)
                : null;

        ItemMappingFileData itemMappingFileData = !itemMapPath.equals("none")
                ? loadItemMapping(itemMapPath)
                : null;

        Path outDirPath = Paths.get(outDir);
        if (!Files.exists(outDirPath)) {
            try {
                Files.createDirectories(outDirPath);
            } catch (IOException e) {
                System.err.println("Failed to create output directory: " + outDirPath.toAbsolutePath().toString());
            }
        }

        if (maps.contains(",")) {
            String[] split = maps.split(",");
            for (String map : split) {
                processMap(sourceAssets, targetAssets, tileMappingFileData, itemMappingFileData, map);
            }
        } else {
            processMap(sourceAssets, targetAssets, tileMappingFileData, itemMappingFileData, maps);
        }
    }

    private static AssetManager getOrLoadAssetManager(String vfs) {
        Path path = Paths.get(vfs);

        String configName = path.getFileName().toString();
        String vfsDir = vfs.replace(configName, "");

        System.out.println("vfsDir=[" + vfsDir + "] configName=[" + configName + "]");

        String vfsPath = Paths.get(vfsDir).toAbsolutePath().normalize().toString();

        VirtualFileSystem assetRoot = new VirtualFileSystem(vfsPath);
        if (assetRoot != null && assetRoot.getConfigNames().size() > 0) { // assetRoot shouldn't be null at this point anyway, but we still need it to contain configs!
            VFSConfig assetConfig = assetRoot.getConfig(configName);
            if (assetConfig != null) {
                return new AssetManager(assetConfig);
            }
        }

        return null;
    }

    private static void processMap(AssetManager sourceAssets, AssetManager targetAssets, TileMappingFileData tileMappingFileData, ItemMappingFileData itemMappingFileData, String map) {

        System.out.println("\n");
        System.out.println("======================");
        System.out.println("Processing map: " + map);
        System.out.println("======================");

        MapData mapData = sourceAssets.getMaps().loadMap("\\maps\\" + map);
        MapTransformer transformer = new MapTransformer(sourceAssets, targetAssets, tileMappingFileData, itemMappingFileData, mapData);

        System.out.println("\n======================\n");

        try {
            String out = transformer.saveTo(outDir + "/" + map);
            Files.write(Paths.get(outDir + "/" + map + ".log"), out.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

            System.out.println("\n----------------------");
            System.out.println("Saved to " + outDir +"/"+map+", log file saved to "+outDir +"/"+map+".log");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startMvvmfx(final Stage stage) throws Exception {
        final Parent view = FluentViewLoader.fxmlView(MainScreenView.class).load().getView();

        final Scene scene = new Scene(view);

        stage.setScene(scene);
        stage.setTitle("JA2 1.13 Map Tool (Alpha16)");
        stage.show();
    }

}
