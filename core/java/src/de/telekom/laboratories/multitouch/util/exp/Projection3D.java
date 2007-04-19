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

package de.telekom.laboratories.multitouch.util.exp;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
enum Projection3D {    

    // <editor-fold defaultstate="collapsed" desc=" ORTHOGONAL ">

    ORTHOGONAL
    {
        @Override
        public Object matrix(double left, double right, double bottom, double top, double near, double far)
        throws IllegalArgumentException
        {
            final double 
                        width  = right - left,
                        height = top - bottom,
                        depth  = far - near;

            if( width <= 0.0 || height <= 0.0 || depth <= 0.0)
            {
                throw new IllegalArgumentException();
            }

            final double[] matrix = 
            {   
                (2.0 / width),              0.0,                0.0,    -(right+left)/width,
                0.0,            ( 2.0 / height),                0.0,   -(top+bottom)/height,
                0.0,                        0.0,     ( 2.0 / depth),      -(far+near)/depth,
                0.0,                        0.0,                0.0,                    1.0,
            };
            return matrix;
        }

        @Override
        public Object matrix(double width, double height, double near, double far)
        throws IllegalArgumentException
        {
            final double 
                        depth  = far - near;

            if( width <= 0.0 || height <= 0.0 || depth <= 0.0)
            {
                throw new IllegalArgumentException();
            }

            final double[] matrix = 
            {   
                (2.0 / width),              0.0,                0.0,                    0.0,
                0.0,            ( 2.0 / height),                0.0,                    0.0,
                0.0,                        0.0,     ( 2.0 / depth),      -(far+near)/depth,
                0.0,                        0.0,                0.0,                    1.0,
            };
            return matrix;
        }

        @Override
        public Object matrix(double width, double height, double depth)
        throws IllegalArgumentException
        {
            if( width <= 0.0 || height <= 0.0 || depth <= 0.0)
            {
                throw new IllegalArgumentException();
            }
            final double[] matrix = 
            {   
                (2.0 / width),              0.0,                0.0,                    0.0,
                0.0,            ( 2.0 / height),                0.0,                    0.0,
                0.0,                        0.0,     ( 2.0 / depth),                    0.0,
                0.0,                        0.0,                0.0,                    1.0,
            };
            return matrix;
        }

    },

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" PERSPECTIVE ">

    PERSPECTIVE
    {
        @Override
        public Object matrix(double left, double right, double bottom, double top, double near, double far)
        throws IllegalArgumentException
        {
            if( near <= 0.0)
            {
                throw new IllegalArgumentException();
            }
            
            final double 
                        width  = right - left,
                        height = top - bottom,
                        depth  = far - near;

            if( width <= 0.0 || height <= 0.0 || depth <= 0.0)
            {
                throw new IllegalArgumentException();
            }


            final double[] matrix = 
            {   
                (2.0 / width),              0.0,   -(right+left)/width,                 0.0,
                0.0,            ( 2.0 / height),   -(top+bottom)/height,                0.0,
                0.0,                        0.0,   -(far+near)/depth,   -2.0*far*near/depth,
                0.0,                        0.0,                0.0,                    1.0,
            };
            return matrix;
        }

        @Override
        public Object matrix(double width, double height, double near, double far)
        throws IllegalArgumentException
        {
            if( near <= 0.0)
            {
                throw new IllegalArgumentException();
            }

            final double 
                        depth  = far - near;

            if( width <= 0.0 || height <= 0.0 || depth <= 0.0)
            {
                throw new IllegalArgumentException();
            }

            final double[] matrix = 
            {   
                (2.0 / width),              0.0,                   0.0,                 0.0,
                0.0,            ( 2.0 / height),                   0.0,                 0.0,
                0.0,                        0.0,     -(far+near)/depth,   -2.0*far*near/depth,
                0.0,                        0.0,                   0.0,                    1.0,
            };
            return matrix;
        }
        @Override
        public Object matrix(double width, double height, double depth)
        throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }
    };

    // </editor-fold>

    public abstract Object matrix(double left, double right, double bottom, double top, double near, double far) throws IllegalArgumentException;

    public Object matrix(double width, double height, double near, double far)  throws IllegalArgumentException, UnsupportedOperationException
    {
        final double
                     extX = (0.5*width), 
                     extY = (0.5*height);

        return matrix(-extX, +extX, -extY, +extY, near, far);
    }
    public Object matrix(double width, double height, double depth) throws IllegalArgumentException, UnsupportedOperationException
    {
        final double
                     extX = (0.5*width), 
                     extY = (0.5*height),
                     extZ = (0.5*depth);

        return matrix(-extX, +extX, -extY, +extY, -extZ, +extZ);
    }

    // dominant axis 
    private static Object ratio(double width, double height)
    throws IllegalArgumentException
    {
        if(width <= 0.0 || height <= 0.0)
        {
            throw new IllegalArgumentException();
        }

        if(width >= height)
        {
            return new double[]
            {
                (width / height),  
                 1.0,
            };
        }
        else
        {
            return new double[]
            {
                1.0,
                (height / width),
            };            
        }
    }


}
