package jp.saka1029.cspj.solver.sat;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.saka1029.cspj.problem.Bind;
import jp.saka1029.cspj.problem.Constraint;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Answer;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.sat.VariableMap;
import jp.saka1029.minisatj.core.Lit;
import jp.saka1029.minisatj.core.VecLit;


public class MinisatSolver implements Solver {

	static final Logger logger = Logger.getLogger(MinisatSolver.class.getName());
	
    private VariableMap map;
    private jp.saka1029.minisatj.core.Solver solver;
    
    private Answer answer;

    private String toString(Lit l) {
        int lv = l.var();
        return String.format("%s%d(%s)",
            l.sign() ? "!" : " ", lv, map.multiValues.get(lv).toString(l.sign()));
    }

    private String toString(VecLit lits) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0, size = lits.size(); i < size; ++i) {
            Lit l = lits.get(i);
            if (i > 0) sb.append(", ");
            sb.append(toString(l));
        }
        sb.append("]");
        return sb.toString();
    }

    private void addClause(VecLit lits) {
        logger.fine("clause: " + toString(lits));
        solver.addClause(lits);
    }

	public Lit lit(Variable<?> variable, Object value, boolean sign) {
		VariableMap.SatLiteral lit = map.get(variable, value, sign);
		if (lit == null)
			return null;
		return Lit.valueOf(lit.variable, lit.sign);
	}

    private void encode(Variable<?> variable, Bind bind) {
        Domain<?> domain = bind.get(variable);
        List<?> list = domain.asList();
        int size = domain.size();
        if (size == 2) {
        	addClause(new VecLit(
                lit(variable, list.get(0), false),
                lit(variable, list.get(0), true)));
        } else if (size > 2) {
        	VecLit vec = new VecLit();
        	for (Object v : domain)
        		vec.push(lit(variable, v, false));
            addClause(vec);
            for (int i = 0; i < size; ++i)
            	for (int j = i + 1; j < size; ++j)
            		addClause(new VecLit(
                        lit(variable, list.get(i), true),
                        lit(variable, list.get(j), true)));
        }
    }
    
    private void encode(Constraint c, Bind bind) {
    	c.encode(bind, false, (indices, values) -> {
            VecLit vec = new VecLit();
            for (int i = 0, size = values.size(); i < size; ++i){
                Lit lit = lit(c.variables.get(i), values.get(i), true);
                if (lit != null) vec.push(lit);
            }
    		addClause(vec);
    	});
    }

	
	void result(Result result, VecLit deny) {
		for (VariableMap.VarValue v : map.singleValues)
			result.put(v.variable, v.value);
		for (int i = 0, size = solver.model.size(); i < size; ++i) {
            VariableMap.VarValue2 v = map.multiValues.get(i);
            switch (solver.model.get(i)) {
                case TRUE:
                	result.put(v.variable, v.value);
                	deny.push(Lit.valueOf(i, true));
                	break;
                case FALSE:
                	if (v.falseValue != null) {
                		result.put(v.variable, v.falseValue);
                		deny.push(Lit.valueOf(i, false));
                	}
                	break;
                case UNDEF:
                	logger.info("MinisatSolver: UNKNOWN result var=" + v + " logical=" + i);
                	break;
            }
        }
	}

    public void solve(Problem problem) {
        long start = System.currentTimeMillis();
        Bind bind = problem.bind();
        if (bind == null) return;
//        logger.info("MinisatSolver: **** reduced problem ****");
//        for (Variable<?> v : problem.variables)
//            logger.info("MinisatSolver: " + v + " : " + bind.get(v));
        this.map = new VariableMap(problem, bind);
        this.solver = new jp.saka1029.minisatj.core.Solver();
        jp.saka1029.minisatj.core.Log.logger.setLevel(Level.SEVERE);
        for (Variable<?> v : problem.variables)
            encode(v, bind);
        for (Constraint c : problem.constraints)
        	encode(c, bind);
        int count = 0;
        while (true) {
            boolean r = solver.solve();
            if (!r) break;
            ++count;
            Result result = new Result();
            VecLit deny = new VecLit();
            result(result, deny);
            if (!answer.answer(result)) break;
            // add a negative clause of the answer to search next solution.
            if (deny.size() == 0) break;
            solver.addClause(deny);
        }
        logger.info(String.format("MinisatSolver: variables=%d reduced variables=%d logical vars=%d answers=%d elapse=%dms",
            problem.variables.size(), bind.notUniqueVariableSize(),
            map.multiValues.size(), count, System.currentTimeMillis() - start));
    }

    @Override
    public void solve(Problem problem, Answer answer) {
        this.answer = answer;
        solve(problem);
    }

//    public void toDimacs(Problem problem, File file) throws IOException {
//        Bind bind = problem.bind();
//        if (bind == null) return;
//        this.map = new VariableMap(problem, bind);
//        this.solver = new jp.saka1029.minisatj.core.Solver();
////        minisat.core.Log.logger.setLevel(Level.OFF);
//        for (Variable<?> v : problem.variables)
//            encode(v, bind);
//        for (Constraint<?> c : problem.constraints)
//            encode(c, bind);
//        solver.toDimacs(file);
//    }
}
