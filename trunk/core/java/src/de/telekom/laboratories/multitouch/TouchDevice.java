/*
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.telekom.laboratories.multitouch;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public abstract class TouchDevice {
    
    private final Collection<TouchListener> listeners = new ArrayList<TouchListener>();
    
    public TouchDevice() {
    }
    
//    protected void fireXXX(TouchEvent e) {
//        for(TouchListener l : listeners) {
//            //l.xxx(e);
//        }
//    }
    
    
    public abstract void update(); // readAndDispatch();
    
    
    public void addTouchListener(TouchListener l) {
        listeners.add( l );
    }
    
    public void removeTouchListener(TouchListener l) {
        listeners.remove( l );
    }
    
    public TouchListener[] getTouchListeners() {
        return listeners.toArray( new TouchListener[ listeners.size() ] ) ;//0] );
    }
    
    
}
