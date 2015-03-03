package jp.saka1029.cspj.problem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Problem {

    private List<Variable<?>> variables = new ArrayList<>();
    public List<Variable<?>> variables() { return Collections.unmodifiableList(variables); }
    public int size() { return variables.size(); }
    
    private Map<String, Variable<?>> map = new HashMap<>();
    public Variable<?> variable(String name) { return map.get(name); }
    public Variable<?> variable(int no) { return variables.get(no); }
    
    void add(Variable<?> f) {
        String name = f.name;
        if (map.get(name) != null)
            throw new IllegalArgumentException("variable name(" + name + ") duplicated");
        variables.add(f);
        map.put(name, f);
    }
    
    
    public void info() {
        for (Variable<?> v : variables)
            Log.info("Problem: %s : %s", v, v.domain);
    }
    
    public Bind bind() {
        Bind bind = new Bind(variables.size(), true);
        for (Variable<?> v : variables)
            bind.put(v, v.domain(bind));
        for (Variable<?> v : variables)
            v.bind(bind);
        return bind;
    }

    public <T> Constant<T> constant(T value) { return new Constant<>(this, value); }

    public <T> Variable<T> variable(String name, Domain<T> domain) { return new Variable<>(this, domain, name); }

    public final <T> Constraint<T> constraint(ConstraintFunction<T> func, String funcName, Expression<?>... args) {
        return new Constraint<>(this, func, funcName, args);
    }

    public final <T> Constraint<T> constraint(ConstraintFunction<T> func, String funcName, Collection<? extends Expression<?>> args) {
        return new Constraint<>(this, func, funcName, args);
    }
 
    public void forEachPairs(ConstraintFunction<Boolean> func, String funcName, Expression<?>... args) {
        for (int i = 0, size = args.length; i < size; ++i)
            for (int j = i + 1; j < size; ++j)
                constraint(func, funcName, args[i], args[j]);
    }
 
    public void forEachPairs(ConstraintFunction<Boolean> func, String funcName, Collection<? extends Expression<?>> args) {
    	int i = 0;
    	for (Expression<?> a : args) {
    		int j = 0;
    		for (Expression<?> b : args) {
    			if (i < j)
    				constraint(func, funcName, a, b);
    			++j;
    		}
    		++i;
    	}
    }
 
    public static ConstraintFunction<Boolean> EQ = x -> x[0].equals(x[1]);
    public static ConstraintFunction<Boolean> NE = x -> !x[0].equals(x[1]);
    
    public void allDifferent(Expression<?>... args) { forEachPairs(NE, "!=", args); }
    public void allDifferent(Collection<? extends Expression<?>> args) { forEachPairs(NE, "!=", args); }

    @Override public String toString() { return variables.toString(); }
}

