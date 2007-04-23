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
    
    // <editor-fold defaultstate="collapsed" desc=" Attributes ">
    
    private final double[][] moments;
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Initializers ">    
    
    /**
     * 
     */
    public Moments2D() {
        this(0);
    }
    
    /**
     * 
     * @param degree 
     */
    public Moments2D(int degree) {
        moments = new double[degree+1][];
        for(int i=0; i<moments.length; ) {
            moments[i] = new double[++i];
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Properties ">    
    
    /**
     * 
     * @return 
     */
    public int getDegree() {
        return moments.length-1;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">    
    
    /**
     * 
     * @param x 
     * @param y 
     * @return 
     * @throws java.lang.IllegalArgumentException 
     */
    public double getMoment(int x, int y)
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
    
    
    /**
     * 
     * @param shape 
     */
    public void from(Shape2D shape) {
        from(shape, false);
    }
    
    /**
     * 
     * @param shape 
     * @param normalize 
     */
    public void from(Shape2D shape, boolean normalize) {
        clear();
        shape.points(new Shape2D.Points() {
            
            private final double[] tmp = new double[getDegree()+1];
            
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
        
        
        if(!normalize) {
            return;
        }
        
        final double sum = moments[0][0];
        for(int degree=0; degree<moments.length; degree++) {
            final double pow = (degree+2) / 2;
            final double factor = 1.0 / Math.pow(sum, pow);
            
            final double[] ithMoments = moments[degree];
            for(int i=0; i<ithMoments.length; i++) {
                ithMoments[i] *= factor;
            }
        }
    }
    
    
    /**
     * 
     * @param shape 
     * @param x 
     * @param y 
     */
    public void from(Shape2D shape, double x, double y) {
        from(new TranslatedShape2D(shape, -x, -y));
    }
    
    /**
     * 
     * @param shape 
     * @param x 
     * @param y 
     * @param normalize 
     */
    public void from(Shape2D shape, double x, double y, boolean normalize) {
        from(new TranslatedShape2D(shape, -x, -y), normalize);
    }    
    
    /**
     * 
     * @param shape 
     * @param degree 
     * @return 
     */
    public static double[] from(Shape2D shape, int degree) {
        final double[] moments = new double[degree+1];
        from(shape, degree, moments, 0);
        return moments;
    }
    
    /**
     * 
     * @param shape 
     * @param degree 
     * @param moments 
     * @param offset 
     */
    public static void from(Shape2D shape, int degree, final double[] moments, final int offset) {
        
        Arrays.fill(moments, 0.0);
        
        final double[] tmp = new double[degree+1];
        
        shape.points(new Shape2D.Points() {
            
            public void point(double x, double y) {
                point(x,y,1.0);
            }
            public void point(double x, double y, double intensity) {
                
                for(int j=1; j<tmp.length; j++) {
                    tmp[j] = 0.0;
                }
                tmp[0] = intensity;
                for(int j=1; j<tmp.length; j++) {
                    for (int i=tmp.length-1; i>0; i--) {
                        tmp[i] = (tmp[i-1] * y);
                    }
                    tmp[0] *= x;
                }
                
                for(int i=0; i<tmp.length; i++) {
                    moments[i+offset] += tmp[i];
                }
            }
        });
    }
    
    /**
     * 
     * @param shape 
     * @param x 
     * @param y 
     * @return 
     */
    public static double from(Shape2D shape, int x, int y) {
        
        class Moment implements Shape2D.Points {
            private final int mX, mY;
            private double value;
            
            Moment(final int mX, final int mY) {
                if(mX < 0 || mY < 0) {
                    throw new IllegalArgumentException();
                }
                this.mX = mX;
                this.mY = mY;
            }
            
            public void point(double x, double y) {
                point(x,y,1.0);
            }
            public void point(double x, double y, double intensity) {
                
                double tmp = intensity;
                for(int i=0; i<mX; i++) {
                    tmp *= x;
                }
                for(int i=0; i<mY; i++) {
                    tmp *= y;
                }
                value += tmp;
            }
        }
        final double moment = 0.0;
        
        final Moment m = new Moment(x,y);
        shape.points(m);
        return m.value;
    }
    
    private void clear() {
        final double zero = 0.0;
        for(double[] moment : moments) {
            Arrays.fill(moment, zero);
        }
    }    
    
    @Deprecated() // use from(TranslatedShape2D(shape, -x, -y)) instead
    private void relative(Shape2D shape, double x, double y) {
        clear();
        final double cX = x, cY = y;
        
        shape.points(new Shape2D.Points() {
            
            private final double[] tmp = new double[getDegree()+1];
            
            public void point(double x, double y) {
                point(x,y,1.0);
            }
            public void point(double x, double y, double intensity) {
                x -= cX; y -= cY;
                
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
    
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc=" TranslatedShape2D ">
    
    private static class TranslatedShape2D implements Shape2D {
        
        // <editor-fold defaultstate="collapsed" desc=" Attributes ">
        
        private final Shape2D original;
        private final double offX, offY;
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Initializers ">
        
        public TranslatedShape2D(Shape2D original, double offX, double offY)
                throws NullPointerException {
            if(original == null) {
                throw new NullPointerException();
            }
            this.original = original;
            this.offX = offX;
            this.offY = offY;
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Properties ">
        
        public double getMinX() {
            return original.getMinX() + offX;
        }
        
        public double getMinY() {
            return original.getMinY() + offY;
        }
        
        public double getMaxX() {
            return original.getMaxX() + offX;
        }
        
        public double getMaxY() {
            return original.getMaxY() + offY;
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Methods ">
        
        public void points(Shape2D.Points points) {
            class TranslatedPoints implements Shape2D.Points {
                
                private final Shape2D.Points original;
                
                public TranslatedPoints(Shape2D.Points original)
                        throws NullPointerException {
                    if(original == null) {
                        throw new NullPointerException();
                    }
                    this.original = original;
                }
                
                public void point(double x, double y) {
                    original.point(x+offX, y+offY);
                }
                public void point(double x, double y, double intensity) {
                    original.point(x+offX, y+offY, intensity);
                }
            }
            
            original.points(new TranslatedPoints(points));
        }
        
        // </editor-fold>    
    }
    
    // </editor-fold>    
        
    
    // <editor-fold defaultstate="collapsed" desc=" main(..) ">
    
//    public static void main(String... args) throws Exception {
//        Shape2D s = new Shape2D() {
//            public void points(Shape2D.Points points) {
//                points.point(3,2, 4);
//                points.point(4,2, 1);
//                points.point(5,2, 1);
//                points.point(3,3, 2);
//                points.point(4,3, 5);
//                points.point(5,3, 9);
//            }
//            
//            public double getMinX() { return 3; }
//            public double getMinY() { return 2; }
//            
//            public double getMaxX() { return 5; }
//            public double getMaxY() { return 3; }
//        };
//        
//        Moments2D m = new Moments2D(3);
//        m.from(s, false);
//        System.out.printf("S: %f\n", m.at(0,0) );
//        System.out.printf("Sx: %f Sy: %f\n", m.at(1,0), m.at(0,1) );
//        System.out.printf("Sxx: %f Syy %f Sxy %f\n", m.at(2,0), m.at(0,2), m.at(1,1) );
//        System.out.printf("Sxxx: %f Sxxy %f Sxyy %f Syyy %f\n", m.at(3,0), m.at(2,1), m.at(1,2), m.at(0,3) );
//        System.out.println();
//        
//        final double cX = m.at(1, 0) / m.at(0, 0);
//        final double cY = m.at(0, 1) / m.at(0, 0);
//        
//        System.out.printf("Cx: %f Cy: %f\n", cX, cY );
//        System.out.println();
//        
//        //s = new TranslatedShape2D(s, -cX, -cY);
//     
//        //m.from(s, false);
//        m.from(s, cX, cY, false);     
//      
//        System.out.printf("U: %f\n", m.at(0,0) );
//        System.out.printf("Ux: %f Uy: %f\n", m.at(1,0), m.at(0,1) );
//        System.out.printf("Uxx: %f Uyy %f Uxy %f\n", m.at(2,0), m.at(0,2), m.at(1,1) );
//        System.out.printf("Uxxx: %f Uxxy %f Uxyy %f Uyyy %f\n", m.at(3,0), m.at(2,1), m.at(1,2), m.at(0,3) );
//        System.out.println();
//        
//        //m.from(s, true);
//        m.from(s, cX, cY, true);
//     
//        System.out.printf("N: %f\n", m.at(0,0) );
//        System.out.printf("Nx: %f Ny: %f\n", m.at(1,0), m.at(0,1) );
//        System.out.printf("Nxx: %f Nyy %f Nxy %f\n", m.at(2,0), m.at(0,2), m.at(1,1) );
//        System.out.printf("Nxxx: %f Nxxy %f Nxyy %f Nyyy %f\n", m.at(3,0), m.at(2,1), m.at(1,2), m.at(0,3) );
//        System.out.println();              
//    }
    
    // </editor-fold>
    
}




