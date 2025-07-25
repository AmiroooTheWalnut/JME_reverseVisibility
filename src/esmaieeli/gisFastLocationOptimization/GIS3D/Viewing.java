package esmaieeli.gisFastLocationOptimization.GIS3D;

import esmaieeli.gisFastLocationOptimization.GUI.MainFrame;
import esmaieeli.gisFastLocationOptimization.Simulation.FacilityLocation;
import esmaieeli.gisFastLocationOptimization.Simulation.Lava_parallel;
import esmaieeli.gisFastLocationOptimization.Simulation.NeighborNode;
import esmaieeli.gisFastLocationOptimization.Simulation.Routing;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import com.jme3.util.BufferUtils;
import esmaieeli.gisFastLocationOptimization.GUI.MainFramePanel;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Viewing extends SimpleApplication {

//    Mesh all_meshes[];
    Mesh bachedMesh;
    ArrayList<Geometry> highDetailedGeomList = new ArrayList();
    ArrayList<Geometry> mediumDetailedGeomList = new ArrayList();
    ArrayList<Geometry> lowDetailedGeomList = new ArrayList();
    MainFramePanel mainFParent;
    public boolean isRefreshing = false;
    public boolean isIterateDebug = false;
    public Node myRootNode = rootNode;
    public Node collideHost = new Node();
    public Geometry plan;
    public boolean isPickingRoute = false;
    public boolean isPickingNode = false;
    Picture crossHair;
    public AppSettings mySettings;
    boolean isOriginSet = false;
    boolean isDestinationSet = false;
    LocationNode startNode = null;
    LocationNode endNode = null;
    BitmapText valueText[];

    public boolean isBatchRendering = true;

    public Viewing(MainFramePanel parent) {
        mainFParent = parent;
        this.setPauseOnLostFocus(false);
    }
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("focus") && keyPressed == false) {
                if (isPickingRoute == false && isPickingNode == false) {
                    if (inputManager.isCursorVisible() == false) {
                        inputManager.setCursorVisible(true);
                        flyCam.setEnabled(false);
                    } else {
                        inputManager.setCursorVisible(false);
                        flyCam.setEnabled(true);
                    }
                } else {
                    CollisionResults results = new CollisionResults();
                    Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                    collideHost.collideWith(ray, results);
                    //System.out.println("----- Collisions? " + results.size() + "-----");
                    Vector3f collisionPosition = new Vector3f();
                    System.out.println(results.size());
                    for (int i = 0; i < results.size(); i++) {
                        float dist = results.getCollision(i).getDistance();
                        Vector3f pt = results.getCollision(i).getContactPoint();
                        String hit = results.getCollision(i).getGeometry().getName();
//                        System.out.println("* Collision #" + i);
//                        System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                        collisionPosition = pt;
                    }
                    boolean isValidCollition = false;
                    Grid outputGrid = new Grid(0, 0, 0, 0, 0, 0, "");
                    for (int i = 0; i < mainFParent.allData.grid.length; i++) {
                        for (int j = 0; j < mainFParent.allData.grid[0].length; j++) {
//                            System.out.println("max x val: "+((myParent.allData.grid[i][j].max_x_val-myParent.allData.myScale.center_x)*myParent.allData.myScale.scale));
//                            System.out.println("min x val: "+((myParent.allData.grid[i][j].min_x_val-myParent.allData.myScale.center_x)*myParent.allData.myScale.scale));
//                            System.out.println("max y val: "+((myParent.allData.grid[i][j].max_y_val-myParent.allData.myScale.center_y)*myParent.allData.myScale.scale));
//                            System.out.println("min y val: "+((myParent.allData.grid[i][j].min_y_val-myParent.allData.myScale.center_y)*myParent.allData.myScale.scale));
                            float max_x = (float) ((mainFParent.allData.grid[i][j].max_x_val - (mainFParent.allData.myScale.center_x + mainFParent.allData.myScale.x_position)) * mainFParent.allData.myScale.scale);
                            float min_x = (float) ((mainFParent.allData.grid[i][j].min_x_val - (mainFParent.allData.myScale.center_x + mainFParent.allData.myScale.x_position)) * mainFParent.allData.myScale.scale);
                            float max_y = (float) ((mainFParent.allData.grid[i][j].max_y_val - (mainFParent.allData.myScale.center_y + mainFParent.allData.myScale.y_position)) * mainFParent.allData.myScale.scale);
                            float min_y = (float) ((mainFParent.allData.grid[i][j].min_y_val - (mainFParent.allData.myScale.center_y + mainFParent.allData.myScale.y_position)) * mainFParent.allData.myScale.scale);
//                            System.out.println("max x: "+max_x);
//                            System.out.println("min x: "+min_x);
//                            System.out.println("max y: "+max_y);
//                            System.out.println("min y: "+min_y);
                            if (collisionPosition.y < max_x && collisionPosition.y > min_x && collisionPosition.x < max_y && collisionPosition.x > min_y) {
                                isValidCollition = true;
//                                System.out.println("grid x: "+i);
//                                System.out.println("grid y: "+j);
                                outputGrid = mainFParent.allData.grid[i][j];
                                break;
                            }
                        }
                        if (isValidCollition == true) {
                            break;
                        }
                    }
                    LocationNode nearestNode;
                    if (isValidCollition == true) {
                        double leastDistance = Double.POSITIVE_INFINITY;
                        nearestNode = outputGrid.myNodes[0];
                        for (int i = 0; i < outputGrid.myNodes.length; i++) {
                            //System.out.println(outputGrid.myNodes[i]);//WARNING, NULL POINTER SPOTTED, A GRID HAS A NULL LOCATIONNODE
                            if (outputGrid.myNodes[i] != null) {
                                double dist = Math.sqrt(Math.pow(collisionPosition.x - outputGrid.myNodes[i].renderingLocation.x, 2) + Math.pow(collisionPosition.y - outputGrid.myNodes[i].renderingLocation.y, 2));
                                if (dist < leastDistance) {
                                    nearestNode = outputGrid.myNodes[i];
                                    leastDistance = dist;
                                }
                            }
                        }
                        Box b = new Box(0.1f, 0.1f, 1f);
                        Geometry choiceGeom = new Geometry("Choice", b);
                        choiceGeom.setLocalTranslation(nearestNode.renderingLocation);
                        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mat.setColor("Color", ColorRGBA.Yellow);
                        choiceGeom.setMaterial(mat);
                        rootNode.attachChild(choiceGeom);
                        if (isOriginSet == false) {
                            startNode = nearestNode;
                            isOriginSet = true;
                            if (isPickingNode == true) {
                                mainFParent.constructingNumericLayerController.passedNode = startNode;
                                startNode = null;
                                endNode = null;
                                removeCrosshair();
                                isPickingNode = false;
                                isPickingRoute = false;
                                isOriginSet = false;
                                mainFParent.constructingNumericLayerController.fillPassedData();
                            }
                        } else if (isPickingNode == false) {
                            endNode = nearestNode;
                            isDestinationSet = true;
                            int trafficLayerIndex = -1;
                            for (int i = 0; i < mainFParent.allData.all_Layers.size(); i++) {
                                if (((LayerDefinition) mainFParent.allData.all_Layers.get(i)).layerName.toLowerCase().contains("traffic")) {
                                    trafficLayerIndex = i;
                                }
                            }
                            mainFParent.allData.setParallelLayers(1, -1);
                            mainFParent.routing = new Routing(mainFParent.allData, trafficLayerIndex, 0);
                            System.out.println("Start node id: "+startNode.id);
                            System.out.println("End node id: "+endNode.id);
                            mainFParent.routing.findPath(startNode, endNode);
                            //System.out.println(mainFParent.routing.pathDistance);
                            if (mainFParent.routing.path == null) {
                                System.out.println("NO PATH FOUND!");
                                return;
                            }
                            Box[] all_path_box = new Box[mainFParent.routing.path.size()];
                            Geometry[] all_path_geom = new Geometry[mainFParent.routing.path.size()];
                            //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                            mat.setColor("Color", ColorRGBA.Red);
                            for (int i = 0; i < mainFParent.routing.path.size(); i++) {
                                all_path_box[i] = new Box(0.1f, 0.1f, 0.1f);
                                all_path_geom[i] = new Geometry("PATH", all_path_box[i]);
                                all_path_geom[i].setMaterial(mat);
                                all_path_geom[i].setLocalTranslation(((NeighborNode) mainFParent.routing.path.get(i)).myNode.renderingLocation);
                                rootNode.attachChild(all_path_geom[i]);
                            }
                            for (int i = 0; i < mainFParent.routing.path.size(); i++) {
                                for (int j = 0; j < ((NeighborNode) mainFParent.routing.path.get(i)).myNode.myWays.length; j++) {
                                    int index = mainFParent.routing.detectNodeIndex(((NeighborNode) mainFParent.routing.path.get(i)).myNode.id, ((NeighborNode) mainFParent.routing.path.get(i)).myNode.myWays[j]);
                                    ((NeighborNode) mainFParent.routing.path.get(i)).myNode.myWays[j].color[index * 3 + 0] = 1;
                                    ((NeighborNode) mainFParent.routing.path.get(i)).myNode.myWays[j].color[index * 3 + 1] = 1f;
                                    ((NeighborNode) mainFParent.routing.path.get(i)).myNode.myWays[j].color[index * 3 + 2] = 1;
                                }
                            }
                            startNode = null;
                            endNode = null;
                            removeCrosshair();
                            isPickingNode = false;
                            isPickingRoute = false;
                            isDestinationSet = false;
                            isOriginSet = false;
                            isRefreshing = true;
                        }
                    } else {
                        System.out.println("Not in map selection!");
                    }
                }
            } else if (name.equals("focusAlt") && keyPressed == false) {
                if (inputManager.isCursorVisible() == false) {
                    inputManager.setCursorVisible(true);
                    flyCam.setEnabled(false);
                } else {
                    inputManager.setCursorVisible(false);
                    flyCam.setEnabled(true);
                }
            }
        }
    };

    public void plotLine() {
        //renderer.cleanup();
        //rootNode.detachChildNamed("line");
        int isDeleted = 1;
        while (isDeleted > -1) {
            isDeleted = rootNode.detachChildNamed("line");
        }

        if (isBatchRendering == false) {
            Mesh[] all_meshes = new Mesh[mainFParent.allData.all_Ways.length];
            Geometry all_geo[] = new Geometry[mainFParent.allData.all_Ways.length];
            int[][] indexes = new int[mainFParent.allData.all_Ways.length][];
            for (int i = 0; i < all_meshes.length; i++) {
                indexes[i] = new int[mainFParent.allData.all_Ways[i].myNodes.length * 2];
                for (int j = 0; j < mainFParent.allData.all_Ways[i].myNodes.length - 1; j++) {
                    indexes[i][2 * j] = (j);
                    indexes[i][2 * j + 1] = (j + 1);
                }
                all_meshes[i] = new Mesh();
                all_meshes[i].setMode(Mesh.Mode.Lines);
                all_meshes[i].setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(mainFParent.allData.all_Ways[i].lines));
                all_meshes[i].setBuffer(VertexBuffer.Type.Index, 2, indexes[i]);
                all_meshes[i].updateBound();
                all_meshes[i].updateCounts();
                all_geo[i] = new Geometry("line", all_meshes[i]);
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setBoolean("VertexColor", true);
                all_meshes[i].setBuffer(VertexBuffer.Type.Color, 3, mainFParent.allData.all_Ways[i].color);
                all_geo[i].setMaterial(mat);
                rootNode.attachChild(all_geo[i]);
            }
        } else {
            Geometry bachedGeom;
            ArrayList<Vector3f> allVerticesArrayList = new ArrayList();
            ArrayList indicesArrayList = new ArrayList();
            ArrayList colorsArrayList = new ArrayList();

            int counter = 0;
            for (int i = 0; i < mainFParent.allData.all_Ways.length; i++) {
                if (mainFParent.allData.all_Ways[i].myNodes.length > 0) {
                    allVerticesArrayList.add(new Vector3f(0, 0, -20));
                    colorsArrayList.add(0f);
                    colorsArrayList.add(0f);
                    colorsArrayList.add(0f);
                    indicesArrayList.add(counter);
                    indicesArrayList.add(counter + 1);
                    counter = counter + 1;

                    try {
                        if(mainFParent.allData.all_Ways[i].myNodes[0].renderingLocation==null){
                            mainFParent.allData.all_Ways[i].myNodes[0].renderingLocation=mainFParent.allData.all_Ways[i].lines[0];
//                            for(int i=0;i<)
                        }
                        allVerticesArrayList.add(new Vector3f(mainFParent.allData.all_Ways[i].myNodes[0].renderingLocation.x, mainFParent.allData.all_Ways[i].myNodes[0].renderingLocation.y, mainFParent.allData.all_Ways[i].myNodes[0].renderingLocation.z - 0.5f));
                    } catch (Exception ex) {
                        System.out.println("PROBLEM MAKING VERTEX TAIL INWARD!");
                    }

                    colorsArrayList.add(0f);
                    colorsArrayList.add(0f);
                    colorsArrayList.add(0f);
                    indicesArrayList.add(counter);
                    indicesArrayList.add(counter + 1);
                    counter = counter + 1;

                    for (int j = 0; j < mainFParent.allData.all_Ways[i].myNodes.length; j++) {
                        allVerticesArrayList.add(mainFParent.allData.all_Ways[i].myNodes[j].renderingLocation);
                        colorsArrayList.add(mainFParent.allData.all_Ways[i].color[3 * j + 0]);
                        colorsArrayList.add(mainFParent.allData.all_Ways[i].color[3 * j + 1]);
                        colorsArrayList.add(mainFParent.allData.all_Ways[i].color[3 * j + 2]);
                    }
                    for (int j = 0; j < mainFParent.allData.all_Ways[i].myNodes.length - 1; j++) {
                        indicesArrayList.add(counter);
                        indicesArrayList.add(counter + 1);
                        counter = counter + 1;
                    }
                    int lastIndex = mainFParent.allData.all_Ways[i].myNodes.length - 1;
                    try {
                        allVerticesArrayList.add(new Vector3f(mainFParent.allData.all_Ways[i].myNodes[lastIndex].renderingLocation.x, mainFParent.allData.all_Ways[i].myNodes[lastIndex].renderingLocation.y, mainFParent.allData.all_Ways[i].myNodes[lastIndex].renderingLocation.z - 0.5f));
                    } catch (Exception ex) {
                        System.out.println();
                    }

                    colorsArrayList.add(0f);
                    colorsArrayList.add(0f);
                    colorsArrayList.add(0f);
                    indicesArrayList.add(counter);
                    indicesArrayList.add(counter + 1);
                    counter = counter + 1;
                }
            }

            Vector3f vertices[] = new Vector3f[allVerticesArrayList.size()];
            for (int i = 0; i < allVerticesArrayList.size(); i++) {
                vertices[i] = allVerticesArrayList.get(i);
            }

            int indices[] = new int[indicesArrayList.size()];
            for (int i = 0; i < indicesArrayList.size(); i++) {
                indices[i] = (int) indicesArrayList.get(i);
            }

            float colors[] = new float[colorsArrayList.size()];
            for (int i = 0; i < colorsArrayList.size(); i++) {
                colors[i] = (float) colorsArrayList.get(i);
            }

            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setBoolean("VertexColor", true);
            bachedMesh = new Mesh();

            bachedMesh.setMode(Mesh.Mode.Lines);
            bachedMesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));

            bachedMesh.setBuffer(VertexBuffer.Type.Index, 2, indices);
            bachedMesh.updateBound();
            bachedMesh.updateCounts();
            bachedMesh.setBuffer(VertexBuffer.Type.Color, 3, colors);
            bachedGeom = new Geometry("line", bachedMesh);
            bachedGeom.setMaterial(mat);

            rootNode.attachChild(bachedGeom);
        }
    }

    public void plotGrids() {
//        renderer.cleanup();
        Mesh[][] grid_meshes = new Mesh[mainFParent.allData.grid.length][mainFParent.allData.grid[0].length];
        Geometry grid_geo[][] = new Geometry[mainFParent.allData.grid.length][mainFParent.allData.grid[0].length];
        for (int i = 0; i < grid_meshes.length; i++) {
            for (int j = 0; j < grid_meshes[i].length; j++) {
                Vector3f[] lines = new Vector3f[5];
                lines[0] = new Vector3f((float) ((mainFParent.allData.grid[i][j].min_y_val - (mainFParent.allData.myScale.center_y + mainFParent.allData.myScale.y_position)) * mainFParent.allData.myScale.scale), (float) ((mainFParent.allData.grid[i][j].min_x_val - (mainFParent.allData.myScale.center_x + mainFParent.allData.myScale.x_position)) * mainFParent.allData.myScale.scale), 0f);
                lines[1] = new Vector3f((float) ((mainFParent.allData.grid[i][j].min_y_val - (mainFParent.allData.myScale.center_y + mainFParent.allData.myScale.y_position)) * mainFParent.allData.myScale.scale), (float) ((mainFParent.allData.grid[i][j].max_x_val - (mainFParent.allData.myScale.center_x + mainFParent.allData.myScale.x_position)) * mainFParent.allData.myScale.scale), 0f);
                lines[2] = new Vector3f((float) ((mainFParent.allData.grid[i][j].max_y_val - (mainFParent.allData.myScale.center_y + mainFParent.allData.myScale.y_position)) * mainFParent.allData.myScale.scale), (float) ((mainFParent.allData.grid[i][j].max_x_val - (mainFParent.allData.myScale.center_x + mainFParent.allData.myScale.x_position)) * mainFParent.allData.myScale.scale), 0f);
                lines[3] = new Vector3f((float) ((mainFParent.allData.grid[i][j].max_y_val - (mainFParent.allData.myScale.center_y + mainFParent.allData.myScale.y_position)) * mainFParent.allData.myScale.scale), (float) ((mainFParent.allData.grid[i][j].min_x_val - (mainFParent.allData.myScale.center_x + mainFParent.allData.myScale.x_position)) * mainFParent.allData.myScale.scale), 0f);
                lines[4] = new Vector3f((float) ((mainFParent.allData.grid[i][j].min_y_val - (mainFParent.allData.myScale.center_y + mainFParent.allData.myScale.y_position)) * mainFParent.allData.myScale.scale), (float) ((mainFParent.allData.grid[i][j].min_x_val - (mainFParent.allData.myScale.center_x + mainFParent.allData.myScale.x_position)) * mainFParent.allData.myScale.scale), 0f);
                float[] color = new float[3 * lines.length];
                for (int h = 0; h < color.length; h++) {
                    color[h] = 1f;
                }
                int[] indexes = new int[2 * lines.length];
                for (int g = 0; g < lines.length - 1; g++) {

                    indexes[2 * g] = (g);
                    indexes[2 * g + 1] = (g + 1);
                }
                grid_meshes[i][j] = new Mesh();
                grid_meshes[i][j].setMode(Mesh.Mode.Lines);
                grid_meshes[i][j].setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(lines));
                grid_meshes[i][j].setBuffer(VertexBuffer.Type.Index, 2, indexes);
                grid_meshes[i][j].updateBound();
                grid_meshes[i][j].updateCounts();
                grid_meshes[i][j].setBuffer(VertexBuffer.Type.Color, 3, color);
                grid_geo[i][j] = new Geometry("grid", grid_meshes[i][j]);
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setBoolean("VertexColor", true);
                grid_geo[i][j].setMaterial(mat);
                rootNode.attachChild(grid_geo[i][j]);
            }
        }
    }

    public void refresh_color() {
//        renderer.cleanup();
        this.enqueue(new Callable() {
            public Object call() throws Exception {
                // call methods that modify the scene graph here
                bachedMesh.clearBuffer(VertexBuffer.Type.Color);
                ArrayList colorsArrayList = new ArrayList();
                for (int i = 0; i < mainFParent.allData.all_Ways.length; i++) {
                    for (int j = 0; j < mainFParent.allData.all_Ways[i].myNodes.length; j++) {
                        colorsArrayList.add(0f);
                        colorsArrayList.add(0f);
                        colorsArrayList.add(0f);

                        colorsArrayList.add(0f);
                        colorsArrayList.add(0f);
                        colorsArrayList.add(0f);

                        colorsArrayList.add(mainFParent.allData.all_Ways[i].color[3 * j + 0]);
                        colorsArrayList.add(mainFParent.allData.all_Ways[i].color[3 * j + 1]);
                        colorsArrayList.add(mainFParent.allData.all_Ways[i].color[3 * j + 2]);

                        colorsArrayList.add(0f);
                        colorsArrayList.add(0f);
                        colorsArrayList.add(0f);
                    }
                }
                float colors[] = new float[colorsArrayList.size()];
                for (int i = 0; i < colorsArrayList.size(); i++) {
                    colors[i] = (float) colorsArrayList.get(i);
                }

                bachedMesh.setBuffer(VertexBuffer.Type.Color, 3, colors);
                return null;
            }
        });
    }

    public void refresh_lava(FacilityLocation testFacilities[], boolean isLavaBased) {
        //System.out.println("number of lava in renderer: "+myParent.lavaBuffer.size());
        int output = rootNode.detachChildNamed("LAVA");
        while (output != -1) {
            output = rootNode.detachChildNamed("LAVA");
        }

        if (isLavaBased == true) {//LAVA ON FLOW CONTROL
            Box[] all_lava_box = new Box[mainFParent.flowControl.lavaBuffer.size()];
            Geometry[] all_lava_geom = new Geometry[mainFParent.flowControl.lavaBuffer.size()];
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Red);

//            if (valueText != null) {
//                for (int i = 0; i < valueText.length; i++) {
//                    rootNode.detachChild(valueText[i]);
//                }
//            }
//            valueText = new BitmapText[mainFParent.flowControl.lavaBuffer.size()];
            for (int i = 0; i < mainFParent.flowControl.lavaBuffer.size(); i++) {
                all_lava_box[i] = new Box(0.01f, 0.01f, 0.01f);
                all_lava_geom[i] = new Geometry("LAVA", all_lava_box[i]);
                all_lava_geom[i].setMaterial(mat);
                all_lava_geom[i].setLocalTranslation(((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentWay.lines[((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentIndex]);
                rootNode.attachChild(all_lava_geom[i]);

//                valueText[i] = new BitmapText(guiFont, false);
//                valueText[i].setSize(0.2f);
//                valueText[i].setColor(ColorRGBA.Red);
//                valueText[i].setText(String.valueOf(((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).fuel));
//                valueText[i].setLocalTranslation(((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentWay.lines[((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentIndex].x, ((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentWay.lines[((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentIndex].y, ((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentWay.lines[((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentIndex].z + 1 + i * 0.4f);
//                rootNode.attachChild(valueText[i]);
            }
        } else if (testFacilities != null) {
            for (int j = 0; j < testFacilities.length; j++) {
                Box[] all_lava_box = new Box[testFacilities[j].lavaBuffer.size()];
                Geometry[] all_lava_geom = new Geometry[testFacilities[j].lavaBuffer.size()];
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", new ColorRGBA((float) testFacilities[j].color.getRed() / 255f, (float) testFacilities[j].color.getGreen() / 255f, (float) testFacilities[j].color.getBlue() / 255f, 1f));
//        if (valueText != null) {
//            for (int i = 0; i < valueText.length; i++) {
//                rootNode.detachChild(valueText[i]);
//            }
//        }
//
//        valueText = new BitmapText[mainFParent.flowControl.lavaBuffer.size()];
//
                for (int i = 0; i < testFacilities[j].lavaBuffer.size(); i++) {
//            valueText[i] = new BitmapText(guiFont, false);
                    all_lava_box[i] = new Box(0.01f, 0.01f, 0.01f);
                    all_lava_geom[i] = new Geometry("LAVA", all_lava_box[i]);
                    all_lava_geom[i].setMaterial(mat);
                    all_lava_geom[i].setLocalTranslation(((Lava_parallel) testFacilities[j].lavaBuffer.get(i)).currentWay.lines[((Lava_parallel) testFacilities[j].lavaBuffer.get(i)).currentIndex]);
                    rootNode.attachChild(all_lava_geom[i]);
//
//            valueText[i].setSize(0.2f);
//            valueText[i].setColor(ColorRGBA.Red);
//            valueText[i].setText(String.valueOf(((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).fuel));
//            valueText[i].setLocalTranslation(((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentWay.lines[((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentIndex].x, ((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentWay.lines[((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentIndex].y, ((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentWay.lines[((Lava_parallel) mainFParent.flowControl.lavaBuffer.get(i)).currentIndex].z + 1 + i * 0.4f);
//            rootNode.attachChild(valueText[i]);
                }
            }
        }

    }

    @Override
    public void simpleInitApp() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        inputManager.addMapping("focus", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "focus");
        inputManager.addMapping("focusAlt", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "focusAlt");
        Box b = new Box((float) ((mainFParent.allData.myScale.max_y - mainFParent.allData.myScale.min_y) * mainFParent.allData.myScale.scale), (float) ((mainFParent.allData.myScale.max_x - mainFParent.allData.myScale.min_x) * mainFParent.allData.myScale.scale), 0.1f);
        plan = new Geometry("Plan", b);
        plan.setLocalTranslation(0, 0, -0.5f);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0, 0, 1, 0.2f));
        mat.setTransparent(true);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        plan.setQueueBucket(RenderQueue.Bucket.Translucent);
        plan.setMaterial(mat);
        collideHost.attachChild(plan);
        initCrosshair();
        if (mainFParent.allData != null) {
            plotLine();
            plotGrids();
        }
        flyCam.setMoveSpeed(5);
    }

    public void showCrosshair() {
        guiNode.attachChild(crossHair);
        inputManager.setCursorVisible(false);
        flyCam.setEnabled(true);
    }

    public void removeCrosshair() {
        guiNode.detachChildNamed("crosshair");
        inputManager.setCursorVisible(true);
        flyCam.setEnabled(false);
    }

    public void initCrosshair() {
        rootNode.attachChild(collideHost);

        crossHair = new Picture("crosshair");
        crossHair.setImage(assetManager, "Textures/crosshair.png", true);
        float scale = ((float) Math.min(mySettings.getWidth(), mySettings.getHeight()) / 10f);
        crossHair.setWidth(scale);
        crossHair.setHeight(scale);

        crossHair.setPosition(((float) mySettings.getWidth() / 2f) - (scale / 2f), ((float) mySettings.getHeight() / 2f) - (scale / 2f));

    }

    public void resizeCrossHair() {
        float scale = ((float) Math.min(mySettings.getWidth(), mySettings.getHeight()) / 10f);
        crossHair.setWidth(Math.min(mySettings.getWidth(), mySettings.getHeight()) / 10);
        crossHair.setHeight(Math.min(mySettings.getWidth(), mySettings.getHeight()) / 10);
        crossHair.setPosition(((float) mySettings.getWidth() / 2f) - (scale / 2f), ((float) mySettings.getHeight() / 2f) - (scale / 2f));
    }

    public void refreshApp() {
        rootNode.detachAllChildren();
        if (mainFParent.allData != null) {
            if (isPickingRoute == true) {
                rootNode.attachChild(collideHost);
            }
            plotLine();
            plotGrids();
        }
    }

    public void headquarter(Vector3f passed_position, double capacity, String type) {
        Box b = new Box(0.1f, 0.1f, (float) capacity / (float) 2);
        Geometry geom = new Geometry(type, b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        if ("center".equals(type)) {
            mat.setColor("Color", ColorRGBA.Green);
        } else if ("candidate".equals(type)) {
            mat.setColor("Color", ColorRGBA.Yellow);
        }

        geom.setMaterial(mat);
        geom.setLocalTranslation(passed_position.x, passed_position.y, ((float) capacity / (float) 2));
        rootNode.attachChild(geom);
    }

    public void removeAllHeadquarters() {
        int output = rootNode.detachChildNamed("center");
        while (output != -1) {
            output = rootNode.detachChildNamed("center");
        }
        output = rootNode.detachChildNamed("candidate");
        while (output != -1) {
            output = rootNode.detachChildNamed("candidate");
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        //System.out.println(isRefreshing);
        if (isRefreshing) {
//            refresh_color();
            plotLine();
            if (isIterateDebug) {
//                refresh_lava();
            }
            isRefreshing = false;
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
