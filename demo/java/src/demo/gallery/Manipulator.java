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

import static demo.gallery.Touch.Utils.distance;
import static demo.gallery.Touch.Utils.distanceSquared;
import de.telekom.laboratories.multitouch.util.Moments2D;


/**
 * 
 * @author Michael Nischt
 * @version 0.1
 */
public class Manipulator
{   
    // <editor-fold defaultstate="collapsed" desc=" Translatable ">
    
    public static interface Translatable
    {
        void translate(double x, double y);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Rotatable ">
    
    public static interface Rotatable
    {
        void rotate(double amount);
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Scalable ">
    
    public static interface Scalable
    {
        void scale(double ratio);
    }
    
    // </editor-fold>

    
    //TODO: seperate RigBody- and Scale-Manipulator
    
    // <editor-fold defaultstate="collapsed" desc=" Distance ">
    
    final static private class Distance
    {
        // <editor-fold defaultstate="collapsed" desc=" Attributes ">
        
        private Touch last;
        private double distance;
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Methods ">
        
        private void point (Touch current)
        {
            if( last != null) 
            {
                distance += distance ( last, current );
            }
            last = current;
        }
        
        private double value ()
        {
            return distance;
        }
        
        private void reset ()
        {
            last = null;
            distance = 0.0;
        }
        
        // </editor-fold>
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Distances ">
    
    final static private class Distances
    {
        // <editor-fold defaultstate="collapsed" desc=" Attributes ">
        
        final private Distance from = new Distance();
        final private Distance to = new Distance();
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Methods ">
        
        private void add (Touch from, Touch to)
        {
            this.from.point ( from );
            this.to  .point ( to   );
        }
        
        private double from ()
        {
            return from.value();
        }
        
        private double to ()
        {
            return to.value();
        }    
        
        private void scale(Scalable scalable)
        {
            final double from  = from(), to = to();
            
            final double scale = (from > 0.0 && to > 0.0) ? ( to / from ) : 1.0;
//            if(scale != 1.0)
//                System.out.println("to(" + to + ")  / from(" + from + "): " + scale);   
            scalable.scale ( scale );
            
//            final double ext = to - from;
//            if(ext != 0.0)
//                System.out.println("to(" + to + ")  - from(" + from + ") = " + ext);
            //scalable.scale ( ext );
        }
            
        
        private void reset ()
        {
            from.reset();
            to  .reset();
        }
        
        // </editor-fold>
    }    
    
    // </editor-fold>
    

    // <editor-fold defaultstate="collapsed" desc=" Body ">
    
    final static private class Body
    {
        // <editor-fold defaultstate="collapsed" desc=" Attributes ">
        
        final private Moments2D moments = new Moments2D (2);
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Methods ">
        
        private void point (Touch point)
        {
            moments.point ( point.getX(), point.getY() );
        }
        
        private void reset()
        {
            moments.clear();
        }
        
        // postion + rotation
        private void foo(double[] values)
        {
            // Mass is given by the 0th moment
            final double mass = moments.getMoment(0,0);

            if(mass <= 0.0001)
            {
                values[0] = values[1] = values[2] = 0.0;
                return;
            }
            
            // Centre of mass is given by the 1st moments        
            final double x = moments.getMoment(1, 0) / mass;
            final double y = moments.getMoment(0, 1) / mass; 
            
            // Teh centered 2nd moments build the intertia (tensor)

            // special case for: moments.from(s, -centerX, -centerY)
            final double uXX = moments.getMoment(2,0) - x*moments.getMoment(1, 0); 
            final double uXY = moments.getMoment(1,1) - x*moments.getMoment(0, 1);
            final double uYY = moments.getMoment(0,2) - y*moments.getMoment(0, 1);

            // final double[][] inertiaTensor = 
            // {
            //   {  uXX, -uXY }, 
            //   { -uXY, -uYY }, 
            // };

            final double inertia = uXX + uYY; // == uZZ with z = (x^2 + y^2)^(1/2)

            // The orientation of an object is defined as the axis of minimum inertia. 
            // This is the axis of the least 2nd moment, the orientation of which is                
            final double uXY2 = 2*uXY;
            final double uXXsubUYY = uXX-uYY;

            final double orientation = Math.atan2(uXY2, uXXsubUYY + Math.sqrt( uXXsubUYY*uXXsubUYY + uXY2*uXY2 ) );
            
            values[0] = x;
            values[1] = y;
            values[2] = orientation;
        }
        
        // </editor-fold>
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Bodies ">
    
    final static private class Bodies
    {
        // <editor-fold defaultstate="collapsed" desc=" Attributes ">
        
        final private Body from = new Body();
        final private Body to   = new Body();
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Methods ">
        
        private void add (Touch from, Touch to)
        {
            this.from.point ( from );
            this.to  .point ( to   );
        }         
        
        private void reset ()
        {
            from.reset();
            to  .reset();
        }
        
        private void apply(Translatable translatable, Rotatable rotatable)
        {
            final double[] values = new double[3];
            
            to.foo(values);
            double x = values[0], y = values[1];            
            double rot2 = values[2];
            
            from.foo(values);            
            x -= values[0];
            y -= values[1];
            
            double rot1 = values[2];
            
            final double deg45 = Math.PI / 4.0;
            if(rot1 > deg45 && rot2 < -deg45)
                rot2 += Math.PI;
            
            if(rot2 > deg45 && rot1 < -deg45)
                rot1 += Math.PI;
                        
            double rot = (rot2 - rot1);
                                  
            translatable.translate ( x, y );
            rotatable.rotate ( rot );
        }              
        
        // </editor-fold>
    }    
    
    // </editor-fold>    
            
    
    
    // <editor-fold defaultstate="collapsed" desc=" Attributes ">
    
    final private Bodies    bodies    = new Bodies ();    
    final private Distances distances = new Distances ();
        
            // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Methods ">    
    
    public void reset()
    {
        bodies.reset ();
        distances.reset ();
    }

    public void add (Touch from, Touch to)
    {
        bodies   .add ( from, to );
        distances.add ( from, to );
    }
    
    public <Target extends Translatable & Rotatable & Scalable> void manipulate (Target target)
    {
        bodies.apply ( target, target );
        distances.scale  ( target );
        reset();
    }
    
    // </editor-fold>
}