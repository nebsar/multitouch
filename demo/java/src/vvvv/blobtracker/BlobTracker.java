/*
 * jens wunderling
 * http://www.sport4minus.de/blog/?p=226
 */

package vvvv.blobtracker;

import java.io.FileOutputStream;
import java.io.PrintStream;


import processing.core.PApplet;
import processing.core.PFont;

public class BlobTracker extends PApplet {
    PFont font;
    
    Cursor[] myCursors;
    
    Tracker myTracker;
    
    static int WIDTH = 1024;
    static int HEIGHT = 768;
    
    public void setup() {
        background(0);
        ellipseMode(CENTER);
        rectMode(CENTER);
        size(WIDTH, HEIGHT, OPENGL);
        frameRate(120);
        
        myCursors = new Cursor[10];
        myTracker = new Tracker(myCursors);
        
        fill(255);
        font = loadFont("Beatbox-10.vlw");
        textFont(font, 10);
        
        smooth();
    }
    
    public void draw() {
        background(0);
        myTracker.checkGlobs();
        myTracker.draw(g);
        //myTracker.clearBlobs();
        
        for (int i = 0; i < myCursors.length; i++) {
            if (myCursors[i] != null) {
                myCursors[i].draw(g);
            }
        }
        
    }
    
    static public void main(String args[])  {
        
        try {
            FileOutputStream fos = new FileOutputStream("d:\\errors.txt");
            PrintStream ps = new PrintStream(fos);
            System.setErr(ps);
            System.setOut(ps);
            
            
            PApplet.main(new String[] { "vvvv.blobtracker.BlobTracker" });
            
        } catch(Exception e) {}
    }
}
