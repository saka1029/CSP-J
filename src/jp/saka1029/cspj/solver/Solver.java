package jp.saka1029.cspj.solver;

import java.util.EnumSet;

import jp.saka1029.cspj.problem.Problem;

public interface Solver {

    void solve(Problem problem, Answer answer);

    EnumSet<Debug> debug();
    void debug(EnumSet<Debug> debug);

}
