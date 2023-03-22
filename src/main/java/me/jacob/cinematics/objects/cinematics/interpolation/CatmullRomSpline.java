package me.jacob.cinematics.objects.cinematics.interpolation;

import org.bukkit.util.Vector;

import java.util.List;

public class CatmullRomSpline {

    public static Vector interpolate(double t, List<Vector> controlPoints) {
        int n = controlPoints.size() - 1;

        double segmentLength = 1.0 / n;
        int segmentIndex = (int) (t / segmentLength);

        if (segmentIndex >= n) {
            segmentIndex = n - 1;
        }

        double localT = (t - segmentIndex * segmentLength) / segmentLength;

        if (segmentIndex + 3 >= controlPoints.size()) {
            return null;
        }

        Vector p0 = controlPoints.get(segmentIndex);
        Vector p1 = controlPoints.get(segmentIndex + 1);
        Vector p2 = controlPoints.get(segmentIndex + 2);
        Vector p3 = controlPoints.get(segmentIndex + 3);

        double t2 = localT * localT;
        double t3 = t2 * localT;

        double a = -0.5 * t3 + t2 - 0.5 * localT;
        double b = 1.5 * t3 - 2.5 * t2 + 1.0;
        double c = -1.5 * t3 + 2.0 * t2 + 0.5 * localT;
        double d = 0.5 * t3 - 0.5 * t2;

        double x = a * p0.getX() + b * p1.getX() + c * p2.getX() + d * p3.getX();
        double y = a * p0.getY() + b * p1.getY() + c * p2.getY() + d * p3.getY();
        double z = a * p0.getZ() + b * p1.getZ() + c * p2.getZ() + d * p3.getZ();

        return new Vector(x, y, z);
    }

}