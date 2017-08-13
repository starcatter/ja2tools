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
package thebob.ja2maptool.ui.tabs.viewers.map;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.scopes.map.MapScope;
import static thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.MAP_LOADED;
import thebob.ja2maptool.util.map.IMapDisplayManager;
import thebob.ja2maptool.util.map.MapDisplayManager;
import thebob.ja2maptool.util.map.controller.viewer.base.IMapViewerController;

/**
 *
 * @author the_bob
 */
public class MapViewerTabViewModel implements ViewModel {

    public static final String VIEWER_MODE_SET = "VIEWER_MODE_SET";
    public static final String TOOLBAR_SWITCH = "TOOLBAR_SWITCH";
    public static final String FOCUS_WINDOW = "FOCUS_WINDOW";

    @InjectScope
    MapScope mapScope;

    IMapDisplayManager renderer = new MapDisplayManager();
    IMapViewerController viewer = null;

    StringProperty mapNameProperty = new SimpleStringProperty();

    public enum MapViewerMode {
	Browser,
	Editor
    }

    public void initialize() {
	if (viewer == null) {
	    setViewerMode(MapViewerMode.Browser);
	}
	mapScope.subscribe(MapScope.MAP_UPDATED, (key, values) -> {
	    updateRenderer(true);
	});
    }

    public void updateRenderer(boolean centerMap) {
	if (mapScope == null || mapScope.getMapData() == null) {
	    return;
	}

	mapNameProperty.set(mapScope.getMapName());
	int mapTilesetId = mapScope.getTilesetId();
	Tileset tileset = mapScope.getTileset();

	if (tileset == null) {
	    System.err.println("Original tileset could not be loaded! (" + mapTilesetId + ")");
	    tileset = mapScope.getMapAssets().getTilesets().getTileset(0);
	}

	int oldX = viewer.getWindowOffsetX();
	int oldY = viewer.getWindowOffsetY();

	renderer.setMapTileset(tileset);
	renderer.loadMap(mapScope.getMapData());

	if (!centerMap) {
	    viewer.setWindowOffsetX(oldX);
	    viewer.setWindowOffsetY(oldY);
	}

	// viewModel.scrollPreview(0, 0); // <- renderer should update itself
	publish(MAP_LOADED);
    }

    public IMapDisplayManager getRenderer() {
	return renderer;
    }

    public MapScope getMapScope() {
	return mapScope;
    }

    public void setMapScope(MapScope mapScope) {
	this.mapScope = mapScope;
    }

    void scrollPreview(int xDelta, int yDelta) {
	viewer.moveWindow(xDelta, yDelta);
    }

    public StringProperty getMapNameProperty() {
	return mapNameProperty;
    }

    void setLayerButtons(BooleanProperty[] viewerButtons) {
	renderer.setMapLayerButtons(viewerButtons);
    }

    public IMapViewerController getViewer() {
	return viewer;
    }

    public void setViewerMode(MapViewerMode mode) {
	switch (mode) {
	    case Browser:
		viewer = renderer.connectBasicViewer(this);
		break;
	    case Editor:
		viewer = renderer.connectEditorViewer(this);
		break;
	    default:
		throw new AssertionError(mode.name());
	}
	publish(VIEWER_MODE_SET, mode);
    }

    public void focusWindow() {
        publish(FOCUS_WINDOW);
    }
    
    public void toggleToolbars() {
	publish(TOOLBAR_SWITCH);
    }
    
}
