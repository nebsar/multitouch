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

//~--- non-JDK imports --------------------------------------------------------

import de.telekom.laboratories.capture.Aquire;
import de.telekom.laboratories.capture.Device;
import de.telekom.laboratories.capture.VideoMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;



/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class FlyCaptureService extends CaptureService
{
    
    public FlyCaptureService()
    {
        try {
            System.load("C:\\nischt.michael\\Code\\Multitouch\\Capture.Win32\\dist\\FlyCaptrueService.dll");        
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public Device[] getDevices()
    {
        Device[] devices;
                
        try {
            devices = new Device[] { new FlyCaptureDevice(0) };
        } catch(Exception e) {
            devices = new Device[0];
            System.out.println(e.getMessage());
            e.printStackTrace();            
        }
        
        return devices;
    }

    private static class FlyCaptureDevice implements Device
    {
        private final long handle;    // = 0;
        private Aquire aquire;
        private ByteBuffer buffer;

        private FlyCaptureDevice(int index)
        throws IllegalStateException
        {
            handle = FlyCaptureNative.createDeviceHandle(index);
            if(handle == 0) {
                throw new IllegalStateException();
            }

            final Runnable cleanUp = new Runnable()
            {
                public void run()
                {
                    FlyCaptureNative.releaseDeviceHandle(handle);
                }
            };

            Runtime.getRuntime().addShutdownHook(new Thread(cleanUp));
        }

        public boolean isConnected()
        {
            return aquire != null;
        }

        public void connect(VideoMode mode, Aquire aquire) throws NullPointerException, IllegalStateException
        {
            if(mode == null || aquire == null) {
                throw new NullPointerException();
            }
            if(isConnected()) {
                disconnect();
            }
            
           
            if(!FlyCaptureNative.connect(handle, mode)) {
                throw new IllegalStateException();
            }
            
            this.aquire = aquire;
            
            final int bytes = mode.getWidth() * mode.getHeight() * mode.getFormat().size();
            if(buffer == null || buffer.capacity() != bytes)
            {
                buffer = ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder());
            }
        }
        
        public void disconnect() {
            if(isConnected()) {
                FlyCaptureNative.disconnect(handle);
            }
            this.aquire = null;
        }

        public void capture() throws IllegalStateException
        {
            if (!isConnected()) {
                throw new IllegalStateException();
            }
            
            buffer.rewind();
            final boolean capture = FlyCaptureNative.capture(handle, buffer);
            if(!capture) {
                disconnect();
                throw new IllegalStateException();
            }
            aquire.capture(buffer);
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
