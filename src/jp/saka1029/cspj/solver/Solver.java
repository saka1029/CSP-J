package jp.saka1029.cspj.solver;

import jp.saka1029.cspj.problem.Bind;
import jp.saka1029.cspj.problem.Problem;

public interface Solver {

    void solve(Problem problem, Answer answer);

    void solve(Problem problem, Bind bind, Answer answer);

}
