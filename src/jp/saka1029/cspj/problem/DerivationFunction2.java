package jp.saka1029.cspj.problem;

import java.util.List;

public interface DerivationFunction2<T, A> extends DerivationFunction<T> {
	
	public T applyA(List<A> a);
	
	@SuppressWarnings("unchecked")
	public default T apply(List<?> a) {
		return applyA((List<A>)a);
	}

}
