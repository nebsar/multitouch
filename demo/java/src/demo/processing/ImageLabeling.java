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

package demo.processing;

import static java.lang.String.format;
import de.telekom.laboratories.multitouch.util.Labels;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class ImageLabeling 
{
    public interface Collect
    {
        boolean collect(Touch t);
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    final static private int THRESHOLD_INTENSITY = 20;
    final static private int THRESHOLD_SIZE      = 5;
    
    final private int width, height;
    
    final private byte[] diff;
    
    final private int[][] image;
    final private Labels labels;
    
    private boolean start = false;
    private int index;
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">
    
    public ImageLabeling (int width, int height)
    {
        if(width <= 0 || height <= 0)
        {
            throw new IllegalArgumentException ();
        }
        this.width = width;
        this.height = height;
        
        diff = new byte[width*height];
        image = new int[height][width];
        labels = new Labels ( image );
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    public void capture (byte[] data, Collect collect)
    {        
        if(collect == null) throw new NullPointerException();        
        if(data.length != (width * height))
            throw new IllegalArgumentException(format("Image bytes must match width(=%d) * height(=%d) = %d, but is %d!", width, height, width*height, data.length));
        
        if(!start) // x*30 = x sec with 30fps
        {        
            if(++index < 30*1)//5)
            {              
                //System.out.println(index);
                return;
            } else {
                start = true;                
                System.arraycopy(data, 0, diff, 0, diff.length);
                System.out.println(start);
            }
        }        
        
        for(int y=0; y<height; y++)
        {
            final int off = y*width;
            for(int x=0; x<width; x++)
            {
                final int ndx =  off+x;
                data[ndx] = (byte) Math.max ( 0, (0xFFFFFFFF & data[ndx]) - (0xFFFFFFFF & diff[ndx]) );
            }
        }
        
        
        //int sum = 0;
        for(int y=0; y<height; y++) {
            final int[] row = image[y];
            final int off = y*width;
            for(int x=0; x<width; x++) {
                final int ndx =  off+x;
                final int value = (0xff & data[ndx]);
                if(value > THRESHOLD_INTENSITY) {
                    row[x] = (int) (value * 255.0f / (255 - (THRESHOLD_INTENSITY+(0xFF & diff[ndx])))) ; //value;
                    //sum ++;
                } else {
                    row[x] = 0;
                }
            }
        }        
        //System.out.println(sum);
                
        float xScale = 1.0f;
        float yScale = 1.0f;
        
        if(width >= height) xScale = width / (float) height;
        else yScale = height / (float) width;
        
        //sum = 0;
        //int index = 0;
        final int[][] bounds = labels.bounds();
        for(int[] b : bounds) {
            int width  = (b[2]-b[0]+1);
            int height = (b[3]-b[1]+1);                        
            if(width > THRESHOLD_SIZE && height > THRESHOLD_SIZE) {                            
                //System.out.printf("%d %d %d %d\n", b[0], b[1], b[2], b[3]);                
                //sum++;
                
                final float x = 2.0f * ( (width  / 2.0f + b[0] ) / this.width  ) - 1.0f;
                final float y = 2.0f * ( (height / 2.0f + b[1] ) / this.height ) - 1.0f;
                final Touch touch = new Touch( x*xScale, y*yScale );
                //System.out.println( touch.getX() + " " + touch.getY() );
                if(!collect.collect( touch )) return;
            } else {
                ;//System.out.println(width + " x " + height);
            }
        }
    }
    
    // </editor-fold>
}
