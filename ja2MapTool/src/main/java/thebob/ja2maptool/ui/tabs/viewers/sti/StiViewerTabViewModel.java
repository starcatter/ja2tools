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
package thebob.ja2maptool.ui.tabs.viewers.sti;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import thebob.assetloader.common.ImageAdapter;
import thebob.assetloader.sti.StiLoader;
import thebob.ja2maptool.scopes.view.StiViewerScope;
import thebob.ja2maptool.scopes.view.VfsBrowserScope;

/**
 *
 * @author the_bob
 */
public class StiViewerTabViewModel implements ViewModel {

    @InjectScope
    StiViewerScope scope;

    StiLoader loader = new StiLoader();

    StringProperty stiName = new SimpleStringProperty();
    StringProperty stiStatus = new SimpleStringProperty();

    int _currentIndex = 0;
    IntegerProperty currentIndex = new SimpleIntegerProperty(0);
    IntegerProperty maxIndex = new SimpleIntegerProperty(-1);

    byte[][] palette;

    public void initialize() {
	if (scope.getFilePath() != null) {
	    loader.loadFile(scope.getFilePath());
	} else if (scope.getFileBytes() != null) {
	    loader.loadAsset(scope.getFileBytes());
	} else {

	}

	maxIndex.set(loader.getImageCount() - 1);
	palette = loader.getPalette();

	currentIndex.addListener(event -> {
	    stiStatus.set(currentIndex.get() + "/" + maxIndex.get() + ", " + loader.getImageWidth(_currentIndex) + "x" + loader.getImageHeight(_currentIndex));
	});
    }

    Image getImage() {
	return getImage(_currentIndex);
    }

    Image getImage(int i) {
	return ImageAdapter.convertStiImage(loader.getImageWidth(i), loader.getImageHeight(i), loader.getImage(i), palette);
    }

    public boolean nextImage() {
	if (_currentIndex < maxIndex.get()) {
	    _currentIndex++;
	    currentIndex.set(_currentIndex);
	    return true;
	}
	return false;
    }

    public boolean prevImage() {
	if (_currentIndex > 0) {
	    _currentIndex--;
	    currentIndex.set(_currentIndex);
	    return true;
	}
	return false;
    }

    public StringProperty getStiName() {
	return stiName;
    }

    public StringProperty getStiStatus() {
	return stiStatus;
    }

    public IntegerProperty getCurrentIndex() {
	return currentIndex;
    }

    public IntegerProperty getMaxIndex() {
	return maxIndex;
    }

    public byte[][] getPalette() {
	return palette;
    }

}
