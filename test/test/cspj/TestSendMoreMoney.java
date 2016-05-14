package test.cspj;

import static jp.saka1029.cspj.problem.Helper.*;

import org.junit.Test;

import jp.saka1029.cspj.problem.ConstraintPredicate5;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;

public class TestSendMoreMoney {

    @Test
    public void testSendMoreMoney() {
        Problem problem = new Problem();
        Domain<Integer> digits = Domain.range(0, 9);
        Domain<Integer> first = Domain.range(1, 9);
        Domain<Integer> carry = Domain.of(0, 1);
        Variable<Integer> s = problem.variable("s", first);
        Variable<Integer> e = problem.variable("e", digits);
        Variable<Integer> n = problem.variable("n", digits);
        Variable<Integer> d = problem.variable("d", digits);
        Variable<Integer> m = problem.variable("m", first);
        Variable<Integer> o = problem.variable("o", digits);
        Variable<Integer> r = problem.variable("r", digits);
        Variable<Integer> y = problem.variable("y", digits);
        Variable<Integer> c1 = problem.variable("c1", carry);
        Variable<Integer> c2 = problem.variable("c2", carry);
        Variable<Integer> c3 = problem.variable("c3", carry);
        Variable<Integer> c4 = problem.variable("c4", carry);
        //c4 c3 c2 c1
        //    s  e  n  d
        // +  m  o  r  e
        //--------------
        // m  o  n  e  y
        ConstraintPredicate5<Integer, Integer, Integer, Integer, Integer, Integer> f = (a, b, c, p, q) -> a + b + c == p + q * 10;
        String fn = "%s + %s + %s == %s + %s * 10";
        Variable<Integer> Z = problem.constant(0);
        constraint(fn, f, Z, d, e, y, c1);
        constraint(fn, f, c1, n, r, e, c2);
        constraint(fn, f, c2, e, o, n, c3);
        constraint(fn, f, c3, s, m, o, c4);
        constraint(fn, f, c4, Z, Z, m, Z);
        allDifferent(s, e, n, d, m, o, r, y);
        Solver solver = new BasicSolver();
        solver.solve(problem, result -> {
            System.out.println(result);
            return true;
        });
    }

    static int seq(int... digits) {
        int r = 0;
        for (int d : digits)
            r = r * 10 + d;
        return r;
    }

    @Test
    public void testSendMoreMoney2() {
        Problem problem = new Problem();
        Domain<Integer> digits = Domain.range(0, 9);
        Domain<Integer> first = Domain.range(1, 9);
        Variable<Integer> S = problem.variable("s", first);
        Variable<Integer> E = problem.variable("e", digits);
        Variable<Integer> N = problem.variable("n", digits);
        Variable<Integer> D = problem.variable("d", digits);
        Variable<Integer> M = problem.variable("m", first);
        Variable<Integer> O = problem.variable("o", digits);
        Variable<Integer> R = problem.variable("r", digits);
        Variable<Integer> Y = problem.variable("y", digits);
        //    s  e  n  d
        // +  m  o  r  e
        //--------------
        // m  o  n  e  y
        allDifferent(S, E, N, D, M, O, R, Y);
        constraint("calculate",
            (s, e, n, d, m, o, r, y) -> seq(s, e, n, d) + seq(m, o, r, e) == seq(m, o, n, e, y),
             S, E, N, D, M, O, R, Y);
        Solver solver = new BasicSolver();
        solver.solve(problem, result -> {
            System.out.println(result);
            return true;
        });
    }

    @Test
    public void testPerm() {
        Problem problem = new Problem();
        Domain<Integer> dom = Domain.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Variable<Integer> a = problem.variable("a", dom);
        Variable<Integer> b = problem.variable("b", dom);
        Variable<Integer> c = problem.variable("c", dom);
        Variable<Integer> d = problem.variable("d", dom);
        Variable<Integer> e = problem.variable("e", dom);
        Variable<Integer> f = problem.variable("f", dom);
        Variable<Integer> g = problem.variable("g", dom);
        Variable<Integer> h = problem.variable("h", dom);
        Variable<Integer> i = problem.variable("i", dom);
        allDifferent(a, b, c, d, e, f, g, h);
        Solver solver = new BasicSolver();
        solver.solve(problem, result -> {
            System.out.println(result);
            return true;
        });
    }
}
