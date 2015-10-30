package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jp.saka1029.cspj.problem.Bind;
import jp.saka1029.cspj.problem.Constraint;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.problem.Domain;
import static jp.saka1029.cspj.problem.Helper.*;

import org.junit.Test;

public class TestProblem {

    static final Logger logger = Logger.getLogger(TestProblem.class.getName());
    static { System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tFT%1$tT.%1$tL %4$s %5$s %6$s%n"); }

    public static void methodName() {
        StackTraceElement s = Thread.currentThread().getStackTrace()[2];
        logger.info("<<<<< " + s.getClassName() + "." + s.getMethodName() + " >>>>>");
    }

    Bind print(Problem problem) {
        logger.info("*** variables ***");
        for (Variable<?> e : problem.variables)
            logger.info(e.name + " = " + e.domain);
        logger.info("*** constraints ***");
        for (Constraint e : problem.constraints)
            logger.info(e.toString());
        Bind bind = problem.bind();
        logger.info("*** reduced variables ***");
        for (Variable<?> e : problem.variables)
            logger.info(e.name + " = " + bind.get(e));
        return bind;
    }

    @Test
    public void testConstraint() {
    	methodName();
        Problem problem = new Problem();
        Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
        Variable<Integer> y = problem.variable("y", Domain.of(2, 3, 4));
        problem.constraint("==", (a, b) -> a.equals(b), x, y);
        Bind bind = print(problem);
        assertEquals(Domain.of(2, 3), bind.get(x));
        assertEquals(Domain.of(2, 3), bind.get(y));
    }

    @Test
    public void testDerivationFunction() {
    	methodName();
        Problem problem = new Problem();
        Variable<String> x = problem.variable("x", Domain.of("a", "b", "c"));
        Variable<Boolean> xa = problem.variable("xa", "is_a", a -> a.equals("a"), x);
        Variable<Boolean> xb = problem.variable("xb", "is_b", a -> a.equals("b"), x);
        problem.constraint("||", (a, b) -> a || b, xa, xb);
        Bind bind = print(problem);
        assertEquals(Domain.of("a", "b", "c"), bind.get(x));
    }

    enum Gender { Male, Female };

    @Test
    public void testDifferentTypes() {
    	methodName();
        Problem problem = new Problem();
        Variable<Integer> x = problem.variable("x", Domain.of(-1, 0));
        Variable<Gender> g = problem.variable("g", Domain.of(Gender.values()));
        problem.constraint("==", (a, b) -> a == b.ordinal(), x, g);
        Bind bind = print(problem);
        assertEquals(Domain.of(0), bind.get(x));
        assertEquals(Domain.of(Gender.Male), bind.get(g));
    }

    @Test
    public void testDerivationFunction2() {
    	methodName();
        Problem problem = new Problem();
        Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
        Variable<Integer> y = problem.variable("y", Domain.of(2, 3, 4));
        Variable<Integer> z = problem.variable("z", "+", (a, b) -> a + b, x, y);
        problem.constraint("%s == 6", a -> a == 6, z);
        Bind bind = print(problem);
        assertEquals(Domain.of(2, 3), bind.get(x));
        assertEquals(Domain.of(3, 4), bind.get(y));
    }

    static class Foo {
    	final int i;
    	Foo(int i) { this.i = i; }
    	@Override
    	public int hashCode() { return i; }
    	@Override public boolean equals(Object obj) { return obj instanceof Foo && ((Foo)obj).i == i; }
    	@Override public String toString() { return "Foo" + i; }
    }

    static class Bar extends Foo {
		Bar(int i) { super(i); } 
    	@Override public String toString() { return "Bar" + i; }
    }

    static class Baz extends Foo {
		Baz(int i) { super(i); } 
    	@Override public String toString() { return "Baz" + i; }
    }

    @Test
    public void testDerivationSubSuper() {
    	methodName();
        Problem problem = new Problem();
        Variable<Baz> x = problem.variable("x", Domain.of(new Baz(1), new Baz(2)));
        Variable<Bar> y = problem.variable("y", Domain.of(new Bar(2), new Bar(3)));
        Variable<Foo> z = problem.variable("z", "+", (a, b) -> new Baz(a.i + b.i), x, y);
        problem.constraint("is5", a -> a.i == 5, z);
        Bind bind = print(problem);
        assertEquals(Domain.of(new Foo(2)), bind.get(x));
    }

    @Test
    public void testConstraintSubSuper() {
    	methodName();
        Problem problem = new Problem();
        Variable<Baz> x = problem.variable("x", Domain.of(new Baz(1), new Baz(2)));
        Variable<Bar> y = problem.variable("y", Domain.of(new Bar(2), new Bar(3)));
        problem.constraint("==", (a, b) -> a.i == b.i, x, y);
        Bind bind = print(problem);
        assertEquals(Domain.of(new Foo(2)), bind.get(x));
    }

//    @Test
//    public void testForEachPairs() {
//    	methodName();
//        Problem problem = new Problem();
//        Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
//        Variable<Integer> y = problem.variable("y", Domain.of(1, 2));
//        Variable<Integer> z = problem.variable("z", Domain.of(2));
//        forAllPairs("!=", (a, b) -> !a.equals(b), x, y, z);
//        Bind bind = print(problem);
//        assertEquals(Domain.of(3), bind.get(x));
//    }

    @Test
    public void testAllDiffernt() {
    	methodName();
        Problem problem = new Problem();
        Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
        Variable<Integer> y = problem.variable("y", Domain.of(2, 3));
        Variable<Integer> z = problem.variable("z", Domain.of(2, 3));
        allDifferent(x, y, z);
        Bind bind = print(problem);
        assertEquals(Domain.of(1, 2, 3), bind.get(x));
    }
    
    @Test
    public void testCollection() {
    	methodName();
        Problem problem = new Problem();
        Variable<Baz> x = problem.variable("x", Domain.of(new Baz(1), new Baz(2)));
        Variable<Bar> y = problem.variable("y", Domain.of(new Bar(2), new Bar(3)));
        List<Variable<? extends Foo>> list = new ArrayList<>();
        list.add(x);
        list.add(y);
        Variable<Foo> z = problem.variable("z", "+", a -> new Baz(a.get(0).i + a.get(1).i), list);
        problem.constraint("is5", a -> a.i == 5, z);
        Bind bind = print(problem);
        assertEquals(Domain.of(new Foo(2)), bind.get(x));
    }
    
    @SafeVarargs
	static <T> T[] toArray(List<T> list, T... dummy) {
    	if (dummy.length != 0) throw new IllegalArgumentException("dummy");
    	return list.toArray(dummy);
    }

}
