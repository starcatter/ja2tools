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
package thebob.assetloader.tileset;

import java.nio.ByteBuffer;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import thebob.assetloader.common.ImageAdapter;
import thebob.assetloader.sti.StiLoader;

/**
 *
 * @author the_bob
 */
public class Tile {
    
    Image image = null;
    StiLoader loader = null;
    int type = -1;
    int index = -1;
    int width = 0;
    int height = 0;
    int offsetX = 0;
    int offsetY = 0;

    public Tile(StiLoader loader, int index, int type) {
        this.loader = loader;
        this.index = index;
        this.type = type;
    }

    public Image getImage() {
        if (image == null) {
            loadImage();
        }
        return image;
    }

    private void loadImage() {
        width = loader.getImageWidth(index);
        height = loader.getImageHeight(index);
        offsetX = loader.getImageOffsetX(index);
        offsetY = loader.getImageOffsetY(index);
        
	// TODO: do this once per file, in stiloader or tileloader        
        image = ImageAdapter.convertStiImage(width, height, loader.getImage(index), loader.getPalette());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getType() {
	return type;
    }

    public int getIndex() {
	return index;
    }
    
    
}
