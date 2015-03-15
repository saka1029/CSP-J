package jp.saka1029.cspj.problem;

public abstract class ProblemElement {
	
	public final int no;
	public final String name;
	public final Problem problem;
	
	ProblemElement(Problem problem, int no, String name) {
		this.problem = problem;
		this.no = no;
		this.name = name;
	}

}
