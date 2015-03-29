package jp.saka1029.cspj.problem;

import java.util.List;

public interface DerivationFunction2<X, T, A, B> extends DerivationFunction<T, X> {
	
	public T apply(A a, B b);

	@SuppressWarnings("unchecked")
	public default T apply(List<X> a) {
		return apply((A)a.get(0), (B)a.get(1));
	}
	
}
