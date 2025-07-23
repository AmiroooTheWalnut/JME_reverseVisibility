/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testPrototypes;

import com.jme3.math.Vector3f;
import esmaieeli.gisFastLocationOptimization.GIS3D.LocationNode;
import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author usero
 */
public class StreetSegment implements Comparator<StreetSegment> {
//    double rawLatStart;
//    double rawLonStart;
//    double rawLatEnd;
//    double rawLonEnd;
//    Vector3f start;
//    Vector3f end;
    //ShadowPolygon shadowPolygon=new ShadowPolygon();
    public OffScreenPolyToPixelProcessor osptpp=new OffScreenPolyToPixelProcessor(this);
    public ArrayList<SegmentNode> startIntersectingSegments=new ArrayList();
    public ArrayList<SegmentNode> endIntersectingSegments=new ArrayList();
    public long streetID;
    public int myIndex;
//    long startID;
//    long endID;
//    LocationNode startLN;
//    LocationNode endLN;
    
    public SegmentNode start=new SegmentNode(this);
    public SegmentNode end=new SegmentNode(this);
    public boolean isInitial=false;

    @Override
    public int compare(StreetSegment o1, StreetSegment o2) {
        double minO1Lat = Math.min(o1.start.rawLat, o1.end.rawLat);
        double minO2Lat = Math.min(o2.start.rawLat, o2.end.rawLat);
        if (minO1Lat == minO2Lat)
            return 0;
        else if (minO1Lat > minO2Lat)
            return 1;
        else
            return -1;
    }
}
