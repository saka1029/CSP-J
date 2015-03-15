package jp.saka1029.cspj.problem.old.singletype;

import java.util.List;

@FunctionalInterface
public interface ConstraintPredicate<T> {

	public boolean test(List<T> args);

}
