package jp.saka1029.cspj.problem;

import java.util.List;

public interface DerivationFunction<T, X> {
	
	public T apply(List<X> a);
	
}
