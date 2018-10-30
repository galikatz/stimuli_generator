package stimuli;
import java.awt.*;
import java.util.*;
import java.util.List;

class ConvexHullCalculator {

    // To find orientation of ordered triplet (p, q, r).
    // The function returns following values
    // 0 --> p, q and r are colinear
    // 1 --> Clockwise
    // 2 --> Counterclockwise
    public static int orientation(Point p, Point q, Point r)
    {
        int val = (q.y - p.y) * (r.x - q.x) -
                (q.x - p.x) * (r.y - q.y);

        if (val == 0) return 0; // collinear
        return (val > 0)? 1: 2; // clock or counterclock wise
    }

    // Prints convex hull of a set of n points.
    public static List<Point> convexHull(Point points[])
    {
        int n = points.length;
        // There must be at least 3 points
        if (n < 3) return null;

        // Initialize Result
        List<Point> hull = new ArrayList<>();

        // Find the leftmost point
        int l = 0;
        for (int i = 1; i < n; i++)
            if (points[i].x < points[l].x)
                l = i;

        // Start from leftmost point, keep moving
        // counterclockwise until reach the start point
        // again. This loop runs O(h) times where h is
        // number of points in result or output.
        int p = l, q;
        do
        {
            // Add current point to result
            hull.add(points[p]);

            // Search for a point 'q' such that
            // orientation(p, x, q) is counterclockwise
            // for all points 'x'. The idea is to keep
            // track of last visited most counterclock-
            // wise point in q. If any point 'i' is more
            // counterclock-wise than q, then update q.
            q = (p + 1) % n;

            for (int i = 0; i < n; i++)
            {
                // If i is more counterclockwise than
                // current q, then update q
                if (orientation(points[p], points[i], points[q])
                        == 2)
                    q = i;
            }

            // Now q is the most counterclockwise with
            // respect to p. Set p as q for next iteration,
            // so that q is added to result 'hull'
            p = q;

        } while (p != l); // While we don't come to first
        // point

        //return result
        return hull;
    }

    public static double convexHullLength(List<Point> pointsOrder){
        Point[] points = pointsOrder.toArray(new Point[pointsOrder.size()]);
        double d = 0.0;
        for(int i=0; i<points.length; i++){
            Point p1 = points[i];
            Point p2;
            if(i+1 == points.length){
                p2=points[0];
            }else{
                 p2 = points[i+1];
            }
            d+=Math.sqrt(Math.pow(p2.getX()-p1.getX(),2)+(Math.pow(p2.getY()-p1.getY(),2)));
        }
        return d;
    }

    /* Driver program to test above function */
    public static void main(String[] args)
    {
        Point points[] = new Point[7];
        points[0]=new Point(0, 3);
        points[1]=new Point(2, 3);
        points[2]=new Point(1, 1);
        points[3]=new Point(2, 1);
        points[4]=new Point(3, 0);
        points[5]=new Point(0, 0);
        points[6]=new Point(3, 3);

        List<Point> pointsOrder = convexHull(points);
        // Print Result
        for (Point temp : pointsOrder)
            System.out.println("(" + temp.x + ", " +
                    temp.y + ")");

        double d = convexHullLength(pointsOrder);
        System.out.println(d);
    }
}

