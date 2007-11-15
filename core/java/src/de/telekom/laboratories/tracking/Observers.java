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

package de.telekom.laboratories.tracking;

import java.io.PrintStream;

/**
 * Offers factory functionality for common implemetations of the {@link Observer Observer} interface.
 * @author Michael Nischt
 * @version 0.1
 */
public class Observers 
{
    private Observers() {}
    
    /**
     * A stateless object, which ignores all notification.
     */
    public static <Feature> Observer<Feature> nullObserver()
    {
        return new Observer.Adapter<Feature>() {};
    }    
    
    /**
     * A stateless object, which prints all notification.
     */
    public static <Feature> Observer<Feature> printObserver(PrintStream out)
    {
        return new PrintObserver<Feature>(out);
    }
    
    // <editor-fold defaultstate="collapsed" desc=" PrintObserver ">
    
    private static class PrintObserver<Feature> implements Observer<Feature>
    {
        private final PrintStream out;
        
        public PrintObserver(PrintStream out)
        {
            if(out == null) throw new NullPointerException();
            
            this.out = out;
        }
        
        public void startedTracking  (Feature current)
        {
            out.printf("startedTracking (%s)\n", current);
        }

        public void updatedTracking  (Feature last, Feature current)
        {
            out.printf("updatedTracking (%s,%s)\n", last, current);
        }

        public void finishedTracking (Feature last)
        {
            out.printf("finishedTracking (%s)\n", last);
        }
    }
    
    // </editor-fold>   
}
