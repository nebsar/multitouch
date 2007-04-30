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

package de.telekom.laboratories.multitouch.demo.gallery;

import java.nio.ByteBuffer;

/**
 * @author Michael Nischt
 * @version 0.1
 */
class TLCapture {

    // <editor-fold defaultstate="collapsed" desc=" TL ">
    
    public static interface TL {                
        
        void flip(boolean x);
        void capture(int width, int height, ByteBuffer data);
    }
    
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    private final int width, height;    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Initializers ">
    
    public TLCapture(int width, int height) {
        if(width <= 0 || height <= 0)
        {
            throw new IllegalArgumentException();
        }
        this.width = width;
        this.height = height;
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    public void capture(TL tl) {
        tl.capture(width, height, null);
    }
    
    // </editor-fold>
}
