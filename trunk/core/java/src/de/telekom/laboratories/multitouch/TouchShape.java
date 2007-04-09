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

package de.telekom.laboratories.multitouch;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public interface TouchShape {
    
    void begin(int[] bounds);
    void end();    
    // sequenceTypes:
    // (top-botton (left-right) )
    // (top-botton (right-left) )
    // (botton-top (left-right) )
    // (botton-top (right-left) )
        
    

    //void iterate(int x, int y, double pressure);
    
    // only changes are called !!
    // vs.
    // changes or more are called ??
    void x(int y);
    void y(int x);
    // also for pressure (could be nice for constant shapes)
    void pressure(double pressure);    
}
