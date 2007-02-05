/*
 * Created on 28.03.2006
 *
 * jens wunderling
 * http://www.sport4minus.de/blog/?p=226
 */

package vvvv.blobtracker;

import processing.core.PGraphics;

public class Cursor extends TrackedObject {

	public Cursor(int theObjectID) {
		myID = theObjectID;
	}

	/**
	 * @param theObjectID
	 */
	

	public void draw(final PGraphics g) {
		/*
		 stroke(255);
		 rect(_myPosition.x, _myPosition.y, 5,5);
		 line(_myPosition.x, _myPosition.y, 12 + myID*60, height-26);
		 noStroke();
		 */
		g.fill(255);
		g.text(myID, _myPosition.x, _myPosition.y);

		//text("ID:" + myID + (millis()-myStartLiveTime),  10 + myID*60, height-20);
		/*fill(255,0,0);
		 drawBuffer();
		 ellipse(_myNextPosition.x,_myNextPosition.y, 3,3);
		 noFill();
		 
		 
		 */
		/*
		 noFill();
		 stroke(255,0,0);
		 ellipse(_myNextPosition.x,_myNextPosition.y, myTracker.trackDistThreshold*2, myTracker.trackDistThreshold*2);
		 noStroke();
		 */
	}

}
