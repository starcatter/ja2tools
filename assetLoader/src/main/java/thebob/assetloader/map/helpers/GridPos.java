/*
 * The MIT License
 *
 * Copyright 2017 the_bob.
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
package thebob.assetloader.map.helpers;

import thebob.assetloader.map.core.MapData;

/**
 * @author the_bob
 */
public class GridPos {

    public static MapData map;

    public long sYPos;
    public long sXPos;
    long sGridNo;

    public GridPos(int sGridNo) {
        this((long) sGridNo);
    }

    public GridPos(long sGridNo) {
        this.sGridNo = sGridNo;
        sYPos = sGridNo / map.getSettings().iColSize;
        sXPos = (sGridNo - (sYPos * map.getSettings().iColSize));

        if (sYPos > map.getSettings().iColSize) throw new RuntimeException("Invalid grid number! " + toString());
        if (sXPos > map.getSettings().iRowSize) throw new RuntimeException("Invalid grid number!" + toString());

        if (sYPos < 0) throw new RuntimeException("Invalid grid number!");
        if (sXPos < 0) throw new RuntimeException("Invalid grid number!");
    }

    @Override
    public String toString() {
        return "GridPos{" + "sYPos=" + sYPos + ", sXPos=" + sXPos + ", sGridNo=" + sGridNo + '}';
    }

}
