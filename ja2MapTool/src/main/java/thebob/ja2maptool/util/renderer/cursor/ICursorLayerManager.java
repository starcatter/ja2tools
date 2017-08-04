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
package thebob.ja2maptool.util.renderer.cursor;

import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.compositor.SelectedTiles;
import thebob.ja2maptool.util.renderer.base.ITileLayerGroup;

/**
 * Extended CursorLayer interface including ITileLayerGroup access
 *
 * @author the_bob
 */
public interface ICursorLayerManager extends ICursorLayerControls, ITileLayerGroup {

    public void init(int mapRows, int mapCols, Tileset tileset);

    void sendCursor(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown);

    void sendClick(double dx, double dy, boolean controlDown, boolean shiftDown, boolean altDown);

    public void setWindow(int windowOffsetX, int windowOffsetY, double scale);

    public void setCanvasSize(int canvasX, int canvasY);

    public SelectedTiles getSelection();

    public void setPlacementPreview(SelectedTiles selection);

    public MapCursor getPlacementCursor();

}
