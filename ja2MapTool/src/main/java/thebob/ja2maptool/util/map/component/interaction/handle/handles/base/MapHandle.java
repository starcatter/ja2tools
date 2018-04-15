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
package thebob.ja2maptool.util.map.component.interaction.handle.handles.base;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import thebob.ja2maptool.util.compositor.TileSnippet;

/**
 *
 * @author starcatter
 */
public class MapHandle {

	public enum HandleStateType {
		None,
		Enabled, // handle can be manipulated directly
		Disabled, // handle can not be manipulated directly
		Focused,	// handle is focused/selected 
	};

	public enum HandleStateMode {
		None,
		Normal, // normal display state
		Hovered, // mouse over handle
		Active		// mouse button down/dragging
	};

	// show/hide the handle. Will also disable active areas and events.
	boolean visible = false;

	// event handlers
	Map<EventType<? extends MouseEvent>, Function<MouseEvent, MouseEvent>> events = new HashMap<>();

	// handle tiles and state
	HandleStateMode currentStateMode = HandleStateMode.None;
	HandleStateType currentStateType = HandleStateType.None;
	MapHandleState currentState = null;
	TileSnippet currentStateTiles = null;

	// handle dimensions
	int width = 1;
	int height = 1;
	int size = 1;

	// handle position
	int cellX;
	int cellY;
	int cell;

	// margins for active area
	int marginX;
	int marginY;

	EnumMap<HandleStateType, MapHandleState> allowedStates = new EnumMap<HandleStateType, MapHandleState>(HandleStateType.class);

	// ----------------------------------
	public MouseEvent fireEvent(MouseEvent e) {
		if (events.containsKey(e.getEventType())) {
			return events.get(e.getEventType()).apply(e);
		}

		return e;
	}

	public void setEvent(EventType<? extends MouseEvent> e, Function<MouseEvent, MouseEvent> f) {
		events.put(e, f);
	}

	public void clearEvent(EventType<MouseEvent> e) {
		events.remove(e);
	}

	// ----------------------------------
	public HandleStateType getState() {
		return currentStateType;
	}

	public void addState(HandleStateType currentStateType, MapHandleState state) {
		allowedStates.put(currentStateType, state);
	}

	public void setState(HandleStateType currentStateType) {
		this.currentStateType = currentStateType;
		currentState = allowedStates.get(currentStateType);

		if (currentState != null && currentStateMode != HandleStateMode.None) {
			setMode(currentStateMode);
		}
	}

	// --------------------------------
	public void setMode(HandleStateMode mode) {
		if (currentStateType == HandleStateType.None) {
			return;
		}

		currentStateTiles = currentState.getState(currentStateMode);
		if (currentStateTiles != null) {
			width = currentStateTiles.getWidth();
			height = currentStateTiles.getHeight();
			size = height * width;
		}
	}

	// ----------------------------------
	public int getCellX() {
		return cellX;
	}

	public void setCellX(int cellX) {
		this.cellX = cellX;
	}

	public int getCellY() {
		return cellY;
	}

	public void setCellY(int cellY) {
		this.cellY = cellY;
	}

	public int getCell() {
		return cell;
	}

	public void setCell(int cell) {
		this.cell = cell;
	}

	public int getMarginX() {
		return marginX;
	}

	public void setMarginX(int marginX) {
		this.marginX = marginX;
	}

	public int getMarginY() {
		return marginY;
	}

	public void setMarginY(int marginY) {
		this.marginY = marginY;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
