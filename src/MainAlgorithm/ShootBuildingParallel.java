/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MainAlgorithm;

import com.jme3.math.ColorRGBA;
import esmaieeli.utilities.taskThreading.ParallelProcessor;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import testPrototypes.CustomMesh;
import testPrototypes.StreetSegment;

/**
 *
 * @author user
 */
public class ShootBuildingParallel extends ParallelProcessor {

    Runnable myRunnable;

    public ShootBuildingParallel(StreetVisAlgorithmV2 parent, StreetSegment data, int startIndex, int endIndex) {
        super(parent, data, startIndex, endIndex);
        myRunnable = new Runnable() {
            @Override
            public void run() {
//                parent.myApp.enqueue(new Callable() {
//                    public Object call() throws Exception {
//                        // call methods that modify the scene graph here
//                        for (int i = startIndex; i < endIndex; i++) {
//                            //for (int i = 0; i < 60; i++) {//FOR DEBUGGING
//                            parent.shootForBuildingByID(i, data, ColorRGBA.Black, -0.01f);
//                            System.out.println("shot meshes: " + ((float) i) / (endIndex-startIndex));
//                        }
//                        return null;
//                    }
//                });

                for (int i = startIndex; i < endIndex; i++) {
                    //for (int i = 0; i < 60; i++) {//FOR DEBUGGING
                    parent.shootForBuildingByID(i, data, ColorRGBA.Black, -0.01f);
                    //System.out.println("shot meshes: " + ((float) i) / (endIndex - startIndex));
                }
                System.out.println("THREAD FINISHED");
            }
        };
    }

    public void addRunnableToQueue(ArrayList<Callable<Object>> calls) {
        calls.add(Executors.callable(myRunnable));
    }

}
