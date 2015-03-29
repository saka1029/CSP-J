package jp.saka1029.cspj.problem;

import java.util.List;
import java.util.Objects;

public class DerivationPredicate<T, A> implements ConstraintPredicate<A> {

	public final DerivationFunction<T, A> function;
	
	DerivationPredicate(DerivationFunction<T, A> function) {
		this.function = function;
	}

	@Override
	public boolean test(List<A> a) {
		A first = (A)a.get(0);
		List<A> rest = (List<A>)a.subList(1, a.size());
		T check = function.apply(rest);
		return check == null ? false : Objects.equals(first, check);
	}

}
