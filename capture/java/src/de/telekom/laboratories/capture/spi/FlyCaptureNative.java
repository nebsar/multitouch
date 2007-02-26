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

import de.telekom.laboratories.capture.VideoMode;
import java.nio.Buffer;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
class FlyCaptureNative {
    
    private FlyCaptureNative() {}
    
    native static int deviceHandles();
    native static long createDeviceHandle(int index);
    native static void releaseDeviceHandle(long handle);
    
    native static boolean connect(long handle, VideoMode mode);
    native static void disconnect(long handle);
    
    native static boolean capture(long handle, Buffer dest);
    
}
