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


import static java.lang.String.*;
import static java.lang.Math.*;
import static java.nio.ByteOrder.*;
import static javax.media.opengl.GL.*;
import static utils.opengl.ProgramUtils.*;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import javax.media.opengl.GLException;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.util.ArrayList;
import java.util.Collection;
import static java.util.Arrays.asList;
import utils.opengl.Mask;
import utils.opengl.Video;

/**
 * @author Michael Nischt
 * @version 0.1
 */
class GLRenderer {        
    
    // <editor-fold defaultstate="collapsed" desc=" Images ">
    
    final static private class Images 
    {        
        // <editor-fold defaultstate="collapsed" desc=" Variables ">
        
        private static final String[] images = 
        {
            "Play",
            "Preview-Shadow",
            //"Preview"
        };        
        
        // resources
        private int program, buffer;
                
        private final int[] textures = new int[images.length];
                
        // animatables
        private int transformLOC;
        private float[] transform = 
        {
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            0,0,0,1,
        };
                
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Methods ">
        
        public void render(GL gl, Image... instances) {
            render ( gl, asList(instances) );
        }
        public void render(GL gl, Iterable<? extends Image> instances) {
            
            final int vSize  = 4*2; //size(float)*(2)
            final int vCount = 4;
            
            // <editor-fold defaultstate="collapsed" desc=" program ">
            
            if(!gl.glIsProgram(program)) {                
                final Class c = getClass();
                final String base = c.getPackage().getName().replace(".", "/");
                program = program(gl, 
                        c.getResource(format("/%s/Image.vert", base)),
                        c.getResource(format("/%s/Image.frag", base)));
                
                
                transformLOC = gl.glGetUniformLocation( program, "transform" );
                final int textureLOC = gl.glGetUniformLocation( program, "texture" );
                gl.glUseProgram(program);
                if(textureLOC >= 0) 
                {
                    gl.glUniform1i(textureLOC, 0);
                }
                gl.glUseProgram(GL_NONE);
            }
            
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc=" buffer ">

            if(!gl.glIsBuffer(buffer)) {
                final float ext = 1.0f;
                
                final FloatBuffer vertexData = ByteBuffer.allocateDirect( vSize * vCount ).order(nativeOrder()).asFloatBuffer();
                vertexData.put( +ext ).put( +ext );
                vertexData.put( -ext ).put( +ext );
                vertexData.put( -ext ).put( -ext );
                vertexData.put( +ext ).put( -ext );
                vertexData.rewind();

                final int[] buffers = new int[1];
                gl.glGenBuffers(1, buffers, 0);

                buffer = buffers[0];
                gl.glBindBuffer(GL_ARRAY_BUFFER, buffer);
                gl.glBufferData(GL_ARRAY_BUFFER, vertexData.capacity()*4, vertexData, GL_STATIC_DRAW);
                gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);
            }
            
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc=" textures ">

            for(int i=0; i<textures.length; i++) {                
                if(!gl.glIsTexture(textures[i])) {                    
                    final String path = this.getClass().getPackage().getName().replace(".", "/");
                    final URL texURL = getClass().getResource(format("/%s/%s.tga", path, images[i]));
                    try {
                          final boolean mipmap = true;
                          final Texture tex = TextureIO.newTexture( texURL, mipmap, "tga" );
                          tex.setTexParameteri(GL_TEXTURE_MIN_FILTER, mipmap ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR);
                          tex.setTexParameteri(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                          tex.setTexParameteri(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                          tex.setTexParameteri(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                          textures[i] = tex.getTextureObject();
                          
                          
                    } catch(IOException ioe) {
                        throw new GLException(String.format("Error loading: %s", texURL.toExternalForm()), ioe);
                    }
                }
            }
            
            // </editor-fold>                                  
            
            gl.glUseProgram(program);                               
            
            gl.glEnableClientState(GL_VERTEX_ARRAY);
            gl.glBindBuffer(GL_ARRAY_BUFFER, buffer);
            gl.glVertexPointer(2, GL_FLOAT, vSize, 0);            
            
            // <editor-fold defaultstate="collapsed" desc=" instances ">
            
            for(Image instance : instances) 
            {                
                final int content = instance.getContent();
                final int texture = (content >= 0) ? (content % (this.textures.length-1) + 1) : 0;
                
                gl.glMatrixMode(GL_MODELVIEW);
                gl.glLoadIdentity();
                if(textures.length > 0) {
                    gl.glTranslated(instance.getCenterX(), instance.getCenterY(), 0.0);
                    gl.glRotated(instance.getOrientation() * 180.0, 0.0, 0.0, 1.0);
                    gl.glScaled(instance.getExtentX(), instance.getExtentY(), 1.0);
                }
                gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, transform, 0);
                                
                if(textures.length > 0) {
                    gl.glBindTexture(GL_TEXTURE_2D, textures[texture]);
                }
                if(transformLOC >= 0) 
                {                    
                    final boolean transpose = true;
                    gl.glUniformMatrix4fv(transformLOC, 1, transpose, transform, 0);
                }                
                gl.glDrawArrays(GL_TRIANGLE_FAN, 0 , vCount);
                                
            }
            
            // </editor-fold>
            
            gl.glBindTexture(GL_TEXTURE_2D, 0);            
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);            
            gl.glDisableClientState(GL_VERTEX_ARRAY_POINTER);            
            
            gl.glUseProgram(0);        
        }
        
        // </editor-fold>
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Touches ">
    
    final static private class Touches 
    {        
        // <editor-fold defaultstate="collapsed" desc=" Variables ">
        
        // resources
        private int program, buffer;
                                
        // animatables
        private int transformLOC;
        private float[] transform = 
        {
            1,0,0,0,
            0,1,0,0,
            0,0,1,0,
            0,0,0,1,
        };
                
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Methods ">
        
        public void render(GL gl, Touch... instances) {
            render ( gl, asList(instances) );
        }
        public void render(GL gl, Iterable<? extends Touch> instances) {
            
            final int vSize  = 4*2; //size(float)*(2)
            final int vCount = 4*8+2;
            
            // <editor-fold defaultstate="collapsed" desc=" program ">
            
            if(!gl.glIsProgram(program)) {                
                final Class c = getClass();
                final String base = c.getPackage().getName().replace(".", "/");
                program = program(gl, 
                        c.getResource(format("/%s/Touch.vert", base)),
                        c.getResource(format("/%s/Touch.frag", base)));
                
                
                transformLOC = gl.glGetUniformLocation( program, "transform" );
                final int textureLOC = gl.glGetUniformLocation( program, "texture" );
            }
            
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc=" buffer ">

            if(!gl.glIsBuffer(buffer)) {
                
                final FloatBuffer vertexData = ByteBuffer.allocateDirect( vSize * vCount ).order(nativeOrder()).asFloatBuffer();
                
                vertexData.put( 0.0f ).put( 0.0f );
                final int steps = vCount-2;
                for(int i=0; i<=steps; i++)
                {
                    final float angle = 2.0f * ((float) Math.PI) * i / (float)steps;
                    
                    vertexData.put( (float) Math.cos( angle ) ).put( (float) Math.sin( angle ) );
                }
                vertexData.rewind();

                final int[] buffers = new int[1];
                gl.glGenBuffers(1, buffers, 0);

                buffer = buffers[0];
                gl.glBindBuffer(GL_ARRAY_BUFFER, buffer);
                gl.glBufferData(GL_ARRAY_BUFFER, vertexData.capacity()*4, vertexData, GL_STATIC_DRAW);
                gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);
            }
            
            // </editor-fold>
                        
            gl.glUseProgram(program);                               
            
            gl.glEnableClientState(GL_VERTEX_ARRAY);
            gl.glBindBuffer(GL_ARRAY_BUFFER, buffer);
            gl.glVertexPointer(2, GL_FLOAT, vSize, 0);            
            
            // <editor-fold defaultstate="collapsed" desc=" instances ">
            
            for(Touch instance : instances) 
            {                
                gl.glMatrixMode(GL_MODELVIEW);
                gl.glLoadIdentity();

                gl.glTranslated(instance.getX(), instance.getY(), 0.0);
                gl.glScaled(0.025,0.025, 1.0);
                gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, transform, 0);
                                
                if(transformLOC >= 0) 
                {                    
                    final boolean transpose = true;
                    gl.glUniformMatrix4fv(transformLOC, 1, transpose, transform, 0);
                }                
                gl.glDrawArrays(GL_TRIANGLE_FAN, 0 , vCount);
                                
            }
            
            // </editor-fold>
            
            gl.glBindTexture(GL_TEXTURE_2D, 0);            
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);            
            gl.glDisableClientState(GL_VERTEX_ARRAY_POINTER);            
            
            gl.glUseProgram(0);        
        }
        
        // </editor-fold>
    }
    
    // </editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    final private Images  images  = new Images();    
    final private Touches touches = new Touches();
    final private Mask mask = new Mask(0.0f);
    final private Video video = new Video(768, 768, Video.Format.LUMINANCE);    
    
    final private Scene scene;
    
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Initializers ">
    
    GLRenderer(Scene scene) {
        if(scene == null) throw new NullPointerException();
        
        this.scene = scene;
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    //@SuppressWarnings("unchecked")
    public void render(GL gl, int width, int height) {                
        
        final Collection<Image> imageList = new ArrayList<Image>();
        final Collection<Touch> touchList = new ArrayList<Touch>();
                
        imageList.add(new Image(-1, 0.2, 0.2));        
        {
            final Scene.Content content = new Scene.Content()
            {
                public void addImage(Image image)
                {
                    if(image != null) {
                        imageList.add(image);
                    }
                }
                public void addTouch(Touch touch)
                {
                    if(touch != null) {
                        touchList.add(touch);
                    }
                }                
            };
            
            scene.view(content);
        }        
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glShadeModel(GL_SMOOTH);
                
        gl.glClearColor(mask.getRed(), mask.getGreen(), mask.getBlue(), mask.getAlpha());
        gl.glClearDepth(1.0f);
        
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);        
                        
        
        // <editor-fold defaultstate="collapsed" desc=" square viewport ">
        {
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
        }        
        // </editor-fold>

        
        gl.glDepthMask(true);        
        
        final boolean showVideo = false;
        if(showVideo)
        {
            video.render(gl);        
        }
        else
        {   // optional
            gl.glMatrixMode(GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glMatrixMode(GL_MODELVIEW);
            gl.glLoadIdentity();
        
            gl.glColor4f(0f, 0f, 0f, 1.0f);
            gl.glBegin(GL_TRIANGLE_FAN);
            gl.glVertex2f( +1.0f, +1.0f );
            gl.glVertex2f( -1.0f, +1.0f );
            gl.glVertex2f( -1.0f, -1.0f );
            gl.glVertex2f( +1.0f, -1.0f );
            gl.glEnd();
        }        
        
        // <editor-fold defaultstate="collapsed" desc=" images ">
        
        gl.glDepthMask(false);
        gl.glEnable(GL_DEPTH_TEST);
        
        gl.glEnable(GL_BLEND);
        gl.glBlendEquation(GL_FUNC_ADD);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            
        images .render (gl, imageList);
        touches.render (gl, touchList);        
        
        gl.glDisable(GL_DEPTH_TEST);
        gl.glDisable(GL_BLEND);
        
        // </editor-fold>
        
        gl.glDepthMask(true);
        
        mask.render(gl);
    }
    
    // </editor-fold>
}
