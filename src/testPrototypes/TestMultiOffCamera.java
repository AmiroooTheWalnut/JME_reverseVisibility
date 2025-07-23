/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testPrototypes;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

/**
 *
 * @author usero
 */
public class TestMultiOffCamera extends SimpleApplication {
    
    private Geometry offBox;
    private float angle = 0;
    private ViewPort offView;
    
    public Texture setupOffscreenView(){
        Camera offCamera = new Camera(512, 512);

        offView = renderManager.createPreView("Offscreen View", offCamera);
        offView.setClearFlags(true, true, true);
        offView.setBackgroundColor(ColorRGBA.DarkGray);

        // create offscreen framebuffer
        FrameBuffer offBuffer = new FrameBuffer(512, 512, 1);

        //setup framebuffer's cam
        offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, -5f));
        offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);

        //setup framebuffer's texture
        Texture2D offTex = new Texture2D(512, 512, Image.Format.RGBA8);
        offTex.setMinFilter(Texture.MinFilter.Trilinear);
        offTex.setMagFilter(Texture.MagFilter.Bilinear);

        //setup framebuffer to use texture
        offBuffer.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
        offBuffer.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(offTex));

        //set viewport to render to offscreen framebuffer
        offView.setOutputFrameBuffer(offBuffer);

        // setup framebuffer's scene
        Box boxMesh = new Box(1, 1, 1);
        Material material = assetManager.loadMaterial("Interface/Logo/Logo.j3m");
        offBox = new Geometry("box", boxMesh);
        offBox.setMaterial(material);

        // attach the scene to the viewport to be rendered
        offView.attachScene(offBox);
        
        return offTex;
    }

    @Override
    public void simpleInitApp() {
        //cam.setLocation(new Vector3f(3, 3, 3));
        //cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        // Setup first view
        viewPort.setBackgroundColor(ColorRGBA.Blue);
        cam.setViewPort(.66f, 1f, 0f, 0.5f);
        cam.setLocation(new Vector3f(0,0,0));
        //cam.setRotation(new Quaternion(-0.07680723f, 0.92299235f, -0.2564353f, -0.27645364f));
 
//        Texture offTex = setupOffscreenView();
//        Spatial sky = SkyFactory.createSky(assetManager, offTex, 
//                EnvMapType.CubeMap);
//        rootNode.attachChild(sky);
    }
}
