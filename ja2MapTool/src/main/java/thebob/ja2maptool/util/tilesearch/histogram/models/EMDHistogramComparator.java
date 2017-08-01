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

import com.telmomenezes.jfastemd.Feature2D;
import com.telmomenezes.jfastemd.JFastEMD;
import com.telmomenezes.jfastemd.Signature;

/**
 * I'm fairly confident I'm doing something massively wrong here. The results are slow and terrible. This was supposed to be fast and good.
 * @author the_bob
 */
public class EMDHistogramComparator implements HistogramComparator {

    public Signature getSig(double[][] h1) {
	int histogramSize = h1[0].length;

	int f1 = 0;
	for (double[] v : h1) {
	    for (double v2 : v) {
		if (v2 > 0) {
		    f1++;
		}
	    }
	}
	// compute features and weights
	Feature2D[] features = new Feature2D[f1];
	double[] weights = new double[f1];
	int i = 0;
	for (int x = 0; x < 3; x++) {
	    for (int y = 0; y < histogramSize; y++) {
		double val = h1[x][y];
		if (val > 0) {
		    Feature2D f = new Feature2D(x, y);
		    features[i] = f;
		    weights[i] = val;
		    i++;
		}
	    }
	}
	Signature signature = new Signature();
	signature.setNumberOfFeatures(f1);
	signature.setFeatures(features);
	signature.setWeights(weights);
	return signature;
    }

    public double compareHistogramsEMD(Signature sig1, Signature sig2) {
	return JFastEMD.distance(sig1, sig2, -1);
    }

    @Override
    public double compareHistograms(double[][] h1, double[][] h2) {
	Signature sig1 = getSig(h1);
	Signature sig2 = getSig(h2);
	return compareHistogramsEMD(sig1, sig2);
    }
}
