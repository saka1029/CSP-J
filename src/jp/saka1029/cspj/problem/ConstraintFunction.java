package jp.saka1029.cspj.problem;

@FunctionalInterface
public interface ConstraintFunction<T> {

    T eval(Object... args);

//    default String name() {
//        return getClass().getSimpleName();
//    }

}
