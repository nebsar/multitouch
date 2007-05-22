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

import static java.lang.Math.*;
import java.nio.ByteBuffer;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableList;
import static javax.media.opengl.GL.*;
import static de.telekom.laboratories.multitouch.Correlations.unique;

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
import de.telekom.laboratories.capture.Aquire;
import java.awt.event.WindowEvent;
import javax.media.opengl.GLAutoDrawable;

import net.monoid.util.FPSCounter;

import de.telekom.laboratories.capture.Device;
import de.telekom.laboratories.capture.VideoMode;
import de.telekom.laboratories.multitouch.Correlation;
import de.telekom.laboratories.multitouch.Matcher;
import de.telekom.laboratories.multitouch.Observer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static demo.gallery.Touch.Utils.distance;
import static demo.gallery.Touch.Utils.distanceSquared;
import static demo.gallery.Image.Utils.bounds;
import static demo.gallery.Manipulator.Rotatable;
import static demo.gallery.Manipulator.Scalable;
import static demo.gallery.Manipulator.Translatable;




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
    
    final private static class ImageAndManipulator
            implements Translatable, Rotatable, Scalable
    {
        final private Manipulator manipulator = new Manipulator ();
        private Image image;
        
        private double x, y, rot, scale = 1.0;
        
        private ImageAndManipulator (Image image)
        {
            if( image == null) throw new NullPointerException ();
            this.image = image;
        }
        
        public void translate (double x, double y)
        {
            this.x = x;
            this.y = y;
        }
        
        public void rotate (double amount)
        {
            this.rot = amount;
        }
        
        public void scale (double ratio)
        {
            this.scale = ratio;
        }
        
        private void add (Touch last, Touch current)
        {
            manipulator.add ( last, current );
        }
        
        private Image getImage ()
        {
            return image;
        }
        
        private void manipulate ()
        {
            manipulator.manipulate (this);
            
            image = image.translated (x, y);
            
            image = image.rotated(this.rot / Math.PI);
            
            if(scale >= 1.0 || (image.getExtentX() > 0.15 || image.getExtentY() > 0.15) )
            {            
                image = image.scaled (scale);
            }
            
            x = y = rot = 0.0;
            scale = 1.0;
        }
        
        static private ImageAndManipulator[] array (Image... images)
        {
            final ImageAndManipulator[] array = new ImageAndManipulator[images.length];
            for(int i=0; i<images.length; i++)
            {
                array[i] = new ImageAndManipulator (images[i]);
            }
            return array;
        }
        
    }
    
    public static void main (String... args)
    {
        final double ext = 0.15, rot = -0.33;
        
        final Image[] images =
        {
//            new Image (0, 1.5* ext, ext).translated (0.4,  -0.5).rotated ( rot ), //0.2),
//            new Image (1, 1.5* ext, ext).translated (0.5,  -0.3).rotated ( rot ), //-0.2),
//            new Image (2, 1.5* ext, ext).translated (0.5,  -0.15).rotated ( rot ), // 0.5),
//            new Image (3, 1.5* ext, ext).translated (0.75,  0.0).rotated ( rot ), //-0.7),
//            new Image (4, 1.5* ext, ext).translated (0.7,  +0.3).rotated ( rot ), // 0.0),
//            new Image (5, 1.5* ext, ext).translated (0.4,  +0.6).rotated ( rot ), //-0.4),
//            new Image (6, 1.5* ext, ext).translated (0.2,  +0.7).rotated ( rot ), // 1.0),
            new Image (0, 1.5* ext, ext).translated (0.4,  -0.5).rotated (  0.2 ),
            new Image (1, 1.5* ext, ext).translated (-0.3,  -0.3).rotated ( -0.2 ),
            new Image (2, 1.5* ext, ext).translated (-0.5,  -0.15).rotated(  0.5 ),
            new Image (3, 1.5* ext, ext).translated (0.75,  0.0).rotated ( -0.7 ),
            new Image (4, 1.5* ext, ext).translated (-0.2,  +0.3).rotated (  0.0 ),
            new Image (5, 1.5* ext, ext).translated (0.4,  +0.6).rotated ( -0.4 ),
            new Image (6, 1.5* ext, ext).translated (0.2,  +0.7).rotated (  1.0 ),
        };
        
        final ImageAndManipulator[] manipulators = ImageAndManipulator.array (images);
        
        final Scene scene = new Scene ()
        {
            final List<Touch> touchList = new ArrayList<Touch>();
            
            final Correlation<Touch> correlation = unique ( new Matcher<Touch,Double>()
            {
                public Double match (Touch a, Touch b)
                {
                    final double distance = distance (a, b);
                    return ( distance <= 0.1 ) ? distance : null;
                }
                
                public int compare (Double a, Double b)
                {
                    return a.compareTo (b);
                }
            });
            
            synchronized public void view (Content content)
            {
                for(int i=manipulators.length-1; i>=0; i--)
                {
                    content.addImage ( manipulators[i].getImage () );
                }
                
                for(Touch touch : touchList)
                {
                    content.addTouch ( touch );
                }
            }
            
            synchronized public void control (Input input)
            {
                touchList.clear ();
                
                { // current contact areas
                    final Iterator<Touch> touchIt = input.getTouches ();
                    while(touchIt.hasNext ())
                    {
                        final Touch touch = touchIt.next ();
                        touchList.add (touch);
                        correlation.touch (touch);
                    }
                    
                    //System.out.println(touchList.size());
                    
                    correlation.nextFrame ( new Observer.Adapter<Touch>()
                    {
                        private int touched; // = 0;
                        
//                        @Override
//                        public void touchBegin (Touch current)
//                        {
//                            System.out.printf ("begin: (%f %f)\n", current.getX (), current.getY () );
//                        }
                        @Override
                        public void touchUpdate (Touch last, Touch current)
                        {
                            //System.out.printf ("update: (%f %f) -> (%f %f)\n", last.getX (), last.getY (), current.getX (), current.getY () );
                            for(int i=0; i<manipulators.length; i++)
                            {
                                final Bounds bounds = bounds ( manipulators[i].getImage() ); // TODO: do no re-create every time
                                if(bounds.contain ( last ) || bounds.contain ( current ))
                                {
                                    //System.out.printf ("(%f %f) -> (%f %f)\n", last.getX (), last.getY (), current.getX (), current.getY () );
                                    manipulators[i].add ( last, current );
                                    if( i >= touched)
                                    {
                                        //swap image and manipulator ( touched, i );
                                        final ImageAndManipulator tmp = manipulators[i];
                                        for(int t=i; t>touched; t--)
                                        {
                                            manipulators[t] = manipulators[t-1];
                                        }
                                        manipulators[touched] = tmp;
                                        touched++;
                                    }
                                    break;
                                }
                            }
                        }
                    } );
                    
                    for(ImageAndManipulator m : manipulators)
                    {
                        m.manipulate ();
                    }
                    
                }
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
                
                final Device camera = cameras[0];
                
                final Thread t = new Thread ("Multitouch.Demo.Capture")
                {
                    @Override public void run ()
                    {
                        final Aquire aquire = new Aquire ()
                        {
                            final TLCapture capture = new TLCapture (scene, width, height);
                            
                            final byte[] target  = new byte[width * height];
                            
                            final byte[]  image  = new byte[width *height];
                            
                            public void capture (ByteBuffer buffer)
                            {
                                for(int i=0; i<height; i++)
                                {
                                    buffer.position ((height-(i+1))*1024 + (1024-width)/2);
                                    buffer.get (target, i*width, width);
                                }
                                
                                final boolean m = true;
                                if(m)
                                {
                                    final int w = width-1;
                                    for(int y=0; y<height; y++)
                                    {
                                        final int off = y*width;
                                        for(int x=0; x<width; x++)
                                        {
                                            image[off+x] = target[off-x+w];
                                        }
                                    }
                                }
                                else
                                {
                                    for(int y=0; y<height; y++)
                                    {
                                        final int off = y*width;
                                        for(int x=0; x<width; x++)
                                        {
                                            image[off+x] = target[off+x];
                                        }
                                    }
                                }
                                
                                capture.capture (image);
                            }
                        };
                        
                        final VideoMode mode = new VideoMode (1024, 768, VideoMode.Format.LUMINACE_8, 30.0f);
                        camera.connect (mode, aquire);
                        while(true)
                        {
                            camera.capture ();
                        }
                    }
                };
                t.setDaemon (true);
                t.setPriority ((Thread.NORM_PRIORITY + Thread.MAX_PRIORITY)/2);
                t.start ();
                
                
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
