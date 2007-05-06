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

package demo.gallery;


import de.telekom.laboratories.capture.Aquire;
import de.telekom.laboratories.capture.Device;
import de.telekom.laboratories.capture.VideoMode;
import static java.lang.Math.*;
import static java.lang.String.*;
import java.nio.ByteBuffer;
import static java.nio.ByteOrder.*;
import static javax.media.opengl.GL.*;


import javax.media.opengl.GL;
import utils.opengl.Mask;
import static utils.opengl.ProgramUtils.*;
import utils.opengl.Video;
import de.telekom.laboratories.multitouch.util.Labels;


/**
 * @author Michael Nischt
 * @version 0.1
 */
public class GLMain {
    
    private final Mask mask = new Mask();
    private final GLGallery gallery = new GLGallery();
    
    private final Video video = new Video(768, 768, Video.Format.LUMINANCE);
    final Video.Stream stream;
    
    private Device camera;
    private final byte[] data = new byte[768 * 768];
    
    private boolean capture = true;
    
    private final byte[] diff   = new byte[768 * 768];
    private final byte[] mirror = new byte[768 * 768];
    
    final int[][] image = new int[768][768];
    private final Labels labels = new Labels(image);
    
    public GLMain() {
        stream = new Video.Stream() {
            public void to(Video video, ByteBuffer data) {
                synchronized(GLMain.this) {
                    //System.out.println("Stream - Start");
                    data.put(GLMain.this.data);
                    capture = true;
                    //System.out.println("Stream - End");
                }
            }
        };
        
        try {
            final boolean m = true;
            final Device cam = Device.Registry.getLocalRegistry().getDevices()[0];
            
            final Aquire aquire = new Aquire() {
                int index = 0;
                public void capture(ByteBuffer buffer) {
                    //System.out.println("Aquire - Start");
                    
                    if(index++ == 100) {
                        for(int i=0; i<768; i++) {
                            buffer.position(i*1024 + (1024-768)/2);
                            buffer.get(mirror, i*768, 768);
                        }
                        if(m) {
                            for(int y=0; y<768; y++) {
                                final int off = y*768;
                                for(int x=0; x<768; x++) {
                                    diff[off+x] = mirror[off-x+767];
                                }
                            }
                        } else {
                            System.arraycopy(mirror, 0, diff, 0, diff.length);
                        }
                    }
                    for(int i=0; i<768; i++) {
                        buffer.position(i*1024 + (1024-768)/2);
                        buffer.get(mirror, i*768, 768);
                    }
                    
                    if(m) {
                        for(int y=0; y<768; y++) {
                            final int off = y*768;
                            for(int x=0; x<768; x++) {
                                data[off+x] = mirror[off-x+767];
                            }
                        }
                    } else {
                        System.arraycopy(mirror, 0, data, 0, data.length);
                    }
                    
                    for(int y=0; y<768; y++) {
                        final int off = y*768;
                        for(int x=0; x<768; x++) {
                            final int index =  off+x;
                            data[index] = (byte) Math.max( 0, (0xFF & data[index]) - (0xFF & diff[index]) );
                        }
                    }
                    
                    int sum = 0;
                    final int threshold = 20;
                    for(int y=0; y<768; y++) {
                        final int[] row = image[y];
                        final int off = y*768;
                        for(int x=0; x<768; x++) {
                            final int value = (0xff & data[off+x]);
                            if(value > threshold) {
                                row[x] = (int) (value * 255.0f / (threshold+(0xFF & diff[index]))) ; //value;
                                sum ++;
                            } else {
                                row[x] = 0;
                            }
                        }
                    }
                    
//                        System.out.println(sum);
                    sum = 0;
                    int index = 0;
                    final int[][] bounds = labels.bounds();
                    for(int[] b : bounds) {
                        index++;
                        int width  = (b[2]-b[0]);
                        int height = (b[3]-b[1]);                        
                        if(width > 10 && height > 10) {                            
                            //System.out.printf("%d %d %d %d\n", b[0], b[1], b[2], b[3]);                            
                            sum++;
                        } else {
                        for(int y=b[1]; y<=b[3]; y++) {
                            final int[] row = image[y];
                            for(int x=b[0]; x<=b[2]; x++) {
                                if(row[x] == index) {
                                    row[x] = 0;
                                }
                            }
                        }
                        }
                    }
                    
//                    if(sum > 0) {
                       // System.out.println(sum + " / " + bounds.length);
                        //System.out.println("------------------------------");
//                    }// else {
                    //    System.out.println("------ missed ------");
                    //}
                    

                    for(int y=0; y<768; y++) {
                        final int[] row = image[y];
                        final int off = y*768;
                        for(int x=0; x<768; x++) {
                            data[off+x] = ((row[x] != 0)) ? (byte)255 : 0;
                        }
                    }
                    
                    video.update(stream);
                    //System.out.println("Aquire - End");
                }
            };
            
            cam.connect(new VideoMode(1024, 768, VideoMode.Format.LUMINACE_8, 1.0f), aquire);
            camera = cam;
            
            final Thread t = new Thread("Multitouch.Demo.Capture") {
                @Override public void run() {
                    synchronized(GLMain.this) {
                        camera.capture();
                    }
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
    
    public void render(GL gl, int width, int height) {

        
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glShadeModel(GL_SMOOTH);
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
        gl.glDepthMask(true);
        
        
        final int x = 0, y = 0;
        width  = max(1, width);
        height = max(1, height);
        
//        // <editor-fold defaultstate="collapsed" desc=" normal viewport with scissor">
//
//        gl.glViewport(x, y, width, height);
//
//        // </editor-fold>
//
//        if(width >= height) {
//            final int space = (width - height) / 2;
//            gl.glScissor(x+space, y, height, height);
//        } else {
//            final int space = (height - width) / 2;
//            gl.glScissor(x, y+space, width, width);
//        }
//        gl.glEnable(GL_SCISSOR_TEST);
//
        video.render(gl);
//
//        gl.glDisable(GL_SCISSOR_TEST);
        
        // <editor-fold defaultstate="collapsed" desc=" square viewport ">
        
        if(width >= height) {
            final int space = (width - height) / 2;
            gl.glViewport(x+space, y, height, height);
        } else {
            final int space = (height - width) / 2;
            gl.glViewport(x, y+space, width, width);
        }
        
        // </editor-fold>
        
       // video.render(gl);
        
        {   // optional
//            gl.glMatrixMode(GL_PROJECTION);
//            gl.glLoadIdentity();
//            gl.glMatrixMode(GL_MODELVIEW);
//            gl.glLoadIdentity();
//
//            gl.glColor4f(101/255f, 139/255f, 169/255f, 1.0f);
//            //gl.glColor4f(223/255f, 223/255f, 223/255f, 1.0f);
//            //gl.glColor4f(1f, 1f, 1f, 1.0f);
//            //gl.glColor4f(0f, 0f, 0f, 1.0f);
//            gl.glBegin(GL_TRIANGLE_FAN);
//            gl.glVertex2f( +1.0f, +1.0f );
//            gl.glVertex2f( -1.0f, +1.0f );
//            gl.glVertex2f( -1.0f, -1.0f );
//            gl.glVertex2f( +1.0f, -1.0f );
//            gl.glEnd();
        }
        
        gl.glDepthMask(false);
        
        gallery.render(gl);
        
        gl.glDepthMask(true);
        
        mask.render(gl);
    }
    
}
