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
package thebob.ja2maptool.util.tilesearch.histogram.models;

import thebob.ja2maptool.util.tilesearch.com.telmomenezes.jfastemd.Feature2D;
import thebob.ja2maptool.util.tilesearch.com.telmomenezes.jfastemd.JFastEMD;
import thebob.ja2maptool.util.tilesearch.com.telmomenezes.jfastemd.Signature;

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
