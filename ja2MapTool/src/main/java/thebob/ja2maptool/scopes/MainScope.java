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
package thebob.ja2maptool.scopes;

import de.saxsys.mvvmfx.Scope;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Tab;

/**
 *
 * @author the_bob
 */
public class MainScope implements Scope {

    public static final String NEW_TAB = "NEW_TAB";
    public static final String GOTO_TAB = "GOTO_TAB";

    public static final String UPDATE_SCOPES = "UPDATE_SCOPES";

    public enum TabTypes {
	TAB_VFS_SETUP,	
	TAB_MAPPING_SETUP,
	TAB_CONVERT
    }

    ListChangeListener<Scope> listener = new ListChangeListener<Scope>(){
	@Override
	public void onChanged(ListChangeListener.Change<? extends Scope> c) {
	    publish(UPDATE_SCOPES);
	}
    };
    
    ObservableList<TilesetMappingScope> activeTilesetMappings = FXCollections.observableArrayList();
    ObservableList<ItemMappingScope> activeItemMappings = FXCollections.observableArrayList();
    ObservableList<ConvertMapScope> activeMapConversions = FXCollections.observableArrayList();

    ObservableMap<Scope, Tab> openedScopeTabs = FXCollections.observableHashMap();

    public MainScope(){
	activeTilesetMappings.addListener(listener);
	activeItemMappings.addListener(listener);
	activeMapConversions.addListener(listener);
    }
    
    public Tab getTabForScope(Scope scope) {
	return openedScopeTabs.get(scope);
    }

    public void registerScopeTab(Scope scope, Tab tab) {
	openedScopeTabs.put(scope, tab);
    }

    public void freeScopeTab(Scope scope) {
	openedScopeTabs.remove(scope);
    }

    public void registerMapConversionScope(ConvertMapScope scope) {
	activeMapConversions.add(scope);
    }

    public void registerTilesetMappingScope(TilesetMappingScope scope) {
	activeTilesetMappings.add(scope);
    }

    public void registerItemMappingScope(ItemMappingScope scope) {
	activeItemMappings.add(scope);
    }

    public void freeItemMappingScope(ItemMappingScope scope) {
	activeItemMappings.remove(scope);
    }

    public void freeTilesetMappingScope(TilesetMappingScope scope) {
	activeTilesetMappings.remove(scope);
    }

    public void freeMapConversionScope(ConvertMapScope scope) {
	activeMapConversions.remove(scope);
    }

    public List<ConvertMapScope> getActiveMapConversions() {
	return activeMapConversions;
    }

    public List<TilesetMappingScope> getActiveTilesetMappings() {
	return activeTilesetMappings;
    }

    public List<ItemMappingScope> getActiveItemMappings() {
	return activeItemMappings;
    }

}
