package jp.saka1029.cspj.problem;

import java.util.List;

@FunctionalInterface
public interface ConstraintPredicate5<X, A, B, C, D, E> extends ConstraintPredicate<X> {

	public boolean test(A a, B b, C c, D d, E e);
	
	@SuppressWarnings("unchecked")
	public default boolean test(List<X> a) {
		return test((A)a.get(0), (B)a.get(1), (C)a.get(2), (D)a.get(3), (E)a.get(4));
	}
}
