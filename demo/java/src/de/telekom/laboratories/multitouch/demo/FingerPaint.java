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

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import com.sun.opengl.util.Animator;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    
    // <editor-fold defaultstate="collapsed" desc=" Brush ">
    
    private static class Brush {
        
        // <editor-fold defaultstate="collapsed" desc=" Attributes ">
        
        // center of the brush
        private boolean init = false;
        private float x, oldX;
        private float y, oldY;
        //color of brush
        private float r;
        private float g;
        private float b;
        
        private float radius = 0.01f;
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Constructors ">
        
        public Brush() {}
        
        public Brush(float x, float y) {
            setX(x);
            setY(y);
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Location ">
        
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
        
        public void setLocation(float x, float y) {
            setX(x);
            setY(y);
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Radius ">
        
        public float getRadius() {
            return radius;
        }
        
        public void setRadius(float radius) {
            this.radius = radius;
        }        
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Color ">
        
        public float getR() {
            return r;
        }

        public void setR(float r) {
            this.r = r;
        }

        public float getG() {
            return g;
        }

        public void setG(float g) {
            this.g = g;
        }

        public float getB() {
            return b;
        }

        public void setB(float b) {
            this.b = b;
        }                
        
        public void setColor(float r, float g, float b) {
            setR(r);
            setG(g);
            setB(b);
        }
        
        // </editor-fold>
        
        private void draw(final GL gl) {
            
            final float x = getX(), y = getY(), radius = getRadius();
            final float r = getR(), g = getG(), b = getB();                        

            gl.glColor3f(r, g, b);            
                        
            gl.glBegin(GL.GL_TRIANGLE_FAN);
            
            gl.glVertex3f(x, y, 0.5f);
            
            for(double angle=0.0; angle <= 2*PI+0.1; angle+=0.1 ) {
                gl.glVertex3d(x-radius*cos(angle), y-radius*sin(angle), 0.5f);
            }
            
            gl.glEnd();           
            
            if(!init) {
                oldX = x;
                oldY = y;
                init = true;
                return;
            }
            
            gl.glBegin(GL.GL_TRIANGLE_FAN);
            
            gl.glVertex3f(oldX, oldY, 0.5f);
            
            for(double angle=0.0; angle <= 2*PI+0.1; angle+=0.1 ) {
                gl.glVertex3d(oldX-radius*cos(angle), oldY-radius*sin(angle), 0.5f);
            }
            
            gl.glEnd();            
            
            float dX = x - oldX;
            float dY = y - oldY;
            
            final float len = (float) Math.sqrt(dX*dX + dY*dY);
            if(len > 0.0001) {
                dX /= len;
                dX *= radius;
                dY /= len;
                dY *= radius;
            }

            // x* cos (angle) - y* sin(angle) = x'
            // x* sin (angle) + y* cos(angle) = y'
            
            final float perpX = - dY;
            final float perpY =   dX;
            
            
            gl.glPointSize(4.0f);
            gl.glBegin(GL.GL_QUADS);
            
//                gl.glVertex3f(x+perpX, y+perpY, 0.5f);
//                gl.glVertex3f(x-perpX, y-perpY, 0.5f);
//                
//                gl.glVertex3f(oldX-perpX, oldY-perpY, 0.5f);                
//                gl.glVertex3f(oldX+perpX, oldY+perpY, 0.5f);
            
            if(RANDOM.nextBoolean()) {            
                gl.glVertex3f(x-perpX, y-perpY, 0.5f);
                gl.glVertex3f(x+perpX, y+perpY, 0.5f);
                                
                gl.glVertex3f(oldX-perpX, oldY-perpY, 0.5f);                
                gl.glVertex3f(oldX+perpX, oldY+perpY, 0.5f);            
            } else {
                gl.glVertex3f(x+perpX, y+perpY, 0.5f);
                gl.glVertex3f(x-perpX, y-perpY, 0.5f);
              
                gl.glVertex3f(oldX+perpX, oldY+perpY, 0.5f);                
                gl.glVertex3f(oldX-perpX, oldY-perpY, 0.5f);                
            }
                            
            gl.glEnd();
            
            oldX = x;
            oldY = y;
        }   
        
        private static final Random RANDOM = new Random();
        
    }
    
    // </editor-fold>
       
    
    /** Creates a new instance of FingerPaint */
    public FingerPaint() {        
    }
    
    
    static void setBrushColor(Brush brush, int color) {
        brush.setColor(
                ((0x00ff0000 & color) >> 16)/ 255.0f,
                ((0x0000ff00 & color) >> 8) / 255.0f,                 
                (0x000000ff & color) / 255.0f
                );
    }
    
    public static void main(String... args) {
        
        final int COLORS = 10;
        
        final int[] colors = new int[COLORS];
        
        for (int i=0; i<colors.length; i++) {            
            final float hue = (float)i/(colors.length+1);
            colors[i] = Color.HSBtoRGB(hue, 1.0f, 1.0f);       
        }

        
        final List<Brush> brushes = new ArrayList<Brush>();
        
        //brushes.add(new Brush(0.25f,0.25f));
        
//        Brush brush;
//        
//        //1st brush
//        brush = new Brush();
//        brush.setRadius(0.025f);     
//        setBrushColor(brush, colors[0]);
//        brushes.add(brush);
//        
//        //2nd brush
//        brush = new Brush(0.25f, 0.25f); //x,y
//        setBrushColor(brush, colors[1]);
//        brushes.add(brush);
//        
//        //3nd brush
//        brush = new Brush(0.25f, -0.25f);
//        setBrushColor(brush, colors[2]);
//        brushes.add(brush);
        
        
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
                                
                gl.glClearColor(0.4f,0.2f,0.2f,1.0f);
                gl.glClear(GL.GL_COLOR_BUFFER_BIT);
                               
                
                gl.glBlendEquation(GL.GL_ADD);
                //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);  
                gl.glBlendFuncSeparate(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA, GL.GL_ZERO, GL.GL_ONE);  
                //gl.glColor4f(1.0f,1.0f,1.0f,0.5f);			// Full Brightness, 50% Alpha ( NEW )
                
                
            }
            
            public void display(GLAutoDrawable drawable) {
                final GL gl = drawable.getGL();
                
                //gl.glClear(GL.GL_COLOR_BUFFER_BIT);
                
//                gl.glMatrixMode(GL.GL_PROJECTION);
//                gl.glPushMatrix();
//                gl.glLoadIdentity();
//                
//                gl.glEnable(GL.GL_BLEND);
//                
//                gl.glBegin(GL.GL_QUADS);
//
//                gl.glColor4f(0.4f,0.2f,0.2f,0.05f);
//
//                gl.glVertex3f(-1.0f,  1.0f, 0.5f);
//                gl.glVertex3f(-1.0f, -1.0f, 0.5f);
//                gl.glVertex3f( 1.0f, -1.0f, 0.5f);
//                gl.glVertex3f( 1.0f,  1.0f, 0.5f);
//
//                gl.glEnd();
//                
//                gl.glDisable(GL.GL_BLEND);
//                
//                gl.glPopMatrix();
//                gl.glMatrixMode(GL.GL_MODELVIEW);
//                
//                gl.glLoadIdentity();
                                               
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
        
        
        // <editor-fold defaultstate="collapsed" desc=" MouseInput ">
        
        final MouseAdapter mouseInput = new MouseAdapter()
        {                        
            
            private Brush brush;
            private int color = 0;
            
            public void mouseDragged(MouseEvent e)
            {
                if(!brushes.isEmpty()) {
                    final Brush brush = brushes.get(brushes.size()-1);
                    setBrushLocation(brush, e);
                }
            }            

            public void mousePressed(MouseEvent e)
            {
                final Brush brush = new Brush();
                setBrushLocation(brush, e);
                
                color %= colors.length;                
                setBrushColor(brush, colors[color]);
                color++;

                brushes.add(brush);
            }
            public void mouseReleased(MouseEvent e)
            {
                brushes.clear();
            }
            
            private void setBrushLocation(final Brush brush, final MouseEvent e) {
                
                float width = e.getComponent().getWidth(), height = e.getComponent().getHeight();
                
                if (height <= 0.0f) // avoid a divide by zero error!
                    height = 1.0f;           
                
                final float ratio = (float) width / (float) height;
                
                float x = 2.0f * (+ (e.getX() / width)  - 0.5f);
                float y = 2.0f * (- (e.getY() / height) + 0.5f);
                
                if(ratio >= 1.0f) {
                    x *= ratio;
                } else {
                    y /= ratio;

                }
                
                brush.setLocation(x,y);
            }            
        };
        canvas.addMouseListener(mouseInput);
        canvas.addMouseMotionListener(mouseInput);
        
        // </editor-fold>
        
        final JFrame frame = new JFrame("FingerPaint");
        //frame.setSize(640, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(canvas);
        
        // <editor-fold defaultstate="collapsed" desc=" Fullscreen ">
        
        final boolean fullscreen = true;
        final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        if(fullscreen && device.isFullScreenSupported()) {
            frame.setUndecorated(true);
            device.setFullScreenWindow(frame);
        } else {
            frame.pack();
        }
        
        // </editor-fold>
        
        frame.setVisible(true);
    }
    
}
