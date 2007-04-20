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

import java.util.Arrays;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class Moments2D {
    
    private double zero;
    private final double[][] moments;
    
    public Moments2D() {
        this(0);
    }
    
    public Moments2D(int degree) {
        //moments = new double[degree+1][];
        //for(int i=0; i<moments.length; i++) {
        //    moments[i] = new double[i];
        //}
        moments = new double[degree][];
        for(int i=0; i<moments.length;) {
            moments[i] = new double[++i];
        }
    }
    
    public int getDegree() {
        //return moments.length-1;
        return moments.length;
    }    

    public void clear() {
        //final double zero = 0.0;
        zero = 0.0;
        for(double[] moment : moments) {
            Arrays.fill(moment, zero);
        }
    }
    
    public double at(int x, int y) 
    throws IllegalArgumentException {
        if(x < 0 || y < 0) {
            throw new IllegalArgumentException();
        }
        final int degree = x+y;
        if(degree > getDegree()) {
            throw new IllegalArgumentException();
        }                
        //return moments[degree][y] : zero;
        return (degree == 0) ? moments[degree-1][y] : zero;
    }            
    
    public void from(Shape2D shape) {
                
        shape.points(new Shape2D.Points() {
            public void point(double x, double y) { 
                point(x,y,1.0);
            }
            public void point(double x, double y, double intensity) { 
                //moments[0][0] += intensity;
                zero += intensity;
                for(int i=1; i<moments.length; i++) {
                    final double[] ithMoments = moments[i];
                    
                    
                }
            }
        });
    }
    
    public static double from(Shape2D shape, int x, int y) {
        final double moment = 0.0;
        
        return moment;
    }
    
}
