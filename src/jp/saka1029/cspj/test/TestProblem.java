package jp.saka1029.cspj.test;

import static jp.saka1029.cspj.problem.Helper.*;
import static org.junit.Assert.*;
import jp.saka1029.cspj.problem.Bind;
import jp.saka1029.cspj.problem.Constant;
import jp.saka1029.cspj.problem.Constraint;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;

import org.junit.Test;

public class TestProblem {

    @Test
    public void testConstantInteger() {
        Log.methodName();
        Problem p = new Problem();
        Constant<Integer> a = p.constant(8);
        assertEquals("8", a.toString());
        assertEquals("[]", p.toString());
    }

    @Test
    public void testConstantBoolean() {
        Log.methodName();
        Problem p = new Problem();
        Constant<Boolean> b = p.constant(false);
        assertEquals("false", b.toString());
        assertEquals("[]", p.toString());
    }

    @Test
    public void testVariableInteger() {
        Log.methodName();
        Problem p = new Problem();
        Variable<Integer> a = p.variable("a", Domain.of(1, 2, 3));
        assertEquals("a", a.name);
        assertEquals("a", a.toString());
        assertEquals(0, a.no);
        assertEquals("[a]", p.toString());
        assertEquals(3, a.domain.size());
    }
    
    @Test
    public void testVariableBoolean() {
        Log.methodName();
        Problem p = new Problem();
        Variable<Boolean> b = p.variable("b", Domain.of(true, false));
        assertEquals("b", b.name);
        assertEquals("b", b.toString());
        assertEquals(0, b.no);
        assertEquals("[b]", p.toString());
        assertEquals(2, b.domain.size());
    }
    
    @Test
    public void testVariableLong() {
        Log.methodName();
        Problem p = new Problem();
        Variable<Long> a = p.variable("a", Domain.of(1L, 2L, 3L));
        assertEquals("a", a.name);
        assertEquals("a", a.toString());
        assertEquals(0, a.no);
        assertEquals("[a]", p.toString());
        assertEquals(3, a.domain.size());
    }

    @Test
    public void testBindEq() {
        Log.methodName();
        Problem p = new Problem();
        Variable<Integer> a = p.variable("a", Domain.of(1, 2, 3));
        Variable<Integer> b = p.variable("b", Domain.of(2, 3, 4));
        Constraint<Boolean> e = eq(a, b);
        Bind bind = p.bind();
        assertNotNull(bind);
        assertEquals(Domain.TRUE, bind.get(e));
        assertEquals(Domain.of(2, 3), bind.get(a));
        assertEquals(Domain.of(2, 3), bind.get(b));
    }

    @Test
    public void testBindEqPlus() {
        Log.methodName();
        Problem p = new Problem();
        Variable<Integer> a = p.variable("a", Domain.of(1, 2, 3));
        Variable<Integer> b = p.variable("b", Domain.of(2, 3, 4));
        Constant<Integer> c = p.constant(4);
        Constraint<Integer> d = plus(a, b);
        Constraint<Boolean> e = eq(d, c);
        Bind bind = p.bind();
        assertNotNull(bind);
        assertEquals(Domain.of(true, false), e.domain);
        assertEquals(Domain.TRUE, bind.get(e));
        assertEquals(Domain.of(3, 4, 5, 6, 7), d.domain);
        assertEquals(Domain.of(4), bind.get(d));
        assertEquals(Domain.of(1, 2), bind.get(a));
        assertEquals(Domain.of(2, 3), bind.get(b));
    }

//    @Test
//    public void testBindEqPlusTry() {
//        Log.methodName();
//        Problem p = new Problem();
//        Variable<Integer> a = p.variable("a", Domain.of(1, 2, 3));
//        Variable<Integer> b = p.variable("b", Domain.of(2, 3, 4));
//        Constant<Integer> c = p.constant(4);
//        Constraint<Integer> d = plus(a, b);
//        Constraint<Boolean> e = eq(d, c);
//        Bind bind = p.bind();
//        assertNotNull(bind);
//        assertTrue(a.bind(Domain.of(1), bind));
//        assertEquals(Domain.TRUE, bind.get(e));
//        assertEquals(Domain.of(4), bind.get(d));
//        assertEquals(Domain.of(1), bind.get(a));
//        assertEquals(Domain.of(3), bind.get(b));
//    }

//    @Test
//    public void testBindEqPlusTryFail() {
//        Log.methodName();
//        Problem p = new Problem();
//        Variable<Integer> a = p.variable("a", Domain.of(1, 2, 3));
//        Variable<Integer> b = p.variable("b", Domain.of(2, 3, 4));
//        Constant<Integer> c = p.constant(4);
//        Constraint<Integer> d = plus(a, b);
//        eq(d, c);
//        Bind bind = p.bind();
//        assertNotNull(bind);
//        assertFalse(a.bind(Domain.of(3), bind));
//    }
}
