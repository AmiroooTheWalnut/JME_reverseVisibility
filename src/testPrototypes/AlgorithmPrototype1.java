package testPrototypes;

import static com.github.davidmoten.rtree.Entries.entry;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.internal.EntryDefault;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import com.jme3.util.Screenshots;
import esmaieeli.gisFastLocationOptimization.GIS3D.AllData;
import esmaieeli.gisFastLocationOptimization.GIS3D.PreProcessor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import jme3tools.converters.ImageToAwt;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

/**
 *
 * @author usero
 */
public class AlgorithmPrototype1 extends SimpleApplication {

    boolean isDebugging = false;

    int imageQuality = 64;

    PreProcessor preProcessor = new PreProcessor();
    AllData allData;

    Node shootables;
    Node selectables;
    Node debugNode;
    Node skyable;
    Node shadowables;
    ViewPort offView;

    float maxX = -10000;
    float minX = 10000;
    float maxY = -10000;
    float minY = 10000;
    float maxZ = -10000;
    float maxXRaw = -10000;
    float minXRaw = 10000;
    float maxYRaw = -10000;
    float minYRaw = 10000;
    float maxZRaw = -10000;
    float zMargin = 2;
    boolean isLatLon = true;
    ArrayList<CustomMesh> myMeshes = new ArrayList();
    ArrayList<StreetSegment> segments = new ArrayList();
    int lastProcessedSegment = 0;
    int debugShadowTimerIndex = 0;
    float debugShadowTimer = 0;
    float debugShadowInterval = 2;
    boolean isRunDebugShadows = true;
    boolean isRunningDebugShadows = false;
    boolean isRunningSegmentShadows = false;
    boolean isRunAlgorithm = false;
//    enum state{};
    boolean isRunning = false;
    Material debugShadowMaterial;
    float heightScaling = 1.7f;

    MeshSegmentShadowHolder mss = new MeshSegmentShadowHolder();
    RTree<com.jme3.scene.Geometry, com.github.davidmoten.rtree.geometry.Geometry> rtree;

//    OffScreenIntersectionProcessor currentIntersectionProcessor;
//    boolean isIntersectingGPU = false;
//    boolean isWaitingForIntersection = false;
    ArrayList<Frontier> frontiers = new ArrayList();

    public static void main(String[] args) {
        AlgorithmPrototype1 app = new AlgorithmPrototype1();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        allData = preProcessor.preprocess("assets/Scenes/tucson_large.json", 10, 10, 6);
//        flyCam.setEnabled(false);
        flyCam.setMoveSpeed(20);
        cam.setLocation(new Vector3f(100.67f, 0.175f, 25.987f));
        initCrossHairs();
        shootables = new Node("Shootables");
        selectables = new Node("Selectables");
        shadowables = new Node("Shadowables");
        debugNode = new Node("DebugNode");
        skyable = new Node("Skyable");
        rootNode.attachChild(shootables);
        rootNode.attachChild(selectables);
        rootNode.attachChild(debugNode);
        skyable.attachChild(shadowables);
        rootNode.attachChild(skyable);
//        isLatLon = false;
//        triangulateGeoJSON("assets/Scenes/debug.geojson");
        isLatLon = true;
        triangulateGeoJSON("assets/Scenes/tucson_large.geojson");
        addBuildings();
        fixStartEndLatOriented();
        //cutLonStartEndSorted(0.0f);

//        minXRaw = minX;
//        maxXRaw = maxX;
//        minYRaw = minY;
//        maxYRaw = maxY;
//        maxZRaw = maxZ;
        minX = minX * 2.5f;
        maxX = maxX * 2.5f;
        minY = minY * 2.5f;
        maxY = maxY * 2.5f;

        createBounds();

        connectSegNodes();
        connectSegments();

//        mergeStartEnds();
        //Collections.sort(segments, new StreetSegment());
        initKeys();
        //polygonToTextureVisible();
        polygonToTextureVisibleDebug();
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (isRunningSegmentShadows == true) {
            if (lastProcessedSegment < segments.size() - 1) {
//                if (lastProcessedSegment < 10) {
                if (lastProcessedSegment > 0) {
                    if (segments.get(lastProcessedSegment - 1).osptpp.completed == true) {
                        segments.get(lastProcessedSegment).osptpp.texture = new Texture2D(imageQuality, imageQuality, Image.Format.BGRA8);
                        segments.get(lastProcessedSegment).osptpp.texture.setMinFilter(Texture.MinFilter.Trilinear);
                        segments.get(lastProcessedSegment).osptpp.texture.setMagFilter(Texture.MagFilter.Bilinear);
                        segments.get(lastProcessedSegment).osptpp.myFb = new FrameBuffer(imageQuality, imageQuality, 1);
                        segments.get(lastProcessedSegment).osptpp.myFb.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
                        segments.get(lastProcessedSegment).osptpp.myFb.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(segments.get(lastProcessedSegment).osptpp.texture));

                        offView.setOutputFrameBuffer(segments.get(lastProcessedSegment).osptpp.myFb);
                        offView.clearProcessors();
                        offView.addProcessor(segments.get(lastProcessedSegment).osptpp);
                        debugNode.detachAllChildren();
                        shadowables.detachAllChildren();
                        shootForAllBuildings(lastProcessedSegment, ColorRGBA.Black);
                        System.out.println(lastProcessedSegment);
                        System.out.println((float) lastProcessedSegment / segments.size());
                        lastProcessedSegment = lastProcessedSegment + 1;
                    }
                } else {
                    segments.get(lastProcessedSegment).osptpp.texture = new Texture2D(imageQuality, imageQuality, Image.Format.BGRA8);
                    segments.get(lastProcessedSegment).osptpp.texture.setMinFilter(Texture.MinFilter.Trilinear);
                    segments.get(lastProcessedSegment).osptpp.texture.setMagFilter(Texture.MagFilter.Bilinear);
                    segments.get(lastProcessedSegment).osptpp.myFb = new FrameBuffer(imageQuality, imageQuality, 1);
                    segments.get(lastProcessedSegment).osptpp.myFb.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
                    segments.get(lastProcessedSegment).osptpp.myFb.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(segments.get(lastProcessedSegment).osptpp.texture));

                    offView.setOutputFrameBuffer(segments.get(lastProcessedSegment).osptpp.myFb);
                    offView.clearProcessors();
                    offView.addProcessor(segments.get(lastProcessedSegment).osptpp);
                    debugNode.detachAllChildren();
                    shadowables.detachAllChildren();
                    shootForAllBuildings(lastProcessedSegment, ColorRGBA.Black);
                    System.out.println(lastProcessedSegment);
                    System.out.println((float) lastProcessedSegment / segments.size());
                    lastProcessedSegment = lastProcessedSegment + 1;
                }

            } else {
                isRunningSegmentShadows = false;
                if (isRunDebugShadows == true) {
                    isRunningDebugShadows = true;
                    debugShadowTimerIndex = 0;
                    debugShadowTimer = 0;
                }
            }

//            if(lastProcessedSegment<segments.size()-1){
//                offView.clearProcessors();
//                offView.addProcessor(segments.get(lastProcessedSegment).osptpp);
//                lastProcessedSegment=lastProcessedSegment+1;
//                System.out.println(lastProcessedSegment);
//            }else{
//                isRunningSegmentShadows=false;
//            }
//            for (int i = lastProcessedSegment; i < segments.size(); i++) {
//                offView.addProcessor(segments.get(i).osptpp);
//            }
        } else {
//            if (isWaitingForIntersection == true) {
//                if (currentIntersectionProcessor != null) {
//                    if (currentIntersectionProcessor.completed == true) {
//                        ByteBuffer buf = ByteBuffer.allocateDirect(1 * 1 * 4); // RGBA = 4 bytes per pixel
//                        renderer.readFrameBuffer(currentIntersectionProcessor.myFb, buf); // Copy from GPU to CPU
//                        isIntersectingGPU = OffScreenIntersectionProcessor.isIntersect(buf);
//                    }
//                } else {
//                    System.out.println("WAITING FOR INTERSECTION BUT PROCESSOR IS NULL!!!");
//                }
//            }
            if (isRunningDebugShadows == true) {
//                System.out.println(debugShadowTimerIndex);
                if (debugShadowTimerIndex > segments.size() - 1) {
                    //if (debugShadowTimerIndex > 10) {
                    debugShadowTimerIndex = 0;
                }
                debugShadowMaterial.setTexture("ColorMap", segments.get(debugShadowTimerIndex).osptpp.texture);

                ByteBuffer buf = ByteBuffer.allocateDirect(imageQuality * imageQuality * 4); // RGBA = 4 bytes per pixel
                renderer.readFrameBuffer(segments.get(debugShadowTimerIndex).osptpp.myFb, buf); // Copy from GPU to CPU
                ArrayList<ArrayList<ColorPixel>> res2DImg = OffScreenPolyToPixelProcessor.convertTo2DImage(buf, imageQuality, imageQuality);
                segments.get(debugShadowTimerIndex).osptpp.cpuImg = res2DImg;

//                BufferedImage image = new BufferedImage(imageQuality, imageQuality, BufferedImage.TYPE_4BYTE_ABGR);
//                //Screenshots.convertScreenShot(buf, image);
//                OffScreenPolyToPixelProcessor.convertScreenShot(buf, image);
//                try {
//                    ImageIO.write(image, "png", new File("test"));
//                    System.out.println("Saved texture/framebuffer to " + "test");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Image img = segments.get(debugShadowTimerIndex).osptpp.texture.getImage();
//                int width = img.getWidth();
//                int height = img.getHeight();
//                ByteBuffer buf = img.getData(0);
//
//                BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
//                Screenshots.convertScreenShot(buf, bufferedImage);
//
//                try {
//                    ImageIO.write(bufferedImage, "png", new File("test"));
//                    System.out.println("Saved texture to " + "test");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Image myImg = segments.get(debugShadowTimerIndex).osptpp.texture.getImage();
//                //segments.get(debugShadowTimerIndex).osptpp
//                BufferedImage myBI = ImageToAwt.convert(myImg, false, true, 0);
//                //Image img = new Image(Image.Format.RGBA8, imageQuality, imageQuality, null);
//                //ByteBuffer cpuBuffer = BufferUtils.createByteBuffer(imageQuality * imageQuality * 4);
//
//                //renderManager.getRenderer().readFrameBuffer(segments.get(debugShadowTimerIndex).osptpp.myFb, cpuBuffer);
//                //img.setData(cpuBuffer);
//
//                //BufferedImage image = new BufferedImage(imageQuality, imageQuality, BufferedImage.TYPE_INT_ARGB);
//                //Screenshots.convertScreenShot(cpuBuffer, image);
//
//                try {
//                    ImageIO.write(myBI, "png", new File("testImg"));
//                    System.out.println("Saved screenshot to " + "testImg");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                debugShadowTimer = debugShadowTimer + tpf;
                if (debugShadowTimer > debugShadowInterval) {
                    debugShadowTimer = 0;
                    debugShadowTimerIndex = debugShadowTimerIndex + 1;
                }
            }
        }
    }

    /**
     * Declaring the "Shoot" action and mapping to its triggers.
     */
    private void initKeys() {
        inputManager.addMapping("Start",
                new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(runAlgListener, "Start");
        inputManager.addMapping("SelectStreetPoint",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(selectListener, "SelectStreetPoint");
    }

    final private ActionListener runAlgListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (keyPressed == true) {
                System.out.println(name);
                runMainAlgorithm();
            }
        }
    };

    public void runMainAlgorithm() {
        renderAllSegments();
        constructFrontier0();
    }

    public void constructFrontier0() {
        setInitialSegments(0.1f);

        for (int i = 0; i < segments.size(); i++) {
            if (segments.get(i).isInitial == true) {
                segments.get(i).start.beta.texture = segments.get(i).osptpp.texture;
                segments.get(i).start.isChecked = true;
                for (int m = 0; m < segments.get(i).start.duplicates.size(); m++) {
                    segments.get(i).start.duplicates.get(m).isChecked = true;
                }
//                if (segments.get(i).start.duplicates != null) {
//                    
//                }
                segments.get(i).end.beta.texture = segments.get(i).osptpp.texture;
                segments.get(i).end.isChecked = true;
                for (int m = 0; m < segments.get(i).end.duplicates.size(); m++) {
                    segments.get(i).end.duplicates.get(m).isChecked = true;
                }
            }
        }
        for (int i = 0; i < segments.size(); i++) {
            ArrayList<StreetSegment> connectingCheckedSegments = new ArrayList();
            if (segments.get(i).start.rawLat < segments.get(i).end.rawLat) {
                if (segments.get(i).start.isChecked == false) {

                    for (int m = 0; m < segments.get(i).start.parentOtherFromStart.size(); m++) {
                        if (segments.get(i).start.parentOtherFromStart.get(m).end.isChecked == true) {
                            connectingCheckedSegments.add(segments.get(i).start.parentOtherFromStart.get(m));
                        }
                    }
                    for (int m = 0; m < segments.get(i).start.parentOtherFromEnd.size(); m++) {
                        if (segments.get(i).start.parentOtherFromEnd.get(m).start.isChecked == true) {
                            connectingCheckedSegments.add(segments.get(i).start.parentOtherFromEnd.get(m));
                        }
                    }
                }
            } else {
                if (segments.get(i).end.isChecked == false) {
//                    ArrayList<StreetSegment> connectingCheckedSegments=new ArrayList();
                    for (int m = 0; m < segments.get(i).end.parentOtherFromStart.size(); m++) {
                        if (segments.get(i).end.parentOtherFromStart.get(m).end.isChecked == true) {
                            connectingCheckedSegments.add(segments.get(i).end.parentOtherFromStart.get(m));
                        }
                    }
                    for (int m = 0; m < segments.get(i).end.parentOtherFromEnd.size(); m++) {
                        if (segments.get(i).end.parentOtherFromEnd.get(m).start.isChecked == true) {
                            connectingCheckedSegments.add(segments.get(i).start.parentOtherFromEnd.get(m));
                        }
                    }
                }
            }
            if (connectingCheckedSegments.size() > 0) {

                System.out.println("!!!");
            }
        }

//        sweepNodes();
//        for (int i = 0; i < segments.size(); i++) {
//            for (int m = 0; m < imageQuality; m++) {
//                for (int n = 0; n < imageQuality; n++) {
//                    int index = (m - 1) * imageQuality + n;
//                    segments.get(i).osptpp.texture.getImage().getData(0).get(index);
//                }
//            }
    

    ////            if()
//        }
    }
    
    public void setInitialSegments(float percentage) {
        float range = maxXRaw - minXRaw;
        for (int i = 0; i < segments.size(); i++) {
            double minLat = Math.min(segments.get(i).start.rawLat, segments.get(i).end.rawLat);
            if (minLat < minXRaw + range * percentage) {
                if (segments.get(i).isInitial == false) {
                    segments.get(i).isInitial = true;
                    Sphere sphere = new Sphere(10, 10, 0.3f + i * 0.001f);
                    Geometry markT = new Geometry("DebugSweep", sphere);
                    Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mark_mat.setColor("Color", ColorRGBA.Red);
                    markT.setMaterial(mark_mat);
                    markT.setLocalTranslation(segments.get(i).start.location);
                    rootNode.attachChild(markT);
                }
            }
        }
    }

    public void sweepNodes() {
        for (int i = 0; i < segments.size(); i++) {
            if (segments.get(i).start.rawLat < segments.get(i).end.rawLat) {
                if (segments.get(i).start.isChecked == false && segments.get(i).start.duplicates.get(0) == null) {
                    segments.get(i).start.beta.texture = segments.get(i).osptpp.texture;
                    segments.get(i).start.isChecked = true;
                    segments.get(i).end.beta.texture = segments.get(i).osptpp.texture;
                    segments.get(i).end.isChecked = true;
                }
            }
        }
        //Collections.sort(segments);
//        for (int i = 0; i < 500; i++) {

    

    ////        for (int i = 0; i < segments.size(); i++) {
//            Sphere sphere = new Sphere(10, 10, 0.3f+i*0.001f);
//            Geometry markT = new Geometry("DebugSweep", sphere);
//            Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//            mark_mat.setColor("Color", ColorRGBA.Red);
//            markT.setMaterial(mark_mat);
//            markT.setLocalTranslation(segments.get(i).start.location);
//            rootNode.attachChild(markT);
//        }
    }

    public void connectSegments() {
        for (int i = 0; i < segments.size(); i++) {
            for (int j = 0; j < segments.size(); j++) {
                if (i != j) {
                    if (segments.get(i).start.locationNode.id == segments.get(j).start.locationNode.id) {
                        segments.get(i).startIntersectingSegments.add(segments.get(j).start);
                        segments.get(i).start.parentOtherFromStart.add(segments.get(j));
                        segments.get(i).start.duplicates.add(segments.get(j).start);
                        segments.get(j).start.duplicates.add(segments.get(i).start);
                    }
                    if (segments.get(i).start.locationNode.id == segments.get(j).end.locationNode.id) {
                        segments.get(i).startIntersectingSegments.add(segments.get(j).end);
                        segments.get(i).start.parentOtherFromEnd.add(segments.get(j));
                        segments.get(i).start.duplicates.add(segments.get(j).end);
                        segments.get(j).end.duplicates.add(segments.get(i).start);
                    }
                    if (segments.get(i).end.locationNode.id == segments.get(j).start.locationNode.id) {
                        segments.get(i).endIntersectingSegments.add(segments.get(j).start);
                        segments.get(i).end.parentOtherFromStart.add(segments.get(j));
                        segments.get(i).end.duplicates.add(segments.get(j).start);
                        segments.get(j).start.duplicates.add(segments.get(i).end);
                    }
                    if (segments.get(i).end.locationNode.id == segments.get(j).end.locationNode.id) {
                        segments.get(i).endIntersectingSegments.add(segments.get(j).end);
                        segments.get(i).end.parentOtherFromEnd.add(segments.get(j));
                        segments.get(i).end.duplicates.add(segments.get(j).end);
                        segments.get(j).end.duplicates.add(segments.get(i).end);
                    }
                }
            }
        }
    }

    public void renderAllSegments() {
        lastProcessedSegment = 0;
        Camera offCamera = new Camera(imageQuality, imageQuality);
        offCamera.setParallelProjection(true);
        offCamera.setFrustum(0.1f, 2000, minX, maxX, maxY, minY);

        offView = renderManager.createPreView("Offscreen View", offCamera);
        offView.setClearFlags(true, true, true);
        offView.setBackgroundColor(ColorRGBA.DarkGray);

        //FrameBuffer offBuffer = new FrameBuffer(imageQuality, imageQuality, 1);
        offCamera.setLocation(new Vector3f(0f, 0f, 0f));
        offCamera.lookAt(offCamera.getLocation().add(new Vector3f(0f, 1f, 0f)), new Vector3f(0, 0f, 1f));

        //Texture2D offTex = new Texture2D(imageQuality, imageQuality, Image.Format.RGBA8);
        //offTex.setMinFilter(Texture.MinFilter.Trilinear);
        //offTex.setMagFilter(Texture.MagFilter.Bilinear);
        offView.attachScene(skyable);

        isRunningSegmentShadows = true;
//        for(int i=0;i<segments.size();i++){
//            offView.addProcessor(segments.get(i).osptpp);
//        }

//        offBuffer.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
//        offBuffer.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(offTex));
//        offView.setOutputFrameBuffer(offBuffer);
    }

    final private ActionListener selectListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (keyPressed == true) {
                System.out.println(name);
                System.out.println(cam.getLocation());
                debugNode.detachAllChildren();
                shadowables.detachAllChildren();
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                selectables.collideWith(ray, results);
                for (int i = 0; i < selectables.getQuantity(); i++) {
                    Geometry ch = (Geometry) (selectables.getChild(i));
                    ch.getMaterial().setColor("Color", ColorRGBA.Yellow);
                }
                if (results.size() > 0) {
                    CollisionResult closest = results.getClosestCollision();
                    String hitName = results.getClosestCollision().getGeometry().getName();
                    System.out.println(hitName);
                    Geometry selObj = (Geometry) (selectables.getChild(hitName));
                    selObj.getMaterial().setColor("Color", ColorRGBA.Red);
                    int segmentID = Integer.valueOf(hitName.split("_")[1]);
                    System.out.println(segmentID);
                    shootForAllBuildings(segmentID, ColorRGBA.Black);
                }
            }
        }
    };

    public void shootForAllBuildings(int segmentIDX, ColorRGBA color) {
        //HashMap<Integer, ShadowPolygon> buildingsMap = new HashMap();

        //Now for the start of the segment
        Vector3f viewerLoc = segments.get(segmentIDX).start.location;
        viewerLoc.y = viewerLoc.y + 0.9f;
        searchingNode = null;
        int depthStart = findSmallestNodeViewerIn(shootables, viewerLoc, 0, -1);
        if (searchingNode == null) {
            System.out.println("SEVERE ERROR: VIEWER NODE NOT FOUND!!!");
        }
        Node searchingNodeStart = searchingNode;
        findBuildingShootingSelfNode(searchingNodeStart, viewerLoc, segments.get(segmentIDX).end.location);
        viewerLoc = segments.get(segmentIDX).end.location;
        viewerLoc.y = viewerLoc.y + 0.9f;
        searchingNode = null;
        int depthEnd = findSmallestNodeViewerIn(shootables, viewerLoc, 0, -1);
        if (searchingNode == null) {
            System.out.println("SEVERE ERROR: VIEWER NODE NOT FOUND!!!");
        }
        Node searchingNodeEnd = searchingNode;
        findBuildingShootingSelfNode(searchingNodeEnd, viewerLoc, segments.get(segmentIDX).end.location);
        
        
        //mss.data.put(segmentIDX, buildingsMap);

    }

    public void shootForBuildingsSameNode(Node nodeStart, Vector3f origin) {

        List<Spatial> children = nodeStart.getChildren();

    }

    public void shootForAllBuildingsOld(int segmentIDX, ColorRGBA color) {
        HashMap<Integer, ShadowPolygon> buildingsMap = new HashMap();
        for (int i = 0; i < myMeshes.size(); i++) {
            shootForBuildingOld(buildingsMap, i, segmentIDX, color, -0.01f);
        }
        mss.data.put(segmentIDX, buildingsMap);
    }

    public void shootForBuildingOld(HashMap<Integer, ShadowPolygon> buildingsMap, int buildingIDX, int segmentIDX, ColorRGBA color, float offset) {
        ShadowPolygon shadowPolygon = new ShadowPolygon();
        Vector3f origin = segments.get(segmentIDX).start.location;
        CustomMesh m2 = myMeshes.get(buildingIDX);
        for (int c = 0; c < m2.quads.size(); c++) {
            boolean isOnePointHitSky = false;
            for (int r = 0; r < 2; r++) {
                CollisionResults results = new CollisionResults();
                //markP.setLocalTranslation(m2.quads.get(c).tops[0]);
                Vector3f top = new Vector3f(m2.quads.get(c).tops[r].x, m2.quads.get(c).tops[r].y, m2.quads.get(c).tops[r].z);
                top.y = top.y + 0.01f;
                if (isDebugging == true) {
                    Sphere sphere = new Sphere(10, 10, 0.2f);
                    Geometry markT = new Geometry("DebugMarker", sphere);
                    Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mark_mat.setColor("Color", ColorRGBA.Yellow);
                    markT.setMaterial(mark_mat);
                    markT.setLocalTranslation(top);
                    //System.out.println(m2.quads.get(c).tops[0]);
                    debugNode.attachChild(markT);
                }
                Ray ray = new Ray(origin, top.subtract(origin));
                shootables.collideWith(ray, results);
                if (results.size() > 0) {
                    //CollisionResult closest = results.getClosestCollision();
                    String hitName = results.getClosestCollision().getGeometry().getName();
                    if (hitName.equals("meshSky")) {
                        isOnePointHitSky = true;
                        handleAHit(shadowPolygon, results, ray, -1);
                    } else if (isOnePointHitSky == true) {
                        for (int m = 0; m < results.size(); m++) {
                            String geomName = results.getCollision(m).getGeometry().getName();
                            if (geomName.equals("meshSky")) {
                                handleAHit(shadowPolygon, results, ray, m);
                            }
                        }
                    }
                }
            }
            isOnePointHitSky = false;
            for (int r = 1; r > -1; r--) {
                CollisionResults results = new CollisionResults();
                //markP.setLocalTranslation(m2.quads.get(c).tops[0]);
                Vector3f top = new Vector3f(m2.quads.get(c).tops[r].x, m2.quads.get(c).tops[r].y, m2.quads.get(c).tops[r].z);
                top.y = top.y + 0.01f;
                Ray ray = new Ray(origin, top.subtract(origin));
                shootables.collideWith(ray, results);
                if (results.size() > 0) {
                    //CollisionResult closest = results.getClosestCollision();
                    String hitName = results.getClosestCollision().getGeometry().getName();
                    if (hitName.equals("meshSky")) {
                        isOnePointHitSky = true;
                        handleAHit(shadowPolygon, results, ray, -1);
                    } else if (isOnePointHitSky == true) {
                        for (int m = 0; m < results.size(); m++) {
                            String geomName = results.getCollision(m).getGeometry().getName();
                            if (geomName.equals("meshSky")) {
                                handleAHit(shadowPolygon, results, ray, m);
                            }
                        }
                    }
                }
            }
        }
        ArrayList<Vector3f> corners = new ArrayList();
        corners.add(new Vector3f(0.999f * maxX, maxZ + zMargin * maxZ, 0.999f * maxY));
        corners.add(new Vector3f(0.999f * minX, maxZ + zMargin * maxZ, 0.999f * maxY));
        corners.add(new Vector3f(0.999f * minX, maxZ + zMargin * maxZ, 0.999f * minY));
        corners.add(new Vector3f(0.999f * maxX, maxZ + zMargin * maxZ, 0.999f * minY));
        shootToCorners(shadowPolygon, origin, corners, buildingIDX);

        // GO FOR THE SECOND POINT IN THE STREET
        Vector3f origin2 = segments.get(segmentIDX).end.location;
        for (int c = 0; c < m2.quads.size(); c++) {
            boolean isOnePointHitSky = false;
            for (int r = 0; r < 2; r++) {
                CollisionResults results = new CollisionResults();
                Vector3f top = new Vector3f(m2.quads.get(c).tops[r].x, m2.quads.get(c).tops[r].y, m2.quads.get(c).tops[r].z);
                top.y = top.y + 0.1f;
                Ray ray = new Ray(origin2, top.subtract(origin2));
                shootables.collideWith(ray, results);

                if (results.size() > 0) {
                    //CollisionResult closest = results.getClosestCollision();
                    String hitName = results.getClosestCollision().getGeometry().getName();
                    if (hitName.equals("meshSky")) {
                        isOnePointHitSky = true;
                        handleAHit(shadowPolygon, results, ray, -1);
                    } else if (isOnePointHitSky == true) {
                        for (int m = 0; m < results.size(); m++) {
                            String geomName = results.getCollision(m).getGeometry().getName();
                            if (geomName.equals("meshSky")) {
                                handleAHit(shadowPolygon, results, ray, m);
                            }
                        }
                    }
                }
            }

            isOnePointHitSky = false;
            for (int r = 1; r > -1; r--) {
                CollisionResults results = new CollisionResults();
                Vector3f top = new Vector3f(m2.quads.get(c).tops[r].x, m2.quads.get(c).tops[r].y, m2.quads.get(c).tops[r].z);
                top.y = top.y + 0.1f;
                Ray ray = new Ray(origin2, top.subtract(origin2));
                shootables.collideWith(ray, results);

                if (results.size() > 0) {
                    //CollisionResult closest = results.getClosestCollision();
                    String hitName = results.getClosestCollision().getGeometry().getName();
                    if (hitName.equals("meshSky")) {
                        isOnePointHitSky = true;
                        handleAHit(shadowPolygon, results, ray, -1);
                    } else if (isOnePointHitSky == true) {
                        for (int m = 0; m < results.size(); m++) {
                            String geomName = results.getCollision(m).getGeometry().getName();
                            if (geomName.equals("meshSky")) {
                                handleAHit(shadowPolygon, results, ray, m);
                            }
                        }
                    }
                }
            }
        }
        shootToCorners(shadowPolygon, origin2, corners, buildingIDX);
        //ArrayList<Vector3f> debugConvexHull = calcConvexHull(segments.get(segmentIDX).shadowPolygon, offset);
        ArrayList<Vector3f> debugConvexHull = calcConvexHull(shadowPolygon, offset);
        if (debugConvexHull.size() > 2) {
            debugDrawShadow(debugConvexHull, color);
        }
    }

    public void shootToCorners(ShadowPolygon shadowPolygon, Vector3f origin, ArrayList<Vector3f> corners, int buildingIDX) {
        for (int co = 0; co < corners.size(); co++) {
            CollisionResults results = new CollisionResults();
            Vector3f top = new Vector3f(corners.get(co).x, corners.get(co).y, corners.get(co).z);
            top.y = top.y + 0.1f;
            Ray ray = new Ray(origin, top.subtract(origin));
            shootables.collideWith(ray, results);
            if (results.size() > 0) {
                String hitName = results.getClosestCollision().getGeometry().getName();
                //System.out.println(results.getClosestCollision().getGeometry().getName());
                if (hitName.equals("Building_" + buildingIDX)) {
                    Sphere sphereS = new Sphere(10, 10, 0.2f);
                    Geometry markS = new Geometry("DebugMarker", sphereS);
                    Material mark_matS = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mark_matS.setColor("Color", ColorRGBA.Orange);
                    markS.setMaterial(mark_matS);
                    markS.setLocalTranslation(corners.get(co));
                    //System.out.println(m2.quads.get(c).tops[0]);
                    //segments.get(segmentIDX).shadowPolygon.points.add(corners.get(co));
                    shadowPolygon.points.add(corners.get(co));
                    debugNode.attachChild(markS);

                    //Shoot another ray to hit the boundary of sky***
                    Ray ray_p = new Ray();
                    ray_p.origin = ray.origin;
                    ray_p.direction = ray.direction;
                    ray_p.direction.y = 0.1f;
                    CollisionResults results_p = new CollisionResults();
                    shootables.collideWith(ray_p, results_p);
                }
            }
        }
    }

    public ArrayList<Vector3f> calcConvexHull(ShadowPolygon input, float offset) {
        ArrayList<Vector3f> output = new ArrayList();
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 0);

        Coordinate[] coords = new Coordinate[input.points.size()];
        for (int i = 0; i < input.points.size(); i++) {
            coords[i] = new Coordinate();
            coords[i].x = input.points.get(i).x;
            coords[i].y = input.points.get(i).z;
            coords[i].z = 0;
        }
        org.locationtech.jts.geom.Geometry points = geometryFactory.createMultiPointFromCoords(coords);

        ConvexHull convexHull = new ConvexHull(points);
        org.locationtech.jts.geom.Geometry hullGeometry = convexHull.getConvexHull();
        Coordinate[] outputCoords = hullGeometry.getCoordinates();
        for (int i = 0; i < outputCoords.length; i++) {
            output.add(new Vector3f((float) (outputCoords[i].x), maxZ + zMargin * maxZ + offset, (float) (outputCoords[i].y)));
        }

        return output;
    }

    public void triangulateGeoJSON(String fileName) {
        String content;
        try {
            rtree = RTree.star().maxChildren(10).create();
            content = new String(Files.readAllBytes(Paths.get(fileName)));
            JSONObject jsonObject = new JSONObject(content);

            JSONArray geoms = (JSONArray) (jsonObject.get("features"));
            float avgX = 0;
            float avgY = 0;
            int counterT = 0;
            for (int i = 0; i < geoms.length(); i++) {
                JSONObject geom = (JSONObject) geoms.get(i);
                JSONObject geomProp = (JSONObject) geom.get("properties");
                JSONObject geom1 = (JSONObject) geom.get("geometry");
                JSONArray geom2 = (JSONArray) geom1.get("coordinates");
                if (geomProp.isNull("highway") == false) {
                    if (!geomProp.get("highway").equals("footway")) {
                        if (geom1.get("type").equals("Polygon")) {
                            for (int p = 0; p < 1; p++) {
                                JSONArray poly1 = (JSONArray) geom2.get(p);
                                for (int c = 0; c < poly1.length() - 1; c++) {
                                    JSONArray coords = (JSONArray) poly1.get(c);
                                    float x = (coords.getFloat(0));
                                    float y = (coords.getFloat(1));
                                    avgX = avgX + x;
                                    avgY = avgY + y;
                                    counterT = counterT + 1;
                                }
                            }
                        } else if (geom1.get("type").equals("LineString")) {
                            for (int c = 0; c < geom2.length(); c++) {
                                JSONArray coords = (JSONArray) geom2.get(c);
                                float x = (coords.getFloat(0));
                                float y = (coords.getFloat(1));
                                avgX = avgX + x;
                                avgY = avgY + y;
                                counterT = counterT + 1;
                            }
                        }
                    }

                } else if (geomProp.isNull("building") == false) {
                    for (int p = 0; p < 1; p++) {
                        JSONArray poly1 = (JSONArray) geom2.get(p);
                        for (int c = 0; c < poly1.length() - 1; c++) {
                            JSONArray coords = (JSONArray) poly1.get(c);
                            float x = (coords.getFloat(0));
                            float y = (coords.getFloat(1));
                            avgX = avgX + x;
                            avgY = avgY + y;
                            counterT = counterT + 1;
                        }
                    }
                }

            }
            avgX = avgX / counterT;
            avgY = avgY / counterT;

            float XOffset = -avgX;
            float YOffset = -avgY;
            float scale;
            if (isLatLon == true) {
                scale = 10000;
            } else {
                scale = 1;
            }
            int buildingCounter = 0;
            int segmentCounter = 0;

            for (int i = 0; i < geoms.length(); i++) {
                float height = 4;
                int numLevels = -1;
                JSONObject geom = (JSONObject) geoms.get(i);
                JSONObject geom1 = (JSONObject) geom.get("geometry");
                JSONArray geom2 = (JSONArray) geom1.get("coordinates");
                JSONObject geomProp = (JSONObject) geom.get("properties");
                if (geomProp.isNull("highway") == false) {
                    if (!geomProp.get("highway").equals("footway")) {
                        if (geom1.get("type").equals("Polygon")) {
                            for (int p = 0; p < 1; p++) {
                                JSONArray poly1 = (JSONArray) geom2.get(p);
                                //Mesh mesh = new Mesh();
                                //vertices = new Vector3f[(poly1.length() - 1) * 2];
                                //int[] indices = new int[(poly1.length() - 1) * 6];
                                for (int c = 1; c < poly1.length(); c++) {
                                    JSONArray coords = (JSONArray) poly1.get(c);
                                    float rawX = coords.getFloat(0);
                                    float rawY = coords.getFloat(1);
                                    float x = (coords.getFloat(0) + XOffset) * scale;
                                    float y = (coords.getFloat(1) + YOffset) * scale;
                                    if (x > maxX) {
                                        maxX = x;
                                    }
                                    if (x < minX) {
                                        minX = x;
                                    }
                                    if (y > maxY) {
                                        maxY = y;
                                    }
                                    if (y < minY) {
                                        minY = y;
                                    }

                                    if (rawX > maxXRaw) {
                                        maxXRaw = rawX;
                                    }
                                    if (rawX < minXRaw) {
                                        minXRaw = rawX;
                                    }
                                    if (rawY > maxYRaw) {
                                        maxYRaw = rawY;
                                    }
                                    if (rawY < minYRaw) {
                                        minYRaw = rawY;
                                    }

                                    JSONArray coordsP = (JSONArray) poly1.get(c - 1);
                                    float rawXP = coordsP.getFloat(0);
                                    float rawYP = coordsP.getFloat(1);
                                    float xP = (coordsP.getFloat(0) + XOffset) * scale;
                                    float yP = (coordsP.getFloat(1) + YOffset) * scale;
                                    if (xP > maxX) {
                                        maxX = xP;
                                    }
                                    if (xP < minX) {
                                        minX = xP;
                                    }
                                    if (yP > maxY) {
                                        maxY = yP;
                                    }
                                    if (yP < minY) {
                                        minY = yP;
                                    }

                                    if (rawXP > maxXRaw) {
                                        maxXRaw = rawXP;
                                    }
                                    if (rawXP < minXRaw) {
                                        minXRaw = rawXP;
                                    }
                                    if (rawYP > maxYRaw) {
                                        maxYRaw = rawYP;
                                    }
                                    if (rawYP < minYRaw) {
                                        minYRaw = rawYP;
                                    }

                                    Vector3f start = new Vector3f(xP, 0, yP);
                                    Vector3f end = new Vector3f(x, 0, y);
                                    Line line = new Line(start, end);
                                    StreetSegment sg = new StreetSegment();
                                    sg.start.location = start;
                                    sg.end.location = end;
                                    sg.start.rawLat = coordsP.getDouble(0);
                                    sg.start.rawLon = coordsP.getDouble(1);
                                    sg.start.isStart = true;
                                    sg.end.rawLat = coords.getDouble(0);
                                    sg.end.rawLon = coords.getDouble(1);
                                    sg.streetID = Long.parseLong(((String) (geomProp.get("@id"))).split("way/")[1]);
                                    segments.add(sg);
                                    Geometry lineGeom = new Geometry("line", line);
                                    Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                                    mat2.setColor("Color", ColorRGBA.Red);  // Set the color
                                    lineGeom.setMaterial(mat2);
                                    line.setLineWidth(2);  // Only visible in some contexts (e.g., OpenGL core profile)
                                    rootNode.attachChild(lineGeom);
                                    Sphere sphereS = new Sphere(5, 5, 0.2f);
                                    Geometry markS = new Geometry("StreetStart_" + segmentCounter, sphereS);
                                    Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                                    mark_mat.setColor("Color", ColorRGBA.Yellow);
                                    markS.setMaterial(mark_mat);
                                    markS.setLocalTranslation(start);
                                    //System.out.println(m2.quads.get(c).tops[0]);
                                    selectables.attachChild(markS);
                                    Sphere sphereE = new Sphere(5, 5, 0.2f);
                                    Geometry markE = new Geometry("StreetEnd_" + segmentCounter, sphereE);
                                    markE.setMaterial(mark_mat);
                                    markE.setLocalTranslation(end);
                                    //System.out.println(m2.quads.get(c).tops[0]);
                                    selectables.attachChild(markE);
                                    segmentCounter = segmentCounter + 1;
                                }
                            }
                        } else if (geom1.get("type").equals("LineString")) {
                            for (int c = 1; c < geom2.length(); c++) {
                                JSONArray coords = (JSONArray) geom2.get(c);
                                float rawX = coords.getFloat(0);
                                float rawY = coords.getFloat(1);
                                float x = (coords.getFloat(0) + XOffset) * scale;
                                float y = (coords.getFloat(1) + YOffset) * scale;
                                if (x > maxX) {
                                    maxX = x;
                                }
                                if (x < minX) {
                                    minX = x;
                                }
                                if (y > maxY) {
                                    maxY = y;
                                }
                                if (y < minY) {
                                    minY = y;
                                }

                                if (rawX > maxXRaw) {
                                    maxXRaw = rawX;
                                }
                                if (rawX < minXRaw) {
                                    minXRaw = rawX;
                                }
                                if (rawY > maxYRaw) {
                                    maxYRaw = rawY;
                                }
                                if (rawY < minYRaw) {
                                    minYRaw = rawY;
                                }

                                JSONArray coordsP = (JSONArray) geom2.get(c - 1);
                                float rawXP = coordsP.getFloat(0);
                                float rawYP = coordsP.getFloat(1);
                                float xP = (coordsP.getFloat(0) + XOffset) * scale;
                                float yP = (coordsP.getFloat(1) + YOffset) * scale;
                                if (xP > maxX) {
                                    maxX = xP;
                                }
                                if (xP < minX) {
                                    minX = xP;
                                }
                                if (yP > maxY) {
                                    maxY = yP;
                                }
                                if (yP < minY) {
                                    minY = yP;
                                }

                                if (rawXP > maxXRaw) {
                                    maxXRaw = rawXP;
                                }
                                if (rawXP < minXRaw) {
                                    minXRaw = rawXP;
                                }
                                if (rawYP > maxYRaw) {
                                    maxYRaw = rawYP;
                                }
                                if (rawYP < minYRaw) {
                                    minYRaw = rawYP;
                                }

                                Vector3f start = new Vector3f(xP, 0, yP);
                                Vector3f end = new Vector3f(x, 0, y);
                                Line line = new Line(start, end);
                                StreetSegment sg = new StreetSegment();
                                sg.start.location = start;
                                sg.end.location = end;
                                sg.start.rawLat = coordsP.getDouble(0);
                                sg.start.rawLon = coordsP.getDouble(1);
                                sg.start.isStart = true;
                                sg.end.rawLat = coords.getDouble(0);
                                sg.end.rawLon = coords.getDouble(1);
                                sg.streetID = Long.parseLong(((String) (geomProp.get("@id"))).split("way/")[1]);
                                segments.add(sg);
                                Geometry lineGeom = new Geometry("line", line);
                                Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                                mat2.setColor("Color", ColorRGBA.Red);  // Set the color
                                lineGeom.setMaterial(mat2);
                                line.setLineWidth(2);  // Only visible in some contexts (e.g., OpenGL core profile)
                                rootNode.attachChild(lineGeom);
                                Sphere sphereS = new Sphere(5, 5, 0.2f);
                                Geometry markS = new Geometry("StreetStart_" + segmentCounter, sphereS);
                                Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                                mark_mat.setColor("Color", ColorRGBA.Yellow);
                                markS.setMaterial(mark_mat);
                                markS.setLocalTranslation(start);
                                //System.out.println(m2.quads.get(c).tops[0]);
                                selectables.attachChild(markS);
                                if (c == geom2.length() - 1) {
                                    Sphere sphereE = new Sphere(5, 5, 0.2f);
                                    Geometry markE = new Geometry("StreetEnd_" + segmentCounter, sphereE);
                                    Material mark_matE = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                                    mark_matE.setColor("Color", ColorRGBA.Yellow);
                                    markE.setMaterial(mark_matE);
                                    markE.setLocalTranslation(end);
                                    //System.out.println(m2.quads.get(c).tops[0]);
                                    selectables.attachChild(markE);
                                }
                                segmentCounter = segmentCounter + 1;
                            }
                        }
                    }
                } else if (geomProp.isNull("building") == false) {
                    if (geomProp.isNull("building:levels") == false) {
                        numLevels = geomProp.getInt("building:levels");
                    }
                    if (geomProp.isNull("height") == false) {
                        String h = geomProp.getString("height");
                        h = h.replaceAll("[^\\d.]", "");
                        height = Float.parseFloat(h);
//                    height=geomProp.getFloat("height");
                    } else if (numLevels > -1) {
                        height = 4 * numLevels;
                    }
                    height = height * heightScaling;
                    if (height > maxZ) {
                        maxZ = height;
                    }
//                for(int p=0;p<geom2.length();p++){
                    for (int p = 0; p < 1; p++) {
                        JSONArray poly1 = (JSONArray) geom2.get(p);
                        Mesh mesh = new Mesh();
                        CustomMesh tempMesh = new CustomMesh();
                        Vector3f[] vertices = new Vector3f[(poly1.length() - 1) * 2];
                        int[] indices = new int[(poly1.length() - 1) * 6];
                        for (int c = 0; c < poly1.length() - 1; c++) {
                            JSONArray coords = (JSONArray) poly1.get(c);
                            float x = (coords.getFloat(0) + XOffset) * scale;
                            float y = (coords.getFloat(1) + YOffset) * scale;
                            if (x > maxX) {
                                maxX = x;
                            }
                            if (x < minX) {
                                minX = x;
                            }
                            if (y > maxY) {
                                maxY = y;
                            }
                            if (y < minY) {
                                minY = y;
                            }

                            vertices[2 * c] = new Vector3f(x, 0, y);
                            vertices[2 * c + 1] = new Vector3f(x, height, y);
                        }
                        int counter = 0;
                        for (int c = 1; c < poly1.length(); c++) {
                            Quad quad = new Quad();
                            if (c == poly1.length() - 1) {
                                indices[6 * counter] = (c - 1) * 2;
                                indices[6 * counter + 1] = (c - 1) * 2 + 1;
                                indices[6 * counter + 2] = (0) * 2;

                                indices[6 * counter + 3] = (c - 1) * 2 + 1;
                                indices[6 * counter + 4] = (0) * 2 + 1;
                                indices[6 * counter + 5] = (0) * 2;

                                quad.tops[0] = vertices[(c - 1) * 2 + 1];
                                quad.tops[1] = vertices[(0) * 2 + 1];
                                quad.bottoms[0] = vertices[(c - 1) * 2];
                                quad.bottoms[1] = vertices[(0) * 2];
                                tempMesh.quads.add(quad);
                            } else {
                                indices[6 * counter] = (c - 1) * 2;
                                indices[6 * counter + 1] = (c - 1) * 2 + 1;
                                indices[6 * counter + 2] = (c) * 2;

                                indices[6 * counter + 3] = (c - 1) * 2 + 1;
                                indices[6 * counter + 4] = (c) * 2 + 1;
                                indices[6 * counter + 5] = (c) * 2;

                                quad.tops[0] = vertices[(c - 1) * 2 + 1];
                                quad.tops[1] = vertices[(c) * 2 + 1];
                                quad.bottoms[0] = vertices[(c - 1) * 2];
                                quad.bottoms[1] = vertices[(c) * 2];
                                tempMesh.quads.add(quad);
                            }
                            counter = counter + 1;
                        }
                        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
                        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
                        mesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
                        mesh.setBound(new BoundingBox());
                        mesh.updateBound();
                        tempMesh.mesh = mesh;
                        myMeshes.add(tempMesh);
                        Geometry geo = new Geometry("Building_" + buildingCounter, mesh); // using our custom mesh object
                        Material mat = new Material(assetManager,
                                "Common/MatDefs/Misc/Unshaded.j3md");
                        mat.setColor("Color", ColorRGBA.Brown);
                        //mat.setBoolean("VertexColor", true);
                        geo.setMaterial(mat);
                        //geo.setUserData("ID", );
                        //shootables.attachChild(geo);
                        //shootables.setModelBound(new BoundingBox());
                        //shootables.updateModelBound();
                        Vector3f point = geo.getWorldBound().getCenter();
                        rtree = rtree.add(entry(geo, (com.github.davidmoten.rtree.geometry.Geometry) Geometries.point(point.x, point.z)));
                        buildingCounter = buildingCounter + 1;
//                    System.out.println("!!!");
                    }
                }
//                System.out.println("!!!");
            }

//            System.out.println("!!!");
        } catch (IOException ex) {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createBounds() {
        Mesh meshN = new Mesh();
        Vector3f[] northVertices = new Vector3f[4];
        int[] indexesN = {2, 0, 1, 1, 3, 2};
        northVertices[0] = new Vector3f(minX, 0, minY);
        northVertices[1] = new Vector3f(maxX, 0, minY);
        northVertices[2] = new Vector3f(minX, maxZ + zMargin * maxZ, minY);
        northVertices[3] = new Vector3f(maxX, maxZ + zMargin * maxZ, minY);

        meshN.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(northVertices));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        meshN.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indexesN));
        meshN.updateBound();

        Geometry geoN = new Geometry("meshN", meshN); // using our custom mesh object
        Material matN = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matN.setColor("Color", ColorRGBA.Orange);
        //mat.setBoolean("VertexColor", true);
        geoN.setMaterial(matN);
        shootables.attachChild(geoN);

        Mesh meshS = new Mesh();
        Vector3f[] southVertices = new Vector3f[4];
        int[] indexesS = {2, 1, 0, 1, 2, 3};
        southVertices[0] = new Vector3f(minX, 0, maxY);
        southVertices[1] = new Vector3f(maxX, 0, maxY);
        southVertices[2] = new Vector3f(minX, maxZ + zMargin * maxZ, maxY);
        southVertices[3] = new Vector3f(maxX, maxZ + zMargin * maxZ, maxY);

        meshS.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(southVertices));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        meshS.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indexesS));
        meshS.updateBound();

        Geometry geoS = new Geometry("meshS", meshS); // using our custom mesh object
        Material matS = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matS.setColor("Color", ColorRGBA.Green);
        //mat.setBoolean("VertexColor", true);
        geoS.setMaterial(matS);
        shootables.attachChild(geoS);

        Mesh meshSky = new Mesh();
        Vector3f[] skyVertices = new Vector3f[4];
        int[] indexesSky = {2, 0, 1, 1, 3, 2};
        skyVertices[0] = new Vector3f(minX, maxZ + zMargin * maxZ, minY);
        skyVertices[1] = new Vector3f(maxX, maxZ + zMargin * maxZ, minY);
        skyVertices[2] = new Vector3f(minX, maxZ + zMargin * maxZ, maxY);
        skyVertices[3] = new Vector3f(maxX, maxZ + zMargin * maxZ, maxY);

        meshSky.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(skyVertices));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        meshSky.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indexesSky));
        meshSky.updateBound();

        Geometry geoSky = new Geometry("meshSky", meshSky); // using our custom mesh object
        Material matSky = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matSky.setColor("Color", ColorRGBA.Blue);
        //mat.setBoolean("VertexColor", true);
        geoSky.setMaterial(matSky);
        shootables.attachChild(geoSky);

        Mesh meshE = new Mesh();
        Vector3f[] eastVertices = new Vector3f[4];
        int[] indexesE = {2, 1, 0, 1, 2, 3};
        eastVertices[0] = new Vector3f(minX, 0, minY);
        eastVertices[1] = new Vector3f(minX, 0, maxY);
        eastVertices[2] = new Vector3f(minX, maxZ + zMargin * maxZ, minY);
        eastVertices[3] = new Vector3f(minX, maxZ + zMargin * maxZ, maxY);

        meshE.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(eastVertices));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        meshE.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indexesE));
        meshE.updateBound();

        Geometry geoE = new Geometry("meshE", meshE); // using our custom mesh object
        Material matE = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matE.setColor("Color", ColorRGBA.Cyan);
        //mat.setBoolean("VertexColor", true);
        geoE.setMaterial(matE);
        shootables.attachChild(geoE);

        Mesh meshW = new Mesh();
        Vector3f[] westVertices = new Vector3f[4];
        int[] indexesW = {2, 0, 1, 1, 3, 2};
        westVertices[0] = new Vector3f(maxX, 0, minY);
        westVertices[1] = new Vector3f(maxX, 0, maxY);
        westVertices[2] = new Vector3f(maxX, maxZ + zMargin * maxZ, minY);
        westVertices[3] = new Vector3f(maxX, maxZ + zMargin * maxZ, maxY);

        meshW.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(westVertices));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        meshW.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indexesW));
        meshW.updateBound();

        Geometry geoW = new Geometry("meshW", meshW); // using our custom mesh object
        Material matW = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matW.setColor("Color", ColorRGBA.DarkGray);
        //mat.setBoolean("VertexColor", true);
        geoW.setMaterial(matW);
        shootables.attachChild(geoW);

        Mesh meshSkyD = new Mesh();
        Vector3f[] skyVerticesD = new Vector3f[4];
        int[] indexesSkyD = {2, 0, 1, 1, 3, 2};
        skyVerticesD[0] = new Vector3f(minX, maxZ + zMargin * maxZ, minY);
        skyVerticesD[1] = new Vector3f(maxX, maxZ + zMargin * maxZ, minY);
        skyVerticesD[2] = new Vector3f(minX, maxZ + zMargin * maxZ, maxY);
        skyVerticesD[3] = new Vector3f(maxX, maxZ + zMargin * maxZ, maxY);

        meshSkyD.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(skyVerticesD));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        meshSkyD.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indexesSkyD));
        meshSkyD.updateBound();

        Geometry geoSkyD = new Geometry("meshSkyD", meshSkyD); // using our custom mesh object
        Material matSkyD = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matSkyD.setColor("Color", ColorRGBA.Blue);
        //mat.setBoolean("VertexColor", true);
        geoSkyD.setMaterial(matSkyD);
        skyable.attachChild(geoSkyD);
    }

    public void debugDrawShadow(ArrayList<Vector3f> debugConvexHull, ColorRGBA color) {
        Vector3f verticesLocal[] = new Vector3f[debugConvexHull.size()];
        for (int i = 0; i < verticesLocal.length; i++) {
            verticesLocal[i] = debugConvexHull.get(i);
            verticesLocal[i].y = verticesLocal[i].y - 0.01f;
        }
        int[] indices = new int[(debugConvexHull.size() - 2) * 3];
        int counter = 0;
        for (int i = 2; i < debugConvexHull.size(); i++) {
            indices[counter] = 0;
            counter = counter + 1;
            indices[counter] = i;
            counter = counter + 1;
            indices[counter] = i - 1;
            counter = counter + 1;
        }
        Mesh mesh = new Mesh();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(verticesLocal));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        mesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        mesh.updateBound();

        Geometry geo = new Geometry("OurMesh", mesh);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");

        mat.setColor("Color", color);
        geo.setMaterial(mat);
        shadowables.attachChild(geo);
    }

    private void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
                settings.getWidth() / 2 - ch.getLineWidth() / 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }

    public void polygonToTextureVisible() {
        Camera offCamera = new Camera(imageQuality, imageQuality);
        offCamera.setParallelProjection(true);
        //offCamera.setFrustum(-50, 50, -(maxX-minX)/2+7, (maxX-minX)/2+8, (maxY-minY)/2+12, -(maxY-minY)/2+11);
        offCamera.setFrustum(0.1f, 2000, minX, maxX, maxY, minY);

        offView = renderManager.createPreView("Offscreen View", offCamera);
        offView.setClearFlags(true, true, true);
        offView.setBackgroundColor(ColorRGBA.DarkGray);

        // create offscreen framebuffer
        FrameBuffer offBuffer = new FrameBuffer(imageQuality, imageQuality, 1);

        //setup framebuffer's cam
        //offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, 0f));
        //offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);

//        Vector3f lookDirection = Vector3f.UNIT_Z;
//        Vector3f upDirection = Vector3f.UNIT_Y; 
        offCamera.lookAt(offCamera.getLocation().add(new Vector3f(0f, 1f, 0f)), new Vector3f(0, 0f, 1f));

        //setup framebuffer's texture
        Texture2D offTex = new Texture2D(imageQuality, imageQuality, Image.Format.RGBA8);
        offTex.setMinFilter(Texture.MinFilter.Trilinear);
        offTex.setMagFilter(Texture.MagFilter.Bilinear);

        //setup framebuffer to use texture
        offBuffer.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
        offBuffer.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(offTex));

        //set viewport to render to offscreen framebuffer
        offView.setOutputFrameBuffer(offBuffer);

        offView.attachScene(skyable);
    }

    public void polygonToTextureVisibleDebug() {
        Geometry quad = new Geometry("box", new Box(2, 2, 2));
        quad.setLocalTranslation(0, 1f, 0);
        //Texture offTex = setupOffscreenView();

        Camera offCamera = new Camera(imageQuality, imageQuality);
        offCamera.setParallelProjection(true);
        //offCamera.setFrustum(-50, 50, -(maxX-minX)/2+7, (maxX-minX)/2+8, (maxY-minY)/2+12, -(maxY-minY)/2+11);
        offCamera.setFrustum(0.1f, 2000, minX, maxX, maxY, minY);

        ViewPort offView = renderManager.createPreView("Offscreen View", offCamera);
        offView.setClearFlags(true, true, true);
        offView.setBackgroundColor(ColorRGBA.DarkGray);

        // create offscreen framebuffer
        FrameBuffer offBuffer = new FrameBuffer(imageQuality, imageQuality, 1);

        //setup framebuffer's cam
        //offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, 0f));
        //offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);

//        Vector3f lookDirection = Vector3f.UNIT_Z;
//        Vector3f upDirection = Vector3f.UNIT_Y; 
        offCamera.lookAt(offCamera.getLocation().add(new Vector3f(0f, 1f, 0f)), new Vector3f(0, 0f, 1f));

        //setup framebuffer's texture
        Texture2D offTex = new Texture2D(imageQuality, imageQuality, Image.Format.RGBA8);
        offTex.setMinFilter(Texture.MinFilter.Trilinear);
        offTex.setMagFilter(Texture.MagFilter.Bilinear);

        //setup framebuffer to use texture
        offBuffer.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
        offBuffer.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(offTex));

        //set viewport to render to offscreen framebuffer
        offView.setOutputFrameBuffer(offBuffer);

        offView.attachScene(skyable);

        debugShadowMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        debugShadowMaterial.setTexture("ColorMap", offTex);
//        mat.setColor("Color", ColorRGBA.Red);
        quad.setMaterial(debugShadowMaterial);
        rootNode.attachChild(quad);
    }

    public void connectSegNodes() {
        for (int i = 0; i < segments.size(); i++) {
            for (int j = 0; j < allData.all_Ways.length; j++) {
                if (segments.get(i).streetID == allData.all_Ways[j].id) {
                    boolean isStartFound = false;
                    boolean isEndFound = false;
                    for (int n = 0; n < allData.all_Ways[j].myNodes.length; n++) {
                        if (isStartFound == false) {
                            if (allData.all_Ways[j].myNodes[n].lon == segments.get(i).start.rawLat && allData.all_Ways[j].myNodes[n].lat == segments.get(i).start.rawLon) {
                                isStartFound = true;
                                segments.get(i).start.id = allData.all_Ways[j].myNodes[n].id;
                                segments.get(i).start.locationNode = allData.all_Ways[j].myNodes[n];
                            }
                        } else {
                            if (allData.all_Ways[j].myNodes[n].lon == segments.get(i).start.rawLat && allData.all_Ways[j].myNodes[n].lat == segments.get(i).start.rawLon) {
                                System.out.println("SEVERE ISSUE! TWO NODES HAVE THE SAME START!!!");
                                System.out.println("ORIG FOUND ID: " + segments.get(i).start.id);
                                System.out.println("FOUND ID: " + allData.all_Ways[j].myNodes[n].id);
                            }
                        }
                        if (isEndFound == false) {
                            if (allData.all_Ways[j].myNodes[n].lon == segments.get(i).end.rawLat && allData.all_Ways[j].myNodes[n].lat == segments.get(i).end.rawLon) {
                                isEndFound = true;
                                segments.get(i).end.id = allData.all_Ways[j].myNodes[n].id;
                                segments.get(i).end.locationNode = allData.all_Ways[j].myNodes[n];
                            }
                        } else {
                            if (allData.all_Ways[j].myNodes[n].lon == segments.get(i).end.rawLat && allData.all_Ways[j].myNodes[n].lat == segments.get(i).end.rawLon) {
                                System.out.println("SEVERE ISSUE! TWO NODES HAVE THE SAME END!!!");
                                System.out.println("ORIG FOUND ID: " + segments.get(i).end.id);
                                System.out.println("FOUND ID: " + allData.all_Ways[j].myNodes[n].id);
                            }
                        }
                    }
                }
            }
        }
    }

    /*
    * Percentage is between 0 and 0.5f and it cuts from both sides of the lon values
     */
    public void cutLonStartEndSorted(float percentage) {
        Collections.sort(segments, new StreetSegmentComparatorLon());
        int numRemove = (int) (segments.size() * percentage);
        for (int i = 0; i < numRemove; i++) {
            segments.remove(0);
            segments.remove(segments.size() - 1);
        }
    }

//    public void mergeStartEnds(){
//        System.out.println("!!!");
//        for (int i = 0; i < segments.size(); i++) {
//            for(int o=0;o<segments.get(i).start.parentOtherFromStart.size();o++){
//                SegmentNode sn=new SegmentNode(segments.get(i).start,segments.get(i).start.parentOtherFromStart.get(o));
//                segments.get(i).start.parentOtherFromStart.get(o)
//            }
//            //if(segments.get(i).start.)
//            
//            
    ////            for (int j = 0; j < segments.size(); j++) {
////                if(i!=j){
////                    if(segments.get(i).start.parent.streetID==segments.get(i).start.parent.start.)
////                }
////                
////                System.out.println("@@@");
////            }
//            System.out.println("@@@");
//        }
//    }
    
    public void handleAHit(ShadowPolygon shadowPolygon, CollisionResults results, Ray origRay, int collisionOrder) {
        if (isDebugging == true) {
            Sphere sphereS = new Sphere(10, 10, 0.2f);
            Geometry markS = new Geometry("DebugMarker", sphereS);
            Material mark_matS = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_matS.setColor("Color", ColorRGBA.Orange);
            markS.setMaterial(mark_matS);
            if (collisionOrder == -1) {
                markS.setLocalTranslation(results.getClosestCollision().getContactPoint());
            } else {
                markS.setLocalTranslation(results.getCollision(collisionOrder).getContactPoint());
            }
            debugNode.attachChild(markS);
        }
        if (collisionOrder == -1) {
            shadowPolygon.points.add(results.getClosestCollision().getContactPoint());
        } else {
            shadowPolygon.points.add(results.getCollision(collisionOrder).getContactPoint());
        }

        //Shoot another ray to hit the boundary of sky***
        Ray ray_p = new Ray();
        ray_p.origin = origRay.origin;
        ray_p.direction = origRay.direction;
        ray_p.direction.y = 0.01f;
        CollisionResults results_p = new CollisionResults();
        shootables.collideWith(ray_p, results_p);
        for (int i = 0; i < results_p.size(); i++) {
            String hitNameB = results_p.getCollision(i).getGeometry().getName();
            if (hitNameB.equals("meshN") || hitNameB.equals("meshS") || hitNameB.equals("meshE") || hitNameB.equals("meshW")) {
                Vector3f pt = results_p.getCollision(i).getContactPoint();
                pt.y = maxZ + zMargin * maxZ;
                if (isDebugging == true) {
                    Sphere sphereEdge = new Sphere(4, 4, 0.2f);
                    Geometry markEdge = new Geometry("DebugMarker", sphereEdge);
                    Material mark_matEdge = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mark_matEdge.setColor("Color", ColorRGBA.Orange);
                    markEdge.setMaterial(mark_matEdge);
                    markEdge.setLocalTranslation(results_p.getCollision(i).getContactPoint());
                    //System.out.println(m2.quads.get(c).tops[0]);
                    //segments.get(segmentIDX).shadowPolygon.points.add(results_p.getCollision(i).getContactPoint());

                    debugNode.attachChild(markEdge);
                }
                shadowPolygon.points.add(results_p.getCollision(i).getContactPoint());
            }
        }
    }

    public void fixStartEndLatOriented() {
        for (int i = 0; i < segments.size(); i++) {
            if (segments.get(i).start.rawLat > segments.get(i).end.rawLat) {
                SegmentNode temp = segments.get(i).start;
                segments.get(i).start = segments.get(i).end;
                segments.get(i).end = temp;
            }
        }
        for (int i = 0; i < segments.size(); i++) {
            if (segments.get(i).start.rawLat > segments.get(i).end.rawLat) {
                System.out.println("SEVERE ERROR!!! LAT OF START IS LARGER THAN END!!!");
            }
        }
    }

    public void addBuildings() {
        Node rootBuildingNode = new Node("root_building");
        rootBuildingNode.setUserData("isCheckedVis", false);
        goToDepthCreateNodes(rtree.root().get(), 0, rootBuildingNode,0);
        //System.out.println(rootBuildingNode.);
        shootables.attachChild(rootBuildingNode);
        System.out.println("!!!");

//        BoundingBox bbox = (BoundingBox) (rootBuildingNode.getWorldBound());
//        Vector3f center = bbox.getCenter();
//        Vector3f extent = bbox.getExtent(null);
//
//        Box box = new Box(extent.x, extent.y, extent.z); // Half extents
//        Geometry boxGeom = new Geometry("bbox", box);
//
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Pink);
//        mat.getAdditionalRenderState().setWireframe(true);
//        boxGeom.setMaterial(mat);
//
//        boxGeom.setLocalTranslation(center);
//        rootNode.attachChild(boxGeom);
        goToDepthAddDebugBoundingBoxes(rootBuildingNode, 0);

    }

    public void goToDepthCreateNodes(com.github.davidmoten.rtree.Node node, int depth, Node nodes, int counter) {
        if (node instanceof Leaf) {
            System.out.println("LEAF size: " + ((Leaf) node).count());
            for (int i = 0; i < ((Leaf) node).count(); i++) {
                nodes.attachChild(((com.jme3.scene.Geometry) (((EntryDefault) (((Leaf) node).entries().get(i))).value())));
            }
            nodes.updateModelBound();
            //return depth;
//            System.out.println("LEAF");
        } else {
            System.out.println("BRANCH size: " + ((NonLeaf) node).count());
            for (int i = 0; i < ((NonLeaf) node).count(); i++) {
                Node childNode = new Node("buildingBranch_"+counter);
                childNode.setUserData("isCheckedVis", false);
                childNode.setUserData("id", counter);
                goToDepthCreateNodes(((NonLeaf) node).child(i), depth + 1, childNode, counter+1);
                nodes.attachChild(childNode);
                nodes.updateModelBound();
            }
        }
        //return depth;
    }

    public void goToDepthAddDebugBoundingBoxes(Node nodes, int depth) {
        List<Spatial> children = nodes.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof Node) {
                BoundingBox bbox = (BoundingBox) (nodes.getWorldBound());
                Vector3f center = bbox.getCenter();
                Vector3f extent = bbox.getExtent(null);

                Box box = new Box(extent.x, extent.y, extent.z); // Half extents
                Geometry boxGeom = new Geometry("bbox", box);

                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                switch (depth) {
                    case 0:
                        mat.setColor("Color", ColorRGBA.Blue);
                        break;
                    case 1:
                        mat.setColor("Color", ColorRGBA.Brown);
                        break;
                    case 2:
                        mat.setColor("Color", ColorRGBA.Cyan);
                        break;
                    case 3:
                        mat.setColor("Color", ColorRGBA.Magenta);
                        break;
                    case 4:
                        mat.setColor("Color", ColorRGBA.Red);
                        break;
                    case 5:
                        mat.setColor("Color", ColorRGBA.Green);
                        break;
                    default:
                        mat.setColor("Color", ColorRGBA.Yellow);
                        break;
                }
                mat.getAdditionalRenderState().setWireframe(true);
                boxGeom.setMaterial(mat);

                boxGeom.setLocalTranslation(center);
                rootNode.attachChild(boxGeom);
                goToDepthAddDebugBoundingBoxes((Node) (children.get(i)), depth + 1);
            }
        }
    }

    Node searchingNode = null;
    public int findSmallestNodeViewerIn(Node node, Vector3f loc, int depth, int foundDepth) {
        List<Spatial> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            //if (searchingNode == null) {
            if (children.get(i) instanceof Node) {
                BoundingBox bbox = (BoundingBox) (node.getWorldBound());
                if (bbox.contains(loc) == true) {
                    if (depth > foundDepth) {
                        searchingNode = node;
                        foundDepth = depth;
                    }
                }
                foundDepth = findSmallestNodeViewerIn((Node) (children.get(i)), loc, depth + 1, foundDepth);
            } else if (children.get(i) instanceof Geometry) {
                BoundingBox bbox = (BoundingBox) (node.getWorldBound());
                if (bbox.contains(loc) == true) {
                    if (depth > foundDepth) {
                        searchingNode = node;
                        foundDepth = depth;
                    }
                }
            }
            //}
        }
        return foundDepth;
    }

    public void findNodeShadowContributing(Node node, Vector3f originStart, Vector3f originEnd) {
        List<Spatial> children = node.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof Node) {
                BoundingBox bbox = (BoundingBox) (node.getWorldBound());
                if (bbox.contains(originStart) == false) {
                    //Shoot bounding box
                    
                }
                findNodeShadowContributing((Node) (children.get(i)), originStart, originEnd);
            } else if (children.get(i) instanceof Geometry) {
                shootForBuilding((Geometry) (children.get(i)), originStart, originEnd);
            }
        }
    }

    public void findBuildingShootingSelfNode(Node nodeStart, Vector3f originStart, Vector3f originEnd) {
        List<Spatial> children = nodeStart.getChildren();
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof Node) {
                findBuildingShootingSelfNode((Node) (children.get(i)), originStart, originEnd);
            } else if (children.get(i) instanceof Geometry) {
                shootForBuilding((Geometry) (children.get(i)), originStart, originEnd);
            }
        }
    }

    public void shootForBuilding(Geometry building, Vector3f origin, Vector3f origin2) {
        ShadowPolygon shadowPolygon = new ShadowPolygon();
        Integer buildingIDX = Integer.valueOf(building.getName().split("_")[1]);
        CustomMesh m2 = myMeshes.get(buildingIDX);
        for (int c = 0; c < m2.quads.size(); c++) {
            boolean isOnePointHitSky = false;
            for (int r = 0; r < 2; r++) {
                CollisionResults results = new CollisionResults();
                //markP.setLocalTranslation(m2.quads.get(c).tops[0]);
                Vector3f top = new Vector3f(m2.quads.get(c).tops[r].x, m2.quads.get(c).tops[r].y, m2.quads.get(c).tops[r].z);
                top.y = top.y + 0.01f;
                if (isDebugging == true) {
                    Sphere sphere = new Sphere(10, 10, 0.2f);
                    Geometry markT = new Geometry("DebugMarker", sphere);
                    Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mark_mat.setColor("Color", ColorRGBA.Yellow);
                    markT.setMaterial(mark_mat);
                    markT.setLocalTranslation(top);
                    //System.out.println(m2.quads.get(c).tops[0]);
                    debugNode.attachChild(markT);
                }
                Ray ray = new Ray(origin, top.subtract(origin));
                shootables.collideWith(ray, results);
                if (results.size() > 0) {
                    //CollisionResult closest = results.getClosestCollision();
                    String hitName = results.getClosestCollision().getGeometry().getName();
                    if (hitName.equals("meshSky")) {
                        isOnePointHitSky = true;
                        handleAHit(shadowPolygon, results, ray, -1);
                    } else if (isOnePointHitSky == true) {
                        for (int m = 0; m < results.size(); m++) {
                            String geomName = results.getCollision(m).getGeometry().getName();
                            if (geomName.equals("meshSky")) {
                                handleAHit(shadowPolygon, results, ray, m);
                            }
                        }
                    }
                }
            }
            isOnePointHitSky = false;
            for (int r = 1; r > -1; r--) {
                CollisionResults results = new CollisionResults();
                //markP.setLocalTranslation(m2.quads.get(c).tops[0]);
                Vector3f top = new Vector3f(m2.quads.get(c).tops[r].x, m2.quads.get(c).tops[r].y, m2.quads.get(c).tops[r].z);
                top.y = top.y + 0.01f;
                Ray ray = new Ray(origin, top.subtract(origin));
                shootables.collideWith(ray, results);
                if (results.size() > 0) {
                    //CollisionResult closest = results.getClosestCollision();
                    String hitName = results.getClosestCollision().getGeometry().getName();
                    if (hitName.equals("meshSky")) {
                        isOnePointHitSky = true;
                        handleAHit(shadowPolygon, results, ray, -1);
                    } else if (isOnePointHitSky == true) {
                        for (int m = 0; m < results.size(); m++) {
                            String geomName = results.getCollision(m).getGeometry().getName();
                            if (geomName.equals("meshSky")) {
                                handleAHit(shadowPolygon, results, ray, m);
                            }
                        }
                    }
                }
            }
        }
        ArrayList<Vector3f> corners = new ArrayList();
        corners.add(new Vector3f(0.999f * maxX, maxZ + zMargin * maxZ, 0.999f * maxY));
        corners.add(new Vector3f(0.999f * minX, maxZ + zMargin * maxZ, 0.999f * maxY));
        corners.add(new Vector3f(0.999f * minX, maxZ + zMargin * maxZ, 0.999f * minY));
        corners.add(new Vector3f(0.999f * maxX, maxZ + zMargin * maxZ, 0.999f * minY));
        shootToCorners(shadowPolygon, origin, corners, buildingIDX);

        // GO FOR THE SECOND POINT IN THE STREET
        //Vector3f origin2 = segments.get(segmentIDX).end.location;
        for (int c = 0; c < m2.quads.size(); c++) {
            boolean isOnePointHitSky = false;
            for (int r = 0; r < 2; r++) {
                CollisionResults results = new CollisionResults();
                Vector3f top = new Vector3f(m2.quads.get(c).tops[r].x, m2.quads.get(c).tops[r].y, m2.quads.get(c).tops[r].z);
                top.y = top.y + 0.1f;
                Ray ray = new Ray(origin2, top.subtract(origin2));
                shootables.collideWith(ray, results);

                if (results.size() > 0) {
                    //CollisionResult closest = results.getClosestCollision();
                    String hitName = results.getClosestCollision().getGeometry().getName();
                    if (hitName.equals("meshSky")) {
                        isOnePointHitSky = true;
                        handleAHit(shadowPolygon, results, ray, -1);
                    } else if (isOnePointHitSky == true) {
                        for (int m = 0; m < results.size(); m++) {
                            String geomName = results.getCollision(m).getGeometry().getName();
                            if (geomName.equals("meshSky")) {
                                handleAHit(shadowPolygon, results, ray, m);
                            }
                        }
                    }
                }
            }

            isOnePointHitSky = false;
            for (int r = 1; r > -1; r--) {
                CollisionResults results = new CollisionResults();
                Vector3f top = new Vector3f(m2.quads.get(c).tops[r].x, m2.quads.get(c).tops[r].y, m2.quads.get(c).tops[r].z);
                top.y = top.y + 0.1f;
                Ray ray = new Ray(origin2, top.subtract(origin2));
                shootables.collideWith(ray, results);

                if (results.size() > 0) {
                    //CollisionResult closest = results.getClosestCollision();
                    String hitName = results.getClosestCollision().getGeometry().getName();
                    if (hitName.equals("meshSky")) {
                        isOnePointHitSky = true;
                        handleAHit(shadowPolygon, results, ray, -1);
                    } else if (isOnePointHitSky == true) {
                        for (int m = 0; m < results.size(); m++) {
                            String geomName = results.getCollision(m).getGeometry().getName();
                            if (geomName.equals("meshSky")) {
                                handleAHit(shadowPolygon, results, ray, m);
                            }
                        }
                    }
                }
            }
        }
        shootToCorners(shadowPolygon, origin2, corners, buildingIDX);
        //ArrayList<Vector3f> debugConvexHull = calcConvexHull(segments.get(segmentIDX).shadowPolygon, offset);
        ArrayList<Vector3f> debugConvexHull = calcConvexHull(shadowPolygon, -0.01f);
        if (debugConvexHull.size() > 2) {
            debugDrawShadow(debugConvexHull, ColorRGBA.Black);
        }
    }

}
