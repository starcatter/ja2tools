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
package thebob.ja2maptool.util.compositor;

/**
 *
 * @author the_bob
 */
public class SelectionPlacementOptions {

    private final boolean place_land;
    private final boolean place_objects;
    private final boolean place_structures;
    private final boolean place_shadows;
    private final boolean place_roofs;
    private final boolean place_onRoof;

    private final boolean place_land_floors;
    private final boolean place_structures_walls;

    public SelectionPlacementOptions(SelectionPlacementOptions that) {
        this.place_land = that.place_land;
        this.place_objects = that.place_objects;
        this.place_structures = that.place_structures;
        this.place_shadows = that.place_shadows;
        this.place_roofs = that.place_roofs;
        this.place_onRoof = that.place_onRoof;
        this.place_land_floors = that.place_land_floors;
        this.place_structures_walls = that.place_structures_walls;
    }

    public SelectionPlacementOptions(boolean place_land, boolean place_objects, boolean place_structures, boolean place_shadows, boolean place_roofs, boolean place_onRoof, boolean place_land_floors, boolean place_structures_walls) {
        this.place_land = place_land;
        this.place_objects = place_objects;
        this.place_structures = place_structures;
        this.place_shadows = place_shadows;
        this.place_roofs = place_roofs;
        this.place_onRoof = place_onRoof;
        this.place_land_floors = place_land_floors;
        this.place_structures_walls = place_structures_walls;
    }

    SelectionPlacementOptions(boolean[] layers) {
        this.place_land = layers[0];
        this.place_objects = layers[1];
        this.place_structures = layers[2];
        this.place_shadows = layers[3];
        this.place_roofs = layers[4];
        this.place_onRoof = layers[5];
        if (layers.length > 6) {
            this.place_land_floors = layers[6];
            this.place_structures_walls = layers[7];
        } else {
            this.place_land_floors = this.place_land;
            this.place_structures_walls = this.place_structures;
        }
    }

    public boolean[] getAsArray() {
        return new boolean[]{
            place_land,
            place_objects,
            place_structures,
            place_shadows,
            place_roofs,
            place_onRoof,};
    }

    public boolean isPlace_land_floors() {
        return place_land_floors;
    }

    public boolean isPlace_structures_walls() {
        return place_structures_walls;
    }

}
