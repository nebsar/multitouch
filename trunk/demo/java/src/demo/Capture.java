/*
 * Capture.java
 * 
 * Created on Jul 10, 2007, 9:59:34 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demo;

import java.util.EnumSet;
import static java.util.EnumSet.of;
import static java.util.EnumSet.allOf;
import static java.util.EnumSet.noneOf;
import static demo.Capture.Flip.HORIZONTAL;
import static demo.Capture.Flip.VERTICAL;

/**
 *
 * @author gestalt
 * @version 0.1
 */
abstract public class Capture 
{    
    public enum Flip
    {
        VERTICAL,
        HORIZONTAL
    };
    
    private final int width, height;
    
    protected Capture(int width, int height)
    {
        if(width <= 0 || height <= 0) throw new IllegalArgumentException();
        this.width = width;
        this.height = height;
    }    
        
    public int getWidth()  { return width; }
    public int getHeight() { return height; }
        
    protected void copy(byte[] src, byte[] dst, EnumSet<Flip> flip)
    {            
        //final int widht = getWidth(), height = getHeight();
        
        if(src.length != dst.length)// || src.length != width * height) 
            throw new IllegalArgumentException();
                    
        if( flip.containsAll( allOf(Flip.class) ) )
        {
            final int w = width - 1, h = height - 1;
            for (int y = 0; y < height; y++) {
                final int srcOff = width * (h-y);
                final int dstOff = width * y;
                for (int x = 0; x < width; x++) {
                    dst[dstOff + x] = src[srcOff + (w-x)];
                }
            }
        }
        else if ( flip.contains( HORIZONTAL ))
        {
            final int h = height - 1;
            for (int y = 0; y < height; y++) {
                final int srcOff = width * (h-y);
                final int dstOff = width * y;
                for (int x = 0; x < width; x++) {
                    dst[dstOff + x] = src[srcOff + x];
                }
            }
        }
        else if ( flip.contains( VERTICAL ))
        {
            final int w = width - 1;
            for (int y = 0; y < height; y++) {
                final int off = y * width;
                for (int x = 0; x < width; x++) {
                    dst[off + x] = src[off + (w-x)];
                }
            }   
        }
        else
        {        
            System.arraycopy(src, 0, dst, 0, src.length);
        }
    }
    
    public void capture(byte[] dst)
    {
        capture(dst, noneOf(Flip.class));
    }
    public void capture(byte[] dst, Flip flip)
    {
        capture( dst, of(flip));
    }        
    
    abstract public void capture(byte[] dst, EnumSet<Flip> flip);    
    
    public static Capture startDevice(int width, int height)
    {
        return new FlyCapture(width, height);
    }        
}