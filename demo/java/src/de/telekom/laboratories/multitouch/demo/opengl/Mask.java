/*
 * Mask.java
 *
 * Created on 25. April 2007, 13:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.telekom.laboratories.multitouch.demo.opengl;

import static java.lang.Math.*;
import static java.nio.ByteOrder.*;
import static javax.media.opengl.GL.*;
import static de.telekom.laboratories.multitouch.demo.opengl.ProgramUtils.program;


import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.media.opengl.GL;

/**
 *
 * @author nischt.michael
 */
public class Mask {

    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
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
        final URL vertURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/opengl/Mask.vert" );
        final URL fragURL = Mask.class.getResource( "/de/telekom/laboratories/multitouch/demo/opengl/Mask.frag" );
        return ProgramUtils.program(gl, vertURL, fragURL);
    }

    public void render(GL  gl) {

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