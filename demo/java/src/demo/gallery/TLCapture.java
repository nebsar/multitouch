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

package demo.gallery;

import static java.util.Collections.unmodifiableList;
import java.util.ArrayList;
import java.util.Iterator;


import de.telekom.laboratories.multitouch.util.Labels;
import java.util.List;

/**
 * @author Michael Nischt
 * @version 0.1
 */
class TLCapture
{    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    final static private int THRESHOLD_INTENSITY = 15;
    final static private int THRESHOLD_SIZE      = 5;
    
    final private Scene scene;
    final private int width, height;
    
    final private byte[] diff;
    
    final private int[][] image;
    final private Labels labels;
    
    private boolean start = false;
    private int index;
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">
    
    public TLCapture (Scene scene, int width, int height)
    {
        if( scene == null ) throw new NullPointerException ();
        if(width <= 0 || height <= 0)
        {
            throw new IllegalArgumentException ();
        }
        this.scene = scene;
        this.width = width;
        this.height = height;
        
        diff = new byte[width*height];
        image = new int[width][height];
        labels = new Labels ( image );
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    public void capture (byte[] data)
    {
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
                data[ndx] = (byte) Math.max ( 0, (0xFF & data[ndx]) - (0xFF & diff[ndx]) );
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
                    row[x] = (int) (value * 255.0f / (THRESHOLD_INTENSITY+(0xFF & diff[ndx]))) ; //value;
                    //sum ++;
                } else {
                    row[x] = 0;
                }
            }
        }        
        
        final List<Touch> touchList = new ArrayList<Touch>();
        
        //sum = 0;
        //int index = 0;
        final int[][] bounds = labels.bounds();
        for(int[] b : bounds) {
            int width  = (b[2]-b[0]);
            int height = (b[3]-b[1]);                        
            if(width > THRESHOLD_SIZE && height > THRESHOLD_SIZE) {                            
                //System.out.printf("%d %d %d %d\n", b[0], b[1], b[2], b[3]);                
                //sum++;
                
                final double x = 2.0 * ( (width  / 2.0 + b[0] ) / this.width  ) - 1.0;
                final double y = 2.0 * ( (height / 2.0 + b[1] ) / this.height ) - 1.0;
                final Touch touch = new Touch( x, y );
                //System.out.println( touch.getX() + " " + touch.getY() );
                touchList.add( touch );
            }
        }        
        
        //if(!touchList.isEmpty())
        //    System.out.println(touchList.size());
        
        final Scene.Input input = new Scene.Input ()
        {
            public Iterator<Touch> getTouches ()
            {
                return unmodifiableList ( touchList ).iterator ();
            }
            
        };
        scene.control (input);
    }
    
    // </editor-fold>
}
