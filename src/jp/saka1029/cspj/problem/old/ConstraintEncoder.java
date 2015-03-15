package jp.saka1029.cspj.problem.old;

@FunctionalInterface
public interface ConstraintEncoder {

	void encode(int index, Object value, Expression<?>[] args, int[] argsIndex, Object[] argsValue);

}
