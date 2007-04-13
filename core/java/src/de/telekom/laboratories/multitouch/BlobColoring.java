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

    // <editor-fold defaultstate="collapsed" desc=" Ideas ">
    
//    public class InputControl {
//        
//        private int x, y;
//        
//        private boolean[] lastLine    = scanLines[0];
//        private boolean[] currentLine = scanLines[1];
//
//        public void nextValues(boolean[] values, int offset, int count) { // throws BoundsException
//            System.arraycopy(values, offset, currentLine, x, count);
//            x += count;
//        }         
//        
//        public void nextValues(boolean... values) { // throws BoundsException
//            System.arraycopy(values, 0, currentLine, x, values.length);
//            x += values.length;
//        }        
//        
//        public void nextValue(boolean value) { // throws BoundsException
//            currentLine[x++] = value;
//        }
//        
//        public void nextValue() {
//            x++;            
//        }
//        
//        public void nextLine() {               // throws BoundsException
//            final int width = getWidth(), height = getHeight();
//            
//            while(x++ < width) nextValue();
//            
//            final int offset = y*width;
//            
//            if(y == 0) {
//                
//                //int left = // blobImage[offset+0];
//                for(x=1; x<width; x++) {
//                    // blobImage[offset+x] = currentLine[x];
//                }
//                
//                    
//                x = 0;
//            } else {
//                // update
//            }
//            
//            
//            
//            
//            x=0;
//            y++;
//            y %= height;
//                
//            final boolean[] tmp = lastLine;
//            lastLine = currentLine;
//            currentLine = tmp;
//        }
//    }    
//    
//    //Input: Scanlines, 2 form a step, there are (height-1) steps, with each length equals to (width)
//    public interface ScanLine {
//        void copy(boolean[] target);
//    }
//    
//
//    public interface Blobs {
//        
//    }
//    
//    public interface Image {
//        boolean get(int x, int y);
//    }
//    
//    public interface Labels {
//        int label();
//        
//        int x();
//        int y();
//        
//        void set(int x, int y, int label);
//    }    
//    
//    private final int width, height;
//    private final boolean[][] scanLines;
//    
//    private final boolean[] blobs;    
//    
//    public BlobColoring(int width, int height) throws IllegalArgumentException {
//        if(width < 1 || height < 1) {
//            throw new IllegalArgumentException();
//        }
//        this.width  = width;
//        this.height = height;
//        
//        blobs = new boolean[width * height];
//        
//        scanLines = new boolean[2][width];
//    }
//    
//    public int getWidth() {
//        return width;
//    }
//    
//    public int getHeight() {
//        return height;
//    }
//    
//    void begin(ScanLine scanline) {
//        
//    }
//    
//    public void update(Image state) { // readAndDispatch();
//        final int width = this.width, height = this.height;
//        
//        for(int y=0; y<height; y++) {
//            final int offset = y*width;
//            for(int x=0; x<width; x++) {
//                blobs[offset+x] = state.get(x,y);
//            }
//        }
//        
//        foo();
//    }    
//    
//    public static interface Bounds {
//        
//        void union(int x, int y);
//        
//        public static class AxisAlignedBox implements Bounds {
//        
//            private int minX;
//            private int minY;
//
//            private int maxX;
//            private int maxY;
//
//            public AxisAlignedBox(int x, int y) {
//                this.minX = this.maxX = x;
//                this.minY = this.maxY = y;                
//            }
//
//            public int getMinX() {
//                return minX;
//            }
//
//            public int getMinY() {
//                return minY;
//            }
//
//            public int getMaxX() {
//                return maxX;
//            }
//
//            public int getMaxY() {
//                return maxY;
//            }        
//
//            public void union(int x, int y) {
//                this.minX = Math.min(this.minX, x);
//                this.minY = Math.min(this.minY, y);
//
//                this.maxX = Math.max(this.maxX, x);
//                this.maxY = Math.max(this.maxY, y);            
//            }
//
//        }
//    }    
    
    // </editor-fold>
    
//    private void foo() {
//        
//        final class ReplaceColor {
//            ReplaceColor(final int[] lookup, final int from, final int to) {
//                for(int i=0; i<lookup.length; i++) {
//                    if(lookup[i] == from) {
//                        lookup[i] = to;
//                    }
//                }
//            }
//        }           
//        
//        int color = 1;
//        int[] colorLookup = new int[8]; 
//        
//        final int width = this.width, height = this.height;
//        final int[] blobImage = new int[width*height];
//        
//        for(int h=1; h<height-1; h++) {
//            final int offset = h*width;
//            for(int w=1; w<width-1; w++) {
//                final int current = offset+w;
//                
//                if(!blobs[current]) {
//                    continue;
//                }
//                
//                final int upper = current-offset;  // upper pixel
//                final int left = current-1;       // left pixel                                
//                
//                final boolean p_u = blobs[upper];    // upper pixel
//                final boolean p_l = blobs[left];     // left pixel                
//                
//                //TODO: optimize if statements                
//                
//                if( (p_u) && (!p_l) ) {
//                    blobImage[current] = blobImage[upper]; // set current to UPPER pixel
//                }
//                if( (p_l) && (!p_u) ) {
//                    blobImage[current] = blobImage[left]; // set current to LEFT pixel
//                }
//                if( (!p_l) && (!p_u) ) {
//                     
//                     blobImage[current] = color;
//                     // increase color lookup, if necessary
//                     if(color >= colorLookup.length) {
//                         int[] incrLookup = new int[colorLookup.length*2];
//                         System.arraycopy(colorLookup, 0, incrLookup, 0, colorLookup.length);
//                         colorLookup = incrLookup;
//                     }
//                     colorLookup[color] = color+1;
//                     color++;
//                }                 
//                if( (p_l) && (p_u) ) {    
//                    
//                    final int col_l = blobImage[left];
//                    final int col_u = blobImage[upper];
//                    
//                    final int ref_l = colorLookup[col_l];
//                    final int ref_u = colorLookup[col_u];
//                    
//                    // bottleneck, makes it O(width*height*k) where k == the number of id's to be merged
//                    if(ref_l >= ref_u) {
//                        blobImage[current] = col_l;                        
//                        new ReplaceColor(colorLookup, ref_u, ref_l);
//                    } else {
//                         blobImage[current] = col_u;
//                         new ReplaceColor(colorLookup, ref_l, ref_u);
//                    }
//                }               
//                
//               //progress = ( (h*width) + (i+1) ) / (width*height); 
//            }
//        }        
//                
//        final int colors = color;
//        color = 0;
//        for(int c=0; c<colors; c++) {
//            if(colorLookup[c] == (c+1)) {
//                color++;                
//                new ReplaceColor(colorLookup, (c+1), color);              
//            }
//        }
//  
//        // run over image and assign right blob color value
//        for(int h=1; h<height-1; h++) {
//            final int offset = h*width;
//            for(int w=1; w<width-1; w++) {
//                final int current = offset+w;
//                final int blob = blobImage[current];
//                // Is the pixel part of a blob 
//                if(blob > 0) {
//                    // assign right label/id
//                    blobImage[current] = colorLookup[blob];
//                }
//            }
//        }        
//    }
    

    
    public static void main(String... args) {
        
        int a = 1, b=2, color=0;
        color += (a != b) ? 1 : 0;
        
        
//        boolean b = true;
//        int val = (b == true) ? 1 : 0;
        //val = Math.abs( a - b ) / 
        //System.out.println( val /  );
        
//        final int width = 8, height = 8;
//        final float[] image = new float[width*height];
//        
//        final ScanLine l = new ScanLine()
//        {    
//            private int index = 0;
//            
//            public void copy(boolean[] target)
//            {
//                // check argument & state
//                if(target.length < width) {
//                    throw new IllegalArgumentException();
//                } else if(index >= height) {
//                    throw new IllegalStateException();
//                }
//                
//                { // copy to target
//                    final int offset = width*index;
//                    for(int i=0; i<width; i++) {
//                        final float intensity = image[offset+i];
//                        target[i] = (intensity > 0.5f);
//                    }
//                }
//                
//                // update state
//                index++;
//            }
//        };
//        
//        final BlobColoring blobColoring = new BlobColoring( width, height );        
//        
//        for(int y=0; y<height; y++) {
//                        
//            blobColoring.begin(l);
//            
//        }
//        

        
        
        
        
        
    }
    
}
