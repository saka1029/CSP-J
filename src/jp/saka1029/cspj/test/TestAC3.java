package jp.saka1029.cspj.test;

import static org.junit.Assert.*;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Answer;
import jp.saka1029.cspj.solver.Debug;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.basic.BasicSolver;

import org.junit.Test;

public class TestAC3 {

	/**
	 * http://en.wikipedia.org/wiki/AC-3_algorithm
	 * 
	 * AC-3 algorithm
	 * --------------
	 * 
	 * For illustration, here is an example of a very simple constraint problem:
	 * X (a variable) has the possible values {0, 1, 2, 3, 4, 5}
	 * -- the set of these values are the domain of X, or D(X).
	 * The variable Y likewise has the domain D(Y) = {0, 1, 2, 3, 4, 5}.
	 * Together with the constraints C1 = "X must be even" and C2 = "X + Y must equal 4"
	 * we have a CSP which AC-3 can solve.
	 * Notice that the actual constraint graph representing this problem must contain two edges between X and Y
	 * since C2 is undirected but the graph representation being used by AC-3 is directed.
	 * It does so by first removing the non-even values out of the domain of X as required by C1,
	 * leaving D(X) = { 0, 2, 4 }.
	 * It then examines the arcs between X and Y implied by C2.
	 * Only the pairs (X=0, Y=4), (X=2, Y=2), and (X=4, Y=0) match the constraint C2.
	 * AC-3 then terminates, with D(X) = {0, 2, 4} and D(Y) = {0, 2, 4}.
	 * 
	 * BasicSolverはAC-3アルゴリズムを満たしている。
	 * 
	 * 2015-02-22T14:42:41.282 情報 Problem: X : [0, 1, 2, 3, 4, 5] 
	 * 2015-02-22T14:42:41.282 情報 Problem: Y : [0, 1, 2, 3, 4, 5] 
	 * 2015-02-22T14:42:41.291 情報 Problem: X % 2 == 0 : [false, true] 
	 * 2015-02-22T14:42:41.291 情報 Problem: X + Y == 4 : [false, true] 
	 * 2015-02-22T14:42:41.307 情報 BasicSolver: **** reduced problem **** 
	 * 2015-02-22T14:42:41.307 情報 BasicSolver: X : [0, 2, 4] 
	 * 2015-02-22T14:42:41.307 情報 BasicSolver: Y : [0, 2, 4] 
	 * 2015-02-22T14:42:41.307 情報 BasicSolver: X % 2 == 0 : [true] 
	 * 2015-02-22T14:42:41.307 情報 BasicSolver: X + Y == 4 : [true] 
	 * 2015-02-22T14:42:41.307 情報 BasicSolver: ***** start backtrack ***** 
	 * 2015-02-22T14:42:41.354 情報 X = 4 Y = 0 
	 * 2015-02-22T14:42:41.354 情報 X = 2 Y = 2 
	 * 2015-02-22T14:42:41.354 情報 X = 0 Y = 4 
	 * 2015-02-22T14:42:41.354 情報 BasicSolver: vars=4 reduced vars=2 answers=3 elapse=103ms 
	 */
	@Test
	public void testAC3() {
		Problem problem = new Problem();
		Variable<Integer> x = problem.variable("X", Domain.range(0, 5));
		Variable<Integer> y = problem.variable("Y", Domain.range(0, 5));
		problem.constraint(a -> (int)a[0] % 2 == 0, "%s %% 2 == 0", x);
		problem.constraint(a -> (int)a[0] + (int)a[1] == 4, "%s + %s == 4", x, y);
		BasicSolver solver = new BasicSolver(Debug.ALL);
		solver.solve(problem, new Answer() {
			@Override
			public boolean answer(Result result) {
				Log.info("X = %s Y = %s", result.get(x), result.get(y));
				return true;
			}
		});
	}

}
