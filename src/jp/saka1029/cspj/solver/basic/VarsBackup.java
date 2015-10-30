package jp.saka1029.cspj.solver.basic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import jp.saka1029.cspj.problem.ConstraintPredicate;
import jp.saka1029.cspj.problem.ConstraintPredicate1;
import jp.saka1029.cspj.problem.ConstraintPredicate2;
import jp.saka1029.cspj.problem.ConstraintPredicate3;
import jp.saka1029.cspj.problem.ConstraintPredicate4;
import jp.saka1029.cspj.problem.ConstraintPredicate5;
import jp.saka1029.cspj.problem.ConstraintPredicate6;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;

public class VarsBackup<T> {

	final Problem problem = new Problem();
	final List<Variable<? extends T>> variables = new ArrayList<>();
	
	public static <T extends Comparable<T>> Comparator<List<T>> Compare() {
		return (a, b) -> {
			int size = a.size();
			for (int i = 0; i < size; ++i) {
				int c = a.get(i).compareTo(b.get(i));
				if (c != 0) return c;
			}
			return 0;
		};
	}

	VarsBackup(int n, Domain<T> domain) {
		for (int i = 0; i < n; ++i)
			variables.add(problem.variable(null, domain));
	}
	
	@SafeVarargs
	VarsBackup(Domain<T>... domains) {
		for (Domain<T> d :domains)
			variables.add(problem.variable(null, d));
	}
	
	public static <T> VarsBackup<T> of(int n, Domain<T> domain) {
		return new VarsBackup<>(n, domain);
	}

	@SafeVarargs
	public static <T> VarsBackup<T> of(Domain<T>... domains) {
		return new VarsBackup<>(domains);
	}

	public VarsBackup<T> constraint(ConstraintPredicate<T> predicate) {
		problem.constraint(null, predicate, variables);
		return this;
	}
	
	/**********************
//	public Vars<T> constraint(ConstraintPredicate1<T> predicate, Variable<? extends T> v) {
//		problem.constraint(null, predicate, v);
//		return this;
//	}

	public Vars<T> constraint(ConstraintPredicate1<T> predicate) {
		problem.constraint(null, predicate, variables.get(0));
		return this;
	}

	public Vars<T> constraint(ConstraintPredicate2<T, T, T> predicate) {
		problem.constraint(null, predicate, variables.get(0), variables.get(1));
		return this;
	}

	public Vars<T> constraint(ConstraintPredicate3<T, T, T, T> predicate) {
		problem.constraint(null, predicate, variables.get(0),
			variables.get(1), variables.get(2));
		return this;
	}

	public Vars<T> constraint(ConstraintPredicate4<T, T, T, T, T> predicate) {
		problem.constraint(null, predicate, variables.get(0),
			variables.get(1), variables.get(2), variables.get(3));
		return this;
	}

	public Vars<T> constraint(ConstraintPredicate5<T, T, T, T, T, T> predicate) {
		problem.constraint(null, predicate, variables.get(0),
			variables.get(1), variables.get(2), variables.get(3), variables.get(4));
		return this;
	}

	public Vars<T> constraint(ConstraintPredicate6<T, T, T, T, T, T, T> predicate) {
		problem.constraint(null, predicate,
			variables.get(0), variables.get(1), variables.get(2), variables.get(3), variables.get(4), variables.get(5));
		return this;
	}

	public Vars<T> forAllPairs(ConstraintPredicate2<T, T, T> predicate) {
		problem.forAllPairs(null, predicate, variables);
		return this;
	}

	public Vars<T> forAllPairs(ConstraintPredicate4<Object, Integer, T, Integer, T> predicate) {
		problem.forAllPairs(null, predicate, variables);
		return this;
	}
	
	public Vars<T> forAllNeighbors(ConstraintPredicate2<T, T, T> predicate) {
		problem.forAllPairs(null, predicate, variables);
		return this;
	}
	
	public Vars<T> allDifferent() {
		problem.allDifferent(variables);
		return this;
	}
	**********************/
	
	public Stream<List<T>> solve() {
		List<List<T>> all = new ArrayList<>();
        Solver solver = new BasicSolver();
        solver.solve(problem, (result) -> {
        	List<T> list = new ArrayList<>();
        	for (Variable<? extends T> v : variables)
        		list.add(result.get(v));
        	all.add(list);
            return true;
        });
		return all.stream();
	}
	
}
