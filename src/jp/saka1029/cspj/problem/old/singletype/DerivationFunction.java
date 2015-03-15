package jp.saka1029.cspj.problem.old.singletype;

import java.util.List;

@FunctionalInterface
public interface DerivationFunction<T> {

	T apply(List<T> arguments);

}
