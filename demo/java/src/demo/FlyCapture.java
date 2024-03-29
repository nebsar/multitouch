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


import java.nio.ByteBuffer;
import de.telekom.laboratories.capture.Aquire;
import de.telekom.laboratories.capture.Device;
import de.telekom.laboratories.capture.VideoMode;
import java.util.EnumSet;
import static de.telekom.laboratories.capture.VideoMode.Format.LUMINACE_8;


/**
 * @author Michael Nischt
 * @version 0.1
 */
class FlyCapture extends Capture implements Aquire
{
    private final Device camera;
    private final byte[] target;

    FlyCapture(int width, int height)
    {
        super(width, height);
        this.target = new byte[width * height];

        camera = Device.Registry.getLocalRegistry().getDevices()[0];

        final VideoMode mode = new VideoMode(width, height, LUMINACE_8, 100.0f);
        camera.connect(mode, (Aquire) this);
    }

    @Override
    public void capture(ByteBuffer buffer)
    {
        buffer.get(target);
//        final int width = getWidth();
//        final int height = getHeight();
//
//        for (int i = 0; i < height; i++) {
//            buffer.position((height - (i + 1)) * width);
//            buffer.get(target, i * width, width);
//        }
    }

    @Override
    public void capture(byte[] image, EnumSet<Flip> flip)
    {
        final int width = getWidth(), height = getHeight();        
        if (image.length != width * height) throw new IllegalArgumentException();
        
        camera.capture();

        //System.arraycopy(target, 0, image, 0, image.length);
        copy(target, image, flip);
    }
    
    @Override
    public void dispose() 
    {
        camera.disconnect();
    }

    @Override
    public boolean isDisposed() 
    {
        return !camera.isConnected();
    }

}
