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
 * A class which can compute all (derived) moments of a two-dimensional shape up to a specified degree upon the following formulas:<br/>
 * <br/>
 * <b>Standard:</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <code>M<sub>m,n</sub> = &sum; &sum; x<sup>m</sup> * y<sup>n</sup> * I(x,y)</code>
 * <br/><br/>
 * <b>Relative to P(x,y):</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <code>U<sub>m,n</sub> = &sum; &sum; (x-x<sub>p</sub>)<sup>m</sup> * (y-y<sub>p</sub>)<sup>n</sup> * I(x,y)</code>
 * <br/><br/>
 * <b>Normalized:</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <code>N<sub>m,n</sub> = (M<sub>m,n</sub> or U<sub>m,n</sub>) / (&sum; &sum; I(x,y))<sup>(m+n+2)/2</sup></code>
 * <br/><br/>
 * Note: Besides <i>scaling invariance</i> through normalization, <i>translation invariance</i> can be acchieved by calculating the moments relative to the <code>Pivot( M<sub>1,0</sub> / M<sub>0,0</sub>, M<sub>,1</sub> / M<sub>0,0</sub> )</code>.
 * @author Michael Nischt
 * @version 0.1
 */
public class Moments2D implements Cloneable
{
    // <editor-fold defaultstate="collapsed" desc=" Attributes ">
    
    private final double[][] moments;
    
    private final double[] tmp;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">
    
    /**
     * Creates a new instance which only consistis of the 0th-moment.
     */
    public Moments2D ()
    {
        this (0);
    }
    
    /**
     * Creates a new instance for all moments up to the specified degree.
     * @param degree the maximal degree of the moments
     */
    public Moments2D (int degree)
    {
        degree++;
        moments = new double[degree][];
        for(int i=0; i<moments.length; )
        {
            moments[i] = new double[++i];
        }
        tmp = new double[degree];
    }
    
    private Moments2D (Moments2D other)
    {
        moments = other.moments.clone();
        for(int i=0; i<moments.length; i++)
        {
            moments[i] = other.moments[i].clone();
        }
        tmp = new double[moments.length];
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Properties ">
    
    /**
     * Returns the highest degree of all the moments.
     * @return the highest degree of all the moments.
     */
    public int getDegrees ()
    {
        return moments.length-1;
    }
    
    /**
     * Returns the moment with the specified derivation degrees for the two directions (x and y).<br/>
     * Note that: <code>total-degree(x,y) == x + y</code>
     * @param x the derivation degree for the x-direction
     * @param y the derivation degree for the y-direction
     * @return the moment with the specified derivation degrees for the two directions (x and y).
     * @throws java.lang.IllegalArgumentException if <code>x</code> or <code>y</code> is smaller than <code>0</code>
     * or <code>x + y</code> is greater than the maximal {@link Moments2D#getDegrees degree}.
     */
    public double getMoment (int x, int y)
            throws IllegalArgumentException
    {
        if(x < 0 || y < 0)
        {
            throw new IllegalArgumentException ();
        }
        final int degree = x+y;
        if(degree > getDegrees ())
        {
            throw new IllegalArgumentException ();
        }
        return moments[degree][y];
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    public void clear ()
    {
        final double zero = 0.0;
        for(double[] moment : moments)
        {
            Arrays.fill (moment, zero);
        }
    }    
    
    
    public void point (double x, double y)
    {
        point ( x, y, 1.0f );
    }
    
    public void point (double x, double y, double intensity)
    {
        for(int j=1; j<tmp.length; j++)
        {
            tmp[j] = 0.0;
        }
        moments[0][0] += (tmp[0] = intensity);
        for(int j=1; j<moments.length; j++)
        {
            final double[] ithMoments = moments[j];
            for (int i=ithMoments.length-1; i>0; i--)
            {
                ithMoments[i] += tmp[i] = (tmp[i-1] * y);
            }
            ithMoments[0] += (tmp[0] *= x);
        }
    }
    
    public void normalize ()
    {
        final double sum = moments[0][0];
        for(int degree=0; degree<moments.length; degree++)
        {
            final double pow = (degree+2) / 2;
            final double factor = 1.0 / Math.pow (sum, pow);
            
            final double[] ithMoments = moments[degree];
            for(int i=0; i<ithMoments.length; i++)
            {
                ithMoments[i] *= factor;
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Cloneable ">
    
    @Override
    public Moments2D clone()
    {
        return new Moments2D(this);
    }
    
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc=" Old Utility Meothods ">    
    
//    /**
//     * Calculates all moments of the specified degree for a given shape.
//     * @param shape the shape for which the moments will be calculated
//     * @param degree the exact degree of the moments
//     * @throws java.lang.IllegalArgumentException if <code>degree</code> is less than <code>0</code>
//     * @throws java.lang.NullPointerException if <code>shape</code> is <code>null</code>
//     * @return all moments of the specified degree for a given shape
//     */
//    public static double[] from (Shape2D shape, int degree)
//            throws IllegalArgumentException
//    {
//        final double[] moments = new double[degree+1];
//        from (shape, degree, moments, 0);
//        return moments;
//    }
    
//    /**
//     * Calculates all moments of the specified degree for a given shape.
//     * @param shape the shape for which the moments will be calculated
//     * @param degree the exact degree of the moments
//     * @param moments the destiation to write the moments to
//     * @param offset the start-index for the destiation array
//     * @throws java.lang.IllegalArgumentException if <code>degree</code> or <code>offset</code> are less than <code>0</code>
//     * or moments has no enough remaining space.
//     * @throws java.lang.NullPointerException if <code>moments</code> is <code>null</code>
//     */
//    private static void from (Shape2D shape, int degree, final double[] moments, final int offset)
//            throws IllegalArgumentException, NullPointerException
//    {
//        
//        if(degree < 0 || offset < 0 || moments.length < (offset+degree))
//        {
//            throw new IllegalArgumentException ();
//        }
//        
//        final double[] tmp = new double[degree+1];
//        
//        shape.points (new Shape2D.Points ()
//        {
//            
//            @Override
//            public void point (double x, double y, double intensity)
//            {
//                
//                for(int j=1; j<tmp.length; j++)
//                {
//                    tmp[j] = 0.0;
//                }
//                tmp[0] = intensity;
//                for(int j=1; j<tmp.length; j++)
//                {
//                    for (int i=tmp.length-1; i>0; i--)
//                    {
//                        tmp[i] = (tmp[i-1] * y);
//                    }
//                    tmp[0] *= x;
//                }
//                
//                for(int i=0; i<tmp.length; i++)
//                {
//                    moments[i+offset] += tmp[i];
//                }
//            }
//        });
//    }
    
//    /**
//     * Calculates the moment with the specified derivation degrees for the two directions (x and y).
//     * @param shape the shape for which the moment is calculated
//     * @param x the derivation degree for the x-direction
//     * @param y the derivation degree for the y-direction
//     * @return the moment with the specified derivation degrees for the two directions (x and y).
//     * @throws java.lang.IllegalArgumentException if <code>x</code> or <code>y</code> is smaller than <code>0</code>
//     * @throws java.lang.NullPointerException if <code>moments</code> is <code>null</code>
//     */
//    public static double from (Shape2D shape, int x, int y)
//            throws IllegalArgumentException, NullPointerException
//    {
//        
//        class Moment extends Shape2D.Points
//        {
//            private final int mX, mY;
//            private double value;
//            
//            Moment (final int mX, final int mY)
//            {
//                if(mX < 0 || mY < 0)
//                {
//                    throw new IllegalArgumentException ();
//                }
//                this.mX = mX;
//                this.mY = mY;
//            }
//            
//            @Override
//            public void point (double x, double y, double intensity)
//            {
//                
//                double tmp = intensity;
//                for(int i=0; i<mX; i++)
//                {
//                    tmp *= x;
//                }
//                for(int i=0; i<mY; i++)
//                {
//                    tmp *= y;
//                }
//                value += tmp;
//            }
//        }
//        final double moment = 0.0;
//        
//        final Moment m = new Moment (x,y);
//        shape.points (m);
//        return m.value;
//    }

    // </editor-fold>
}


