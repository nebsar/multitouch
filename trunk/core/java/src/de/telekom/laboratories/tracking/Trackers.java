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

import java.util.ArrayList;
import java.util.List;

/**
 * Offers factory functionality for common correlation algorithms.
 * @author Michael Nischt
 * @version 0.1
 */
final public class Trackers 
{
    private Trackers() {}
    
        
    // <editor-fold defaultstate="collapsed" desc=" bestMatch ">    

    /**
     * Returns an implementation, which correlates two features, if the matching quality is maximal regarding one of them. 
     * this is the case iff there is no other match with higher quality.
     * 
     * @param matcher provides a quality measure how well two features match.
     * @return An implementation, which correlates two features, if they match best.
     */     
    static public <Feature, Quality> Tracker<Feature> bestMatch(Matcher<? super Feature, Quality> matcher)
    {
        return bestMatch(matcher, false);
    }
    
    /**
     * Returns an implementation, which correlates two features, if the matching quality is maximal.
     * This can be the case only for one feature in the pair or for both.<br/><br/>
     * 
     * Note: Requiring a mutual match, features are only merged/split, iff the matching is of exactly the same quality.
     * Else, it is sufficient that there is no other match with higher quality.
     * @param matcher provides a quality measure how well two features match.
     * @param mutual specifies whether the quality must be mutually equal for a pair to match successfully.
     * @return An implementation, which correlates two features, if they match best.
     */    
    static public <Feature, Quality> Tracker<Feature> bestMatch(Matcher<? super Feature, Quality> matcher, boolean mutual)
    {
        if(mutual) return bestMutualMatch(matcher);
        else return bestEqualMatch(matcher);
    }
    
    static private <Feature, Quality> Tracker<Feature> bestEqualMatch(Matcher<? super Feature, Quality> matcher)
    {
        // <editor-fold defaultstate="collapsed" desc=" doEvents ">
        
        return new Nearest.Base<Feature, Quality> (matcher) {
            @Override
            protected void doEvents(Observer<? super Feature> observer) {
                // end
                for (Nearest<Feature, Quality> last : lastList) {
                    if (! (last.uniqueMatch() || last.equalMatch(matcher)) ) {
                        observer.finishedTracking( last.touch );
                    }
                }
                
                // update
                {
                    if(lastList.size() < currentList.size()) {
                        for (Nearest<Feature, Quality> last : lastList) {
                            if ( last.uniqueMatch() || last.equalMatch(matcher) ) {
                                observer.updatedTracking( last.touch, last.linked() );
                            }
                        }
                        for (Nearest<Feature, Quality> current : currentList) {
                            if ( !current.uniqueMatch() && current.equalMatch(matcher) ) {
                                observer.updatedTracking( current.linked(), current.touch );
                            }
                        }
                        
                    } else {
                        for (Nearest<Feature, Quality> current : currentList) {
                            if ( current.uniqueMatch() || current.equalMatch(matcher) ) {
                                observer.updatedTracking( current.linked(), current.touch );
                            }
                        }
                        for (Nearest<Feature, Quality> last : lastList) {
                            if ( !last.uniqueMatch() && last.equalMatch(matcher) ) {
                                observer.updatedTracking( last.touch, last.linked() );
                            }
                        }                        
                    }
                }
                // begin
                for (Nearest<Feature, Quality> current : currentList) {
                    if (! (current.uniqueMatch() || current.equalMatch(matcher)) ) {
                        observer.startedTracking( current.touch );
                    }
                }
            }
        };
        
        // </editor-fold>
    }

    static private <Feature, Quality> Tracker<Feature> bestMutualMatch(Matcher<? super Feature, Quality> matcher)
    {
        // <editor-fold defaultstate="collapsed" desc=" doEvents ">
        
        return new Nearest.Base<Feature, Quality> (matcher) {
            @Override
            protected void doEvents(Observer<? super Feature> observer) {
                // end
                for (Nearest<Feature, Quality> last : lastList) {
                    if ( last.isolated() ) {
                        observer.finishedTracking( last.touch );
                    }
                }
                
                // update
                {
                    if(lastList.size() < currentList.size()) {
                        for (Nearest<Feature, Quality> last : lastList) {
                            if ( !(last.isolated() || last.uniqueMatch()) ) {
                                observer.updatedTracking( last.touch, last.linked() );
                            }
                        }
                        for (Nearest<Feature, Quality> current : currentList) {
                            if ( !current.isolated() ) {
                                observer.updatedTracking( current.linked(), current.touch );
                            }
                        }
                        
                    } else {
                        for (Nearest<Feature, Quality> current : currentList) {
                            if ( !(current.isolated() || current.uniqueMatch()) ) {
                                observer.updatedTracking( current.linked(), current.touch );
                            }
                        }
                        for (Nearest<Feature, Quality> last : lastList) {
                            if ( !last.isolated() ) {
                                observer.updatedTracking( last.touch, last.linked() );
                            }
                        }
                    }
                    
                }
                // begin
                for (Nearest<Feature, Quality> current : currentList) {
                    if ( current.isolated() ) {
                        observer.startedTracking( current.touch );
                    }
                }
            }
        };
        
        // </editor-fold>
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Nearest ">
    
    static final private class Nearest<Feature, Quality>
    {
        // <editor-fold defaultstate="collapsed" desc=" Base ">
        
        static abstract private class Base<Feature, Quality>
                implements Tracker<Feature>
        {
            // <editor-fold defaultstate="collapsed" desc=" Attributes ">
            
            final protected Matcher<? super Feature, Quality> matcher;
            protected List<Nearest<Feature, Quality>> lastList    = new ArrayList<Nearest<Feature, Quality>>();
            protected List<Nearest<Feature, Quality>> currentList = new ArrayList<Nearest<Feature, Quality>>();
            
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc=" Initializers ">
            
            protected Base(final Matcher<? super Feature, Quality> matcher) {
                if(matcher == null) throw new NullPointerException();
                this.matcher = matcher;
            }
            
            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc=" Methods ">
            
            abstract protected void doEvents(Observer<? super Feature> observer);
            
            public void reset() {
                lastList.clear();
                currentList.clear();
            }
            
            public void track(Feature feature) {
                final Nearest<Feature, Quality> current = new Nearest<Feature, Quality>( feature );
                currentList.add( current );
                
                // calculate matches
                for (Nearest<Feature, Quality> last : lastList) {
                    final Quality quality = matcher.match( last.touch, current.touch );
                    
                    // no match
                    if(quality == null) { continue; }
                    
                    
                    int c;
                    
                    // potential matches
                    if( (last.quality == null)
                     || ((c = matcher.compare(quality, last.quality)) > 0)
                     || ((c == 0) && last.uniqueMatch())) 
                    {
                        last.nearest = current;
                        last.quality = quality;
                    }                        
                        
                    if( (current.quality == null)
                     || ((c = matcher.compare(quality, current.quality)) > 0)
                     || ((c == 0) && current.uniqueMatch()))
                    {
                        current.nearest = last;
                        current.quality = quality;
                    }
                }
            }
            
            
            public void nextFrame(Observer<? super Feature> observer) {
                //// calculate matches
                //for (Nearest<Feature, Quality> current : currentList) 
                //{
                //    for (Nearest<Feature, Quality> last : lastList) 
                //    {
                //
                //        final Quality quality = matcher.match( last.touch, current.touch );
                //
                //        // no match
                //        if(quality == null) continue;
                //        // potential matches
                //        if(last.quality == null || matcher.compare(quality, last.quality) <= 0) {
                //            last.nearest = current;
                //            last.quality = quality;
                //        }
                //        if(current.quality == null || matcher.compare(quality, current.quality) <= 0) {
                //            current.nearest = last;
                //            current.quality = quality;
                //        }
                //    }
                //}
                
                doEvents(observer);
                
                {
                    final List<Nearest<Feature, Quality>> tmp = currentList;
                    currentList = lastList;
                    lastList = tmp;
                    
                    currentList.clear();
                    for(Nearest n : lastList) {
                        n.reset();
                    }
                }
            }
            
            // </editor-fold>
        }
        
        // </editor-fold>
        
        
        // <editor-fold defaultstate="collapsed" desc=" Attributes ">
        
        final private Feature touch;
        private Nearest<Feature, Quality> nearest;
        private Quality quality = null;//Double.MAX_VALUE; // squared
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Initializers ">
        
        private Nearest(Feature touch) {
            if(touch == null) {
                throw new NullPointerException();
            }
            this.touch = touch;
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Methods ">
        
        private void reset() {
            nearest = null;
            quality = null;//Double.MAX_VALUE;
        }
        
        
        private Feature linked() {
            return nearest.touch;
        }

        
        private boolean isolated() {
            return (nearest == null);
        }

        private boolean uniqueMatch() {
            return (nearest != null) && (this == nearest.nearest);
        }
        
        private boolean equalMatch(final Matcher<? super Feature, Quality> matcher) 
        {
            return (nearest != null) && ((this == nearest.nearest) || (matcher.compare(this.quality, nearest.nearest.quality) == 0));
        }        
        
        // </editor-fold>
    }
    
    // </editor-fold>
}

