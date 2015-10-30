package jp.saka1029.cspj.main;

import java.io.IOException;
import java.util.logging.Logger;

import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class TypeInference extends SolverMain {

	static final Logger logger = Logger.getLogger(TypeInference.class.getName());

	enum T { Int, Bool, Str };
	Domain<T> types = Domain.of(T.values());
	
	// fact(n) = if n == 0 then 1 else n * fact(n - 1);
	//
	// n : %0
	// fact(%0) : %1
	//
	// if(%2, %3, %4) : %1
	// ==(%0, int) : %2
	// int : %3
	// *(%0, %1) : %4
	// -(%0, int) : %0

	// ==(int, int) : bool
	// ==(str, str) : bool
	// ==(bool, bool) : bool
	// *(int, int) : int
	// -(int, int) : int
	// if(bool, %9, %9) : %9

	Variable<T> INT = problem.constant(T.Int);
	Variable<T> v0 = problem.variable("v0", types);
	Variable<T> v1 = problem.variable("v1", types);
	Variable<T> v2 = problem.variable("v2", types);
	Variable<T> v3 = problem.variable("v3", types);
	Variable<T> v4 = problem.variable("v4", types);
	
	@FunctionalInterface
	interface TypeRule {
		boolean test(String name, T ret, T[] args);
	}
	
	TypeRule[] rules = {
		(n, r, a) -> n.equals("==") && a.length == 2 && r == T.Bool && a[0] == T.Int && a[1] == T.Int,
		(n, r, a) -> n.equals("==") && a.length == 2 && r == T.Bool && a[0] == T.Str && a[1] == T.Str,
		(n, r, a) -> n.equals("==") && a.length == 2 && r == T.Bool && a[0] == T.Bool && a[1] == T.Bool,
		(n, r, a) -> n.equals("*") && a.length == 2 && r == T.Int && a[0] == T.Int && a[1] == T.Int,
		(n, r, a) -> n.equals("-") && a.length == 2 && r == T.Int && a[0] == T.Int && a[1] == T.Int,
		(n, r, a) -> n.equals("if") && a.length == 3 && r == a[1] && a[0] == T.Bool && a[1] == a[2],
	};
	
	boolean rule(String name, T ret, T... args) {
		for (TypeRule e : rules)
			if (e.test(name, ret, args))
				return true;
        return false;
	}

	@Override
	public void define() throws IOException {
		problem.constraint("c0", (a, b, c, d) -> rule("if", a, b, c, d), v1, v2, v3, v4);
		problem.constraint("c1", (a, b, c) -> rule("==", a, b, c), v2, v0, INT);
		problem.constraint("c2", (a) -> a == T.Int, v3);
		problem.constraint("c3", (a, b, c) -> rule("*", a, b, c), v4, v0, v1);
		problem.constraint("c4", (a, b, c) -> rule("-", a, b, c), v0, v0, INT);
	}

	@Override
	public boolean answer(int n, Result result) throws IOException {
		logger.info(String.format("%s=%s", v0, result.get(v0)));
		logger.info(String.format("%s=%s", v1, result.get(v1)));
		logger.info(String.format("%s=%s", v2, result.get(v2)));
		logger.info(String.format("%s=%s", v3, result.get(v3)));
		logger.info(String.format("%s=%s", v4, result.get(v4)));
		return true;
	}

	public static void main(String[] args) throws Exception {
		new TypeInference().parse(args).solve();
	}

}
