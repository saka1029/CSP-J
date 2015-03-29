package jp.saka1029.cspj.main.old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.saka1029.cspj.geometry.Printer;
import static jp.saka1029.cspj.problem.old.Helper.*;
import jp.saka1029.cspj.problem.old.Domain;
import jp.saka1029.cspj.problem.old.Log;
import jp.saka1029.cspj.problem.old.Variable;
import jp.saka1029.cspj.solver.old.Result;
import jp.saka1029.cspj.solver.old.SolverMain;

public class NQueen extends SolverMain {

	int number;
	public NQueen number(int n) { number = n; return this; }
	
	public NQueen() {
		addOption("-n", true, true, a -> number(Integer.parseInt(a)));
	}

	final List<Variable<Integer>> variables = new ArrayList<>();
	
	@Override
	public void define() throws IOException {
		Domain<Integer> domain = Domain.range(0, number - 1);
        List<Variable<Integer>> lefts = new ArrayList<>();
        List<Variable<Integer>> rights = new ArrayList<>();
		for (int i = 0; i < number; ++i) {
			Variable<Integer> v = problem.variable("v" + i, domain);
			variables.add(v);
			lefts.add(plus(v, i));
			rights.add(minus(v, i));
		}
		problem.allDifferent(variables);
		problem.allDifferent(lefts);
		problem.allDifferent(rights);
	}

	@Override
	public boolean answer(int n, Result result) throws IOException {
		Printer printer = new Printer();
		printer.draw(0, 0, number, number);
		for (int x = 0; x < number; ++x)
			printer.draw(x, (int)result.get(variables.get(x)), "*");
		Log.info(printer);
		return false;
	}

	public static void main(String[] args) throws IOException, Exception {
		new NQueen().parse(args).solve();
	}

}
