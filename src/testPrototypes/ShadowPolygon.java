/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testPrototypes;

import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author usero
 */
public class ShadowPolygon {
    public ArrayList<Vector3f> points=new ArrayList();
    public VisibleMap visibleMap=new VisibleMap();
    public ShadowPolygon(){}
    public ShadowPolygon(ArrayList<Vector3f> p_points){points=p_points;}
}
