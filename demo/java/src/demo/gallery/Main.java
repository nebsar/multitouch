/*
 * Main.java
 *
 * Created on May 5, 2007, 11:39:13 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package demo.gallery;


import static de.telekom.laboratories.multitouch.Correlations.unique;
import de.telekom.laboratories.multitouch.Correlation;
import de.telekom.laboratories.multitouch.Matcher;
import de.telekom.laboratories.multitouch.Observer;

import static demo.gallery.Touch.Utils.distance;
import static demo.gallery.Touch.Utils.distanceSquared;
import static demo.gallery.Image.Utils.bounds;
import static demo.gallery.Manipulator.Rotatable;
import static demo.gallery.Manipulator.Scalable;
import static demo.gallery.Manipulator.Translatable;

/**
 *
 * @author Michael Nischt
 */
public class Main
{
    final private static class ImageAndManipulator 
            implements Translatable, Rotatable, Scalable
    {
        final private Manipulator manipulator = new Manipulator();
        private Image image;
        
        private double x, y, rot, scale = 1.0;
        
        private ImageAndManipulator(Image image)
        {
            if( image == null) throw new NullPointerException();
            this.image = image;
        }

        public void translate(double x, double y)
        {
            this.x += x;
            this.y += y;
        }

        public void rotate(double amount)
        {
            this.rot += rot;
        }

        public void scale(double ratio)
        {
            this.scale *= scale;
        }
        
        private void add(Touch last, Touch current)
        {
            manipulator.add ( last, current );
        }
        
        private Image getImage()
        {
            return image;
        }
        
        private Image manipulate()
        {
            manipulator.manipulate(this);
            //image = image.transform(x, y, rot, scale);
            
            x = y = rot = 0.0;
            scale = 1.0;
            
            return image;
        }
        
        static private ImageAndManipulator[] array(Image... images)
        {
            final ImageAndManipulator[] array = new ImageAndManipulator[images.length];
            for(int i=0; i<images.length; i++)
            {
                array[i] = new ImageAndManipulator(images[i]);
            }
            return array;
        }
        
    }
    
    private Main () { }
    
    /**
     * @param args the command line arguments
     */
    public static void main (String[] args)
    {
        // <editor-fold defaultstate="collapsed" desc=" TEST Moments ">
        
        //final Moments2D m = new Moments2D (2);
        //final Body2D b = new Body2D ();
        //
        //final int steps = 16;
        //for(int i=0; i<steps; i++)
        //{
        //    final double angle = 2*Math.PI * i/(float) (steps);
        //    final double x = Math.cos (angle);
        //    final double y = Math.sin (angle);
        //
        //    m.clear ();
        //    m.point (x, y);
        //    m.point (-x, -y);
        //    //m.point(0.0, 0.0);
        //    //m.point(y, x);
        //    b.from (m);
        //    System.out.printf ("(%f %f) (%f %f): Angle: %f\n", x,y, -x, -y,angle / Math.PI * 180.0);
        //    System.out.printf ("x: %f y: %f rot: %f\n", b.getX (), b.getY (), b.getOrientation () / Math.PI * 180.0 );
        //    System.out.println ();
        //}
        
        // </editor-fold>
                
        final Correlation<Touch> correlation = unique ( new Matcher<Touch,Double>()
        {
            public Double match(Touch a, Touch b)
            {
                final double sqDistance = distanceSquared(a, b);
                return ( sqDistance <= 0.2 ) ? sqDistance : null;
            }

            public int compare(Double a, Double b)
            {
                return a.compareTo(b);
            }            
        });
        
        
        final int numImages = 10;
        final Image[] images =
        {
        };
        
        final ImageAndManipulator[] manipulators = ImageAndManipulator.array(images);
                        
        boolean run = true;
        while (run)
        {
            final Touch[] touches = { };
            
            // input
            for(Touch touch : touches)
            {
                correlation.touch(touch);
            }
            correlation.nextFrame ( new Observer.Adapter<Touch>()
            {
                private int touched; // = 0;
                
                @Override
                public void touchUpdate(Touch last, Touch current)
                {
                    for(int i=0; i<manipulators.length; i++)
                    {
                        final Bounds bounds = bounds ( images[i] ); // TODO: do no re-create every time
                        if(bounds.contain( last ) || bounds.contain( current ))
                        {
                            manipulators[i].add ( last, current );
                            if( i >= touched)
                            {   
                                //swap image and manipulator ( touched, i );
                                final ImageAndManipulator tmp = manipulators[i];
                                manipulators[i] = manipulators[touched];
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
                m.manipulate();
            }
            
            {
                output ( images, touches );
            }
        }
    }
    
    private static void input (Image[] images, Touch[] touches)
    {
        //        final Manipulator manipulator = new Manipulator();
        //        for ( Image image : images )
        //        {
        //            manipulator.frame(image, touches);
        //        }
    }
    
    private static void output (Image[] images, Touch[] touches)
    {
    }
    
    
}
