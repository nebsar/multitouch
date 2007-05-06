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

package .machina.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.annotation.CallingConvention;
import com.sun.jna.annotation.NativeFunction;
import com.sun.jna.annotation.NativeLibrary;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
@NativeLibrary(name="GDI32", convention=CallingConvention.STDCALL) // stdcall for Win32
public interface GDI32 {
    GDI32 INSTANCE = Native.loadLibrary(GDI32.class);
    
    @NativeFunction
    Pointer CreateRoundRectRgn(int left, int top, int right, int bottom, int w, int h);
}