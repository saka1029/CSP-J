package jp.saka1029.cspj.problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Helper {

	private Helper() {}
	
	public static <T> Variable<Boolean> eq(Variable<T> x, Variable<T> y) {
		return variable(null, "==", (a, b) -> a.equals(b), x, y);
	}

	public static <T> Variable<Boolean> eq(Variable<T> x, T y) {
		return eq(x, x.problem.constant(y));
	}
	
	public static <T> Variable<Boolean> ne(Variable<T> x, Variable<T> y) {
		return variable(null, "!=", (a, b) -> !a.equals(b), x, y);
	}
	
	public static <T> Variable<Boolean> ne(Variable<T> x, T y) {
		return ne(x, x.problem.constant(y));
	}
	
	public static <T extends Comparable<T>> Variable<Boolean> lt(Variable<T> x, Variable<T> y) {
		return variable(null, "<", (a, b) -> a.compareTo(b) < 0, x, y);
	}

	public static <T extends Comparable<T>> Variable<Boolean> lt(Variable<T> x, T y) {
		return lt(x, x.problem.constant(y));
	}
	
	public static <T extends Comparable<T>> Variable<Boolean> le(Variable<T> x, Variable<T> y) {
		return variable(null, "<=", (a, b) -> a.compareTo(b) <= 0, x, y);
	}

	public static <T extends Comparable<T>> Variable<Boolean> le(Variable<T> x, T y) {
		return le(x, x.problem.constant(y));
	}
	
	public static <T extends Comparable<T>> Variable<Boolean> gt(Variable<T> x, Variable<T> y) {
		return variable(null, ">", (a, b) -> a.compareTo(b) > 0, x, y);
	}

	public static <T extends Comparable<T>> Variable<Boolean> gt(Variable<T> x, T y) {
		return gt(x, x.problem.constant(y));
	}
	
	public static <T extends Comparable<T>> Variable<Boolean> ge(Variable<T> x, Variable<T> y) {
		return variable(null, ">=", (a, b) -> a.compareTo(b) >= 0, x, y);
	}

	public static <T extends Comparable<T>> Variable<Boolean> ge(Variable<T> x, T y) {
		return ge(x, x.problem.constant(y));
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
		return plus(list(x));
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
		return minus(list(x));
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
		return mult(list(x));
	}

	public static Variable<Integer> mult(Variable<Integer> x, int y) {
		return mult(x, x.problem.constant(y));
	}
	
	public static Variable<Integer> abs(Variable<Integer> x) {
		return variable(null, "abs", a -> Math.abs(a), x);
	}

	public static Variable<Integer> div(Variable<Integer> x, Variable<Integer> y) {
		return variable(null, "/", (a, b) -> a / b, x, y);
	}

	public static Variable<Integer> div(Variable<Integer> x, int y) {
		return div(x, x.problem.constant(y));
	}

	public static Variable<Integer> mod(Variable<Integer> x, Variable<Integer> y) {
		return variable(null, "%", (a, b) -> a % b, x, y);
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
		return variable(null, "!", a -> !a, x);
	}
	
	@SafeVarargs
	public static Variable<Boolean> and(Variable<Boolean>... x) {
		return and(list(x));
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
		return or(list(x));
	}

	// define derived variable.

	public static <T, A> Variable<T> variable(String name, String constraintName,
	        DerivationFunction<T, A> function, Collection<Variable<? extends A>> variables) {
		return variables.iterator().next().problem.variable(name, constraintName, function, variables);
	}

	public static <T, A> Variable<T> variable(String name, String constraintName,
	        DerivationFunction1<T, A> function, Variable<A> a) {
		return a.problem.variable(name, constraintName, function, list(a));
	}

	public static <X, T, A extends X, B extends X> Variable<T> variable(
            String name, String constraintName, DerivationFunction2<X, T, A, B> function,
            Variable<A> a, Variable<B> b) {
		return a.problem.variable(name, constraintName, function, list(a, b));
	}
	
	public static <X, T, A extends X, B extends X, C extends X> Variable<T> variable(
            String name, String constraintName, DerivationFunction3<X, T, A, B, C> function,
            Variable<A> a, Variable<B> b, Variable<C> c) {
		return a.problem.variable(name, constraintName, function, list(a, b, c));
	}
	
	public static <X, T, A extends X, B extends X, C extends X, D extends X> Variable<T> variable(
            String name, String constraintName, DerivationFunction4<X, T, A, B, C, D> function,
            Variable<A> a, Variable<B> b, Variable<C> c, Variable<D> d) {
		return a.problem.variable(name, constraintName, function, list(a, b, c, d));
	}
	
	public static <X, T, A extends X, B extends X, C extends X, D extends X, E extends X> Variable<T> variable(
            String name, String constraintName, DerivationFunction5<X, T, A, B, C, D, E> function,
            Variable<A> a, Variable<B> b, Variable<C> c, Variable<D> d, Variable<E> e) {
		return a.problem.variable(name, constraintName, function, list(a, b, c, d, e));
	}
	
	public static <X, T, A extends X, B extends X, C extends X, D extends X, E extends X, F extends X> Variable<T> variable(
			String name, String constraintName, DerivationFunction6<X, T, A, B, C, D, E, F> function,
            Variable<A> a, Variable<B> b, Variable<C> c, Variable<D> d, Variable<E> e, Variable<F> f) {
		return a.problem.variable(name, constraintName, function, list(a, b, c, d, e, f));
	}
	
	public static <X, T, A extends X, B extends X, C extends X, D extends X, E extends X, F extends X, G extends X> Variable<T> variable(
			String name, String constraintName, DerivationFunction7<X, T, A, B, C, D, E, F, G> function,
            Variable<A> a, Variable<B> b, Variable<C> c, Variable<D> d, Variable<E> e, Variable<F> f, Variable<G> g) {
		return a.problem.variable(name, constraintName, function, list(a, b, c, d, e, f, g));
	}
	
	public static <X, T, A extends X, B extends X, C extends X, D extends X, E extends X, F extends X, G extends X, H extends X> Variable<T> variable(
			String name, String constraintName, DerivationFunction8<X, T, A, B, C, D, E, F, G, H> function,
            Variable<A> a, Variable<B> b, Variable<C> c, Variable<D> d, Variable<E> e, Variable<F> f, Variable<G> g, Variable<H> h) {
		return a.problem.variable(name, constraintName, function, list(a, b, c, d, e, f, g, h));
	}
	
	// define constraint
	public static void constraint(Collection<Variable<Boolean>> variables) {
	    for (Variable<? extends Boolean> v : variables)
            constraint("isTrue", a -> a, v);
	}

	@SafeVarargs
    public static void constraint(Variable<Boolean>... variables) {
	    constraint(list(variables));
	}

	public static <A> Constraint constraint(String name, ConstraintPredicate1<A> predicate, Variable<? extends A> a) {
		return a.problem.constraint(name, predicate, list(a));
	}
	
	public static <X, A extends X, B extends X>
            Constraint constraint(String name,
            ConstraintPredicate2<X, A, B> predicate,
            Variable<? extends A> a, Variable<? extends B> b) {
		return a.problem.constraint(name, predicate, list(a, b));
	}
	
	public static <X, A extends X, B extends X, C extends X>
            Constraint constraint(String name,
            ConstraintPredicate3<X, A, B, C> predicate,
            Variable<? extends A> a, Variable<? extends B> b, Variable<? extends C> c) {
		return a.problem.constraint(name, predicate, list(a, b, c));
	}
	
	public static <X, A extends X, B extends X, C extends X, D extends X>
            Constraint constraint(String name,
            ConstraintPredicate4<X, A, B, C, D> predicate,
            Variable<? extends A> a, Variable<? extends B> b, Variable<? extends C> c,
            Variable<? extends D> d) {
		return a.problem.constraint(name, predicate, list(a, b, c, d));
	}
	
	public static <X, A extends X, B extends X, C extends X, D extends X, E extends X>
            Constraint constraint(String name,
            ConstraintPredicate5<X, A, B, C, D, E> predicate,
            Variable<? extends A> a, Variable<? extends B> b, Variable<? extends C> c,
            Variable<? extends D> d, Variable<? extends E> e) {
		return a.problem.constraint(name, predicate, list(a, b, c, d, e));
	}
	
	public static <X, A extends X, B extends X, C extends X, D extends X, E extends X, F extends X>
            Constraint constraint(String name,
            ConstraintPredicate6<X, A, B, C, D, E, F> predicate,
            Variable<? extends A> a, Variable<? extends B> b, Variable<? extends C> c,
            Variable<? extends D> d, Variable<? extends E> e, Variable<? extends F> f) {
		return a.problem.constraint(name, predicate, list(a, b, c, d, e, f));
	}
	
	public static <X, A extends X, B extends X, C extends X, D extends X, E extends X, F extends X, G extends X>
            Constraint constraint(String name,
            ConstraintPredicate7<X, A, B, C, D, E, F, G> predicate,
            Variable<? extends A> a, Variable<? extends B> b, Variable<? extends C> c,
            Variable<? extends D> d, Variable<? extends E> e, Variable<? extends F> f,
            Variable<? extends G> g) {
		return a.problem.constraint(name, predicate, list(a, b, c, d, e, f, g));
	}
	
	public static <X, A extends X, B extends X, C extends X, D extends X, E extends X, F extends X, G extends X, H extends X>
            Constraint constraint(String name,
            ConstraintPredicate8<X, A, B, C, D, E, F, G, H> predicate,
            Variable<? extends A> a, Variable<? extends B> b, Variable<? extends C> c,
            Variable<? extends D> d, Variable<? extends E> e, Variable<? extends F> f,
            Variable<? extends G> g, Variable<? extends H> h) {
		return a.problem.constraint(name, predicate, list(a, b, c, d, e, f, g, h));
	}
	
//	public static <A> void allDifferent(Collection<Variable<? extends A>> variables) {
//		variables.iterator().next().problem.allDifferent(variables);
//	}
//
//	@SafeVarargs
//	public static <A> void allDifferent(Variable<A>... variables) {
//		allDifferent(list(variables));
//	}
   
	@SafeVarargs
    public static <T> List<T> list(T... args) {
	    return Arrays.asList(args);
	}

	public static <X, Y> List<Y> map(Function<X, Y> f, Collection<X> args) {
    	List<Y> r = new ArrayList<>();
    	for (X a : args)
    		r.add(f.apply(a));
    	return r;
    }

    @SafeVarargs
	public static <X, Y> List<Y> map(Function<X, Y> f, X... args) {
    	return map(f, list(args));
    }
    
//    public static <T, R> Collection<R> mapPair(BiFunction<T, T, R> mapper, Collection<T> source) {
//        List<R> result = new ArrayList<>();
//        int i = 0;
//        for (T a : source) {
//            int j = 0;
//            for (T b : source) {
//                if (i < j)
//                    result.add(mapper.apply(a, b));
//                ++j;
//            }
//            ++i;
//        }
//        return result;
//    }
//    
//    @SafeVarargs
//    public static <T, R> Collection<R> mapPair(BiFunction<T, T, R> mapper, T... source) {
//        return mapPair(mapper, list(source));
//    }
    
    public static <T, R> Collection<Variable<R>> mapPair(String constraintName,
            DerivationFunction2<T, R, T, T> mapper, Collection<Variable<T>> source) {
        List<Variable<R>> result = new ArrayList<>();
        int i = 0;
        for (Variable<T> a : source) {
            int j = 0;
            for (Variable<T> b : source) {
                if (i < j)
                    result.add(variable(null, constraintName, mapper, a, b));
                ++j;
            }
            ++i;
        }
        return result;
    }

    @SafeVarargs
    public static <T, R> Collection<Variable<R>> mapPair(String constraintName,
            DerivationFunction2<T, R, T, T> mapper, Variable<T>... source) {
        return mapPair(constraintName, mapper, list(source));
    }
    
    public static <T, R> Collection<Variable<R>> mapNeighbor(String constraintName,
            DerivationFunction2<T, R, T, T> mapper, Collection<Variable<T>> source) {
        List<Variable<R>> result = new ArrayList<>();
        boolean first = true;
        Variable<T> prev = null;
        for (Variable<T> e : source) {
            if (first) {
                first = false;
                prev = e;
            } else
                result.add(variable(null, constraintName, mapper, e, prev));
        }
        return result;

    }

    @SafeVarargs
    public static <T, R> Collection<Variable<R>> mapNeighbor(String constraintName,
            DerivationFunction2<T, R, T, T> mapper, Variable<T>... source) {
        return mapNeighbor(constraintName, mapper, list(source));
    }

//    public static <T, R> List<R> mapNeighbor(BiFunction<T, T, R> mapper, Collection<T> source) {
//        List<R> result = new ArrayList<>();
//        boolean first = true;
//        T prev = null;
//        for (T e : source) {
//            if (first) {
//                first = false;
//                prev = e;
//            } else
//                result.add(mapper.apply(prev, e));
//        }
//        return result;
//    }
//    
//    @SafeVarargs
//    public static <T, R> List<R> mapNeighbor(BiFunction<T, T, R> mapper, T... source) {
//        return mapNeighbor(mapper, list(source));
//    }
    
//	public static <X, T, A extends X, B extends X> Variable<T> variable(
//            String name, String constraintName, DerivationFunction2<X, T, A, B> function,
//            Variable<A> a, Variable<B> b) {
//		return a.problem.variable(name, constraintName, function, list(a, b));
//	}

//    public static <T> void forAllPairs(String name, DerivationFunction2<T, Boolean, T, T> mapper, Collection<Variable<? extends T>> variables) {
////        constraint(and(mapPair((x, y) -> variable(null, name, mapper, list(x, y)), variables)));
////        for (Variable<Boolean> v : mapPair((x, y) -> variable(null, name, mapper, list(x, y)), variables))
////            constraint(v);
//        constraint(mapPair((x, y) -> variable(null, name, mapper, list(x, y)), variables));
//    }
//
//    @SafeVarargs
//    public static <T> void forAllPairs(String name, DerivationFunction2<T, Boolean, T, T> mapper, Variable<T>... variables) {
//        forAllPairs(name, mapper, list(variables));
//    }

    public static <T> void allDifferent(Collection<Variable<T>> variables) {
//        constraint(and(mapPair((x, y) -> variable(null, "!=", (a, b) -> !a.equals(b), x, y), variables)));
//        for (Variable<Boolean> v : mapPair((x, y) -> variable(null, "!=", (a, b) -> !a.equals(b), x, y), variables))
//            constraint(v);
//        forAllPairs("!=", (x, y) -> !x.equals(y), variables);
        constraint(mapPair("!=", (x, y) -> !x.equals(y), variables));
    }
    
    @SafeVarargs
    public static <T> void allDifferent(Variable<T>... variables) {
        allDifferent(list(variables));
    }
}
