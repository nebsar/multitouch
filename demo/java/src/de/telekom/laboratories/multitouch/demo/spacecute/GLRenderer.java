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


/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class GLRenderer {
    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    private Mask mask = new Mask();
    private Background background = new Background();    
    private Title title = new Title();

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializer ">
    
    public GLRenderer() {
        
        final Runnable init = new Runnable() {
            public void run() {
                final boolean fullscreen = false;
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
        
        
        background.render(gl, width, height );
        
        //title.render(gl, width, height);
        
        mask.render(gl, width, height);
        
    }
    
    public static void main(String... args) {
        new GLRenderer();
    }
        
}

// <editor-fold defaultstate="collapsed" desc=" Star ">

class Star {
    
    // <editor-fold defaultstate="collapsed" desc=" Varaibles ">
        
    private int program;
    private int vBuffer;
    private int texture;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">    
    
    public Star() {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    private int arrayBuffer(GL gl) {

        final int vertices = 4;

        final float ext = 0.1f;//1.0f
        
        final FloatBuffer vertexData = ByteBuffer.allocateDirect( 4*(2+2) * vertices ).order(nativeOrder()).asFloatBuffer();
        vertexData.put( 1.0f ).put( 0.0f );
        vertexData.put( +ext ).put( +ext );
        vertexData.put( 0.0f ).put( 0.0f );
        vertexData.put( -ext ).put( +ext );
        vertexData.put( 0.0f ).put( 1.0f );
        vertexData.put( -ext ).put( -ext );
        vertexData.put( 1.0f ).put( 1.0f );
        vertexData.put( +ext ).put( -ext );
        vertexData.rewind();

        final int[] buffers = new int[1];
        gl.glGenBuffers(1, buffers, 0);

        final int vBuffer = buffers[0];
        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
        gl.glBufferData(GL_ARRAY_BUFFER, vertexData.capacity()*4, vertexData, GL_STATIC_DRAW);
        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);

        return vBuffer;
    }    
    
    private int texture(GL gl) {

        final URL texURL = Title.class.getResource("/com/lostgarden/spacecute/background/star.png");

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
    
    private int program(GL gl) {
        final URL vertURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Star.vert" );
        final URL fragURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/spacecute/Star.frag" );
        return GLUtils.program(gl, vertURL, fragURL);
    }    
    
    public void render(GL gl, int width, int height) {
        
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



        final int vertices = 4;

        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vBuffer);
        gl.glTexCoordPointer(2, GL_FLOAT, (2+2)*4, 0);
        gl.glVertexPointer(2, GL_FLOAT, (2+2)*4, 2*4);

        gl.glDrawArrays(GL_TRIANGLE_FAN, 0 , vertices);

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
    
    private Star star = new Star();
    
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">    
    
    public Background() {
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    public void render(GL gl, int width, int height) {
        star.render(gl, width, height);
    }
    
    // </editor-fold> 
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

// <editor-fold defaultstate="collapsed" desc=" GLUtils ">

class GLUtils {
    
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
