package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import jp.saka1029.cspj.geometry.Board;
import jp.saka1029.cspj.geometry.Box;
import jp.saka1029.cspj.geometry.Point;
import jp.saka1029.cspj.geometry.Printer;
import jp.saka1029.cspj.problem.old.singletype.Answer;
import jp.saka1029.cspj.problem.old.singletype.Bind;
import jp.saka1029.cspj.problem.old.singletype.Constant;
import jp.saka1029.cspj.problem.old.singletype.Constraint;
import jp.saka1029.cspj.problem.old.singletype.ConstraintPredicate;
import jp.saka1029.cspj.problem.old.singletype.Domain;
import jp.saka1029.cspj.problem.old.singletype.Problem;
import jp.saka1029.cspj.problem.old.singletype.Result;
import jp.saka1029.cspj.problem.old.singletype.SimpleSolver;
import jp.saka1029.cspj.problem.old.singletype.Variable;

import org.junit.Test;

public class TestSingletype {

	static final Logger logger = Logger.getLogger(TestSingletype.class.getName());

    public static void methodName() {
        StackTraceElement s = Thread.currentThread().getStackTrace()[2];
        logger.info("<<<<< " + s.getClassName() + "." + s.getMethodName() + " >>>>>");
    }

	@Test
	public void testDomainElements() {
		methodName();
		Domain<Integer> domain = Domain.of(0, 1);
		for (Domain<Integer> e : domain.singleDomains()) {
			assertEquals(1, e.size());
			assertTrue(e.equals(Domain.of(0)) || e.equals(Domain.of(1)));
		}
	}

	@Test
	public void testDerivation() {
		methodName();
		Problem<Integer> problem = new Problem<>();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		Variable<Integer> y = problem.variable("y", Domain.of(3, 4));
		Variable<Integer> z = problem.variable("z", a -> a.get(0) + a.get(1), x, y);
		logger.info(problem.variables().toString());
		logger.info(problem.constraints().toString());
		assertEquals(Domain.of(4, 5, 6, 7), z.domain);
		assertEquals(1, problem.constraints().size());
		Constraint<Integer> c = problem.constraints().get(0);
		assertEquals(Arrays.asList(z, x, y), c.arguments);
		assertTrue(c.predicate.test(Arrays.asList(6, 2, 4)));
		assertFalse(c.predicate.test(Arrays.asList(6, 1, 4)));
		assertEquals(1, x.constraints().size());
		assertEquals(c, x.constraints().iterator().next());
		assertEquals(x.constraints(), y.constraints());
		assertEquals(x.constraints(), z.constraints());
	}

	@Test
	public void testBind() {
		methodName();
		Problem<Integer> problem = new Problem<>();
		Variable<Integer> x = problem.variable("x", Domain.of(1, 2, 3));
		Variable<Integer> y = problem.variable("y", Domain.of(3, 4));
		problem.constraint("==", a -> a.get(0) == a.get(1), x, y);
		Bind<Integer> bind = problem.bind();
		logger.info(bind.toString());
		assertEquals(Domain.of(3), bind.get(x));
		assertEquals(Domain.of(3), bind.get(y));
	}
	
	@Test
	public void testSendMoreMoney() {
		methodName();
		Problem<Integer> problem = new Problem<>();
		Domain<Integer> first = Domain.range(1, 9);
		Domain<Integer> rest = Domain.range(0, 9);
		Variable<Integer> s = problem.variable("s", first);
		Variable<Integer> e = problem.variable("e", rest);
		Variable<Integer> n = problem.variable("n", rest);
		Variable<Integer> d = problem.variable("d", rest);
		Variable<Integer> m = problem.variable("m", first);
		Variable<Integer> o = problem.variable("o", rest);
		Variable<Integer> r = problem.variable("r", rest);
		Variable<Integer> y = problem.variable("y", rest);
		Domain<Integer> carry = Domain.of(0, 1);
		Variable<Integer> c1 = problem.variable("c1", carry);
		Variable<Integer> c2 = problem.variable("c2", carry);
		Variable<Integer> c3 = problem.variable("c3", carry);
		Variable<Integer> c4 = problem.variable("c4", carry);
		Constant<Integer> z = problem.constant(0);
		//c4 c3 c2 c1  0
		//    s  e  n  d
		//+   m  o  r  e
		//--------------
		// m  o  n  e  y

		ConstraintPredicate<Integer> f = a -> a.get(0) + a.get(1) + a.get(2) == a.get(3) + a.get(4) * 10;
		String fn = "%s + %s + %s == %s + %s * 10";
		problem.constraint(fn, f,  z, d, e, y, c1);
		problem.constraint(fn, f, c1, n, r, e, c2);
		problem.constraint(fn, f, c2, e, o, n, c3);
		problem.constraint(fn, f, c3, s, m, o, c4);
		problem.constraint(fn, f, c4, z, z, m,  z);

//		DerivationFunction<Integer> ll = a -> a.get(0) + a.get(1) + a.get(2);
//		DerivationFunction<Integer> rr = a -> a.get(0) + a.get(1) * 10;
//		ConstraintPredicate<Integer> eq = problem.EQ;
//		problem.constraint("==", eq, problem.variable(ll, z, d, e), problem.variable(rr, y, c1));
//		problem.constraint("==", eq, problem.variable(ll, c1, n, r), problem.variable(rr, e, c2));
//		problem.constraint("==", eq, problem.variable(ll, c2, e, o), problem.variable(rr, n, c3));
//		problem.constraint("==", eq, problem.variable(ll, c3, s, m), problem.variable(rr, o, c4));
//		problem.constraint("==", eq, problem.variable(ll, c4, z, z), problem.variable(rr, m,  z));

		problem.allDifferent(s, e, n, d, m, o, r, y);
		logger.info("*** variables ****");
		for (Variable<Integer> x : problem.variables())
            logger.info(x + " : " + x.domain);
		logger.info("*** constraints ****");
		for (Constraint<Integer> x : problem.constraints())
            logger.info(x.toString());
		SimpleSolver<Integer> solver = new SimpleSolver<>();
		int answers = solver.solve(problem, a -> {
			logger.info(" " + a.get(s) + a.get(e) + a.get(n) + a.get(d));
			logger.info("+" + a.get(m) + a.get(o) + a.get(r) + a.get(e));
			logger.info("-----");
			logger.info("" + a.get(m) + a.get(o) + a.get(n) + a.get(e) + a.get(y));
			return true;
		});
		assertEquals(1, answers);
	}
	
	@Test
	public void testUnmodifiable() {
		List<Integer> list = new ArrayList<>();
		list.add(0);
		List<Integer> unmodifiable = Collections.unmodifiableList(list);
		list.set(0, 9);
		assertEquals(9, (int)unmodifiable.get(0));
	}
	
    void printReduced(Board board, Problem<Box> problem, Bind<Box> bind) {
    	logger.info("*** reduces problem ***");
        Printer printer = new Printer();
        printer.draw(board.box);
        for (Variable<Box> v : problem.variables()) {
        	Domain<Box> d = bind.get(v);
        	if (d.size() == 1)
            printer.draw(d.first());
        }
        for (Entry<Point, Integer> e : board.numbers.entrySet())
            printer.draw(e.getKey(), e.getValue());
        logger.info(printer.toString());
    	
    }
	@Test
	public void testShikaku() throws IOException {
		File input = new File("data/cis2.txt");
		Board board = new Board(input);
		Problem<Box> problem = new Problem<>();
        logger.info("Shikaku: file=" + input);
        logger.info(board.toString());
        List<Variable<Box>> variables = new ArrayList<>();
        for (Entry<Point, Integer> e : board.numbers.entrySet()) {
            Point p = e.getKey();
            int n = e.getValue();
            Domain.Builder<Box> builder = new Domain.Builder<>();
            for (int h = 1; h <= n; ++h) {
                if (n % h != 0) continue;
                int w = n / h;
                for (int y = p.y - h + 1; y <= p.y; ++y)
                    L: for (int x = p.x - w + 1; x <= p.x; ++x) {
                        Box b = new Box(x, y, w, h);
                        if (!board.box.contains(b)) continue;
                        for (Point q : board.numbers.keySet())
                            if (!q.equals(p) && b.contains(q))
                                continue L;
                        builder.add(b);
                    }
            }
            variables.add(problem.variable(String.format("%d@%s", n, p), builder.build()));
        }
        problem.forEachPairs("notOverlap", a -> !a.get(0).overlap(a.get(1)), variables);
        printReduced(board, problem, problem.bind());
        SimpleSolver<Box> solver = new SimpleSolver<>();
        int answers = solver.solve(problem, new Answer<Box>() {
			@Override
			public boolean answer(Result<Box> r) {
		        Printer printer = new Printer();
		        printer.draw(board.box);
		        for (Variable<Box> v : variables)
		            printer.draw(r.get(v));
		        for (Entry<Point, Integer> e : board.numbers.entrySet())
		            printer.draw(e.getKey(), e.getValue());
		        logger.info(printer.toString());
				return true;
			}
		});
        logger.info("answers=" + answers);
    }

}
