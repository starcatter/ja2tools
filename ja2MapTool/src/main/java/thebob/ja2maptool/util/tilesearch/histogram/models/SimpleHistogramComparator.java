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
package thebob.ja2maptool.util.tilesearch.histogram.models;

/**
 *
 * @author the_bob
 */
public class SimpleHistogramComparator implements HistogramComparator {
    
    public static int trim = 1;

    public double compareHistograms(double[][] h1, double[][] h2) {
	double distance = 0;
	int histogramSize = h1[0].length;

	for (int i = trim; i < histogramSize - trim; i++) {
	    distance += Math.abs(h1[0][i] - h2[0][i]);
	    distance += Math.abs(h1[1][i] - h2[1][i]);
	    distance += Math.abs(h1[2][i] - h2[2][i]);
	}
	return distance;
    }

}
