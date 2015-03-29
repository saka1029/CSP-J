package jp.saka1029.cspj.solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.saka1029.cspj.problem.Problem;

public abstract class SolverMain {

	static final Logger logger = Logger.getLogger(SolverMain.class.getName());

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

	
	final List<Option> options = new ArrayList<>();
	
	protected void addOption(String name, boolean argument, boolean required, Consumer<String> process) {
		options.add(new Option(name, argument, required, process));
	}

	public SolverMain() {
		addOption("-s", true, true, a -> solver(a));
	}

    protected Solver solver;
    public SolverMain solver(Solver solver) { this.solver = solver; return this; }
    public SolverMain solver(String solver) {
    	try {
            Class.forName(solver).newInstance();
    	} catch (Exception e) {
    		throw new RuntimeException("cannot create solver: " + solver);
    	}
    	return this;
    }

    public Problem problem = new Problem();
    
    public abstract void define() throws IOException;
    public abstract boolean answer(int n, Result result) throws IOException;
    
    public int solve() throws IOException {
    	logger.info("SolverMain: start solve() solver=" + solver.getClass().getSimpleName());
        define();
        int count[] = {0};
        solver.solve(problem, new Answer() {
            @Override
            public boolean answer(Result result) {
                ++count[0];
                try {
                return SolverMain.this.answer(count[0], result);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "exception thrown", e);
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
