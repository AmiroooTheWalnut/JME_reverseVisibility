/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package esmaieeli.gisFastLocationOptimization.GUI;

import esmaieeli.gisFastLocationOptimization.GIS3D.ImageLayer;
import esmaieeli.gisFastLocationOptimization.GIS3D.LayerDefinition;
import esmaieeli.gisFastLocationOptimization.GIS3D.NumericLayer;
import esmaieeli.gisFastLocationOptimization.GIS3D.ReportResults;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

/**
 *
 * @author Amir72c
 */
public class AutoImageLayers extends javax.swing.JDialog {

    /**
     * Creates new form AutoImageLayers
     */
    public AutoImageLayers(java.awt.Window window, MainFramePanel parent, ModalityType modal) {
        super(window, modal);
        myParent = parent;
        initComponents();
    }
    MainFramePanel myParent;
    File configFile;
    ArrayList images;
    ImageControllerDefaults imageControllerDefaults;
    ImageLayer imageControlled[];
    int imgNumber = 0;
    ReportResults localReport;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jProgressBar1 = new javax.swing.JProgressBar();
        runButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        jFormattedTextField3 = new javax.swing.JFormattedTextField();
        jFormattedTextField4 = new javax.swing.JFormattedTextField();
        jFormattedTextField5 = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButton1.setText("Open folder");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "File name", "Title 2"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Config file:");

        jLabel2.setText("No file");

        jLabel3.setText("X value:");

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField1.setText("-196.44");

        jLabel4.setText("Y value:");

        jLabel5.setText("Width:");

        jLabel6.setText("Height:");

        jFormattedTextField2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField2.setText("-94.49");

        jFormattedTextField3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField3.setText("342.56");

        jFormattedTextField4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField4.setText("168.68");

        jFormattedTextField5.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jFormattedTextField5.setText("7");

        jLabel7.setText("RGB telorance:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jFormattedTextField1))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jFormattedTextField2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jFormattedTextField3))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jFormattedTextField4))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton1)
                                    .addComponent(runButton)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jFormattedTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(runButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jFormattedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jFormattedTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jFormattedTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        JFileChooser jFileChooser1 = new javax.swing.JFileChooser();
        jFileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser1.setAcceptAllFileFilterUsed(false);
        int returnVal = jFileChooser1.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            imageControllerDefaults = new ImageControllerDefaults(myParent);
            imageControllerDefaults.xPosition = Float.valueOf(jFormattedTextField1.getText());
            imageControllerDefaults.yPosition = Float.valueOf(jFormattedTextField2.getText());
            imageControllerDefaults.width = Float.valueOf(jFormattedTextField3.getText());
            imageControllerDefaults.height = Float.valueOf(jFormattedTextField4.getText());
            File directory = jFileChooser1.getSelectedFile();
            images = new ArrayList();
            for (int i = 0; i < directory.listFiles().length; i++) {
                Image img = null;
                try {
                    img = ImageIO.read(directory.listFiles()[i]);
                } catch (IOException ex) {
                }
                if (img != null) {
                    images.add(directory.listFiles()[i]);
                } else {
                    int index = directory.listFiles()[i].getName().lastIndexOf('.');
                    if (index > 0) {
                        String extension = directory.listFiles()[i].getName().substring(index + 1);
                        if (extension.equals("config")) {
                            configFile = directory.listFiles()[i];
                            jLabel2.setText(configFile.getName());
                        }
                    }
                }
            }
            Object[][] data = new Object[images.size()][2];
            String[] headers = new String[2];
            headers[0] = "File name";
            headers[1] = "Process";
            imageControlled = new ImageLayer[images.size()];
            int imageCounter = 0;
            int globalCounter = 0;
            BufferedReader br;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
                String sCurrentLine;
                try {
                    while ((sCurrentLine = br.readLine()) != null) {
                        String sections[] = sCurrentLine.split(",");
                        data[globalCounter][0] = sections[0];
                        data[globalCounter][1] = new Boolean(sections[4]);
                        globalCounter = globalCounter + 1;
                        if (sections[4].toLowerCase().equals("true")) {
                            String fileName = sections[0];
                            int numCategories = Integer.valueOf(sections[1]);
                            String allColors = sections[2];
                            boolean isBenefit = Boolean.valueOf(sections[3]);
                            String group = sections[5];
                            String RGB[] = allColors.split("-");
                            BufferedImage img = null;
                            for (int i = 0; i < images.size(); i++) {
                                if (((File) images.get(i)).getName().equals(fileName)) {
                                    img = ImageIO.read(new File(((File) images.get(i)).getPath()));
                                }
                            }
                            imageControlled[imageCounter] = new ImageLayer(fileName + "_" + group, imageControllerDefaults, img);
                            imageControlled[imageCounter].colors = new Color[numCategories];
                            imageControlled[imageCounter].values = new double[numCategories];
                            imageControlled[imageCounter].categories = new String[numCategories];
                            imageControlled[imageCounter].isBenefit = isBenefit;
                            for (int i = 0; i < numCategories; i++) {
                                String colorInts[] = RGB[i].split("/");
                                imageControlled[imageCounter].colors[i] = new Color(Integer.parseInt(colorInts[0]), Integer.parseInt(colorInts[1]), Integer.parseInt(colorInts[2]));
                                if (isBenefit == true) {
                                    imageControlled[imageCounter].values[i] = ((1f / (float) numCategories) * (i + 1));
                                } else {
                                    imageControlled[imageCounter].values[i] = (1 - ((1f / (float) numCategories) * (i)));
                                }
                                imageControlled[imageCounter].categories[i] = String.valueOf(i);
                            }
                            imageCounter = imageCounter + 1;
                        }
                    }
                    jProgressBar1.setMaximum(imageCounter);
                } catch (IOException ex) {
                    Logger.getLogger(AutoImageLayers.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AutoImageLayers.class.getName()).log(Level.SEVERE, null, ex);
            }
            jTable1.setModel(new javax.swing.table.DefaultTableModel(
                    data,
                    headers) {
                Class[] types = new Class[]{
                    java.lang.String.class, java.lang.Boolean.class
                };
                boolean[] canEdit = new boolean[]{
                    false, false
                };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
        }
        ExcelAdapter excelAdapter = new ExcelAdapter(jTable1);
    }//GEN-LAST:event_jButton1ActionPerformed

    private String detailedResults;
    
    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        // TODO add your handling code here:
        //REPORTING
        detailedResults = "Automatically add image layers."+System.lineSeparator();
        Calendar currentDate = Calendar.getInstance();
        Date date = currentDate.getTime();
        double startRAM = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0;
        long startTime = System.nanoTime();
        //REPORTING
        
        detailedResults=detailedResults+"Telorance: "+jFormattedTextField5.getText()+System.lineSeparator();
        
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                for (int itr = 0; itr < imageControlled.length; itr++) {
                    imgNumber = itr;

                    if (imageControlled[imgNumber] != null) {
                        detailedResults=detailedResults+"Image name: "+imageControlled[itr].layerName+System.lineSeparator();
                        BufferedImage img = null;
                        InputStream in = new ByteArrayInputStream(imageControlled[imgNumber].imageInByte);

                        try {
                            img = ImageIO.read(in);
                        } catch (IOException ex) {
                            System.out.println("Image not read!");
                        }
                        myParent.allData.all_Layers.add(imageControlled[imgNumber]);
                        int numLayers = myParent.allData.all_Layers.size();
//                    System.out.println("numLayers "+numLayers);
                        boolean isBroken;
                        for (int i = 0; i < myParent.allData.all_Ways.length; i++) {
                            for (int j = 0; j < myParent.allData.all_Ways[i].myNodes.length; j++) {
//                            System.out.println("node layers "+myParent.allData.all_Ways[i].myNodes[j].layers.size());
                                if (myParent.allData.all_Ways[i].myNodes[j].layers.size() < numLayers) {
                                    int imageX = (int) (((double) (myParent.allData.all_Ways[i].myNodes[j].renderingLocation.x - imageControlled[imgNumber].my_position.x) / (double) imageControlled[imgNumber].my_width) * (double) img.getWidth());
                                    int imageY = (int) ((((double) myParent.allData.all_Ways[i].myNodes[j].renderingLocation.y - (double) imageControlled[imgNumber].my_position.y) / (double) imageControlled[imgNumber].my_height) * (double) img.getHeight());
                                    isBroken = checkColor(img, imageX, imageY, i, j);
                                    if (isBroken == false) {
                                        int dist = 1;
                                        while (isBroken == false) {
                                            for (int l = -dist; l < dist; l++) {
                                                for (int m = -dist; m < dist; m++) {
                                                    isBroken = checkColor(img, imageX + l, imageY + m, i, j);
                                                    if (isBroken == true) {
                                                        break;
                                                    }
                                                }
                                                if (isBroken == true) {
                                                    break;
                                                }
                                            }
                                            dist = dist + 1;
                                        }
                                    }
                                }
                            }
                        }
                        jProgressBar1.setValue(itr);
                    }
                }
                detailedResults=detailedResults+"Group layer: locationLayer"+System.lineSeparator();
                detailedResults=detailedResults+"Group layer: studentLayer"+System.lineSeparator();
                detailedResults=detailedResults+"Group layer: luxuryLayer"+System.lineSeparator();
                detailedResults=detailedResults+"Group layer: tourismLayer"+System.lineSeparator();
                NumericLayer locationLayer = new NumericLayer("locationLayer");
                NumericLayer studentLayer = new NumericLayer("studentLayer");
                NumericLayer luxuryLayer = new NumericLayer("luxuryLayer");
                NumericLayer tourismLayer = new NumericLayer("tourismLayer");

                ArrayList locationLayerIndex = new ArrayList();
                ArrayList studentLayerIndex = new ArrayList();
                ArrayList luxuryLayerIndex = new ArrayList();
                ArrayList tourismLayerIndex = new ArrayList();
                for (int i = 0; i < myParent.allData.all_Layers.size(); i++) {
                    if (((LayerDefinition) myParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("location")) {
                        locationLayerIndex.add(i);
                    }
                    if (((LayerDefinition) myParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("student")) {
                        studentLayerIndex.add(i);
                    }
                    if (((LayerDefinition) myParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("luxury")) {
                        luxuryLayerIndex.add(i);
                    }
                    if (((LayerDefinition) myParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("tourism")) {
                        tourismLayerIndex.add(i);
                    }
                }
                myParent.allData.all_Layers.add(locationLayer);
                myParent.allData.all_Layers.add(studentLayer);
                myParent.allData.all_Layers.add(luxuryLayer);
                myParent.allData.all_Layers.add(tourismLayer);

                int numLayers = myParent.allData.all_Layers.size();

                for (int i = 0; i < myParent.allData.all_Ways.length; i++) {
                    for (int j = 0; j < myParent.allData.all_Ways[i].myNodes.length; j++) {
                        if (myParent.allData.all_Ways[i].myNodes[j].layers.size() < numLayers) {
                            double temp = 0;
                            for (int k = 0; k < locationLayerIndex.size(); k++) {
                                int index = ((short[]) myParent.allData.all_Ways[i].myNodes[j].layers.get((int) locationLayerIndex.get(k)))[0];
                                temp = temp + ((LayerDefinition) myParent.allData.all_Layers.get((int) locationLayerIndex.get(k))).values[index - 1];
                            }
                            temp = temp / (float) (locationLayerIndex.size());
                            myParent.allData.all_Ways[i].myNodes[j].layers.add(temp);

                            temp = 0;
                            for (int k = 0; k < studentLayerIndex.size(); k++) {
                                int index = ((short[]) myParent.allData.all_Ways[i].myNodes[j].layers.get((int) studentLayerIndex.get(k)))[0];
                                temp = temp + ((LayerDefinition) myParent.allData.all_Layers.get((int) studentLayerIndex.get(k))).values[index - 1];
                            }
                            temp = temp / (float) (studentLayerIndex.size());
                            myParent.allData.all_Ways[i].myNodes[j].layers.add(temp);

                            temp = 0;
                            for (int k = 0; k < luxuryLayerIndex.size(); k++) {
                                int index = ((short[]) myParent.allData.all_Ways[i].myNodes[j].layers.get((int) luxuryLayerIndex.get(k)))[0];
                                temp = temp + ((LayerDefinition) myParent.allData.all_Layers.get((int) luxuryLayerIndex.get(k))).values[index - 1];
                            }
                            temp = temp / (float) (luxuryLayerIndex.size());
                            myParent.allData.all_Ways[i].myNodes[j].layers.add(temp);

                            temp = 0;
                            for (int k = 0; k < tourismLayerIndex.size(); k++) {
                                int index = ((short[]) myParent.allData.all_Ways[i].myNodes[j].layers.get((int) tourismLayerIndex.get(k)))[0];
                                temp = temp + ((LayerDefinition) myParent.allData.all_Layers.get((int) tourismLayerIndex.get(k))).values[index - 1];
                            }
                            temp = temp / (float) (tourismLayerIndex.size());
                            myParent.allData.all_Ways[i].myNodes[j].layers.add(temp);
                        }
                    }
                }
                jProgressBar1.setValue(imgNumber + 1);
            }
        });
        thread.start();
        try {
            thread.join();
            //REPORTING
            long endTime = System.nanoTime();
            double elapsed = ((endTime - startTime) / 1000000000);
            double endRAM = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0;
            double usedRAM = endRAM - startRAM;
            localReport = new ReportResults(date, "Auto image add", startTime, endTime, elapsed, 1, startRAM, endRAM, usedRAM, detailedResults);
            myParent.allData.results.add(localReport);
            myParent.refreshReportList();
            //REPORTING
            myParent.refreshLayersList();
        } catch (InterruptedException ex) {
            Logger.getLogger(AutoImageLayers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_runButtonActionPerformed

    private boolean checkColor(BufferedImage img, int imageX, int imageY, int i, int j) {
        boolean isBroken = false;
        if (imageX > -1 && imageX < img.getWidth() - 1 && -imageY + img.getHeight() > -1 && -imageY + img.getHeight() < img.getHeight() - 1) {
            Color temp = new Color(img.getRGB(imageX, -imageY + img.getHeight()));
            for (int k = 0; k < imageControlled[imgNumber].colors.length; k++) {
                if (Math.abs(temp.getRed() - imageControlled[imgNumber].colors[k].getRed()) + Math.abs(temp.getGreen() - imageControlled[imgNumber].colors[k].getGreen()) + Math.abs(temp.getBlue() - imageControlled[imgNumber].colors[k].getBlue()) < Integer.parseInt(jFormattedTextField5.getText())) {
                    short[] pass = new short[1];
                    pass[0] = (short) (k + 1);
//                    System.out.println("Passed value: "+pass[0]);
//                    System.out.println("Size of colors: "+imageControlled[imgNumber].colors.length);
                    myParent.allData.all_Ways[i].myNodes[j].layers.add(pass);
                    isBroken = true;
                    break;
                }
            }
        }
        return isBroken;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JFormattedTextField jFormattedTextField3;
    private javax.swing.JFormattedTextField jFormattedTextField4;
    private javax.swing.JFormattedTextField jFormattedTextField5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton runButton;
    // End of variables declaration//GEN-END:variables
}
