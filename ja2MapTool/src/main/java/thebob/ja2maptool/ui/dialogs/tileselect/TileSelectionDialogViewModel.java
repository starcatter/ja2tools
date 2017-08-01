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
package thebob.ja2maptool.ui.dialogs.tileselect;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import static thebob.assetloader.common.LayerConstants.gTileSurfaceName;
import thebob.assetloader.tileset.Tile;
import thebob.ja2maptool.model.TileMapping;
import thebob.ja2maptool.scopes.TilesetMappingScope;
import static thebob.ja2maptool.scopes.TilesetMappingScope.REFRESH_MAPPING_LIST;

/**
 *
 * @author the_bob
 */
public class TileSelectionDialogViewModel implements ViewModel {

    public static final String UPDATE_SELECTION = "UPDATE_SELECTION";
    public static final String CLOSE_DIALOG_NOTIFICATION = "CLOSE_DIALOG_NOTIFICATION";
    ObservableList<String> categories = FXCollections.observableArrayList();

    private int type = 0;
    private int index = 0;

    @InjectScope
    private TilesetMappingScope mappingScope;

    public void initialize() {
	for (int i = 0; i < mappingScope.getTargetAssets().getTilesets().getNumFiles(); i++) {
	    categories.add(TilesetMappingScope.getTileCategortyName(i));
	}
    }

    public ObservableList<String> getTileCategories() {
	return categories;
    }

    public void setInitialSelection(int selectedType, int selectedIndex) {
	type = selectedType;
	index = selectedIndex;

	publish(UPDATE_SELECTION);
    }

    ObservableList<Tile> getTilesForType(int selectedIndex) {
	return FXCollections.observableArrayList(mappingScope.getTargetTileset().getTiles(selectedIndex));
    }

    public int getType() {
	return type;
    }

    public int getIndex() {
	return index;
    }

    Image getTargetTileImage(int type, int index) {
	if (type >= 0 && index >= 0) {
	    Tile tile = mappingScope.getTargetAssets().getTilesets().getTileset(mappingScope.getTargetTilesetId()).getTile(type, index);
	    if (tile != null) {
		return tile.getImage();
	    }
	}
	return null;
    }

    void updateTiles(int selectedType, int selectedIndex) {
	TileMapping tileMapping = mappingScope.getMappingList().get(type).getMappings().get(index);

	tileMapping.setTargetType(selectedType);
	tileMapping.setTargetIndex(selectedIndex);
	tileMapping.setMappingMode( TileMapping.MappingMode.Manual );
	mappingScope.publish(REFRESH_MAPPING_LIST);
    }

}
