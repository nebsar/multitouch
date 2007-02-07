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

package de.telekom.laboratories.multitouch.demo;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import com.sun.opengl.util.Animator;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.media.opengl.DefaultGLCapabilitiesChooser;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class FingerPaint {

    private static class Brush {

        private float x;
        private float y;

        private float radius = 0.01f;
        
        public Brush() {}
        
        public Brush(float x, float y) {
            setX(x);
            setY(y);
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;            
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }        
        
        
        private void draw(final GL gl) {

            final float x = getX(), y = getY(), radius = getRadius();            
            
            gl.glBegin(GL.GL_TRIANGLE_FAN);                                                                                

            gl.glColor3f(0.9f, 0.9f, 1.0f);

            gl.glVertex3f(x, y, 0.5f);

            for(double angle=0.0; angle <= 2*PI+0.1; angle+=0.1 )
            {                                
                gl.glVertex3d(x-radius*cos(angle), y-radius*sin(angle), 0.5f);
            }

            gl.glEnd();                
        }

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

    }    
    
    
    private List<Brush> brushes;
    
    /** Creates a new instance of FingerPaint */
    public FingerPaint() {        
        
    }
    
    
    
    
    public static void main(String... args) {
        
        final List<Brush> brushes = new ArrayList<Brush>();
        
        Brush brush;
        
        //1st brush        
        brush = new Brush();        
        brush.setRadius(0.025f);        
        brushes.add(brush);

        //2nd brush        
        brush = new Brush(0.25f, 0.25f); //x,y
        brushes.add(brush);     

        //3nd brush        
        brushes.add(new Brush(0.25f, -0.25f));
        
                
        // <editor-fold defaultstate="collapsed" desc=" OpenGL ">
        
        final GLCanvas canvas = new GLCanvas();
        canvas.setSize(640, 480);
        canvas.setPreferredSize(new Dimension(640, 480));
        
        final GLCapabilities caps = new GLCapabilities(); 
        caps.setHardwareAccelerated(true);
        caps.setDoubleBuffered(true);
        
        GLDrawableFactory.getFactory().getGLDrawable(canvas, caps, new DefaultGLCapabilitiesChooser());
        
        canvas.addGLEventListener(new GLEventListener() {
            
            private Animator animator;
            
            public void init(GLAutoDrawable drawable) {
                if(animator != null) {
                    animator.stop();
                }
                animator = new Animator(drawable);
                animator.start();
                
                final GL gl = drawable.getGL();
                
                gl.glClearColor(0.4f,0.2f,0.2f,0.5f);
                
            }
            
            public void display(GLAutoDrawable drawable) {
                final GL gl = drawable.getGL();
                
                gl.glClear(GL.GL_COLOR_BUFFER_BIT);
                
                gl.glLoadIdentity();
                
                
//                gl.glBegin(GL.GL_QUADS);
//                
//                gl.glColor3f(0.0f, 1.0f, 0.0f);
//                
//                gl.glVertex3f(-0.25f,  0.25f, 0.5f);
//                gl.glVertex3f(-0.25f, -0.25f, 0.5f);
//                gl.glVertex3f( 0.25f, -0.25f, 0.5f);
//                gl.glVertex3f( 0.25f,  0.25f, 0.5f);                                                                
//                
//                gl.glEnd();

                for(Brush brush : brushes) {
                    
                    brush.draw(gl);
                    
                }
                
            }

            
            public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
            }
        
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

                final GL gl = drawable.getGL();

                if (height <= 0) // avoid a divide by zero error!
                    height = 1;
                
                gl.glViewport(0, 0, width, height);

                final float h = (float) width / (float) height;
                gl.glMatrixMode(GL.GL_PROJECTION);
                gl.glLoadIdentity();
                
                if(h >= 1.0f) {
                    gl.glOrtho( -1.0f*h, 1.0f*h, -1.0f, 1.0f, 1.0f, -1.0f );
                } else {
                    gl.glOrtho( -1.0f, 1.0f, -1.0f/h, 1.0f/h, 1.0f, -1.0f );
                }
                
                gl.glMatrixMode(GL.GL_MODELVIEW);                


//                gl.glLoadIdentity();
//                glu.gluPerspective(45.0f, h, 1.0, 20.0);
//                gl.glLoadIdentity();                
                
            }
        });
        
              
        // </editor-fold>
        
        final JFrame frame = new JFrame("FingerPaint");      
        //frame.setSize(640, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        frame.getContentPane().add(canvas);
        
        // <editor-fold defaultstate="collapsed" desc=" Fullscreen ">
        
        final boolean fullscreen = true;                
        final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        if(fullscreen && device.isFullScreenSupported())
        {
            frame.setUndecorated(true);            
            device.setFullScreenWindow(frame);
        } else {
            frame.pack();
        }
              
        // </editor-fold>
        
        frame.setVisible(true);           
    }
    
}
