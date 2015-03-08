package jp.saka1029.cspj.solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.solver.basic.BasicSolver;
import jp.saka1029.cspj.solver.sat.minisat.MinisatSolver;

public abstract class SolverMain {

	private static class Option {

		final String name;
		final boolean argument;
		final boolean required;
		final Consumer<String> process;

		Option(String name, boolean argument, boolean required, Consumer<String> process) {
			this.name = name;
			this.argument = argument;
			this.required = required;
			this.process = process;
		}
	}

	
    private static final Map<String, Class<? extends Solver>> SOLVERS = new HashMap<>();

    static {
        SOLVERS.put("b", BasicSolver.class);
        SOLVERS.put("m", MinisatSolver.class);
    }

	final List<Option> options = new ArrayList<>();
	
	protected void addOption(String name, boolean argument, boolean required, Consumer<String> process) {
		options.add(new Option(name, argument, required, process));
	}

	public SolverMain() {
		addOption("-s", true, true, a -> solver(a));
		addOption("-d", true, false, a -> solver(a));
	}

    protected Solver solver;
    public SolverMain solver(Solver solver) { this.solver = solver; return this; }
    public SolverMain solver(String solver) {
    	try {
            solver(SOLVERS.get(solver).newInstance());
    	} catch (Exception e) {
    		throw new RuntimeException("cannot create solver: " + solver);
    	}
    	return this;
    }

    public SolverMain debug(EnumSet<Debug> debug) {
    	if (solver == null) throw new RuntimeException("solver not defined");
    	solver.debug(debug);
    	return this;
    }

    public SolverMain debug(String a) {
    	if (solver == null) throw new RuntimeException("solver not defined");
    	List<Debug> list = new ArrayList<>();
    	for (String s : a.split(","))
    		list.add(Enum.valueOf(Debug.class, s));
    	debug(EnumSet.copyOf(list));
    	return this;
    }

    public EnumSet<Debug> debug() {
    	if (solver == null) throw new RuntimeException("solver not defined");
    	return solver.debug();
    }
    
    public Problem problem = new Problem();
    
    protected void write(String format, Object... args) {
        Log.info(format, args);
    }

    public abstract void define() throws IOException;
    public abstract boolean answer(int n, Result result) throws IOException;
    
    public int solve() throws IOException {
    	Log.info("SolverMain: start solve() solver=%s", solver.getClass().getSimpleName());
        define();
        int count[] = {0};
        solver.solve(problem, new Answer() {
            @Override
            public boolean answer(Result result) {
                ++count[0];
                try {
                return SolverMain.this.answer(count[0], result);
                } catch (IOException e) {
                    Log.info(e);
                    return false;
                }
            }
        });
        return count[0];
    }
    
    protected SolverMain parse(String args[]) {
    	Set<Option> used = new HashSet<>();
        for (int i = 0, size = args.length; i < size; ++i) {
            String arg = args[i];
        	for (Option opt : options) {
        		if (arg.equals(opt.name)) {
        			if (opt.argument) {
        				if (i + 1 >= size)
        					throw new IllegalArgumentException("no argument for option " + opt.name);
        				opt.process.accept(args[i + 1]);
        				++i;
        			} else
        				opt.process.accept(null);
        			used.add(opt);
        		}
        	}
        }
        for (Option opt : options)
        	if (opt.required && !used.contains(opt))
        		throw new IllegalArgumentException("option " + opt.name + " missing");
        return this;
    }
}
