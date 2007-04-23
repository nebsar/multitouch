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
public class Particle2D {
    
    private double x, y;
    private double mass;    
    
    public Particle2D() {
        this(0.0, 0.0);
    }

    public Particle2D(double x, double y) {
        this(x,y, 1.0);
    }
    
    public Particle2D(double x, double y, double mass) {
        setMass( mass );
        setX( x );
        setY( y );
    }
    
    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = Math.max(0.0, mass);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    public void from(Moments2D moments) 
    throws NullPointerException, IllegalArgumentException {
        if(moments.getDegrees() < 1) {
            throw new IllegalArgumentException();
        }
        
        // Mass is given by the 0th moment
        mass = moments.getMoment(0,0);
        
        // Centre of mass is given by the 1st moments        
        x = moments.getMoment(1, 0) / mass;
        y = moments.getMoment(0, 1) / mass;        
    }
    
}
