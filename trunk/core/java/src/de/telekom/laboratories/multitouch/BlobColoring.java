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
public class BlobColoring {
    
    public interface Blobs {
        boolean at(int x, int y);
    }
    
    private final int width, height;
    private final boolean[] blobs;
    
    
    public BlobColoring(int width, int height) throws IllegalArgumentException {
        if(width < 1 || height < 1) {
            throw new IllegalArgumentException();
        }
        this.width  = width;
        this.height = height;
        
        blobs = new boolean[width * height];
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    
    public void update(Blobs state) { // readAndDispatch();
        final int width = this.width, height = this.height;
        
        for(int y=0; y<height; y++) {
            final int offset = y*width;
            for(int x=0; x<width; x++) {
                blobs[offset+x] = state.at(x,y);
            }
        }
    }    
    
    
    private void foo() {
        
        final class ReplaceColor {
            ReplaceColor(final int[] lookup, final int from, final int to) {
                for(int i=0; i<lookup.length; i++) {
                    if(lookup[i] == from) {
                        lookup[i] = to;
                    }
                }
            }
        }           
        
        int color = 1;
        int[] colorLookup = new int[8]; 
        
        final int width = this.width, height = this.height;
        final int[] blobImage = new int[width*height];
        
        for(int h=1; h<height-1; h++) {
            final int offset = h*width;
            for(int w=1; w<width-1; w++) {
                final int current = offset+w;
                
                if(!blobs[current]) {
                    continue;
                }
                
                final int upper = current-offset;  // upper pixel
                final int left = current-1;       // left pixel                                
                
                final boolean p_u = blobs[upper];    // upper pixel
                final boolean p_l = blobs[left];     // left pixel                
                
                //TODO: optimize if statements                
                
                if( (p_u) && (!p_l) ) {
                    blobImage[current] = blobImage[upper]; // set current to UPPER pixel
                }
                if( (p_l) && (!p_u) ) {
                    blobImage[current] = blobImage[left]; // set current to LEFT pixel
                }
                if( (!p_l) && (!p_u) ) {
                     
                     blobImage[current] = color;
                     // increase color lookup, if necessary
                     if(color >= colorLookup.length) {
                         int[] incrLookup = new int[colorLookup.length*2];
                         System.arraycopy(colorLookup, 0, incrLookup, 0, colorLookup.length);
                         colorLookup = incrLookup;
                     }
                     colorLookup[color] = color+1;
                     color++;
                }                 
                if( (p_l) && (p_u) ) {    
                    
                    final int col_l = blobImage[left];
                    final int col_u = blobImage[upper];
                    
                    final int ref_l = colorLookup[col_l];
                    final int ref_u = colorLookup[col_u];
                    
                    // bottleneck, makes it O(width*height*k) where k == the number of id's to be merged
                    if(ref_l >= ref_u) {
                        blobImage[current] = col_l;                        
                        new ReplaceColor(colorLookup, ref_u, ref_l);
                    } else {
                         blobImage[current] = col_u;
                         new ReplaceColor(colorLookup, ref_l, ref_u);
                    }
                }               
                
               //progress = ( (h*width) + (i+1) ) / (width*height); 
            }
        }        
                
        final int colors = color;
        color = 0;
        for(int c=0; c<colors; c++) {
            if(colorLookup[c] == (c+1)) {
                color++;                
                new ReplaceColor(colorLookup, (c+1), color);              
            }
        }
  
        // run over image and assign right blob color value
        for(int h=1; h<height-1; h++) {
            final int offset = h*width;
            for(int w=1; w<width-1; w++) {
                final int current = offset+w;
                final int blob = blobImage[current];
                // Is the pixel part of a blob 
                if(blob > 0) {
                    // assign right label/id
                    blobImage[current] = colorLookup[blob];
                }
            }
        }        
    }
    
}
