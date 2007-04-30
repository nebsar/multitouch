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

import java.util.Random;

/**
 * @author Michael Nischt
 * @version 0.1
 */
public class Gallery {
    
    //Play play;
    private final Preview[] previews;
    
    public Gallery(int previews) {
        final Random r = new Random();
        
        this.previews = new Preview[previews];
        for(int i=0; i<this.previews.length; i++) 
        {
            final Preview p = new Preview(i);
            this.previews[i] = p;
                    
            { // position
                final float x = r.nextFloat(),
                            y = r.nextFloat(),
                            z = 0.0f;//i / (float) previews;
                p.position( x , y , z );
            }
            { // orientation           
                final float angle = r.nextFloat();
                p.orientation( angle );
            }
            
            // move to controller
            for(int j=i-1; j>=0; j--) 
            {
                final Preview below = this.previews[j];
                if( overlap( p, below ) )
                {
                    p.setPosition( p.getPosition().z( below.getPosition().getZ() + (1.0f/previews) ) );
                    break;
                }
            }
        }
    }    
    
    private static boolean overlap(Preview a, Preview b) {
        return false;
    }
    
}
