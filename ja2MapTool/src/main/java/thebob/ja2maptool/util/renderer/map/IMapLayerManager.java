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
package thebob.ja2maptool.util.renderer.map;

import thebob.ja2maptool.util.compositor.SelectionPlacementOptions;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.renderer.base.ITileLayerGroup;
import thebob.ja2maptool.util.renderer.cursor.MapCursor;

/**
 * Extended MapLayer interface including ITileLayerGroup access
 * @author the_bob
 */
public interface IMapLayerManager extends IMapLayerControls, ITileLayerGroup{

    public SelectedTiles getTilesForSelection(SelectedTiles selection);

    public void appendTiles(MapCursor placement, SelectedTiles selection, SelectionPlacementOptions options);
    
}
