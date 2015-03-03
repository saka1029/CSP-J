package jp.saka1029.cspj.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import jp.saka1029.cspj.problem.ConstraintFunction;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class Benchmark extends SolverMain {

    static class Pair {
        final int a, b;
        Pair(int a, int b) { this.a = a; this.b = b; }
        @Override public int hashCode() { return a << 16 ^ b; }
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Pair))
                return false;
            Pair o = (Pair)obj;
            return a == o.a && b == o.b;
        }
        @Override
        public String toString() {
        	return String.format("(%s %s)", a, b);
        }
    }

	static class Const implements ConstraintFunction<Boolean>  {
		
		final Set<Pair> pairs = new HashSet<>();
		
		void add(int a, int b) { pairs.add(new Pair(a, b)); }
		
		@Override
		public Boolean eval(Object... args) {
			return !pairs.contains(new Pair((int)args[0], (int)args[1]));
		}
	}

	static String ENCODING = "UTF-8";
	static Pattern DELIMITER = Pattern.compile("[ \t:()]+");
	
	final List<Domain.Builder<Integer>> domains = new ArrayList<>();
	final List<Variable<Integer>> variables = new ArrayList<>();
	final Map<Pair, Const> constraints = new HashMap<>();

	Domain.Builder<Integer> domain(int n) {
		for (int i = domains.size(); i <= n; ++i)
			domains.add(new Domain.Builder<>());
		return domains.get(n);
	}

	void parse(File file) throws FileNotFoundException {
		try (Scanner s = new Scanner(file, ENCODING)) {
            s.useDelimiter(DELIMITER);
            while (s.hasNext()) {
                int v1 = s.nextInt();
                int v2 = s.nextInt();
//                Log.info("line v1=%s v2=%s", v1, v2);
                Domain.Builder<Integer> d1 = domain(v1);
                Domain.Builder<Integer> d2 = domain(v2);
                Pair p = new Pair(v1, v2);
                Const c = constraints.get(p);
                if (c == null)
                    constraints.put(new Pair(v1, v2), c = new Const());
                else
                	Log.info("Benchmark: duplicate %s", p);
                while (!s.hasNext("\r?\n")) {
                    int e1 = s.nextInt();
                    int e2 = s.nextInt();
//                    Log.info("e1=%s e2=%s", e1, e2);
                    d1.add(e1);
                    d2.add(e2);
                    c.add(e1, e2);
                }
                s.next();
            }
		}
	}

	File input;
	public Benchmark input(String a) { input = new File(a); return this; }
	
	public Benchmark() {
		addOption("-i", true, true, a -> input(a));
	}
	
	@Override
	public void define() throws IOException {
		parse(input);
		for (int i = 0, size = domains.size(); i < size; ++i) {
			Variable<Integer> v = problem.variable("v" + i, domains.get(i).build());
			variables.add(v);
		}
		for (Entry<Pair, Const> e : constraints.entrySet()) {
			Pair p = e.getKey();
			Variable<Integer> v1 = variables.get(p.a);
			Variable<Integer> v2 = variables.get(p.b);
			Const c = e.getValue();
			problem.constraint(c, "constraint", v1, v2);
		}
		Log.info("Benchmark.define: varibles=%s constraints=%s", variables.size(), constraints.size());
	}

	@Override
	public boolean answer(int n, Result result) throws IOException {
		for (Variable<Integer> v : variables)
			Log.info("%s = %s", v, result.get(v));
		return false;
	}

	public static void main(String[] args) throws IOException, Exception {
		new Benchmark().parse(args).solve();
	}
}
