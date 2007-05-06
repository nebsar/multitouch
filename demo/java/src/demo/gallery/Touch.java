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

package demo.gallery;


/**
 * @author Michael Nischt
 * @version 0.1
 */
final public class Touch
{
    private final double x, y;
    
    public Touch (double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    public double getX ()
    { return x; }
    public double getY ()
    { return y; }
    
    
    static public class Utils
    {
        public static double distanceSquared (Touch from, Touch to)
        {
            final double x = to.x-from.x;
            final double y = to.y-from.y;
            return x*x + y*y;
        }
        
        public static double distance (Touch from, Touch to)
        {
            return Math.sqrt ( distanceSquared ( from, to ) );
        }
    }
    
}
