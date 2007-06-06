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

/**
 * An interface providing call-back methods about the tracking state of features from different frames.
 * @param Feature the type of the objects to be tracked.
 * @author Michael Nischt
 * @version 0.1
 */
public interface Observer<Feature>
{
    /**
     * Called if a new feature is recognized.
     * @param current the newly recognized feature
     */
    void startedTracking  (Feature current);    
    
    /**
     * Called if two feature from successive tracking frames belong together. 
     * (See the individual {@link Tracker Tracker} {@link Trackers implemetations}, about their correlation rules.
     * @param last 
     * @param current 
     */
    void updatedTracking  (Feature last, Feature current);

    /**
     * Called if a former recognized feature is not present anymore.
     * @param last the feature not present anymore
     */
    void finishedTracking (Feature last);
    
    /**
     * A stateless object, which ignores all notification.
     */
    final Observer IGNORE = new Adapter() {};
    
    // <editor-fold defaultstate="collapsed" desc=" Blind ">
    
    /**
     * A implementation, which ignores the notification with the primary usage to implemeoverride 
     * @param Feature the type of the objects to be tracked.
     */
    static abstract public class Adapter<Feature> implements Observer<Feature>
    {
        /**
         * To be used for <code>super>/code>-constructor in classes extending this one.
         */
        protected Adapter() {}
        
        public void startedTracking  (Feature current) {}        
        public void updatedTracking  (Feature last, Feature current) {}
        public void finishedTracking (Feature last) {}
    }
    
    // </editor-fold>
}