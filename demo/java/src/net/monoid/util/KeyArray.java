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
public abstract class KeyArray implements Keys{
    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    private int lastKey = 0;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Constructors ">
    
    /** Creates a new instance of KeyFrames */
    public KeyArray() {        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    protected void checkOrdering() throws IllegalStateException {
        int count = this.getCount();
        
        if(count < 1)
            throw new IllegalStateException();//TODO: message
        
        
        double prev = this.getKey(0);
        for(int i=1; i<count; i++) {
            double key = this.getKey(i);            
            if(key <= prev)
                throw new IllegalStateException(String.format("Key[%d] (%f) cannot be greater or equal than Key[%d] (%f)", (i-1), prev, i, key));//TODO: message
            prev = key;
        }        
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Keys ">
    
    public double getStart() {
        return this.getKeyTime(0);
    }
    
    public double getEnd() {
        return this.getKeyTime(this.getCount()-1);
    }
    
    public double getDuration() {
        return this.getEnd()-this.getStart();
    }
    
    public abstract double getKey(int index);
    public abstract void setKey(int index, double key) throws UnsupportedOperationException, IllegalArgumentException, IndexOutOfBoundsException;
    
    public double getKeyTime(int index) {
        return getKey(index);
    }
    
    public boolean getMarginalKeys(double time, int[] keys) {
        int count = this.getCount();
        time = Math.max(this.getStart(),Math.min(time,this.getEnd()));
        lastKey = Math.max(0, Math.min(lastKey, count-1));
                
        if(this.getCount() < 2) {
            keys[0] = keys[1] = 0;
            return true;
        }
        
        while(lastKey > 0 && time < this.getKey(lastKey))
            lastKey--;
        
        while(lastKey < (count-2) && time > this.getKey(lastKey+1))
            lastKey++;
        
        // TODO: amke them equal, if returning true
        keys[0] = lastKey;
        keys[1] = lastKey+1;        
        
        assert(time >= this.getKey(keys[0]) && time <= this.getKey(keys[1]));
        
        return this.getKeyTime(keys[0]) == time || this.getKeyTime(keys[1]) == time;
    }
    
    public double getInterpolationFactor(double time, int[] keys) {
        int count = this.getCount();
        time = Math.max(this.getStart(),Math.min(time,this.getEnd()));
        lastKey = Math.max(0, Math.min(lastKey, count-1));
        
        
        if(this.getCount() < 2) {
            keys[0] = keys[1] = 0;
            return 0.0;
        }
        
        while(lastKey > 0 && time < this.getKey(lastKey))
            lastKey--;
        
        while(lastKey < (count-2) && time > this.getKey(lastKey+1))
            lastKey++;
        
        // TODO: amke them equal time is matching getKey(keys[0]) or getKey(keys[1])
        keys[0] = lastKey;
        keys[1] = lastKey+1;
        
        assert(time >= this.getKey(keys[0]) && time <= this.getKey(keys[1]));
                
        double factor = ((this.getKey(keys[1]) - time) / (this.getKey(keys[1]) - this.getKey(keys[0])));
        
        assert(factor >= 0.0 && factor <= 1.0);

        return factor;
    }   
    
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc=" Float ">
    
    public static class Float extends KeyArray {
        
        private final float[] times;
        
        public Float(float[] times) throws IllegalArgumentException {
            this(times, false);
        }
        
        public Float(float[] times, boolean unsafe) throws IllegalArgumentException {           
            this.times = unsafe ? times : times.clone();
            try {
                this.checkOrdering();
            }
            catch(IllegalStateException e) {
                throw new IllegalArgumentException(e.getMessage(),e); //TODO: Message
            }
        }
        
        public int getCount() {
            return this.times.length;
        }
        
        @Override
        public double getKey(int index) {
            return this.times[index];
        }
        
        @Override
        public void setKey(int index, double key) throws UnsupportedOperationException, IllegalArgumentException, IndexOutOfBoundsException {
            if(index > 0 && (this.getKey(index-1) >= key))
                throw new IllegalArgumentException(); //TODO: message
            else if(index < (this.getCount()-2) && (key <= this.getKey(index+1)))
                throw new IllegalArgumentException(); //TODO: message
            else
                this.times[index] = (float) key;
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Double ">
    
    public static class Double extends KeyArray {
        
        private final double[] times;
        
        public Double(double[] times) throws IllegalArgumentException {
            this(times, false);
        }
        
        public Double(double[] times, boolean unsafe) throws IllegalArgumentException {           
            this.times = unsafe ? times : times.clone();
            try {
                this.checkOrdering();
            }
            catch(IllegalStateException e) {
                throw new IllegalArgumentException(e); //TODO: Message
            }
        }
        
        public int getCount() {
            return 0;
        }
        
        @Override
        public double getKey(int index) {
            return this.times[index];
        }
        
        @Override
        public void setKey(int index, double key) throws UnsupportedOperationException, IllegalArgumentException, IndexOutOfBoundsException {
            if(index > 0 && (this.getKey(index-1) >= key))
                throw new IllegalArgumentException(); //TODO: message
            else if(index < (this.getCount()-2) && (key <= this.getKey(index+1)))
                throw new IllegalArgumentException(); //TODO: message
            else
                this.times[index] = key;
        }
    }    
    
    // </editor-fold>    
}
