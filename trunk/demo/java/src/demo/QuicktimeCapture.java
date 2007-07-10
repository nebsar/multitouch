/*
 * QuicktimeCapture.java
 * 
 * Created on Jul 10, 2007, 10:38:40 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demo;

import java.util.EnumSet;
import quicktime.QTException;
import static java.lang.Runtime.getRuntime;

import static quicktime.std.StdQTConstants.kComponentVideoCodecType;
import static quicktime.std.StdQTConstants.seqGrabRecord;
import static quicktime.std.StdQTConstants.seqGrabPreview;
import static quicktime.std.StdQTConstants.seqGrabPlayDuringRecord;
import static quicktime.std.StdQTConstants.seqGrabDontMakeMovie;
import static quicktime.qd.QDConstants.k32RGBAPixelFormat;
import quicktime.QTSession;
import quicktime.qd.PixMap;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.sg.SGVideoChannel;
import quicktime.std.sg.SequenceGrabber;
import quicktime.util.RawEncodedImage;


/**
 *
 * @author gestalt
 * @version 0.1
 */;
class QuicktimeCapture extends Capture
{
    static
    {
        final Thread cleanUp = new Thread ()
        {
            @Override
            public void run ()
            {
                try
                { 
                    if(QTSession.isInitialized()) 
                      QTSession.close (); 
                }
                catch(Exception e) {}
            }
        };
        getRuntime().addShutdownHook(cleanUp);        
    }
    
    private final byte[] target;
    private final int[] rgbaPixels; 
    private final SequenceGrabber sg;
    private final RawEncodedImage raw;
    
    QuicktimeCapture(int width, int height)
    {    
        super(width, height);
        target = new byte[width * height]; 
        rgbaPixels  = new int[width * height];
        try 
        {
            QTSession.open();
            final QDRect size = new QDRect ( width, height );
            // vc.getSrcVideoBounds();
            
            //if (quicktime.util.EndianOrder.isNativeLittleEndian())
            //{
            //    gfx = new QDGraphics(k32BGRAPixelFormat, size);
            //} else {
            //    gfx = new QDGraphics(kDefaultPixelFormat, size);
            //}
            // ignore native platform format which may be more performant
            final QDGraphics gfx = new QDGraphics (k32RGBAPixelFormat, size);
                    
            sg = new SequenceGrabber ();
            sg.setGWorld (gfx , null);
            //sg.setDataOutput (null, seqGrabDontMakeMovie);
            {
            
                final SGVideoChannel  vc = new SGVideoChannel (sg);
                //vc.setDevice ( device );
                vc.setBounds ( size );
                vc.setUsage ( seqGrabPreview );   // 2
                //| seqGrabRecord );// 1
                //| seqGrabPlayDuringRecord );// ?
                //vc.setCompressorType ( kComponentVideoCodecType );
                vc.setFrameRate ( 0 );
                //vc.settingsDialog();                        
            }
            
            //sg.prepare(true, false);
            sg.startPreview ();
                                                
            final PixMap pixMap = gfx.getPixMap();
            raw = pixMap.getPixelData();
            
            final int rowBytes  = raw.getRowBytes();
            final int rawWidth  = rowBytes / 4; // 4 == R-G-B-A
            final int rawHeight = raw.getSize() / rowBytes;                        
            
            if (width  != rawWidth) throw new IllegalStateException();    
            if (height != rawHeight) throw new IllegalStateException();                  

        }
        catch(QTException e)
        {
            throw new RuntimeException("Couldn't initialize Quicktime Device", e);
        }        
    }
    
    public void capture(byte[] image, EnumSet<Flip> flip)
    {
        final int width = getWidth(), height = getHeight();        
        if (image.length != width * height) throw new IllegalArgumentException();
        
        try{
            sg.idle (); //sg.idleMore ();
        } catch(QTException e) {} // very bad coding style
        
        raw.copyToArray(0, rgbaPixels, 0, width * height);


        for(int h=0; h<height; h++)
        {
            final int sRow = ( (height-(h+1))*width );
            final int tRow = ( h*width );

            for(int w=0; w<width; w++)
            {
                final int rgba = rgbaPixels[sRow+w];
                final int r = (0xFF000000 & rgba) >> 24;
                final int g = (0x00FF0000 & rgba) >> 16;
                final int b = (0x0000FF00 & rgba) >> 8;
                // Luminance
                target[tRow+w] = (byte) ((0.257 * r) + (0.504 * g) + (0.098 * b));
//                target[tRow+w] = (byte) ((r+g+b)/3);
            }                                    
        }
        
        copy(target, image, flip);       
    }

}
