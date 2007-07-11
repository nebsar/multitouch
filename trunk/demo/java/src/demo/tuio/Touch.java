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

package demo.tuio;

import de.telekom.laboratories.tracking.Matcher;
import demo.processing.*;

/**
 * @author Michael Nischt
 * @version 0.1
 */
final public class Touch
{
    private static class TouchMatcher implements Matcher<Touch,Float>
    {
        private final float threshold = 0.1f;
        private final float xScale, yScale;

        private TouchMatcher(int width, int height)
        {
            if(width <= 0 || height <= 0) throw new IllegalArgumentException();
        
            float x = 1.0f;
            float y = 1.0f;

            if(width >= height) x = width / (float) height;
            else y = height / (float) width;            
            
            this.xScale = x;
            this.yScale = y;
        }
        
        @Override
        public Float match (Touch a, Touch b)
        {            
            float x = xScale*( b.getX() - a.getX() );
            float y = yScale*( b.getY() - a.getY() );
            float distance = (float) Math.sqrt(x*x+y+y);
            return (distance  <= threshold ) ? distance : null;
        }

        @Override
        public int compare (Float a, Float b)
        {
            return a.compareTo (b);
        }                    
    }
    
    private final float x, y;
    
    public Touch (float x, float y)
    {
        this.x = x;
        this.y = y;
    }
    
    public float getX ()
    { return x; }
    public float getY ()
    { return y; }
    
    public static Matcher<Touch,Float> matcher(int widht, int height)
    {
        return new TouchMatcher(widht, height);
    }
    
}