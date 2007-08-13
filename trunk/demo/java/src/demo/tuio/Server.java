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

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
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
import de.telekom.laboratories.tracking.Observer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;




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
    private TLCapture capture;;
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
        
        capture = new TLCapture(width, height);
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
                final Touch[] touches = capture.capture(image);
                for(Touch t : touches) tracker.track(t);
                
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

    private static class Alive
    {
        private final SortedSet<Integer> ids = new TreeSet<Integer>();
        
        public int spawn() 
        {
            if(ids.isEmpty()) 
            {
                ids.add(1);
                return 1;
            }
            final int min = ids.first(), max = ids.last();
            final int id = (min > 1) ? (min-1) : (max+1);
            ids.add(id);
            return id;
        }

        public void die(int id) 
        {
            ids.remove(id);
        }

        public Object[] toArray()
        {
            final Object[] alive = new Object[ids.size()+1];
            int index = 0;
            alive[index++] = "alive";
            for(int id : ids)
            {
                alive[index++] = id;
            }
            return alive;
        }
    }

    class Update
    {
        int id;
        boolean dirty; // only update if true
        private float motionAcceleration;
        private float rotationAcceleration;
        Update(int id) 
        {
            this.id = id;
            dirty = true;
        }
    }

    private final Alive alive = new Alive();
    private final Map<Touch, Update> touches = new HashMap<Touch, Update>();
    private boolean sendAlive = false;
    private int updated = 0;
    private int frame = 0;

    private void startedTracking(Touch current)
    {
        touches.put(current, new Update( alive.spawn() ) );
        updated++;
    }

    private void updatedTracking(Touch last, Touch current)
    {
        final Update update = touches.remove(last);
        update.dirty = true;
        //TODO: update accelrations
        touches.put( current, update ); 
        updated++;
    }

    private void finishedTracking(Touch last)
    {
        final Update update= touches.remove( last );
        alive.die(update.id);
        sendAlive = true;
    }      
    
    private void sendFrame()
    {        
        if(updated > 0)
        {
            System.out.println("send: " + updated);
            
            final OSCPacket[] setPkgs = new OSCPacket[updated];
            int index=0;
            for(Map.Entry<Touch,Update> entry : touches.entrySet())
            {
                final Update update = entry.getValue();

                if(!update.dirty) continue;
                else update.dirty = false;

                final Touch touch = entry.getKey();

                setPkgs[index++] = new OSCMessage("/tuio/2Dcur", new Object[] 
                { "set", 
                    update.id, touch.getX(), touch.getY(), 
                    update.motionAcceleration, update.rotationAcceleration 
                } );
            }

            try 
            {
                sender.send(new OSCBundle(setPkgs));
            }
            catch(IOException ioe)
            {
                stop();
            }

            updated = 0;
        }


        if(sendAlive)
        {
            final OSCPacket aliveMsg = new OSCMessage("/tuio/2Dcur",
                alive.toArray()
            );
            try 
            {
                sender.send(aliveMsg);
            }
            catch(IOException ioe)
            {
                stop();
            }            
        }

        final OSCPacket fseqMsg = new OSCMessage("/tuio/2Dcur",
            new Object[] { "fseq", (++frame % Integer.MAX_VALUE) }
        );
        
        try 
        {
            sender.send(fseqMsg);
        }
        catch(IOException ioe)
        {
            stop();
        }        
    }
    
    // </editor-fold>
    
    public static void main(String... args) throws Exception
    {
        Server server;
        if(args != null && args.length > 0) server = new Server(Integer.parseInt(args[0]));
        else server = new Server();
        
        final Runnable run = server.start(1024, 768, true ,true);
        while(true) run.run();
    }
}
