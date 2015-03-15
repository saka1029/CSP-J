package jp.saka1029.cspj.problem;

import java.util.List;

@FunctionalInterface
public interface ConstraintPredicate2<A> extends ConstraintPredicate {

	public boolean testA(List<A> a);
	
	@SuppressWarnings("unchecked")
	default public boolean test(List<?> a) {
		return testA((List<A>)a);
	}

}
