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

package de.telekom.laboratories.multitouch.demo.gallery;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import static java.lang.Math.*;
import static java.lang.String.*;
import static java.nio.ByteOrder.*;
import static javax.media.opengl.GL.*;


import javax.media.opengl.GL;
import de.telekom.laboratories.multitouch.demo.opengl.Mask;
import static de.telekom.laboratories.multitouch.demo.opengl.ProgramUtils.*;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.media.opengl.GLException;

/**
 * @author Michael Nischt
 * @version 0.1
 */
public class GLGallery 
{

    // <editor-fold defaultstate="collapsed" desc=" Play ">
    
    private static class Play {
        
        // <editor-fold defaultstate="collapsed" desc=" Variables ">        
        
        // resources
        private int program, buffer;
                
        private int texture;
        private final float[] size = { 0.15f, 0.15f };
                
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
        
        public void render(GL gl) {
            
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
            
            // <editor-fold defaultstate="collapsed" desc=" texture ">

            if(!gl.glIsTexture(texture)) {

                final String path = this.getClass().getPackage().getName().replace(".", "/");
                final URL texURL = getClass().getResource(format("/%s/Play.tga", path));
                try {
                      final boolean mipmap = true;
                      final Texture tex = TextureIO.newTexture( texURL, mipmap, "tga" );
                      tex.setTexParameteri(GL_TEXTURE_MIN_FILTER, mipmap ? GL_LINEAR_MIPMAP_LINEAR : GL_LINEAR);                      
                      tex.setTexParameteri(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                      tex.setTexParameteri(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                      tex.setTexParameteri(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

                      texture = tex.getTextureObject();

                } catch(IOException ioe) {
                    throw new GLException(String.format("Error loading: %s", texURL.toExternalForm()), ioe);
                }
            }
            
            // </editor-fold>
                                                
            gl.glUseProgram(program);
            
            gl.glEnable(GL_BLEND);
            gl.glBlendEquation(GL_FUNC_ADD);
            gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            
            gl.glEnableClientState(GL_VERTEX_ARRAY);                                    
            gl.glBindBuffer(GL_ARRAY_BUFFER, buffer);
            gl.glVertexPointer(2, GL_FLOAT, vSize, 0);            
            
            gl.glBindTexture(GL_TEXTURE_2D, texture);
                            
            {
                gl.glMatrixMode(GL_MODELVIEW);
                gl.glLoadIdentity();
                gl.glScalef(size[0], size[1], 1.0f);
                gl.glGetFloatv(GL_TRANSPOSE_MODELVIEW_MATRIX, transform, 0);
            }

            if(transformLOC >= 0) 
            {                    
                final boolean transpose = true;
                gl.glUniformMatrix4fv(transformLOC, 1, transpose, transform, 0);
            }                
            gl.glDrawArrays(GL_TRIANGLE_FAN, 0 , vCount);
                                            
            // </editor-fold>
            
            gl.glBindTexture(GL_TEXTURE_2D, 0);            
            gl.glBindBuffer(GL_ARRAY_BUFFER, 0);            
            gl.glDisableClientState(GL_VERTEX_ARRAY_POINTER);            
            
            gl.glUseProgram(0);         
            
            gl.glDisable(GL_BLEND);
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Preview ">
    
    private static class Preview {
        
        // <editor-fold defaultstate="collapsed" desc=" Variables ">
        
        private static final String[] images = 
        {
            "Preview-Shadow",
            "Preview"
        };        
        
        // resources
        private int program, buffer;
                
        private final int[] textures = new int[images.length];
        private final float[][] sizes = 
        {
            { 1.38f*0.15f, 0.15f},   
            { 1.55f*0.15f, 0.15f},   
        };
                
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
        
        public void render(GL gl, Object... instances) {
            
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
                    final float[] size = sizes[i];
                    
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
                      
            gl.glEnable(GL_BLEND);
            gl.glBlendEquation(GL_FUNC_ADD);
            gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            
            
            gl.glUseProgram(program);                               
            
            gl.glEnableClientState(GL_VERTEX_ARRAY);                                    
            gl.glBindBuffer(GL_ARRAY_BUFFER, buffer);
            gl.glVertexPointer(2, GL_FLOAT, vSize, 0);            
            
            // <editor-fold defaultstate="collapsed" desc=" instances ">
            
            for(Object instance : instances) 
            {                
                final int texture = 0;
                
                gl.glMatrixMode(GL_MODELVIEW);
                gl.glLoadIdentity();
                if(textures.length > 0) {
                    gl.glTranslatef(0.2f, 0.2f, 0.0f);
                    final float[] size = sizes[texture];
                    gl.glScalef(size[0], size[1], 1.0f);
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
            
            gl.glDisable(GL_BLEND);
            
        }
    }
    
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    private final Mask mask = new Mask();
    private final Preview preview = new Preview();
    private final Play play = new Play();
    
    // </editor-fold>
        
    public void render(GL gl) 
    {              
        gl.glEnable(GL_DEPTH_TEST);                
        
        play.render(gl);
        
        preview.render(gl, 1);
        
        gl.glDisable(GL_DEPTH_TEST);           
    }
}
