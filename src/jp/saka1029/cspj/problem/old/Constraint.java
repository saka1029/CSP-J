package jp.saka1029.cspj.problem.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Constraint<T> extends Variable<T> {

    public final ConstraintFunction<T> function;
    public final String functionName;
    public final List<Expression<?>> arguments;
    
    private static class DomainBuilder<T> {
    	final int size;
    	final ConstraintFunction<T> function;
    	final List<Domain<?>> argsDomain;
    	final Object[] args;
    	final Domain.Builder<T> builder;
    	
    	void build(int index) {
            if (index >= size) {
                T t = function.eval(args);
                if (t != null)
                    builder.add(t);
            } else {
                for (Object d : argsDomain.get(index)) {
                    args[index] = d;
                    build(index + 1);
                }
            }
    	}
    	
    	DomainBuilder(ConstraintFunction<T> function, Collection<? extends Expression<?>> args) {
    		this.size = args.size();
    		this.function = function;
    		this.argsDomain = new ArrayList<>();
    		this.args = new Object[size];
    		for (Expression<?> e : args)
    			argsDomain.add(e.domain);
    		this.builder = new Domain.Builder<>();
    	}

    	Domain<T> domain() {
    		build(0);
    		return builder.build();
    	}
    }

//    private static <T> void domain(ConstraintFunction<T> func, List<Domain<?>> ds, Domain.Builder<T> b, Object[] args, int index) {
//        int size = ds.size();
//        if (index >= size) {
//            T t = func.eval(args);
//            if (t != null)
//                b.add(t);
//        } else {
//            for (Object d : ds.get(index)) {
//                args[index] = d;
//                domain(func, ds, b, args, index + 1);
//            }
//        }
//    }
//    
//    private static <T> Domain<T> domain(ConstraintFunction<T> function, Collection<? extends Expression<?>> args) {
//        int size = args.size();
//        List<Domain<?>> ds = new ArrayList<>(size);
//        for (Expression<?> a : args)
//            ds.add(a.domain);
//        Domain.Builder<T> b = new Domain.Builder<>();
//        Object[] as = new Object[size];
//        domain(function, ds, b, as,  0);
//        return b.build();
//    }

    Constraint(Problem problem, ConstraintFunction<T> function, String functionName, Collection<? extends Expression<?>> args) {
        super(problem, new DomainBuilder<>(function, args).domain(), null);
//        super(problem, domain(function, args), null);
        int size = args.size();
        List<Expression<?>> list = new ArrayList<>(size);
        boolean notUniq = domain.size() > 1;
        for (Expression<?> a : args) {
            list.add(a);
            if (a instanceof Variable) {
                Variable<?> v = (Variable<?>)a;
                v.refered = true;
                if (notUniq)
                    v.addDependent(this);
            }
        }
        this.function = function;
        this.functionName = functionName;
        this.arguments = Collections.unmodifiableList(list);
    }
    
    Constraint(Problem problem, ConstraintFunction<T> function, String functionName, Expression<?>[] args) {
        this(problem, function, functionName, Arrays.asList(args));
    }
    
    private class EncodeDriver {
    	final int size;
    	final Domain<?> domain;
    	final ConstraintEncoder encoder;
    	final Expression<?>[] args;
    	final Domain<?>[] argsDomain;
    	final Object[] argsValue;
    	final int[] argsIndex;
    	
    	void build(int i) {
    		if (i >= size) {
    			Object value = function.eval(argsValue);
    			int index = domain.indexOf(value);
    			if (value != null && !domain.contains(value))
    				value = null;
                encoder.encode(index, value, args, argsIndex, argsValue);
    		} else {
    			int k = 0;
                for (Object e : argsDomain[i]) {
                    argsValue[i] = e;
                    argsIndex[i] = k++;
                    build(i + 1);
                }
    		}
    	}
    	
    	EncodeDriver(Domain<?> domain, Bind bind, ConstraintEncoder encoder) {
    		this.size = arguments.size();
    		this.domain = domain;
    		this.encoder = encoder;
    		this.args = new Expression<?>[size];
    		this.argsDomain = new Domain[size];
    		for (int i = 0; i < size; ++i) {
    			args[i] = arguments.get(i);
    			argsDomain[i] = arguments.get(i).domain(bind);
    		}
    		argsValue = new Object[size];
    		argsIndex = new int[size];
    		build(0);
    	}
    }

    public void encode(Domain<?> domain, Bind bind, ConstraintEncoder encoder) {
    	new EncodeDriver(domain, bind, encoder);
    }
    
    public List<Variable<?>> variableArguments() {
    	List<Variable<?>> r = new ArrayList<>();
    	for (Expression<?> e : arguments)
    		if (e instanceof Variable<?>)
    			r.add((Variable<?>)e);
    	return r;
    }

    private class VariableEncoder implements ConstraintEncoder {

    	final ConstraintEncoder org;
    	final int size;
    	final int[] map;
    	final Expression<?>[] variableArgs;
    	final int[] variableArgsIndex;
    	final Object[] variableArgsValue;

    	VariableEncoder(ConstraintEncoder org) {
    		this.org = org;
    		size = arguments.size();
            map = new int[size];
            int j = 0;
            for (int i = 0; i < size; ++i)
            	map[i] = arguments.get(i) instanceof Variable<?> ? j++ : -1;
            variableArgs = new Expression<?>[j];
            for (int i = 0; i < size; ++i) {
            	int k = map[i];
            	if (k != -1)
            		variableArgs[k] = arguments.get(i);
            }
            variableArgsIndex = new int[j];
            variableArgsValue = new Object[j];
    	}

		@Override
		public void encode(int index, Object value, Expression<?>[] args, int[] argsIndex, Object[] argsValue) {
			for (int i = 0; i < size; ++i) {
				int j = map[i];
				if (j != -1) {
                    variableArgsIndex[j] = argsIndex[i];
                    variableArgsValue[j] = argsValue[i];
				}
			}
			org.encode(index, value, variableArgs, variableArgsIndex, variableArgsValue);
		}
    	
    }
    
    public void encodeVariable(Domain<?> domain, Bind bind, ConstraintEncoder encoder) {
    	new EncodeDriver(domain, bind, new VariableEncoder(encoder));
    }
    
	@SuppressWarnings("unchecked")
	@Override
    public boolean bind(Domain<T> domain, Bind bind, Set<Variable<?>> que, Variable<?> sender) {
//        Log.info("Constraint.bind: %s = %s", this, domain);
    	int size = arguments.size();
    	final Domain.Builder<T> builder = new Domain.Builder<>();
    	final List<Domain.Builder<Object>> argsBuilder = new ArrayList<>();
    	for (int i = 0; i < size; ++i)
    		argsBuilder.add(new Domain.Builder<Object>());
    	encode(domain, bind, (index, value, args, argsIndex, argsValue) -> {
    		if (value != null) {
    			builder.add((T)value);
    			for (int i = 0; i < size; ++i)
    				argsBuilder.get(i).add(argsValue[i]);
    		}
    	});
        if (!put(builder.build(), bind, que, sender))
            return false;
        for (int i = 0; i < size; ++i) {
            Expression<?> e = arguments.get(i);
            if (e instanceof Variable<?>)
                if (!((Variable<Object>)e).bind(argsBuilder.get(i).build(), bind, que, this))
                    return false;
        }
        return true;
    }

    @Override
    public String toString() {
        char c = functionName.charAt(0);
        if (functionName.contains("%s"))
            return Util.format(functionName, arguments);
        else if  (Character.isAlphabetic(c) || c > 0xff)
            return Util.join(functionName + "(", ", ", ")", arguments);
        else if (arguments.size() == 1)
            return String.format("(%s %s)", functionName, arguments.get(0));
        else
            return Util.join("(", " " + functionName + " ", ")", arguments);
    }
    
}
