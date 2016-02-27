package jp.saka1029.cspj.test;


import org.junit.Test;

public class TestSuginami {

    static int abs(int x) {
        return Math.abs(x);
    }

    /**
     * 杉並の土地を囲む長方形を求める。
     * 
     * 参考URL:
     * http://d.hatena.ne.jp/sugyan/20090408/1239148436
     * 
     */
    
    static double dist(double[] a, double[] b) {
        double x = a[0] - b[0];
        double y = a[1] - b[1];
        return Math.sqrt(x * x + y * y);
    }

    public static double[] p(double x, double y) {
        return new double[] {x, y};
    }
    
    public static String s(double[] p) {
        return String.format("(%.2f,%.2f)", p[0], p[1]);
    }

    static double[] from(double[] a, double da, double[] b, double db) {
        double dd = dist(a, b);
        double xx = (da * da - db * db + dd * dd) / (2.0 * dd);
        double s = (da + db + dd) / 2.0;
        double yy = 2.0 * Math.sqrt(s * (s - da) * (s - db) * (s - dd)) / dd;
        double alpha = Math.atan((b[1] - a[1]) / (b[0] - a[0]));
        double sina = Math.sin(alpha);
        double cosa = Math.cos(alpha);
        double xxcosa = xx * cosa;
        double xxsina = xx * sina;
        double yycosa = yy * cosa;
        double yysina = yy * sina;
        return (xxsina + yycosa + a[1] > xxsina - yycosa + a[1])
            ? p(xxcosa - yysina + a[0], xxsina + yycosa + a[1])
            : p(xxcosa + yysina + a[0], xxsina - yycosa + a[1]);
    }
    
    @Test
    public void testFrom() {
        double[] a = p(0.0, 0.0);
        double[] e = p(9.57, 0.0);
        double ab = 10.17;
        double bc = 13.99;
        double bd = 14.05;
        double be = 14.27;
        double cd = 0.32;
        double de = 3.00;
        double[] b = from(a, ab, e, be);
        double[] d = from(b, bd, e, de);
        double[] c = from(b, bc, d, cd);
        System.out.printf("a=%s%n", s(a));
        System.out.printf("b=%s%n", s(b));
        System.out.printf("c=%s%n", s(c));
        System.out.printf("d=%s%n", s(d));
        System.out.printf("e=%s%n", s(e));
    }

}
