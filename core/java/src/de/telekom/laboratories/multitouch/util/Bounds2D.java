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

import static de.telekom.laboratories.multitouch.util.Shape2D.Transform.translate;
import static de.telekom.laboratories.multitouch.util.Shape2D.Transform.rotate;

/**
 @author Michael Nischt
 @version 0.1
 */
public class Bounds2D 
{    
    private double minX, minY;
    private double maxX, maxY;
    
    public Bounds2D() {
        this(0.0, 0.0);
    }
    
    public Bounds2D(double x, double y) {
        minX = maxX = x;        
        minY = maxY = y;
    }
    
    
    /**
     * Returns the minimal x-coordinate.
     * @return the minimal x-coordinate.
     */
    public double getMinX() {
        return minX;
    }
    
    /**
     * Returns the minimal y-coordinate.
     * @return the minimal y-coordinate.
     */
    public double getMinY() {
        return minY;
    }

    /**
     * Returns the maximal x-coordinate.
     * @return the maximal x-coordinate.
     */
    public double getMaxX() {
        return maxX;
    }
    /**
     * Returns the maximal y-coordinate.
     * @return the maximal y-coordinate.
     */
    public double getMaxY() {
        return maxY;
    }
    
    
    public void union(double x, double y)
    {
        if(x < minX) { minX = x; }
        if(x > maxX) { maxX = x; }
        
        if(y < minY) { minY = y; }
        if(y > maxY) { maxY = y; }
    }
//    {
//        minX = Math.min(minX, x);
//        minY = Math.min(minY, y);
//
//        maxX = Math.max(maxX, x);
//        maxY = Math.max(maxY, y);        
//    }
    
    public void from(Shape2D shape) 
    throws NullPointerException
    {
        if(shape == null) {
            throw new NullPointerException();
        }
        
        minX = minY = Double.MAX_VALUE;
        maxX = maxY = Double.MIN_VALUE;
        
        shape.points(new Shape2D.Points()
        {
            @Override            
            public void point(double x, double y) {
                union(x,y);
            }
            @Override            
            public void point(double x, double y, double intensity) {
                union(x,y);
            }
        });        
    }

//    public void from(Shape2D shape, double orientation) 
//    throws NullPointerException
//    {        
//        shape = rotate(-orientation).shape(shape);
//        from(shape);   
//    }    
//
//    public void from(Shape2D shape, double x, double y) 
//    throws NullPointerException
//    {        
//        shape = translate(-x,-y).shape(shape);
//        from(shape);   
//    }    
//    
//    
//    public void from(Shape2D shape, double x, double y, double orientation) 
//    throws NullPointerException
//    {        
//        shape = concat( translate(-x,-y), rotate(-orientation) ).shape(shape);
//        from(shape);   
//    }    

    // <editor-fold defaultstate="collapsed" desc=" Translated ">
    
//    private static abstract class Translated {
//        public abstract Transform rotate(final double radians);
//    }
//    
//    private static Translated translate(final double x, final double y) {
//        return new Translated()
//        {
//            public Transform rotate(final double radians) 
//            {                                
//                return new Transform() {
//
//                    private final Transform translate = Transform.translate(x, y);
//                    private final Transform rotate    = Transform.rotate(radians);                    
//                    
//                    protected double[] point(double... point) {
//                        return rotate.point( translate.point ( point) );
//                    }
//                };
//            }            
//        };
//    }
    
    // </editor-fold>    
    
}
