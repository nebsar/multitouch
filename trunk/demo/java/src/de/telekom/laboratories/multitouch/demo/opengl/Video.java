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

package de.telekom.laboratories.multitouch.demo.opengl;

import static java.lang.String.*;
import static java.nio.ByteOrder.*;
import static javax.media.opengl.GL.*;
import static de.telekom.laboratories.multitouch.demo.opengl.ProgramUtils.*;


import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class Video {
    
    public enum Format {
        LUMINANCE {
            public int size() { return 1; }
            int glFormat()   { return GL_LUMINANCE; }
            int glType()     { return GL_UNSIGNED_BYTE; } 
            int glInternal() { return GL_LUMINANCE; }
        },
        LUMINANCE_ALPHA {
            public int size() { return 2; }
            int glFormat()   { return GL_LUMINANCE_ALPHA; }
            int glType()     { return GL_UNSIGNED_BYTE; } 
            int glInternal() { return GL_LUMINANCE_ALPHA; }
        },
        RGB {
            public int size() { return 3; }
            int glFormat()   { return GL_RGB; }
            int glType()     { return GL_UNSIGNED_BYTE; } 
            int glInternal() { return GL_RGB; }
        },
        RGBA {
            public int size() { return 4; }
            int glFormat()   { return GL_RGBA; }
            int glType()     { return GL_UNSIGNED_BYTE; } 
            int glInternal() { return GL_RGBA; }
        };
        
        public abstract int size(); // in bytes
        abstract int glFormat();    // opengl's pixel format
        abstract int glType();      // opengl's pixel type
        abstract int glInternal();  // opengl's internal format
    }
  
    
    public interface Stream {
        void to(Video video, ByteBuffer data);
    }
    
    private final int width, height;
    private final Format format;
    
    private int texture;
    private int pixelBuffer, vertexBuffer;
    private int program;
    
    private Stream stream;
    
    public Video(int width, int height, Format format) {
        if(width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        } else if(format == null) {
            throw new NullPointerException();
        }
        this.width = width;
        this.height = height;
        this.format = format;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public Format getFormat() {
        return format;
    }
    
    public void update(Stream stream) {
        synchronized(this) {
            this.stream = stream;
        }
    }
    
    public void render(GL gl) {
        
        final int vSize  = 4*2; //size(float)*(2)
        final int vCount = 4;
        
        // <editor-fold defaultstate="collapsed" desc=" program ">

        if(!gl.glIsProgram(program)) {                
            final Class c = getClass();
            final String base = c.getPackage().getName().replace(".", "/");
            program = program(gl, 
                    c.getResource(format("/%s/Video.vert", base)),
                    c.getResource(format("/%s/Video.frag", base)));


            final int textureLOC = gl.glGetUniformLocation( program, "texture" );
            gl.glUseProgram(program);
            if(textureLOC >= 0) 
            {
                gl.glUniform1i(textureLOC, 0);
            }
            gl.glUseProgram(GL_NONE);
        }

        // </editor-fold>        
        
        // <editor-fold defaultstate="collapsed" desc=" vertex-buffer ">
        
        if(!gl.glIsBuffer(vertexBuffer)) {
            final float ext = 1.0f;
            
            final FloatBuffer vertexData = ByteBuffer.allocateDirect( vSize * vCount ).order(nativeOrder()).asFloatBuffer();
            vertexData.put( +ext ).put( +ext );
            vertexData.put( -ext ).put( +ext );
            vertexData.put( -ext ).put( -ext );
            vertexData.put( +ext ).put( -ext );
            vertexData.rewind();
            
            final int[] buffers = new int[1];
            gl.glGenBuffers(1, buffers, 0);
            vertexBuffer = buffers[0];
            
            gl.glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
            gl.glBufferData(GL_ARRAY_BUFFER, vertexData.capacity()*4, vertexData, GL_STATIC_DRAW);
            gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" vertex-buffer ">
        
        if(!gl.glIsBuffer(pixelBuffer)) {
            final int[] ref = new int[1];
            gl.glGenBuffers(1, ref, 0);
            pixelBuffer = ref[0];
            
            gl.glBindBuffer(GL_PIXEL_UNPACK_BUFFER_ARB, pixelBuffer);
            gl.glBufferData(GL_PIXEL_UNPACK_BUFFER_ARB, width*height*format.size(), null, GL_DYNAMIC_DRAW);
            gl.glBindTexture(GL_PIXEL_UNPACK_BUFFER_ARB, GL_NONE);
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" texture ">
        
        if(!gl.glIsTexture(texture)) {
            final int[] ref = new int[1];
            gl.glGenTextures(1, ref, 0);
            texture = ref[0];
            
            gl.glBindTexture(GL_TEXTURE_2D, texture);
            
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            
            // Define texture level zero (without an image); notice the
            // explicit bind to the zero pixel unpack buffer object so that
            // pass NULL for the image data leaves the texture image
            // unspecified.
            gl.glBindBuffer(GL_PIXEL_UNPACK_BUFFER_ARB, GL_NONE);
            gl.glTexImage2D(GL_TEXTURE_2D, 0, format.glInternal(), width, height, 0, format.glFormat(), format.glType(), null);
                        
            final int[] value = new int[1];
            gl.glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_LUMINANCE_SIZE, value, 0);
            System.out.println("Luminance: " + value[0]);
            gl.glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_RED_SIZE, value, 0);
            System.out.println("Red: " + value[0]);
            gl.glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_GREEN_SIZE, value, 0);
            System.out.println("Green: " + value[0]);
            gl.glGetTexLevelParameteriv(GL_TEXTURE_2D, 0, GL_TEXTURE_BLUE_SIZE, value, 0);
            System.out.println("Blue: " + value[0]);

            
            gl.glBindTexture(GL_TEXTURE_2D, GL_NONE);
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" update ">
                
        synchronized(this) 
        {
            if(stream != null) {
                gl.glBindTexture(GL_TEXTURE_2D, texture);     
                gl.glBindBuffer(GL_PIXEL_UNPACK_BUFFER_ARB, pixelBuffer);

                final ByteBuffer data = gl.glMapBuffer(GL_PIXEL_UNPACK_BUFFER_ARB, GL_WRITE_ONLY);

                stream.to(this, data);

                gl.glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER_ARB);

                gl.glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, format.glFormat(), format.glType(), 0);

                gl.glBindBuffer(GL_PIXEL_UNPACK_BUFFER_ARB, GL_NONE);
                gl.glBindTexture(GL_TEXTURE_2D, GL_NONE);                
    
                stream = null;                
            }            
        } 
        
        // </editor-fold>
        
        gl.glUseProgram(program);                               

        gl.glEnableClientState(GL_VERTEX_ARRAY);                                    
        
        gl.glBindTexture(GL_TEXTURE_2D, texture);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);        
        gl.glVertexPointer(2, GL_FLOAT, vSize, 0);            
        
        

        // <editor-fold defaultstate="collapsed" desc=" instances ">

        //for(Object instance : instances) 
        {                
            gl.glDrawArrays(GL_TRIANGLE_FAN, 0 , vCount);
        }

        // </editor-fold>

        gl.glBindTexture(GL_TEXTURE_2D, GL_NONE);
        gl.glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);            
        gl.glDisableClientState(GL_VERTEX_ARRAY_POINTER);            

        gl.glUseProgram(0);          
    }
}

