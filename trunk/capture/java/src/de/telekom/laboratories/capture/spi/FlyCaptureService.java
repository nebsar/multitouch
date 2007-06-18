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



package de.telekom.laboratories.capture.spi;

import de.telekom.laboratories.capture.Aquire;
import de.telekom.laboratories.capture.Device;
import de.telekom.laboratories.capture.VideoMode;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static java.lang.Thread.MIN_PRIORITY;
import static de.telekom.laboratories.capture.spi.FlyCaptureNative.*;


/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class FlyCaptureService extends CaptureService
{
    static final boolean DEBUG = false;
    
    static final private List<FlyCaptureDevice> REF_LIST            = new ArrayList<FlyCaptureDevice>();
    static final private ReferenceQueue<FlyCaptureDevice> REF_QUEUE = new ReferenceQueue<FlyCaptureDevice>();
    
    
    static volatile boolean done;
    
    public FlyCaptureService ()
    {
        if(done) return;
        else done = true;
        
        try
        {
            System.loadLibrary ("FlyCaptrueService");
            
            final Thread disposeAll = new Thread ()
            {
                @Override public void run ()
                {
                    disposeAll ();
                }
            };
            Runtime.getRuntime ().addShutdownHook (disposeAll);
            
            final Thread disposePhantoms = new Thread ()
            {
                @Override public void run ()
                {
                    while( this == Thread.currentThread ())
                    {
                        disposePhantoms ();
                    }
                }
            };
            disposePhantoms.setDaemon (true);
            disposePhantoms.setPriority (MIN_PRIORITY);
            disposePhantoms.start ();
            
        }
        catch (Exception e)
        {
            System.err.println ("------ Error loading FlyCapture JNI Bridge ------" );
            System.err.println (e.getMessage ());
            e.printStackTrace();
            System.err.println ("-------------------------------------------------" );
        }
    }
    
    static private void disposeAll ()
    {
        synchronized(FlyCaptureService.class)
        {
            if (DEBUG) System.out.println ("Disposing all native devices");
            final Iterator<FlyCaptureDevice> devIt = REF_LIST.iterator ();
            while(devIt.hasNext ())
            {
                final FlyCaptureDevice device = devIt.next ();   
                FlyCaptureDevice.JNIReference ref = device.ref;                
                ref.clear();
                devIt.remove();
                if(device.isConnected ()) device.disconnect ();
                FlyCaptureNative.disconnect (ref.handle);
                                    
                if (DEBUG) System.out.println ("Disposed native device");
            }
            //if (DEBUG) System.out.println ("Disposed all native devices");
        }
    }
    
    static private void disposePhantoms ()
    {
        synchronized(FlyCaptureService.class)
        {
            FlyCaptureDevice.JNIReference ref;
            while( (ref = (FlyCaptureDevice.JNIReference) REF_QUEUE.poll () ) != null )
            {
                final FlyCaptureDevice device = ref.device ();                
                ref.clear();
                REF_LIST.remove(device);
                if(device.isConnected ()) device.disconnect ();
                FlyCaptureNative.disconnect (ref.handle);
                
                if (DEBUG) System.out.println ("Disposed native phantom device");
            }
            //if (DEBUG) System.out.println("Disposed all phantom devices");
        }
    }
    
    public Device[] getDevices ()
    {
        final int max = deviceHandles ();
        final List<FlyCaptureDevice> deviceList = new ArrayList<FlyCaptureDevice>( max );
        
        for(int dev=0; dev<max; dev++)
        {
            try
            {
                final FlyCaptureDevice device = new FlyCaptureDevice (dev);
                deviceList.add ( device );
            }
            catch(Exception e)
            {
                System.err.printf ("------ Error creating FlyCapture Device at Port(%02d) -------\n" );
                System.err.println (e.getMessage ());
                e.printStackTrace();
                System.err.println ("-----------------------------------------------------------\n" );
            }
        }
        if(DEBUG && deviceList.isEmpty()) System.err.println ("------ Warning: No FlyCapture Devices found! ------" );
        return deviceList.toArray ( new Device[deviceList.size ()] );
    }
    
    static private class FlyCaptureDevice implements Device
    {
        class JNIReference extends PhantomReference<FlyCaptureDevice>
        {
            final private long handle;    // = 0;
            JNIReference (long handle)
            {
                super (FlyCaptureDevice.this, REF_QUEUE);
                this.handle = handle;
                REF_LIST.add (FlyCaptureDevice.this);                
                
            }
            FlyCaptureDevice device ()
            {
                return FlyCaptureDevice.this;
            }            
        }
        
        final JNIReference ref;
        volatile Aquire aquire;
        private ByteBuffer buffer;
        
        private FlyCaptureDevice (int index)
                throws IllegalStateException
        {
            final long handle = FlyCaptureNative.createDeviceHandle (index);
            if(handle == 0) throw new IllegalStateException ("------ Error creating FlyCapture Device at Port(%02d) -------\n");
            else ref = new JNIReference (handle);
        }
        
        public boolean isConnected ()
        {
            return aquire != null;
        }
        
        public void connect (VideoMode mode, Aquire aquire) throws NullPointerException, IllegalStateException
        {
            synchronized(FlyCaptureService.class)
            {
                if(mode == null || aquire == null)
                {
                    throw new NullPointerException ();
                }
                if(isConnected ())
                {
                    throw new IllegalStateException("Device is already connected");
                }
                
                
                if(!FlyCaptureNative.connect (ref.handle, mode))
                {
                    throw new IllegalStateException ();
                }
                
                this.aquire = aquire;
                
                final int bytes = mode.getWidth () * mode.getHeight () * mode.getFormat ().size ();
                if(buffer == null || buffer.capacity () != bytes)
                {
                    buffer = ByteBuffer.allocateDirect (bytes).order (ByteOrder.nativeOrder ());
                }
            }
        }
        
        public void disconnect ()
        {
            synchronized(FlyCaptureService.class)
            {
                if(!isConnected ())
                {
                    throw new IllegalStateException("Device is not connected");
                }                
                this.aquire = null;
            }
        }
               
        
        public void capture () throws IllegalStateException
        {
            synchronized(FlyCaptureService.class)
            {
                if (!isConnected ())
                {
                    if(REF_LIST.contains(this)) throw new IllegalStateException ();
                    else return;
                }
                
                buffer.rewind ();
                final boolean capture = FlyCaptureNative.capture (ref.handle, buffer);
                if(!capture)
                {
                    System.out.println("exception");
                    disconnect ();
                    throw new IllegalStateException ();
                }
                aquire.capture (buffer);
            }
        }
    }
}
