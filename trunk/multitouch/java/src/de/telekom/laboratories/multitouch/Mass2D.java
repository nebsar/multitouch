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
class Mass2D
{

    private double mass;
    private double distX; // = 0;
    private double distY; // = 0;

    // mass := integral densitye dVolume
    // see also: 
    // http://scienceworld.wolfram.com/physics/Mass.html
    public void integrate(double x, double y, double density)
    {
        mass  += density;
        distX += density*x;
        distY += density*y;
    }            

    public void integrate(double x, double y) 
    {
        mass  ++;
        distX += x;
        distY += y;
    }            


    public double getMass()
    {
        return mass;
    }

    public double getMassCenterX()
    {
        return mass / distX;
    }

    public double getMassCenterY()
    {
        return mass / distY;
    } 

}
