package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.io.IOException;

import jp.saka1029.cspj.main.SendMoreMoney;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Debug;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;

import org.junit.Test;

public class TestBasicSolverTrace {

    @Test
    public void test() {
        Log.methodName();
        Problem p = new Problem();
        Variable<Integer> a = p.variable("a", Domain.of(1, 2, 3));
        Variable<Integer> b = p.variable("b", Domain.of(2, 3, 4));
        Variable<Integer> c = p.variable("c", Domain.of(1, 2, 3));
        Variable<Integer> d = p.variable("d", Domain.of(1, 2, 3));
        Variable<Integer> ab = p.constraint(x -> (int)x[0] + (int) x[1], "+", a, b);
        Variable<Boolean> abc = p.constraint(x -> (int)x[0] == (int) x[1], "==", ab, c);
        Variable<Boolean> ad = p.constraint(x -> (int)x[0] == (int) x[1], "==", a, d);
        Solver s = new BasicSolver(Debug.ALL);
        s.solve(p, r -> { Log.info(r); return true; });
    }
    
    @Test
    public void testSendMoreMoney() throws IOException {
        Log.methodName();
        assertEquals(1, new SendMoreMoney().solver(new BasicSolver(Debug.ALL)).solve());
    }
}
