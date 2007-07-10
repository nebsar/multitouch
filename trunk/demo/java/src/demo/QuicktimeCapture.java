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

package demo;

import static java.lang.Runtime.getRuntime;
import java.util.EnumSet;

import static quicktime.std.StdQTConstants.kComponentVideoCodecType;
import static quicktime.std.StdQTConstants.seqGrabRecord;
import static quicktime.std.StdQTConstants.seqGrabPreview;
import static quicktime.std.StdQTConstants.seqGrabPlayDuringRecord;
import static quicktime.std.StdQTConstants.seqGrabDontMakeMovie;
import static quicktime.qd.QDConstants.k32RGBAPixelFormat;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.qd.PixMap;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.sg.SGVideoChannel;
import quicktime.std.sg.SequenceGrabber;
import quicktime.util.RawEncodedImage;

import static demo.Capture.Flip;
/**
 * @author Michael Nischt
 * @version 0.1
 */
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
