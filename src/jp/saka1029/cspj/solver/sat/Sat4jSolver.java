package jp.saka1029.cspj.solver.sat;

import java.util.List;
import java.util.logging.Logger;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import jp.saka1029.cspj.problem.Bind;
import jp.saka1029.cspj.problem.Constraint;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Answer;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.sat.VariableMap;


public class Sat4jSolver implements Solver {

	static final Logger logger = Logger.getLogger(Sat4jSolver.class.getName());
	
    private VariableMap map;
    private ISolver solver;
    
    private Answer answer;

    private String toString(int var) {
        int v = Math.abs(var);
        int lv = v - 1;		// SAT4j's logical variable number begins with 1
        boolean sign = var < 0;
        return String.format("%s%d(%s)",
            sign ? "-" : " ", v, map.multiValues.get(lv).toString(sign));
    }

    private String toString(VecInt lits) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0, size = lits.size(); i < size; ++i) {
            int l = lits.get(i);
            if (i > 0) sb.append(", ");
            sb.append(toString(l));
        }
        sb.append("]");
        return sb.toString();
    }

    private void addClause(VecInt lits) {
        logger.finest("clause: " + toString(lits));
        try {
			solver.addClause(lits);
		} catch (ContradictionException e) {
			throw new RuntimeException(e);
		}
    }

	public int lit(Variable<?> variable, Object value, boolean sign) {
		VariableMap.SatLiteral lit = map.get(variable, value, sign);
		if (lit == null)
			return Integer.MIN_VALUE;
		// SAT4j's logical variable number begins with 1
		return (lit.variable + 1) * (lit.sign ? -1 : 1);
	}

	public VecInt clause(int... lits) {
        VecInt vec = new VecInt();
        for (int i : lits)
        	vec.push(i);
        return vec;
	}

    private void encode(Variable<?> variable, Bind bind) {
        Domain<?> domain = bind.get(variable);
        List<?> list = domain.asList();
        int size = domain.size();
        if (size == 2) {
        	addClause(clause(
                lit(variable, list.get(0), false),
                lit(variable, list.get(0), true)));
        } else if (size > 2) {
        	VecInt vec = new VecInt();
        	for (Object v : domain)
        		vec.push(lit(variable, v, false));
            addClause(vec);
            for (int i = 0; i < size; ++i)
            	for (int j = i + 1; j < size; ++j)
            		addClause(clause(
                        lit(variable, list.get(i), true),
                        lit(variable, list.get(j), true)));
        }
    }
    
    private void encode(Constraint c, Bind bind) {
    	c.encode(bind, false, (indices, values) -> {
            VecInt vec = new VecInt();
            for (int i = 0, size = values.size(); i < size; ++i){
                int lit = lit(c.variables.get(i), values.get(i), true);
                if (lit != Integer.MIN_VALUE) vec.push(lit);
            }
    		addClause(vec);
    	});
    }

	
	void result(Result result, VecInt deny) {
		for (VariableMap.VarValue v : map.singleValues)
			result.put(v.variable, v.value);
		for (int i = 1, size = solver.nVars(); i <= size; ++i) {
            VariableMap.VarValue2 v = map.multiValues.get(i - 1);
            if (solver.model(i)) {
                result.put(v.variable, v.value);
                deny.push(-i);
            } else {
                if (v.falseValue != null) {
                    result.put(v.variable, v.falseValue);
                    deny.push(i);
                }
            }
        }
	}

    public void solve(Problem problem) {
        long start = System.currentTimeMillis();
        Bind bind = problem.bind();
        if (bind == null) return;
        this.map = new VariableMap(problem, bind);
        this.solver = SolverFactory.newDefault();
        for (Variable<?> v : problem.variables)
            encode(v, bind);
        for (Constraint c : problem.constraints)
            encode(c, bind);
        int nConstraints = solver.nConstraints();
        int count = 0;
        while (true) {
            boolean r = false;
			try {
				r = solver.isSatisfiable();
			} catch (TimeoutException e1) {
				throw new RuntimeException(e1);
			}
            if (!r) break;
            ++count;
            Result result = new Result();
            VecInt deny = new VecInt();
            result(result, deny);
            if (!answer.answer(result)) break;
            // add a negative clause of the answer to search next solution.
            if (deny.isEmpty()) break;
            try {
				solver.addClause(deny);
			} catch (ContradictionException e) {
				throw new RuntimeException(e);
			}
        }
        logger.info(String.format("Sat4jSolver: variables=%d reduced variables=%d logical vars=%d constraints=%d->%d answers=%d elapse=%dms",
            problem.variables.size(), bind.notUniqueVariableSize(),
            solver.nVars(), nConstraints, solver.nConstraints(), count, System.currentTimeMillis() - start));
    }

    @Override
    public void solve(Problem problem, Answer answer) {
        this.answer = answer;
        solve(problem);
    }

}
