package jp.saka1029.cspj.problem.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Helper {

    private Helper() {}
    
    @SafeVarargs
    public static <T> Constraint<T> constraint(ConstraintFunction<T> func, String funcName, Expression<?>... args) {
        return args[0].problem.constraint(func, funcName, args);
    }

    public static <T> Constraint<T> constraint(ConstraintFunction<T> func, String funcName, Collection<? extends Expression<?>> args) {
        return args.iterator().next().problem.constraint(func, funcName, args);
    }

    // equals to
    public static <A> Constraint<Boolean> eq(Expression<A> l, Expression<A> r) {
        return constraint(Problem.EQ, "==", l, r);
    }
    public static <A> Constraint<Boolean> eq(Expression<A> l, A r) {
        return constraint(Problem.EQ, "==", l, l.problem.constant(r));
    }

    // not equals to
    public static <A> Constraint<Boolean> ne(Expression<A> l, Expression<A> r) {
        return constraint(Problem.NE, "!=", l, r);
    }
    public static <A> Constraint<Boolean> ne(Expression<A> l, A r) {
        return constraint(Problem.NE, "!=", l, l.problem.constant(r));
    }

    // less than
    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Constraint<Boolean> lt(Expression<A> l, Expression<A> r) {
        return constraint(a  -> ((A)a[0]).compareTo((A)a[1]) < 0, "<", l, r);
    }
    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Constraint<Boolean> lt(Expression<A> l, A r) {
        return constraint(a -> ((A)a[0]).compareTo((A)a[1]) < 0, "<", l, l.problem.constant(r));
    }

    // less than or equals to
    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Constraint<Boolean> le(Expression<A> l, Expression<A> r) {
        return constraint(a -> ((A)a[0]).compareTo((A)a[1]) <= 0, "<=", l, r);
    }
    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Constraint<Boolean> le(Expression<A> l, A r) {
        return constraint(a -> ((A)a[0]).compareTo((A)a[1]) <= 0, "<=", l, l.problem.constant(r));
    }
    
    // greater than
    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Constraint<Boolean> gt(Expression<A> l, Expression<A> r) {
        return constraint(a -> ((A)a[0]).compareTo((A)a[1]) > 0, ">", l, r);
    }
    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Constraint<Boolean> gt(Expression<A> l, A r) {
        return constraint(a -> ((A)a[0]).compareTo((A)a[1]) > 0, ">", l, l.problem.constant(r));
    }

    // greater than or equals to
    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Constraint<Boolean> ge(Expression<A> l, Expression<A> r) {
        return constraint(a -> ((A)a[0]).compareTo((A)a[1]) >= 0, ">=", l, r);
    }
    @SuppressWarnings("unchecked")
    public static <A extends Comparable<A>> Constraint<Boolean> ge(Expression<A> l, A r) {
        return constraint(a -> ((A)a[0]).compareTo((A)a[1]) >= 0, ">=", l, l.problem.constant(r));
    }
    
    // and
    private static ConstraintFunction<Boolean> AND  =
        a -> {
            for (Object e : a)
                if (!(boolean)e) return false;
            return true;
        };

    @SafeVarargs
    public static Constraint<Boolean> and(Expression<Boolean>... args) {
        return constraint(AND, "&&", args);
    }

    public static Constraint<Boolean> and(Collection<? extends Expression<Boolean>> args) {
        return constraint(AND , "&&", args);
    }

    // or
    private static ConstraintFunction<Boolean> OR  =
        a -> {
            for (Object e : a)
                if ((boolean)e) return true;
            return false;
        };

    @SafeVarargs
    public static Constraint<Boolean> or(Expression<Boolean>... args) {
        return constraint(OR , "||", args);
    }

    public static Constraint<Boolean> or(Collection<? extends Expression<Boolean>> args) {
        return constraint(OR , "||", args);
    }

    public static Constraint<Boolean> not(Expression<Boolean> l) {
        return constraint(a -> !(boolean)a[0], "!", l);
    }
    
    public static Constraint<Integer> integer(Expression<Boolean> b) {
        return constraint(a -> (boolean)a[0] ? 1 : 0, "integer", b);
    }

    private static ConstraintFunction<Integer> PLUS = 
        a -> {
            int sum = 0;
            for (Object e : a)
                sum += (int)e;
            return sum;
        };

    @SafeVarargs
    public static Constraint<Integer> plus(Expression<Integer>... args) {
        return constraint(PLUS, "+", args);
    }

    public static Constraint<Integer> plus(Collection<? extends Expression<Integer>> args) {
        return constraint(PLUS, "+", args);
    }

    public static Constraint<Integer> plus(Expression<Integer> l, int r) {
        return constraint( a -> (int)a[0] + (int)a[1] , "+", l, l.problem.constant(r));
    }
    
    @SafeVarargs
    public static Constraint<Integer> minus(Expression<Integer>... args) {
        return constraint(
            a -> {
                int size = a.length;
                switch (size) {
                    case 0: return 0;
                    case 1: return -(int)a[0];
                    default:
                        int r = (int)a[0];
                        for (int i = 1; i < size; ++i)
                            r -= (int)a[i];
                        return r;
                }
            }, "-", args);
    }

    public static Constraint<Integer> minus(Expression<Integer> l, int r) {
        return constraint( a -> (int)a[0] - (int)a[1] , "-", l, l.problem.constant(r));
    }
    
    public static Constraint<Integer> abs(Expression<Integer> arg) {
        return constraint( a -> Math.abs((int)a[0]), "abs", arg);
    }

    @SafeVarargs
    public static Constraint<Integer> mult(Expression<Integer>... args) {
        return constraint(
            a -> {
                int sum = 1;
                for (Object e : a)
                    sum *= (int)e;
                return sum;
            }, "*", args);
    }
    
    public static Constraint<Integer> mult(Expression<Integer> l, int r) {
        return constraint(a -> (int)a[0] * (int)a[1], "*", l, l.problem.constant(r));
    }
    
    public static void forEachPairs(ConstraintFunction<Boolean> func, String funcName, Collection<? extends Expression<?>> args) {
        args.iterator().next().problem.forEachPairs(func, funcName, args);
    }

    @SafeVarargs
    public static void forEachPairs(ConstraintFunction<Boolean> func, String funcName, Expression<?>... args) {
        args[0].problem.forEachPairs(func, funcName, args);
    }

    public static void allDifferent(Expression<?>... args) { args[0].problem.allDifferent(args); } 
    
    public static void allDifferent(Collection<? extends Expression<?>> args) { args.iterator().next().problem.allDifferent(args); }
   
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

    public static int numberFromDigits(int... digits) {
        int r = 0;
        for (int i : digits)
            r = r * 10 + i;
        return r;
    }
    
}
