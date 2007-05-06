/*
 * Touch.java
 *
 * Created on May 5, 2007, 1:11:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package demo.gallery;


/**
 *
 * @author gestalt
 */
final public class Touch
{
    private final double x, y;
    
    public Touch (double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    public double getX ()
    { return x; }
    public double getY ()
    { return y; }
    
    
    static public class Utils
    {
        public static double distanceSquared (Touch from, Touch to)
        {
            return from.x*to.x + from.y*to.y;
        }
        
        public static double distance (Touch from, Touch to)
        {
            return Math.sqrt ( distanceSquared ( from, to ) );
        }
    }
    
}
