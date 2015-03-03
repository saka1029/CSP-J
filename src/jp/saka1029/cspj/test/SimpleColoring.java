package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jp.saka1029.cspj.problem.Domain;
import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Answer;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;

import org.junit.Test;

public class SimpleColoring {

	enum Color { Red, Green, Blue }
	
	@Test
	public void test() {
		Problem problem = new Problem();
		Domain<Color> domain = Domain.of(Color.values());
		List<Variable<Color>> variables = new ArrayList<>();
		for (int i = 0; i < 4; ++i)
			variables.add(problem.variable("v" + i, domain));
		ne(variables.get(0), variables.get(1));
		ne(variables.get(0), variables.get(2));
		ne(variables.get(1), variables.get(2));
		ne(variables.get(1), variables.get(3));
		ne(variables.get(2), variables.get(3));
		Solver solver = new BasicSolver();
		int[] answerCount = {0};
		solver.solve(problem, result -> {
            System.out.printf("*** answer %d :", ++answerCount[0]);
            for (Variable<Color> v : variables)
                System.out.printf(" %s = %s", v, result.get(v));
            System.out.println();
            return true;
        });
	}

}
