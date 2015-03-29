package jp.saka1029.cspj.solver.old.jacop;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.jacop.constraints.ExtensionalSupportVA;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.InputOrderSelect;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;

import jp.saka1029.cspj.problem.old.Bind;
import jp.saka1029.cspj.problem.old.Constraint;
import jp.saka1029.cspj.problem.old.Domain;
import jp.saka1029.cspj.problem.old.Log;
import jp.saka1029.cspj.problem.old.Problem;
import jp.saka1029.cspj.problem.old.Variable;
import jp.saka1029.cspj.solver.old.Answer;
import jp.saka1029.cspj.solver.old.Debug;
import jp.saka1029.cspj.solver.old.Result;
import jp.saka1029.cspj.solver.old.Solver;

public class JacopSolver implements Solver {

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
		Store store = new Store();
		Object[][] map = bind.map();
		int size = problem.variables().size();
		IntVar[] vars = new IntVar[size];
		for (int i = 0; i < size; ++i) {
			Variable<?> v = problem.variable(i);
			Domain<?> domain = bind.get(v);
			vars[i] = new IntVar(store, "v" + i, 0, domain.size() - 1);
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
			List<int[]> list = new ArrayList<>();
			c.encodeVariable(bind.get(c), bind,
				(index, value, args, argsIndex, argsValue) -> {
					if (index == -1) return;
                    int[] indexes = new int[argsSize + 1];
                    indexes[0] = index;
                    for (int i = 0; i < argsSize; ++i)
                    	indexes[i + 1] = argsIndex[i];
                    list.add(indexes);
				});
			int tableSize = list.size();
			int[][] table = new int[tableSize][];
			for (int i = 0; i < tableSize; ++i)
				table[i] = list.get(i);
			store.impose(new ExtensionalSupportVA(ivars, table));
		}
        Search<IntVar> search = new DepthFirstSearch<IntVar>(); 
        search.setPrintInfo(false);
        final int[] answerCount = {0};
        search.setSolutionListener(new AnswerListener() {
			@Override
			public boolean answer(Set<IntVar> variables) {
				++answerCount[0];
				Result result = new Result();
                for (IntVar v : variables) {
//                    Log.info(" %s : %s", v.id, v.domain.value());
                    int no = v.index;
                    int d = v.domain.value();
                    result.put(problem.variable(no), map[no][d]);
                }
                return answer.answer(result);
			}
		});
        SelectChoicePoint<IntVar> select = new InputOrderSelect<IntVar>(store, vars, new IndomainMin<IntVar>()); 
        search.labeling(store, select); 
        Log.info("JacopSolver: vars=%d answers=%d elapse=%dms",
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
