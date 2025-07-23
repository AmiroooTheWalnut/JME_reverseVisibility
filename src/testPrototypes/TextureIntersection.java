/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testPrototypes;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
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
public class TextureIntersection extends SimpleApplication {

    Texture2D offTex;
    Geometry offBox;
    OffScreenIntersectionProcessor osip;

    public static void main(String[] args) {
        TextureIntersection app = new TextureIntersection();
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
        //offView.setBackgroundColor(ColorRGBA.DarkGray);
        offView.setBackgroundColor(ColorRGBA.fromRGBA255(0, 0, 0, 0));

        FrameBuffer offBuffer = new FrameBuffer(1, 1, 1);

        offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, -5f));
        offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);

        offTex = new Texture2D(1, 1, Image.Format.RGBA8);
        offTex.setMinFilter(Texture.MinFilter.Trilinear);
        offTex.setMagFilter(Texture.MagFilter.Bilinear);

        offBuffer.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
        offBuffer.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(offTex));

        osip = new OffScreenIntersectionProcessor();
        offView.addProcessor(osip);
        offView.setOutputFrameBuffer(offBuffer);

        Box boxMesh = new Box(1, 1, 1);
        Material material = new Material(assetManager, "Materials/IntersectionMaterial.j3md");
        offBox = new Geometry("box", boxMesh);
        Texture img1 = assetManager.loadTexture("Textures/T6.png");
        Texture img2 = assetManager.loadTexture("Textures/T8.png");
        material.setTexture("TextureA", img1);
        material.setTexture("TextureB", img2);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
        
        material.setTransparent(true);
        material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); // activate transparency
        offBox.setQueueBucket(Bucket.Transparent);

        offBox.setMaterial(material);

        offView.attachScene(offBox);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        offTex.setWrap(Texture.WrapAxis.T, Texture.WrapMode.EdgeClamp);
        offTex.setWrap(Texture.WrapMode.EdgeClamp);
        mat.setTexture("ColorMap", offTex);
        quad.setMaterial(mat);
        rootNode.attachChild(quad);
    }

    public static void intersectTextureSetup(RenderManager rm, AssetManager am, OffScreenIntersectionProcessor osip_p) {
        //Camera camera = new Camera(512, 512);
        //camera.setLocation(new Vector3f(3, 3, 3));
        //camera.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        Geometry quad = new Geometry("boxInrsection", new Box(1, 1, 1));
        Camera offCamera = new Camera(512, 512);

        ViewPort offView = rm.createPreView("Offscreen View", offCamera);
        offView.setClearFlags(true, true, true);
        offView.setBackgroundColor(ColorRGBA.DarkGray);

        FrameBuffer offBuffer = new FrameBuffer(1, 1, 1);

        offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(0f, 0f, -5f));
        offCamera.lookAt(new Vector3f(0f, 0f, 0f), Vector3f.UNIT_Y);

        Texture2D offTexture = new Texture2D(1, 1, Image.Format.RGBA8);
        offTexture.setMinFilter(Texture.MinFilter.Trilinear);
        offTexture.setMagFilter(Texture.MagFilter.Bilinear);

        offBuffer.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
        offBuffer.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(offTexture));

        //osip = new OffScreenIntersectionProcessor();
        offView.addProcessor(osip_p);
        offView.setOutputFrameBuffer(offBuffer);

        Box boxMesh = new Box(1, 1, 1);
        Material material = new Material(am, "Materials/IntersectionMaterial.j3md");
        Geometry offBox = new Geometry("box", boxMesh);
        Texture img1 = am.loadTexture("Textures/T1.png");
        Texture img2 = am.loadTexture("Textures/T2.png");
        material.setTexture("TextureA", img1);
        material.setTexture("TextureB", img2);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);

        offBox.setMaterial(material);

        offView.attachScene(offBox);
    }

    @Override
    public void simpleUpdate(float tpf) {
        offBox.updateLogicalState(tpf);
        offBox.updateGeometricState();
        if (osip.myFb != null) {
            ByteBuffer buf = ByteBuffer.allocateDirect(512 * 512 * 4); // RGBA = 4 bytes per pixel
            renderer.readFrameBuffer(osip.myFb, buf); // Copy from GPU to CPU
            System.out.println(OffScreenIntersectionProcessor.isIntersect(buf));
        }
//        if (offTex.getImage().getData(0) != null) {
//            if (offTex.getImage().getData(0).hasArray() == true) {
//                ByteBuffer data = offTex.getImage().getData(0);
//                data.rewind();
//                int r = data.get() & 0xFF;
//                int g = data.get() & 0xFF;
//                int b = data.get() & 0xFF;
//                System.out.println("!!!");
//            }
//        }
    }
}
