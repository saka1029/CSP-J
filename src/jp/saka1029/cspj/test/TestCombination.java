package jp.saka1029.cspj.test;

import static jp.saka1029.cspj.problem.Helper.*;

import org.junit.Test;

import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;

public class TestCombination {

    @Test
    public void test() {
        Problem problem = new Problem();
        Domain<Integer> range = Domain.range(1, 3);
        Variable<Integer> a = problem.variable("a", range);
        Variable<Integer> b = problem.variable("b", range);
        allDifferent(a, b);
        constraint(lt(a, b));
        Solver solver = new BasicSolver();
        solver.solve(problem, result -> {
            System.out.println(result);
            return true;
        });



    }

}
