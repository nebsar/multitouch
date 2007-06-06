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

import java.util.Comparator;

/**
 * An interface defining a quality measure how well two features match and if they do. 
 * @param Feature the type of the objects to be matched.
 * @param Quality the quality measure type.
 * @author Michael Nischt
 * @version 0.1
 */
public interface Matcher<Feature, Quality> extends Comparator<Quality>
{
    /**
     * Return the quality of the match, if there is one. Otherwise <code>null</code>.
     * @param a the first features involved in the operation 
     * @param b the second features involved in the operation
     * @return the quality if the match, if there is one. Otherwise <code>null</code>.
     */
    Quality match(Feature a, Feature b);
}