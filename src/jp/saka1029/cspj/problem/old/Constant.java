package jp.saka1029.cspj.problem.old;

public class Constant<T> extends Expression<T> {

    Constant(Problem problem, T value) {
        super(problem, Domain.of(value));
    }
    
    @Override public String toString() { return domain.first().toString(); }

//    @Override public boolean isStable(Bind bind) { return true; }
}
