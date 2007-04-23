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

package de.telekom.laboratories.multitouch;

/**
 An Intefface representing a 2D point shape, 
 which can optionally have a non-uniform density/intensity distribution.
 @author Michael Nischt
 @version 0.1
 */
public interface Shape2D {
    
    /**
     * Interface to be notified for each point during an interation/integration of a {@link Shape2D}.
     */
    public interface Points {
        /**
         * Is called for a shape point with a density/intesity of <code>1.0</code>.
         * @param x the x-coordinate of the shape-point
         * @param y the y-coordinate of the shape-point
         */
        void point(double x, double y);
        /**
         * Is called for a shape point with a specific density/intesity.
         * @param x the x-coordinate of the shape-point
         * @param y the y-coordinate of the shape-point
         * @param intesity the density at the shape-point
         */
        void point(double x, double y, double intesity);                
    }

    /**
     * Returns the minimal x-coordinate of this shape.
     * @return the minimal x-coordinate of this shape.
     */
    double getMinX();
    
    /**
     * Returns the minimal y-coordinate of this shape.
     * @return the minimal y-coordinate of this shape.
     */
    double getMinY();

    /**
     * Returns the maximal x-coordinate of this shape.
     * @return the maximal x-coordinate of this shape.
     */
    double getMaxX();
    /**
     * Returns the maximal y-coordinate of this shape.
     * @return the maximal y-coordinate of this shape.
     */
    double getMaxY();

    /**
     * Interates through all points of this shape.
     * @param points will be informed about each point and optionally its density/intesity.
     */
    void points(Points points);

}
