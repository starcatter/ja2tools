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
package thebob.ja2maptool.util.renderer;

import javafx.scene.effect.Effect;

/**
 *
 * @author the_bob
 */
public class OverlaySettings {

    double opacity;
    double offsetX;
    double offsetY;
    Effect effect;

    public OverlaySettings(double opacity, double offsetX, double offsetY, Effect effect) {
	this.opacity = opacity;
	this.offsetX = offsetX;
	this.offsetY = offsetY;
	this.effect = effect;
    }

    public double getOpacity() {
	return opacity;
    }

    public double getOffsetX() {
	return offsetX;
    }

    public double getOffsetY() {
	return offsetY;
    }

    public Effect getEffect() {
	return effect;
    }

}
