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
package thebob.ja2maptool.scopes.map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.saxsys.mvvmfx.Scope;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import thebob.ja2maptool.ui.tabs.compositor.CompositorTabViewModel;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.compositor.SnippetPlacement;

public class MapCompositorScope implements Scope {

    public static final String PLACEMENT_PICKED = "PLACEMENT_PICKED";
    public static final String PLACEMENT_HOVER_ON = "PLACEMENT_HOVER_ON";
    public static final String PLACEMENT_HOVER_OFF = "PLACEMENT_HOVER_OFF";
    public static final String PLACEMENT_SELECT = "PLACEMENT_SELECT";
    public static final String PLACEMENT_LAYERS_CHANGED = "PLACEMENT_LAYERS_CHANGED";
    public static final String PLACEMENT_LAYER_SWITCHED = "PLACEMENT_LAYER_SWITCHED";
    public static final String PLACEMENT_LIST_CHANGED = "PLACEMENT_LIST_CHANGED";

    MapScope map = new MapScope();
    MapSnippetScope loadedSnippets = null;
    ObservableList<SnippetPlacement> placedSnippets = FXCollections.observableArrayList();
    ListMultimap<String, SelectedTiles> loadedSnippetLibs = ArrayListMultimap.create();

    CompositorTabViewModel viewModel = null;

    public MapScope getMap() {
        return map;
    }

    public void setMap(MapScope map) {
        this.map = map;
    }

    public CompositorTabViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(CompositorTabViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public MapSnippetScope getLoadedSnippets() {
        return loadedSnippets;
    }

    public ListMultimap<String, SelectedTiles> getLoadedSnippetLibs() {
        return loadedSnippetLibs;
    }

    public void setLoadedSnippets(MapSnippetScope loadedSnippets) {
        this.loadedSnippets = loadedSnippets;
    }

    public ObservableList<SnippetPlacement> getPlacedSnippets() {
        return placedSnippets;
    }

    public void setPlacedSnippets(ObservableList<SnippetPlacement> placedSnippets) {
        this.placedSnippets = placedSnippets;
    }

    public void addPlacement(SnippetPlacement placement) {
        placedSnippets.add(placement);
        System.out.println("thebob.ja2maptool.scopes.map.MapCompositorScope.addPlacement() " + placement);
    }

    public void deletePlacement(SnippetPlacement placement) {
        placedSnippets.remove(placement);
        System.out.println("thebob.ja2maptool.scopes.map.MapCompositorScope.deletePlacement()");
    }

    public void pickPlacement(SnippetPlacement placement) {
        publish(PLACEMENT_PICKED, placement);
        System.out.println("thebob.ja2maptool.scopes.map.MapCompositorScope.pickPlacement()");
    }

    public void cancelPlacement() {
        publish(PLACEMENT_PICKED, null);
    }

    public void hoverPlacement(SnippetPlacement placement) {
        if (placement != null) {
            publish(PLACEMENT_HOVER_ON, placement);
        } else {
            publish(PLACEMENT_HOVER_OFF);
        }
    }

    public void selectPlacement(SnippetPlacement placement) {
        if (placement != null) {
            publish(PLACEMENT_SELECT, placement);
        }
    }

    public void updateLayers() {
        publish(PLACEMENT_LAYERS_CHANGED);
    }

    public void updateList() {
        publish(PLACEMENT_LIST_CHANGED);
    }

    public void layerSwitched() {
        publish(PLACEMENT_LAYER_SWITCHED);
    }

}
