package jp.saka1029.cspj.main;

import java.io.IOException;
import java.util.logging.Logger;

import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class Australia extends SolverMain {

	static final Logger logger = Logger.getLogger(Australia.class.getName());

	enum Color {R, G, B}
	
	Variable<Color> WA, NT, SA, Q, NSW, V, T;
	
	@Override
	public void define() throws IOException {
		Domain<Color> domain = Domain.of(Color.values());
		WA = problem.variable("WA", domain);
		NT = problem.variable("NT", domain);
		SA = problem.variable("SA", domain);
		Q = problem.variable("Q", domain);
		NSW = problem.variable("NSW", domain);
		V = problem.variable("V", domain);
		T = problem.variable("T", domain);
		constraint(ne(WA, NT)); constraint(ne(WA, SA));
		constraint(ne(NT, Q)); constraint(ne(NT, SA));
		constraint(ne(SA, Q)); constraint(ne(SA, NSW)); constraint(ne(SA, V));
		constraint(ne(Q, NSW));
		constraint(ne(NSW, V));
	}

	@Override
	public boolean answer(int n, Result r) throws IOException {
		logger.info(String.format("WA=%s, NT=%s, SA=%s, Q=%s, NSW=%s, V=%s, T=%s",
            r.get(WA), r.get(NT), r.get(SA), r.get(Q), r.get(NSW), r.get(V), r.get(T)));
		assert r.get(WA) == r.get(Q);
		assert r.get(WA) == r.get(V);
		assert r.get(NT) == r.get(NSW);
		return true;
	}

	public static void main(String[] args) throws IOException, Exception {
		new Australia().parse(args).solve();
	}

}
