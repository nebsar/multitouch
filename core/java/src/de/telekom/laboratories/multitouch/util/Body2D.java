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
public class Body2D extends Particle2D {
    
    private double orientation;
    private double inertia;
    
    
    public Body2D() {
        this(0.0);
    }

    public Body2D(double orientation) {
        this(0.0, 0.0);
    }
    
    public Body2D(double orientation, double inertia) {
        setOrientation( orientation );
        setInertia( inertia );
    }    

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }

    public double getInertia() {
        return inertia;
    }

    public void setInertia(double inertia) {
        this.inertia = inertia;
    }
    
    public void from(Moments2D moments) 
    throws NullPointerException, IllegalArgumentException {
        if(moments.getDegrees() < 2) {
            throw new IllegalArgumentException();
        }
        
        super.from(moments);
        
        final double x = getX(), y = getY();
        
        // Teh centered 2nd moments build the intertia (tensor)
        
        // special case for: moments.from(s, -centerX, -centerY)
        final double uXX = moments.getMoment(2,0) - x*moments.getMoment(1, 0); 
        final double uXY = moments.getMoment(1,1) - x*moments.getMoment(0, 1);
        final double uYY = moments.getMoment(0,2) - y*moments.getMoment(0, 1);
        
        // final double[][] inertiaTensor = 
        // {
        //   {  uXX, -uXY }, 
        //   { -uXY, -uYY }, 
        // };
        
        inertia = uXX + uYY; // == uZZ with z = (x^2 + y^2)^(1/2)
        
        // The orientation of an object is defined as the axis of minimum inertia. 
        // This is the axis of the least 2nd moment, the orientation of which is                
        final double uXY2 = 2*uXY;
        final double uXXsubUYY = uXX-uYY;
        
        orientation = Math.atan2(uXY2, uXXsubUYY + Math.sqrt( uXXsubUYY*uXXsubUYY + uXY2*uXY2 ) );
    }
    
}
