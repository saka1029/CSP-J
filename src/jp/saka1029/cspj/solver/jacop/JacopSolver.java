package jp.saka1029.cspj.solver.jacop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.jacop.constraints.ExtensionalSupportVA;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.InputOrderSelect;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;

import jp.saka1029.cspj.problem.Bind;
import jp.saka1029.cspj.problem.Constraint;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Answer;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.Solver;

public class JacopSolver implements Solver {

	static final Logger logger = Logger.getLogger(JacopSolver.class.getName());
	
	@Override
	public void solve(Problem problem, Answer answer) {
        long start = System.currentTimeMillis();
        Bind b = problem.bind();
        if (b == null) return;
        Bind bind = new Bind(b);
		Store store = new Store();
		Object[][] map = bind.map();
		int size = problem.variables.size();
		IntVar[] vars = new IntVar[size];
		for (int i = 0; i < size; ++i) {
			Variable<?> v = problem.variable(i);
			Domain<?> domain = bind.get(v);
			vars[i] = new IntVar(store, "v" + i, 0, domain.size() - 1);
		}
		for (Constraint c : problem.constraints) {
			int varSize = c.variables.size();
			IntVar[] ivars = new IntVar[varSize];
			for (int i = 0; i < varSize; ++i)
				ivars[i] = vars[c.variables.get(i).no];
			List<int[]> list = new ArrayList<>();
			c.encode(bind, true, (indices, values) -> {
				int[] indexArray = new int[varSize];
				for (int i = 0; i < varSize; ++i)
					indexArray[i] = indices.get(i);
				list.add(indexArray);
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
        logger.info(String.format("JacopSolver: vars=%d answers=%d elapse=%dms",
            problem.variables.size(), answerCount[0], System.currentTimeMillis() - start));
	}

}
