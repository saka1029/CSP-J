package jp.saka1029.cspj.solver.old;

import java.util.EnumSet;

import jp.saka1029.cspj.problem.old.Problem;

public interface Solver {

    void solve(Problem problem, Answer answer);

    EnumSet<Debug> debug();
    void debug(EnumSet<Debug> debug);

}
