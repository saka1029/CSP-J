package jp.saka1029.cspj.problem;

import java.util.List;
import java.util.Objects;

public class DerivationPredicate<T> implements ConstraintPredicate {

	final DerivationFunction<T> function;
	
	DerivationPredicate(DerivationFunction<T> function) {
		this.function = function;
	}

	@Override
	public boolean test(List<?> values) {
		Object first = values.get(0);
		List<?> rest = values.subList(1, values.size());
		T check = function.apply(rest);
		return check == null ? false : Objects.equals(first, check);
	}

}
