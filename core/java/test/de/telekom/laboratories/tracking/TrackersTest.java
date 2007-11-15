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


import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.reverse;
import static de.telekom.laboratories.tracking.Trackers.*;

import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class TrackersTest 
{    
    private Double[] from =
    { -10.5, -9.5, -7.5, -6.5, -5.5, -0.5, 0.5, 3.5, 4.5, 5.5, 9.0, 10.0 };
    //reverse
    //{ 10.0, 9.0, 5.5, 4.5, 3.5, 0.5, -0.5, -5.5, -6.5, -7.5, -9.5, -10.5 };
            
    //{ -0.5, 0.5 };
    //{ 0.5, -0.5 };
    
    private Double[] to =    
    { -8.0, -7.0, -6.0, -4.0, -3.0, -0.5, 0.5, 4.0, 5.0, 8.5, 9.5, 10.5 };
    //reverse
    //{ 10.5, 9.5, 8.5, 5.0, 4.0, 0.5, -0.5, -3.0, -4.0, -6.0, -7.0, -8.0};
            
    //{ 0.0, 1.0 };
    //{ 1.0, 0.0 };
    
    
    // <editor-fold defaultstate="collapsed" desc=" matcher ">    
    
    private final Matcher<Double,Double> matcher = new Matcher<Double,Double>() 
    {
        private final double MAX_DISTANCE = 2.0;
        
        public Double match(Double a, Double b) 
        {
            final double distance = abs(a-b);
            
            if (distance > MAX_DISTANCE) 
                return null;
            
            return (MAX_DISTANCE - distance) / MAX_DISTANCE;
        }

        public int compare(Double left, Double right) 
        {
            return left.compareTo(right);
        }
    };
    
    // </editor-fold>
    
    private static class DoublePair
    {
        final double last;
        final double current;
        
        DoublePair(double last, double current)
        {
            this.last = last;
            this.current = current;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DoublePair other = (DoublePair) obj;
            if (this.last != other.last) {
                return false;
            }
            if (this.current != other.current) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + (int) (Double.doubleToLongBits(this.last) ^ (Double.doubleToLongBits(this.last) >>> 32));
            hash = 71 * hash + (int) (Double.doubleToLongBits(this.current) ^ (Double.doubleToLongBits(this.current) >>> 32));
            return hash;
        }        
    }
    
    public TrackersTest()
    {        
    }
    
    
    
    
    
    private static class MatchObserver implements Observer<Double>
    {
        private final Collection<Double>  started;
        private final Collection<DoublePair> updated;
        private final Collection<Double>  finished;

        MatchObserver(Collection<Double>  started, Collection<DoublePair> updated, Collection<Double>  finished)
        {
            if(started == null || updated == null || finished == null)
                throw new NullPointerException();
            
            this.started = started;
            this.updated = updated;
            this.finished = finished;
        }
        
        public void startedTracking  (Double current)
        {
            assertTrue(format("StartedTracking of an feature, which is not there actually (%f)",current), started.remove(current));
        }

        public void updatedTracking  (Double last, Double current)
        {
            assertTrue(format("UpdateTracking did not match correctly (%f,%f)",last,current), updated.remove(new DoublePair(last,current)));
        }

        public void finishedTracking (Double last)
        {
            assertTrue(format("FinishedTracking of an feature, which was not there actually (%f)", last), finished.remove(last));
        }
        
        public void verify()
        {
            assertTrue("Not all new feature were matched correctly",  started.size()+updated.size()+finished.size() == 0);
        }
    }     
    
        
    private MatchObserver started()
    {
        return new MatchObserver(new LinkedList<Double>(asList(from)), Collections.<DoublePair>emptyList(), Collections.<Double>emptyList());
    }

    private MatchObserver finished()
    {
        return new MatchObserver(Collections.<Double>emptyList(), Collections.<DoublePair>emptyList(), new LinkedList<Double>(asList(to)));
    }
    
    private MatchObserver bestMatchObserver(boolean mutually)
    {
        final Collection<DoublePair> updated = new LinkedList<DoublePair>();
        final Collection<Double> started = new LinkedList<Double>();
        final Collection<Double> finished = new LinkedList<Double>();

        finished.add( -10.5 );
        
        updated.add(new DoublePair( -7.5, -8.0 ));
        updated.add(new DoublePair( -7.5, -7.0 ));
        updated.add(new DoublePair( -6.5, -7.0 ));
        updated.add(new DoublePair( -6.5, -6.0 ));
        updated.add(new DoublePair( -5.5, -6.0 ));

        updated.add(new DoublePair( -0.5, -0.5 ));
        updated.add(new DoublePair( +0.5, +0.5 ));

        updated.add(new DoublePair( +3.5, +4.0 ));
        updated.add(new DoublePair( +4.5, +4.0 ));
        updated.add(new DoublePair( +4.5, +5.0 ));
        updated.add(new DoublePair( +5.5, +5.0 ));

        updated.add(new DoublePair( +9.0, +8.5 ));
        updated.add(new DoublePair( +9.0, +9.5 ));

        updated.add(new DoublePair(+10.0,  +9.5));
        updated.add(new DoublePair(+10.0, +10.5));            
        
        started.add( -3.0 );
        
        if(mutually)
        {
            finished.add( -9.5 );                        
            started.add ( -4.0 );
        }
        else
        {
            updated.add(new DoublePair( -9.5, -8.0 ));
            updated.add(new DoublePair( -5.5, -4.0 ));        
        }
        
        return new MatchObserver(started, updated, finished);
    }    
    
    
    @Test
    public void testBestMatchSingle() 
    {
        testBestMatch(false);
    }

    @Test
    public void testBestMatchMutually() 
    {
        testBestMatch(true);
    }
            
    private void testBestMatch(boolean mutually) 
    {
        final Double[] rFrom = from.clone();
        reverse(asList(rFrom));
        
        final Double[] rTo = to.clone();
        reverse(asList(rTo));
        
        testBestMatch(from,  to, mutually);
        testBestMatch(from,  rTo, mutually);
        testBestMatch(rFrom, to, mutually);
        testBestMatch(rFrom, rTo, mutually);
    }
    
    private void testBestMatch(Double[] from, Double[] to, boolean mutually)             
    {
        final Tracker<Double> tracker = Trackers.bestMatch(matcher, mutually);
        
        {
            final MatchObserver started = started();
            
            for(Double that : from)
                tracker.track(that);
            
            tracker.nextFrame(started);
            started.verify();
        }

        {        
            for(Double that : to)
                tracker.track(that);
                
            tracker.nextFrame(bestMatchObserver(mutually));
        }
        
        {
            final MatchObserver finished = finished();
            
            tracker.nextFrame(finished);            
            finished.verify();
        }        
    }    

    
    public static junit.framework.Test suite() 
    { 
        return new JUnit4TestAdapter(TrackersTest.class); 
    }        
}
