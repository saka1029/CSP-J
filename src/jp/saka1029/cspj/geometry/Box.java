package jp.saka1029.cspj.geometry;

public class Box {

    public final Point topLeft;
    public final Point size;
 
    public Box(Point topLeft, Point size) {
        if (size.x < 0) throw new IllegalArgumentException("size.x is negative");
        if (size.y < 0) throw new IllegalArgumentException("size.y is negative");
        this.topLeft = topLeft;
        this.size = size;
    }
    
    public Box(int left, int top, int width, int height) {
        this(new Point(left, top), new Point(width, height));
    }

    public int bottom() {
        return topLeft.y + size.y;
    }

    public int right() {
        return topLeft.x + size.x;
    }

    public Point bottomRight() {
        return topLeft.plus(size);
    }

    public boolean contains(int x, int y) {
        Point bottomRight = bottomRight();
        return x >= topLeft.x && y >= topLeft.y &&
            x < bottomRight.x && y < bottomRight.y;
    }

    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    public boolean contains(Box b) {
        return contains(b.topLeft) && contains(b.bottomRight().minus(Point.ONE));
    }

    public boolean overlap(Box b) {
        return b.bottom() > topLeft.y && b.right() > topLeft.x
            && this.bottom() > b.topLeft.y && this.right() > b.topLeft.x;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Box)) return false;
        Box o = (Box)obj;
        return topLeft.equals(o.topLeft) && size.equals(o.size);
    }
    
    @Override
    public int hashCode() {
        return topLeft.hashCode() << 16 ^ size.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Box(%s, %s)", topLeft, size);
    }
}
