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

package de.telekom.laboratories.multitouch.demo.vvvv;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortIn;
import java.net.SocketException;
import java.util.Date;

/**
 * An OpenSound Control (OSC) based client, which receives frames containing a list of tracked ojects (id:float, x:float, y:float).
 * @author Michael Nischt
 * @version 0.1
 */
public class OSCTrackerClient {
    
    private final OSCPortIn inPort;

    
    /**
     * Instances the tracker, which will listen to the specified port.
     * @param port the port to listen to
     * @throws java.net.SocketException if the there is problem binding,connecting or routing problem with socket.
     */
    public OSCTrackerClient(int port) throws SocketException
    {
        inPort = new OSCPortIn(port);

        // The vvvv tracker send a frame containing a list of blobs within a single message
        // adressed by '/blobs', each blob itself consists of 3 flaoting point values defining its:
        // x-coordinate, y-coordinate and id
        // Finger-Touch-Event correspondence:
        // -- finger-pressed:  an id is *not* contained in the last frame but in the current one
        // -- finger-moved:    an id is contained in the last frame and also in the current one
        // -- finger-released: an id is contained in the last frame but *not* in the current one
        inPort.addListener("/blobs", new OSCListener() {
            
            // Note: listing is done in another Thread, because blocking IO is used by JavaOSC, 
            // therefore we have to buffer and synchronize the listener callbacks            
            public void acceptMessage(Date time, OSCMessage message)
            {
//                System.out.println("lala");
                final Object[] args = message.getArguments();                
                
                if(args.length <= 3)
                    return;
                
                System.out.println(message.getAddress() + ": " + (args.length/3));
                
                for(int i=0; i<args.length; i+=3) {

                    final float id = (Float) args[i+2];
                    final float x  = (Float) args[i+0];
                    final float y  = (Float) args[i+1];
                    
                    System.out.println("blod: id(" + id + ") x(" + x + ") y(" + y + ")");
                }
            }
        });
    }
    
    /**
     * Returns whether the tracking is currently active or not.
     * @return <CODE>true</CODE>, if the tracking is currently active
     */
    public boolean isTracking()
    {
        return inPort.isListening();
    }
    
    /**
     * Actviates or deactivates the tracking process, if the object is not already in the desiered state.
     * @param on the state, which defines if the tracking takes place or not.
     */
    public void setTracking(boolean on)
    {        
        // Starts/stops tracking(listing), 
        // if desiered and we're not already in that state
        
        // Note: listing is done in another Thread, because blocking IO is used by JavaOSC, 
        // therefore we have to buffer and synchronize the listener callbacks       
        if(on && !inPort.isListening()) {
            inPort.startListening();
        } else if(!on && inPort.isListening()) {
            inPort.stopListening();
        }        
    }
    
    public static void main(String... args) throws Exception
    {
        OSCTrackerClient tracker = new OSCTrackerClient(9000);
        tracker.setTracking(true);
        
        while(tracker.isTracking())
        {
            try
            {
                Thread.sleep(500);
            }
            catch(InterruptedException e) {}
        
        }
        
        System.out.println("finished");
    }
            
    
}
