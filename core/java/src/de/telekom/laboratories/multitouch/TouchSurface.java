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
public abstract class TouchSurface {
    
    public interface State {
        double at(int x, int y);
    }

    
    private final Collection<TouchListener> listeners = new ArrayList<TouchListener>();
    
    
    private final int width, height;
    private final double[] pressure;
    
    
    public TouchSurface(int width, int height) throws IllegalArgumentException {
        if(width < 1 || height < 1) {
            throw new IllegalArgumentException();
        }
        this.width  = width;
        this.height = height;
        
        pressure = new double[width * height];
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void update(State state) { // readAndDispatch();
        final int width = this.width, height = this.height;
        
        for(int y=0; y<height; y++) {
            final int offset = y*width;
            for(int x=0; x<width; x++) {
                pressure[offset+x] = state.at(x,y);
            }
        }
        
        
    }
    
    
//    protected void fireXXX(TouchEvent e) {
//        for(TouchListener l : listeners) {
//            //l.xxx(e);
//        }
//    }
        
    
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
