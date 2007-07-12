/*
 * InedAddress.java
 * 
 * Created on Jul 12, 2007, 3:47:08 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package demo.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import static java.lang.String.format;
import java.io.Serializable;

/**
 *
 * @author gestalt
 * @version 0.1
 */
public class InetSocketAddressBean implements Serializable
{
    private final static int PORT_MIN = 1, PORT_MAX = 65535;
    
    private String hostName;
    private int port;
    
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private final VetoableChangeSupport vetos   = new VetoableChangeSupport(this);

    public String getHostName()
    {
        System.out.printf("get-hostName: %s\n", hostName);
        return hostName;
    }

    public void setHostName(String hostName) throws PropertyVetoException
    {
        if(hostName == null) throw new NullPointerException("Host name cannot be null!");
        
        final String oldValue = this.hostName;
        vetos.fireVetoableChange("hostName", oldValue, hostName);
        this.hostName = hostName;
        changes.firePropertyChange("hostName", oldValue, hostName);        
        System.out.printf("set-hostName: %s -> %s\n", oldValue, hostName);
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port) throws PropertyVetoException
    {
        if(port < PORT_MIN || port >= PORT_MAX) 
            throw new IllegalArgumentException(format("Illegal port number: must [%d,%d] but is %d!", PORT_MIN, PORT_MAX, port));
        
        final int oldValue = this.port;
        vetos.fireVetoableChange("port", oldValue, port);
        this.port = port;
        changes.firePropertyChange("port", oldValue, port);
        System.out.printf("port: %d -> %d\n", oldValue, port);
    }
    
    
    public InetSocketAddressBean()
    {
        this("localhost", 49152);
    }    
    
    public InetSocketAddressBean(String hostName, int port)
    {
        try 
        {
            setHostName (hostName);
            setPort(port);
        } //can not happen since no listeners (throwers) are registered yet!
        catch(PropertyVetoException pve) {}
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
        final InetSocketAddressBean other = (InetSocketAddressBean) obj;
        if (this.hostName == null || !this.hostName.equals(other.hostName)) {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 37 * hash + this.hostName != null ? this.hostName.hashCode() : 0;
        hash = 37 * hash + this.port;
        return hash;
    }

    @Override
    public String toString()
    {
        return format("%s[hostName=%s,port=%d]", InetSocketAddressBean.class.getName(), hostName, port);
    }
       
    public void addPropertyChangeListener( PropertyChangeListener  listener )
    {
        changes.addPropertyChangeListener( listener );
    }

//    public void addPropertyChangeListener( String propertyName, PropertyChangeListener  listener )
//    {
//        changes.addPropertyChangeListener( propertyName, listener );
//    }
    
    
    public void removePropertyChangeListener( PropertyChangeListener  listener )
    {
        changes.removePropertyChangeListener( listener );
    }   
    
//    public void removePropertyChangeListener( String propertyName, PropertyChangeListener  listener )
//    {
//        changes.removePropertyChangeListener( propertyName, listener );
//    }
    
    
    public void addVetoableChangeListener( VetoableChangeListener listener )
    {
        vetos.addVetoableChangeListener( listener );
    }

//    public void addVetoableChangeListener( String propertyName, VetoableChangeListener listener )
//    {
//        vetos.addVetoableChangeListener( propertyName, listener );
//    }
    
    
    public void removeVetoableChangeListener( VetoableChangeListener listener )
    {
        vetos.removeVetoableChangeListener( listener );
    }      
    
//    public void removeVetoableChangeListener( String propertyName, VetoableChangeListener listener )
//    {
//        vetos.removeVetoableChangeListener( propertyName, listener );
//    }          
}
