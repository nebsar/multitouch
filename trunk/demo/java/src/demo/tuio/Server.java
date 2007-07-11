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

package demo.tuio;

import static java.util.EnumSet.noneOf;
import java.util.EnumSet;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static de.telekom.laboratories.tracking.Trackers.uniqueMatch;
import de.telekom.laboratories.tracking.Tracker;




import static demo.Capture.startDevice;
import static demo.Capture.Flip;
import static demo.Capture.Flip.HORIZONTAL;
import static demo.Capture.Flip.VERTICAL;
import demo.Capture;

import static demo.tuio.Touch.matcher;

import com.illposed.osc.OSCPortOut;
import de.telekom.laboratories.tracking.Matcher;
import de.telekom.laboratories.tracking.Observer;




/**
 * @author Michael Nischt
 * @version 0.1
 */
public class Server 
{    

    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    private final Observer<Touch> observerProxy = new Observer<Touch>()
    {
        @Override
        public void startedTracking(Touch current)
        {
            Server.this.startedTracking(current);
        }

        @Override
        public void updatedTracking(Touch last, Touch current)
        {
            Server.this.updatedTracking(last, current);
        }

        @Override
        public void finishedTracking(Touch last)
        {
            Server.this.finishedTracking(last);
        }                    
    };
    
    private final OSCPortOut sender;
    private Capture camera;
    private Tracker<Touch> tracker;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initialization ">
    
    public Server() throws SocketException, UnknownHostException
    {
        this(new OSCPortOut());
    }

    public Server(int port) throws SocketException, UnknownHostException
    {
        this(InetAddress.getLocalHost(), port);
    }

    public Server(String host, int port) throws SocketException, UnknownHostException
    {
        this(new OSCPortOut(InetAddress.getByName(host), port));
    }    
    
    public Server(InetAddress host, int port) throws SocketException
    {
        this(new OSCPortOut(host, port));
    }
    
    private Server(OSCPortOut sender)
    {
        this.sender = sender;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    public boolean isRunning()
    {
        return !(camera == null || camera.isDisposed());
    }
    
    public Runnable start(int width, int height)
    {
        return start(width, height, false, false);
    }
    
    public Runnable start(int width, int height, boolean flipHorizontal, boolean flipVertical)
    {        
        if(isRunning()) throw new IllegalStateException("Server already started!");                               
        
        camera = startDevice(width, height);
        final byte[] image = new byte[width*height];
        
        final EnumSet<Flip> flip = noneOf(Flip.class);
        if(flipHorizontal)  flip.add(HORIZONTAL);
        if(flipVertical)    flip.add(VERTICAL);
        
        tracker = uniqueMatch( matcher(width, height) );

        return new Runnable()
        {
            @Override
            public void run()
            {
                if(!isRunning()) throw new IllegalStateException("Server is not running!");
                
                camera.capture(image, flip);  
                
                tracker.nextFrame(Server.this.observerProxy);
                
                Server.this.sendFrame();
            }
        };
        
        
    } 
    
    public void stop() 
    {
        if(!isRunning()) throw new IllegalStateException("Server already stopped!");
        camera.dispose();
        camera = null;
    }
    
    private void startedTracking(Touch current)
    {

    }

    private void updatedTracking(Touch last, Touch current)
    {

    }

    private void finishedTracking(Touch last)
    {

    }      
    
    private void sendFrame()
    {
        
    }
    
    // </editor-fold>
}
