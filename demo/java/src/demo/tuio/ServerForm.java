/*
 * ServerForm.java
 *
 * Created on July 12, 2007, 5:27 PM
 */

package demo.tuio;

/**
 *
 * @author  gestalt
 */
public class ServerForm extends javax.swing.JFrame
{
    
    /** Creates new form ServerForm */
    public ServerForm()
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

        inetSocketAddressForm1 = new demo.ui.InetSocketAddressForm();
        cameraSettingsForm1 = new demo.ui.CameraSettingsForm();
        imageProcessingForm1 = new demo.ui.ImageProcessingForm();
        jPreviewToggleButton = new javax.swing.JToggleButton();
        jCancelButton = new javax.swing.JButton();
        jOKButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        inetSocketAddressForm1.setBorder(javax.swing.BorderFactory.createTitledBorder(" Internet Socket Address "));
        inetSocketAddressForm1.setFocusable(false);

        cameraSettingsForm1.setBorder(javax.swing.BorderFactory.createTitledBorder(" Camera Settings "));
        cameraSettingsForm1.setFocusable(false);

        imageProcessingForm1.setBorder(javax.swing.BorderFactory.createTitledBorder(" Image Processing "));
        imageProcessingForm1.setFocusable(false);

        jPreviewToggleButton.setText("View Image");

        jCancelButton.setText("Cancel");

        jOKButton.setText("OK");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inetSocketAddressForm1, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addComponent(cameraSettingsForm1, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addComponent(imageProcessingForm1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPreviewToggleButton, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jOKButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(inetSocketAddressForm1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cameraSettingsForm1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(imageProcessingForm1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPreviewToggleButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCancelButton)
                    .addComponent(jOKButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new ServerForm().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private demo.ui.CameraSettingsForm cameraSettingsForm1;
    private demo.ui.ImageProcessingForm imageProcessingForm1;
    private demo.ui.InetSocketAddressForm inetSocketAddressForm1;
    private javax.swing.JButton jCancelButton;
    private javax.swing.JButton jOKButton;
    private javax.swing.JToggleButton jPreviewToggleButton;
    // End of variables declaration//GEN-END:variables
    
}