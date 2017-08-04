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
package thebob.ja2maptool.util.renderer.base;

import java.util.List;
import javafx.scene.canvas.Canvas;

/**
 * interface for basic renderer controls like moving the view window, changing scale and forcing updates
 * @author the_bob
 */
public interface ITileRendererControls {

    /*
     *
     * moves the view window by specified offsets and redraws the screen. Call with 0,0 to force screen refresh. note that the parameters are offsets to move the window by, not coordinates to move the window to. *
     */
    void moveWindow(int x, int y);

    /*
     *
     * sets the canvas to draw on, will automatically detect its size and adjust *
     */
    void setCanvas(Canvas canvas);

    double getScale();

    void setScale(double scale);

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
