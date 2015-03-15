package jp.saka1029.cspj.problem.old;

import java.util.Set;
import java.util.TreeSet;

public class Variable<T> extends Expression<T> implements Comparable<Variable<T>> {

    public final int no;
    
    public final String name;
    
    protected boolean refered = false;
    
    private Set<Variable<?>> dependents = new TreeSet<>();
    void addDependent(Variable<?> v) { dependents.add(v); }
    
    Variable(Problem problem, Domain<T> domain, String name) {
        super(problem, domain);
        this.no = problem.size();
        this.name = name != null ? name : "_" + problem.size();
        problem.add(this);
    }

    
    /**
     * Bind内の自分自身のドメインを返します。
     * Bind内に自分自身が定義されていない場合は
     * 変数定義時の初期ドメインを返します。
     * ドメインが論理値でかつ他から参照されていない場合は
     * 要素trueのみからなる論理値ドメインを返します。
     */
    @SuppressWarnings("unchecked")
    @Override
    public Domain<T> domain(Bind bind) {
        Domain<T> d = (Domain<T>)bind.get(this);
        if (d == null) d = domain;
        boolean t = d.contains(Boolean.TRUE);
        boolean f = d.contains(Boolean.FALSE);
        if (!refered && (t || f)) {
            if (!t)
                throw new IllegalArgumentException(
                    "top level domain does not contain true (" + this + ")");
            d = (Domain<T>)Domain.TRUE;
        }
        return d;
    }
    
    protected boolean put(Domain<T> domain, Bind bind, Set<Variable<?>> que, Variable<?> sender) {
//        Log.info("Variable.put: %s = %s que=%s", this, domain, que);
        int size = domain.size();
        if (size <= 0) {
//            Log.info("Variabl.put: fail");
            bind.fail(this);
            return false;
        }
        if (size >= bind.get(this).size()) return true;
        bind.put(this, domain);
        que.addAll(dependents);
        que.remove(sender);
//        Log.info("Variable.put: que=%s", que);
        return true;
    }

    private Set<Variable<?>> newQue() { return new TreeSet<>(); }

//    protected abstract boolean bind(Domain<T> domain, Bind bind, Set<Variable<?>> que, Variable<?> sender);

    protected boolean bind(Domain<T> domain, Bind bind, Set<Variable<?>> que, Variable<?> sender) {
//        Log.info("Var.bind: %s = %s", this, domain);
        return put(domain, bind, que, sender);
    }

    protected boolean bind(Bind bind) { return bind(domain(bind), bind); }
    
    private Variable<?> take(Set<Variable<?>> que) {
        if (que.size() <= 0)
            return null;
        Variable<?> r = que.iterator().next();
        que.remove(r);
        return r;
    }

    @SuppressWarnings("unchecked")
    private boolean bind(Domain<T> domain, Bind bind) {
        Set<Variable<?>> que = newQue();
        if (!bind(domain, bind, que, this))
            return false;
        while (true) {
            Variable<?> v = take(que);
            if (v == null)
                break;
            if (!((Variable<T>)v).bind((Domain<T>)v.domain(bind), bind, que, this))
                return false;
        }
        return true;
    }
    
    @SuppressWarnings("unchecked")
    public boolean bindSingle(Object value, Bind bind) {
        return bind(Domain.of((T)value), bind);
    }


//    @Override public boolean isStable(Bind bind) { return bind.get(this).size() == 1; }

    @Override public int compareTo(Variable<T> o) { return no - o.no; }
//  @Override public boolean equals(Object obj) { return this == obj; }
    @Override public int hashCode() { return no; }

    @Override public String toString() { return name; }
}
