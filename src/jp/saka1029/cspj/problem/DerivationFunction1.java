package jp.saka1029.cspj.problem;

import java.util.List;

public interface DerivationFunction1<T, A> extends DerivationFunction<T, A> {
	
	public T apply(A a);

	public default T apply(List<A> a) {
		return apply(a.get(0));
	}
	
}
