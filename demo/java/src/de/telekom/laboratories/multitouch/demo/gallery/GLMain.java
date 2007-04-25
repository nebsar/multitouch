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

package de.telekom.laboratories.multitouch.demo.gallery;


import de.telekom.laboratories.capture.Aquire;
import de.telekom.laboratories.capture.Device;
import de.telekom.laboratories.capture.VideoMode;
import static java.lang.Math.*;
import static java.lang.String.*;
import java.nio.ByteBuffer;
import static java.nio.ByteOrder.*;
import static javax.media.opengl.GL.*;


import javax.media.opengl.GL;
import de.telekom.laboratories.multitouch.demo.opengl.Mask;
import static de.telekom.laboratories.multitouch.demo.opengl.ProgramUtils.*;
import de.telekom.laboratories.multitouch.demo.opengl.Video;


/**
 * @author Michael Nischt
 * @version 0.1
 */
public class GLMain {
    
    private final Mask mask = new Mask();    
    private final GLGallery gallery = new GLGallery();

    private final Video video = new Video(1024, 768, Video.Format.LUMINANCE_8);
    private Device camera;
    private byte[] data = new byte[1024 * 768];
    private boolean capture = true;

    public GLMain() {
        try {
            final Video.Stream stream = new Video.Stream() {
                public void to(Video video, ByteBuffer data) {
                    synchronized(GLMain.this) {
                        data.put(GLMain.this.data);
                        capture = true;
                    }
                }
            };
                        
            final Device cam = Device.Registry.getLocalRegistry().getDevices()[0];
            cam.connect(
                new VideoMode(1024, 768, VideoMode.Format.LUMINACE_8, 1.0f),
                new Aquire() {
                   public void capture(ByteBuffer buffer) {
                           buffer.get(data);
                           video.update(stream);
                   }
                }
                );
             camera = cam;

             final Thread t = new Thread("Multitouch.Demo.Capture") {
                 @Override public void run() 
                 {
                    camera.capture();
                    while(true) {
                        synchronized(GLMain.this) {                       
                            if(capture) {                            
                                capture = false;
                                cam.capture();
                            }                        
                        }                            
                    }
                 }
             };
             t.setDaemon(true);
             t.setPriority((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY)/2);
             t.start();
             
        } catch(Exception e) {}
    }

    public void render(GL gl, int width, int height) 
    {           
        
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glShadeModel(GL_SMOOTH);        
                
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
        gl.glDepthMask(true);
                
        
        final int x = 0, y = 0;
        width  = max(1, width);
        height = max(1, height);        

        // <editor-fold defaultstate="collapsed" desc=" normal viewport ">
        
        gl.glViewport(x, y, width, height);
        
        // </editor-fold>            
                        
        if(width >= height) {
            final int space = (width - height) / 2;
            gl.glScissor(x+space, y, height, height);
        } else {
            final int space = (height - width) / 2;
            gl.glScissor(x, y+space, width, width);
        }
        gl.glEnable(GL_SCISSOR_TEST);
        
        video.render(gl);
        
        gl.glDisable(GL_SCISSOR_TEST);
        
        // <editor-fold defaultstate="collapsed" desc=" square viewport ">
                        
        if(width >= height) {
            final int space = (width - height) / 2;
            gl.glViewport(x+space, y, height, height);
        } else {
            final int space = (height - width) / 2;
            gl.glViewport(x, y+space, width, width);
        }
        
        // </editor-fold>        
        
                
//        {   // optional
//            gl.glMatrixMode(GL_PROJECTION);
//            gl.glLoadIdentity();
//            gl.glMatrixMode(GL_MODELVIEW);
//            gl.glLoadIdentity();
//
//            //gl.glColor4f(101/255f, 139/255f, 169/255f, 1.0f);
//            gl.glColor4f(223/255f, 223/255f, 223/255f, 1.0f);
//            gl.glBegin(GL_TRIANGLE_FAN);
//            gl.glVertex2f( +1.0f, +1.0f );
//            gl.glVertex2f( -1.0f, +1.0f );
//            gl.glVertex2f( -1.0f, -1.0f );
//            gl.glVertex2f( +1.0f, -1.0f );
//            gl.glEnd();
//        }
        
        gl.glDepthMask(false);        
        
        //gallery.render(gl);
        
        gl.glDepthMask(true);
        
        //mask.render(gl);        
    }    
    
}
