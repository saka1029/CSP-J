package jp.saka1029.cspj.problem;

public abstract class Expression<T> {

    public final Problem problem;
    
    public final Domain<T> domain;

    Expression(Problem problem, Domain<T> domain) {
        if (domain == null) throw new IllegalArgumentException("domain is null");
        if (domain.size() == 0) throw new IllegalArgumentException("domain is empty");
        this.problem = problem;
        this.domain = domain;
    }
    
    /**
     * Bind内の自分自身のドメインを返します。
     * このクラスでは自分自身が定義された時点の初期ドメインを返します。
     */
    public Domain<T> domain(Bind bind) { return domain; }
//    public abstract boolean isStable(Bind bind);
}
