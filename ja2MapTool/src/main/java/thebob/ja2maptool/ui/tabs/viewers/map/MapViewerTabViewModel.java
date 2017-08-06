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
package thebob.ja2maptool.ui.tabs.viewers.map;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.scopes.map.MapScope;
import static thebob.ja2maptool.ui.tabs.convert.ConvertMapTabViewModel.MAP_LOADED;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.renderer.DisplayManager;
import thebob.ja2maptool.util.renderer.IMapDisplayManager;

/**
 *
 * @author the_bob
 */
public class MapViewerTabViewModel implements ViewModel {

    @InjectScope
    MapScope mapScope;

    IMapDisplayManager renderer = new DisplayManager();//new MapRenderer();

    StringProperty mapNameProperty = new SimpleStringProperty();

    public void initialize() {
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

	int oldX = renderer.getWindowOffsetX();
	int oldY = renderer.getWindowOffsetY();

	renderer.setMapTileset(tileset);
	renderer.loadMap(mapScope.getMapData());

	if (!centerMap) {
	    renderer.setWindowOffsetX(oldX);
	    renderer.setWindowOffsetY(oldY);
	}

	renderer.moveWindow(0, 0);
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
	renderer.moveWindow(xDelta, yDelta);
    }

    public StringProperty getMapNameProperty() {
	return mapNameProperty;
    }

    void clearSelection() {
	mapScope.setSelection(null);
    }

    void getSelection() {
	SelectedTiles selection = renderer.getSelection();
	// store the selection in the map scope
	if (selection != null) {
	    mapScope.setSelection(selection);
	}
    }

    void setLayerButtons(BooleanProperty[] viewerButtons) {
	renderer.setMapLayerButtons(viewerButtons);
    }

}
