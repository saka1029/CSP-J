package jp.saka1029.cspj.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Point implements Comparable<Point> {

    public static Point ZERO = new Point(0, 0);
    public static Point ONE = new Point(1, 1);
    public static Point[] NEIGHBORS = {
        new Point(-1, 0), new Point(1, 0), new Point(0, -1), new Point(0, 1)
    };
    
    public final int x, y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point plus(Point p) {
        return new Point(x + p.x, y + p.y);
    }

    public Point minus(Point p) {
        return new Point(x - p.x, y - p.y);
    }
    
    public int deviationSquare(Point p) {
        int x = this.x - p.x;
        int y = this.y - p.y;
        return x * x + y + y;
    }

    public Iterable<Point> neighbors() {
        List<Point> r = new ArrayList<>();
        for (Point o : NEIGHBORS)
            r.add(this.plus(o));
        return r;
    }

    @Override
    public int compareTo(Point o) {
        int r = y - o.y;
        if (r != 0) return r;
        return x - o.x;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point)) return false;
        Point o = (Point)obj;
        return y == o.y && x == o.x;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{x, y});
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

}
