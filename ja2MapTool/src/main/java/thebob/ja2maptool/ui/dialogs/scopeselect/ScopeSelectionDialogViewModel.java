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
package thebob.ja2maptool.ui.dialogs.scopeselect;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.Scope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.scene.control.TreeItem;
import thebob.ja2maptool.scopes.map.ConvertMapScope;
import thebob.ja2maptool.scopes.mapping.ItemMappingScope;
import thebob.ja2maptool.scopes.MainScope;
import thebob.ja2maptool.scopes.mapping.TilesetMappingScope;

/**
 *
 * @author the_bob
 */
public class ScopeSelectionDialogViewModel implements ViewModel {

    public static final String CLOSE_DIALOG_NOTIFICATION = "CLOSE_DIALOG_NOTIFICATION";

    public enum ScopeSelectorType {
	SCOPE_MAP_TILES,
	SCOPE_MAP_ITEMS;
    }
    ScopeSelectorType selectorType;

    @InjectScope
    MainScope mainScope;
    @InjectScope
    ConvertMapScope mapScope;

    TreeItem<Scope> listRoot = new TreeItem<Scope>(null);

    void loadScope(Scope selectedItem) {
	mapScope.publish(ConvertMapScope.SCOPE_SELECTED, selectorType, selectedItem);
    }

    TreeItem<Scope> getScopeListRoot() {
	listRoot.setExpanded(true);
	return listRoot;
    }

    public void setSelectorType(ScopeSelectorType type) {
	selectorType = type;
	switch (selectorType) {
	    case SCOPE_MAP_TILES:
		for (TilesetMappingScope scope : mainScope.getActiveTilesetMappings()) {
		    listRoot.getChildren().add(new TreeItem<Scope>(scope));
		}
		break;
	    case SCOPE_MAP_ITEMS:
		for (ItemMappingScope scope : mainScope.getActiveItemMappings()) {
		    listRoot.getChildren().add(new TreeItem<Scope>(scope));
		}		
		break;
	    default:
		throw new AssertionError(selectorType.name());
	}
    }
}
