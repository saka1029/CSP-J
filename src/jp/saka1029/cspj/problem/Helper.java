package jp.saka1029.cspj.problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Helper {

	private Helper() {}
	
	public static <T> Variable<Boolean> eq(Variable<T> x, Variable<T> y) {
		return x.problem.variable(null, "==", (a, b) -> a.equals(b), x, y);
	}

	public static <T> Variable<Boolean> eq(Variable<T> x, T y) {
		return eq(x, x.problem.constant(y));
	}
	
	public static <T> Constraint ceq(Variable<T> x, Variable<T> y) {
		return x.problem.constraint("==", (a, b) -> a.equals(b), x, y);
	}

	public static <T> Constraint ceq(Variable<T> x, T y) {
		return ceq(x, x.problem.constant(y));
	}

	public static <T> Variable<Boolean> ne(Variable<T> x, Variable<T> y) {
		return x.problem.variable(null, "!=", (a, b) -> !a.equals(b), x, y);
	}
	
	public static <T> Variable<Boolean> ne(Variable<T> x, T y) {
		return ne(x, x.problem.constant(y));
	}
	
	public static <T> Constraint cne(Variable<T> x, Variable<T> y) {
		return x.problem.constraint("!=", (a, b) -> !a.equals(b), x, y);
	}
	
	public static <T> Constraint cne(Variable<T> x, T y) {
		return cne(x, x.problem.constant(y));
	}
	
	public static <T extends Comparable<T>> Variable<Boolean> lt(Variable<T> x, Variable<T> y) {
		return x.problem.variable(null, "<", (a, b) -> a.compareTo(b) < 0, x, y);
	}

	public static <T extends Comparable<T>> Variable<Boolean> lt(Variable<T> x, T y) {
		return lt(x, x.problem.constant(y));
	}
	
	public static <T extends Comparable<T>> Constraint clt(Variable<T> x, Variable<T> y) {
		return x.problem.constraint("<", (a, b) -> a.compareTo(b) < 0, x, y);
	}

	public static <T extends Comparable<T>> Constraint clt(Variable<T> x, T y) {
		return clt(x, x.problem.constant(y));
	}
	
	public static <T extends Comparable<T>> Variable<Boolean> le(Variable<T> x, Variable<T> y) {
		return x.problem.variable(null, "<=", (a, b) -> a.compareTo(b) <= 0, x, y);
	}

	public static <T extends Comparable<T>> Variable<Boolean> le(Variable<T> x, T y) {
		return le(x, x.problem.constant(y));
	}
	
	public static <T extends Comparable<T>> Constraint cle(Variable<T> x, Variable<T> y) {
		return x.problem.constraint("<=", (a, b) -> a.compareTo(b) <= 0, x, y);
	}

	public static <T extends Comparable<T>> Constraint cle(Variable<T> x, T y) {
		return cle(x, x.problem.constant(y));
	}
	
	public static <T extends Comparable<T>> Variable<Boolean> gt(Variable<T> x, Variable<T> y) {
		return x.problem.variable(null, ">", (a, b) -> a.compareTo(b) > 0, x, y);
	}

	public static <T extends Comparable<T>> Variable<Boolean> gt(Variable<T> x, T y) {
		return gt(x, x.problem.constant(y));
	}
	
	public static <T extends Comparable<T>> Constraint cgt(Variable<T> x, Variable<T> y) {
		return x.problem.constraint(">", (a, b) -> a.compareTo(b) > 0, x, y);
	}

	public static <T extends Comparable<T>> Constraint cgt(Variable<T> x, T y) {
		return cgt(x, x.problem.constant(y));
	}
	
	public static <T extends Comparable<T>> Variable<Boolean> ge(Variable<T> x, Variable<T> y) {
		return x.problem.variable(null, ">=", (a, b) -> a.compareTo(b) >= 0, x, y);
	}

	public static <T extends Comparable<T>> Variable<Boolean> ge(Variable<T> x, T y) {
		return ge(x, x.problem.constant(y));
	}
	
	public static <T extends Comparable<T>> Constraint cge(Variable<T> x, Variable<T> y) {
		return x.problem.constraint(">=", (a, b) -> a.compareTo(b) >= 0, x, y);
	}

	public static <T extends Comparable<T>> Constraint cge(Variable<T> x, T y) {
		return cge(x, x.problem.constant(y));
	}
	
	public static Variable<Integer> plus(Collection<Variable<? extends Integer>> x) {
		return x.iterator().next().problem.variable(null, "+", a -> {
			int sum = 0;
			for (int e : a)
				sum += e;
			return sum;
		}, x);
	}
	
	@SafeVarargs
	public static Variable<Integer> plus(Variable<Integer>... x) {
		return plus(Arrays.asList(x));
	}

	public static Variable<Integer> plus(Variable<Integer> x, int y) {
		return plus(x, x.problem.constant(y));
	}
	
	public static Variable<Integer> minus(Collection<Variable<? extends Integer>> x) {
		return x.iterator().next().problem.variable(null, "-", a -> {
            int size = a.size();
            switch (size) {
                case 0: return 0;
                case 1: return -a.get(0);
                default:
                    int r = a.get(0);
                    for (int i = 1; i < size; ++i)
                        r -= a.get(i);
                    return r;
            }
		}, x);
	}
	
	@SafeVarargs
	public static Variable<Integer> minus(Variable<Integer>... x) {
		return minus(Arrays.asList(x));
	}

	public static Variable<Integer> minus(Variable<Integer> x, int y) {
		return minus(x, x.problem.constant(y));
	}

	public static Variable<Integer> mult(Collection<Variable<? extends Integer>> x) {
		return x.iterator().next().problem.variable(null, "*", a -> {
			int product = 1;
			for (int e : a)
				product *= e;
			return product;
		}, x);
	}
	
	@SafeVarargs
	public static Variable<Integer> mult(Variable<Integer>... x) {
		return mult(Arrays.asList(x));
	}

	public static Variable<Integer> mult(Variable<Integer> x, int y) {
		return mult(x, x.problem.constant(y));
	}
	
	public static Variable<Integer> abs(Variable<Integer> x) {
		return x.problem.variable(null, "abs", a -> Math.abs(a), x);
	}

	public static Variable<Integer> div(Variable<Integer> x, Variable<Integer> y) {
		return x.problem.variable(null, "/", (a, b) -> a / b, x, y);
	}

	public static Variable<Integer> div(Variable<Integer> x, int y) {
		return div(x, x.problem.constant(y));
	}

	public static Variable<Integer> mod(Variable<Integer> x, Variable<Integer> y) {
		return x.problem.variable(null, "%", (a, b) -> a % b, x, y);
	}

	public static Variable<Integer> mod(Variable<Integer> x, int y) {
		return mod(x, x.problem.constant(y));
	}

	public static Variable<Boolean> and(Collection<Variable<? extends Boolean>> x) {
		return x.iterator().next().problem.variable(null, "&&", a -> {
			for (boolean e : a)
				if (!e)
					return false;
			return true;
		}, x);
	}
	
	public static Variable<Boolean> not(Variable<Boolean> x) {
		return x.problem.variable(null, "!", a -> !a, x);
	}
	
	public static Constraint cnot(Variable<Boolean> x) {
		return x.problem.constraint("!", a -> !a, x);
	}

	@SafeVarargs
	public static Variable<Boolean> and(Variable<Boolean>... x) {
		return and(Arrays.asList(x));
	}

	public static Constraint cand(Collection<Variable<? extends Boolean>> x) {
		return x.iterator().next().problem.constraint("&&", a -> {
			for (boolean e : a)
				if (!e)
					return false;
			return true;
		}, x);
	}
	
	@SafeVarargs
	public static Constraint cand(Variable<Boolean>... x) {
		return cand(Arrays.asList(x));
	}

	public static Variable<Boolean> or(Collection<Variable<? extends Boolean>> x) {
		return x.iterator().next().problem.variable(null, "||", a -> {
			for (boolean e : a)
				if (e)
					return true;
			return false;
		}, x);
	}
	
	@SafeVarargs
	public static Variable<Boolean> or(Variable<Boolean>... x) {
		return or(Arrays.asList(x));
	}

	public static Constraint cor(Collection<Variable<? extends Boolean>> x) {
		return x.iterator().next().problem.constraint("||", a -> {
			for (boolean e : a)
				if (e)
					return true;
			return false;
		}, x);
	}
	
	@SafeVarargs
	public static Constraint cor(Variable<Boolean>... x) {
		return cor(Arrays.asList(x));
	}
	
	public static Constraint constraint(Variable<Boolean> x) {
		return x.problem.constraint("isTrue", a -> a, x);
	}

	public static <A> void forAllPairs(String name, ConstraintPredicate2<A, A, A> predicate, Collection<Variable<? extends A>> variables) {
		variables.iterator().next().problem.forAllPairs(name, predicate, variables);
	}

	@SafeVarargs
	public static <A> void forAllPairs(String name, ConstraintPredicate2<A, A, A> predicate, Variable<A>... variables) {
		forAllPairs(name, predicate, Arrays.asList(variables));
	}

	public static <A> void forAllNeighbors(String name, ConstraintPredicate2<A, A, A> predicate, Collection<Variable<A>> variables) {
		variables.iterator().next().problem.forAllNeighbors(name, predicate, variables);
	}

	@SafeVarargs
	public static <A> void forAllNeighbors(String name, ConstraintPredicate2<A, A, A> predicate, Variable<A>... variables) {
		forAllNeighbors(name, predicate, Arrays.asList(variables));
	}

	public static <A> void allDifferent(Collection<Variable<? extends A>> variables) {
		variables.iterator().next().problem.allDifferent(variables);
	}

	@SafeVarargs
	public static <A> void allDifferent(Variable<A>... variables) {
		allDifferent(Arrays.asList(variables));
	}
   
	public static <X, Y> List<Y> map(Function<X, Y> f, Collection<X> args) {
    	List<Y> r = new ArrayList<>();
    	for (X a : args)
    		r.add(f.apply(a));
    	return r;
    }

    @SafeVarargs
	public static <X, Y> List<Y> map(Function<X, Y> f, X... args) {
    	return map(f, Arrays.asList(args));
    }
}
