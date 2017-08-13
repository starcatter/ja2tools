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
package thebob.ja2maptool.util.map.layers.base;

import java.util.Observer;
import thebob.assetloader.tileset.Tileset;
import thebob.ja2maptool.util.map.events.MapEvent;

/**
 *
 * @author the_bob
 */
public interface ITileLayerGroup extends Iterable<TileLayer> {

    /**
     *
     * @param mapRows
     * @param mapCols
     * @param tileset
     */
    void init(int mapRows, int mapCols, Tileset tileset);

    // -----------------------
    // cell coordinate transformations
    // -----------------------
    /**
     * Calculates cell id for given cell Y/X coordinates. Note the unusual
     * coordinate order <b>Y</b> (row) coordinate goes first, <b>X</b> (col)
     * coordinate second.
     *
     * @param y cell row
     * @param x cell column
     * @return cell id for the map of this size
     */
    int rowColToPos(int y, int x);

    /**
     *
     * @param sGridNo
     * @return X (column) coordinate of this grid for a map of this size
     */
    public int GridNoToCellX(int sGridNo);

    /**
     *
     * @param sGridNo
     * @return Y (row) coordinate of this grid for a map of this size
     */
    public int GridNoToCellY(int sGridNo);

    // -----------------------
    // layer info and setup
    // -----------------------
    /**
     *
     * @return map width in cells
     */
    int getMapCols();

    /**
     *
     * @return map height in cells
     */
    int getMapRows();

    /**
     *
     * @return total number of cells on this layer.
     */
    int getMapSize();

    Tileset getTileset();

    void setTileset(Tileset tileset);

    // observable implementation TODO: to be deleted and replaced by default
    <T extends MapEvent> void notifySubscribers(T message);

    void subscribe(Observer observer);

    void unsubscribe(Observer observer);

}
