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

package machina;

import static java.lang.Math.*;
import static javax.media.opengl.GL.*;

import javax.media.opengl.GL;

/**
 * A annular shaped Dock.
 * @author Michael Nischt
 * @version 0.1
 */
public class Dock {
    
    // <editor-fold defaultstate="collapsed" desc=" Graphics ">
    
    public class Graphics {

        // <editor-fold defaultstate="collapsed" desc=" Attributes ">
        
        private final float[][] points;

        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Initializers ">
        
        protected Graphics(int steps)
        throws IllegalArgumentException
        {
            if(steps <= 0) {
                throw new IllegalArgumentException();
            }
                        
            final float outerRadius = 1.0f;
            final float innerRadius = getRing();
            
            final float[][] points = new float[4*2*steps][];            
            
            for(int i=0; i<points.length; i+=2) {
                final float value = 2.0f*(float)Math.PI*(i+1)/points.length;
                final float sin = (float) sin(value);
                final float cos = (float) cos(value);
                
                points[i+0] = new float[] { cos * outerRadius, sin * outerRadius};
                points[i+1] = new float[] { cos * innerRadius, sin * innerRadius};
            }    
            
            
            // save
            this.points = points;
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Methods ">
        
        public void draw(GL gl) {
            
            gl.glBlendEquation(GL_FUNC_ADD);
            gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);//GL_ONE);
            
            gl.glEnable(GL_BLEND);
            
            gl.glColor4f(0.98f, 0.8f, 0.01f, 0.6f);
            
            gl.glBegin(GL_TRIANGLE_STRIP);
            
            for(float[] point : points) {
                gl.glVertex2fv(point, 0);
            }
            
            gl.glVertex2fv(points[0], 0);
            gl.glVertex2fv(points[1], 0);
            
            gl.glEnd();
            
            gl.glDisable(GL_BLEND);
        }
        
        // </editor-fold>
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Item ">
    
    public static abstract class Item {
        
        // <editor-fold defaultstate="collapsed" desc=" Attributes ">
        
        private float angle;
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Initializers ">        
        
        protected Item(float angle)
        {
            setLocation(angle);
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Methods ">
        
        public float getLocation()
        {
            return angle;
        }
        
        public void setLocation(float angle)
        {
            this.angle = angle;            
        }
                
        // </editor-fold>
    }
    
    // </editor-fold>
    

    // <editor-fold defaultstate="collapsed" desc=" Attributes ">

    // static
    private final float ring;
    
    // dynamic
    //private final float[] color = { 1.0f, 1.0f, 1.0f, 0.5f };    
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Initializers ">

    public Dock()
    {
        this(0.8f);
    }    
    
    public Dock(float ring)
    throws IllegalArgumentException
    {
        if(ring < 0.0f || ring >= 1.0f){
            throw new IllegalArgumentException(String.format("ring must be in [0.0, 1.0[ but is %f.\n", ring));
        }
        
        // save
        this.ring = ring;        
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    public float getRing() {
        return ring;
    }
    
    // [0,2*PI[
    // pos(0,x) == angle(0)    
    public static float angle(float x, float y)
    {        
        // length (x,y) = sqrt ( dot (x,y) (x,y) )
        final float len =(float)  sqrt (x*x + y*y);
        // normalize (x,y)
        if(len <= 0.000001f) {
            return 0.0f;
        }
        else {
            final float invLen = 1.0f / len;
            x *= invLen;
            y *= invLen;
        }
                
        // <editor-fold defaultstate="collapsed" desc=" general ">
        
        //final float axisX = 0.0f, axisY = 1.0f;        
        ////angle (x,y) = axcos ( dot (x,y) (axisX,axisY) )
        //final float angle = (float) acos ( (x*axisX) + (y*axisY) );
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" specific ">

        ////angle (x,y) = axcos ( dot (x,y) (0,1) )
        //final float angle = (float) acos ( y );        
        
        // </editor-fold>

        //angle (x,y) = axcos ( dot (x,y) (0,1) )
        final float angle = (float) acos ( y );        
        
        return angle;
    }

    public float distance(float x, float y) {
        final float ring = getRing();
        
        // length (x,y) = sqrt ( dot (x,y) (x,y) )
        final float len =(float)  sqrt (x*x + y*y);
        // normalize (x,y)
        if(len <= 0.000001f) {
            return ring;
        }
        else {
            final float invLen = 1.0f / len;
            x *= invLen;
            y *= invLen;
        }
        
        return abs (len - ring);
    }
    
    private float[] position(float angle)
    {
        return position(angle, new float[2]);
    }
    
    private float[] position(float angle, float[] dst)
    throws NullPointerException
    {
        if(dst.length < 2) {
            dst = new float[2];
        }
        
        final float ring = getRing();
        final float scale = ring + 0.5f * (1.0f - ring);
        
        // x
        dst[0] = scale * (float) -sin(angle);
        // y
        dst[1] = scale * (float)  cos(angle); 
        
        return dst;
    }
    
        
//    // [0,2*PI[ periodic
//    // pos(0,x) == angle(0)
//    public Item attach(float angle, Object bounds)
//    {
//        return new Item(angle);
//    }
    
    
    
    public Graphics createGraphics(int steps)
    throws IllegalArgumentException
    {
        return new Graphics(steps);
    }
    
    // </editor-fold>    
    
}
