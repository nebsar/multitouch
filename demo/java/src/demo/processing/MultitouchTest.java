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

package demo.processing;

import static java.lang.Math.round;

import static de.telekom.laboratories.tracking.Trackers.uniqueMatch;
import de.telekom.laboratories.tracking.Tracker;
import de.telekom.laboratories.tracking.Matcher;
import de.telekom.laboratories.tracking.Observer;

import static demo.processing.Touch.distance;
import static demo.Capture.startDevice;
import demo.Capture;
import java.util.LinkedList;
import java.util.Queue;


import processing.core.PApplet;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class MultitouchTest extends PApplet {

    private final static int CAPTURE_WIDTH = 1024, CAPTURE_HEIGHT = 768;
        
    private final byte[] image = new byte[CAPTURE_WIDTH*CAPTURE_HEIGHT];
    private final ImageLabeling imageLabels = new ImageLabeling(CAPTURE_WIDTH, CAPTURE_HEIGHT);
    
    private final Tracker<Touch> tracker = uniqueMatch ( new Matcher<Touch,Double>()
    {
        public Double match (Touch a, Touch b)
        {
            final double distance = distance (a, b);
            return ( distance <= 0.1 ) ? -distance : null;
        }

        public int compare (Double a, Double b)
        {
            return a.compareTo (b);
        }
    });   
        
    private final Queue<Touch> renderQueue = new LinkedList<Touch>();
    private Capture device;
    
    
    
    @Override
    public void setup() {
        size(1024, 768);
        this.device = startDevice(CAPTURE_WIDTH, CAPTURE_HEIGHT);
    }

    @Override 
    public void draw() 
    {
        //System.out.println("draw");
        device.capture(image, Capture.Flip.HORIZONTAL);
        frameCaptured();  
        
        background(0xFFFFFFFF);
        Touch next;
        color(0);
        while( (next = renderQueue.poll()) != null)
        {
            final float x = CAPTURE_WIDTH * (0.5f * ( next.getX() + 1.0f )); 
            final float y = CAPTURE_HEIGHT * (0.5f * ( next.getY() + 1.0f ));
         
            final float radius = 5;
            
            ellipse(x, y, radius, radius);
        }
    }    
    
    void frameCaptured() 
    {
        //System.out.println("capture");
        
        imageLabels.capture(image, new ImageLabeling.Collect() 
        {
            public boolean collect(Touch touch)
            {
                tracker.track(touch);
                return true;
            }            
        });
        
        tracker.nextFrame(new Observer<Touch>() 
        {
            public void startedTracking(Touch current)
            {
                //System.out.println("started");
                renderQueue.offer(current);
            }

            public void updatedTracking(Touch last, Touch current)
            {
                //System.out.println("updated");
                renderQueue.offer(current);
            }

            public void finishedTracking(Touch last)
            {
                
            }            
        });
    }
    
}
