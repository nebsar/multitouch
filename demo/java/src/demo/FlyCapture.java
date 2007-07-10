/*
 * FlyCapture.java
 *
 * Created on Jul 10, 2007, 10:36:07 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demo;


import java.nio.ByteBuffer;
import de.telekom.laboratories.capture.Aquire;
import de.telekom.laboratories.capture.Device;
import de.telekom.laboratories.capture.VideoMode;
import java.util.EnumSet;
import static de.telekom.laboratories.capture.VideoMode.Format.LUMINACE_8;


/**
 *
 * @author gestalt
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

        final VideoMode mode = new VideoMode(width, height, LUMINACE_8, 30.0f);
        camera.connect(mode, (Aquire) this);
    }

    public void capture(ByteBuffer buffer)
    {
        final int width = getWidth();
        final int height = getHeight();

        for (int i = 0; i < height; i++) {
            buffer.position((height - (i + 1)) * width);
            buffer.get(target, i * width, width);
        }
    }

    public void capture(byte[] image, EnumSet<Flip> flip)
    {
        final int width = getWidth(), height = getHeight();        
        if (image.length != width * height) throw new IllegalArgumentException();
        
        camera.capture();

        copy(target, image, flip);
    }
}
