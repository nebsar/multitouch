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
public class RangeWarp {
    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    private final double start;
    private final double end;
    
    private final double duration;
    private final double invDuration;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructors ">    
    
    /** Creates a new instance of RangeWarp */
    public RangeWarp(double start, double end) {
        if(start>=end)
            throw new IllegalArgumentException(); //TODO: message
        
        this.start = start;
        this.end = end;
        
        this.duration = this.end-this.start;
        this.invDuration = 1.0/this.duration;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">    
    
    public double getStart() { return this.start; }
    public double getEnd() { return this.end; }
    

    public double warpToUniform(double time) {        
        time = Math.max(this.start,Math.min(time,this.end));
        return ((time-this.start)*(this.invDuration));
    }
    
    public double warpFromUniform(double time) {
        time = Math.max(0.0,Math.min(time,1.0));
        return time*this.duration+this.start;
    }    
    
    
    public double warpTo(double time, double start, double end) {
        if(start>=end)
            throw new IllegalArgumentException(); //TODO: message
        
        time = Math.max(this.start,Math.min(time,this.end));
        return ((time-this.start)*(this.invDuration))*(end-start)+start;
    }

    private final double warpToFast(double time, double start, double end) {
        
        time = Math.max(this.start,Math.min(time,this.end));
        return ((time-this.start)*(this.invDuration))*(end-start)+start;
    }
    
    public double warpFrom(double time, double start, double end) {
        if(start>=end)
            throw new IllegalArgumentException(); //TODO: message
        
        time = Math.max(start,Math.min(time,end));
        return ((time-start)/(end-start))*(this.duration)+this.start;
    }

    private final double warpFromFast(double time, double start, double end) {        
        time = Math.max(start,Math.min(time,end));
        return ((time-start)/(end-start))*(this.duration)+this.start;
    }    
    
    
    public Keys warp(Keys keys) {
        final Keys k = keys;
        return new Keys() {
            
            public int getCount() { return k.getCount(); }            

            public double getKeyTime(int index) {
                return warpFromFast(k.getKeyTime(index), k.getStart(), k.getEnd());
            }
            
            public double getStart() { return start; }

            public double getEnd() { return end; }
            
            public double getDuration() { return this.getEnd() - this.getStart(); }
            
            public boolean getMarginalKeys(double time, int[] keys) {
                return k.getMarginalKeys(warpToFast(time, k.getStart(), k.getEnd()), keys);
            }
            public double getInterpolationFactor(double time, int[] keys) {
                return k.getInterpolationFactor(warpToFast(time, k.getStart(), k.getEnd()), keys);
            }
        };
    }
    
    // </editor-fold>
}
