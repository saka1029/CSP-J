package jp.saka1029.cspj.problem;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Variable<T> extends ProblemElement implements Comparable<Variable<T>> {

	static final Logger logger = Helper.getLogger(Variable.class.getName());

	public final Domain<T> domain;
	final Set<Constraint> constraints = new HashSet<>();

	Variable(Problem problem, int no, String name, Domain<T> domain) {
		super(problem, no, name);
		if (name == null)
			throw new IllegalArgumentException("name");
		if (domain == null || domain.size() <= 0)
			throw new IllegalArgumentException("domain");
		this.domain = domain;
	}

	void put(Bind bind) {
		bind.put(this, domain);
	}

	boolean bind(Domain<T> domain, Bind bind) {
		if (domain.size() <= 0)
			return false;
		Domain<T> org = bind.get(this);
		if (org.size() <= domain.size())
			return true;
		bind.put(this, domain);
		if (logger.isLoggable(Level.FINEST))
            logger.finest("Variable.bind: " + this + " <- " + domain);
		Map<Variable<?>, Domain<?>> que = new TreeMap<>();
		for (Constraint e : constraints)
//			if (!e.test(bind))
			if (!e.test(this, bind, que))
				return false;
		for (Entry<Variable<?>, Domain<?>> e : que.entrySet())
			if (!e.getKey().rawBind(e.getValue(), bind))
				return false;
		return true;
//		return !que.entrySet().parallelStream()
//			.anyMatch(e -> !e.getKey().rawBind(e.getValue(), bind));
	}

	@SuppressWarnings("unchecked")
	public boolean rawBind(Domain<?> domain, Bind bind) {
		return bind((Domain<T>)domain, bind);
	}

	@Override public String toString() {
		return name; // + (domain.size() == 1 ? "(=" + domain.first() + ")" : "");
	}

	@Override
	public int compareTo(Variable<T> o) {
		return no - o.no;
	}

}
