package testPrototypes;

import com.jme3.app.SimpleApplication;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.TextureCubeMap;
import com.jme3.util.SkyFactory;
//import com.jme3.
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.system.AppSettings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import jme3tools.converters.ImageToAwt;

public class TestEnvCam extends SimpleApplication {

    private TextureCubeMap cubeMap;
    private boolean captured = false;
    public EnvironmentCamera envCam;

    public static void main(String[] args) {
        TestEnvCam app = new TestEnvCam();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(800, 600);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        cam.setLocation(new Vector3f(0, 0, 5));

        // Add something visible to capture
        Geometry sphere = new Geometry("Sphere", new Sphere(32, 32, 1f));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sphere.setMaterial(mat);
        rootNode.attachChild(sphere);

        // Set up a 256x256 cube map
        cubeMap = new TextureCubeMap(256, 256, Image.Format.RGBA8);

        // Set up environment camera (renders all 6 directions)
        envCam = new EnvironmentCamera(256, cam.getLocation());
        stateManager.attach(envCam);

        // Link the cube map to the env cam (this step is usually done internally,
        // but we're wiring it manually for control)
        envCam.initialize(stateManager, this);
        envCam.setPosition(cam.getLocation());
        //envCam.debugEnv
        //envCam.setTargetFrameBuffer(cubeMap);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //if (!captured && cubeMap.getImage() != null && cubeMap.getImage().getData(0) != null) {
        if (!captured && envCam.debugEnv!=null) {
            saveCubeMapFaces(cubeMap);
            captured = true;
            System.out.println("Cube map captured and saved.");
            stop(); // Exit after saving
        }
    }

    private void saveCubeMapFaces(TextureCubeMap cubeMap) {
        for (TextureCubeMap.Face face : TextureCubeMap.Face.values()) {
            try {
                TextureCubeMap a = envCam.debugEnv;
                Image image = a.createSimpleClone().getImage();
                //Image image = cubeMap.getImage();
                BufferedImage awtImage = ImageToAwt.convert(image, false, true, face.ordinal());
                File outFile = new File("face_" + face.name() + ".png");
                ImageIO.write(awtImage, "png", outFile);
                System.out.println("Saved: " + outFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}