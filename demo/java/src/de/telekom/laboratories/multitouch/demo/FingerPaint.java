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

import com.sun.opengl.util.Animator;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
    
    /** Creates a new instance of FingerPaint */
    public FingerPaint() {
    }
    
    
    
    public static void main(String... args) {
        
                
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
                
                gl.glClearColor(1.0f,0.0f,0.0f,0.5f);
                
            }
            
            public void display(GLAutoDrawable drawable) {
                final GL gl = drawable.getGL();
                
                gl.glClear(GL.GL_COLOR_BUFFER_BIT);

            }
            
            public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
            }
        
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

                final GL gl = drawable.getGL();

                if (height <= 0) // avoid a divide by zero error!
                    height = 1;
                
                gl.glViewport(0, 0, width, height);

//                final float h = (float) width / (float) height;                                
//                gl.glMatrixMode(GL.GL_PROJECTION);
//                gl.glLoadIdentity();
//                glu.gluPerspective(45.0f, h, 1.0, 20.0);
//                gl.glMatrixMode(GL.GL_MODELVIEW);
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
