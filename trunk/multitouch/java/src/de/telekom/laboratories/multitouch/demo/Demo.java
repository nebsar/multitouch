/*
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.telekom.laboratories.multitouch.demo;


import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.lang.reflect.InvocationTargetException;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
class Demo
{


    // <editor-fold defaultstate="collapsed" desc=" Attributes ">

    private final JFrame frame;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Constructor(s) ">

    private Demo()
    {
        frame = new JFrame("T-Demo: Multitouch");
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
                final Demo demo = new Demo();
            }
        };
        
        EventQueue.invokeAndWait(starter);
    }

    // <editor-fold defaultstate="collapsed" desc=" Main: Entry Point ">

    public static void main(String... args) throws Exception
    {
        Demo.run();
    }

// </editor-fold>
}
