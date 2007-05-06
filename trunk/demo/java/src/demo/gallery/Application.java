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

import static java.lang.Math.*;
import static javax.media.opengl.GL.*;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import com.sun.opengl.util.Animator;
import java.awt.event.WindowEvent;
import javax.media.opengl.GLAutoDrawable;

import net.monoid.util.FPSCounter;

import de.telekom.laboratories.capture.Device;
import de.telekom.laboratories.capture.VideoMode;



/**
 * @author Michael Nischt
 * @version 0.1
 */
public final class Application
{
    
    // <editor-fold defaultstate="collapsed" desc=" Initializer ">
    
    public Application ()
    {
        
    }
    
    // </editor-fold>
    
    public static void main (String... args)
    {
        final Scene scene = new Scene ()
        {
            public void render (Content content)
            {
                
            }
        };
        
        {
            final int width = 768, height = 768;
            final boolean fullscreen = true;
            final int screen = 1;
            
            // <editor-fold defaultstate="collapsed" desc=" Vision ">
            
            try
            {
                final Device[] cameras = Device.Registry.getLocalRegistry ().getDevices ();
                
                //final Device camera = cameras[0];
                
                final Thread t = new Thread ("Multitouch.Demo.Capture")
                {
                    @Override public void run ()
                    {
                        final VideoMode mode = new VideoMode (width, height, VideoMode.Format.LUMINACE_8, 30.0f);
                        //camera.connect(mode, aquire);
                        //while(true) {
                        //    camera.capture();
                        //}
                    }
                };
                t.setDaemon (true);
                t.setPriority ((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY)/2);
                //t.start();
                
                
            }
            catch(Exception e)
            {
                throw new RuntimeException ("Could not initialize vision", e);
            }
            
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc=" Graphics ">
            
            final Runnable init = new Runnable ()
            {
                public void run ()
                {                    
                    final GLRenderer renderer = new GLRenderer (scene);
                    
                    // <editor-fold defaultstate="collapsed" desc=" FPSCounter ">
                    
                    final FPSCounter fpsCounter = new FPSCounter ();
                    
                    // </editor-fold>
                    
                    // <editor-fold defaultstate="collapsed" desc=" OpenGL ">
                    
                    final GLCapabilities caps = new GLCapabilities ();
                    caps.setHardwareAccelerated (true);
                    caps.setDoubleBuffered (true);
                    caps.setSampleBuffers (true);
                    
                    final GLCanvas canvas = new GLCanvas (caps);
                    canvas.setSize (width, height);
                    canvas.setPreferredSize (new Dimension (width, height));
                    
                    final Animator animator = new Animator (canvas);
                    
                    // <editor-fold defaultstate="collapsed" desc=" EventListener ">
                    
                    canvas.addGLEventListener (new GLEventListener ()
                    {
                        public void init (GLAutoDrawable drawable)
                        {
                            if(!animator.isAnimating ())
                            {
                                animator.start ();
                            }
                        }
                        public void display (GLAutoDrawable drawable)
                        {
                            fpsCounter.nextFrame ();
                            renderer.render (drawable.getGL (), drawable.getWidth (), drawable.getHeight ());
                        }
                        public void reshape (GLAutoDrawable drawable, int x, int y, int width, int height)
                        {
                        }
                        public void displayChanged (GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
                        {
                        }
                    });
                    
                    // </editor-fold>
                    
                    // </editor-fold>
                    
                    // <editor-fold defaultstate="collapsed" desc=" AWT / SWING ">
                    
                    final GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment ().getScreenDevices ();
                    final GraphicsDevice   graphicsDevice  = graphicsDevices[max (0, min (screen, graphicsDevices.length))];
                    
                    final Frame frame = new Frame ("T-Demo", graphicsDevice.getDefaultConfiguration ());
                    frame.setSize (width, height);
                    
                    frame.add (canvas);
                    //frame.getContentPane().add(canvas); <-- swing
                    
                    // <editor-fold defaultstate="collapsed" desc=" Window-Sate ">
                    
                    frame.addWindowListener (new WindowAdapter ()
                    {
                        @Override
                        public void windowClosing (WindowEvent e)
                        {
                            
                            if(animator.isAnimating ())
                            {
                                animator.stop ();
                            }
                            
                            frame.dispose ();
                            
                            //TODO:  change to Application.close();
                            System.exit (0);
                            
                        }
                    });
                    
                    // </editor-fold>
                    
                    // <editor-fold defaultstate="collapsed" desc=" FPSCounter.Listener ">
                    
                    fpsCounter.addFPSCounterListener (new FPSCounter.Listener ()
                    {
                        
                        private final String title = frame.getTitle ();
                        
                        public void averageFramesElapsed (FPSCounter.Event e)
                        {
                            frame.setTitle (String.format ("%s  %f(AVG) %f(AGG)", title, e.getAverageFps (), e.getAggregateFps ()));
                        }
                    });
                    
                    // </editor-fold>
                    
                    // <editor-fold defaultstate="collapsed" desc=" Fullscreen ">
                    
                    if(fullscreen)
                    {// && device.isFullScreenSupported()) {
                        try
                        {
                            frame.setUndecorated (true);
                            graphicsDevice.setFullScreenWindow (frame);
                        }
                        catch(Exception e)
                        {
                            graphicsDevice.setFullScreenWindow (null);
                            frame.pack ();
                        }
                    }
                    else
                    {
                        frame.pack ();
                        
                        // <editor-fold defaultstate="collapsed" desc=" Round Region ">
                        try
                        {
                            //final User32 user32 = User32.INSTANCE;
                            //final GDI32 gdi32 = GDI32.INSTANCE;
                            //System.out.println(user32);
                            //System.out.println(gdi32);
                            //final Pointer p = gdi32.CreateRoundRectRgn(0, -150, 300, 300, 300, 300);
                            //final int hWnd = user32.FindWindowA(null, frame.getName());
                            //user32.setWindowRgn(hWnd, p, true);
                        }
                        catch(Exception e)
                        {
                            System.err.println (e.getMessage ());
                            e.printStackTrace ();
                        }
                        // </editor-fold>
                    }
                    
                    // </editor-fold>
                    
                    frame.setVisible (true);
                    
                    // </editor-fold>
                }
            };
            
            if(EventQueue.isDispatchThread ())
            {
                init.run ();
            }
            else
            {
                try
                {
                    EventQueue.invokeAndWait (init);
                }
                catch(Exception e)
                {
                    throw new RuntimeException ("Could not initialize graphics", e);
                }
            }
            
            // </editor-fold>
        }
    }
}
