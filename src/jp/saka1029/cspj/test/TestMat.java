package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import jp.saka1029.cspj.geometry.Mat;
import jp.saka1029.cspj.geometry.Point;

public class TestMat {

    @Test
    public void testInitializer() {
        Mat<Point> m = new Mat<>(3, 2);
        for (int r = 0; r < m.rows; ++r)
            for (int c = 0; c < m.cols; ++c)
                m.set(r, c, new Point(r, c));
        System.out.println(m.col(1));
        Mat<Point> n = new Mat<>(3, 2, (r, c) -> new Point(r, c));
        assertEquals(m, n);
    }
    
    @Test
    public void testRow() {
        Mat<Integer> m = new Mat<>(3, 3, (r, c) -> r * 3 + c);
        assertEquals(Arrays.asList(6, 7, 8), m.row(2));
    }

    @Test
    public void testCol() {
        Mat<Integer> m = new Mat<>(3, 3, (r, c) -> r * 3 + c);
        assertEquals(Arrays.asList(1, 4, 7), m.col(1));
    }

    @Test
    public void testSlice() {
        Mat<Integer> m = new Mat<>(3, 3, (r, c) -> r * 3 + c);
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8), m);
        Mat<Integer> s = m.slice(1, 1, 2, 2);
        assertEquals(Arrays.asList(4, 5, 7, 8), s);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSliceException() {
        Mat<Integer> m = new Mat<>(3, 3, (r, c) -> r * 3 + c).slice(1, 1, 2, 3);
        System.out.println(m);
    }

}
