package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Bind;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.problem.Domain;

import org.junit.Test;

public class TestHelper {

    static final Logger logger = Logger.getLogger(TestHelper.class.getName());
    static { System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tFT%1$tT.%1$tL %4$s %5$s %6$s%n"); }

    public static void methodName() {
        StackTraceElement s = Thread.currentThread().getStackTrace()[2];
        logger.info("<<<<< " + s.getClassName() + "." + s.getMethodName() + " >>>>>");
    }

	@Test
	public void testEqVariableOfQextendsObjectVariableOfQextendsObject() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3, 4));
		constraint(and(eq(x, y)));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2, 3), bind.get(x));
	}

	@Test
	public void testEqVariableOfQextendsObjectObject() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		constraint(and(eq(x, 2)));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2), bind.get(x));
	}

	@Test
	public void testCeqVariableOfQextendsObjectVariableOfQextendsObject() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3, 4));
		constraint(eq(x, y));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2, 3), bind.get(x));
	}

	@Test
	public void testCeqVariableOfQextendsObjectObject() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		constraint(eq(x, 2));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2), bind.get(x));
	}

	@Test
	public void testNeVariableOfQextendsObjectVariableOfQextendsObject() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		Variable<Integer> y = problem.variable("y", Domain.of(2));
		constraint(and(ne(x, y)));
		Bind bind = problem.bind();
		assertEquals(Domain.of(1, 3), bind.get(x));
	}

	@Test
	public void testNeVariableOfQextendsObjectObject() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		constraint(and(ne(x, 2)));
		Bind bind = problem.bind();
		assertEquals(Domain.of(1, 3), bind.get(x));
	}

	@Test
	public void testCneVariableOfQextendsObjectVariableOfQextendsObject() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		Variable<Integer> y = problem.variable("y", Domain.of(2));
		constraint(ne(x, y));
		Bind bind = problem.bind();
		assertEquals(Domain.of(1, 3), bind.get(x));
	}

	@Test
	public void testCneVariableOfQextendsObjectObject() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		constraint(ne(x, 2));
		Bind bind = problem.bind();
		assertEquals(Domain.of(1, 3), bind.get(x));
	}

	@Test
	public void testLtVariableOfTVariableOfT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(2, 3, 4));
		Variable<Integer> y = problem.variable("y", Domain.of(1, 2, 3));
		constraint(and(lt(x, y)));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2), bind.get(x));
	}

	@Test
	public void testLtVariableOfTT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(2, 3, 4));
		constraint(and(lt(x, 3)));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2), bind.get(x));
	}

	@Test
	public void testCltVariableOfTVariableOfT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(2, 3, 4));
		Variable<Integer> y = problem.variable("y", Domain.of(1, 2, 3));
		constraint(lt(x, y));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2), bind.get(x));
	}

	@Test
	public void testCltVariableOfTT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(2, 3, 4));
		constraint(lt(x, 3));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2), bind.get(x));
	}

	@Test
	public void testLeVariableOfTVariableOfT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(2, 3, 4));
		Variable<Integer> y = problem.variable("y", Domain.of(1, 2, 3));
		constraint(and(le(x, y)));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2, 3), bind.get(x));
	}

	@Test
	public void testLeVariableOfTT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(2, 3, 4));
		constraint(and(le(x, 3)));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2, 3), bind.get(x));
	}

	@Test
	public void testCleVariableOfTVariableOfT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(2, 3, 4));
		Variable<Integer> y = problem.variable("y", Domain.of(1, 2, 3));
		constraint(le(x, y));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2, 3), bind.get(x));
	}

	@Test
	public void testCleVariableOfTT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(2, 3, 4));
		constraint(le(x, 3));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2, 3), bind.get(x));
	}

	@Test
	public void testGtVariableOfTVariableOfT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3, 4));
		constraint(and(gt(x, y)));
		Bind bind = problem.bind();
		assertEquals(Domain.of(3), bind.get(x));
	}

	@Test
	public void testGtVariableOfTT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		constraint(and(gt(x, 2)));
		Bind bind = problem.bind();
		assertEquals(Domain.of(3), bind.get(x));
	}

	@Test
	public void testCgtVariableOfTVariableOfT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3, 4));
		constraint(gt(x, y));
		Bind bind = problem.bind();
		assertEquals(Domain.of(3), bind.get(x));
	}

	@Test
	public void testCgtVariableOfTT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		constraint(gt(x, 2));
		Bind bind = problem.bind();
		assertEquals(Domain.of(3), bind.get(x));
	}

	@Test
	public void testGeVariableOfTVariableOfT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3, 4));
		constraint(and(ge(x, y)));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2, 3), bind.get(x));
	}

	@Test
	public void testGeVariableOfTT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		constraint(and(ge(x, 2)));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2, 3), bind.get(x));
	}

	@Test
	public void testCgeVariableOfTVariableOfT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3, 4));
		constraint(ge(x, y));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2, 3), bind.get(x));
	}

	@Test
	public void testCgeVariableOfTT() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		constraint(ge(x, 2));
		Bind bind = problem.bind();
		assertEquals(Domain.of(2, 3), bind.get(x));
	}

	@Test
	public void testPlusCollectionOfVariableOfQextendsInteger() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3));
		List<Variable<? extends Integer>> args = Arrays.asList(x, y);
		Variable<Integer> z = plus(args);
		Bind bind = problem.bind();
		assertEquals(Domain.of(3, 4, 5), bind.get(z));
	}

	@Test
	public void testPlusVariableOfIntegerArray() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3));
		Variable<Integer> z = plus(x, y);
		Bind bind = problem.bind();
		assertEquals(Domain.of(3, 4, 5), bind.get(z));
	}

	@Test
	public void testPlusVariableOfIntegerInt() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2));
		Variable<Integer> z = plus(x, 2);
		Bind bind = problem.bind();
		assertEquals(Domain.of(3, 4), bind.get(z));
	}

	@Test
	public void testMinusCollectionOfVariableOfQextendsInteger() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3));
		List<Variable<? extends Integer>> args = Arrays.asList(x, y);
		Variable<Integer> z = minus(args);
		Bind bind = problem.bind();
		assertEquals(Domain.of(-2, -1, 0), bind.get(z));
	}

	@Test
	public void testMinusVariableOfIntegerArray() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3));
		Variable<Integer> z = minus(x, y);
		Bind bind = problem.bind();
		assertEquals(Domain.of(-2, -1, 0), bind.get(z));
	}

	@Test
	public void testMinusVariableOfIntegerInt() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2));
		Variable<Integer> z = minus(x, 1);
		Bind bind = problem.bind();
		assertEquals(Domain.of(0, 1), bind.get(z));
	}

	@Test
	public void testMultCollectionOfVariableOfQextendsInteger() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3));
		List<Variable<? extends Integer>> list = Arrays.asList(x, y);
		Variable<Integer> z = mult(list);
		Bind bind = problem.bind();
		assertEquals(Domain.of(2, 3, 4, 6), bind.get(z));
	}

	@Test
	public void testMultVariableOfIntegerArray() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3));
		Variable<Integer> z = mult(x, y);
		Bind bind = problem.bind();
		assertEquals(Domain.of(2, 3, 4, 6), bind.get(z));
	}

	@Test
	public void testMultVariableOfIntegerInt() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2));
		Variable<Integer> z = mult(x, 3);
		Bind bind = problem.bind();
		assertEquals(Domain.of(3, 6), bind.get(z));
	}

	@Test
	public void testAbs() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(-1, 0, 1, 2));
		Variable<Integer> z = abs(x);
		Bind bind = problem.bind();
		assertEquals(Domain.of(0, 1, 2), bind.get(z));
	}

	@Test
	public void testDivVariableOfIntegerVariableOfInteger() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3));
		Variable<Integer> z = div(x, y);
		Bind bind = problem.bind();
		assertEquals(Domain.of(0, 1), bind.get(z));
	}

	@Test
	public void testDivVariableOfIntegerInt() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2));
		Variable<Integer> z = div(x, 2);
		Bind bind = problem.bind();
		assertEquals(Domain.of(0, 1), bind.get(z));
	}

	@Test
	public void testModVariableOfIntegerVariableOfInteger() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(5, 6));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3));
		Variable<Integer> z = mod(x, y);
		Bind bind = problem.bind();
		assertEquals(Domain.of(0, 1, 2), bind.get(z));
	}

	@Test
	public void testModVariableOfIntegerInt() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(5, 6));
		Variable<Integer> z = mod(x, 2);
		Bind bind = problem.bind();
		assertEquals(Domain.of(0, 1), bind.get(z));
	}

	@Test
	public void testAndCollectionOfVariableOfQextendsBoolean() {
		methodName();
		Problem problem = new Problem();
		Variable<Boolean> x = problem.variable("x", Domain.of(true, false));
		Variable<Boolean> y = problem.variable("y", Domain.of(false));
		List<Variable<? extends Boolean>> list = Arrays.asList(x, y);
		Variable<Boolean> z = and(list);
		Bind bind = problem.bind();
		assertEquals(Domain.of(false), bind.get(z));
	}

	@Test
	public void testNot() {
		methodName();
		Problem problem = new Problem();
		Variable<Boolean> x = problem.variable("x", Domain.of(false));
		Variable<Boolean> z = not(x);
		Bind bind = problem.bind();
		assertEquals(Domain.of(true), bind.get(z));
	}

	@Test
	public void testCnot() {
		methodName();
		Problem problem = new Problem();
		Variable<Boolean> x = problem.variable("x", Domain.of(true, false));
		constraint(not(x));
		Bind bind = problem.bind();
		assertEquals(Domain.of(false), bind.get(x));
	}

	@Test
	public void testAndVariableOfBooleanArray() {
		methodName();
		Problem problem = new Problem();
		Variable<Boolean> x = problem.variable("x", Domain.of(true, false));
		Variable<Boolean> y = problem.variable("y", Domain.of(false));
		Variable<Boolean> z = and(x, y);
		Bind bind = problem.bind();
		assertEquals(Domain.of(false), bind.get(z));
	}

	@Test
	public void testCandCollectionOfVariableOfQextendsBoolean() {
		methodName();
		Problem problem = new Problem();
		Variable<Boolean> x = problem.variable("x", Domain.of(true, false));
		Variable<Boolean> y = problem.variable("y", Domain.of(true, false));
		List<Variable<? extends Boolean>> list = Arrays.asList(x, y);
		constraint(and(list));
		Bind bind = problem.bind();
		assertEquals(Domain.of(true), bind.get(x));
	}

	@Test
	public void testCandVariableOfBooleanArray() {
		methodName();
		Problem problem = new Problem();
		Variable<Boolean> x = problem.variable("x", Domain.of(true, false));
		Variable<Boolean> y = problem.variable("y", Domain.of(true, false));
		constraint(and(x, y));
		Bind bind = problem.bind();
		assertEquals(Domain.of(true), bind.get(x));
	}

	@Test
	public void testOrCollectionOfVariableOfQextendsBoolean() {
		methodName();
		Problem problem = new Problem();
		Variable<Boolean> x = problem.variable("x", Domain.of(true, false));
		Variable<Boolean> y = problem.variable("y", Domain.of(true));
		List<Variable<? extends Boolean>> list = Arrays.asList(x, y);
		Variable<Boolean> z = or(list);
		Bind bind = problem.bind();
		assertEquals(Domain.of(true), bind.get(z));
	}

	@Test
	public void testOrVariableOfBooleanArray() {
		methodName();
		Problem problem = new Problem();
		Variable<Boolean> x = problem.variable("x", Domain.of(true, false));
		Variable<Boolean> y = problem.variable("y", Domain.of(true));
		Variable<Boolean> z = or(x, y);
		Bind bind = problem.bind();
		assertEquals(Domain.of(true), bind.get(z));
	}

	@Test
	public void testCorCollectionOfVariableOfQextendsBoolean() {
		methodName();
		Problem problem = new Problem();
		Variable<Boolean> x = problem.variable("x", Domain.of(true, false));
		Variable<Boolean> y = problem.variable("y", Domain.of(false));
		List<Variable<? extends Boolean>> list = Arrays.asList(x, y);
		constraint(or(list));
		Bind bind = problem.bind();
		assertEquals(Domain.of(true), bind.get(x));
	}

	@Test
	public void testCorVariableOfBooleanArray() {
		methodName();
		Problem problem = new Problem();
		Variable<Boolean> x = problem.variable("x", Domain.of(true, false));
		Variable<Boolean> y = problem.variable("y", Domain.of(false));
		constraint(or(x, y));
		Bind bind = problem.bind();
		assertEquals(Domain.of(true), bind.get(x));
	}

	@Test
	public void testAllDifferentVariableOfQextendsObjectArray() {
		methodName();
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		Variable<Integer> y = problem.variable("y", Domain.of(2, 3));
		Variable<Integer> z = problem.variable("z", Domain.of(3));
		allDifferent(x, y, z);
		Bind bind = problem.bind();
		assertEquals(Domain.of(1), bind.get(x));
	}

}
