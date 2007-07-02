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
 * An interface for tracking and correlating features.
 * @param Feature the type of the objects to be tracked.
 * @author Michael Nischt
 * @version 0.1
 */
public interface Tracker<Feature>
{
    //Note: Results are unpredictable if the feature changes (during two rounds / correlation). 
    /**
     * Tracks a feature for correlation with those tracked during the last round.
     * @param feature the feature to be tracked.
     */
    void track (Feature feature);
    
    /**
     * Correlates the tracked features from the the current and the last frame.
     * @param observer will notified about the tracking state of each feature
     */    
    void nextFrame (Observer<? super Feature> observer);
}
