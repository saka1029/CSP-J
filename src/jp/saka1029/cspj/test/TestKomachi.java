package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;







import jp.saka1029.cspj.problem.old.Domain;
import jp.saka1029.cspj.problem.old.Log;
import jp.saka1029.cspj.problem.old.Problem;
import jp.saka1029.cspj.problem.old.Variable;






import jp.saka1029.cspj.solver.old.Debug;
import jp.saka1029.cspj.solver.old.Solver;
import jp.saka1029.cspj.solver.old.basic.BasicSolver;

import org.junit.Test;

public class TestKomachi {

	enum Operator {
		None(""), Minus("-"), Plus("+");
		public final String name;
		Operator(String name) { this.name = name; }
		@Override public String toString() { return name; }
	}
	
	List<Variable<Operator>> variables = new ArrayList<>();

	static long sum(long total, Operator op, long acc) {
		switch (op) {
		case None: return acc;
		case Minus: return total - acc;
		case Plus: return total + acc;
		default: throw new IllegalArgumentException("unknown Operator: " + op);
		}
	}

	static long calculate(Object... ops) {
        long total = 0, acc = 0;
        Operator prev = Operator.None;
        for (int i = 1; i <= 9; ++i) {
            Operator op = (Operator)ops[i - 1];
            switch (op) {
            case None:
                acc = acc * 10 + i;
                break;
            case Minus: case Plus:
            	total = sum(total, prev, acc);
                prev = op;
                acc = i;
                break;
            }
        }
        total = sum(total, prev, acc);
        return total;
	}

	static final long TOTAL = 100;
	
	@Test
	public void test() {
		Problem problem = new Problem();
		Domain<Operator> domain = Domain.of(Operator.values());
		variables.add(problem.variable("o1", Domain.of(Operator.None, Operator.Minus)));
		for (int i = 2; i <= 9; ++i)
			variables.add(problem.variable("o" + i, domain));
		problem.constraint(a -> calculate(a) == TOTAL, "constraint", variables);
		Solver solver = new BasicSolver(Debug.ALL);
		solver.solve(problem, result -> {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i <= 9; ++i)
				sb.append(result.get(variables.get(i - 1))).append(i);
			sb.append(" == ").append(TOTAL);
			Log.info(sb.toString());
			return true;
		});
	}

}
