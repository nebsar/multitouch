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

package demo.vvvv;

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
import java.util.Iterator;
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
        
        private int id;
        
        // center of the brush
        private boolean init = false;
        private float x, oldX;
        private float y, oldY;
        //color of brush
        private float r;
        private float g;
        private float b;
        
        private float radius = 0.04f;
        
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
        
        // <editor-fold defaultstate="collapsed" desc=" Id ">
        
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
            
            gl.glBegin(GL.GL_QUADS);
            
                gl.glVertex3f(x+perpX, y+perpY, 0.5f);
                gl.glVertex3f(x-perpX, y-perpY, 0.5f);
                
                gl.glVertex3f(oldX-perpX, oldY-perpY, 0.5f);
                gl.glVertex3f(oldX+perpX, oldY+perpY, 0.5f);
            
//            if(RANDOM.nextBoolean()) {            
//                gl.glVertex3f(x-perpX, y-perpY, 0.5f);
//                gl.glVertex3f(x+perpX, y+perpY, 0.5f);
//                                
//                gl.glVertex3f(oldX-perpX, oldY-perpY, 0.5f);
//                gl.glVertex3f(oldX+perpX, oldY+perpY, 0.5f);
//            } else {
//                gl.glVertex3f(x+perpX, y+perpY, 0.5f);
//                gl.glVertex3f(x-perpX, y-perpY, 0.5f);
//              
//                gl.glVertex3f(oldX+perpX, oldY+perpY, 0.5f);
//                gl.glVertex3f(oldX-perpX, oldY-perpY, 0.5f);
//            }
                            
            gl.glEnd();
            
            oldX = x;
            oldY = y;
        }   
        
        private static final Random RANDOM = new Random();


        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Slider ">
    
    private static class Slider {
        
        // <editor-fold defaultstate="collapsed" desc=" Attributes ">
        
        private int id;
        
        // center of the brush
        private float x;
        private float y;
        //color of brush
        private float extendX = 0.075f;
        private float extendY = 0.250f;
        // slider state
        private  float factor = 0.5f;
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Constructors ">
        
        private static int ids = 0;
        public Slider() {
            id = ids++;
        }
        
        public Slider(float x, float y) {
            this();
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
        
        // <editor-fold defaultstate="collapsed" desc=" Extend & Size ">

        public float getExtendX() {
            return extendX;
        }

        private void setExtendX(float extendX) {
            this.extendX = Math.abs(extendX);
        }

        private float getExtendY() {
            return extendY;
        }

        private void setExtendY(float extendY) {
            this.extendY = Math.abs(extendY);
        }
        
        private void setExtend(float x, float y)
        {
            setExtendX(x);
            setExtendY(y);
        }
        
        private float getWidth() {
            return getExtendX()*2.0f;
        }
        
        public void setWidth(float width) {
            setExtendX(width/2.0f);
        }
        
        private float getHeight() {
            return getExtendY()*2.0f;
        }        
        
        public void setHeight(float height) {
            setExtendY(height/2.0f);
        }
        
        public void setSize(float width, float height) {
            setWidth(width);
            setHeight(height);            
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Id ">
        
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
        
        // </editor-fold>        
        
        // <editor-fold defaultstate="collapsed" desc=" Factor ">
        
        public float getFactor() {
            return factor;
        }
        
        public void setFactor(float factor) {
            this.factor = Math.max(0.0f, Math.min(factor, 1.0f));
            //System.out.println(id + ": " + factor);
        }
        
        // </editor-fold>    
        
        private void draw(final GL gl) {
            
            final float x = getX(), y = getY();
            final float extX = getExtendX(), extY = getExtendY();

            //gl.glLineStipple(1, (short)0xFFFF);
            gl.glLineWidth(2.0f);
            
            gl.glBegin(GL.GL_QUADS);
            
            gl.glColor3f(0.0f, 0.0f, 0.0f);
            gl.glVertex3f(x-extX, y+extY, 0.35f);
            gl.glVertex3f(x-extX, y-extY, 0.35f);
            gl.glVertex3f(x+extX, y-extY, 0.35f);
            gl.glVertex3f(x+extX, y+extY, 0.35f);
            
            gl.glEnd();            
            
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
            
            gl.glBegin(GL.GL_QUADS);
            
            gl.glColor3f(0.66f, 0.66f, 0.66f);
            gl.glVertex3f(x-extX, y+extY, 0.25f);
            gl.glVertex3f(x-extX, y-extY, 0.25f);
            gl.glVertex3f(x+extX, y-extY, 0.25f);
            gl.glVertex3f(x+extX, y+extY, 0.25f);
            
            gl.glEnd();
            
//            gl.glEnable(GL.GL_LINE_STIPPLE);
//            gl.glLineStipple(5, (short)0xAAAA);
            
            gl.glLineWidth(1.0f);
            gl.glBegin(GL.GL_LINES);
            
            gl.glColor3f(0.33f, 0.33f, 0.33f);
            
            final int STEPS = 20;
            final float scale = 0.2f*extX;
            for(int i=2; i<STEPS-1; i++) {
            
                final float step = i*extY*2.0f/STEPS;
                
                gl.glVertex3f(x-scale, step+y-extY, 0.25f);
                gl.glVertex3f(x+scale, step+y-extY, 0.25f);
            
            }
            
            gl.glEnd();  
            
            final float top    =        1  * extY*2.0f/STEPS;
            final float bottom = (STEPS-1) * extY*2.0f/STEPS;
            
            gl.glBegin(GL.GL_LINES);
            
            gl.glColor3f(0.85f, 0.85f, 0.85f);
            gl.glVertex3f(x, top+y-extY, 0.25f);
            gl.glVertex3f(x, bottom+y-extY, 0.25f);
            
            gl.glEnd();  
            
//            gl.glDisable(GL.GL_LINE_STIPPLE);
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
            
            final float scale2 = 0.5f*extX;
            
            final float factor = getFactor();
            final float value = -extY + (1.0f/STEPS) + factor*(2*(extY-(1.0f/STEPS)));
            final float step2 = (extX/7.0f);
            
            
            gl.glBegin(GL.GL_QUADS);
            
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glVertex3f(x-scale2, value+y+step2, 0.25f);
            gl.glVertex3f(x-scale2, value+y-step2, 0.25f);
            gl.glVertex3f(x+scale2, value+y-step2, 0.25f);
            gl.glVertex3f(x+scale2, value+y+step2, 0.25f);
            
            gl.glEnd();            
        }
        
        public boolean update(float x, float y) {
            if(x < this.getX()-this.getExtendX() || x > this.getX()+this.getExtendX()
            || y < this.getY()-this.getExtendY() || y > this.getY()+this.getExtendY()) {
                return false;
            }
            
            this.setFactor( ((y-this.getY())+getExtendY()) / this.getHeight() );
            
            return true;
        }

        
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
    
    public static void main(String... args) throws Exception {
       
        final OSCTrackerClient tracker = new OSCTrackerClient(9000);        
        tracker.setTracking(true);
        
        final int COLORS = 20;
        
        final int[] colors = new int[COLORS];
        
        for (int i=0; i<colors.length; i++) {            
            final float hue = (float)i/(colors.length+1);
            colors[i] = Color.HSBtoRGB(hue, 1.0f, 1.0f);       
        }

        
        final List<Brush> brushes = new ArrayList<Brush>();
        final List<Slider> sliders = new ArrayList<Slider>();  
        
        Slider slider;
        slider = new Slider(-0.4f, -0.0f);
        slider.setSize(slider.getWidth()*1.5f, slider.getHeight() * 1.5f);
        sliders.add(slider);
        slider = new Slider(-0.8f, -0.0f);
        slider.setSize(slider.getWidth()*1.5f, slider.getHeight() * 1.5f);
        sliders.add(slider);
        
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
            private float ratio = 1.0f;
            private int color;
            private final float[] bgColor = { 0.4f,0.2f,0.2f };
            
            private float opacity = 0.5f;
            
            
            @Override
            public void init(GLAutoDrawable drawable) {
                if(animator != null) {
                    animator.stop();
                }
                animator = new Animator(drawable);
                animator.start();
                
                final GL gl = drawable.getGL();
                           
                gl.glClearColor(0.0f,0.0f,0.0f,1.0f);//bgColor[0],bgColor[1],bgColor[2],1.0f);//
                gl.glClear(GL.GL_COLOR_BUFFER_BIT);
                               
                
                gl.glBlendEquation(GL.GL_FUNC_REVERSE_SUBTRACT);                
                gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);//_MINUS_SRC_ALPHA);  
                //gl.glBlendEquationSeparate(GL.GL_ADD,GL.GL_REPLACE);
                //gl.glBlendFuncSeparate(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA, GL.GL_ONE, GL.GL_ZERO);  
                //gl.glColor4f(1.0f,1.0f,1.0f,0.5f);			// Full Brightness, 50% Alpha ( NEW )
                
                
            }
            
            @Override
            public void display(GLAutoDrawable drawable) {                                
                
                // <editor-fold defaultstate="collapsed" desc=" Tracking ">
                
                Object[] data;
                while( (data = tracker.nextFrame()) != null ) {
                   
                    
                    final int[] ids = new int[data.length/3];
                    
                    
                    for(int blob=0; blob < data.length-2; blob+=3) {
                        
                        float x  = 2.0f * (Float) data[blob+0];
                        float y  = 2.0f * (Float) data[blob+1];
                        
                        if(ratio >= 1.0f) {
                            x *= ratio;
                        } else {
                            y /= ratio;

                        }
                        
                        final float id = (Float) data[blob+2];
                        ids[blob/3] = (int) id;
                        
                        for(Slider slider : sliders) {
                            if(slider.update(x, y)) {
                                continue;
                            }
                        }
                        
                        
                        boolean isNew = true;
                        
                        for(Brush brush : brushes) {
                            if(brush.getId() == (int) id) {
                                brush.setLocation(x, y);
                                isNew = false;
                                break;
                            }
                        }
                        
                        if(isNew) {
                            final Brush brush = new Brush(x, y);
                            brush.setId((int)id);

                            color %= colors.length;
                            setBrushColor(brush, colors[color]);
                            color++;                            
                            
                            brushes.add(brush);
                        }                        
                    }
                    
                    final Iterator<Brush> brushIt = brushes.iterator();
                    while(brushIt.hasNext()) {
                        final Brush brush = brushIt.next();
                        
                        boolean removed = true;
                        for(int id : ids) {
                            if(id == brush.getId()) {
                                removed = false;
                                break;
                            }
                        }
                        
                        if(removed) {
                            brushIt.remove();
                        }
                    }
                                        
                }
                
                // </editor-fold>
                
                if(sliders.size() > 0) {
                    final float size = sliders.get(0).getFactor();
                    final float radius = 0.01f + 0.08f * size;
                    for(Brush brush : brushes) {
                        brush.setRadius(radius);
                    }
                }
                if(sliders.size() > 1) {
                    opacity = sliders.get(1).getFactor();
                }                
                
                final GL gl = drawable.getGL();
                
                //gl.glClear(GL.GL_COLOR_BUFFER_BIT);
                
                gl.glMatrixMode(GL.GL_PROJECTION);
                gl.glPushMatrix();
                gl.glLoadIdentity();
                
                gl.glEnable(GL.GL_BLEND);
                
                gl.glBegin(GL.GL_QUADS);

                gl.glColor4f(1.0f,1.0f,1.0f, 0.0025f+ 0.1f*(float)Math.log(opacity+0.5f));//bgColor[0],bgColor[1],bgColor[2],0.005f);////0.4f,0.2f,0.2f, 0.005f);

                gl.glVertex3f(-1.0f,  1.0f, 0.15f);
                gl.glVertex3f(-1.0f, -1.0f, 0.15f);
                gl.glVertex3f( 1.0f, -1.0f, 0.15f);
                gl.glVertex3f( 1.0f,  1.0f, 0.15f);

                gl.glEnd();
                
                gl.glDisable(GL.GL_BLEND);
                
                gl.glPopMatrix();
                gl.glMatrixMode(GL.GL_MODELVIEW);
                
                gl.glLoadIdentity();
                                               
                for(Brush brush : brushes) {                    
                    brush.draw(gl);                    
                }
                
                for(Slider slider : sliders) {                    
                    slider.draw(gl);                    
                }                
                
            }
            
            
            @Override
            public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
            }
            
            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                
                final GL gl = drawable.getGL();
                
                if (height <= 0) // avoid a divide by zero error!
                    height = 1;
                
                gl.glViewport(0, 0, width, height);
                
                ratio = (float) width / (float) height;
                gl.glMatrixMode(GL.GL_PROJECTION);
                gl.glLoadIdentity();
                
                if(ratio >= 1.0f) {
                    gl.glOrtho( -1.0f*ratio, 1.0f*ratio, -1.0f, 1.0f, 1.0f, -1.0f );
                } else {
                    gl.glOrtho( -1.0f, 1.0f, -1.0f/ratio, 1.0f/ratio, 1.0f, -1.0f );
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
            
            @Override
            public void mouseDragged(MouseEvent e)
            {
                if(!brushes.isEmpty()) {
                    final Brush brush = brushes.get(brushes.size()-1);
                    setBrushLocation(brush, e);
                }
            }            

            @Override
            public void mousePressed(MouseEvent e)
            {
                final Brush brush = new Brush();
                setBrushLocation(brush, e);
                
                color %= colors.length;                
                setBrushColor(brush, colors[color]);
                color++;

                brushes.add(brush);
            }
            @Override
            public void mouseReleased(MouseEvent e)
            {
                brushes.clear();
            }
            
            private void setBrushLocation(final Brush brush, final MouseEvent e) {
                
                float width = e.getComponent().getWidth(), height = e.getComponent().getHeight();
                
                if (height <= 0.0f) // avoid a divide by zero error!
                    height = 1.0f;           
                
                final float ratio = width / (float) height;
                
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
        //canvas.addMouseListener(mouseInput);
        //canvas.addMouseMotionListener(mouseInput);
        
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
