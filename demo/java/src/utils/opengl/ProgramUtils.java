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

package utils.opengl;

import static java.nio.ByteOrder.nativeOrder;
import static javax.media.opengl.GL.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import javax.media.opengl.GL;
import javax.media.opengl.GLException;

/**
 * @author Michael Nischt
 * @version 0.1
 */
public class ProgramUtils {
    
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
        
        final String vertSource = source( vertexShader );
        final String fragSoruce = source( fragmentShader );
        
        int program = 0, vShader = 0, fShader = 0;
        try {
            vShader = vertexShader( gl,  vertSource);
            fShader = fragmentShader( gl,  fragSoruce);
            
            program = program( gl, vShader, fShader );
            
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