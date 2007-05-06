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

package de.telekom.laboratories.multitouch;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Michael Nischt
 * @version 0.1
 */
final public class Correlations {

    // <editor-fold defaultstate="collapsed" desc=" unique ">
    
    static public <Touch, Quality> Correlation<Touch> unique(final Matcher<? super Touch, Quality> matcher)
    {
        return new Correlation<Touch> ()
        {
            // <editor-fold defaultstate="collapsed" desc=" Attributes ">

            private List<Nearest<Touch, Quality>> lastList    = new ArrayList<Nearest<Touch, Quality>>();
            private List<Nearest<Touch, Quality>> currentList = new ArrayList<Nearest<Touch, Quality>>();

            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc=" Methods ">

            public void reset ()
            {
                lastList.clear();
                currentList.clear();
            }

            public void touch (Touch touch)
            {
                currentList.add( new Nearest<Touch, Quality>(touch) );
            }


            public void nextFrame (Observer<? super Touch> observer)
            {
                // calculate matches
                for (Nearest<Touch, Quality> last : lastList)
                {
                    for (Nearest<Touch, Quality> current : currentList)
                    {               
                        final Quality quality = matcher.match ( last.touch, current.touch );

                        // no match
                        if(quality == null) continue;
                        // potential matches
                        if(last.quality == null || matcher.compare(quality, last.quality) <= 0)
                        {
                            last.nearest = current;
                            last.quality = quality;
                        }                            
                        if(current.quality == null || matcher.compare(quality, current.quality) <= 0)
                        {
                            current.nearest = last;
                            current.quality = quality;
                        }                                                    
                    }
                }

                // begin
                for (Nearest<Touch, Quality> last : lastList)
                {                                                
                    if ( !last.matches() )
                    {
                        observer.touchEnd( last.touch );
                    }                        
                }

                // update
                if(lastList.size() < currentList.size())
                {
                    for (Nearest<Touch, Quality> last : lastList)
                    {
                        if ( last.matches() )
                        {
                            observer.touchUpdate( last.touch, last.nearest.touch );
                        }
                    }
                } 
                else
                {
                    for (Nearest<Touch, Quality> current : currentList)
                    {
                        if ( current.matches() )
                        {
                            observer.touchUpdate( current.nearest.touch, current.touch );
                        }
                    }                  
                }

                // end
                for (Nearest<Touch, Quality> current : currentList)
                {
                    if ( !current.matches() )
                    {
                        observer.touchBegin( current.touch );
                    }
                }                      

                {
                    final List<Nearest<Touch, Quality>> tmp = currentList;                    
                    currentList = lastList;
                    lastList = tmp;

                    currentList.clear ();
                    for(Nearest n : lastList)
                    {
                        n.reset();
                    }                        
                }                                        
            }

            // </editor-fold>
        };                        
    }        

    // <editor-fold defaultstate="collapsed" desc=" Nearest ">

    static final private class Nearest<Touch, Quality>
    {
        // <editor-fold defaultstate="collapsed" desc=" Attributes ">

        final private Touch touch;
        private Nearest<Touch, ?> nearest;
        private Quality quality = null;//Double.MAX_VALUE; // squared

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Initializers ">

        private Nearest(Touch touch)
        {
            if(touch == null)
            {
                throw new NullPointerException();
            }
            this.touch = touch;
        }

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Methods ">

        private void reset()
        {
            nearest = null;
            quality = null;//Double.MAX_VALUE;
        }


        private boolean matches() {
            return (nearest != null) && (this == nearest.nearest);
        }

        // </editor-fold>
    }

    // </editor-fold>        
    
    // </editor-fold>
}
