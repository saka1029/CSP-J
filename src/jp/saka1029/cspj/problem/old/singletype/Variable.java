package jp.saka1029.cspj.problem.old.singletype;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class Variable<T> extends Expression<T> {

	static final Logger logger = Logger.getLogger(Variable.class.getName());
	
	public final int no;
	public final String name;
	final Set<Constraint<T>> constraints = new HashSet<>();
	public Set<Constraint<T>> constraints() { return Collections.unmodifiableSet(constraints); }
	
	Variable(int no, String name, Domain<T> domain) {
		super(domain);
		this.no = no;
		this.name = name;
	}
	
	enum PutResult { Fail, Put, NotPut }
	
	PutResult put(Domain<T> domain, Bind<T> bind) {
		if (domain.size() <= 0) {
            logger.finest("fail: " + this + " = []");
			return PutResult.Fail;
		}
		Domain<T> org = bind.get(this);
		if (org != null && org.size() <= domain.size())
			return PutResult.NotPut;
		logger.finest("put: " + this + " = " + domain);
		bind.put(this, domain);
		return PutResult.Put;
	}

	boolean test(Bind<T> bind) {
		for (Constraint<T> e : constraints)
			if (!e.test(bind))
				return false;
		return true;
	}

	public boolean bind(Domain<T> domain, Bind<T> bind) {
		PutResult r = put(domain, bind);
		switch (r) {
		case Fail: return false;
		case NotPut: return true;
		case Put: return test(bind);
		default: throw new RuntimeException("unknown PutResult value :" + r);
		}
	}

	@Override
	public String toString() {
		return name;
	}

}
