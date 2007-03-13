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

package de.telekom.laboratories.multitouch.demo.machina;

import com.sun.jna.Pointer;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static java.lang.Math.*;
import static javax.media.opengl.GL.*;

import com.sun.opengl.util.Animator;
import de.telekom.laboratories.multitouch.demo.machina.win32.GDI32;
import de.telekom.laboratories.multitouch.demo.machina.win32.User32;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;
import net.monoid.util.FPSCounter;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class Graphics {
    
    private final int     screen     = 0;
    private final boolean fullscreen = false;
    
    private Graphics() 
    throws IllegalStateException
    {        
        if(!EventQueue.isDispatchThread()) {
            throw new IllegalStateException("A Graphics object has to be created in the AWT-EventThread.");
        }
        
        final GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        final GraphicsDevice   graphicsDevice  = graphicsDevices[max(0, min(screen, graphicsDevices.length))];
        
        final JFrame frame = new JFrame("T-Demo: The Possible Machina");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        
        // <editor-fold defaultstate="collapsed" desc=" FPSCounter ">

        final String origTitle = frame.getTitle();                
        final FPSCounter fpsCounter = new FPSCounter();
        fpsCounter.addFPSCounterListener(new FPSCounter.Listener() {
            public void averageFramesElapsed(FPSCounter.Event e) {
                frame.setTitle(String.format("%s  %f(AVG) %f(AGG)", origTitle, e.getAverageFps(), e.getAggregateFps()));
            }                    
        });

        // </editor-fold>        
        
        // <editor-fold defaultstate="collapsed" desc=" OpenGL ">
        
        final GLCapabilities caps = new GLCapabilities();
        caps.setHardwareAccelerated(true);
        caps.setDoubleBuffered(true);
        
        final GLCanvas canvas = new GLCanvas(caps);
        canvas.setSize(640, 480);
        canvas.setPreferredSize(new Dimension(640, 480));
        
        canvas.addGLEventListener(new GLEventListener() {
            public void init(GLAutoDrawable drawable) {
                fpsCounter.start();
                Graphics.this.init(drawable);
            }
            public void display(GLAutoDrawable drawable) {
                fpsCounter.nextFrame();
                Graphics.this.display(drawable);
            }
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                Graphics.this.reshape(drawable, x, y, width, height);
            }
            public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
                Graphics.this.displayChanged(drawable, modeChanged, deviceChanged);
            }
        });
        
        final Animator animator = new Animator(canvas);
        animator.start();
        
        // </editor-fold>
        
        frame.getContentPane().add(canvas);
        
        // <editor-fold defaultstate="collapsed" desc=" Fullscreen ">
        
        if(fullscreen) {// && device.isFullScreenSupported()) {
            try {
                frame.setUndecorated(true);
                graphicsDevice.setFullScreenWindow(frame);
            } catch(Exception e) {
                graphicsDevice.setFullScreenWindow(null);
                frame.pack();
            }
        } else {            
            frame.pack();
            
            // <editor-fold defaultstate="collapsed" desc=" Round Region ">
            try {
                //final User32 user32 = User32.INSTANCE;
                //final GDI32 gdi32 = GDI32.INSTANCE;
                //System.out.println(user32);
                //System.out.println(gdi32);
                //final Pointer p = gdi32.CreateRoundRectRgn(0, -150, 300, 300, 300, 300);
                //final int hWnd = user32.FindWindowA(null, frame.getName());
                //user32.setWindowRgn(hWnd, p, true);                
            } catch(Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
            // </editor-fold>
        }
        
        // </editor-fold>
                                           
        frame.setVisible(true);
    }

    // <editor-fold defaultstate="collapsed" desc=" ruler ">
    
    private void ruler(GL gl) {
        
        
        final float width = 0.30f*2, height = 0.03f*2;

        final float extX = width/2.0f, extY = height / 2.0f;
        
        final float[][] points =
        {
            { +extX, +extY }, // right - top
            { -extX, +extY }, // left  - top
            { -extX, -extY }, // left  - bottom
            { +extX, -extY }, // right - bottom            
        };
        
        gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
        gl.glBegin(GL_TRIANGLE_FAN);
        
        for(float[] point : points)
        {
            gl.glVertex2fv(point, 0);
        }
                
        gl.glEnd();        
        
        gl.glColor4f(0.23f, 0.23f, 0.23f, 1.0f);
        gl.glBegin(GL_LINE_STRIP);        
        for(float[] point : points)
        {
            gl.glVertex2fv(point, 0);
        }
        gl.glVertex2fv(points[0], 0);        
        gl.glEnd();
        
        
        final int steps   = 20;
        
        final float step = width / (steps+1);
        final float y = height / 3 - extY;
                
        gl.glBegin(GL_LINES);
        for(int i=0; i<steps; i++)
        {            
            final float x = (i+1)*step - extX;
            gl.glVertex2f(x, -extY);
            gl.glVertex2f(x, y);
        }
        
        gl.glEnd();        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" dock ">
    
    private void dock(GL gl) {
        
        final int steps = 16;

        final float[][] points = new float[4*2*steps][];
        
        final float outerRadius = 1.0f;
        final float innerRadius = 0.85f;
        
        for(int i=0; i<points.length; i+=2)
        {
            final float value = 2.0f*(float)Math.PI*(i+1)/points.length;
            final float sin = (float) sin(value);
            final float cos = (float) cos(value);
            
            points[i+0] = new float[] { cos * outerRadius, sin * outerRadius};
            points[i+1] = new float[] { cos * innerRadius, sin * innerRadius};
        }
        
        
        //gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        
        gl.glBlendEquation(GL_FUNC_ADD);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);//GL_ONE);
        
        gl.glEnable(GL_BLEND);                
        
        //gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
        gl.glColor4f(0.98f, 0.8f, 0.01f, 0.6f);
        
        gl.glBegin(GL_TRIANGLE_STRIP);
        
        for(float[] point : points)
        {
            gl.glVertex2fv(point, 0);
        }
        
        gl.glVertex2fv(points[0], 0);
        gl.glVertex2fv(points[1], 0);
        
        gl.glEnd();
        
        gl.glDisable(GL_BLEND);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" mask ">
    
    private void mask(GL gl) {
        
        final int steps = 16;
        
        final int outer = 4;
        final int inner = 4*steps;
        
        final float[][] points = new float[outer+inner][];
        
        final float border = 1.0f;
        points[0] = new float[] { +border, +border };
        points[1] = new float[] { -border, +border};
        points[2] = new float[] { -border, -border };
        points[3] = new float[] { +border, -border };
        
        final float radius = 1.0f;
        
        for(int i=0; i<inner; i++)
        {
            final float value = 2.0f*(float)Math.PI*i/inner;
            points[4+i] = new float[] { (float) cos(value)*radius , (float) sin(value)*radius };            
        }
        
        // <editor-fold defaultstate="collapsed" desc=" symmetric ">
        
//        gl.glBegin(GL_TRIANGLE_STRIP);
//        for(int j=0; j<4; j++)
//        {
//            final float[] corner = points[j];
//            
//            for(int i=j*steps; i<=(j+1)*steps; i++) {
//                final float[] point = points[4+(i%inner)];
//                gl.glVertex2fv(corner, 0);
//                gl.glVertex2fv(point, 0);                
//            }
//        }
//        gl.glVertex2fv(points[0], 0);
//        gl.glEnd();
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" asymmetric ">
        
//        gl.glBegin(GL_TRIANGLE_STRIP);
//        for(int j=0; j<4; j++)
//        {
//            final float[] corner = points[j];
//            
//            for(int i=j*steps; i<(j+1)*steps; i++) {
//                final float[] point = points[4+i];
//                gl.glVertex2fv(corner, 0);
//                gl.glVertex2fv(point, 0);                
//            }
//        }        
//        gl.glVertex2fv(points[0], 0);           
//        gl.glVertex2fv(points[4], 0);        
//        gl.glEnd();        
        
        // </editor-fold>
        
        
        gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        //gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        
        gl.glBegin(GL_TRIANGLE_STRIP);
        for(int j=0; j<4; j++)
        {
            final float[] corner = points[j];
            
            for(int i=j*steps; i<=(j+1)*steps; i++) {
                final float[] point = points[4+(i%inner)];
                gl.glVertex2fv(corner, 0);
                gl.glVertex2fv(point, 0);                
            }
        }
        gl.glVertex2fv(points[0], 0);
        gl.glEnd();        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" OpenGL ">
    
    private final void init(final GLAutoDrawable drawable) {
        final GL gl = drawable.getGL();
        
    }
    private final void display(final GLAutoDrawable drawable) {
        final GL gl = drawable.getGL();

        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
        gl.glColor4f(1.0f,1.0f,1.0f,1.0f);
        gl.glBegin(GL_TRIANGLE_FAN);
        gl.glVertex2f( +1.0f, +1.0f );
        gl.glVertex2f( -1.0f, +1.0f );
        gl.glVertex2f( -1.0f, -1.0f );
        gl.glVertex2f( +1.0f, -1.0f );
        gl.glEnd();
        
        ruler(gl);

        dock(gl);
        
        mask(gl);
    }
    private final void reshape(final GLAutoDrawable drawable, final int x, final int y, int width, int height) {
        final GL gl = drawable.getGL();
  
        gl.glViewport(x, y, width, height);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT);
                
        width  = max(1, width);
        height = max(1, height);
        
        if(width >= height) {
            final int space = (width - height) / 2;
            gl.glViewport(x+space, y, height, height);
        } else {
            final int space = (height - width) / 2;
            gl.glViewport(x, y+space, width, width);            
        }

        // <editor-fold defaultstate="collapsed" desc=" non-square viewport ">
                
//        gl.glViewport(x, y, width, height);
//        
//        width  = max(1, width);
//        height = max(1, height);
//                
//        final float scale = 1.0f;
//        gl.glMatrixMode(GL_PROJECTION);
//        gl.glLoadIdentity();
//        if(width >= height)
//        {
//            final float ratio = scale * (float) width / (float) height;
//            gl.glOrtho(-ratio, ratio, -scale, scale, -1.0f, 1.0f);
//        } else
//        {
//            final float ratio = scale * (float) height / (float) width;
//            gl.glOrtho(-scale, scale, -ratio, ratio, -1.0f, 1.0f);
//            
//        }
//        gl.glMatrixMode(GL_MODELVIEW);
        
        // </editor-fold>
    }
    private final void displayChanged(final GLAutoDrawable drawable, final boolean modeChanged,final  boolean deviceChanged) {
    }    
    
    // </editor-fold>
    
    
    public static void test() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Graphics();
            }
        });
    }
}
