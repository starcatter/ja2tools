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
package thebob.ja2maptool.util.map.controller.viewer.base;

import thebob.ja2maptool.util.map.controller.base.IMapController;

/**
 *
 * @author the_bob
 */
public interface IMapViewerController extends IMapController{
    /*
     *
     * moves the view window by specified offsets and redraws the screen. Call with 0,0 to force screen refresh. note that the parameters are offsets to move the window by, not coordinates to move the window to. *
     */
    void moveWindow(int x, int y);
    
    /**
     * Sets the renderer scale and updated the view
     * @param scale 
     */
    void setScale(double scale);
    /**
     * Gets the current rendering scale
     * @return 
     */
    double getScale();

    /*
     *
     * use this to move the window to specific coordinates, followed by moveWindow(0,0) *
     */
    public void setWindowOffsetX(int oldX);

    /*
     *
     * use this to move the window to specific coordinates, followed by moveWindow(0,0) *
     */
    void setWindowOffsetY(int oldY);

    int getWindowOffsetY();

    int getWindowOffsetX();
}
