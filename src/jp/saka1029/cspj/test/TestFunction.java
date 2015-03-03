package jp.saka1029.cspj.test;

import static jp.saka1029.cspj.problem.Helper.*;
import static org.junit.Assert.*;
import jp.saka1029.cspj.problem.Constraint;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;

import org.junit.Test;

public class TestFunction {

    @Test
    public void testEq() {
        Log.methodName();
        Problem p = new Problem();
        Variable<Integer> a = p.variable("a", Domain.of(1, 2, 3));
        Variable<Integer> b = p.variable("b", Domain.of(2, 3, 4));
        Constraint<Boolean> e = eq(a, b);
        assertEquals("(a == b)", e.toString());
        assertEquals(2, e.domain.size());
        assertTrue(e.domain.contains(true));
        assertTrue(e.domain.contains(false));
    }

    @Test
    public void testEqAllwaysTrue() {
        Log.methodName();
        Problem p = new Problem();
        Variable<Integer> a = p.variable("a", Domain.of(2));
        Variable<Integer> b = p.variable("b", Domain.of(2));
        Constraint<Boolean> e = eq(a, b);
        assertEquals("(a == b)", e.toString());
        assertEquals(1, e.domain.size());
        assertTrue(e.domain.contains(true));
        assertFalse(e.domain.contains(false));
    }
    
    @Test
    public void testPlus() {
        Log.methodName();
        Problem p = new Problem();
        Variable<Integer> a = p.variable("a", Domain.of(1, 2, 3));
        Variable<Integer> b = p.variable("b", Domain.of(2, 3));
        Constraint<Integer> e = plus(a, b);
        assertEquals("(a + b)", e.toString());
        assertEquals(4, e.domain.size());
        assertTrue(e.domain.contains(3));
        assertTrue(e.domain.contains(4));
        assertTrue(e.domain.contains(5));
        assertTrue(e.domain.contains(6));
    }

    @Test
    public void testEqPlus() {
        Log.methodName();
        Problem p = new Problem();
        Variable<Integer> a = p.variable("a", Domain.of(1, 2, 3));
        Variable<Integer> b = p.variable("b", Domain.of(2, 3));
        Variable<Boolean> e = eq(plus(a, b), p.constant(3));
        assertEquals("((a + b) == 3)", e.toString());
        assertEquals(2, e.domain.size());
        assertTrue(e.domain.contains(true));
        assertTrue(e.domain.contains(false));
    }

}
