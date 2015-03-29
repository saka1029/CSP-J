package jp.saka1029.cspj.problem;

import java.util.List;

@FunctionalInterface
public interface ConstraintPredicate<A> {

	public boolean test(List<A> a);
	
}
