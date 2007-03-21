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

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */

public final class Touch
{

	public static final class Creation
	{		
		public static final public enum Type
		{
			ADD,
			SPLIT,
			MERGE;
		}
		
	}
	
	public static final class Destruction
	{		
		public enum Type
		{
			REMOVE,
			SPLIT,
			MERGE;
		}
		
	}	

	public static final class Event
	extends EventObject
	{
		public static final enum Type
		{
			BEGIN,
			END;
		}
		
		Event(Object source)
		{
			super(source);
		}	
		
		
		
	}
	
	public static final interface Listener extends EventListener
	{
		void touchStarted(Event evt);						
		void touchFinished(Event evt);
	}	
	
	public static final interface Observer
	{
		void tochUpdated(Touch touch);		
	}
			
	void split(Touch splitted, Touch... output); // change to start observing each/all output touches 
	void merge(Touch merged, Touch input);	     // change to start observring the merged touch
	

}