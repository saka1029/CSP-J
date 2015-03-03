package jp.saka1029.cspj.geometry;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

public class PointSet implements Iterable<Point>, Comparable<PointSet> {

    private final TreeSet<Point> set = new TreeSet<>();
    
    private PointSet() {
    }
    
    public PointSet(Collection<Point> points) {
        for (Point p : points)
            add(p);
    }
    
    public PointSet(Point... points) {
        for (Point p : points)
            add(p);
    }

    private void add(Point p) {
        set.add(p);
    }
    
    public int size() {
        return set.size();
    }
    
    public boolean contains(Point p) {
        return set.contains(p);
    }
    
    public boolean overlap(PointSet p) {
        for (Point e : set)
            if (p.contains(e))
                return true;
        return false;
    }
    
    public PointSet neighbors() {
        PointSet r = new PointSet();
        for (Point e : set)
            for (Point o : Point.NEIGHBORS) {
                Point p = e.plus(o);
                if (!contains(p))
                    r.set.add(p);
            }

        return r;
    }
    
    public static class Builder {
        private PointSet x = new PointSet();
        public PointSet build() { PointSet r = x; x = null; return r; }
        public Builder add(Point p) { x.add(p); return this; }
        public boolean contains(Point p) { return x.contains(p); }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PointSet))
            return false;
        PointSet o = (PointSet)obj;
        return o.set.equals(set);
    }
    
    @Override
    public int hashCode() {
        return set.hashCode();
    }

    @Override
    public Iterator<Point> iterator() {
        return set.iterator();
    }
    
    @Override
    public String toString() {
        return set.toString();
    }

    @Override
    public int compareTo(PointSet o) {
        Iterator<Point> a = iterator();
        Iterator<Point> b = o.iterator();
        while (a.hasNext() && b.hasNext()) {
            Point pa = a.next();
            Point pb = b.next();
            int r = pa.compareTo(pb);
            if (r != 0) return r;
        }
        return a.hasNext() ? 1 : b.hasNext() ? -1 : 0;
    }
}
