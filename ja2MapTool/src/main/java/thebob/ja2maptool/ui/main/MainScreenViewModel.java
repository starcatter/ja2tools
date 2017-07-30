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
package thebob.ja2maptool.ui.main;

import thebob.ja2maptool.scopes.MainScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.Scope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import javafx.scene.control.Tab;
import thebob.ja2maptool.scopes.ItemMappingScope;
import static thebob.ja2maptool.scopes.MainScope.UPDATE_SCOPES;
import thebob.ja2maptool.scopes.TilesetMappingScope;
import thebob.ja2maptool.scopes.VfsAssetScope;
import thebob.ja2maptool.ui.tabs.mapping.items.ItemMappingTabView;
import thebob.ja2maptool.ui.tabs.mapping.tileset.TilesetMappingTabView;

/**
 *
 * @author the_bob
 */
@ScopeProvider(scopes = {VfsAssetScope.class, MainScope.class})
public class MainScreenViewModel implements ViewModel {

    public static final String ADD_TAB = "ADD_TAB";
    public static final String SHOW_TAB = "SHOW_TAB";
    public static final String UPDATE_SCOPE_MENUS = "UPDATE_SCOPE_MENUS";

    @InjectScope
    VfsAssetScope vfsAssets;
    @InjectScope
    MainScope mainScreen;

    public void initialize() {
	mainScreen.subscribe(MainScope.NEW_TAB, (key, payload) -> {
	    publish(ADD_TAB, payload);
	});
	mainScreen.subscribe(MainScope.GOTO_TAB, (key, payload) -> {
	    publish(SHOW_TAB, payload);
	});
	mainScreen.subscribe(MainScope.UPDATE_SCOPES, (key, payload) -> {
	    publish(UPDATE_SCOPE_MENUS, mainScreen.getActiveItemMappings(), payload);
	});
    }

    VfsAssetScope getVfsScope() {
	return vfsAssets;
    }

    MainScope getMainScope() {
	return mainScreen;
    }

    void registerScopeTab(Tab tab, Scope tabScope) {
	mainScreen.registerScopeTab(tabScope, tab);
    }

    void loadItemMapping(String path) {
	ItemMappingScope itemMappingScope = ItemMappingScope.loadFromFile(path, vfsAssets);
	mainScreen.publish(MainScope.NEW_TAB, new Object[]{ItemMappingTabView.class, "Map items", itemMappingScope});
	mainScreen.registerItemMappingScope(itemMappingScope);
    }

    void loadTilesetMapping(String path) {
	TilesetMappingScope tilesetMappingScope = TilesetMappingScope.loadFromFile(path, vfsAssets);

	mainScreen.publish(MainScope.NEW_TAB, new Object[]{TilesetMappingTabView.class, "Map tileset", tilesetMappingScope});
	mainScreen.registerTilesetMappingScope(tilesetMappingScope);
    }

    void loadSLF(String path) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void loadSTI(String path) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
