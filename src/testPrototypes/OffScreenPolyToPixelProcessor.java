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
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.Screenshots;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author usero
 */
public class OffScreenPolyToPixelProcessor implements SceneProcessor {

    //public boolean isRendered = false;
    public StreetSegment parent;
    private RenderManager renderManager;
    private ViewPort viewPort;
    public FrameBuffer myFb;
    public Texture2D texture;
    public boolean completed = false;
    public boolean isSetup=false;
    public ArrayList<ArrayList<ColorPixel>> cpuImg;

    public OffScreenPolyToPixelProcessor(StreetSegment p) {
        parent = p;
    }

    public OffScreenPolyToPixelProcessor(FrameBuffer fb) {
        this.myFb = fb;
    }

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        this.renderManager = rm;
        this.viewPort = vp;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void reshape(ViewPort vp, int i, int i1) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isInitialized() {
        return renderManager != null;
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void preFrame(float tpf) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void postQueue(RenderQueue rq) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void postFrame(FrameBuffer fb) {
        if (fb != null && fb.getColorTarget() != null) {
            //fb.getColorTarget().getTexture()

            texture = (Texture2D) fb.getColorTarget().getTexture();
            myFb = fb;

//            ByteBuffer buf = ByteBuffer.allocateDirect(512 * 512 * 4); // RGBA = 4 bytes per pixel
//            renderManager.getRenderer().readFrameBuffer(fb, buf); // Copy from GPU to CPU
//
//            BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_4BYTE_ABGR);
//            Screenshots.convertScreenShot(buf, image);
//
//            try {
//                ImageIO.write(image, "png", new File("test1"));
//                System.out.println("Saved texture/framebuffer to " + "test1");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            completed = true;
        }
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void cleanup() {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setProfiler(AppProfiler ap) {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static ArrayList<ArrayList<ColorPixel>> convertTo2DImage(ByteBuffer bgraBuf, int width, int height) {
        ArrayList<ArrayList<ColorPixel>> output = new ArrayList();
//        byte[] cpuArray=bgraBuf.array();
        byte[] cpuArray = new byte[width * height * 4];

        // copy native memory to java memory
        bgraBuf.clear();
        bgraBuf.get(cpuArray);
        bgraBuf.clear();

//        int width  = wr.getWidth();
//        int height = wr.getHeight();
        // flip the components the way AWT likes them
        // calculate half of height such that all rows of the array are written to
        // e.g. for odd heights, write 1 more scanline
        for (int y = 0; y < height; y++) {
            ArrayList<ColorPixel> row = new ArrayList();
            output.add(row);
//            String lrow="";
            for (int x = 0; x < width; x++) {
                int inPtr = ((height - y - 1) * width + x) * 4;
//                int outPtr = ((height-y-1) * width + x) * 4;

                byte b1 = cpuArray[inPtr + 0];
                byte g1 = cpuArray[inPtr + 1];
                byte r1 = cpuArray[inPtr + 2];
                byte a1 = cpuArray[inPtr + 3];
                ColorPixel cp = new ColorPixel();
                cp.red = b1;
                cp.green = g1;
                cp.blue = r1;
                cp.alpha = a1;
                row.add(cp);
//                lrow=lrow+r1;
            }
//            System.out.println(lrow);
        }
        return output;
    }
    
    public static ArrayList<ArrayList<ColorPixel>> convertTo2DImage(Texture2D tex, int width, int height) {
        ArrayList<ArrayList<ColorPixel>> output = new ArrayList();
        
        ByteBuffer bgraBuf = tex.getImage().getData(0);
        
        
//        byte[] cpuArray=bgraBuf.array();
        byte[] cpuArray = new byte[width * height * 4];

        // copy native memory to java memory
        bgraBuf.clear();
        bgraBuf.get(cpuArray);
        bgraBuf.clear();

//        int width  = wr.getWidth();
//        int height = wr.getHeight();
        // flip the components the way AWT likes them
        // calculate half of height such that all rows of the array are written to
        // e.g. for odd heights, write 1 more scanline
        for (int y = 0; y < height; y++) {
            ArrayList<ColorPixel> row = new ArrayList();
            output.add(row);
//            String lrow="";
            for (int x = 0; x < width; x++) {
                int inPtr = ((height - y - 1) * width + x) * 4;
//                int outPtr = ((height-y-1) * width + x) * 4;

                byte b1 = cpuArray[inPtr + 0];
                byte g1 = cpuArray[inPtr + 1];
                byte r1 = cpuArray[inPtr + 2];
                byte a1 = cpuArray[inPtr + 3];
                ColorPixel cp = new ColorPixel();
                cp.red = b1;
                cp.green = g1;
                cp.blue = r1;
                cp.alpha = a1;
                row.add(cp);
//                lrow=lrow+r1;
            }
//            System.out.println(lrow);
        }
        return output;
    }
    
    public static Texture2D deepCopyTextureFromFrameBuffer(ByteBuffer bgraBuf, int width, int height) {
        //ArrayList<ArrayList<ColorPixel>> output = new ArrayList();
//        byte[] cpuArray=bgraBuf.array();
        byte[] cpuArray = new byte[width * height * 4];
        byte[] cpuArray2 = new byte[width * height * 4];

        // copy native memory to java memory
        bgraBuf.clear();
        bgraBuf.rewind();
        bgraBuf.get(cpuArray);
        bgraBuf.clear();
        
        

//        int width  = wr.getWidth();
//        int height = wr.getHeight();
        // flip the components the way AWT likes them
        // calculate half of height such that all rows of the array are written to
        // e.g. for odd heights, write 1 more scanline
        for (int y = 0; y < height; y++) {
            //ArrayList<ColorPixel> row = new ArrayList();
            //output.add(row);
//            String lrow="";
            for (int x = 0; x < width; x++) {
                int inPtr = ((height - y - 1) * width + x) * 4;
//                int outPtr = ((height-y-1) * width + x) * 4;

                byte b1 = cpuArray[inPtr + 0];
                byte g1 = cpuArray[inPtr + 1];
                byte r1 = cpuArray[inPtr + 2];
                byte a1 = cpuArray[inPtr + 3];
                
                cpuArray2[inPtr + 0]=b1;
                cpuArray2[inPtr + 1]=g1;
                cpuArray2[inPtr + 2]=r1;
                cpuArray2[inPtr + 3]=a1;
//                if(a1!=0||r1!=0){
//                    System.out.println("!!!");
//                }
                
                //ColorPixel cp = new ColorPixel();
                //cp.red = b1;
                //cp.green = g1;
                //cp.blue = r1;
                //cp.alpha = a1;
                //row.add(cp);
//                lrow=lrow+r1;
            }
//            System.out.println(lrow);
        }
        ByteBuffer copied = ByteBuffer.allocateDirect(width * height * 4);
        //copied.reset();
        copied.rewind();
        copied.put(cpuArray2);
        //ByteBuffer copied=ByteBuffer.wrap(cpuArray);
        Image img=new Image(Image.Format.RGBA8,width,height, copied, null, ColorSpace.Linear);
        
        return new Texture2D(img);
    }

    /**
     * Flips the image along the Y axis and copies BGRA to BGRA
     *
     * @param bgraBuf (not null, modified)
     * @param out (not null)
     */
    public static void convertScreenShot(ByteBuffer bgraBuf, BufferedImage out) {
        WritableRaster wr = out.getRaster();
        DataBufferByte db = (DataBufferByte) wr.getDataBuffer();

        byte[] cpuArray = db.getData();

        // copy native memory to java memory
        bgraBuf.clear();
        bgraBuf.get(cpuArray);
        bgraBuf.clear();

        int width = wr.getWidth();
        int height = wr.getHeight();

        // flip the components the way AWT likes them
        // calculate half of height such that all rows of the array are written to
        // e.g. for odd heights, write 1 more scanline
        int heightdiv2ceil = height % 2 == 1 ? (height / 2) + 1 : height / 2;
        for (int y = 0; y < heightdiv2ceil; y++) {
            for (int x = 0; x < width; x++) {
                int inPtr = (y * width + x) * 4;
                int outPtr = ((height - y - 1) * width + x) * 4;

                byte b1 = cpuArray[inPtr + 0];
                byte g1 = cpuArray[inPtr + 1];
                byte r1 = cpuArray[inPtr + 2];
                byte a1 = cpuArray[inPtr + 3];

                byte b2 = cpuArray[outPtr + 0];
                byte g2 = cpuArray[outPtr + 1];
                byte r2 = cpuArray[outPtr + 2];
                byte a2 = cpuArray[outPtr + 3];

                cpuArray[outPtr + 0] = a1;
                cpuArray[outPtr + 1] = r1;
                cpuArray[outPtr + 2] = g1;
                cpuArray[outPtr + 3] = b1;

                cpuArray[inPtr + 0] = a2;
                cpuArray[inPtr + 1] = r2;
                cpuArray[inPtr + 2] = g2;
                cpuArray[inPtr + 3] = b2;
            }
        }
    }

}
