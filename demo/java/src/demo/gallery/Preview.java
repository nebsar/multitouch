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

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author Michael Nischt
 * @version 0.1
 */
public class Preview 
{
    // <editor-fold defaultstate="collapsed" desc=" Geometry ">
    
    static class Geometry {
        
        // <editor-fold defaultstate="collapsed" desc=" Center ">

        public static final class Center 
        { 
            public final float x, y;

            public Center(float x, float y) {
                this.x = x;
                this.y = y;
            }
            public float getX() { return x; }
            public float getY() { return y; }

            public Center x(float x) { return new Center(x, y); }
            public Center y(float y) { return new Center(x, y); }

            public boolean equals(Object obj) {
                if (obj == this) // fast return for immutable types
                    return true;
                else if (obj == null || !(obj instanceof Center))//fast check for final classes vs. getClass() != obj.getClass())
                    return false;

                final Center other = (Center) obj;
                if (this.x != other.x || this.y != other.y)
                    return false;
                else
                    return true;
            }

            public int hashCode() {
                //int hash = 3;
                //hash = 13 * hash + Float.floatToIntBits(this.x);
                //hash = 13 * hash + Float.floatToIntBits(this.y);
                //return hash;
                return Float.floatToIntBits(this.x) ^ Float.floatToIntBits(this.y);
            }
        }

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Extent ">

        public static final class Extent 
        { 
            public final float x, y;

            public Extent(float x, float y) {
                this.x = Math.abs(x);
                this.y = Math.abs(x);
            }
            public float getX() { return x; }
            public float getY() { return y; }

            public Extent x(float x) { return new Extent(x, y); }
            public Extent y(float y) { return new Extent(x, y); }

            public boolean equals(Object obj) {
                if (obj == this) // fast return
                    return true;
                else if (obj == null || !(obj instanceof Extent))//fast check for final classes vs. getClass() != obj.getClass())
                    return false;

                final Extent other = (Extent) obj;
                if (this.x != other.x || this.y != other.y)
                    return false;
                else
                    return true;
            }

            public int hashCode() {
                //int hash = 3;
                //hash = 13 * hash + Float.floatToIntBits(this.x);
                //hash = 13 * hash + Float.floatToIntBits(this.y);
                //return hash;
                return Float.floatToIntBits(this.x) ^ Float.floatToIntBits(this.y);
            }
        }

        // </editor-fold>        
        
        
        // <editor-fold defaultstate="collapsed" desc=" Variables ">

        private final Center center;
        private final Extent extent;
        private final float orientation;

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Initializers ">        
        
        Geometry(Center center, Extent extent, float orientation)
        {
            if(center == null || extent == null) 
            {
                throw new NullPointerException();
            }
            
            this.center = center;
            this.extent = extent;
            this.orientation = orientation;
        }
        
        // </editor-fold>
            
        // <editor-fold defaultstate="collapsed" desc=" Properties ">            
        
        public Center getCenter() 
        { return center; }

        public Extent getExtent() 
        { return extent; }
        
        public float getOrientation() 
        { return orientation; }        
        
        // </editor-fold>        
    }
    
    // </editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    private final int content;    
    private final Geometry geometry;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">
    
    private Preview(int content, Geometry geometry) 
    throws IllegalArgumentException, NullPointerException
    {
        if(content < 0) {
            throw new IllegalArgumentException();
        } else if(geometry == null) {
            throw new NullPointerException();
        }
        this.content = content;        
        this.geometry = geometry;
    }  
    
    // </editor-fold>
            
    // <editor-fold defaultstate="collapsed" desc=" Properties ">    
    
    public int getContent() {
        return content;
    }
    
    public Geometry getGeometry() {
        return geometry;
    }    
    
    // </editor-fold>



}
