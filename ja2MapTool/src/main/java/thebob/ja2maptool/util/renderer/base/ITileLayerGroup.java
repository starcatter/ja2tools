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

import thebob.ja2maptool.util.renderer.events.RendererEvent;
import java.util.Observable;
import java.util.Observer;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.renderer.base.TileLayer;

/**
 *
 * @author the_bob
 */
public interface ITileLayerGroup extends Iterable<TileLayer> {

    int getMapCols();

    int getMapRows();

    int getMapSize();

    Tileset getTileset();

    void setTileset(Tileset tileset);
    
    <T extends RendererEvent> void notifySubscribers(T message);
    void subscribe(Observer observer);
    void unsubscribe(Observer observer);

}
