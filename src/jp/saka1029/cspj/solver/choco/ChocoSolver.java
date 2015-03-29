package jp.saka1029.cspj.solver.choco;

import java.util.logging.Logger;

import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

import jp.saka1029.cspj.problem.Bind;
import jp.saka1029.cspj.problem.Constraint;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Answer;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.Solver;

public class ChocoSolver implements Solver {

	static final Logger logger = Logger.getLogger(ChocoSolver.class.getName());
	
	@Override
	public void solve(Problem problem, Answer answer) {
        long start = System.currentTimeMillis();
        Bind b = problem.bind();
        if (b == null) return;
        int reducedVariables = b.notUniqueVariableSize();
        Bind bind = new Bind(b);
		org.chocosolver.solver.Solver solver = new org.chocosolver.solver.Solver();
		Object[][] map = bind.map();
		int size = problem.variables.size();
		IntVar[] vars = new IntVar[size];
		for (int i = 0; i < size; ++i) {
			Variable<?> v = problem.variable(i);
			Domain<?> domain = bind.get(v);
			vars[i] = VariableFactory.bounded("v" + i, 0, domain.size() - 1, solver);
		}
		for (Constraint c : problem.constraints) {
			int varSize = c.variables.size();
			IntVar[] ivars = new IntVar[varSize];
			for (int i = 0; i < varSize; ++i)
				ivars[i] = vars[c.variables.get(i).no];
			Tuples tuples = new Tuples();
			c.encode(bind, true, (indices, values) -> {
				int[] valueIndices = new int[indices.size()];
				for (int i = 0; i < varSize; ++i)
					valueIndices[i] = indices.get(i);
				tuples.add(valueIndices);
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
        logger.info(String.format("ChocoSolver: variables=%d reduced variables=%d constraints=%d answers=%d elapse=%dms",
            problem.variables.size(), reducedVariables, problem.constraints.size(),
            answerCount[0], System.currentTimeMillis() - start));
	}

}
