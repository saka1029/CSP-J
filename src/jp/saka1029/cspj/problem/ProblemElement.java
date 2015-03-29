package jp.saka1029.cspj.problem;

public abstract class ProblemElement {
	
	public final int no;
	public final String name;
	public final Problem problem;
	
	ProblemElement(Problem problem, int no, String name) {
		if (problem == null)
			throw new IllegalArgumentException("problem");
		if (no < 0)
			throw new IllegalArgumentException("no");
		if (name == null)
			throw new IllegalArgumentException("name");
		this.problem = problem;
		this.no = no;
		this.name = name;
	}

}
