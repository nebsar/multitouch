/*
 * Image.java
 *
 * Created on May 5, 2007, 12:37:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package demo.gallery;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 *
 * @author gestalt
 */
public interface Image
{
    double getContent ();
    
    double getCenterX ();
    double getCenterY ();
    
    double getExtentX ();
    double getExtentY ();
    
    double getOrientation ();  
    

    final static public class Utils
    {
        static public Bounds bounds (Image image)
        {
            final double cX = image.getCenterX();
            final double cY = image.getCenterY();

            double rot = - image.getOrientation();
            final double cos = ( float ) cos ( rot );
            final double sin = ( float ) sin ( rot );
            
            final double extX = image.getExtentX();
            final double extY = image.getExtentY();
            
            return new Bounds ()
            {
                public boolean contain(Touch touch)
                {               
                    double x = touch.getX();
                    double y = touch.getY();
                    {
                        x -= cX;
                        y -= cY;
                    }
                    {
                        final double xNew =  cos*x + sin*y;
                        final double yNew =  cos*y - sin*x;
                        
                        x = xNew;
                        y = yNew;
                    }
                    
                    return ( abs( x ) < extX ) && ( abs( y ) < extY );                    
                }
            };
        }
    }
}
