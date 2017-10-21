package com.vraj.polygonalgeofences;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vrajdelhivala on 10/19/17.
 */

public class GeoFence implements Serializable {
    List<double[]> geoFencePoints = new ArrayList<>();
    String imgPath = "";
    String geoFenceName = "";

    public GeoFence(List<double[]> list,String ipath,String gname){
        this.geoFencePoints=list;
        this.imgPath=ipath;
        this.geoFenceName=gname;
    }

}
