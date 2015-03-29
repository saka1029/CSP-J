package jp.saka1029.cspj.problem;

import java.util.List;

@FunctionalInterface
public interface ConstraintPredicate2<X, A, B> extends ConstraintPredicate<X> {

	public boolean test(A a, B b);
	
	@SuppressWarnings("unchecked")
	public default boolean test(List<X> a) {
		return test((A)a.get(0), (B)a.get(1));
	}
}
