package jp.saka1029.cspj.test;

import static org.junit.Assert.*;
import static jp.saka1029.cspj.problem.Helper.*;

import org.junit.Test;

import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;

public class TestTwoSameVariablesBug {

    @Test
    public void test() {
        Problem problem = new Problem();
        Domain<Integer> domain = Domain.range(1, 3);
        Variable<Integer> a = problem.variable("a", domain);
        Variable<Integer> b = problem.variable("b", domain);
        constraint("lessThan", (x, y) -> x < y, a, a);
        constraint("lessThan", (x, y) -> x < y, a, b);
        Solver solver = new BasicSolver();
        solver.solve(problem, result -> {
            System.out.println(result);
            return true;
        });
    }

}
