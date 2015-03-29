package jp.saka1029.cspj.problem;

import java.util.List;

public interface DerivationFunction<T, A> {
	
	public T apply(List<A> a);
	
}
