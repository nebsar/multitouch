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

package demo.worldwind;

import de.telekom.laboratories.tracking.Matcher;
import de.telekom.laboratories.tracking.Observer;
import de.telekom.laboratories.tracking.Tracker;
import de.telekom.laboratories.tracking.Trackers;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Quaternion;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.view.OrbitView;
import gov.nasa.worldwind.view.View;

import java.util.Iterator;

/**
 * @author Michael Nischt
 * @version 0.1
 */
public class TouchInput
{
    // <editor-fold defaultstate="collapsed" desc=" Attributes ">
    
    private OrbitView view;
    private Model model;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">
    
    public TouchInput(OrbitView view, Model model)
    {
        if(view == null || model == null)
            throw new NullPointerException();
        
        this.view = view;
        this.model = model;
    }
    
    // </editor-fold>
        
    
    // <editor-fold defaultstate="collapsed" desc=" DragUtil ">
    
    private Angle computeLatOrLonChange(View view, Globe globe, double amount)
    {
        Vec4 eye = view.getEyePoint();
        if (eye == null)
            return null;

        double normAlt = (eye.getLength3() / globe.getMaximumRadius()) - 1.0;
        if (normAlt < 0.0)
            normAlt = 0.0;
        else if (normAlt > 1.0)
            normAlt = 1.0;

        double coeff = (0.00001 * (1.0 - normAlt)) + (0.2 * normAlt);

        return Angle.fromDegrees(coeff * amount);
    }    
    
    private void drag(java.awt.Point move) 
    {        
    	if(move.x == 0 && move.y == 0)
    		return;
    	
        final Globe globe = model.getGlobe();
        if (globe == null)
            return;
                
        Angle forwardAngle = this.computeLatOrLonChange(this.view, globe, move.y);
        Angle rightAngle = this.computeLatOrLonChange(this.view, globe, -move.x);
        Quaternion forwardQuat = this.view.createRotationForward(forwardAngle);
        Quaternion rightQuat = this.view.createRotationRight(rightAngle);
        Quaternion quaternion = forwardQuat.multiply(rightQuat);
                
        view.setRotation(view.getRotation().multiply(quaternion));
        
        if (view.hasStateIterator())
            view.stopStateIterators();

        view.firePropertyChange(AVKey.VIEW, null, view);        
    }        
    

    // </editor-fold>
       
    // <editor-fold defaultstate="collapsed" desc=" Tracking ">
    
    private final Tracker<Touch> tracker = Trackers.bestMatch ( new Matcher<Touch,Double>()
    {
        @Override
        public Double match (Touch a, Touch b)
        {
            final double distance = Touch.Utils.distance (a, b);
            return -distance;//return ( distance <= 0.1 ) ? distance : null;
        }

        @Override
        public int compare (Double a, Double b)
        {
            return a.compareTo (b);
        }
    }, true);
    
    public void advanceFrame(Iterator<? extends Touch> touchIt)
    {
        // <editor-fold defaultstate="collapsed" desc=" Vision ">
                    
//        try
//        {                
//            final Thread t = new Thread ("Multitouch.Demo.Capture")
//            {                    
//                //final private TLCapture capture = new TLCapture (scene, width, height);
//
//                //final private byte[]  image  = new byte[width*height];
//
//                @Override public void run ()
//                {
//                    //Capture device = Capture.startDevice(width, height);
//                    while(this == currentThread())
//                    {
//                        //device.capture (image, Capture.Flip.VERTICAL);//allOf(Capture.Flip.class));
//                        //capture.capture(image);
//                    }
//                }
//            };
//            t.setDaemon (true);
//            t.setPriority (Thread.NORM_PRIORITY);//(Thread.NORM_PRIORITY + Thread.MAX_PRIORITY)/2);
//            t.start ();
//
//
//        }
//        catch(Exception e)
//        {
//            throw new RuntimeException ("Could not initialize vision", e);
//        }
            
        // </editor-fold>
        
        while(touchIt.hasNext ())
        {
            final Touch touch = touchIt.next ();
            tracker.track (touch);
        }
        
        final java.awt.Point move = new java.awt.Point(0,0);
        
        tracker.nextFrame(new Observer.Adapter<Touch>() 
        {
            @Override
            public void updatedTracking(Touch last, Touch current) 
            {
                //System.out.println("l: " + last.getX() + " " + last.getY());
                //System.out.println("c: " + current.getX() + " " + current.getY());
                move.x += current.getX() - last.getX();
                move.y += current.getY() - last.getY();
            }
        });       
               
        
        //System.out.println(move.x + " " + move.y);
        drag(move);        
    }
    
    // </editor-fold>    
}
