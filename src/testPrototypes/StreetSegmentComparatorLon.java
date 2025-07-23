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
public class StreetSegmentComparatorLon implements Comparator<StreetSegment> {

    @Override
    public int compare(StreetSegment o1, StreetSegment o2) {
        double minO1Lon = Math.min(o1.start.rawLon, o1.end.rawLon);
        double minO2Lon = Math.min(o2.start.rawLon, o2.end.rawLon);
        if (minO1Lon == minO2Lon)
            return 0;
        else if (minO1Lon > minO2Lon)
            return 1;
        else
            return -1;
    }
    
}
