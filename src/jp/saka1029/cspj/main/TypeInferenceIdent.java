package jp.saka1029.cspj.main;

import java.io.IOException;
import java.util.logging.Logger;

import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class TypeInferenceIdent extends SolverMain {

	static final Logger logger = Logger.getLogger(TypeInferenceIdent.class.getName());

	enum T { Int, Bool, Str };
	Domain<T> types = Domain.of(T.values());
	
	// ident(x) = x;
	//
	// x : %0
	// ident(%0) : %1
	//
	// %0 : %1

	Variable<T> v0 = problem.variable("v0", types);
	Variable<T> v1 = problem.variable("v1", types);
	
	@Override
	public void define() throws IOException {
		problem.constraint("c0", (a, b) -> a == b, v0, v1);
	}

	@Override
	public boolean answer(int n, Result result) throws IOException {
		logger.info(String.format("%s=%s", v0, result.get(v0)));
		logger.info(String.format("%s=%s", v1, result.get(v1)));
		return true;
	}

	public static void main(String[] args) throws Exception {
		new TypeInferenceIdent().parse(args).solve();
	}

}
