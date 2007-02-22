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

package de.telekom.laboratories.multitouch.demo;

import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class Techniques {

   // <editor-fold defaultstate="collapsed" desc=" Attributes ">

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Constructor(s) ">

    private Techniques()
    {
        final JFrame frame = new JFrame("T-Demo: Multitouch");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        frame.setSize(640, 480);

        // <editor-fold defaultstate="collapsed" desc=" OpenGL ">
        
//        final GLCanvas canvas = new GLCanvas();
//        canvas.setSize(640, 480);
//        canvas.setPreferredSize(new Dimension(640, 480));
//        
//        final GLCapabilities caps = new GLCapabilities();
//        caps.setHardwareAccelerated(true);
//        caps.setDoubleBuffered(true);
//        
//        GLDrawableFactory.getFactory().getGLDrawable(canvas, caps, new DefaultGLCapabilitiesChooser());

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Fullscreen ">
        
        final boolean fullscreen = true;
        final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();           

        if(fullscreen) {// && device.isFullScreenSupported()) {
            try
            {
                frame.setUndecorated(true);
                device.setFullScreenWindow(frame);
            }
            finally
            {
                device.setFullScreenWindow(null);
            }
        } else {
            frame.pack();
        }
        
        // </editor-fold>

        frame.setVisible(true);
    }

    // </editor-fold>

    public static void run() throws InterruptedException, InvocationTargetException 
    {
        
        final Runnable starter = new Runnable()
        {
            public void run()
            {
                final Techniques demo = new Techniques();
            }
        };
        
        EventQueue.invokeAndWait(starter);
    }

    // <editor-fold defaultstate="collapsed" desc=" Main: Entry Point ">

    public static void main(String... args) throws Exception
    {
        run();
    }

// </editor-fold>
}
