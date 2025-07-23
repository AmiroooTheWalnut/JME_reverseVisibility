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
import com.jme3.texture.Texture2D;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author usero
 */
public class OffScreenNodeProcessor implements SceneProcessor {
    
    public SegmentNode parent;
    private RenderManager renderManager;
    private ViewPort viewPort;
    public FrameBuffer myFb;
    public ByteBuffer byteBuffer;
    public Texture2D texture;
    public Texture2D backupTexture;
    public boolean completed = false;
    public boolean isSetup = false;
    public boolean isTotalBlack = false;
    public ArrayList<ArrayList<ColorPixel>> cpuImg;
    
    public OffScreenNodeProcessor(SegmentNode p) {
        parent=p;
    }

    public OffScreenNodeProcessor(FrameBuffer fb) {
        this.myFb = fb;
    }

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
            texture = (Texture2D) (fb.getColorTarget().getTexture());
            myFb = fb;
            completed = true;
            //isSetup=false;
        }
    }

    @Override
    public void cleanup() {
        
    }

    @Override
    public void setProfiler(AppProfiler ap) {
        
    }
    
    
}
