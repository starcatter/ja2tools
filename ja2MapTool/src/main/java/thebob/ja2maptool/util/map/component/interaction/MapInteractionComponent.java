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
package thebob.ja2maptool.util.map.component.interaction;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.stream.Collectors;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import thebob.ja2maptool.util.map.component.interaction.data.MapInteractionData;
import thebob.ja2maptool.util.map.component.interaction.layer.MapInteractionLayer;
import thebob.ja2maptool.util.map.component.interaction.target.IMapInteractiveComponent;
import thebob.ja2maptool.util.map.controller.base.MapControllerBase;
import thebob.ja2maptool.util.map.layers.map.IMapLayerManager;
import thebob.ja2maptool.util.map.renderer.ITileRendererManager;

/**
 * The interaction component is hooked up to the cursor component and keeps
 * track of areas on the map that should react to being clicked or hovered.
 *
 * @author starcatter
 */
public class MapInteractionComponent extends MapControllerBase implements IMapInteractionComponent {

    // map of registered layers (one per component)
    Map<IMapInteractiveComponent, MapInteractionLayer> layers = new HashMap<IMapInteractiveComponent, MapInteractionLayer>();
    // multimap of all active cells mapped to their layers.
    ListMultimap<Integer, MapInteractionLayer> layerMap = ArrayListMultimap.create();

    public MapInteractionComponent(ITileRendererManager renderer, IMapLayerManager map) {
        super(renderer, map);
    }

    @Override
    public MapInteractionLayer getLayer(IMapInteractiveComponent self) {
        if (layers.containsKey(self) == false) {
            MapInteractionLayer layer = new MapInteractionLayer(self);
            layers.put(self, layer);
            return layer;
        } else {
            return layers.get(self);
        }
    }

    @Override
    public void refreshLayers() {
        layerMap.clear();
        layers.forEach((component, layer) -> {
            layer.getCells().forEach(cell -> {
                layerMap.put(cell, layer);
            });
        });
    }

    @Override
    public void hoverCell(int cell, MapInteractionData data) {
        List<MapInteractionLayer> missedLayers = layers.values().stream().filter( l -> l.isHovered() ).collect(Collectors.toList());

        boolean consumed = false;
        for (MapInteractionLayer layer : layerMap.get(cell)) {
            if (consumed == false) {
                if (layer.hoverCell(cell, data)) {
                    consumed = true;
                }
            }
            missedLayers.remove(layer);
        }

        missedLayers.forEach( l -> l.hoverOff() );        
    }

    @Override
    public boolean activateCell(int cell, MapInteractionData data) {
        for (MapInteractionLayer component : layerMap.get(cell)) {
            if (component.activateCell(cell, data)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void mouseEvent(MouseEvent e) {

    }

    @Override
    public void keyEvent(KeyEvent e) {

    }

    @Override
    public void update(Observable o, Object arg) {

    }

}
