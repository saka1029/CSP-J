package jp.saka1029.cspj.solver.choco;

import java.util.EnumSet;
import java.util.List;

import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

import jp.saka1029.cspj.problem.old.Bind;
import jp.saka1029.cspj.problem.old.Constraint;
import jp.saka1029.cspj.problem.old.Domain;
import jp.saka1029.cspj.problem.old.Log;
import jp.saka1029.cspj.problem.old.Problem;
import jp.saka1029.cspj.problem.old.Variable;
import jp.saka1029.cspj.solver.Answer;
import jp.saka1029.cspj.solver.Debug;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.Solver;

public class ChocoSolver implements Solver {

	private static final EnumSet<Debug> DEFAULT_DEBUG = EnumSet.noneOf(Debug.class);
	private EnumSet<Debug> debug = DEFAULT_DEBUG;
	
	@Override
	public void solve(Problem problem, Answer answer) {
        long start = System.currentTimeMillis();
        if (debug.contains(Debug.Problem))
            problem.info();
        Bind b = problem.bind();
        if (b == null) return;
        Bind bind = new Bind(b);
		org.chocosolver.solver.Solver solver = new org.chocosolver.solver.Solver();
		Object[][] map = bind.map();
		int size = problem.variables().size();
		IntVar[] vars = new IntVar[size];
		for (int i = 0; i < size; ++i) {
			Variable<?> v = problem.variable(i);
			Domain<?> domain = bind.get(v);
			vars[i] = VariableFactory.bounded("v" + i, 0, domain.size() - 1, solver);
		}
		for (Variable<?> v : problem.variables()) {
			if (!(v instanceof Constraint<?>)) continue;
			Constraint<?> c = (Constraint<?>)v;
			List<Variable<?>> arguments = c.variableArguments();
			int argsSize = arguments.size();
			IntVar[] ivars = new IntVar[argsSize + 1];
			ivars[0] = vars[c.no];
			for (int i = 0; i < argsSize; ++i)
				ivars[i + 1] = vars[arguments.get(i).no];
			Tuples tuples = new Tuples();
			c.encodeVariable(bind.get(c), bind,
				(index, value, args, argsIndex, argsValue) -> {
					if (index == -1) return;
                    int[] indexes = new int[argsSize + 1];
                    indexes[0] = index;
                    for (int i = 0; i < argsSize; ++i)
                    	indexes[i + 1] = argsIndex[i];
                    tuples.add(indexes);
				});
			solver.post(ICF.table(ivars,  tuples, "STR2+"));
		}
        final int[] answerCount = {0};
        boolean found = solver.findSolution();
		while (found) {
        	++answerCount[0];
        	Result result = new Result();
        	for (int i = 0, max = vars.length; i < max; ++i) {
        		IntVar var = vars[i];
        		result.put(problem.variable(i), map[i][var.getValue()]);
        	}
        	if (!answer.answer(result)) break;
        	found = solver.nextSolution();
        }
        Log.info("ChocoSolver: vars=%d answers=%d elapse=%dms",
            problem.variables().size(), answerCount[0], System.currentTimeMillis() - start);
	}

	@Override
	public EnumSet<Debug> debug() {
		return debug;
	}

	@Override
	public void debug(EnumSet<Debug> debug) {
		this.debug = debug;
	}

}
