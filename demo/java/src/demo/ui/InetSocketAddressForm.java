/*
 * CameraSettingsForm.java
 *
 * Created on July 12, 2007, 3:39 PM
 */

package demo.ui;

import java.beans.PropertyVetoException;
import java.beans.beancontext.BeanContext;
import javax.swing.JPanel;

/**
 *
 * @author  gestalt
 */
public class InetSocketAddressForm extends JPanel
{

    public InetSocketAddressBean getBoundBean()
    {
        return boundBean;
    }

    public InetSocketAddressForm()
    {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        final javax.swing.JLabel jHostLabel = new javax.swing.JLabel();
        final javax.swing.JLabel jPortLabel = new javax.swing.JLabel();

        boundBean.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {
            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                boundBeanPropertyChange(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("demo/ui/Bundle"); // NOI18N
        jHostLabel.setText(bundle.getString("InetSocketAddressForm.jHostLabel.text")); // NOI18N

        jHostTextField.setText(boundBean.getHostName());
        jHostTextField.addCaretListener(new javax.swing.event.CaretListener()
        {
            public void caretUpdate(javax.swing.event.CaretEvent evt)
            {
                jHostTextFieldCaretUpdate(evt);
            }
        });

        jPortLabel.setText(bundle.getString("InetSocketAddressForm.jPortLabel.text")); // NOI18N

        jPortSpinner.setModel(new javax.swing.SpinnerNumberModel(49152, 1, 65535, 1));
        jPortSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(jPortSpinner, "#"));
        jPortSpinner.setValue(boundBean.getPort());
        jPortSpinner.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                jPortSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jHostLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jHostTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPortLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPortSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jHostTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPortLabel)
                    .addComponent(jPortSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jHostLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void jHostTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_jHostTextFieldCaretUpdate
    cyclic = true;
    try { boundBean.setHostName(jHostTextField.getText()); }
    catch(PropertyVetoException pve) { /*jHostTextField.setText(boundBean.getHostName());*/ }    
    finally {cyclic = false; }
}//GEN-LAST:event_jHostTextFieldCaretUpdate

private void jPortSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jPortSpinnerStateChanged
    cyclic = true;
    try { boundBean.setPort( (Integer)jPortSpinner.getValue() ); }
    catch(PropertyVetoException pve) { /*jPortSpinner.setValue( boundBean.getPort() );*/ }
    finally {cyclic = false; }
}//GEN-LAST:event_jPortSpinnerStateChanged

private void boundBeanPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_boundBeanPropertyChange
   if(cyclic) return;
   
   if(evt.getPropertyName().equals("hostName")) 
       jHostTextField.setText(boundBean.getHostName());
   else if(evt.getPropertyName().equals("port"))
       jPortSpinner.setValue( boundBean.getPort() );
}//GEN-LAST:event_boundBeanPropertyChange
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final demo.ui.InetSocketAddressBean boundBean = new demo.ui.InetSocketAddressBean();
    private final javax.swing.JTextField jHostTextField = new javax.swing.JTextField();
    private final javax.swing.JSpinner jPortSpinner = new javax.swing.JSpinner();
    // End of variables declaration//GEN-END:variables
    private boolean cyclic;
}