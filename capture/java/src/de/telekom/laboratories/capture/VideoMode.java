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

package de.telekom.laboratories.capture;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class VideoMode {
    
    public enum Format
    {
        LUMINACE_8
        {
            public int size() { return 1; }
        },
        LUMINACE_16
        {
            public int size() { return 2; }
        },
        RED_8_GREEN_8_BLUE_8
        {
            public int size() { return 2; }
        };        
        
        public abstract int size();
    }
    
    //private int x;
    //private int y;
    private int width;
    private int height;
    private Format format;
    private float frameRate;
                
    public VideoMode()
    {
        this(640, 480, Format.LUMINACE_8, 15.0f);
    }    

    public VideoMode(int width, int height, Format format, float frameRate) 
    throws NullPointerException, IllegalArgumentException
    {
        this.setWidth(width);
        this.setHeight(height);
        this.setFormat(format);
        this.setFrameRate(frameRate);        
        //this(0, 0, width, height, format, frameRate);
    }
    
//    public VideoMode(int x, int y, int width, int height, Format format, float frameRate)
//    throws NullPointerException, IllegalArgumentException
//    {
//        this.setRegion(x, y, width, height);
//        this.setFormat(format);
//        this.setFrameRate(frameRate);        
//    }
//    
//    public int getX() {
//        return x;
//    }
//
//    public void setX(int x)
//    throws IllegalArgumentException
//    {
//        if(x < 0) {
//            throw new IllegalArgumentException();
//        }
//        
//        this.x = x;
//    }
//
//    public int getY() {
//        return y;
//    }
//
//    public void setY(int y)
//    throws IllegalArgumentException
//    {
//        if(y < 0) {
//            throw new IllegalArgumentException();
//        }
//        
//        this.y = y;
//    }    
    
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) 
    throws IllegalArgumentException
    {
        if(width <= 0) {
            throw new IllegalArgumentException();
        }
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height)
    throws IllegalArgumentException
    {
        if(height <= 0) {
            throw new IllegalArgumentException();
        }     
        this.height = height;
    }

//    public void setRegion(int x, int y, int width, int height)
//    throws IllegalArgumentException
//    {
//        if(x < 0 || y < 0 || width <= 0 || height <= 0) {
//            throw new IllegalArgumentException();
//        }
//        this.x = x;
//        this.y = y;
//        this.height = height;
//        this.width = width;        
//    }
    
    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) 
    throws NullPointerException
    {
        if(format == null) {
            throw new NullPointerException();
        }              
        this.format = format;
    }

    public float getFrameRate() 
    {
        return frameRate;
    }

    public void setFrameRate(float frameRate)
    throws NullPointerException 
    {
        if(frameRate <= 0.0f) {
            throw new IllegalArgumentException();
        }        
        this.frameRate = frameRate;
    }



    
}
