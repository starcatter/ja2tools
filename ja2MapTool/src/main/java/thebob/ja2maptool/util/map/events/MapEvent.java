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
package thebob.ja2maptool.util.map.events;

/**
 *
 * @author the_bob
 */
public class MapEvent {

    public enum ChangeType {
        // Layer events
        LAYER_ALTERED,
        // MapLayer events
        MAP_LOADED, MAP_ALTERED,
        // TileRenderer events
        MAP_WINDOW_MOVED, MAP_WINDOW_ZOOMED, MAP_CANVAS_CHANGED,
        // Selection controller events
        SELECTION_CHANGED, SELECTION_STARTED, SELECTION_CLEARED,
        // Clipboard controller events
        CLIPBOARD_PLACED, CLIPBOARD_FILLED, CLIPBOARD_EMPTIED,
        // placement management
        PLACEMENT_ADDED, PLACEMENT_DELETED, PLACEMENT_PICKED, PLACEMENT_HOVERED,PLACEMENT_CANCELED, PLACEMENT_SELECTED, PLACEMENT_DESELECTED
    }

    ChangeType type;
    MapEventPayload payload = null;

    public MapEvent(ChangeType type) {
        this.type = type;
    }

    public MapEvent(ChangeType type, MapEventPayload payload) {
        this.type = type;
        this.payload = payload;
    }

    public ChangeType getType() {
        return type;
    }

    public MapEventPayload getPayload() {
        return payload;
    }    
    
    @Override
    public String toString() {
        return "TileLayerGroupChange{" + "type=" + type + '}';
    }

}
