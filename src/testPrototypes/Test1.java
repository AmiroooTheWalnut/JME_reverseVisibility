package testPrototypes;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
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
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;
import org.locationtech.jts.triangulate.polygon.PolygonTriangulator;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class Test1 extends SimpleApplication {

    Vector3f[] vertices;
    float maxX = 0;
    float minX = 0;
    float maxY = 0;
    float minY = 0;
    float maxZ = 0;
    float zMargin = 2f;
    boolean isLatLon=true;
    ArrayList<CustomMesh> myMeshes = new ArrayList();
    ArrayList<StreetSegment> segments = new ArrayList();
    Node shootables;
    Node shadowables;
    MeshSegmentShadowHolder mss=new MeshSegmentShadowHolder();

    private Geometry mark;
    private Geometry markP;

    //ArrayList<Vector3f> debugConvexHull;

    public static void main(String[] args) {
        Test1 app = new Test1();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        shootables = new Node("Shootables");
        shadowables = new Node("Shadowables");
        rootNode.attachChild(shadowables);
//        flyCam.setEnabled(false);
        initKeys();
        initMark();       // a red sphere to mark the hit
        initMarkP();
        flyCam.setMoveSpeed(10);
        Box box = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", box);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);

        Texture grass = assetManager.loadTexture("Textures/buildings.png");
        Image image = grass.getImage();
        ByteBuffer buffer = image.getData(0);

        Image.Format format = image.getFormat();

        int width = image.getWidth();
        int height = image.getHeight();

        int x = 0;
        for (int y = 0; y < 10; y++) {
            int bytesPerPixel = 4;
            int index = (y * width + x) * bytesPerPixel;

            int alpha = buffer.get(index) & 0xFF;
            int green = buffer.get(index + 1) & 0xFF;
            int blue = buffer.get(index + 2) & 0xFF;
            int red = buffer.get(index + 3) & 0xFF;

            //System.out.println("R: " + red + ", G: " + green + ", B: " + blue + ", A: " + alpha);
        }

//        isLatLon=true;
//        triangulateGeoJSON("assets/Scenes/tucsonDTStreets.geojson");
        isLatLon=false;
        triangulateGeoJSON("assets/Scenes/debug1.geojson");

        minX = minX * 10;
        maxX = maxX * 10;
        minY = minY * 10;
        maxY = maxY * 10;

        Mesh meshN = new Mesh();
        Vector3f[] northVertices = new Vector3f[4];
        int[] indexesN = {2, 0, 1, 1, 3, 2};
        northVertices[0] = new Vector3f(minX, 0, minY);
        northVertices[1] = new Vector3f(maxX, 0, minY);
        northVertices[2] = new Vector3f(minX, maxZ + zMargin*maxZ, minY);
        northVertices[3] = new Vector3f(maxX, maxZ + zMargin*maxZ, minY);

        meshN.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(northVertices));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        meshN.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexesN));
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
        southVertices[2] = new Vector3f(minX, maxZ + zMargin*maxZ, maxY);
        southVertices[3] = new Vector3f(maxX, maxZ + zMargin*maxZ, maxY);

        meshS.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(southVertices));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        meshS.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexesS));
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
        skyVertices[0] = new Vector3f(minX, maxZ + zMargin*maxZ, minY);
        skyVertices[1] = new Vector3f(maxX, maxZ + zMargin*maxZ, minY);
        skyVertices[2] = new Vector3f(minX, maxZ + zMargin*maxZ, maxY);
        skyVertices[3] = new Vector3f(maxX, maxZ + zMargin*maxZ, maxY);

        meshSky.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(skyVertices));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        meshSky.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexesSky));
        meshSky.updateBound();

        Geometry geoSky = new Geometry("meshSky", meshSky); // using our custom mesh object
        Material matSky = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matSky.setColor("Color", ColorRGBA.Blue);
        //mat.setBoolean("VertexColor", true);
        geoSky.setMaterial(matSky);
        shootables.attachChild(geoSky);
        Mesh meshSkyD = new Mesh();
        Vector3f[] skyVerticesD = new Vector3f[4];
        int[] indexesSkyD = {2, 0, 1, 1, 3, 2};
        skyVerticesD[0] = new Vector3f(minX, maxZ + zMargin*maxZ, minY);
        skyVerticesD[1] = new Vector3f(maxX, maxZ + zMargin*maxZ, minY);
        skyVerticesD[2] = new Vector3f(minX, maxZ + zMargin*maxZ, maxY);
        skyVerticesD[3] = new Vector3f(maxX, maxZ + zMargin*maxZ, maxY);

        meshSkyD.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(skyVerticesD));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        meshSkyD.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexesSkyD));
        meshSkyD.updateBound();

        Geometry geoSkyD = new Geometry("meshSkyD", meshSkyD); // using our custom mesh object
        Material matSkyD = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matSkyD.setColor("Color", ColorRGBA.Blue);
        //mat.setBoolean("VertexColor", true);
        geoSkyD.setMaterial(matSkyD);
        shadowables.attachChild(geoSkyD);

        Mesh meshE = new Mesh();
        Vector3f[] eastVertices = new Vector3f[4];
        int[] indexesE = {2, 1, 0, 1, 2, 3};
        eastVertices[0] = new Vector3f(minX, 0, minY);
        eastVertices[1] = new Vector3f(minX, 0, maxY);
        eastVertices[2] = new Vector3f(minX, maxZ + zMargin*maxZ, minY);
        eastVertices[3] = new Vector3f(minX, maxZ + zMargin*maxZ, maxY);

        meshE.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(eastVertices));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        meshE.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexesE));
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
        westVertices[2] = new Vector3f(maxX, maxZ + zMargin*maxZ, minY);
        westVertices[3] = new Vector3f(maxX, maxZ + zMargin*maxZ, maxY);

        meshW.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(westVertices));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        meshW.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexesW));
        meshW.updateBound();

        Geometry geoW = new Geometry("meshW", meshW); // using our custom mesh object
        Material matW = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matW.setColor("Color", ColorRGBA.DarkGray);
        //mat.setBoolean("VertexColor", true);
        geoW.setMaterial(matW);
        shootables.attachChild(geoW);

        rootNode.attachChild(shootables);
        polygonToTextureVisible(60);
    }

    public void triangulateGeoJSON(String fileName) {
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
            JSONObject jsonObject = new JSONObject(content);

            JSONArray geoms = (JSONArray) (jsonObject.get("features"));
            float avgX = 0;
            float avgY = 0;
            int counterT = 0;
//            float TmaxX = 0;
//            float TminX = 0;
//            float TmaxY = 0;
//            float TminY = 0;
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
//                                    if (x > TmaxX) {
//                                        TmaxX = x;
//                                    }
//                                    if (x < TminX) {
//                                        TminX = x;
//                                    }
//                                    if (y > TmaxY) {
//                                        TmaxY = y;
//                                    }
//                                    if (y < TminY) {
//                                        TminY = y;
//                                    }
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
//                                if (x > TmaxX) {
//                                    TmaxX = x;
//                                }
//                                if (x < TminX) {
//                                    TminX = x;
//                                }
//                                if (y > TmaxY) {
//                                    TmaxY = y;
//                                }
//                                if (y < TminY) {
//                                    TminY = y;
//                                }
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
            if(isLatLon==true){
                scale = 10000;
            }else{
                scale = 1;
            }
            int buildingCounter = 0;

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

                                    JSONArray coordsP = (JSONArray) poly1.get(c - 1);
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

                                    Vector3f start = new Vector3f(xP, 0, yP);
                                    Vector3f end = new Vector3f(x, 0, y);
                                    Line line = new Line(start, end);
                                    StreetSegment sg = new StreetSegment();
                                    sg.start.location = start;
                                    sg.end.location = end;
                                    segments.add(sg);
                                    Geometry lineGeom = new Geometry("line", line);
                                    Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                                    mat2.setColor("Color", ColorRGBA.Red);  // Set the color
                                    lineGeom.setMaterial(mat2);
                                    line.setLineWidth(2);  // Only visible in some contexts (e.g., OpenGL core profile)
                                    rootNode.attachChild(lineGeom);
                                }
                            }
                        } else if (geom1.get("type").equals("LineString")) {
                            for (int c = 1; c < geom2.length(); c++) {
                                JSONArray coords = (JSONArray) geom2.get(c);
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

                                JSONArray coordsP = (JSONArray) geom2.get(c - 1);
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

                                Vector3f start = new Vector3f(xP, 0, yP);
                                Vector3f end = new Vector3f(x, 0, y);
                                Line line = new Line(start, end);
                                StreetSegment sg = new StreetSegment();
                                sg.start.location = start;
                                sg.end.location = end;
                                segments.add(sg);
                                Geometry lineGeom = new Geometry("line", line);
                                Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                                mat2.setColor("Color", ColorRGBA.Red);  // Set the color
                                lineGeom.setMaterial(mat2);
                                line.setLineWidth(2);  // Only visible in some contexts (e.g., OpenGL core profile)
                                rootNode.attachChild(lineGeom);
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
                    if (height > maxZ) {
                        maxZ = height;
                    }
//                for(int p=0;p<geom2.length();p++){
                    for (int p = 0; p < 1; p++) {
                        JSONArray poly1 = (JSONArray) geom2.get(p);
                        Mesh mesh = new Mesh();
                        CustomMesh tempMesh = new CustomMesh();
                        vertices = new Vector3f[(poly1.length() - 1) * 2];
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
                        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
                        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
                        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
                        mesh.updateBound();
                        tempMesh.mesh = mesh;
                        myMeshes.add(tempMesh);
                        Geometry geo = new Geometry("Building_" + buildingCounter, mesh); // using our custom mesh object
                        Material mat = new Material(assetManager,
                                "Common/MatDefs/Misc/Unshaded.j3md");
                        mat.setColor("Color", ColorRGBA.Brown);
                        //mat.setBoolean("VertexColor", true);
                        geo.setMaterial(mat);
                        shootables.attachChild(geo);
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

    public void debugShootFirstTriangle(int buildingIDX, int segmentIDX, ColorRGBA color, float offset) {
        ShadowPolygon shadowPolygon=new ShadowPolygon();
        Vector3f origin = segments.get(segmentIDX).start.location;
        //CustomMesh m2 = myMeshes.get(2);
        CustomMesh m2 = myMeshes.get(buildingIDX);
        //rootNode.detachChild(markP);
        //markP.setLocalTranslation(origin);

        Sphere sphereStart = new Sphere(30, 30, 0.2f);
        Geometry markStart = new Geometry("BOOM!", sphereStart);
        Material mark_matStart = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_matStart.setColor("Color", ColorRGBA.Green);
        markStart.setMaterial(mark_matStart);
        markStart.setLocalTranslation(segments.get(segmentIDX).start.location);
        //System.out.println(m2.quads.get(c).tops[0]);
        rootNode.attachChild(markStart);

        Sphere sphereEnd = new Sphere(30, 30, 0.2f);
        Geometry markEnd = new Geometry("BOOM!", sphereEnd);
        Material mark_matEnd = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_matEnd.setColor("Color", ColorRGBA.Green);
        markEnd.setMaterial(mark_matEnd);
        markEnd.setLocalTranslation(segments.get(segmentIDX).end.location);
        //System.out.println(m2.quads.get(c).tops[0]);
        rootNode.attachChild(markEnd);

//        rootNode.attachChild(markP);
        for (int c = 0; c < m2.quads.size(); c++) {
            for (int r = 0; r < 2; r++) {
                CollisionResults results = new CollisionResults();
                //markP.setLocalTranslation(m2.quads.get(c).tops[0]);
                Sphere sphere = new Sphere(30, 30, 0.2f);
                Geometry markT = new Geometry("BOOM!", sphere);
                Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mark_mat.setColor("Color", ColorRGBA.Yellow);
                markT.setMaterial(mark_mat);
                Vector3f top = new Vector3f(m2.quads.get(c).tops[r].x, m2.quads.get(c).tops[r].y, m2.quads.get(c).tops[r].z);
                top.y = top.y + 0.1f;
                markT.setLocalTranslation(top);
                //System.out.println(m2.quads.get(c).tops[0]);
                rootNode.attachChild(markT);
                Ray ray = new Ray(origin, top.subtract(origin));
                shootables.collideWith(ray, results);
//            System.out.println("----- Collisions? " + results.size() + "-----");
//            for (int i = 0; i < results.size(); i++) {
//                // For each hit, we know distance, impact point, name of geometry.
//                float dist = results.getCollision(i).getDistance();
//                Vector3f pt = results.getCollision(i).getContactPoint();
//                String hit = results.getCollision(i).getGeometry().getName();
//                System.out.println("* Collision #" + i);
//                System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
//            }
                if (results.size() > 0) {
                    // The closest collision point is what was truly hit:
                    CollisionResult closest = results.getClosestCollision();
                    String hitName = results.getClosestCollision().getGeometry().getName();
                    //System.out.println(results.getClosestCollision().getGeometry().getName());
                    if (hitName.equals("meshSky")) {
                        Sphere sphereS = new Sphere(30, 30, 0.2f);
                        Geometry markS = new Geometry("BOOM!", sphereS);
                        Material mark_matS = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mark_matS.setColor("Color", ColorRGBA.Orange);
                        markS.setMaterial(mark_matS);
                        markS.setLocalTranslation(results.getClosestCollision().getContactPoint());
                        //System.out.println(m2.quads.get(c).tops[0]);
                        //segments.get(segmentIDX).shadowPolygon.points.add(results.getClosestCollision().getContactPoint());
                        shadowPolygon.points.add(results.getClosestCollision().getContactPoint());
                        rootNode.attachChild(markS);

                        //Shoot another ray to hit the boundary of sky***
                        Ray ray_p = new Ray();
                        ray_p.origin = ray.origin;
                        ray_p.direction = ray.direction;
                        ray_p.direction.y = 0.1f;
                        CollisionResults results_p = new CollisionResults();
                        shootables.collideWith(ray_p, results_p);
//                    System.out.println("----- Collisions? " + results.size() + "-----");
//                    for (int i = 0; i < results_p.size(); i++) {
//                        // For each hit, we know distance, impact point, name of geometry.
//                        float dist = results_p.getCollision(i).getDistance();
//                        Vector3f pt = results_p.getCollision(i).getContactPoint();
//                        String hit = results_p.getCollision(i).getGeometry().getName();
//                        System.out.println("* Collision #" + i);
//                        System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
//                    }
                        for (int i = 0; i < results_p.size(); i++) {
                            //float dist = results.getCollision(i).getDistance();
                            //Vector3f pt = results.getCollision(i).getContactPoint();
                            String hitNameB = results_p.getCollision(i).getGeometry().getName();
                            //System.out.println("* Collision #" + i);
                            //System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
//                        System.out.println(hitNameB);
                            if (hitNameB.equals("meshN") || hitNameB.equals("meshS") || hitNameB.equals("meshE") || hitNameB.equals("meshW")) {
                                Vector3f pt = results_p.getCollision(i).getContactPoint();
                                pt.y = maxZ + zMargin*maxZ;
                                Sphere sphereEdge = new Sphere(30, 30, 0.2f);
                                Geometry markEdge = new Geometry("BOOMEdge!", sphereEdge);
                                Material mark_matEdge = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                                mark_matEdge.setColor("Color", ColorRGBA.Orange);
                                markEdge.setMaterial(mark_matEdge);
                                markEdge.setLocalTranslation(results_p.getCollision(i).getContactPoint());
                                //System.out.println(m2.quads.get(c).tops[0]);
                                //segments.get(segmentIDX).shadowPolygon.points.add(results_p.getCollision(i).getContactPoint());
                                shadowPolygon.points.add(results_p.getCollision(i).getContactPoint());
                                rootNode.attachChild(markEdge);
                            }
                        }
                    }

                    // Let's interact - we mark the hit with a red dot.
                    mark.setLocalTranslation(closest.getContactPoint());
                    rootNode.attachChild(mark);
                } else {
                    // No hits? Then remove the red mark.
                    rootNode.detachChild(mark);
                }
            }
        }
        ArrayList<Vector3f> corners = new ArrayList();
        corners.add(new Vector3f(0.999f * maxX, maxZ + zMargin*maxZ, 0.999f * maxY));
        corners.add(new Vector3f(0.999f * minX, maxZ + zMargin*maxZ, 0.999f * maxY));
        corners.add(new Vector3f(0.999f * minX, maxZ + zMargin*maxZ, 0.999f * minY));
        corners.add(new Vector3f(0.999f * maxX, maxZ + zMargin*maxZ, 0.999f * minY));
        shootToCorners(shadowPolygon, origin, corners, segmentIDX, buildingIDX);

        // GO FOR THE SECOND POINT IN THE STREET
        Vector3f origin2 = segments.get(segmentIDX).end.location;
        //CustomMesh m2 = myMeshes.get(2);
        //CustomMesh m2 = myMeshes.get(6);
//        rootNode.detachChild(markP);
//        markP.setLocalTranslation(origin2);
//        rootNode.attachChild(markP);
        for (int c = 0; c < m2.quads.size(); c++) {
            for (int r = 0; r < 2; r++) {
                CollisionResults results = new CollisionResults();
//            Sphere sphere = new Sphere(30, 30, 0.2f);
//            Geometry markT = new Geometry("BOOM!", sphere);
//            Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//            mark_mat.setColor("Color", ColorRGBA.Yellow);
//            markT.setMaterial(mark_mat);
                Vector3f top = new Vector3f(m2.quads.get(c).tops[r].x, m2.quads.get(c).tops[r].y, m2.quads.get(c).tops[r].z);
                top.y = top.y + 0.1f;
//            markT.setLocalTranslation(top);
//            //System.out.println(m2.quads.get(c).tops[0]);
//            rootNode.attachChild(markT);
                Ray ray = new Ray(origin2, top.subtract(origin2));
                shootables.collideWith(ray, results);
//            System.out.println("----- Collisions? " + results.size() + "-----");
//            for (int i = 0; i < results.size(); i++) {
//                // For each hit, we know distance, impact point, name of geometry.
//                float dist = results.getCollision(i).getDistance();
//                Vector3f pt = results.getCollision(i).getContactPoint();
//                String hit = results.getCollision(i).getGeometry().getName();
//                System.out.println("* Collision #" + i);
//                System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
//            }

                if (results.size() > 0) {
                    // The closest collision point is what was truly hit:
                    CollisionResult closest = results.getClosestCollision();
                    String hitName = results.getClosestCollision().getGeometry().getName();
                    //System.out.println(results.getClosestCollision().getGeometry().getName());
                    if (hitName.equals("meshSky")) {
                        Sphere sphereS = new Sphere(30, 30, 0.2f);
                        Geometry markS = new Geometry("BOOM!", sphereS);
                        Material mark_matS = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mark_matS.setColor("Color", ColorRGBA.Magenta);
                        markS.setMaterial(mark_matS);
                        markS.setLocalTranslation(results.getClosestCollision().getContactPoint());
                        //System.out.println(m2.quads.get(c).tops[0]);
                        //segments.get(segmentIDX).shadowPolygon.points.add(results.getClosestCollision().getContactPoint());
                        shadowPolygon.points.add(results.getClosestCollision().getContactPoint());
                        rootNode.attachChild(markS);

                        //Shoot another ray to hit the boundary of sky***
                        Ray ray_p = new Ray();
                        ray_p.origin = ray.origin;
                        ray_p.direction = ray.direction;
                        ray_p.direction.y = 0.1f;
                        CollisionResults results_p = new CollisionResults();
                        shootables.collideWith(ray_p, results_p);
//                    System.out.println("----- Collisions? " + results.size() + "-----");
//                    for (int i = 0; i < results_p.size(); i++) {
//                        // For each hit, we know distance, impact point, name of geometry.
//                        float dist = results_p.getCollision(i).getDistance();
//                        Vector3f pt = results_p.getCollision(i).getContactPoint();
//                        String hit = results_p.getCollision(i).getGeometry().getName();
//                        System.out.println("* Collision #" + i);
//                        System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
//                    }
                        for (int i = 0; i < results_p.size(); i++) {
                            //float dist = results.getCollision(i).getDistance();
                            //Vector3f pt = results.getCollision(i).getContactPoint();
                            String hitNameB = results_p.getCollision(i).getGeometry().getName();
                            //System.out.println("* Collision #" + i);
                            //System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
//                        System.out.println(hitNameB);
                            if (hitNameB.equals("meshN") || hitNameB.equals("meshS") || hitNameB.equals("meshE") || hitNameB.equals("meshW")) {
                                Vector3f pt = results_p.getCollision(i).getContactPoint();
                                pt.y = maxZ + zMargin*maxZ;
                                Sphere sphereEdge = new Sphere(30, 30, 0.2f);
                                Geometry markEdge = new Geometry("BOOMEdge!", sphereEdge);
                                Material mark_matEdge = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                                mark_matEdge.setColor("Color", ColorRGBA.Magenta);
                                markEdge.setMaterial(mark_matEdge);
                                markEdge.setLocalTranslation(results_p.getCollision(i).getContactPoint());
                                //System.out.println(m2.quads.get(c).tops[0]);
                                //segments.get(segmentIDX).shadowPolygon.points.add(results_p.getCollision(i).getContactPoint());
                                shadowPolygon.points.add(results_p.getCollision(i).getContactPoint());
                                rootNode.attachChild(markEdge);
                            }
                        }
                    }

                    // Let's interact - we mark the hit with a red dot.
                    mark.setLocalTranslation(closest.getContactPoint());
                    rootNode.attachChild(mark);
                } else {
                    // No hits? Then remove the red mark.
                    rootNode.detachChild(mark);
                }
            }
        }
        shootToCorners(shadowPolygon, origin2, corners, segmentIDX, buildingIDX);
        //debugConvexHull = calcConvexHull(segments.get(segmentIDX).shadowPolygon, offset);
        ArrayList<Vector3f> debugConvexHull = calcConvexHull(shadowPolygon, offset);
        //ShadowPolygon a = new ShadowPolygon(debugConvexHull);
        HashMap<Integer,ShadowPolygon> buildingsMap=new HashMap();
        buildingsMap.put(buildingIDX, new ShadowPolygon(debugConvexHull));
        mss.data.put(segmentIDX, buildingsMap);

        if(mss.data.get(segmentIDX).get(buildingIDX).points.size()>2){
            debugDrawShadow(mss.data.get(segmentIDX).get(buildingIDX),color);
        }

        System.out.println("!!!");
    }

    public void debugDrawShadow(ShadowPolygon shadowPolygon,ColorRGBA color) {
        Vector3f verticesLocal[] = new Vector3f[shadowPolygon.points.size()];
        for (int i = 0; i < verticesLocal.length; i++) {
            verticesLocal[i] = shadowPolygon.points.get(i);
            verticesLocal[i].y = verticesLocal[i].y - 0.01f;
        }
        int[] indices = new int[(shadowPolygon.points.size() - 2) * 3];
        int counter = 0;
        for (int i = 2; i < shadowPolygon.points.size(); i++) {
            indices[counter] = 0;
            counter = counter + 1;
            indices[counter] = i;
            counter = counter + 1;
            indices[counter] = i - 1;
            counter = counter + 1;
        }
        Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(verticesLocal));
        //mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
        mesh.updateBound();

        Geometry geo = new Geometry("OurMesh", mesh); // using our custom mesh object
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.White);
        mat.setColor("Color", color);
        //mat.setBoolean("VertexColor", true);
        geo.setMaterial(mat);
        shadowables.attachChild(geo);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
//        if (debugConvexHull != null) {
//            System.out.println("!1!");
//        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    /**
     * Declaring the "Shoot" action and mapping to its triggers.
     */
    private void initKeys() {
        inputManager.addMapping("Shoot",
                new KeyTrigger(KeyInput.KEY_SPACE), // trigger 1: spacebar
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
        inputManager.addListener(actionListener, "Shoot");
    }
    /**
     * Defining the "Shoot" action: Determine what was hit and how to respond.
     */
    final private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            mss=new MeshSegmentShadowHolder();
            if (name.equals("Shoot") && !keyPressed) {
                debugShootFirstTriangle(0, 0, ColorRGBA.White, -0.01f);
                debugShootFirstTriangle(0, 1, ColorRGBA.Pink, -0.02f);
                debugShootFirstTriangle(0, 2, new ColorRGBA(.2f, .2f, .2f, 0.1f), -0.03f);
                debugShootFirstTriangle(0, 3, ColorRGBA.LightGray, -0.04f);
                debugShootFirstTriangle(0, 4, new ColorRGBA(65 / 255f, 40 / 255f, 25 / 255f, 0.1f), -0.05f);
                
                debugShootFirstTriangle(1, 0, ColorRGBA.White, -0.01f);
                debugShootFirstTriangle(0, 1, ColorRGBA.Pink, -0.02f);
                debugShootFirstTriangle(0, 2, new ColorRGBA(.2f, .2f, .2f, 0.1f), -0.03f);
                debugShootFirstTriangle(0, 3, ColorRGBA.LightGray, -0.04f);
                debugShootFirstTriangle(0, 4, new ColorRGBA(65 / 255f, 40 / 255f, 25 / 255f, 0.1f), -0.05f);

                CollisionResults results = new CollisionResults();

                Ray ray = new Ray(cam.getLocation(), cam.getDirection());

                rootNode.collideWith(ray, results);

                //System.out.println("----- Collisions? " + results.size() + "-----");
                for (int i = 0; i < results.size(); i++) {
                    // For each hit, we know distance, impact point, name of geometry.
                    float dist = results.getCollision(i).getDistance();
                    Vector3f pt = results.getCollision(i).getContactPoint();
                    String hit = results.getCollision(i).getGeometry().getName();
                    //System.out.println("* Collision #" + i);
                    //System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                }

                if (results.size() > 0) {
                    // The closest collision point is what was truly hit:
                    CollisionResult closest = results.getClosestCollision();
                    mark.setLocalTranslation(closest.getContactPoint());
                    rootNode.attachChild(mark);
                } else {
                    // No hits? Then remove the red mark.
                    rootNode.detachChild(mark);
                }
            }
        }
    };

    /**
     * A red ball that marks the last spot that was "hit" by the "shot".
     */
    private void initMark() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
    }

    /**
     * A red ball that marks the last spot that was "hit" by the "shot".
     */
    private void initMarkP() {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        markP = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Green);
        markP.setMaterial(mark_mat);
    }

    public ArrayList<Vector3f> calcConvexHull(ShadowPolygon input, float offset) {
        ArrayList<Vector3f> output = new ArrayList();
        // Step 1: Create a GeometryFactory
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 0);

//        // Step 2: Define input coordinates
//        Coordinate[] coords = new Coordinate[] {
//            new Coordinate(0, 0),
//            new Coordinate(1, 1),
//            new Coordinate(2, 2),
//            new Coordinate(3, 1),
//            new Coordinate(2, 0),
//            new Coordinate(1.5, 0.5)  // inner point (won’t be on the hull)
//        };
        Coordinate[] coords = new Coordinate[input.points.size()];
        for (int i = 0; i < input.points.size(); i++) {
            coords[i] = new Coordinate();
            coords[i].x = input.points.get(i).x;
            coords[i].y = input.points.get(i).z;
            coords[i].z = 0;
        }

        // Step 3: Create a MultiPoint geometry
        org.locationtech.jts.geom.Geometry points = geometryFactory.createMultiPointFromCoords(coords);

        // Step 4: Compute the convex hull
        ConvexHull convexHull = new ConvexHull(points);
        org.locationtech.jts.geom.Geometry hullGeometry = convexHull.getConvexHull();
        Coordinate[] outputCoords = hullGeometry.getCoordinates();
        for (int i = 0; i < outputCoords.length; i++) {
            output.add(new Vector3f((float) (outputCoords[i].x), maxZ + zMargin*maxZ + offset, (float) (outputCoords[i].y)));
        }

        return output;
    }

    public void shootToCorners(ShadowPolygon shadowPolygon,Vector3f origin, ArrayList<Vector3f> corners, int segmentIDX, int buildingIDX) {
        for (int co = 0; co < corners.size(); co++) {
            CollisionResults results = new CollisionResults();
            //markP.setLocalTranslation(m2.quads.get(c).tops[0]);
            Sphere sphere = new Sphere(30, 30, 0.2f);
            Geometry markT = new Geometry("BOOM!", sphere);
            Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat.setColor("Color", ColorRGBA.Yellow);
            markT.setMaterial(mark_mat);
            Vector3f top = new Vector3f(corners.get(co).x, corners.get(co).y, corners.get(co).z);
            top.y = top.y + 0.1f;
            markT.setLocalTranslation(top);
            //System.out.println(m2.quads.get(c).tops[0]);
            shadowables.attachChild(markT);
            Ray ray = new Ray(origin, top.subtract(origin));
            shootables.collideWith(ray, results);
//            System.out.println("----- Collisions? " + results.size() + "-----");
//            for (int i = 0; i < results.size(); i++) {
//                // For each hit, we know distance, impact point, name of geometry.
//                float dist = results.getCollision(i).getDistance();
//                Vector3f pt = results.getCollision(i).getContactPoint();
//                String hit = results.getCollision(i).getGeometry().getName();
//                System.out.println("* Collision #" + i);
//                System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
//            }
            if (results.size() > 0) {
                // The closest collision point is what was truly hit:
                CollisionResult closest = results.getClosestCollision();
                String hitName = results.getClosestCollision().getGeometry().getName();
                //System.out.println(results.getClosestCollision().getGeometry().getName());
                if (hitName.equals("Building_" + buildingIDX)) {
                    Sphere sphereS = new Sphere(30, 30, 0.2f);
                    Geometry markS = new Geometry("BOOM!", sphereS);
                    Material mark_matS = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mark_matS.setColor("Color", ColorRGBA.Orange);
                    markS.setMaterial(mark_matS);
                    markS.setLocalTranslation(corners.get(co));
                    //System.out.println(m2.quads.get(c).tops[0]);
//                    segments.get(segmentIDX).shadowPolygon.points.add(corners.get(co));
                    shadowPolygon.points.add(corners.get(co));
                    shadowables.attachChild(markS);

                    //Shoot another ray to hit the boundary of sky***
                    Ray ray_p = new Ray();
                    ray_p.origin = ray.origin;
                    ray_p.direction = ray.direction;
                    ray_p.direction.y = 0.1f;
                    CollisionResults results_p = new CollisionResults();
                    shootables.collideWith(ray_p, results_p);
//                    System.out.println("----- Collisions? " + results.size() + "-----");
//                    for (int i = 0; i < results_p.size(); i++) {
//                        // For each hit, we know distance, impact point, name of geometry.
//                        float dist = results_p.getCollision(i).getDistance();
//                        Vector3f pt = results_p.getCollision(i).getContactPoint();
//                        String hit = results_p.getCollision(i).getGeometry().getName();
//                        System.out.println("* Collision #" + i);
//                        System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
//                    }
                }

                // Let's interact - we mark the hit with a red dot.
//                mark.setLocalTranslation(closest.getContactPoint());
//                rootNode.attachChild(mark);
            }
            //else {
            // No hits? Then remove the red mark.
//                rootNode.detachChild(mark);
            //}
        }
    }
    
    public void polygonToTextureVisible(int size){
        Geometry quad = new Geometry("box", new Box(2, 2, 2));
        quad.setLocalTranslation(0, 1f, 0);
        //Texture offTex = setupOffscreenView();
        
        Camera offCamera = new Camera(512, 512);
        offCamera.setParallelProjection(true);
        //offCamera.setFrustum(-50, 50, -(maxX-minX)/2+7, (maxX-minX)/2+8, (maxY-minY)/2+12, -(maxY-minY)/2+11);
        offCamera.setFrustum(-50, 50, minX, maxX, maxY, minY);
        
        ViewPort offView = renderManager.createPreView("Offscreen View", offCamera);
        offView.setClearFlags(true, true, true);
        offView.setBackgroundColor(ColorRGBA.DarkGray);

        // create offscreen framebuffer
        FrameBuffer offBuffer = new FrameBuffer(512, 512, 1);

        //setup framebuffer's cam
        //offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, 0f));
        //offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);
        
//        Vector3f lookDirection = Vector3f.UNIT_Z;
//        Vector3f upDirection = Vector3f.UNIT_Y; 
        offCamera.lookAt(offCamera.getLocation().add(new Vector3f(0f,1f,0f)), new Vector3f(0,0f,1f));

        //setup framebuffer's texture
        Texture2D offTex = new Texture2D(512, 512, Image.Format.RGBA8);
        offTex.setMinFilter(Texture.MinFilter.Trilinear);
        offTex.setMagFilter(Texture.MagFilter.Bilinear);

        //setup framebuffer to use texture
        offBuffer.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
        offBuffer.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(offTex));

        //set viewport to render to offscreen framebuffer
        offView.setOutputFrameBuffer(offBuffer);
        
        offView.attachScene(shadowables);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", offTex);
//        mat.setColor("Color", ColorRGBA.Red);
        quad.setMaterial(mat);
        rootNode.attachChild(quad);
    }

}
