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



package de.telekom.laboratories.capture;

//~--- non-JDK imports --------------------------------------------------------

import de.telekom.laboratories.capture.spi.CaptureService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//~--- JDK imports ------------------------------------------------------------

import java.util.ServiceLoader;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public interface Device
{
    boolean isConnected();
    
    void connect(VideoMode mode, Aquire aquire) throws NullPointerException, IllegalStateException;    
    void disconnect();

    void capture() throws IllegalStateException;

    public static class Registry
    {
        private static final Registry INSTANCE = new Registry();
        private Device[] device;

        private Registry() {}

        public static Registry getLocalRegistry()
        {
            return INSTANCE;
        }
        
        public Device[] getDevices() {
            final List<Device> devices = new ArrayList<Device>();
            
            final ServiceLoader<CaptureService> services = ServiceLoader.load(CaptureService.class);
            for (CaptureService service : services) {
                devices.addAll(Arrays.asList(service.getDevices()));

            }
            
            return devices.toArray(new Device[devices.size()]);
        }
    }
}