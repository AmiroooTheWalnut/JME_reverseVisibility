/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testPrototypes;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import java.nio.ByteBuffer;

/**
 *
 * @author usero
 */
public class TextureIntersectionDetailed extends SimpleApplication {

    public Texture2D offTex;
    Geometry offBoxMain;
    public Geometry offBox;
    public FrameBuffer offBuffer;
    OffScreenIntersectionProcessor osip;
    public OffScreenNodeProcessor osnp;

    Texture tex1;
    Texture tex2;
    Material material;

    public static void main(String[] args) {
        TextureIntersectionDetailed app = new TextureIntersectionDetailed();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(3, 3, 3));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        Geometry quad = new Geometry("box", new Box(1, 1, 1));
        Camera offCamera = new Camera(512, 512);

        ViewPort offView = renderManager.createPreView("Offscreen View", offCamera);
        offView.setClearFlags(true, true, true);
        offView.setBackgroundColor(ColorRGBA.DarkGray);

        FrameBuffer offBuffer = new FrameBuffer(512, 512, 1);

        offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, -5f));
        offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);

        offTex = new Texture2D(512, 512, Image.Format.RGBA8);
        offTex.setMinFilter(Texture.MinFilter.Trilinear);
        offTex.setMagFilter(Texture.MagFilter.Bilinear);

        offBuffer.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
        offBuffer.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(offTex));

        osip = new OffScreenIntersectionProcessor();
        offView.addProcessor(osip);
        offView.setOutputFrameBuffer(offBuffer);

        Box boxMesh = new Box(1, 1, 1);
        Material material = new Material(assetManager, "Materials/IntersectionMaterialDetailed.j3md");
        offBoxMain = new Geometry("box", boxMesh);
        Texture img1 = assetManager.loadTexture("Textures/T8.png");
        Texture img2 = assetManager.loadTexture("Textures/T10.png");
        material.setTexture("TextureA", img1);
        material.setTexture("TextureB", img2);

        offBoxMain.setMaterial(material);

        offView.attachScene(offBoxMain);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        offTex.setWrap(Texture.WrapAxis.T, Texture.WrapMode.EdgeClamp);
        offTex.setWrap(Texture.WrapMode.EdgeClamp);
        mat.setTexture("ColorMap", offTex);
        quad.setMaterial(mat);
        rootNode.attachChild(quad);
    }

    public void changeTextures(Texture pt1, Texture pt2) {
        material.setTexture("TextureA", pt1);
        material.setTexture("TextureB", pt2);
    }

    public void setup(RenderManager rm, AssetManager am, OffScreenNodeProcessor osip_p, int textSize) {
        //Camera camera = new Camera(512, 512);
        //camera.setLocation(new Vector3f(3, 3, 3));
        //camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        //Geometry quad = new Geometry("box", new Box(1, 1, 1));
        Camera offCamera = new Camera(textSize, textSize);

        ViewPort offView = rm.createPreView("Offscreen View", offCamera);
        offView.setClearFlags(true, true, true);
        offView.setBackgroundColor(ColorRGBA.DarkGray);

        //offBuffer = new FrameBuffer(textSize, textSize, 1);
        osip_p.myFb = new FrameBuffer(textSize, textSize, 1);

        offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, -5f));
        offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);

        //Texture2D offTexture = new Texture2D(textSize, textSize, Image.Format.RGBA8);
        osip_p.texture = new Texture2D(textSize, textSize, Image.Format.RGBA8);
        osip_p.texture.setMinFilter(Texture.MinFilter.Trilinear);
        osip_p.texture.setMagFilter(Texture.MagFilter.Bilinear);

        osip_p.myFb.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
        osip_p.myFb.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(osip_p.texture));
        //osip_p.myFb.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
        //osip_p.myFb.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(osip_p.texture));

        //osip = new OffScreenIntersectionProcessor();
        offView.setOutputFrameBuffer(osip_p.myFb);
        offView.addProcessor(osip_p);
        osip_p.completed=false;

        Box boxMesh = new Box(1, 1, 1);
        material = new Material(am, "Materials/IntersectionMaterialDetailed.j3md");
        offBox = new Geometry("box", boxMesh);
        tex1 = am.loadTexture("Textures/T1.png");
        tex2 = am.loadTexture("Textures/T2.png");
        material.setTexture("TextureA", tex1);
        material.setTexture("TextureB", tex2);
        //material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);

        offBox.setMaterial(material);

        offView.attachScene(offBox);
        osip_p.completed=false;
        osnp=osip_p;
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (offBox != null) {
            offBox.updateLogicalState(tpf);
            offBox.updateGeometricState();
        }
        if (offBoxMain != null) {
            offBoxMain.updateLogicalState(tpf);
            offBoxMain.updateGeometricState();
        }
//        if (osip.myFb != null) {
//            ByteBuffer buf = ByteBuffer.allocateDirect(512 * 512 * 4); // RGBA = 4 bytes per pixel
//            renderer.readFrameBuffer(osip.myFb, buf); // Copy from GPU to CPU
//            System.out.println(OffScreenIntersectionProcessor.isIntersect(buf));
//        }
    }
}
