package jp.saka1029.cspj.solver.basic;

import java.util.logging.Logger;

import jp.saka1029.cspj.problem.Bind;
import jp.saka1029.cspj.problem.Constraint;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Answer;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.Solver;

public class BasicSolver implements Solver {

	static final Logger logger = Logger.getLogger(BasicSolver.class.getName());
	
    private Problem problem;
    private Answer answer;
    private int answerCount;
    
//    static class Bin {
//    	final Variable<?> v;
//    	final Domain<?> d;
//    	Bin(Variable<?> v, Domain<?> d) { this.v = v; this.d = d; }
//    }

    private Variable<?> select(Bind bind) {
//    	return reduced.stream()
//    		.map(v -> new Bin(v, bind.get(v)))
//    		.filter(e -> e.d.size() > 1)
//    		.peek(e -> { if (e.d.size() == 0) throw new IllegalStateException("empty domain found"); })
//    		.min((a, b) -> Math.min(a.d.size(), b.d.size()))
//    		.orElse(new Bin(null, null)).v;
        Variable<?> sel = null;
        int min = Integer.MAX_VALUE;
        for (Variable<?> v : problem.variables) {
            Domain<?> d = bind.get(v);
            int size = d.size();
            if (size <= 0) throw new IllegalStateException("empty domain found in bound");
            if (size == 1) continue;
            if (sel == null || size < min) {
                sel = v;
                min = size;
            }
        }
        return sel;
    }

    private boolean solveSimple(Bind bind) {
        boolean b = true;
        Variable<?> var = select(bind);
        if (var == null) {
            ++answerCount;
            b = answer.answer(new Result(problem, bind));
        } else
            for (Domain<?> d : bind.get(var).singleDomains()) {
                Bind newBind = new Bind(bind);
                if (var.rawBind(d, newBind))
                    b = solveSimple(newBind);
                if (!b) break;
            }
        return b;
    }
    
//    private boolean solveParallel(Bind bind, boolean parallel) {
//        boolean b = true;
//        Variable<?> var = select(bind);
//        if (var == null) {
//            ++answerCount;
//            b = answer.answer(new Result(problem, bind));
//        } else
//        	return bind.get(var).stream(parallel)
//        		.map(i -> {
//        			Bind nb = new Bind(bind);
//        			return var.bindSingle(i, nb) ? nb : null;
//        		})
//        		.filter(x -> x != null)
//        		.allMatch(x -> solveParallel(x, false));
//        return b;
//    }
    
    private void solve(Problem problem) {
        long start = System.currentTimeMillis();
        this.answerCount = 0;
        Bind b = problem.bind();
        if (b == null) return;
        int reducedVariables = b.notUniqueVariableSize();
        logger.info("BasicSolver: **** reduced problem ****");
        for (Variable<?> v : problem.variables)
            logger.info("BasicSolver: variable " + v + " : " + b.get(v));
        for (Constraint c : problem.constraints)
            logger.info("BasicSolver: constraint " + c);
        Bind bind = new Bind(b);
        solveSimple(bind); 
//        solveParallel(bind, false); 
        logger.info(String.format("BasicSolver: variables=%d reduced variables=%d constraints=%d answers=%d elapse=%dms",
            problem.variables.size(), reducedVariables, problem.constraints.size(),
            answerCount, System.currentTimeMillis() - start));
    }

    @Override
    public void solve(Problem problem, Answer answer) {
        this.problem = problem;
        this.answer = answer;
        solve(problem);
    }
}