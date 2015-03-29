package jp.saka1029.cspj.problem;

import java.util.List;

@FunctionalInterface
public interface ConstraintPredicate1<A> extends ConstraintPredicate<A> {

	public boolean test(A a);
	
	public default boolean test(List<A> a) {
		return test((A)a.get(0));
	}
}
