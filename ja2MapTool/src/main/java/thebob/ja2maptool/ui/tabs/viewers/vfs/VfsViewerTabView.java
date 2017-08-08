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
package thebob.ja2maptool.ui.tabs.viewers.vfs;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import thebob.assetloader.map.core.MapData;
import thebob.assetloader.vfs.accessors.VFSAccessor;
import thebob.assetmanager.AssetManager;
import thebob.ja2maptool.scopes.map.MapScope;
import thebob.ja2maptool.scopes.view.StiViewerScope;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabView;
import thebob.ja2maptool.ui.tabs.viewers.map.MapViewerTabViewModel;
import thebob.ja2maptool.ui.tabs.viewers.sti.StiViewerTabView;
import thebob.ja2maptool.ui.tabs.viewers.sti.StiViewerTabViewModel;
import static thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabViewModel.PREVIEW_OPTIONS_UPDATED;
import static thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabViewModel.PREVIEW_REQUESTED;

/**
 * FXML Controller class
 *
 * @author the_bob
 */
public class VfsViewerTabView implements FxmlView<VfsViewerTabViewModel>, Initializable {

    @FXML
    private Label vfs_name;

    @FXML
    private TreeView<String> vfs_list;

    @FXML
    private ListView<VFSAccessor> vfs_variants;

    @FXML
    private AnchorPane vfs_preview;

    @FXML
    private Button btn_prev;

    @FXML
    private Button btn_open;

    @FXML
    private Button btn_extract;

    @FXML
    void extract(MouseEvent event) {

    }

    @FXML
    void open(MouseEvent event) {

    }

    @FXML
    void preview(MouseEvent event) {
	viewModel.preview(vfs_variants.getSelectionModel().getSelectedItem());
    }

    @FXML
    void select_file(MouseEvent event) {
	select_file();
    }

    void select_file() {
	viewModel.populateVariants(vfs_list.getSelectionModel().getSelectedItem());
	vfs_variants.refresh();
	vfs_variants.getSelectionModel().selectLast();
	viewModel.populatePreview(vfs_variants.getSelectionModel().getSelectedItem());
    }

    @FXML
    void select_variant(MouseEvent event) {
	viewModel.populatePreview(vfs_variants.getSelectionModel().getSelectedItem());
    }

    // MVVMFX inject
    @InjectViewModel
    private VfsViewerTabViewModel viewModel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
	vfs_name.setText(viewModel.getConfigName());
	vfs_list.setRoot(viewModel.getListRoot());
	vfs_variants.setItems(viewModel.getVariantsList());
	viewModel.populateTree();

	vfs_list.getSelectionModel().selectedIndexProperty().addListener(event -> {
	    select_file();
	});

	viewModel.subscribe(PREVIEW_OPTIONS_UPDATED, (key, value) -> {
	    System.out.println("thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabView.initialize() PREVIEW_OPTIONS_UPDATED");
	    boolean canPrev = (boolean) value[0];
	    boolean canLoad = (boolean) value[1];
	    boolean canExtr = (boolean) value[2];
	    System.out.println("thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabView.initialize(): " + canPrev + "/" + canLoad + "/" + canExtr);
	    btn_prev.setDisable(!canPrev);
	    btn_open.setDisable(!canLoad);
	    btn_extract.setDisable(!canExtr);
	});

	viewModel.subscribe(PREVIEW_REQUESTED, (key, value) -> {
	    System.out.println("thebob.ja2maptool.ui.tabs.viewers.vfs.VfsViewerTabView.initialize() PREVIEW_REQUESTED");
	    VfsViewerTabViewModel.DetectedAssetType assetType = (VfsViewerTabViewModel.DetectedAssetType) value[0];
	    VFSAccessor assetAccessor = vfs_variants.getSelectionModel().getSelectedItem();
	    switch (assetType) {
		case STI:
		    loadStiPreview(assetAccessor);
		    break;
		case SLF:
		    break;
		case Map:
		    loadMapPreview(assetAccessor);
		    break;
		case Text:
		    break;
		case Unknown:
		    break;
		default:
		    throw new AssertionError(assetType.name());
	    }
	});
    }

    ViewTuple<MapViewerTabView, MapViewerTabViewModel> mapViewer = null;

    private void loadMapViewer(MapScope scope) {
	mapViewer = FluentViewLoader.fxmlView(MapViewerTabView.class)
		.providedScopes(scope)
		.load();
    }

    private void loadMapPreview(VFSAccessor source) {

	AssetManager assets = viewModel.getAssets();
	MapData data = assets.getMaps().loadMapData(source.getBytes());

	MapScope scope = new MapScope();
	scope.setMapData(data);
	scope.setTilesetId(data.getSettings().iTilesetID);
	scope.setTileset(assets.getTilesets().getTileset(data.getSettings().iTilesetID));
	scope.setMapAssets(assets);
	scope.setMapName(source.getPath());

	if (mapViewer == null) {
	    loadMapViewer(scope);
	} else {
	    mapViewer.getViewModel().setMapScope(scope);
	}

	Parent content = mapViewer.getView();

	vfs_preview.getChildren().clear();
	vfs_preview.getChildren().add(content);

	vfs_preview.setTopAnchor(content, 0d);
	vfs_preview.setLeftAnchor(content, 0d);
	vfs_preview.setRightAnchor(content, 0d);
	vfs_preview.setBottomAnchor(content, 0d);

	mapViewer.getViewModel().updateRenderer(true);
    }

    private void loadStiPreview(VFSAccessor source) {
	StiViewerScope scope = new StiViewerScope();
	scope.setFileBytes(source.getBytes());

	ViewTuple<StiViewerTabView, StiViewerTabViewModel> selectorTouple = FluentViewLoader.fxmlView(StiViewerTabView.class)
		.providedScopes(scope)
		.load();

	Parent content = selectorTouple.getView();

	vfs_preview.getChildren().clear();
	vfs_preview.getChildren().add(content);

	vfs_preview.setTopAnchor(content, 0d);
	vfs_preview.setLeftAnchor(content, 0d);
	vfs_preview.setRightAnchor(content, 0d);
	vfs_preview.setBottomAnchor(content, 0d);
    }

}
