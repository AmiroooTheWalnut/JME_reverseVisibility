/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esmaieeli.gisFastLocationOptimization.Simulation;

import esmaieeli.gisFastLocationOptimization.GIS3D.AllData;
import esmaieeli.gisFastLocationOptimization.GIS3D.LayerDefinition;
import esmaieeli.gisFastLocationOptimization.GIS3D.ParallelProcessor;
import jankovicsandras.imagetracer.ImageTracer;
import static jankovicsandras.imagetracer.ImageTracer.checkoptions;
import static jankovicsandras.imagetracer.ImageTracer.imagedataToTracedata;
import static jankovicsandras.imagetracer.ImageTracer.loadImageData;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class ParallelVectorToPolyProcess extends ParallelProcessor {

    public VectorToPolygon myParent;
    public AllData myAllData;
    public int[][] myInput;
    public int myLayerIndex;
    public int myThreadIndex;
    public HashMap<Integer, SimplePolygons> localPolygons = new HashMap();
    
    boolean isReported=false;
    int lastProgressPercent=0;

    public ParallelVectorToPolyProcess(VectorToPolygon parent, AllData allData, int[][] input, int layerIndex, int startIndex, int endIndex, int threadIndex) {
        super(startIndex, endIndex);
        myParent = parent;
        myAllData = allData;
        myInput = input;
        myLayerIndex = layerIndex;
        myThreadIndex = threadIndex;
        myThread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = startIndex; i < endIndex; i++) {
                    SimplePolygons boundaries = new SimplePolygons();
                    //FOR DEBUGGING
//                  saveIndexedImageAsPNG(getVDImage(input, i), allData, layerIndex, "temp");
                    //FOR DEBUGGING

                    BufferedImage image = myParent.indexedImageToBufferedImage(myParent.getVDImage(myInput, i+1), myAllData, myLayerIndex);

                    try {
                        ImageTracer.ImageData imgd = loadImageData(image);

                        HashMap<String, Float> options = checkoptions(null);
                        options.put("roundcoords", 3f);
                        options.put("blurdelta", 1f);
                        options.put("colorsampling", 0f);
                        options.put("numberofcolors", 2f);
                        options.put("ltres", 0.001f);
                        options.put("qtres", 0.001f);
                        options.put("mincolorratio", 0.0f);

                        byte[][] palette = new byte[2][4];
                        palette[0][0] = (byte) (-128);  // R
                        palette[0][1] = (byte) (-128);  // G
                        palette[0][2] = (byte) (-128);  // B
                        palette[0][3] = (byte) 127;     // A

                        palette[1][0] = (byte) (127);  // R
                        palette[1][1] = (byte) (-128);  // G
                        palette[1][2] = (byte) (-128);  // B
                        palette[1][3] = (byte) 127;     // A

                        ImageTracer.IndexedImage ii = imagedataToTracedata(imgd, options, palette);
                        
                        boolean isPolyAdded=false;
                        for (int j = 0; j < ii.layers.get(1).size(); j++) {
                            SimplePolygon polygon = new SimplePolygon();
                            for (int k = 0; k < ii.layers.get(1).get(j).size(); k++) {
                                if (ii.layers.get(1).get(j).get(k)[0] == 1) {
                                    polygon.points.add(new SimplePoint(ii.layers.get(1).get(j).get(k)[3], ii.layers.get(1).get(j).get(k)[4]));
                                } else {
                                    polygon.points.add(new SimplePoint(ii.layers.get(1).get(j).get(k)[5], ii.layers.get(1).get(j).get(k)[6]));
                                }
                            }
//                            System.out.println("polygon " + j + " added");
                            boundaries.polygons.add(polygon);
                            isPolyAdded=true;
                        }
                        if (isPolyAdded==false) {
                            System.out.println("EMPTY POLYGON ON THREAD: " + myThreadIndex);
                        }
                        
                    } catch (Exception ex) {
                        Logger.getLogger(VectorToPolygon.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if(isReported==false){
                        System.out.println("THREAD "+myThreadIndex+" progressed: "+(((float)(i-startIndex)/(float)(endIndex-startIndex))*100));
                        lastProgressPercent=(int)(((float)(i-startIndex)/(float)(endIndex-startIndex))*10);
                        isReported=true;
                    }else if(lastProgressPercent<(int)(((float)(i-startIndex)/(float)(endIndex-startIndex))*10)){
                        isReported=false;
                    }
                    
                    localPolygons.put(i+1, boundaries);
                }
            }
        });

    }

}
