package jp.saka1029.cspj.problem.old.singletype;

public class Constant<T> extends Expression<T> {

	public Constant(T value) {
		super(Domain.of(value));
	}

	@Override
	public String toString() {
		return domain.first().toString();
	}
}
