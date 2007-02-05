/*
 * Created on 28.03.2006
 *
 * jens wunderling
 * http://www.sport4minus.de/blog/?p=226
 */

package vvvv.blobtracker;


class Vector3f {

	float x = 0;

	float y = 0;

	float z = 0;

	public void set(float theX, float theY, float theZ) {
		x = theX;
		y = theY;
		z = theZ;
	}

	public void set(Vector3f theVector) {
		x = theVector.x;
		y = theVector.y;
		z = theVector.z;
	}

	public void add(Vector3f theVector) {
		x += theVector.x;
		y += theVector.y;
		z += theVector.z;
	}

	public void sub(Vector3f theVector) {
		x -= theVector.x;
		y -= theVector.y;
		z -= theVector.z;
	}

	public void multiply(float s) {
		x *= s;
		y *= s;
		z *= s;
	}

	public float length() {
		float myLengthSquard = x * x + y * y + z * z;
		float myLength = (float)Math.sqrt(myLengthSquard);
		return myLength;
	}

	public void normalize() {
		float d = length();
		x /= d;
		y /= d;
		z /= d;
	}

	public void cross(Vector3f a, Vector3f b) {
		x = a.y * b.z - a.z * b.y;
		y = b.x * a.z - b.z * a.x;
		z = a.x * b.y - a.y * b.x;
	}

	public void print() {
		System.out.println("(" + x + ", " + y + ", " + z + ")");
	}
}
