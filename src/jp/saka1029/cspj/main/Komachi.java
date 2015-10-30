package jp.saka1029.cspj.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jp.saka1029.cspj.geometry.Array;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class Komachi extends SolverMain {

	static final Logger logger = Logger.getLogger(Komachi.class.getName());

	enum Operator {
		None("") { public long sum(long total, long acc) { return acc; }},
		Minus("-") { public long sum(long total, long acc) { return total - acc; }},
		Plus("+") { public long sum(long total, long acc) { return total + acc; }};
		public final String name;
		Operator(String name) { this.name = name; }
		@Override public String toString() { return name; }
		public abstract long sum(long total, long acc);
	}
	
	static long calculate(int[] digits, List<Operator> ops) {
        long total = 0, acc = 0;
        Operator prev = Operator.None;
        for (int i = 0, size = digits.length; i < size; ++i) {
            Operator op = ops.get(i);
            switch (op) {
            case None:
                acc = acc * 10 + digits[i];
                break;
            case Minus: case Plus:
            	total = prev.sum(total, acc);
                prev = op;
                acc = digits[i];
                break;
            }
        }
        total = prev.sum(total, acc);
        return total;
	}

	static final int[] DIGITS = {1, 2, 3, 4, 5, 6, 7, 8, 9};
//	static final int[] DIGITS = {9, 8, 7, 6, 5, 4, 3, 2, 1};
	static final long TOTAL = 100;
	
//	List<Variable<? extends Operator>> variables = new ArrayList<>();
	Array<Variable<? extends Operator>> variables;

	@Override
	public void define() throws IOException {
		Domain<Operator> first = Domain.of(Operator.None, Operator.Minus);
		Domain<Operator> domain = Domain.of(Operator.values());
		variables = Array.of(DIGITS.length, i -> problem.variable("o" + i, i == 0 ? first : domain));
//		Domain<Operator> rest = Domain.of(Operator.values());
//		Domain<Operator> domain = Domain.of(Operator.None, Operator.Minus);
//		for (int i = 0, size = DIGITS.length; i < size; ++i) {
//			variables.set(i, problem.variable("o" + i, domain));
//			domain = rest;
//		}
		problem.constraint("constraint", a -> calculate(DIGITS, a) == TOTAL, variables);
	}

	@Override
	public boolean answer(int n, Result result) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = DIGITS.length; i < size; ++i)
            sb.append(result.get(variables.get(i))).append(DIGITS[i]);
        sb.append(" = ").append(TOTAL);
        logger.info(String.format("%4d: %s", n, sb.toString()));
        return true;
	}

	public static void main(String[] args) throws Exception {
		new Komachi().parse(args).solve();
	}

}
