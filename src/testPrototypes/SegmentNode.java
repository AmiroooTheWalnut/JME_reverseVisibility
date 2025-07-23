/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testPrototypes;

import com.jme3.math.Vector3f;
import com.jme3.texture.Texture;
import esmaieeli.gisFastLocationOptimization.GIS3D.LocationNode;
import java.util.ArrayList;

/**
 *
 * @author usero
 */
public class SegmentNode {

    public double rawLat;
    public double rawLon;
    public Vector3f location;
    public long id;
    public LocationNode locationNode;
    public StreetSegment parent;
    public StreetSegment parentOther;//After removing duplicates
    public ArrayList<StreetSegment> parentOtherFromStart = new ArrayList();
    public ArrayList<StreetSegment> parentOtherFromEnd = new ArrayList();
    public ArrayList<SegmentNode> duplicates = new ArrayList();
    public OffScreenNodeProcessor beta=new OffScreenNodeProcessor(this);
    public boolean isStart=false;
    public boolean isChecked=false;
    public boolean isSkipped=false;
    
    public int guardNumber=0;

    SegmentNode(StreetSegment parent_p) {
        parent = parent_p;
    }
    
    SegmentNode(SegmentNode sn, StreetSegment otherParent){
        rawLat=sn.rawLat;
        rawLon=sn.rawLon;
        location=sn.location;
        id=sn.id;
        locationNode=sn.locationNode;
        parent=sn.parent;
        parentOther=otherParent;
    }

}
