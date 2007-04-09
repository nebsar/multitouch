/*
 * Copyright (C) 2007 Deutsche Telekom AG Laboratories
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.telekom.laboratories.multitouch.util;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
class ViewPort {

    public ViewPort() {
    }


    private static Object fromUnit(double x, double y, double width, double height) {
        // unit = [-1,1]
        // window = [x/y,width/hieght]

        // window_x = unit_x * width  + x;
        // window_y = unit_y * height + y;
        final double[] matrix =
        {
            width,      0.0,      x,
              0.0,   height,      y,
              0.0,      0.0,    1.0,
        };
        return matrix;
    }

    private static Object toUnit(double x, double y, double width, double height) {
        // unit = [-1,1]
        // window = [x/y,width/hieght]

        // unit_x = (window_x - x) / width
        // unit_y = (window_y - y) / height

        final double[] matrix =
        {
            1.0/width,   0.0,   -x/width,
            0.0,    1.0/height, -y/height,
            0.0,      0.0,      1.0,
        };
        return matrix;
    }
}
