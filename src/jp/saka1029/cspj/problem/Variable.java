package jp.saka1029.cspj.problem;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Variable<T> extends ProblemElement {

	static final Logger logger = Logger.getLogger(Variable.class.getName());
	
	public final Domain<T> domain;
	final Set<Constraint> constraints = new HashSet<>();
	
	Variable(Problem problem, int no, String name, Domain<T> domain) {
		super(problem, no, name);
		this.domain = domain;
	}
	
	void put(Bind bind) {
		bind.put(this, domain);
	}
	
	enum PutResult { Fail, Put, Skip }
	
	PutResult put(Domain<T> domain, Bind bind) {
		if (domain.size() <= 0)
			return PutResult.Fail;
		Domain<T> org = bind.get(this);
		if (org.size() <= domain.size())
			return PutResult.Skip;
		bind.put(this, domain);
		logger.fine("Variable.put: " + this + " <- " + domain);
		return PutResult.Put;
	}
	
	boolean test(Bind bind) {
		for (Constraint e : constraints)
			if (!e.test(bind))
				return false;
		return true;
	}
	
	public boolean bind(Domain<T> domain, Bind bind) {
		PutResult r = put(domain, bind);
		switch (r) {
		case Fail: return false;
		case Skip: return true;
		case Put: return test(bind);
		default: throw new RuntimeException("unknown PutResult value(" + r + ")");
		}
	}
	
	@SuppressWarnings("unchecked")
	boolean rawBind(Domain<?> domain, Bind bind) {
		return bind((Domain<T>)domain, bind);
	}
	
	@Override public String toString() { return name; }

}
