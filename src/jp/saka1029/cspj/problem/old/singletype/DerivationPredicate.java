package jp.saka1029.cspj.problem.old.singletype;

import java.util.List;
import java.util.Objects;

public class DerivationPredicate<T> implements ConstraintPredicate<T> {

	final DerivationFunction<T> function;
	
	DerivationPredicate(DerivationFunction<T> function) {
		this.function = function;
	}

	@Override
	public boolean test(List<T> args) {
		T first = args.get(0);
		List<T> rest = args.subList(1, args.size());
		T check = function.apply(rest);
		return check == null ? false : Objects.equals(first, check);
	}

}
