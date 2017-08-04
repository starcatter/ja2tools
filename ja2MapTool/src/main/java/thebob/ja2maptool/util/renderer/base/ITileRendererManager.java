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
package thebob.ja2maptool.util.renderer.base;

import java.util.List;
import java.util.Observer;

/**
 * Extended tile renderer interface including methods for managing its state
 *
 * @author the_bob
 */
public interface ITileRendererManager extends ITileRendererControls, Observer {

    /*
     * Direct access to render layer group data. Note that changes done this way might not properly update all the renderer settings.
     * Use the add/remove methods to for manipulating the layer groups.
     */
    List<ITileLayerGroup> getRenderLayers();

    void addRenderLayer(ITileLayerGroup layer);

    void removeRenderLayer(ITileLayerGroup layer);

    void removeRenderLayer(int index);

    public int getCanvasX();

    public int getCanvasY();
}
