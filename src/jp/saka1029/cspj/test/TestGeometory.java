package jp.saka1029.cspj.test;

import java.util.Arrays;

import jp.saka1029.cspj.geometry.Matrix;
import jp.saka1029.cspj.geometry.Point;
import jp.saka1029.cspj.geometry.Printer;
import jp.saka1029.cspj.problem.Log;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestGeometory {

    @Test
    public void testPrinter() {
        Log.methodName();
        Printer p = new Printer();
        p.draw(new Point(0, 0), "原 点");
        p.draw(new Point(1, 1), "壱");
        p.draw(new Point(0, 0), new Point(1, 1));
        p.draw(new Point(1, 1), new Point(3, 0));
        p.draw(new Point(1, 2), "**");
        p.draw(new Point(4, 4), "@@");
        p.draw(Point.ZERO, new Point(5, 5));
        Log.info(p);
    }

    @Test
    public void testPrinter2() {
        Log.methodName();
        Printer p = new Printer();
        p.draw(new Point(-2, -2), "原 点");
        p.draw(new Point(-1, -1), "壱");
        p.draw(new Point(-2, -2), new Point(1, 1));
        p.draw(new Point(-1, -1), new Point(3, 0));
        p.draw(new Point(-1, 0), "**");
        p.draw(new Point(2, 2), "@@");
        p.draw(new Point(-2, -2), new Point(5, 5));
        Log.info(p);
    }
    
    @Test
    public void testMatrix() {
        Matrix<Integer> m = new Matrix<>(3, 3);
        int i = 0;
        for (int y = 0; y < 3; ++y)
            for (int x = 0; x < 3; ++x)
                m.set(x, y, i++);
        assertEquals(Arrays.asList(0, 1, 2), m.row(0));
        assertEquals(Arrays.asList(0, 3, 6), m.column(0));
        assertEquals(Arrays.asList(0, 4, 8), m.diagonal(0));
        assertEquals(Arrays.asList(2, 4, 6), m.diagonal(1));
    }
}
