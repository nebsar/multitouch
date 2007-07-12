/*
 * CameraSettingsBean.java
 * 
 * Created on Jul 12, 2007, 7:02:33 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demo.ui;

import java.io.Serializable;
import static java.lang.String.format;

/**
 *
 * @author gestalt
 * @version 0.1
 */
public class CameraSettingsBean implements Serializable
{
    // <editor-fold defaultstate="collapsed" desc=" Resolution ">
    
    static final public class Resolution
    {
        // <editor-fold defaultstate="collapsed" desc=" Variables ">
        
        private final int width;
        private final int height;
        
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Properties ">
        
        public int getHeight()
        {
            return height;
        }

        public int getWidth()
        {
            return width;
        }
        
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Initialization ">
        
        public Resolution(int width, int height)
        {
            if(width <= 0 || height <= 0) throw new IllegalArgumentException();
            
            this.width = width;
            this.height = height;
        }
        
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Methods ">
        
        @Override
        public boolean equals(Object obj)
        {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Resolution other = (Resolution) obj;
            if (this.width != other.width) {
                return false;
            }
            if (this.height != other.height) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 47 * hash + this.width;
            hash = 47 * hash + this.height;
            return hash;
        }

        @Override
        protected Resolution clone()
        {
            return new Resolution(width, height);
        }

        @Override
        public String toString()
        {
            return format("%s(width=%d,height=%d)", getClass().getName(), width, height);
        }
        
        // </editor-fold>
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ImageFlipping ">
    
    static final public class ImageFlipping
    {
        // <editor-fold defaultstate="collapsed" desc=" Variables ">
        
        private final boolean horizontal;
        private final boolean vertical;
        
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Properties ">
        
        public boolean isHorizontal()
        {
            return horizontal;
        }

        public boolean isVertical()
        {
            return vertical;
        }

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Initialization ">
        
        public ImageFlipping()
        {
            this(false, false);
        }                
        
        public ImageFlipping(boolean horizontal, boolean vertical)
        {
            this.horizontal = horizontal;
            this.vertical = vertical;
        }        

        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Methods ">
        
        public boolean equals(Object obj)
        {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ImageFlipping other = (ImageFlipping) obj;
            if (this.horizontal != other.horizontal) {
                return false;
            }
            if (this.vertical != other.vertical) {
                return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            return hash;
        }

        @Override
        protected ImageFlipping clone() 
        {
            return new ImageFlipping(horizontal, vertical);
        }

        @Override
        public String toString()
        {            
            return format("%s(horizontal=%d,vertical=%d)", getClass().getName(), horizontal, vertical);
        }
        
        // </editor-fold>
    }
    
    // </editor-fold>    
    
    
    private Resolution resolution;
    private ImageFlipping imageFlipping;    
    
    public ImageFlipping getImageFlipping()
    {
        return imageFlipping;
    }

    public void setImageFlipping(ImageFlipping imageFlipping)
    {
        if(imageFlipping == null) throw new NullPointerException();
        this.imageFlipping = imageFlipping;
    }

    public Resolution getResolution()
    {
        return resolution;
    }

    public void setResolution(Resolution resolution)
    {
        if(resolution == null) throw new NullPointerException();
        this.resolution = resolution;
    }

    public CameraSettingsBean()
    {
        this( new Resolution(320, 240), new ImageFlipping());
    }
    
    public CameraSettingsBean(Resolution resolution, ImageFlipping imageFlipping)
    {
        this.setResolution (resolution) ;
        this.setImageFlipping (imageFlipping) ;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CameraSettingsBean other = (CameraSettingsBean) obj;
        if (this.resolution != other.resolution && (this.resolution == null || !this.resolution.equals(other.resolution))) {
            return false;
        }
        if (this.imageFlipping != other.imageFlipping && (this.imageFlipping == null || !this.imageFlipping.equals(other.imageFlipping))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        return hash;
    }    

    @Override
    public String toString()
    {
        return format("%s(resolution=%s,imageFlipping=%s)", getClass().getName(), resolution, imageFlipping);
    }
}
