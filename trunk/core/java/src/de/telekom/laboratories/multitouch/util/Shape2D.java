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
 An Intefface representing a 2D point shape, 
 which can optionally have a non-uniform density/intensity distribution.
 @author Michael Nischt
 @version 0.1
 */
public interface Shape2D 
{    
    // <editor-fold defaultstate="collapsed" desc=" Points ">
    
    /**
     * Instances will be notified for each point during an interation/integration of a {@link Shape2D}.
     */
    public static abstract class Points {
        /**
         * Is called for a shape point with a density/intesity of <code>1.0</code>. 
         * @param x the x-coordinate of the shape-point
         * @param y the y-coordinate of the shape-point
         */
        public void point(double x, double y)
        {
            point( x, y, 1.0 );
        }
        /**
         * Is called for a shape point with a specific density/intesity.
         * @param x the x-coordinate of the shape-point
         * @param y the y-coordinate of the shape-point
         * @param intesity the density at the shape-point
         */
        public abstract void point(double x, double y, double intesity);                
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Transform ">
    
    /**
     * A class to transform the individual points of a shape.
     */
    public static abstract class Transform 
    {
        /**
         * erived classes must override this method, which will be called for all shape points.
         * @param point the point to be transformed
         * @return transformed point <code>{x,y}</code>
         */
        protected abstract double[] point(double... point);
        
        
        /**
         * Returns a new shape, which points always equal those of the argument, but transformed.
         * @param shape the reference shape
         * @return the transformed reference shape.
         */
        public Shape2D shape(Shape2D shape) {
            return new Transformed(shape);
        }
        
        /**
         * Creates a new Transform which applies the individual ones sequentially
         * @param transforms the transformes to be applied
         * @return new Transform which applies the individual ones sequentially
         */
        public static Transform concat(final Transform... transforms) {
            return new Sequence(transforms);
        }
        
        /**
         * Creates a new transformation, which describes a translation.
         * @param x the amount of tanslation along the x-axis
         * @param y the amount of tanslation along the y-axis
         * @return a new transformation, which describes a translation.
         */
        public static Transform translate(final double x, final double y) {
            return new Translation(x, y);
        }
        
        /**
         * Creates a new transformation, which describes a rotation in counter-clockwise direction.
         * @param radians the rotation angle in radians
         * @return a new transformation, which describes a rotation
         */
        public static Transform rotate(final double radians) {
            return new Rotation(radians);
        }        
        
        // <editor-fold defaultstate="collapsed" desc=" Transformed ">

        private class Transformed implements Shape2D {

            // <editor-fold defaultstate="collapsed" desc=" Attributes ">

            private final Shape2D shape;

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc=" Initializers ">

            public Transformed(Shape2D shape)
                    throws NullPointerException {
                if(shape == null) {
                    throw new NullPointerException();
                }
                this.shape = shape;
            }

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc=" Methods ">
            
            public void points(Shape2D.Points points) 
            {
                if(points == null) {
                    throw new NullPointerException();
                }
                final Shape2D.Points original = points;           
                shape.points(new Points()
                {
                    private final double[] point = new double[2];

                    @Override
                    public void point(double x, double y) {                    
                        point[0] = x; point[1] = y;
                        point(x,y);
                        original.point(point[0], point[1]);
                    }

                    @Override
                    public void point(double x, double y, double intesity) {
                        point[0] = x; point[1] = y;
                        point(x,y);
                        original.point(point[0], point[1], intesity);
                    }
                });
        }                
            // </editor-fold>    
        }

         // </editor-fold>
        
        
        // <editor-fold defaultstate="collapsed" desc=" Sequence ">
        
        private static final class Sequence extends Shape2D.Transform {

            // <editor-fold defaultstate="collapsed" desc=" Attributes ">

            private final Shape2D.Transform[] transforms;

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc=" Initializers ">

            public Sequence(final Shape2D.Transform[] transforms)
            {
                for(Shape2D.Transform t : transforms) {
                    if(t == null) {
                        throw new NullPointerException();
                    }                    
                }
                this.transforms = transforms.clone();                
            }

            // </editor-fold>        

            // <editor-fold defaultstate="collapsed" desc=" Methods ">

            @Override
            protected double[] point(double... point) 
            {
                for(Transform t : transforms) {
                    point = t.point( point );
                }
                return point;
            }       

            // </editor-fold>    
        }
                
        // </editor-fold>                

        // <editor-fold defaultstate="collapsed" desc=" Translation ">
        
        private static final class Translation extends Shape2D.Transform {

            // <editor-fold defaultstate="collapsed" desc=" Attributes ">

            private final static int X = 0, Y = 1;
            private final double x, y;

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc=" Initializers ">

            public Translation(final double x, final double y)
            {
                this.x = x;
                this.y = y;
            }

            // </editor-fold>        

            // <editor-fold defaultstate="collapsed" desc=" Methods ">

            @Override
            protected double[] point(double... point) 
            {
                point[X] += x;
                point[Y] += y;
                return point;
            }       

            // </editor-fold>    
        }
        
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Rotation ">
        
        private static final class Rotation extends Shape2D.Transform {

            // <editor-fold defaultstate="collapsed" desc=" Attributes ">

            private final static int X = 0, Y = 1;
            private final double cos, sin;

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc=" Initializers ">

            public Rotation(final double radians)
            {
                this.cos = Math.cos( radians );
                this.sin = Math.sin( radians );
            }

            // </editor-fold>        

            // <editor-fold defaultstate="collapsed" desc=" Methods ">

            @Override
            protected double[] point(double... point) 
            {
                final double x = point[X];
                final double y = point[Y];
                point[X] =  cos*x + sin*y;
                point[Y] = -sin*x + cos*y;
                return point;
            }       

            // </editor-fold>    
        }
                
        // </editor-fold>        
        
    }
    
    // </editor-fold>
    
    /**
     * Interates through all points of this shape.
     * @param points will be informed about each point and optionally its density/intesity.
     */
    void points(Points points);
}
