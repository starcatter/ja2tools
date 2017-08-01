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
