/*
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.telekom.laboratories.multitouch.demo.spacecute;

import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import static java.lang.Math.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static java.nio.ByteOrder.*;
import static javax.media.opengl.GL.*;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.texture.Texture;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import javax.media.opengl.GLException;

import net.monoid.util.FPSCounter;
import net.monoid.util.RangeWarp;


/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class GLRenderer {
    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    private Mask mask = new Mask();
    private Background background = new Background();    
    private Ship ship = new Ship();
    private Cute cute = new Cute();
    private Planet planet = new Planet();
    private Title title = new Title();
    
    final Camera cam = new Camera();


    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializer ">
    
    public GLRenderer() {
        
        final Runnable init = new Runnable() {
            public void run() {
                final boolean fullscreen = true;
                final int screen = 0;
                final int width = 768, height = 768;
                
                // <editor-fold defaultstate="collapsed" desc=" FPSCounter ">
                
                final FPSCounter fpsCounter = new FPSCounter();
                
                // </editor-fold>
                
                // <editor-fold defaultstate="collapsed" desc=" OpenGL ">
                
                final GLCapabilities caps = new GLCapabilities();
                caps.setHardwareAccelerated(true);
                caps.setDoubleBuffered(true);
                caps.setSampleBuffers(true);
                
                final GLCanvas canvas = new GLCanvas(caps);
                canvas.setSize(width, height);
                canvas.setPreferredSize(new Dimension(width, height));
                
                final Animator animator = new Animator(canvas);
                
                // <editor-fold defaultstate="collapsed" desc=" EventListener ">
                
                canvas.addGLEventListener(new GLEventListener() {
                    public void init(GLAutoDrawable drawable) {
                        if(!animator.isAnimating()) {
                            animator.start();
                        }
                    }
                    public void display(GLAutoDrawable drawable) {
                        fpsCounter.nextFrame();
                        render(drawable.getGL(), drawable.getWidth(), drawable.getHeight());
                    }
                    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                    }
                    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
                    }
                });
                
                // </editor-fold>
                
                // </editor-fold>
                
                // <editor-fold defaultstate="collapsed" desc=" AWT / SWING ">
                
                final GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
                final GraphicsDevice   graphicsDevice  = graphicsDevices[max(0, min(screen, graphicsDevices.length))];
                
                final Frame frame = new Frame("Spacecute", graphicsDevice.getDefaultConfiguration());
                frame.setSize(width, height);
                
                frame.add(canvas);
                //frame.getContentPane().add(canvas); <-- swing
                
                // <editor-fold defaultstate="collapsed" desc=" Window-Sate ">
                
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        
                        if(animator.isAnimating()) {
                            animator.stop();
                        }
                        
                        frame.dispose();
                        
                        //TODO:  change to Application.close();
                        System.exit(0);
                        
                    }
                });
                
                // </editor-fold>
                
                // <editor-fold defaultstate="collapsed" desc=" FPSCounter.Listener ">
                
                fpsCounter.addFPSCounterListener(new FPSCounter.Listener() {
                    
                    private final String title = frame.getTitle();
                    
                    public void averageFramesElapsed(FPSCounter.Event e) {
                        frame.setTitle(String.format("%s  %f(AVG) %f(AGG)", title, e.getAverageFps(), e.getAggregateFps()));
                    }
                });
                
                // </editor-fold>
                
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
                
                canvas.addKeyListener(new KeyListener() {
                    public void keyPressed(KeyEvent e) {
                        if(e.getKeyCode() == KeyEvent.VK_LEFT){
                            cam.setX( cam.getX() - 0.0075f);
                        }
                        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                            cam.setX( cam.getX() + 0.0075f);
                        }
                        if(e.getKeyCode() == KeyEvent.VK_UP){
                            cam.setY( cam.getY() + 0.0075f);
                        }
                        if(e.getKeyCode() == KeyEvent.VK_DOWN){
                            cam.setY( cam.getY() - 0.0075f);
                        }
                        if(e.getKeyCode() == KeyEvent.VK_PAGE_UP){
                            cam.setZoom( cam.getZoom() + 0.0075f);
                        }
                        if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN){
                            cam.setZoom( cam.getZoom() - 0.0075f);
                        }
                        if(e.getKeyCode() == KeyEvent.VK_INSERT){
                            cam.setOrientation( cam.getOrientation() + 0.002f);
                        }
                        if(e.getKeyCode() == KeyEvent.VK_DELETE){
                            cam.setOrientation( cam.getOrientation() - 0.002f);
                        }
                    }
                    public void keyReleased(KeyEvent e) {
                    }
                    public void keyTyped(KeyEvent e) {
                    }
                });
                
                // </editor-fold>
            }
        };
        
        if(EventQueue.isDispatchThread()) {
            init.run();
        } else {
            try {
                EventQueue.invokeAndWait(init);
            } catch(Exception e) {
                throw new RuntimeException("Could not initialize graphcis", e);
            }
        }
    }
    
    // </editor-fold>
            
    private void render(GL gl, int width, int height) {
        
        // <editor-fold defaultstate="collapsed" desc=" square viewport ">
        
        final int x = 0, y = 0;
        
        width  = max(1, width);
        height = max(1, height);
        
        if(width >= height) {
            final int space = (width - height) / 2;
            gl.glViewport(x+space, y, height, height);
        } else {
            final int space = (height - width) / 2;
            gl.glViewport(x, y+space, width, width);
        }
        
        // </editor-fold>
        
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glShadeModel(GL_SMOOTH);
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //gl.glClearDepth(1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        
        gl.glColor4f(101/255f, 139/255f, 169/255f, 1.0f);
        gl.glBegin(GL_TRIANGLE_FAN);
        gl.glVertex2f( +1.0f, +1.0f );
        gl.glVertex2f( -1.0f, +1.0f );
        gl.glVertex2f( -1.0f, -1.0f );
        gl.glVertex2f( +1.0f, -1.0f );
        gl.glEnd();
        
                
        objects(gl, width, height);
        
        mask.render(gl, width, height);

    }
    
    private void objects(GL gl, int width, int height) {
                
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glPushMatrix();
        
        
        final float[] viewMatrix  = new float[16];
        final float[] modelMatrix = new float[16];
        
        {            
            final float x = -cam.getX(), y = -cam.getY();
            final float orientation = -cam.getOrientation() * (float) PI;
            final float zoom = (float) new RangeWarp(-1.0f, 1.0f).warpTo(cam.getZoom(), 0.25f, 1.0f);
            
            gl.glLoadIdentity(); 
            
            gl.glTranslatef( x, y, 0.0f);
            gl.glRotatef(orientation / (float)PI * 180.0f, 0.0f, 0.0f, 1.0f);            
            gl.glScalef(zoom, zoom, 1.0f);  
            gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, viewMatrix, 0);
            
        }
        
        background.render(gl, new Transform(viewMatrix) );

        
        
        
       

//        final float[] m = new float[16];
//        Transform t = new Transform(m);
//        gl.glMatrixMode(GL_MODELVIEW);
//        gl.glLoadIdentity();        
//        gl.glTranslatef(1,2,0);
//        gl.glRotatef(-90, 0,0,1);
//        gl.glScalef(4, 5, 0);
//        gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, m, 0);
//        System.out.println( t.getRotation() );
        
        
        
        
        
                
//        planet.render(gl, GLGameUtils.transform(x=+0.5f, y=-0.0f, orientation=+0.0f),
//                          GLGameUtils.transform(x=+0.5f, y=-0.0f, orientation=+0.37f),
//                            GLGameUtils.transform(x=+0.5f, y=-0.0f, orientation=+0.75f),
//                            GLGameUtils.transform(x=+0.5f, y=-0.0f, orientation=+1.12f),
//                            GLGameUtils.transform(x=+0.5f, y=-0.0f, orientation=+1.57f));
//        
//        ship.render(gl, GLGameUtils.transform(x=y=-0.4f,y,orientation=-1.0f));
//        
//        cute.render(gl, GLGameUtils.transform(x=y=+0.5f,y, orientation=0.0f),
//                        GLGameUtils.transform(x=-0.5f,  y, orientation=0.1f));
        
        gl.glPopMatrix();
        
        //title.render(gl, width, height);                        
    }
    
    public static void main(String... args) {
        new GLRenderer();
    }
        
}


// <editor-fold defaultstate="collapsed" desc=" Cute ">

class Cute {
    
    // <editor-fold defaultstate="collapsed" desc=" Varaibles ">
        
    public int cute = 0;
    private final float[] transform = new float[16];
    

    private int program, transformLOC; //positionLOC, orientationLOC;
    private int vBuffer;
    private final int[] textures = new int[6];
    
    private final String[] cuties = { "SuseSaturn", "MilaMercury", "JuleJupiter", "PiyaPluto", "VeraVenus", "MikaMars" };        
    private final float ext = 0.1f, ratio = 0.260f / 0.295f;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">    
    
    public Cute() {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    private int arrayBuffer(GL gl) {       
        final float extX = ext*ratio, extY = ext;        
        return GLGameUtils.rectangleArrayBuffer(gl, extX, extY);
    }    
    
    private int texture(GL gl, int cute) {        
        final URL texURL = Title.class.getResource("/com/lostgarden/spacecute/cuties/" + cuties[cute] + ".png");
        return GLGameUtils.loadTexture(gl, texURL, true, "png");
    }
    
    private int program(GL gl) {
        final URL vertURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Cute.vert" );
        final URL fragURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Cute.frag" );
        return GLUtils.program(gl, vertURL, fragURL);
    }    
    
    public void render(GL gl, Transform... instances) {        
               
        // <editor-fold defaultstate="collapsed" desc=" Init ">
        
        if(!gl.glIsTexture(textures[cute])) {
            textures[cute] = texture(gl, cute);
        }
        
        if(!gl.glIsProgram(program)) {
            program = program(gl);
            gl.glUseProgram(program);
            {
                final int texLOC = gl.glGetUniformLocation( program, "texture" );
                if(texLOC >= 0) {
                    final int texUnit = 0;
                    gl.glUniform1i(texLOC, 0); // Texture-Unit: 0
                }
                //positionLOC = gl.glGetUniformLocation( program, "position" );
                //orientationLOC = gl.glGetUniformLocation( program, "orientation" );
                transformLOC = gl.glGetUniformLocation( program, "transform" );
            }
            gl.glUseProgram(GL_NONE);
        }  
        
        if(!gl.glIsBuffer(vBuffer)) {
            vBuffer = arrayBuffer(gl);
        }       
        
        // </editor-fold>        
        

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendEquation(GL_ADD);

        gl.glUseProgram( program );
        
        final int texture = textures[cute];
        final int vertices = 4;

        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
        gl.glTexCoordPointer(2, GL_FLOAT, (2+2)*4, 0);
        gl.glVertexPointer(2, GL_FLOAT, (2+2)*4, 2*4);

        
        for(Transform instance : instances) {
            instance.to(transform);
            if(transformLOC >= 0) {
                gl.glUniformMatrix4fv(transformLOC, 1, true, transform, 0);
            }
            //gl.glActiveTexture(GL_TEXTURE0); // Texture-Unit: 0
            gl.glBindTexture(GL_TEXTURE_2D, texture);

            gl.glDrawArrays(GL_TRIANGLE_FAN, 0 , vertices);
        }
        
        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL_VERTEX_ARRAY);

        gl.glBindTexture(GL_TEXTURE_2D, GL_NONE);


        gl.glUseProgram( GL_NONE );

        gl.glDisable(GL_BLEND);        
        
    }
    
    // </editor-fold> 
}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" Ship ">

class Ship {
    
    // <editor-fold defaultstate="collapsed" desc=" Varaibles ">
        
    public int ship = 0;
    
    private final float[] transform = new float[16];
    private float[] scale = new float[2];

    
    private int program, transformLOC;
    private int[] vBuffers = new int [3];
    private final int[][] textures = new int[3][3];
    
    // rocket, beetle, octopus
    private final String[] ships = { "rocket", "beetle", "octopus" };    
    private final float[] ext    = {     0.086f,     0.1f,     0.1f  };
    private final float[] ratio  = { 0.719f / 0.238f, 0.465f / 0.271f, 0.547f / 0.249f };
    
    private final String[] types = { "Still", "Moving", "Shadow" };

    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">    
    
    public Ship() {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    private int arrayBuffer(GL gl, int ship) {
        final float ext = this.ext[ship];
        final float extX = ext*ratio[ship], extY = ext;
        return GLGameUtils.rectangleArrayBuffer(gl, extX, extY);
    }    
    
    private int texture(GL gl, int ship, int type) {        
        final URL texURL = Title.class.getResource("/com/lostgarden/spacecute/ships/" + ships[ship] + "/" + types[type] + ".png");
        return GLGameUtils.loadTexture(gl, texURL, true, "png");
    }
    
    private int program(GL gl) {
        final URL vertURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Ship.vert" );
        final URL fragURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Ship.frag" );
        return GLUtils.program(gl, vertURL, fragURL);
    }    
    
    public void render(GL gl, Transform... instances) {        

        final int[] shipTextures = textures[ship];        
        
        // <editor-fold defaultstate="collapsed" desc=" Init ">
        
        for(int type=0; type<shipTextures.length; type++) {            
            if(!gl.glIsTexture(shipTextures[type])) {
                shipTextures[type] = texture(gl, ship, type);
            }
        }
        
        if(!gl.glIsProgram(program)) {
            program = program(gl);
            gl.glUseProgram(program);
            {
                final int texLOC = gl.glGetUniformLocation( program, "texture" );
                if(texLOC >= 0) {
                    final int texUnit = 0;
                    gl.glUniform1i(texLOC, 0); // Texture-Unit: 0
                }                
                transformLOC = gl.glGetUniformLocation( program, "transform" );
            }
            gl.glUseProgram(GL_NONE);
        }  
        
        if(!gl.glIsBuffer(vBuffers[ship])) {
            vBuffers[ship] = arrayBuffer(gl, ship);
        }       
        
        // </editor-fold>        
        
        final int vBuffer = vBuffers[ship];

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendEquation(GL_ADD);



        gl.glUseProgram( program );

        final int vertices = 4;

        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
        gl.glTexCoordPointer(2, GL_FLOAT, (2+2)*4, 0);
        gl.glVertexPointer(2, GL_FLOAT, (2+2)*4, 2*4);

            
        for(Transform instance : instances) {
            instance.to(transform);
            instance.getScale(scale);
            
            { // first pass: shadow
                if(transformLOC >= 0) {
                    gl.glMatrixMode(GL_MODELVIEW);
                    gl.glPushMatrix();
                    gl.glLoadIdentity();
                    gl.glTranslatef(0.0f*scale[0], -0.05f*scale[1], 0.0f); // shadow
                    gl.glMultTransposeMatrixf(transform, 0);
                    gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, transform, 0);
                    gl.glPopMatrix();                    
                    gl.glUniformMatrix4fv(transformLOC, 1, true, transform, 0);
                }        

                //gl.glActiveTexture(GL_TEXTURE0); // Texture-Unit: 0
                gl.glBindTexture(GL_TEXTURE_2D, shipTextures[2]);

                gl.glDrawArrays(GL_TRIANGLE_FAN, 0 , vertices);
            }

            instance.to(transform);
            { // second pass: ship (still or moving                
                if(transformLOC >= 0) {
                    gl.glUniformMatrix4fv(transformLOC, 1, true, transform, 0);
                }          

                //gl.glActiveTexture(GL_TEXTURE0); // Texture-Unit: 0
                gl.glBindTexture(GL_TEXTURE_2D, shipTextures[0]);

                gl.glDrawArrays(GL_TRIANGLE_FAN, 0 , vertices);
            }
        }
        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL_VERTEX_ARRAY);

        gl.glBindTexture(GL_TEXTURE_2D, GL_NONE);

        gl.glUseProgram( GL_NONE );

        gl.glDisable(GL_BLEND);        
        
    }
    
    // </editor-fold> 
}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" Planet ">

class Planet {
    
    // <editor-fold defaultstate="collapsed" desc=" Varaibles ">
        
    private final float[] transform = new float[16];    
    private final float[] scale = new float[2];
    
    private int program, transformLOC;
    private int vBuffer;
    private final int[] textures = new int[2];
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">    
    
    public Planet() {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    private int arrayBuffer(GL gl) {
        final float ext = 0.2f;
        final float extX = ext, extY = ext;
        return GLGameUtils.rectangleArrayBuffer(gl, extX, extY);
    }    
    
    private int texture(GL gl, int type) {
        final String[] types = { "Planet", "Shadow" };        
        final URL texURL = Title.class.getResource("/com/lostgarden/spacecute/planets/green/" + types[type] + ".png");
        return GLGameUtils.loadTexture(gl, texURL, true, "png");
    }
    
    private int program(GL gl) {
        final URL vertURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Planet.vert" );
        final URL fragURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Planet.frag" );
        return GLUtils.program(gl, vertURL, fragURL);
    }
    
    public void render(GL gl, Transform... instances) {
        
        // <editor-fold defaultstate="collapsed" desc=" Init ">
        
        for(int type=0; type<textures.length; type++)  {
            if(!gl.glIsTexture(textures[type])) {
                textures[type] = texture(gl, type);
            }
        }
        
        if(!gl.glIsProgram(program)) {
            program = program(gl);
            gl.glUseProgram(program);
            {
                final int texLOC = gl.glGetUniformLocation( program, "texture" );
                if(texLOC >= 0) {
                    final int texUnit = 0;
                    gl.glUniform1i(texLOC, 0); // Texture-Unit: 0
                }
                transformLOC = gl.glGetUniformLocation( program, "transform" );
            }
            gl.glUseProgram(GL_NONE);
        }  
        
        if(!gl.glIsBuffer(vBuffer)) {
            vBuffer = arrayBuffer(gl);
        }       
        
        // </editor-fold>        
        

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendEquation(GL_ADD);



        gl.glUseProgram( program );
        
        final int vertices = 4;

        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
        gl.glTexCoordPointer(2, GL_FLOAT, (2+2)*4, 0);
        gl.glVertexPointer(2, GL_FLOAT, (2+2)*4, 2*4);

        
        for(Transform instance : instances) {
            
            final float rot = instance.getRotation();
            
            instance.to(transform);            
            instance.getScale(scale);
            { // first pass: shadow
                if(transformLOC >= 0) {
                    gl.glMatrixMode(GL_MODELVIEW);
                    gl.glPushMatrix();
                    gl.glLoadIdentity();                    
                    gl.glTranslatef(0.0f*scale[0], -0.05f*scale[1], 0.0f); // shadow                    
                    gl.glMultTransposeMatrixf(transform, 0);                    
                    gl.glRotatef(180.0f*rot/(float)PI, 0,0,1);                    
                    gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, transform, 0);
                    gl.glPopMatrix();                    
                    gl.glUniformMatrix4fv(transformLOC, 1, true, transform, 0);           
                }      

                //gl.glActiveTexture(GL_TEXTURE0); // Texture-Unit: 0
                gl.glBindTexture(GL_TEXTURE_2D, textures[1]);

                gl.glDrawArrays(GL_TRIANGLE_FAN, 0 , vertices);
            }

            instance.to(transform);            
            { // second pass: ship (still or moving                
                if(transformLOC >= 0) {   
                    gl.glMatrixMode(GL_MODELVIEW);
                    gl.glPushMatrix();                    
                    gl.glLoadIdentity();
                    gl.glMultTransposeMatrixf(transform, 0);                    
                    gl.glRotatef(180.0f*rot/(float)PI, 0,0,1);                    
                    gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, transform, 0);
                    gl.glPopMatrix();
                    gl.glUniformMatrix4fv(transformLOC, 1, true, transform, 0);
                }          

                //gl.glActiveTexture(GL_TEXTURE0); // Texture-Unit: 0
                gl.glBindTexture(GL_TEXTURE_2D, textures[0]);

                gl.glDrawArrays(GL_TRIANGLE_FAN, 0 , vertices);
            }    
        }

        
        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL_VERTEX_ARRAY);

        gl.glBindTexture(GL_TEXTURE_2D, GL_NONE);


        gl.glUseProgram( GL_NONE );

        gl.glDisable(GL_BLEND);        
        
    }
    
    // </editor-fold> 
}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" Star ">

class Star {        
            
    // <editor-fold defaultstate="collapsed" desc=" Varaibles ">
        
    private final float[] transform = new float[16];    
        
    private int program, transformLOC;
    private int vBuffer;
    private int texture;
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">    
    
    public Star() {
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    private int arrayBuffer(GL gl) {

        final float ext = 1.0f;
        final float extX = ext, extY = ext;
        return GLGameUtils.rectangleArrayBuffer(gl, extX, extY);
    }    
    
    private int texture(GL gl) {
        final URL texURL = Title.class.getResource("/com/lostgarden/spacecute/background/" + "Star" + ".png");
        return GLGameUtils.loadTexture(gl, texURL, true, "png");
    }    
    
    private int program(GL gl) {
        final URL vertURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Star.vert" );
        final URL fragURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Star.frag" );
        return GLUtils.program(gl, vertURL, fragURL);
    }    
    
    public void render(GL gl, Transform... instances) {
       // <editor-fold defaultstate="collapsed" desc=" Init ">
        
        if(!gl.glIsTexture(texture)) {
            texture = texture(gl);
        }
        
        if(!gl.glIsProgram(program)) {
            program = program(gl);
            gl.glUseProgram(program);
            {
                final int texLOC = gl.glGetUniformLocation( program, "texture" );
                if(texLOC >= 0) {
                    final int texUnit = 0;
                    gl.glUniform1i(texLOC, 0); // Texture-Unit: 0
                }
                transformLOC = gl.glGetUniformLocation( program, "transform" );
            }
            gl.glUseProgram(GL_NONE);
        }  
        
        if(!gl.glIsBuffer(vBuffer)) {
            vBuffer = arrayBuffer(gl);
        }       
        
        // </editor-fold>        
        

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendEquation(GL_ADD);



        gl.glUseProgram( program );
        
        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
        gl.glTexCoordPointer(2, GL_FLOAT, (2+2)*4, 0);
        gl.glVertexPointer(2, GL_FLOAT, (2+2)*4, 2*4);
                   
        //gl.glActiveTexture(GL_TEXTURE0); // Texture-Unit: 0
        gl.glBindTexture(GL_TEXTURE_2D, texture);

        for(Transform instance : instances) {
            if(transformLOC >= 0) {                    
                instance.to(transform);
                final float rot =instance.getRotation();
                gl.glMatrixMode(GL_MODELVIEW);
                gl.glPushMatrix();                    
                //gl.glLoadIdentity();                
                //gl.glRotatef(180.0f*rot/(float)PI, 0,0,1);
                //gl.glMultTransposeMatrixf(transform, 0);                    
                gl.glLoadTransposeMatrixf(transform, 0);
                gl.glRotatef(180.0f*rot/(float)PI, 0,0,1);
                gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, transform, 0);
                gl.glPopMatrix();
                gl.glUniformMatrix4fv(transformLOC, 1, true, transform, 0);
            }
            gl.glDrawArrays(GL_TRIANGLE_FAN, 0 , 4);
        }
        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL_VERTEX_ARRAY);

        gl.glBindTexture(GL_TEXTURE_2D, GL_NONE);


        gl.glUseProgram( GL_NONE );

        gl.glDisable(GL_BLEND);        
        
    }
    
    // </editor-fold> 
}

// </editor-fold>   


// <editor-fold defaultstate="collapsed" desc=" Background ">

class Background {        
        
    // <editor-fold defaultstate="collapsed" desc=" Varaibles ">
    
    private final Star star = new Star();
    private final float[][] transforms = new float[2][16];
        
    // x,y,size
    final float[][] stars = {
        { +0.0f, +0.0f, 0.1f  },
        { +0.0f, +0.5f, 0.15f  },
        { -0.2f, +0.5f, 0.1f  },
        { +0.0f, -0.5f, 0.1f  },
        { +0.0f, -1.0f, 0.2f  },
        { +0.2f,  1.1f, 0.08f  },
    };        
    
    private final Planet planet = new Planet();
    
    // x,y,size
    final float[][] planets = {
        { +1.0f, +0.0f, 1.0f  },
        { -.5f, -0.2f, 1.0f  },
        { -1.5f, 1.2f, 1.2f  },
    }; 
    
    private final Ship ship = new Ship();
    
    // x,y,size
    final float[][] ships = {
        { -1.0f, -.5f, 0.0f  },
        {  .5f, +0.7f, 1.0f  },
        {  1.5f, 1.8f, -1.2f  },
        { -1.0f, 0.8f, -0.5f  },
        {  1.0f, -1.6f, 0.0f  },
        {  -2.0f, -0.6f, 0.0f  },
    };     
    
    private final Cute cute = new Cute();
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">    
    
    public Background() {
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    // <editor-fold defaultstate="collapsed" desc=" Old ">
    
//    private int arrayBuffer(GL gl) {
//
//        final int X=0, Y=1, SIZE=2;
//
//
//                      
//        
//        this.stars = stars.length;
//        
//        final int vertices = 4*stars.length; 
//        final int vSize = 4*(2+2);//size(float)*(x,y + u,v)
//        
//        final FloatBuffer vertexData = ByteBuffer.allocateDirect( vertices*vSize).order(nativeOrder()).asFloatBuffer();
//
//        // NOTE: hardware instancing with mixed streams  would be nice, 
//        // but only available in D3D yet
//        for(float[] star : stars)
//        {            
//            final float x = star[X], y = star[Y];
//            final float ext = 0.5f*star[SIZE];
//                        
//            vertexData.put( 1.0f  ).put( 0.0f  );
//            vertexData.put( x+ext ).put( y+ext );
//            vertexData.put( 0.0f  ).put( 0.0f  );
//            vertexData.put( x-ext ).put( y+ext );
//            vertexData.put( 0.0f  ).put( 1.0f  );
//            vertexData.put( x-ext ).put( y-ext );
//            vertexData.put( 1.0f  ).put( 1.0f  );
//            vertexData.put( x+ext ).put( y-ext );
//        }
//        
//        vertexData.rewind();
//
//        final int[] buffers = new int[1];
//        gl.glGenBuffers(1, buffers, 0);
//
//        final int vBuffer = buffers[0];
//        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
//        gl.glBufferData(GL_ARRAY_BUFFER, vertexData.capacity()*4, vertexData, GL_STATIC_DRAW);
//        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);
//
//        return vBuffer;
//    }    
//    
//    private int texture(GL gl) {
//        final URL texURL = Title.class.getResource("/com/lostgarden/spacecute/background/" + "Star" + ".png");
//        return GLGameUtils.loadTexture(gl, texURL, true, "png");
//    }    
//    
//    private int program(GL gl) {
//        final URL vertURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Star.vert" );
//        final URL fragURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Star.frag" );
//        return GLUtils.program(gl, vertURL, fragURL);
//    }    
    
    // </editor-fold>
    
    public void render(GL gl, Transform instance) {
       // <editor-fold defaultstate="collapsed" desc=" Init ">
        
//        if(!gl.glIsTexture(texture)) {
//            texture = texture(gl);
//        }
//        
//        if(!gl.glIsProgram(program)) {
//            program = program(gl);
//            gl.glUseProgram(program);
//            {
//                final int texLOC = gl.glGetUniformLocation( program, "texture" );
//                if(texLOC >= 0) {
//                    final int texUnit = 0;
//                    gl.glUniform1i(texLOC, 0); // Texture-Unit: 0
//                }
//                transformLOC = gl.glGetUniformLocation( program, "transform" );
//            }
//            gl.glUseProgram(GL_NONE);
//        }  
//        
//        if(!gl.glIsBuffer(vBuffer)) {
//            vBuffer = arrayBuffer(gl);
//        }       
//        
        // </editor-fold>        
        
        // <editor-fold defaultstate="collapsed" desc=" Old ">
//        gl.glEnable(GL_BLEND);
//        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        gl.glBlendEquation(GL_ADD);
//
//
//
//        gl.glUseProgram( program );
//        
//        gl.glEnableClientState(GL_VERTEX_ARRAY);
//        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
//
//        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
//        gl.glTexCoordPointer(2, GL_FLOAT, (2+2)*4, 0);
//        gl.glVertexPointer(2, GL_FLOAT, (2+2)*4, 2*4);
//                   
//
//        {
//            instance.to(transform);
//            
//            if(transformLOC >= 0) {                    
//                gl.glUniformMatrix4fv(transformLOC, 1, true, transform, 0);
//            }          
//
//            //gl.glActiveTexture(GL_TEXTURE0); // Texture-Unit: 0
//            gl.glBindTexture(GL_TEXTURE_2D, texture);
//
//            for(int i=0; i<stars; i++) {
//                gl.glDrawArrays(GL_TRIANGLE_FAN, 4*i , 4);
//            }
//        }    
//
//        
//        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);
//
//        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
//        gl.glDisableClientState(GL_VERTEX_ARRAY);
//
//        gl.glBindTexture(GL_TEXTURE_2D, GL_NONE);
//
//
//        gl.glUseProgram( GL_NONE );
//
//        gl.glDisable(GL_BLEND);        
                
        // </editor-fold>        
        
        final int X=0, Y=1, SIZE=2;
        
        instance.to(transforms[0]);
        
        for(float[] s : stars) {
            final float x = s[X], y=s[Y];
            final float size = s[SIZE];    
            
            final Transform t = new Transform(this.transforms[1]).from(x,y,size,size);
            gl.glMatrixMode(GL_MODELVIEW);
            gl.glPushMatrix();
            gl.glLoadTransposeMatrixf(transforms[0], 0);
            gl.glMultTransposeMatrixf(transforms[1], 0);
            gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, transforms[1], 0);
            gl.glPopMatrix();
            star.render(gl, t);

        }
        
        for(float[] p : planets) {
            final float x = p[X], y=p[Y];
            final float size = p[SIZE];    
            
            final Transform t = new Transform(this.transforms[1]).from(x,y,size,size);
            gl.glMatrixMode(GL_MODELVIEW);
            gl.glPushMatrix();
            gl.glLoadTransposeMatrixf(transforms[0], 0);
            gl.glMultTransposeMatrixf(transforms[1], 0);
            gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, transforms[1], 0);
            gl.glPopMatrix();
            planet.render(gl, t);

        }     
        
        for(int i=0; i<ships.length ; i++) {
            final float x = ships[i][X], y=ships[i][Y];
            final float rot = ships[i][SIZE];   
            
            final Transform t = new Transform(this.transforms[1]).from(x,y,rot);
            gl.glMatrixMode(GL_MODELVIEW);
            gl.glPushMatrix();
            gl.glLoadTransposeMatrixf(transforms[0], 0);
            gl.glMultTransposeMatrixf(transforms[1], 0);
            gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, transforms[1], 0);
            ship.ship = i % 3;
            ship.render(gl, t);
            

            if(ship.ship == 0) {
                gl.glTranslatef(-0.0449f,0.11f,0.0f);
            } else if(ship.ship == 1) {
                gl.glTranslatef(-0.002f,0.148f,0.0f);
            } else {
                gl.glTranslatef(0.05f,0.13f,0.0f);
            }
            
            gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, transforms[1], 0);
            cute.cute = i % 6;
            
            cute.render(gl, t);
            gl.glPopMatrix();
            
        }
        
    }
    
    // </editor-fold> 
}

// </editor-fold>   

// <editor-fold defaultstate="collapsed" desc=" Camera ">

class Camera {
    
    private float x, y, orientation, zoom;
    
    Camera() {}
    Camera(float x, float y, float orientation, float zoom) {
        this.setX(x); this.setY(y);
        this.setOrientation(orientation);
        this.setZoom(zoom);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;//max(-1.0f, min(x, 1.0f) );
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;//max(-1.0f, min(y, 1.0f) );
    }

    public float getOrientation() {
        return orientation;
    }

    public void setOrientation(float orientation) {
        this.orientation = max(-1.0f, min(orientation, 1.0f) );
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = max(-1.0f, min(zoom, 1.0f) );
    }
    
}

// </editor-fold>  

// <editor-fold defaultstate="collapsed" desc=" Title ">

class Title {

    // <editor-fold defaultstate="collapsed" desc=" Varaibles ">
    
    private final int steps = 32;
    private final float min = 0.4f*0.258f, max = 0.4f*0.875f;//, closed = 0.5f;

    private int program;
    private int vBuffer;
    private int texture;
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Initializers ">    
    
    public Title() {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">

    private int texture(GL gl) {

        final URL texURL = Title.class.getResource("/com/lostgarden/spacecute/title.png");

        try {
              final Texture tex = TextureIO.newTexture( texURL, false, "png" );
              tex.setTexParameteri(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
              tex.setTexParameteri(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
              tex.setTexParameteri(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
              tex.setTexParameteri(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
              return tex.getTextureObject();
            
        } catch(IOException ioe) {
            throw new GLException(String.format("Error loading: %s", texURL.toExternalForm()), ioe);
        }
    }

    private int arrayBuffer(GL gl) {

        final int outer = 4;
        final int inner = 4*steps;

        final int vertices = (2*steps+1)*2;

        final FloatBuffer vertexData = ByteBuffer.allocateDirect( 4*(2+2) * vertices ).order(nativeOrder()).asFloatBuffer();
        for(int i=-steps; i<=steps; i++) {
            //final float text = 1.0f - (i+steps)/(float)(2*steps);
            //final float value = closed*(float)Math.PI*(i/(float)steps);
            //final float x = +(float) cos(value+(float)Math.PI/2);
            //final float y = -(float) sin(value+(float)Math.PI/2);

            //vertexData.put( text  ).put( 0.0f  );
            //vertexData.put( x*min ).put( y*min );
            //vertexData.put( text  ).put( 1.0f  );
            //vertexData.put( x*max ).put( y*max );



            final float text = (i+steps)/(float)(2*steps);
            final float x = max*(i)/(float)(steps);

            vertexData.put( text ).put( 1.0f );
            vertexData.put( x    ).put( -min );
            vertexData.put( text ).put( 0.0f );
            vertexData.put( x    ).put(  min );
        }
        vertexData.rewind();

        final int[] buffers = new int[1];
        gl.glGenBuffers(1, buffers, 0);

        final int vBuffer = buffers[0];
        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, vertexData.capacity()*4, vertexData, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

        return vBuffer;
    }

    private int program(GL gl) {
        final URL vertURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Title.vert" );
        final URL fragURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Title.frag" );
        return GLUtils.program(gl, vertURL, fragURL);
    }


    public void render(GL  gl, int width, int height) {

        // <editor-fold defaultstate="collapsed" desc=" Init ">
        
        if(!gl.glIsTexture(texture)) {
            texture = texture(gl);
        }

        if(!gl.glIsProgram(program)) {
            program = program(gl);
            gl.glUseProgram(program);
            {
                final int texLOC = gl.glGetUniformLocation( program, "texture" );
                if(texLOC >= 0) {
                    final int texUnit = 0;
                    gl.glUniform1i(texLOC, 0); // Texture-Unit: 0
                }
            }
            gl.glUseProgram(GL_NONE);
        }

        if(!gl.glIsBuffer(vBuffer)) {
            vBuffer = arrayBuffer(gl);
        }
        
        // </editor-fold>


        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendEquation(GL_ADD);



        gl.glUseProgram( program );

        //gl.glActiveTexture(GL_TEXTURE0); // Texture-Unit: 0
        gl.glBindTexture(GL_TEXTURE_2D, texture);



        final int vertices = (2*steps+1)*2;

        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
        gl.glTexCoordPointer(2, GL_FLOAT, (2+2)*4, 0);
        gl.glVertexPointer(2, GL_FLOAT, (2+2)*4, 2*4);

        gl.glDrawArrays(GL_TRIANGLE_STRIP, 0 , vertices);

        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);


        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL_VERTEX_ARRAY);

        gl.glBindTexture(GL_TEXTURE_2D, GL_NONE);


        gl.glUseProgram( GL_NONE );

        gl.glDisable(GL_BLEND);
    }
    
    // </editor-fold>
}

// </editor-fold>   


// <editor-fold defaultstate="collapsed" desc=" GLGameUtils ">

class GLGameUtils {    
            
    public static Transform transform(float x, float y) {
        return new Transform().from(x, y);
    }

    public static Transform transform(float orientation) {
        return new Transform().from(orientation);
    }

    public static Transform transform(float x, float y, float orientation) {
        return new Transform().from(x, y, orientation);
    }
    
    
    public static int rectangleArrayBuffer(GL gl, float extX, float extY) throws GLException {
        final int vertices = 4;

        final float ext = 0.15f;//1.0f
        
        final FloatBuffer vertexData = ByteBuffer.allocateDirect( 4*(2+2) * vertices ).order(nativeOrder()).asFloatBuffer();
        vertexData.put( 1.0f  ).put( 0.0f  );
        vertexData.put( +extX ).put( +extY );
        vertexData.put( 0.0f  ).put( 0.0f  );
        vertexData.put( -extX ).put( +extY );
        vertexData.put( 0.0f  ).put( 1.0f  );
        vertexData.put( -extX ).put( -extY );
        vertexData.put( 1.0f  ).put( 1.0f  );
        vertexData.put( +extX ).put( -extY );
        vertexData.rewind();
        
        final int[] buffers = new int[1];
        gl.glGenBuffers(1, buffers, 0);

        final int vBuffer = buffers[0];
        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, vertexData.capacity()*4, vertexData, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

        return vBuffer;
    }    
    
    public static int loadTexture(GL gl, URL texURL, boolean mipmap, String fileType) throws GLException{
        try {
              final Texture tex = TextureIO.newTexture( texURL, mipmap, fileType);
              if(mipmap) {
                tex.setTexParameteri(GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
              } else {
                  tex.setTexParameteri(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
              }
              tex.setTexParameteri(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
              tex.setTexParameteri(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
              tex.setTexParameteri(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
              return tex.getTextureObject();
        } catch(IOException ioe) {
            throw new GLException(String.format("Error loading: %s", texURL.toExternalForm()), ioe);
        }        
    }
}

// </editor-fold>


// <editor-fold defaultstate="collapsed" desc=" Mask ">

class Mask {

    // <editor-fold defaultstate="collapsed" desc=" Varaibles ">
    
    private final int steps = 16;
    private final float border = 1.0f, radius = 1.0f;

    private int vBuffer;
    private int iBuffer;
    private int program;
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Initializers ">    
    
    public Mask() {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">

    private int arrayBuffer(GL gl) {

        final int outer = 4;
        final int inner = 4*steps;

        final int vertices = (inner+outer);

        final FloatBuffer vertexData = ByteBuffer.allocateDirect( 4*2 * vertices ).order(nativeOrder()).asFloatBuffer();
        vertexData.put( +border ).put( +border );
        vertexData.put( -border ).put( +border );
        vertexData.put( -border ).put( -border );
        vertexData.put( +border ).put( -border );
        for(int i=0; i<inner; i++) {
            final float value = 2.0f*(float)Math.PI*i/inner;
            vertexData.put( (float) cos(value)*radius ).put( (float) sin(value)*radius );
        }
        vertexData.rewind();

        final int[] buffers = new int[1];
        gl.glGenBuffers(1, buffers, 0);

        final int vBuffer = buffers[0];
        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, vertexData.capacity()*4, vertexData, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

        return vBuffer;
    }

    private int arrayElementBuffer(GL gl) {

        final int outer = 4;
        final int inner = 4*steps;

        final int indices = outer*(2*(steps+1)) + 1;

        final IntBuffer indexData = ByteBuffer.allocateDirect( 4 * indices ).order(nativeOrder()).asIntBuffer();
        for(int j=0; j<outer; j++) {
            for(int i=j*steps; i<=(j+1)*steps; i++) {
                indexData.put( j );
                indexData.put( outer+(i%inner) );
            }
        }
        indexData.put( 0 );
        indexData.rewind();

        final int[] buffers = new int[1];
        gl.glGenBuffers(1, buffers, 0);

        final int iBuffer = buffers[0];
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iBuffer);
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData.capacity()*4, indexData, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_NONE);

        return iBuffer;
    }

    private int program(GL gl) {
        final URL vertURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Mask.vert" );
        final URL fragURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Mask.frag" );
        return GLUtils.program(gl, vertURL, fragURL);
    }

    public void render(GL  gl, int width, int height) {

        // <editor-fold defaultstate="collapsed" desc=" Init ">
        
        if( !gl.glIsProgram( program ) ) {
            program = program(gl);
        }

        if(!gl.glIsBuffer(vBuffer)) {
            vBuffer = arrayBuffer(gl);
        }

        if(!gl.glIsBuffer(iBuffer)) {
            iBuffer = arrayElementBuffer(gl);
        }

        // </editor-fold>

        
        gl.glUseProgram( program );

        final int outer = 4;
        final int inner = 4*steps;

        final int vertices = (inner+outer);
        final int indices = (outer*(2*(steps+1))+1);


        gl.glEnableClientState(GL_VERTEX_ARRAY);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
        gl.glVertexPointer(2, GL_FLOAT, 2*4, 0);
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iBuffer);

        gl.glDrawRangeElements(GL_TRIANGLE_STRIP, 0, vertices, indices, GL_UNSIGNED_INT, 0);

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_NONE);
        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

        gl.glDisableClientState(GL_VERTEX_ARRAY);

        gl.glUseProgram( GL_NONE );
    }

    // </editor-fold>
}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" Transform ">

class Transform {
    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    // row-major    
    private final float[] matrix;
    private final int offset;
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Initializers ">    
    
    public Transform() {
        this.matrix = new float[] {
            1.0f, 0.0f, 0.0f, 0.0f, 
            0.0f, 1.0f, 0.0f, 0.0f, 
            0.0f, 0.0f, 1.0f, 0.0f, 
            0.0f, 0.0f, 0.0f, 1.0f, 
        };
        this.offset = 0;
    }

    public Transform(float[] matrix) {
        this(matrix, 0);
    }
    
    public Transform(float[] matrix, int offset) {
        if(offset < 0 ||matrix.length<offset+16) {
            throw new IllegalArgumentException();
        }
        this.matrix = matrix;
        this.offset = offset;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    public Transform from(float x, float y, float sx, float sy) {
        FloatBuffer.wrap(matrix, offset, 16)
        .put(  sx ). put(  0  ). put( 0 ). put( x )
        .put(  0  ). put(  sy ). put( 0 ). put( y )
        .put(  0  ). put(  0  ). put( 1 ). put( 0 )
        .put(  0  ). put(  0  ). put( 0 ). put( 1 );

        return this;
    }

    
    public Transform from(float x, float y, float orientation, float sx, float sy) {
        float cos = (float) cos(orientation);
        float sin = (float) sin(orientation);

        FloatBuffer.wrap(matrix, offset, 16)
        .put( cos*sx ). put(   sin  ). put( 0 ). put( x )
        .put(  -sin  ). put( cos*sy ). put( 0 ). put( y )
        .put(    0   ). put(    0   ). put( 1 ). put( 0 )
        .put(    0   ). put(    0   ). put( 0 ). put( 1 );

        return this;
    }    
    
    public Transform from(float x, float y, float orientation) {
        float cos = (float) cos(orientation);
        float sin = (float) sin(orientation);

        FloatBuffer.wrap(matrix, offset, 16)
        .put( cos ). put( sin ). put( 0 ). put( x )
        .put(-sin ). put( cos ). put( 0 ). put( y )
        .put(  0  ). put(  0  ). put( 1 ). put( 0 )
        .put(  0  ). put(  0  ). put( 0 ). put( 1 );

        return this;
    }    

    public Transform from(float x, float y) {            
        FloatBuffer.wrap(matrix, offset, 16)
        .put(  1  ). put(  0  ). put( 0 ). put( x )
        .put(  0  ). put(  1  ). put( 0 ). put( y )
        .put(  0  ). put(  0  ). put( 1 ). put( 0 )
        .put(  0  ). put(  0  ). put( 0 ). put( 1 );

        return this;
    }
    
    public Transform from(float orientation) {
        float cos = (float) cos(orientation);
        float sin = (float) sin(orientation);

        FloatBuffer.wrap(matrix, offset, 16)
        .put( cos ). put( sin ). put( 0 ). put( 0 )
        .put(-sin ). put( cos ). put( 0 ). put( 0 )
        .put(  0  ). put(  0  ). put( 1 ). put( 0 )
        .put(  0  ). put(  0  ). put( 0 ). put( 1 );

        return this;
    }
    
    
    public float[] getScale(float[] scale) {        
        final float norm = matrix[offset+15];
        if(abs(norm) < 0.000001f) {
            scale[0] = scale[1] = 0.0f;
            return scale;
        }     
        
        final float invNorm = 1.0f / norm;
        { // x
            final float x = matrix[offset+0], y = matrix[offset+4], z = matrix[offset+12];
            scale[0] = (float)sqrt(x*x + y*y + z*z) * invNorm;
        }        
        { // y
            final float x = matrix[offset+1], y = matrix[offset+5], z = matrix[offset+13];
            scale[1] = (float)sqrt(x*x + y*y + z*z) * invNorm;
        }
        return scale;
    }
    
    public float getRotation() {
        
        // matrix * (0,1,0,0)^T
        final float norm = matrix[offset+12] + matrix[offset+15];
        if(abs(norm) < 0.000001f) {
            return 0.0f;
        }
        
        final float invNorm = 1.0f / norm;
        float tX = matrix[offset+0] * invNorm;
        float tY = matrix[offset+4] * invNorm;
        
        final float lenSQ = tX*tX + tY*tY;
        if(abs(lenSQ) < 0.000001f) {
            return 0.0f;
        }
        
        final float lenInv = 1.0f / (float) sqrt(lenSQ);
        tX *= lenInv;
        tY *= lenInv;       

        return (float) -atan2(tY, tX);
    }
    
    public void to(float[] matrix) {
        to(matrix, 0);
    }
    public void to(float[] matrix, int offset) {
        System.arraycopy(this.matrix, this.offset, matrix, offset, 16);
    }
    
    // </editor-fold>
}

// </editor-fold>

// <editor-fold defaultstate="collapsed" desc=" GLUtils ">

class GLUtils {
    
    public static int rectangleArrayBuffer(GL gl, float extX, float extY) throws GLException {
        final int vertices = 4;

        final float ext = 0.15f;//1.0f
        
        final FloatBuffer vertexData = ByteBuffer.allocateDirect( 4*(2+2) * vertices ).order(nativeOrder()).asFloatBuffer();
        vertexData.put( 1.0f  ).put( 0.0f  );
        vertexData.put( +extX ).put( +extY );
        vertexData.put( 0.0f  ).put( 0.0f  );
        vertexData.put( -extX ).put( +extY );
        vertexData.put( 0.0f  ).put( 1.0f  );
        vertexData.put( -extX ).put( -extY );
        vertexData.put( 1.0f  ).put( 1.0f  );
        vertexData.put( +extX ).put( -extY );
        vertexData.rewind();
        
        final int[] buffers = new int[1];
        gl.glGenBuffers(1, buffers, 0);

        final int vBuffer = buffers[0];
        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, vertexData.capacity()*4, vertexData, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

        return vBuffer;
    }
    
    public static String source(String resource, ClassLoader finder) throws GLException {
        final URL url = finder.getResource(resource);
        if(url == null) {
            throw new GLException( String.format( "Resource not found: %s", resource ) );
        }
        return source( url );
    }
    
    public static String source(URL url) throws GLException {
        
        try {
            final InputStream in = url.openStream();
            
            final StringBuilder sb = new StringBuilder();
            final BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
            
            String line;
            while( (line = reader.readLine()) != null ) {
                sb.append(line);
                sb.append('\n');
            }
            
            in.close();
            
            return sb.toString();
        } catch(IOException e) {
            throw new GLException( String.format( "Could not read resource: %s", url.toString() ) );
        }
    }
    
    public static int program(GL gl, URL vertexShader, URL fragmentShader) throws GLException {
        
        final String vertSource = GLUtils.source( vertexShader );
        final String fragSoruce = GLUtils.source( fragmentShader );
        
        int program = 0, vShader = 0, fShader = 0;
        try {
            vShader = GLUtils.vertexShader( gl,  vertSource);
            fShader = GLUtils.fragmentShader( gl,  fragSoruce);
            
            program = GLUtils.program( gl, vShader, fShader );
            
            // mark for automatic deletion, if program will be deleted
            gl.glDeleteShader( vShader );
            gl.glDeleteShader( fShader );
            
        } catch(GLException glException) {
            
            gl.glDeleteProgram( program );
            vShader = fShader = program = 0;
            throw glException;
        }
        
        return program;
    }
    
    
    public static int program(GL gl, int... shaders) throws GLException {
        final int program = gl.glCreateProgram();
        for(int shader : shaders) {
            gl.glAttachShader(program, shader);
        }
        gl.glLinkProgram(program);
        
        final int[] status = new int[1];
        gl.glGetProgramiv(program, GL_LINK_STATUS, status, 0);
        if(status[0] == GL_FALSE) {
            gl.glGetProgramiv(program, GL_INFO_LOG_LENGTH, status, 0);
            
            final byte[] infoLog = new byte[ status[0] ];
            if(status[0] > 0) {
                gl.glGetProgramInfoLog(program, infoLog.length, status, 0, infoLog, 0 );
            }
            
            final String error = Charset.forName("US-ASCII").decode(ByteBuffer.wrap(infoLog)).toString();
            throw new GLException(error);
        }
        
        return program;
    }
    
    
    public static int vertexShader(GL gl, String source) throws GLException {
        return shader(gl, GL_VERTEX_SHADER, source);
    }
    
    public static int fragmentShader(GL gl, String source) throws GLException {
        return shader(gl, GL_FRAGMENT_SHADER, source);
    }
    
    private static int shader(GL gl, int type, String source) throws GLException {
        
        final int shader = gl.glCreateShader(type);
        gl.glShaderSource(shader, 1, new String[] { source }, new int[] { source.length() }, 0 );
        gl.glCompileShader(shader);
        
        final int[] status = new int[1];
        gl.glGetShaderiv(shader, GL_COMPILE_STATUS, status, 0);
        if(status[0] == GL_FALSE) {
            gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, status, 0);
            
            final byte[] infoLog = new byte[ status[0] ];
            
            if(status[0] > 0) {
                gl.glGetShaderInfoLog(shader, infoLog.length, status, 0, infoLog, 0 );
            }
            
            final String error = Charset.forName("US-ASCII").decode(ByteBuffer.wrap(infoLog)).toString();
            throw new GLException(error);
        }
        
        return shader;
    }        
}

// </editor-fold>
