/*
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.monoid.util;

/**
 * @author Michael Nischt
 * @version 0.1
 */
public interface Keys {

    int getCount();             // number of keys
    double getKeyTime(int index);  // key-index
    
    double getStart();  // starttime
    double getEnd();    // endtime
    
    double getDuration(); // end-start, for convinience ;-)
            
    boolean getMarginalKeys(double time, int[] keys); // true if matches a single key
    double  getInterpolationFactor(double time, int[] keys); // marginal keys: if keys[0] == keys[1], the returned factor may be any between 0.0 and 1.0

}
