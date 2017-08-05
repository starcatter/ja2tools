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
package thebob.assetloader.common;

import java.nio.ByteBuffer;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 *
 * @author the_bob
 */
public class ImageAdapter {

    public static Image convertStiImage(int width, int height, byte[] imagePixels, byte[][] palette) {

	int[] palette2 = new int[256];
	// transparent color
	int color0 = 0;

	// 255 looks like the highlight/outline color, it makes evetything suck.
	//palette[0][255] = (byte) 0; //Byte.MIN_VALUE; 
	//palette[1][255] = (byte) 0; //Byte.MIN_VALUE; 
	//palette[2][255] = (byte) 0; //Byte.MIN_VALUE; 
	for (int i = 0; i < 256; i++) {

	    int r = (palette[0][i] & 0xFF) << 16;
	    int g = (palette[1][i] & 0xFF) << 8;
	    int b = (palette[2][i] & 0xFF);
	    int a = 0xFF << 24;

	    int color = r | g | b | a;

	    if (i == 0) {
		color0 = color;
	    } else if (color == color0) {
		palette2[i] = 0;
	    } else {
		palette2[i] = color;
	    }

	}
	WritableImage wimg = new WritableImage(width, height);
	PixelWriter pw = wimg.getPixelWriter();
	PixelFormat<ByteBuffer> pf = PixelFormat.createByteIndexedInstance(palette2);
	pw.setPixels(0, 0, width, height, pf, imagePixels, 0, width);
	return wimg;
    }
}
