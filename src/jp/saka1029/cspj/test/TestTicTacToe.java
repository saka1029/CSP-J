package jp.saka1029.cspj.test;

import static jp.saka1029.cspj.geometry.TicTacToe.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import jp.saka1029.cspj.geometry.TicTacToe;
import jp.saka1029.cspj.problem.Log;

import org.junit.Test;

public class TestTicTacToe {

    @Test
    public void testSet() {
        for (int y = 0; y < 3; ++y)
            for (int x = 0; x < 3; ++x) {
                int tic = set(0, x, y, 1);
                Log.info("x = %d, y = %d", x, y);
                Log.info(string(tic));
                assertEquals(1 << ((y * 3 + x) * 2), tic);
            }
    }
    
    @Test
    public void testChild() {
        Set<Integer> prev = new HashSet<>();
        prev.add(0);
        for (int i = 0; i < 5; ++i)
            for (int v = 1; v <= 2; ++v) {
                Set<Integer> set = new HashSet<>();
                for (int org : prev)
                    for (int y = 0; y < 3; ++y)
                        for (int x = 0; x < 3; ++x)
                            if (get(org, x, y) == 0)
                                set.add(set(org, x, y, v));
                Log.info("i = %d v = %d size = %d", i, v, set.size());
//                Log.info(set);
                prev = set;
            }
    }
    
    @Test
    public void testTree() {
        TicTacToe t = TicTacToe.tree();
        assertNotNull(t);
    }
}
