/*
* Created on 28.03.2006
*
* jens wunderling
* http://www.sport4minus.de/blog/?p=226
*/

package vvvv.blobtracker;

import processing.core.PGraphics;





/**
 * tracked object is a simple class to keep the tracking stuff adaptable in the future...
 */

public abstract class TrackedObject {

	Vector3f _myPosition;

	Vector3f _myPreviousPosition;

	Vector3f _myNextPosition;

	Vector3f _myDiffPosition;

	float lookAheadFactor = 3;

	int myID;

	long myStartLiveTime;

	boolean assignedToBlob;

	/*
	 *blob Buffer to smooth movement
	 */
	Vector3f[] blobBuffer;

	int currentBufferField = 0;

	int bufferSize = 8;

	//constructor
	TrackedObject() {
		
		_myPosition = new Vector3f();
		_myPreviousPosition = new Vector3f();
		_myNextPosition = new Vector3f();
		_myDiffPosition = new Vector3f();
		myStartLiveTime = System.currentTimeMillis();
		/*2D array of vector3fs for smoothing the blob input*/
		blobBuffer = new Vector3f[bufferSize];
		for (int i = 0; i < blobBuffer.length; i++) {
			blobBuffer[i] = new Vector3f();
		}
	}
	
	public void setPosition(Vector3f v){
		setPosition(v.x,v.y,v.z);
	}

	public void setPosition(float theX, float theY, float theZ) {
		_myPreviousPosition.set(_myPosition);
		_myPosition.set(theX, theY, theZ);
		_myDiffPosition.set(_myPosition);
		_myDiffPosition.sub(_myPreviousPosition);
		_myNextPosition.set(_myPosition);
		_myNextPosition.add(_myDiffPosition);
	}

	public void bufferBlob(Vector3f theBlob) {
		blobBuffer[currentBufferField].set(theBlob);
		currentBufferField++;
		currentBufferField %= bufferSize;
		
		_myPreviousPosition.set(theBlob);
		_myPosition.set(getBufferedPosition());
		_myDiffPosition.set(_myPosition);
		_myDiffPosition.sub(_myPreviousPosition);
		//estimates new position with a scale factor
		_myDiffPosition.multiply(lookAheadFactor);
		_myNextPosition.set(_myPosition);
		_myNextPosition.add(_myDiffPosition);
	}

	public Vector3f getBufferedPosition() {
		Vector3f pos = new Vector3f();
		for (int i = 0; i < blobBuffer.length; i++)
			pos.add(blobBuffer[i]);
		pos.multiply(1 / (float) blobBuffer.length);
		return pos;
	}

	public void setStartValues() {
		_myPreviousPosition.set(_myPosition);
		_myNextPosition.set(_myPosition);
		for (int i = 0; i < blobBuffer.length; i++) {

			blobBuffer[i].set(_myPosition);

		}
	}

	public void drawBuffer(PGraphics g) {
		for (int i = 0; i < blobBuffer.length; i++) {
			g.fill(0, 0, 255);
			g.ellipse(blobBuffer[i].x, blobBuffer[i].y, 3, 3);
		}
	}

	public Vector3f getPreviousPosition() {
		return _myPreviousPosition;
	}

	public Vector3f getPosition() {
		return _myPosition;
	}

	public float velocity() {
		Vector3f diff = new Vector3f();
		diff.set(_myPosition);
		diff.sub(_myPreviousPosition);
		return diff.length();
	}

}
