/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testPrototypes;

import java.util.Comparator;

/**
 *
 * @author usero
 */
public class StreetSegmentComparatorLat implements Comparator<StreetSegment> {

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
