/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testPrototypes;

import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Texture;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author usero
 */
public class OffScreenIntersectionProcessor implements SceneProcessor {

    private ViewPort viewPort;
    private RenderManager renderManager;
    public FrameBuffer myFb;
    public Texture texture;
    public boolean completed = false;
    public Texture text1;
    public Texture text2;

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        this.renderManager = rm;
        this.viewPort = vp;
    }

    @Override
    public void reshape(ViewPort vp, int i, int i1) {

    }

    @Override
    public boolean isInitialized() {
        return renderManager != null;
    }

    @Override
    public void preFrame(float tpf) {

    }

    @Override
    public void postQueue(RenderQueue rq) {

    }

    @Override
    public void postFrame(FrameBuffer fb) {
        if (fb != null && fb.getColorTarget() != null) {
            texture = fb.getColorTarget().getTexture();
            myFb = fb;
            completed = true;
        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void setProfiler(AppProfiler ap) {

    }

    public static boolean isIntersect(ByteBuffer bgraBuf) {
        ArrayList<ArrayList<ColorPixel>> output = new ArrayList();
//        byte[] cpuArray=bgraBuf.array();
        byte[] cpuArray = new byte[1 * 4];

        // copy native memory to java memory
        bgraBuf.clear();
        bgraBuf.get(cpuArray);
        bgraBuf.clear();

        byte b1 = cpuArray[0 + 0];
        //byte g1 = cpuArray[0 + 1];
        //byte r1 = cpuArray[0 + 2];
        //byte a1 = cpuArray[0 + 3];
        //ColorPixel cp = new ColorPixel();
        //cp.red = b1;
        //cp.green = g1;
        //cp.blue = r1;
        //cp.alpha = a1;
        if(b1 == -1) {
            return true;
        } else {
            return false;
        }
        //return output;
    }

}
