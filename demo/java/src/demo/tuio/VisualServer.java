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
package demo.tuio;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import static java.awt.EventQueue.invokeAndWait;

/**
 *
 * @author Michael Nischt
 * @version 0.1
 */
public class VisualServer
{

    private final Image icon;
    private Server server;

    private VisualServer()
    {
        {
            Image image = null;
            try {
                image = ImageIO.read(VisualServer.class.getResource("/de/telekom/Icon.png"));
            }
            catch (IOException ioe) {
            }
            icon = image;
        }


        final ServerForm form = new ServerForm();
        form.setTitle("Multi-T-ouch: TUIO Server");
        form.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        if (icon != null) form.setIconImage(icon);                
        
        form.addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                if ( server == null || !server.isRunning() ) 
                {
                    exit();
                    return;
                }

                if (!systemTray()) {
                    floatingWindow();
                }
            }
        });

        form.pack();
        form.setResizable(false);
        {
            final DisplayMode d = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
            form.setLocation(Math.max(0, (d.getWidth() / 2) - (form.getWidth() / 2)), Math.max(0, (d.getHeight() / 2) - (form.getHeight() / 2)  - 50));
            // that following has problems with multiple graphics devices (screens):
            //final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            //jFrame.setLocation(Math.max(0, (d.width / 2) - (jFrame.getWidth() / 2)), Math.max(0, (d.height / 2) - (jFrame.getHeight() / 1)));
        }
        form.setVisible(true);
    }

    // <editor-fold defaultstate="collapsed" desc=" System Tray ">
        
    private boolean systemTray()
    {
        if (icon == null || !SystemTray.isSupported()) return false;
        
        final SystemTray tray = SystemTray.getSystemTray();


        final ActionListener exitListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                exit();
            }
        };

        final PopupMenu popup = new PopupMenu();

        //final CheckboxMenuItem cameraItem = new CheckboxMenuItem("Camera");
        final MenuItem stopItem = new MenuItem("Stop");
        stopItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                exit();
            }
        });

        final MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(exitListener);

        //popup.add(cameraItem);
        //popup.addSeparator();
        popup.add(exitItem);

        final TrayIcon trayIcon = new TrayIcon(icon, "MultiTouch: TUIO Server", popup);
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        }
        catch (AWTException e) {
            return false;
        }

        return true;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Floating Window ">
    private void floatingWindow()
    {
        final JDialog jDialog = new JDialog();
        jDialog.setLocationByPlatform(true);
        jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jDialog.setTitle("Multi-T-ouch: TUIO Server");

        jDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e)
            {
                exit();
            }
        });

        //final JToggleButton jCameraButton = new JToggleButton("Camera");
        final JButton jStopButton = new JButton("Stop");
        jStopButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                exit();
            }
        });

        jDialog.getContentPane().setLayout(new GridLayout(1, 1));
        //jDialog.getContentPane().setLayout(new GridLayout(2, 1));
        //jDialog.getContentPane().add(jCameraButton);
        jDialog.getContentPane().add(jStopButton);

        jDialog.setPreferredSize(new Dimension(240, 120));
        jDialog.pack();
        jDialog.setResizable(false);
        jDialog.setVisible(true);
    }

    // </editor-fold>

    private void exit()
    {
        if (server != null && server.isRunning()) {
            server.stop();
        }
        System.exit(0);
    }


    public static void main(String... args) throws Exception
    {
        invokeAndWait(new Runnable() {

            @Override
            public void run()
            {
                new VisualServer();
            }
        });
    }
}
