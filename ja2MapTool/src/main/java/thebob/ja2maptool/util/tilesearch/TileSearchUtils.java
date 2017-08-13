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
package thebob.ja2maptool.util.tilesearch;

/**
 *
 * @author the_bob
 */
public class TileSearchUtils {

    public static int[] remapPalette(byte[][] palette, byte[][] palette0) {
	int[] remap = new int[256];
	for (int i = 0; i < 256; i++) {
	    int bestDistance = Integer.MAX_VALUE;
	    int bestMap = i;

	    for (int j = 0; j < 256; j++) {
		int distance = Math.abs(palette[0][i] - palette0[0][j]) + Math.abs(palette[1][i] - palette0[1][j]) + Math.abs(palette[2][i] - palette0[2][j]);
		if (distance < bestDistance) {
		    bestDistance = distance;
		    bestMap = j;

		    if (bestDistance == 0) {
			break;
		    }
		}
	    }

	    remap[i] = bestMap;
	}
	return remap;
    }
}
