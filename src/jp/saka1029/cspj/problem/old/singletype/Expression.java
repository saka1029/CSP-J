package jp.saka1029.cspj.problem.old.singletype;

public abstract class Expression<T> {

	public final Domain<T> domain;
	
	public Expression(Domain<T> domain) {
		if (domain == null) throw new IllegalArgumentException("null domain");
		if (domain.size() <= 0) throw new IllegalArgumentException("empty domain");
		this.domain = domain;
	}
	
}
