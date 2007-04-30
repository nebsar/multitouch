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

package de.telekom.laboratories.multitouch.demo.gallery;

import java.beans.ConstructorProperties;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author Michael Nischt
 * @version 0.1
 */
public class Preview 
{
    // <editor-fold defaultstate="collapsed" desc=" Position ">
    
    public static final class Position { 
        
        //public final static float MIN_VALUE_X = -1.0f;
        //public final static float MAX_VALUE_X = +1.0f;
        //
        //public final static float MIN_VALUE_Y = -1.0f;
        //public final static float MAX_VALUE_Y = +1.0f;
        //
        //public final static float MIN_VALUE_Z = +0.0f;
        //public final static float MAX_VALUE_Z = +1.0f;
                
        public final float x, y, z;
                
        public Position(float x, float y, float z) {            
            this.x = x;
            this.y = y;
            this.z = z;
            //this.x = max(MIN_VALUE_X, min(x, MAX_VALUE_X) );
            //this.y = max(MIN_VALUE_Y, min(y, MAX_VALUE_Y) );
            //this.z = max(MIN_VALUE_Z, min(z, MAX_VALUE_Z) );
        }
        public float getX() { return x; }
        public float getY() { return y; }
        public float getZ() { return z; }
        
        public Position x(float x) { return new Position(x, y, z); }
        public Position y(float y) { return new Position(x, y, z); }
        public Position z(float z) { return new Position(x, y, z); }
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Orientation ">
    
    public static final class Orientation { 
        //public final static float MIN_VALUE_ANGLE = -1.0f;
        //public final static float MAX_VALUE_ANGLE = +1.0f;
        
        public final float angle;
        
        Orientation(float angle) {
            this.angle = angle;
            //this.angle = max(MIN_VALUE_ANGLE, min(angle, MAX_VALUE_ANGLE) );
        }        
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Size ">

    public static final class Size 
    { 
        //public final static float MIN_VALUE_WIDTH = +0.1f;
        //public final static float MAX_VALUE_WIDTH = +1.0f;
        //
        //public final static float MIN_VALUE_HEIGHT = +0.1f;
        //public final static float MAX_VALUE_HEIGHT = +1.0f;        
        
        public final float width, height;
        
        public Size(float width, float height) 
        {
            this.width= width;
            this.height = height;
            //this.width  = max(MIN_VALUE_WIDTH,  min(width,  MAX_VALUE_WIDTH) );
            //this.height = max(MIN_VALUE_HEIGHT, min(height, MAX_VALUE_HEIGHT) );
        }
        
        public float getWidth()  { return width;  }
        public float getHeight() { return height; }        
    }
    
    // </editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    private final int content;
    
    private Position position;
    private Orientation orientation;
    private Size size;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">
    
    @ConstructorProperties("content")
    public Preview(int content) 
    throws IllegalArgumentException
    { 
        this( content, new Position(0.0f, 0.0f, 0.0f), new Orientation(0.0f), new Size(1.0f, 1.0f) );
    }
    
    @ConstructorProperties({"content", "position", "orientation", "size"})
    private Preview(int content, Position position, Orientation orientation, Size size) 
    throws IllegalArgumentException, NullPointerException
    {
        if(content < 0) {
            throw new IllegalArgumentException();
        }
        this.content = content;
        
        setPosition ( position );
        setOrientation( orientation );
        setSize( size );        
    }
    
    private static class New
    { 
        private int content;

        private Position position;
        private Orientation orientation;
        private Size size;
        
        public New content(int content) 
        { this.content = content; return this; }
        
        public New position(Position position)
        { this.position = position; return this; }

        public New orientation(Orientation orientation)
        { this.orientation = orientation; return this; }

        public New size(Size size)
        { this.size = size; return this; }                
     
        public Preview instance() {
            return new Preview(content, position, orientation, size);
        }        
    }    
    
    // </editor-fold>
            
    // <editor-fold defaultstate="collapsed" desc=" Properties ">    
    
    public int getContent() {
        return content;
    }
    
    public Position getPosition() 
    { return position; }
    public void     setPosition(Position position) throws NullPointerException
    { if( position == null ) throw new NullPointerException(); this.position = position; }
    
    public Orientation getOrientation() 
    { return orientation; }
    public void setOrientation(Orientation orientation) throws NullPointerException
    { if( orientation == null ) throw new NullPointerException(); this.orientation =  orientation; }

    public Size getSize() 
    { return size; }
    public void setSize(Size size) throws NullPointerException
    { if( size == null ) throw new NullPointerException(); this.size = size; }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">

    public Position position(float x, float y, float z) 
    { return position = new Position(x, y, z); }

    public Orientation orientation(float angle) 
    { return orientation = new Orientation(angle); }
    
    public Size size(float width, float height) 
    { return size = new Size(width, height); }

    
//    public Position translate(float x, float y) 
//    { return position = new Position(position.x + x, position.y + y, position.z); }
//    
//    public Orientation rotate(float angle) 
//    { return orientation = new Orientation(orientation.angle + angle); }
//    
//    public Size scale(float factor) 
//    { return size = new Size(size.width * factor, size.height * factor); }
    
    // </editor-fold>

    
    //void toTop() {}
    //void rise() {}
    //void lower() {}
    //void toBottom() {}        
}
