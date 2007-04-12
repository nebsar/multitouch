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
public class Normalization {
    
    static class Float1D {
        private final int steps;
        
        private Float1D(final int length) throws IllegalArgumentException {
            if(length <= 0) {
                throw new IllegalArgumentException();
            }
            this.steps = (length-1);
        }
       
        public float getStepSize() {
            return 1.0f / steps;
        }
        
        public int getStepCount() {
            return steps;
        }          
        
        // normalize ( denormalize( n, factor ) ) + factor * stepSize = id (n) = n 
        public int denormalize(float normalized, float[] outFactor, int index) throws NullPointerException, ArrayIndexOutOfBoundsException {
            final int denorm = denormalize(normalized);
            outFactor[index] = ( normalized - normalize( denorm ) ) * steps;
            return denorm;
        }
        public int denormalize(float normalized) {
            int denormalized =  (int) Math.floor( normalized * steps );
            //clamp
            denormalized = Math.max(0, Math.min(denormalized, steps) );
             //repeat
            denormalized %= (steps+1);
            
            return denormalized;
        }
        public float normalize(int denormalized) {
            //clamp
            denormalized = Math.max(0, Math.min(denormalized, steps) );
            //repeat
            denormalized %= (steps+1);
            
            return denormalized / (float) (steps);
        }
    } 
    
    static class Float1D2 {
        private final int offset, steps;
        
        private Float1D2(final int start, final int end) throws IllegalArgumentException {
            this.offset = start;
            this.steps = (end-start)-1;
            if(steps <= 0) {
                throw new IllegalArgumentException();
            }
            
        }
        
        public float getStepSize() {
            return 1.0f / steps;
        }
        
        public int getStepCount() {
            return steps;
        }        
        
        // normalize ( denormalize( n, factor ) ) + factor * stepSize = id (n) = n 
        public int denormalize(float normalized, float[] outFactor, int index) throws NullPointerException, ArrayIndexOutOfBoundsException {
            final int denorm = denormalize(normalized);
            outFactor[index] = ( normalized - normalize( denorm ) ) * steps;
            return denorm;
        }        
        
        public int denormalize(float normalized) {
            int denormalized =  (int) Math.floor( (normalized * steps) );
            //clamp
            denormalized = Math.max(0, Math.min(denormalized, steps) );
             //repeat
            denormalized %= (steps+1);
            
            return denormalized + offset;
        }
        
        public float normalize(int denormalized) {
            denormalized -= offset;
            //clamp
            denormalized = Math.max(0, Math.min(denormalized, steps) );
            //repeat
            denormalized = (denormalized) % (steps+1);
            
            return ( denormalized / (float) steps );
        }
    }   
    

    
    public static void main(String... args) {
        
        final float[] norms = { 0.0f, 0.5f, 1.0f };
        final int[] denorms = { 4, 8 };
        
        Float1D2 f = new Float1D2(4, 8);
        //Float1D2 f = new Float1D2(0, 4);
        //Float1D f = new Float1D(4);

        final float[] factor = new float[1];
        for(int i=0; i<3; i++) {
            final float norm = norms[i];
            final int denorm  = f.denormalize(norm, factor, 0);
            System.out.printf("Norm: %f -> DeNorm: %d (Factor: %f) -> Norm: %f\n", norm, denorm, factor[0], f.normalize(denorm) + factor[0]*f.getStepSize());            
        }
        
        
        
    }
    
}
