package jp.saka1029.cspj.problem;

import java.util.List;

public interface DerivationFunction3<X, T, A, B, C> extends DerivationFunction<T, X> {
	
	public T apply(A a, B b, C c);

	@SuppressWarnings("unchecked")
	public default T apply(List<X> a) {
		return apply((A)a.get(0), (B)a.get(1), (C)a.get(2));
	}
	
}
