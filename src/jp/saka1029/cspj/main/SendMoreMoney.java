package jp.saka1029.cspj.main;

import java.util.logging.Logger;

import jp.saka1029.cspj.problem.ConstraintPredicate5;
import jp.saka1029.cspj.problem.Domain;
import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class SendMoreMoney extends SolverMain {

	static final Logger logger = Logger.getLogger(SendMoreMoney.class.getName());
	
    Variable<Integer> s, e, n, d, m, o, r, y;
    Variable<Integer> c1, c2, c3, c4;

    @Override
    public void define() {
        Domain<Integer> digits = Domain.range(0, 9);
        Domain<Integer> first = Domain.range(1, 9);
        Domain<Integer> carry = Domain.of(0, 1);
        s = problem.variable("s", first);
        e = problem.variable("e", digits);
        n = problem.variable("n", digits);
        d = problem.variable("d", digits);
        m = problem.variable("m", first);
        o = problem.variable("o", digits);
        r = problem.variable("r", digits);
        y = problem.variable("y", digits);
        c1 = problem.variable("c1", carry);
        c2 = problem.variable("c2", carry);
        c3 = problem.variable("c3", carry);
        c4 = problem.variable("c4", carry);
        //c4 c3 c2 c1
        //    s  e  n  d
        // +  m  o  r  e
        //--------------
        // m  o  n  e  y
        ConstraintPredicate5<Integer, Integer, Integer, Integer, Integer, Integer> f = (a, b, c, d, e) -> a + b + c == d + e * 10;
        String fn = "%s + %s + %s == %s + %s * 10";
//        String fn = "constraint";
        Variable<Integer> Z = problem.constant(0);
//        problem.constraint(fn, (b, c, d, e) -> b + c == d + e * 10,  d, e, y, c1);	// <<<<< compile OK 1 constraint, 0 variables
        constraint(fn, f, Z, d, e, y, c1);
//        ceq(plus(d, e), plus(y, mult(c1, 10)));	// <<<<< compile OK 4 constraints, 3 variables
//        cand(eq(plus(d, e), plus(y, mult(c1, 10))));	// <<<<< compile OK 4 constraints, 3 variables
        constraint(fn, f, c1, n, r, e, c2);
        constraint(fn, f, c2, e, o, n, c3);
        constraint(fn, f, c3, s, m, o, c4);
        constraint(fn, f, c4, Z, Z, m, Z); // <<<<< compile OK
//        problem.constraint("==", a -> a.get(0) == a.get(1), c4, m); // <<<<< compile OK
//        problem.constraint("==", Problem.CEQ, c4, m); // <<<<< compile OK
//        ceq(c4, m); // <<<<< compile OK
//        cne(s, e); // <<<<< compile OK
//        ceq(m, 1);	// <<<<< compile OK
        allDifferent(s, e, n, d, m, o, r, y); // <<<<< compile OK
//        allDifferent(s, e, n, d, m, o, r, y); // <<<<< compile OK
//        problem.forEachPairs("!=", a -> !a.get(0).equals(a.get(1)), s, e, n, d, m, o, r, y); // <<<<< compile OK
//        problem.forEachPairs("!=", Problem.NE, s, e, n, d, m, o, r, y); // <<<<< compile error
//        ConstraintPredicate<Integer> NE = a -> !a.get(0).equals(a.get(1));
//        problem.forEachPairs("!=", NE, s, e, n, d, m, o, r, y); // <<<<< compile OK
    }

    void info(String format, Object... args) {
    	logger.info(String.format(format, args));
    }

    @Override
    public boolean answer(int c, Result x) {
    	info("***** answer %d *****", c);
        info(" %s%s%s%s", x.get(s), x.get(e), x.get(n), x.get(d));
        info("+%s%s%s%s", x.get(m), x.get(o), x.get(r), x.get(e));
        info("-----");
        info("%s%s%s%s%s", x.get(m), x.get(o), x.get(n), x.get(e), x.get(y));
        return true;
    }
    
    public static void main(String[] args) throws Exception {
        new SendMoreMoney().parse(args).solve();
    }

}
