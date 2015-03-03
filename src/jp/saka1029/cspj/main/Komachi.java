package jp.saka1029.cspj.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class Komachi extends SolverMain {

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

	static long calculate(int[] digits, Object... ops) {
        long total = 0, acc = 0;
        Operator prev = Operator.None;
        for (int i = 0, size = digits.length; i < size; ++i) {
            Operator op = (Operator)ops[i];
            switch (op) {
            case None:
                acc = acc * 10 + digits[i];
                break;
            case Minus: case Plus:
            	total = sum(total, prev, acc);
                prev = op;
                acc = digits[i];
                break;
            }
        }
        total = sum(total, prev, acc);
        return total;
	}

//	static final int[] DIGITS = {1, 2, 3, 4, 5, 6, 7, 8, 9};
	static final int[] DIGITS = {9, 8, 7, 6, 5, 4, 3, 2, 1};
	static final long TOTAL = 100;
	
	@Override
	public void define() throws IOException {
		Domain<Operator> rest = Domain.of(Operator.values());
		Domain<Operator> domain = Domain.of(Operator.None, Operator.Minus);
		for (int i = 0, size = DIGITS.length; i < size; ++i) {
			variables.add(problem.variable("o" + i, domain));
			domain = rest;
		}
		problem.constraint(a -> calculate(DIGITS, a) == TOTAL, "constraint", variables);
	}

	@Override
	public boolean answer(int n, Result result) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = DIGITS.length; i < size; ++i)
            sb.append(result.get(variables.get(i))).append(DIGITS[i]);
        sb.append(" = ").append(TOTAL);
        Log.info("%4d: %s", n, sb.toString());
        return true;
	}

	public static void main(String[] args) throws Exception {
		new Komachi().parse(args).solve();
	}

}
