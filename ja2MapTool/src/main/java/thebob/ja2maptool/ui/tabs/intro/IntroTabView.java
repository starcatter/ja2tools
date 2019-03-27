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
package thebob.ja2maptool.ui.tabs.intro;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.inject.Inject;
import thebob.assetloader.map.core.MapData;
import thebob.assetmanager.AssetManager;
import thebob.ja2maptool.scopes.MainScope;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.scopes.map.ConvertMapScope;
import thebob.ja2maptool.scopes.map.MapCompositorScope;
import thebob.ja2maptool.scopes.map.MapScope;
import thebob.ja2maptool.scopes.mapping.ItemMappingScope;
import thebob.ja2maptool.scopes.view.StiViewerScope;
import thebob.ja2maptool.ui.tabs.compositor.CompositorTabView;
import thebob.ja2maptool.ui.tabs.convert.ConvertMapTabView;
import thebob.ja2maptool.ui.tabs.mapping.items.ItemMappingTabView;
import thebob.ja2maptool.ui.tabs.viewers.sti.StiViewerTabView;
import thebob.ja2maptool.ui.tabs.viewers.sti.StiViewerTabViewModel;

import static thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.ITEM_MAPPING_LOADED;

public class IntroTabView implements FxmlView<IntroTabViewModel>, Initializable {

    @FXML
    private AnchorPane intro_pane;

    @FXML
    private Text intro_config;

    @FXML
    private Text intro_map;

    @FXML
    private Text intro_convert;

    @FXML
    void intro_config_click(MouseEvent event) {
        viewModel.goToConfigSetupTab();
    }

    @FXML
    void intro_convert_click(MouseEvent event) {
        viewModel.goToConvertSetupTab();
    }

    @FXML
    void intro_map_click(MouseEvent event) {
        viewModel.goToMapSetupTab();
    }

    private void showLogo() {
        AnchorPane pane = intro_pane;
        pane.getChildren().clear();

        StiViewerScope scope = new StiViewerScope();
        scope.setFilePath("..\\..\\JA113.data\\gameData\\Data\\Interface\\SIRTECHSPLASH.STI");

        ViewTuple<StiViewerTabView, StiViewerTabViewModel> selectorTouple = FluentViewLoader.fxmlView(StiViewerTabView.class)
                .context(context)
                .providedScopes(scope)
                .load();

        pane.getChildren().add(
                selectorTouple.getView()
        );
    }

    private void showCompositor() {

        VfsAssetScope vfsAssets = viewModel.getVfsAssets();

        MapCompositorScope scope = new MapCompositorScope();

        ViewTuple tabTouple = FluentViewLoader.fxmlView(CompositorTabView.class)
                .context(context) // VfsAssetScope, MainScope
                .providedScopes(scope)
                .load();

        AnchorPane pane = intro_pane;
        Parent compositor = tabTouple.getView();
        pane.getChildren().clear();
        pane.getChildren().add(compositor);

        AnchorPane.setBottomAnchor(compositor, 0d);
        AnchorPane.setTopAnchor(compositor, 0d);
        AnchorPane.setLeftAnchor(compositor, 0d);
        AnchorPane.setRightAnchor(compositor, 0d);

        AssetManager assets = vfsAssets.getOrLoadAssetManager("v:\\JA2.05-2018\\", "vfs_config.JA2Vanilla.ini");
        MapData mapData = assets.getMaps().loadMap("\\maps\\a3.dat");
        scope.getMap().setMapName("test map");
        scope.getMap().setMapAssets(assets);
        scope.getMap().setTilesetId(mapData.getSettings().iTilesetID);
        scope.getMap().setTileset(assets.getTilesets().getTileset(mapData.getSettings().iTilesetID));
        scope.getMap().setMapData(mapData);
        scope.getMap().publish(MapScope.MAP_UPDATED);

    }

    private void showConverter() {

        VfsAssetScope vfsAssets = viewModel.getVfsAssets();

        ConvertMapScope scope = new ConvertMapScope();

        //ItemMappingScope mappingScope = ItemMappingScope.loadFromFile("C:\\JA2 Workshop\\JA2\\Tools\\Map Tool\\1.13 - AIMNAS.itemmap", vfsAssets);
        //scope.setItemMapping(mappingScope);

        ViewTuple tabTouple = FluentViewLoader.fxmlView(ConvertMapTabView.class)
                .context(context) // VfsAssetScope, MainScope
                .providedScopes(scope)
                .load();

        AnchorPane pane = intro_pane;
        Parent compositor = tabTouple.getView();
        pane.getChildren().clear();
        pane.getChildren().add(compositor);

        AnchorPane.setBottomAnchor(compositor, 0d);
        AnchorPane.setTopAnchor(compositor, 0d);
        AnchorPane.setLeftAnchor(compositor, 0d);
        AnchorPane.setRightAnchor(compositor, 0d);

        AssetManager assets = vfsAssets.getOrLoadAssetManager("C:\\Code\\JA2\\Data\\", "vfs_config.UC113.ini");
//        AssetManager assets = vfsAssets.getOrLoadAssetManager("C:\\Code\\JA2\\Data\\", "vfs_config.JA2113Wildfire607.ini");
//        AssetManager assets = vfsAssets.getOrLoadAssetManager("C:\\Games\\JA2.8578\\", "vfs_config.JA2113AIMNAS-WF.ini");
        MapData mapData = assets.getMaps().loadMap("\\maps\\c13.dat");

        AssetManager assets2 = vfsAssets.getOrLoadAssetManager("C:\\Code\\JA2\\Data\\", "vfs_config.JA2113AIMNAS.ini");

        scope.getMap().setMapName("test map");
        scope.getMap().setMapAssets(assets);
        scope.getMap().setTilesetId(mapData.getSettings().iTilesetID);
        scope.getMap().setTileset(assets.getTilesets().getTileset(mapData.getSettings().iTilesetID));
        scope.getMap().setMapData(mapData);
        scope.getMap().publish(MapScope.MAP_UPDATED);

//        scope.setItemMapping(mappingScope);
//        scope.publish(ITEM_MAPPING_LOADED);
    }

    private void showMapper() {
        VfsAssetScope vfsAssets = viewModel.getVfsAssets();

        AssetManager assets = vfsAssets.getOrLoadAssetManager("D:\\NetBeansProjects\\JA113.data\\gameData", "vfs_config.JA2113AIMNAS.ini");
        AssetManager assets2 = vfsAssets.getOrLoadAssetManager("D:\\NetBeansProjects\\JA113.data\\gameData", "vfs_config.UC113.ini");


        Platform.runLater(()->{
            ItemMappingScope itemMappingScope = new ItemMappingScope();

            itemMappingScope.setSourceAssets(assets);
            itemMappingScope.setTargetAssets(assets2);

            viewModel.getMainScreen().publish(MainScope.NEW_TAB, ItemMappingTabView.class, "Map items", itemMappingScope);
            viewModel.getMainScreen().registerItemMappingScope(itemMappingScope);
        });
    }

    // MVVMFX inject
    @InjectViewModel
    private IntroTabViewModel viewModel;

    @Inject
    private Stage primaryStage;

    @InjectContext
    private Context context;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //showLogo();
        //showConverter();
        //showMapper();
    }

}
