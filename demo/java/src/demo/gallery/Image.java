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

package demo.gallery;

import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * @author Michael Nischt
 * @version 0.1
 */
final public class Image
{
    // <editor-fold defaultstate="collapsed" desc=" Variables ">
    
    private final int content;
    private final double centerX, centerY;
    private final double extentX, extentY;
    private final double orientation;

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Initializers ">    

    public Image(int content, double extentX, double extentY)
    {
        this.content = content;
        this.extentX = extentX;
        this.extentY = extentY;
        this.centerX = 0.0;
        this.centerY = 0.0;
        this.orientation = 0.0;
    }    
    
    private Image(int content, double extentX, double extentY, double centerX, double centerY, double orientation)
    {
        this.extentX = extentX;
        this.extentY = extentY;
        this.content = content;
        this.centerX = centerX;
        this.centerY = centerY;
        this.orientation = orientation;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Properties ">    
    
    public int getContent()
    { 
        return content;
    }

    public double getCenterX()
    {
        return centerX;
    }

    public double getCenterY()
    {
        return centerY;
    }

    public double getExtentX()
    {
        return extentX;
    }

    public double getExtentY()
    {
        return extentY;
    }

    public double getOrientation()
    {
        return orientation;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    public Image translated(double x, double y)
    {
        return new Image(content, extentX, extentY, centerX+x, centerY+y, orientation);
    }

    public Image rotated(double amount)
    {
        return new Image(content, extentX, extentY, centerX, centerY, (orientation + amount) );
    }
    
    public Image scaled(double ratio)
    {
        return new Image(content, extentX*ratio, extentY*ratio, centerX, centerY, orientation);
    }    

    public Image extended(double amount)
    {
        return new Image(content, extentX+amount, extentY+amount, centerX, centerY, orientation);
    }    
    
    
    // </editor-fold>
    
    final static public class Utils
    {
        static public Bounds bounds (Image image)
        {
            final double cX = image.getCenterX();
            final double cY = image.getCenterY();

            double rot = Math.PI * image.getOrientation();
            
            final double cos = cos ( rot );
            final double sin = sin ( rot );
            
            final double extX = image.getExtentX();
            final double extY = image.getExtentY();
            
            return new Bounds ()
            {
                @Override
                public boolean contain(Touch touch)
                {               
                    double x = touch.getX();
                    double y = touch.getY();
                    {
                        x -= cX;
                        y -= cY;
                    }
                    
                    {                        
                        final double xNew =  cos*x + sin*y;
                        final double yNew =  cos*y - sin*x;
                        
                        x = xNew;
                        y = yNew;
                    }
                                        
                    return ( abs(x) <= extX ) && ( abs(y) <= extY );
                }
            };
        }
    }
}
