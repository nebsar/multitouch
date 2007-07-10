/*
 * CameraTest.java
 * 
 * Created on Jul 10, 2007, 2:29:26 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demo.camera;

import static java.lang.Math.min;
import static java.lang.Math.max;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.nio.ByteBuffer;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.sun.opengl.util.Animator;
import de.telekom.laboratories.capture.Aquire;
import de.telekom.laboratories.capture.Device;
import de.telekom.laboratories.capture.VideoMode;
import demo.Capture;
import java.awt.event.WindowEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import net.monoid.util.FPSCounter;
import utils.opengl.Video;

/**
 *
 * @author gestalt
 * @version 0.1
 */
public class CameraTest 
{
    private volatile boolean captured = false;
    
    private CameraTest()
    {                        
        //final int cameraWidth = 640, cameraHeight = 480;
        final int cameraWidth = 1024, cameraHeight = 768;
        final byte[] videoBuffer = new byte[cameraWidth*cameraHeight];
                         
        final int displayWidth = 1024, displayHeight = 768;        
        final boolean fullscreen = false;
        final int screen = 0;
        

        // <editor-fold defaultstate="collapsed" desc=" Graphics ">

        final Runnable init = new Runnable ()
        {
            @Override
            public void run ()
            {
                // <editor-fold defaultstate="collapsed" desc=" FPSCounter ">

                final FPSCounter fpsCounter = new FPSCounter ();

                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc=" OpenGL ">

                final GLCapabilities caps = new GLCapabilities ();
                caps.setHardwareAccelerated (true);
                caps.setDoubleBuffered (true);
                caps.setSampleBuffers (true);

                final GLCanvas canvas = new GLCanvas (caps);
                canvas.setSize (displayWidth, displayHeight);
                canvas.setPreferredSize (new Dimension (displayWidth, displayHeight));

                final Animator animator = new Animator (canvas);

                // <editor-fold defaultstate="collapsed" desc=" EventListener ">

                abstract class GLVideoStreamEventListener implements GLEventListener, Video.Stream {}
                canvas.addGLEventListener (new GLVideoStreamEventListener ()
                {
                    private Video video = new Video(cameraWidth, cameraHeight, Video.Format.LUMINANCE);
                    
                    @Override
                    public void init (GLAutoDrawable drawable)
                    {
                        if(!animator.isAnimating ())
                        {
                            //animator.setRunAsFastAsPossible(true);
                            animator.start ();                            
                        }
                    }
                    @Override
                    public void display (GLAutoDrawable drawable)
                    {                        
                        fpsCounter.nextFrame ();
                        
                        final GL gl = drawable.getGL();
                            
                        synchronized(videoBuffer)
                        {
                            if(captured)
                            {          
                                video.update(this);                                
                                captured = false;     
                            }
                        }
                        video.render( gl );
                    }
                    @Override
                    public void reshape (GLAutoDrawable drawable, int x, int y, int width, int height)
                    {
                    }
                    @Override
                    public void displayChanged (GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
                    {
                    }

                    @Override
                    public void to(Video video, ByteBuffer data)
                    {
                        data.put(videoBuffer);                            
                        //data.flip();
                    }
                });

                // </editor-fold>

                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc=" AWT / SWING ">

                final GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment ().getScreenDevices ();
                final GraphicsDevice   graphicsDevice  = graphicsDevices[max (0, min (screen, graphicsDevices.length-1))];

                final Frame frame = new Frame ("T-Demo: CameraTest", graphicsDevice.getDefaultConfiguration ());
                frame.setSize (displayWidth, displayHeight);

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

                    @Override
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
        
        
        // <editor-fold defaultstate="collapsed" desc=" Vision ">        
        
        final Capture device = Capture.startDevice(cameraWidth, cameraHeight);
        final Thread visionThread = new Thread("Vision")
        {
            @Override
            public void run()
            {
                while(this == currentThread())
                {
                    synchronized(videoBuffer)
                    {
                        if(!captured)
                        {
                            //System.out.print("capture");
                            
                            device.capture(videoBuffer);
                            
                            //System.out.println("d");                            
                            captured = true;
                        }
                    }
                }
            }
        };
        visionThread.setDaemon(true);
        visionThread.setPriority(Thread.NORM_PRIORITY);
        visionThread.start();
        
        // </editor-fold>
    }
    
    public static void main(String... args) throws Exception
    {
        new CameraTest();
    }
            
}
