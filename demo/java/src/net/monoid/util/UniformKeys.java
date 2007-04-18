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

package net.monoid.util;

/**
 * @author Michael Nischt
 * @version 0.1
 */
public class UniformKeys implements Keys {
    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    private final int oneLessKeyCount;
    private final double frameTime;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructors ">
    
    /** Creates a new instance of UniformKeys */
    public UniformKeys(int numKeys) {
        this.frameTime = 1.0/--numKeys;
        this.oneLessKeyCount = numKeys;        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    public int getCount() {
        return this.oneLessKeyCount+1;
    }
        
    public double getKeyTime(int index) {
        return index*this.frameTime;
    }
    
    public double getStart() {
        return 0.0;
    }

    public double getEnd() {
        return 1.0;
    }
    
    public double getDuration() {
        return 1.0;
    }
    
    public boolean getMarginalKeys(double time, int[] keys) {
        time = Math.max(0.0,Math.min(time,1.0));
        
        final double key = time*oneLessKeyCount;
        keys[0] = (int) Math.floor(key);
        keys[1] = (int) Math.ceil(key);
        
        return keys[0] != keys[1];
    }    
    
    public double getInterpolationFactor(double time, int[] keys) {
        time = Math.max(0.0,Math.min(time,1.0));
        
        final double key = time*oneLessKeyCount;
        keys[0] = (int) Math.floor(key);
        keys[1] = (int) Math.ceil(key);
        
        //if(keys[0] == keys[1])
        //    return 0.0;
        
        return (time-(keys[0]*this.frameTime))/this.frameTime;
    }
    
    // </editor-fold>
}
