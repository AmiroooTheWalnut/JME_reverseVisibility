/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testPrototypes;

import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

public class ConvexHullExample {
    public static void main(String[] args) {
        // Step 1: Create a GeometryFactory
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 0);

        // Step 2: Define input coordinates
        Coordinate[] coords = new Coordinate[] {
            new Coordinate(0, 0),
            new Coordinate(1, 1),
            new Coordinate(2, 2),
            new Coordinate(3, 1),
            new Coordinate(2, 0),
            new Coordinate(1.5, 0.5)  // inner point (won’t be on the hull)
        };

        // Step 3: Create a MultiPoint geometry
        Geometry points = geometryFactory.createMultiPointFromCoords(coords);

        // Step 4: Compute the convex hull
        ConvexHull convexHull = new ConvexHull(points);
        Geometry hullGeometry = convexHull.getConvexHull();

        // Step 5: Print the result
        System.out.println("Convex Hull Geometry: " + hullGeometry);
    }
}
