/*
 * Copyright (C) 2017 the_bob.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
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

    public boolean[] getAsArray() {
	return new boolean[]{
	    place_land,
	    place_objects,
	    place_structures,
	    place_shadows,
	    place_roofs,
	    place_onRoof,
	};
    }

    public boolean isPlace_land_floors() {
	return place_land_floors;
    }

    public boolean isPlace_structures_walls() {
	return place_structures_walls;
    }

    
    
}
