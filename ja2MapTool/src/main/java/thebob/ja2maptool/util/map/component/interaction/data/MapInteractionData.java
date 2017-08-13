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
package thebob.ja2maptool.util.map.component.interaction.data;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Data attached to interaction events, sent back to the component that
 * registered with the map interaction component
 *
 * @author starcatter
 */
public class MapInteractionData {

    MapInteractionUserdata userdata = null;
    MouseEvent event = null;
    private final int mouseCellX;
    private final int mouseCellY;
    private final int mouseCell;
    private final boolean shiftDown;
    private final boolean controlDown;
    private final boolean altDown;
    private final MouseButton button;

    public MapInteractionData(int mouseCellX, int mouseCellY, int mouseCell, boolean shiftDown, boolean controlDown, boolean altDown, MouseButton button) {
        this.mouseCellX = mouseCellX;
        this.mouseCellY = mouseCellY;
        this.mouseCell = mouseCell;
        this.shiftDown = shiftDown;
        this.controlDown = controlDown;
        this.altDown = altDown;
        this.button = button;
    }

    public MapInteractionData(int mouseCellX, int mouseCellY, int mouseCell, MouseEvent event) {
        this.mouseCellX = mouseCellX;
        this.mouseCellY = mouseCellY;
        this.mouseCell = mouseCell;
        this.shiftDown = event.isShiftDown();
        this.controlDown = event.isControlDown();
        this.altDown = event.isAltDown();
        this.button = event.getButton();
        this.event = event;
    }

    public MapInteractionUserdata getUserdata() {
        return userdata;
    }

    public void setUserdata(MapInteractionUserdata userdata) {
        this.userdata = userdata;
    }

    public int getMouseCellX() {
        return mouseCellX;
    }

    public int getMouseCellY() {
        return mouseCellY;
    }

    public int getMouseCell() {
        return mouseCell;
    }

    public boolean isShiftDown() {
        return shiftDown;
    }

    public boolean isControlDown() {
        return controlDown;
    }

    public boolean isAltDown() {
        return altDown;
    }

    public MouseEvent getEvent() {
        return event;
    }

    public MouseButton getButton() {
        return button;
    }
        
}
