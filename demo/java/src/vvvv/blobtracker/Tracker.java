/*
 * Created on 28.03.2006
 * 
 * jens wunderling
 * http://www.sport4minus.de/blog/?p=226
 */

package vvvv.blobtracker;

import oscP5.OscIn;
import oscP5.OscP5;
import processing.core.PGraphics;

public class Tracker {

	TrackedObject[] myTrackedObjects;

	float trackDistThreshold = 50;

	static final int LOOKATPOSITION = 0;

	static final int LOOKAHEAD = 1;

	int mode = LOOKAHEAD;

	//blobs per second can be calculated by this method.

	//blobs received is incremented every time a osc message is received
	int timesBlobReceived = 0;

	/* this method returns blobspersecond value */
	public int getBlobsPerSecond() {
		int theBlobs = 0;
		theBlobs = (timesBlobReceived / (int) (System.currentTimeMillis() / 1000));
		return theBlobs;
	}

	/* holds number of blobs */
	int blobCount = 0;

	/* Vector Array holds coordinates received from OSC */
	Vector3f[] blobCoords;

	OscP5 oscP5;

	int receiveAtPort;

	int sendToPort;

	String host;

	String oscP5event;

	public void initOsc() {
		receiveAtPort = 9000;
		sendToPort = 57120;
		host = "127.0.0.1";
		//oscP5event = "oscBlobs";
		oscP5 = new OscP5(this, host, sendToPort, receiveAtPort, "getBlobs");
		oscP5.plug (this,"/clear","clear");
	}

	/* the tracked objects are overloaded for reference */
	Tracker(final TrackedObject[] theTrackedObjects) {
		myTrackedObjects = theTrackedObjects;
		/* the blob coordinates after the buffer */
		blobCoords = new Vector3f[10];
		for (int i = 0; i < blobCoords.length; i++) {
			blobCoords[i] = new Vector3f();
		}
		/* osc is initialized */
	 	initOsc();
	}

	public void clearBlobs(float theFloat) {
		if(theFloat == 1.0f){
			for (int i = 0; i < blobCoords.length; i++) {
			blobCoords[i].set(0,0,0);	
			}	
		};
	}

	/* this method is called when the osc message arrives */
	public void getBlobs(OscIn oscIn) {
		/* is used for calculating blobs per second performance */
		timesBlobReceived++;

		blobCount = oscIn.getDataList().size() / 3;

		float blobX, blobY, blobID;

		for (int i = 0; i < blobCoords.length; i++) {

			/*
			 * normalised coordinates are filled in the Vector3f-array and
			 * translated to application width.
			 */

			blobX = oscIn.getFloat(i * 3);
			blobX *= (float) BlobTracker.WIDTH;
			blobX += (float) BlobTracker.WIDTH / 2;

			blobY = oscIn.getFloat(i * 3 + 1);
			blobY *= -(float) BlobTracker.HEIGHT;
			blobY += (float) BlobTracker.HEIGHT / 2;

			blobID = oscIn.getFloat(i * 3 + 2);

			blobCoords[i].set(blobX, blobY, blobID);
			//blobCoords[i].print();
			/*
			 * third coord holds permanent ID sent by VVVV
			 */

		}

	}

	public void draw(PGraphics g) {
		g.fill(255, 0, 0);
		for (int i = 0; i < blobCoords.length; i++) {
			g.ellipse(blobCoords[i].x, blobCoords[i].y, 5, 5);
		}
		g.fill(0);
		g.rect(0, 0, 20, 100);
		g.fill(255);
		g.text(blobCount + "  bps:" + getBlobsPerSecond(), 10, 10);
		for (int i = 0; i < blobCoords.length; i++) {
			g.text("blob" + i + " " + blobCoords[i].x + " " + blobCoords[i].y
					+ " ID" + blobCoords[i].z, 10, 30 + 15 * i);
		}

	}

	/*
	 * most important method: it assigns the existing objects to blobs, creates
	 * new objects, kills objects
	 */
	public void checkGlobs() {

		for (int j = 0; j < myTrackedObjects.length; j++) {
			if (myTrackedObjects[j] != null)
				myTrackedObjects[j].assignedToBlob = false;
		}

		/*
		 * looks through the whole coordinates array and checks if a new
		 * TrackedObject has to be created or an existing Tracked Object has to
		 * be updated.
		 */

		for (int i = 0; i < blobCoords.length; i++) {

			boolean createTrackedObject = true;
			if (i < blobCount) {

				for (int j = 0; j < myTrackedObjects.length; j++) {
					if (myTrackedObjects[i] != null
							&& myTrackedObjects[i].myID == blobCoords[i].z) {
						myTrackedObjects[i].setPosition(blobCoords[i].x,
								blobCoords[i].y, 0f);
						myTrackedObjects[i].assignedToBlob = true;

						createTrackedObject = false;
						break;

					}
				}
				if (createTrackedObject)
					createObject(blobCoords[i].x, blobCoords[i].y,
							(int) blobCoords[i].z);
			}

		}

		killObjects();
	}

	public void createObject(float theX, float theY, int theObjectID) {

		for (int i = 0; i < myTrackedObjects.length; i++) {
			if (myTrackedObjects[i] == null) {
				myTrackedObjects[i] = new Cursor(theObjectID);
				myTrackedObjects[i].setPosition(theX, theY, 0);
				myTrackedObjects[i].setStartValues();
				myTrackedObjects[i].assignedToBlob = true;
				break;
			}
		}
	}

	public void killObjects() {
		//looks through the whole object array
		for (int i = 0; i < myTrackedObjects.length; i++) {
			//if a certain object exists...
			if (myTrackedObjects[i] != null
					&& !myTrackedObjects[i].assignedToBlob) {
				//it is to be killed
				myTrackedObjects[i] = null;
			}
		}
		if (blobCount == 0) {
			for (int i = 0; i < myTrackedObjects.length; i++) {
				//if a certain object exists...
				if (myTrackedObjects[i] != null) {
					//it is to be killed
					myTrackedObjects[i] = null;
				}
			}
		}
	}

}
