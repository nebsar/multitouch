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

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class Labels {
    
    // <editor-fold defaultstate="collapsed" desc=" Attributes ">
    
    private final int[][] image;    
    private final int[] labels;
    
    private int current;
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Initializers ">
    
    public Labels(int[][] image) {
        if(image.length < 1) {
            throw new IllegalArgumentException();
        }
        final int width = image[0].length;
        for(int i=1; i<image.length; i++) {
            if(image[i].length != width) {
                throw new IllegalArgumentException();
            }
        }
        this.image = image.clone();
        this.labels = new int[ (width*image.length) / 2 ]; // max possible unique labels
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Properties ">
    
    public int getCount() {
        return current;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    public int update() {
        current = 0;
        
        int[] line = image[0];
        
        // first pixel
        int pixel = line[0] = (line[0] != 0) ? apply() : 0;
        
        // firstLine
        for(int i=1; i<line.length; i++) {
            pixel = line[i] = (line[i] != 0) ? apply(pixel) : 0;
        }
        
        for(int j=1; j<image.length; j++) {            
            final int[] line2 = image[j];
            
            // first pixel
            pixel = line2[0] = (line2[0] != 0) ? apply(line[0]) : 0;

            // firstLine
            for(int i=1; i<line.length; i++) {
                pixel = line2[i] = (line2[i] != 0) ? apply(line[i], pixel) : 0;
            }
                        
            line = line2;
        }
        
        int count = 0;
        for(int i=1; i<=current; i++) {
            if(i != labels[i]) {
                labels[i] = labels[ labels[i] ];
            } else {
                count++;
                labels[i] = count;                
            }
        }
        current = count;
        
        for(int j=0; j<image.length; j++) {
            line = image[j];
            for(int i=0; i<line.length; i++) {
                line[i] = labels[line[i]];
            }
        }
        return count;        
    }    
    
    private int apply() {
        ++current;        
        return (labels[current] = current);
    }
    private int apply(int a) {
        return (a != 0) ? labels[a] : apply();
    }
    private int apply(int a, int  b) {
        assert(a <= b);
        
        if(a != 0 && b != 0) {
            return apply( union(a,b) );
        } else if( a != 0) {            
            return apply(a);
        } else if(b != 0) {
            return apply(b);
        } else {
            return apply();
        }
    }
    
    private int union(int a, int b) {
        if(a < b) {                 
            return (labels[b] = a);
        } else {//a > b)
            return (labels[a] = b);
        }     
    }
    
    // </editor-fold>
}
