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
    
    private final double[][] moments;
    
    public Moments2D() {
        this(0);
    }
    
    public Moments2D(int degree) {
        moments = new double[degree+1][];
        for(int i=0; i<moments.length; ) {
            moments[i] = new double[++i];
        }
    }
    
    public int getDegree() {
        return moments.length-1;
    }    

    public void clear() {
        final double zero = 0.0;
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
        return moments[degree][y];
    } 
    
    
    public void from(Shape2D shape) {
                
        shape.points(new Shape2D.Points() {

           final double[] tmp = new double[getDegree()+1];            
            
            public void point(double x, double y) { 
                point(x,y,1.0);
            }
            public void point(double x, double y, double intensity) {                                 
                
                for(int j=1; j<tmp.length; j++) {
                    tmp[j] = 0.0;
                }                
                moments[0][0] += (tmp[0] = intensity);
                for(int j=1; j<moments.length; j++) {
                    final double[] ithMoments = moments[j];
                    for (int i=ithMoments.length-1; i>0; i--) {
                        ithMoments[i] += tmp[i] = (tmp[i-1] * y);
                    }                        
                    ithMoments[0] += (tmp[0] *= x);
                }
            }
        });
    }
    
    public static double from(Shape2D shape, int x, int y) {
        
        final double moment = 0.0;
        
        
        return moment;
    }
    
    public static void main(String... args) throws Exception {
        Shape2D s = new Shape2D() {
            public void points(Shape2D.Points points) {
                points.point(3,2, 4);
                points.point(4,2, 1);
                points.point(5,2, 1);
                points.point(3,3, 2);
                points.point(4,3, 5);
                points.point(5,3, 9);
            }

            public double getMinX() { return 3; }
            public double getMinY() { return 2; }

            public double getMaxX() { return 5; }
            public double getMaxY() { return 3; }            
        };
        
        Moments2D m = new Moments2D(3);
        m.from(s);
        System.out.printf("S: %f\n", m.at(0,0) );
        System.out.printf("Sx: %f Sy: %f\n", m.at(1,0), m.at(0,1) );
        System.out.printf("Sxx: %f Syy %f Sxy %f\n", m.at(2,0), m.at(0,2), m.at(1,1) );
        System.out.printf("Sxxx: %f Sxxy %f Sxyy %f Syyy %f\n", m.at(3,0), m.at(2,1), m.at(1,2), m.at(0,3) );

        System.out.println();
        
        
    }    
    
}
