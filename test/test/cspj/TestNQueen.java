package test.cspj;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;

import static jp.saka1029.cspj.problem.Helper.*;

public class TestNQueen {

	@Test
	public void test8Queen() {
		int n = 8;
		Problem problem = new Problem();
		Domain<Integer> d = Domain.range(1, n);
		List<Variable<Integer>> vars = new ArrayList<>();
		for (int i = 0; i < n; ++i)
			vars.add(problem.variable("r" + i, d));
		for(int i  = 0; i < n-1; i++) {
			int ii = i;
		    for(int j = i + 1; j < n; j++) {
		    	int jj = j;
		    	constraint(null, (a, b) -> a != b + 0, vars.get(i), vars.get(j));
		    	constraint(null, (a, b) -> a != b - (jj - ii), vars.get(i), vars.get(j));
		    	constraint(null, (a, b) -> a != b + (jj - ii), vars.get(i), vars.get(j));
		    }
		}
        Solver solver = new BasicSolver();
        solver.solve(problem, result -> {
            System.out.println(result);
            return true;
        });
	}

	@Test
	public void test8QueenAllDifferent() {
		int n = 8;
		Problem problem = new Problem();
		Domain<Integer> d = Domain.range(1, n);
		List<Variable<Integer>> vars = new ArrayList<>();
		for (int i = 0; i < n; ++i)
			vars.add(problem.variable("r" + i, d));
		List<Variable<Integer>> diag1 = new ArrayList<>();
		List<Variable<Integer>> diag2 = new ArrayList<>();
		for (int i = 0; i < n; ++i) {
			int ii = i;
			diag1.add(variable("d" + i, null, a -> a - ii, vars.get(i)));
			diag2.add(variable("e" + i, null, a -> a + ii, vars.get(i)));
		}
		allDifferent(vars);
		allDifferent(diag1);
		allDifferent(diag2);
        Solver solver = new BasicSolver();
        solver.solve(problem, result -> {
            System.out.println(result);
            return true;
        });
	}
}
