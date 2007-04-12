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

package de.telekom.laboratories.multitouch;

// |  0  |  1  |  2  |  3  |
//    |        |        |
//   0.0      0.5      1.0


/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public abstract class Normalization {    
    
    public abstract int getOffset();
    public abstract int getStepCount();
    
    public double getStepSize() {
        return 1.0 / getStepCount();
    }
    
    public abstract double normalize(int value);
    
    public abstract int inverse(double norm);
    public abstract int inverse(double norm, double[] factor, int index)
    throws NullPointerException, ArrayIndexOutOfBoundsException;

    public static Normalization unsafe(int range) 
    throws IllegalArgumentException {
        return new To(range);
    }

    public static Normalization wrapped(int range)
    throws IllegalArgumentException {
        return new ToWrapped(range);        
    }
    
    public static Normalization repeated(int range)
    throws IllegalArgumentException {
        return new ToRepeated(range);
    }
    
    public static Normalization unsafe(int from, int to) 
    throws IllegalArgumentException {
        return new FromTo(from, to);
    }

    public static Normalization wrapped(int from, int to)
    throws IllegalArgumentException {
        return new FromToWrapped(from, to);        
    }
    
    public static Normalization repeated(int from, int to)
    throws IllegalArgumentException {
        return new FromToRepeated(from, to);
    }    
    
    
    public static double normalize(int steps, int value) {
        return value / (double) (steps);
    }

    public static int inverse(int steps, double norm) {
        return (int) Math.floor( norm * steps );
    }
    
    
    static class To extends Normalization {        
        
        protected final int steps;
        
        To(final int length) throws IllegalArgumentException {
            if(length <= 0) {
                throw new IllegalArgumentException();
            }
            this.steps = (length-1);
        }       
        
        @Override
        public int getOffset() {
            return 0;
        }         
        
        @Override
        public final int getStepCount() {
            return steps;
        }        

        @Override
        public double normalize(int value) {
            return normalize(steps, clamp( value ) );
        }

        @Override
        public int inverse(double norm) {
            return clamp ( inverse( steps, norm ) );
        }        
        
        protected int clamp(int value) {
            return value;
        }

        @Override
        public int inverse(double norm, double[] factor, int index) {
            final int invNorm = inverse( norm );
            factor[index] = ( norm - normalize(steps, invNorm) ) * steps;
            return invNorm;        
        }       
    } 
    


    static class ToWrapped extends To {

        ToWrapped(final int count) { super(count); }

        public double normalize(int value) {
            value = Math.max(0, Math.min(value, steps) );
            return super.normalize( value );

        }

        @Override
        public int clamp(int value) {
            return Math.max(0, Math.min( value, steps) );
        }                                    
    }

    static class ToRepeated extends To {

        ToRepeated(final int count) { super(count); }

        @Override
        public int clamp(int value) {
            return value % (steps+1);            
        }     
    }    
    
    static class FromTo extends To {

        protected final int offset;

        FromTo(final int start, final int end) throws IllegalArgumentException {
            super( (end-start) );
            this.offset = start;
        }       

        @Override
        public int getOffset() {
            return offset;
        }         

        @Override
        public double normalize(int value) {
            return super.normalize( value - offset );
        }

        @Override
        public int inverse(double norm) {
            return super.inverse( norm ) + offset;
        }
        
        @Override
        public int inverse(double norm, double[] factor, int index) {
            final int invNorm = super.inverse(norm, factor, index);
            factor[index] += offset;
            return invNorm;
        }       
        
    }    
    
        
    static class FromToWrapped extends FromTo {

        FromToWrapped(final int start, final int end) { super(start, end); }

        @Override
        public int clamp(int value) {
            return Math.max(0, Math.min( value, steps) );
        }                                     
    }

    static class FromToRepeated extends FromTo {

        FromToRepeated(final int start, final int end) { super(start, end); }

        @Override
        public int clamp(int value) {
            return value % (steps+1);            
        }       
    }
    
}
