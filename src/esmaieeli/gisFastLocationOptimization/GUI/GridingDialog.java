/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package esmaieeli.gisFastLocationOptimization.GUI;

import esmaieeli.gisFastLocationOptimization.GIS3D.Grid;
import esmaieeli.gisFastLocationOptimization.GIS3D.LayerDefinition;
import esmaieeli.gisFastLocationOptimization.GIS3D.LocationNode;
import esmaieeli.gisFastLocationOptimization.GIS3D.NumericLayer;
import esmaieeli.gisFastLocationOptimization.GIS3D.ReportResults;
import static com.github.davidmoten.rtree.Entries.entry;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.internal.EntryDefault;
import java.awt.Color;
import java.awt.Component;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 * @author Amir72c
 */
public class GridingDialog extends javax.swing.JDialog {

    MainFramePanel myParent;
    int rtreeCounter = 0;
    ReportResults localReport;
    String[] layers;

    /**
     * Creates new form GridingDialog
     */
    public GridingDialog(java.awt.Window window, MainFramePanel parent, ModalityType modal) {
        super(window, modal);
        initComponents();
        myParent = parent;

        System.out.println(myParent.allData.grid[0][0].name);
        currentGridNameLabel.setText(myParent.allData.grid[0][0].name);

        layers = new String[myParent.allData.all_Layers.size()];
        for (int i = 0; i < layers.length; i++) {
            //System.out.println(((LayerDefinition) mainFrame.allData.all_Layers.get(i)).layerName);
            layers[i] = ((LayerDefinition) myParent.allData.all_Layers.get(i)).layerName;
        }

        layersList.setModel(new javax.swing.AbstractListModel() {
            @Override
            public int getSize() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                return layers.length;
            }

            @Override
            public Object getElementAt(int index) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                return layers[index];
            }
        });

        layersList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                LayerDefinition layer = (LayerDefinition) myParent.allData.all_Layers.get(index);
                if (layer instanceof NumericLayer) {
                    setBackground(Color.RED);
                } else {
                    setBackground(Color.WHITE);
                }
                if (isSelected) {
                    setBackground(getBackground().darker());
                }
                return c;
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        h_grid_num = new javax.swing.JTextField();
        v_grid_num = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        genSimpleGrid = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        treeSizeText = new javax.swing.JFormattedTextField();
        genRtreeGrid = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        genAbsorberGrid = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        layersList = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        setAsGrid = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        currentGridNameLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Grid generation as layer"));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Grid static"));

        h_grid_num.setText("10");

        v_grid_num.setText("10");

        jLabel5.setText("Horizental grid number:");

        jLabel6.setText("Vertical grid number:");

        genSimpleGrid.setText("Generate layer");
        genSimpleGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genSimpleGridActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(h_grid_num))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(v_grid_num, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genSimpleGrid)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(h_grid_num, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genSimpleGrid))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(v_grid_num, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Rtree"));

        jLabel1.setText("Max entities in each geomery:");

        treeSizeText.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0"))));
        treeSizeText.setText("1000");

        genRtreeGrid.setText("Generate layer");
        genRtreeGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genRtreeGridActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(treeSizeText, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(genRtreeGrid))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(treeSizeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(genRtreeGrid)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Absorber"));

        genAbsorberGrid.setText("Generate layer");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(genAbsorberGrid)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(genAbsorberGrid)
                .addContainerGap())
        );

        jLabel2.setText("Status");

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Set layer as grid"));

        jScrollPane1.setViewportView(layersList);

        jLabel3.setText("Select layer:");

        setAsGrid.setText("Set as grid");
        setAsGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setAsGridActionPerformed(evt);
            }
        });

        jLabel4.setText("Current grid:");

        currentGridNameLabel.setText("jLabel7");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(setAsGrid)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentGridNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(currentGridNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setAsGrid)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void genRtreeGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genRtreeGridActionPerformed
        // TODO add your handling code here:
        System.out.println("Starting to make RTree");
        //REPORTING
        String detailedResults = "Generating RTree for benchmarking griding performance." + System.lineSeparator();
        detailedResults = detailedResults + "Number of entities in each leaf: " + Integer.parseInt(treeSizeText.getText()) + System.lineSeparator();
        Calendar currentDate = Calendar.getInstance();
        Date date = currentDate.getTime();
        double startRAM = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0;
        long startTime = System.nanoTime();
        //REPORTING
        RTree<LocationNode, Geometry> tree = RTree.star().maxChildren(Integer.parseInt(treeSizeText.getText())).create();

        for (int i = 0; i < myParent.allData.all_Nodes.length; i++) {
            tree = tree.add(entry(myParent.allData.all_Nodes[i], (Geometry) Geometries.point(myParent.allData.all_Nodes[i].lat, myParent.allData.all_Nodes[i].lon)));
        }
        int numCurrentLayer = myParent.allData.all_Layers.size();
        numCurrentLayer = numCurrentLayer + 1;//NEW LAYER
        rtreeCounter = 0;
        goToDepth(tree.root().get(), 0, numCurrentLayer);

        LayerDefinition tempLayer = new LayerDefinition("category", "RTree"+treeSizeText.getText());
        tempLayer.categories = new String[rtreeCounter];
        tempLayer.colors = new Color[rtreeCounter];
        tempLayer.values = new double[rtreeCounter];
        for (int i = 0; i < rtreeCounter; i++) {
            tempLayer.categories[i] = String.valueOf(i);
            tempLayer.colors[i] = new Color(Color.HSBtoRGB((float) i / (float) rtreeCounter - 1, 1, 1));
            tempLayer.values[i] = Double.valueOf(i + 1);
        }

        //REPORTING
        long endTime = System.nanoTime();
        double elapsed = ((endTime - startTime) / 1000000000);
        double endRAM = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0;
        double usedRAM = endRAM - startRAM;
        localReport = new ReportResults(date, "Gen RTree"+treeSizeText.getText(), startTime, endTime, elapsed, 1, startRAM, endRAM, usedRAM, detailedResults);
        myParent.allData.results.add(localReport);
        myParent.refreshReportList();
        //REPORTING

        myParent.allData.all_Layers.add(tempLayer);

        System.out.println("Layer made");
        myParent.refreshLayersList();

    }//GEN-LAST:event_genRtreeGridActionPerformed

    private void genSimpleGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genSimpleGridActionPerformed
        // TODO add your handling code here:

        //REPORTING
        String detailedResults = "Generating default simple equal-width grid." + System.lineSeparator();
        detailedResults = detailedResults + "Number of horizontal simple grid: " + h_grid_num.getText() + System.lineSeparator();
        detailedResults = detailedResults + "Number of vertical simple grid: " + v_grid_num.getText() + System.lineSeparator();
        Calendar currentDate = Calendar.getInstance();
        Date date = currentDate.getTime();
        double startRAM = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0;
        long startTime = System.nanoTime();
        //REPORTING

        int H_G = Integer.parseInt(h_grid_num.getText());
        int V_G = Integer.parseInt(v_grid_num.getText());
        myParent.allData.grid = new Grid[H_G][V_G];
        for (int i = 0; i < H_G; i++) {
            for (int j = 0; j < V_G; j++) {
                myParent.allData.grid[i][j] = new Grid(i, j, myParent.allData.myScale.min_x + i * ((myParent.allData.myScale.max_x - myParent.allData.myScale.min_x) / (double) myParent.allData.grid.length), myParent.allData.myScale.min_x + (i + 1) * ((myParent.allData.myScale.max_x - myParent.allData.myScale.min_x) / (double) myParent.allData.grid.length), myParent.allData.myScale.min_y + j * ((myParent.allData.myScale.max_y - myParent.allData.myScale.min_y) / (double) myParent.allData.grid[0].length), myParent.allData.myScale.min_y + (j + 1) * ((myParent.allData.myScale.max_y - myParent.allData.myScale.min_y) / (double) myParent.allData.grid[0].length), "Simple equal-width grid");
            }
        }

        for (int i = 0; i < myParent.allData.all_Nodes.length; i++) {
            if (myParent.allData.all_Nodes[i].myWays[0].type != null) {
                myParent.allData.all_Nodes[i].myGrid = myParent.preProcessor.detect_grid(myParent.allData, myParent.allData.all_Nodes[i].lat, myParent.allData.all_Nodes[i].lon);
                myParent.allData.all_Nodes[i].myGrid.temporaryNodes.add(myParent.allData.all_Nodes[i]);
            }
        }

        for (int i = 0; i < myParent.allData.grid.length; i++) {
            for (int j = 0; j < myParent.allData.grid[0].length; j++) {
                myParent.allData.grid[i][j].myNodes = new LocationNode[myParent.allData.grid[i][j].temporaryNodes.size()];
                for (int s = 0; s < myParent.allData.grid[i][j].temporaryNodes.size(); s++) {
                    myParent.allData.grid[i][j].myNodes[s] = (LocationNode) myParent.allData.grid[i][j].temporaryNodes.get(s);
                }
            }
        }

        //REPORTING
        long endTime = System.nanoTime();
        double elapsed = ((endTime - startTime) / 1000000000);
        double endRAM = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0;
        double usedRAM = endRAM - startRAM;
        localReport = new ReportResults(date, "Generate simple grid", startTime, endTime, elapsed, 1, startRAM, endRAM, usedRAM, detailedResults);
        myParent.allData.results.add(localReport);
        myParent.refreshReportList();
        //REPORTING
    }//GEN-LAST:event_genSimpleGridActionPerformed

    private void setAsGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setAsGridActionPerformed
        // TODO add your handling code here:

        int activeLayer = layersList.getSelectedIndex();
        if (((LayerDefinition) myParent.allData.all_Layers.get(activeLayer)).myType.equals("category") || ((LayerDefinition) myParent.allData.all_Layers.get(activeLayer)).myType.equals("base") || ((LayerDefinition) myParent.allData.all_Layers.get(activeLayer)).myType.equals("image")) {

            //REPORTING
            String detailedResults = "Set a layer as grid." + System.lineSeparator();
            detailedResults = detailedResults + "Layer name: " + ((LayerDefinition) myParent.allData.all_Layers.get(layersList.getSelectedIndex())).layerName;
            Calendar currentDate = Calendar.getInstance();
            Date date = currentDate.getTime();
            double startRAM = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0;
            long startTime = System.nanoTime();
            //REPORTING

            int numGrids = ((LayerDefinition) myParent.allData.all_Layers.get(layersList.getSelectedIndex())).categories.length;
            myParent.allData.grid = new Grid[1][numGrids];
            double minX[] = new double[numGrids];
            double maxX[] = new double[numGrids];
            double minY[] = new double[numGrids];
            double maxY[] = new double[numGrids];
            int numNodesInGrid[] = new int[numGrids];
            for (int i = 0; i < numGrids; i++) {
                minX[i] = Double.POSITIVE_INFINITY;
                maxX[i] = Double.NEGATIVE_INFINITY;
                minY[i] = Double.POSITIVE_INFINITY;
                maxY[i] = Double.NEGATIVE_INFINITY;
                numNodesInGrid[i] = 0;
            }
            int categoryValue;
            for (int i = 0; i < myParent.allData.all_Nodes.length; i++) {

                categoryValue = ((short[]) myParent.allData.all_Nodes[i].layers.get(activeLayer))[0] - 1;
//                System.out.println(categoryValue);
                if (myParent.allData.all_Nodes[i].lat < minX[categoryValue]) {
                    minX[categoryValue] = myParent.allData.all_Nodes[i].lat;
                }
                if (myParent.allData.all_Nodes[i].lat > maxX[categoryValue]) {
                    maxX[categoryValue] = myParent.allData.all_Nodes[i].lat;
                }

                if (myParent.allData.all_Nodes[i].lon < minY[categoryValue]) {
                    minY[categoryValue] = myParent.allData.all_Nodes[i].lon;
                }
                if (myParent.allData.all_Nodes[i].lon > maxY[categoryValue]) {
                    maxY[categoryValue] = myParent.allData.all_Nodes[i].lon;
                }
                numNodesInGrid[categoryValue] = numNodesInGrid[categoryValue] + 1;
            }
            int counter[] = new int[numGrids];
            for (int i = 0; i < numGrids; i++) {
                myParent.allData.grid[0][i] = new Grid(i, 0, minX[i], maxX[i], minY[i], maxY[i], ((LayerDefinition) myParent.allData.all_Layers.get(layersList.getSelectedIndex())).layerName);
                myParent.allData.grid[0][i].myNodes = new LocationNode[numNodesInGrid[i]];
                counter[i] = 0;
            }

            for (int i = 0; i < myParent.allData.all_Nodes.length; i++) {
                categoryValue = ((short[]) myParent.allData.all_Nodes[i].layers.get(activeLayer))[0] - 1;
                myParent.allData.grid[0][categoryValue].myNodes[counter[categoryValue]] = myParent.allData.all_Nodes[i];
                counter[categoryValue] = counter[categoryValue] + 1;
                myParent.allData.all_Nodes[i].myGrid = myParent.allData.grid[0][categoryValue];
            }
            //REPORTING
            long endTime = System.nanoTime();
            double elapsed = ((endTime - startTime) / 1000000000);
            double endRAM = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0;
            double usedRAM = endRAM - startRAM;
            localReport = new ReportResults(date, "Set grid: "+((LayerDefinition) myParent.allData.all_Layers.get(layersList.getSelectedIndex())).layerName, startTime, endTime, elapsed, 1, startRAM, endRAM, usedRAM, detailedResults);
            myParent.allData.results.add(localReport);
            myParent.refreshReportList();
            //REPORTING
        } else {
            System.out.println("CAN'T HANDLE NUMERIC LAYERS!");
        }

    }//GEN-LAST:event_setAsGridActionPerformed

    public void goToDepth(Node node, int depth, int layerIndex) {
        if (node instanceof Leaf) {
            for (int i = 0; i < ((Leaf) node).count(); i++) {
//                System.out.println(((Leaf)node).entries().get(i).getClass());
                short[] temp = new short[1];
                temp[0] = (short) (rtreeCounter + 1);
                ((LocationNode) (((EntryDefault) (((Leaf) node).entries().get(i))).value())).layers.add(temp);
            }
            rtreeCounter = rtreeCounter + 1;
//            System.out.println("LEAF");
        } else {
            for (int i = 0; i < ((NonLeaf) node).count(); i++) {
                goToDepth(((NonLeaf) node).child(i), depth + 1, layerIndex);
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel currentGridNameLabel;
    private javax.swing.JButton genAbsorberGrid;
    private javax.swing.JButton genRtreeGrid;
    private javax.swing.JButton genSimpleGrid;
    private javax.swing.JTextField h_grid_num;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList layersList;
    private javax.swing.JButton setAsGrid;
    private javax.swing.JFormattedTextField treeSizeText;
    private javax.swing.JTextField v_grid_num;
    // End of variables declaration//GEN-END:variables
}
