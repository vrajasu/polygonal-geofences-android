package com.vraj.polygonalgeofences;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;

/**
 * Created by vrajdelhivala on 10/20/17.
 */

public class PolygonTest
{

    public static boolean PointIsInRegion(double x, double y, List<double[]> list)
    {
        int crossings = 0;

        LatLng point = new LatLng (x, y);
        int count = list.size();


        // for each edge
        for (int i=0; i < count; i++)
        {
            LatLng a= new LatLng(list.get(i)[0],list.get(i)[1]);

            int j = i + 1;
            if (j >= count)
            {
                j = 0;
            }
            LatLng b = new LatLng(list.get(j)[0],list.get(j)[1]);

            if (RayCrossesSegment(point, a, b))
            {
                crossings++;
            }
        }
        return (crossings % 2 == 1);
    }


    public static boolean RayCrossesSegment(LatLng point, LatLng a, LatLng b)
    {

        double px = point.longitude;
        double py = point.latitude;
        double ax = a.longitude;
        double ay = a.latitude;
        double bx = b.longitude;
        double by = b.latitude;
        if (ay > by)
        {
            ax = b.longitude;
            ay = b.latitude;
            bx = a.longitude;
            by = a.latitude;
        }

        if (px < 0) { px += 360; };
        if (ax < 0) { ax += 360; };
        if (bx < 0) { bx += 360; };

        if (py == ay || py == by) py += 0.00000001;
        if ((py > by || py < ay) || (px > Math.max(ax, bx))) return false;
        if (px < Math.min(ax, bx)) return true;

        double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Double.MAX_VALUE;
        double blue = (ax != px) ? ((py - ay) / (px - ax)) :Double.MAX_VALUE;
        return (blue >= red);
    }
}
