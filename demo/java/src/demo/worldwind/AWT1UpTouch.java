/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package demo.worldwind;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.examples.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.layers.Earth.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.view.*;

import javax.swing.*;
import javax.xml.parsers.*;
import java.awt.*;
import java.awt.font.*;
import java.io.*;
import java.util.*;

/**
 * @author Tom Gaskins
 * @version $Id: AWT1Up.java 3209 2007-10-06 21:57:53Z tgaskins $
 */
public class AWT1UpTouch
{
    private static class AWT1UpFrame extends javax.swing.JFrame
    {
        private boolean fullscreen = true;
        private int screen = 0;
                
        StatusBar statusBar;
        JLabel cursorPositionDisplay;
        WorldWindowGLCanvas wwd;

        public AWT1UpFrame()
        {
            try
            {                
                System.out.println(gov.nasa.worldwind.Version.getVersion());

                wwd = new gov.nasa.worldwind.awt.WorldWindowGLCanvas();
                wwd.setPreferredSize(new java.awt.Dimension(800, 600));
                this.getContentPane().add(wwd, java.awt.BorderLayout.CENTER);

                this.statusBar = new StatusBar();
                this.getContentPane().add(statusBar, BorderLayout.PAGE_END);                                
                
                // <editor-fold defaultstate="collapsed" desc=" Fullscreen ">

                final GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment ().getScreenDevices ();
                final GraphicsDevice   graphicsDevice  = graphicsDevices[Math.max (0, screen = Math.min (screen, graphicsDevices.length-1))];                
                                
                if(fullscreen)
                {// && device.isFullScreenSupported()) {
                    try
                    {
                        setUndecorated (true);
                        graphicsDevice.setFullScreenWindow (this);
                    }
                    catch(Exception e)
                    {
                        System.err.println(e.getMessage());
                        e.printStackTrace();
                        graphicsDevice.setFullScreenWindow (null);
                        fullscreen = false;
                    }
                }

                if(!fullscreen)
                {
                    java.awt.Dimension prefSize = this.getPreferredSize();
                    java.awt.Dimension parentSize;
                    java.awt.Point parentLocation = new java.awt.Point(0, 0);
                    parentSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                    int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
                    int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
                    this.setLocation(x, y);
                    this.setResizable(true);
                }

                // </editor-fold>
                
                Model model = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
                wwd.setModel(model);
                
                // Forward events to the status bar to provide the cursor position info.
                this.statusBar.setEventSource(wwd);
                
                
                final OrbitView view = (OrbitView)wwd.getView();
                final TouchInput touchInput = new TouchInput(view, model);
                
                Thread t = new Thread("Touch.Inputs")
                {        
                    private boolean even = true;
                    
                    private void frame()
                    {
                        System.out.println("frame");
                        if(even)
                        {
                            touchInput.advanceFrame(Collections.singleton(new Touch(0, -5)).iterator());
                        }
                        else
                        {
                            touchInput.advanceFrame(Collections.singleton(new Touch(0, +5)).iterator());
                        }
                        even = !even;                        
                    }
                    
                    @Override public void run ()
                    {
                        while(true)
                        {
                            try
                            {
                                sleep(1000);   
                            }
                            catch(InterruptedException ie) {}

                            EventQueue.invokeLater(new Runnable() 
                            {
                                public void run() 
                                {
                                    frame();
                                }
                            });                        
                        }
                        
                    }
                };
                t.setDaemon (true);
                t.setPriority (Thread.NORM_PRIORITY);//(Thread.NORM_PRIORITY + Thread.MAX_PRIORITY)/2);
                t.start ();
                
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception
    {
        System.out.println("Java run-time version: " + System.getProperty("java.version"));

        EventQueue.invokeAndWait(new Runnable() 
        {            
            public void run() 
            {
                AWT1UpFrame frame = new AWT1UpFrame();
                frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
        
        
        //for(int i=0; i)
    }
}
