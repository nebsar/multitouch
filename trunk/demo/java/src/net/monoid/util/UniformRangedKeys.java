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
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class UniformRangedKeys extends UniformKeys {
        
    private final double start;
    private final double end;
    
    private final double duration;
    private final double invDuration;
    
    /** Creates a new instance of RangedKeys */    
    public UniformRangedKeys(int numKeys, double start, double end) {
        super(numKeys);
        if(start >= end)
            throw new IllegalArgumentException(); //TODO: message
                
        this.start = start;
        this.end   = end;
        this.duration = (end-start);
        this.invDuration = 1.0/this.duration;
    }
    
    public double getStart() {
        return this.start;
    }
    
    public double getEnd() {
        return this.end;
    }
    
    public double getDuration() {
        return this.duration;
    }
        
    public double getInterpolationFactor(double time, int[] keys) {
        time = Math.max(this.start,Math.min(time,this.end));
        return super.getInterpolationFactor((time-start)*invDuration, keys);
    }
    
    public boolean getMarginalKeys(double time, int[] keys) {
        time = Math.max(this.start,Math.min(time,this.end));
        return super.getMarginalKeys((time-start)*invDuration, keys);
    }
    
    public double getKeyTime(int key) {
        return super.getKeyTime(key) * duration + start;
    }
    
}
