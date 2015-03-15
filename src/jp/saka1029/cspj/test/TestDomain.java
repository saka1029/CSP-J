package jp.saka1029.cspj.test;

import static org.junit.Assert.*;
import jp.saka1029.cspj.problem.old.Domain;
import jp.saka1029.cspj.problem.old.Log;

import org.junit.Test;

public class TestDomain {

    @Test
    public void testBoolean() {
        Log.methodName();
        Domain<Boolean> b = Domain.of(true);
        assertEquals("[true]", b.toString());
        assertEquals(1, b.size());
        assertEquals(true, b.first());
        for (boolean e : b)
            assertEquals(true, e);
    }

    @Test
    public void testInteger() {
        Log.methodName();
        Domain<Integer> i = Domain.of(1, 2, 3);
        assertEquals(3, i.size());
        assertTrue(i.contains(1));
        assertTrue(i.contains(2));
        assertTrue(i.contains(3));
        Log.info("%s", i);
    }
    
}
