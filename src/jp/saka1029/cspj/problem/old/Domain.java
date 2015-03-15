package jp.saka1029.cspj.problem.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 
 * @param <T> 不変クラスを指定します。
 *             equals()とhashCode()を適切に実装している必要があります。
 */
public class Domain<T> implements Iterable<T> {

    public static final Domain<Boolean> TRUE = Domain.of(true);
    
    private Set<T> set = new HashSet<>();
    public int size() { return set.size(); }
    public boolean contains(Object a) { return set.contains(a); }
    private void add(T a) { set.add(a); }
    /**
     * 先頭の要素を返します。
     * 要素数が１であることがわかっている場合に使用すべきです。
     * それ以外の場合はどの要素が帰るかわかりません。
     */
    public T first() { return set.iterator().next(); }
    public int indexOf(Object a) {
    	int i = 0;
    	for (T e : set) {
    		if (e.equals(a))
    			return i;
    		++i;
    	}
    	return -1;
    }
    public List<T> asList() {
    	List<T> r = new ArrayList<>();
    	for (T e : set)
            r.add(e);
    	return r;
    }

    public Domain<T> except(Collection<T> ex) {
        Domain<T> r = new Domain<>();
        for (T e : set)
            if (!ex.contains(e))
                r.add(e);
        return r;
    }
    public Domain<T> except(@SuppressWarnings("unchecked") T... ex) {
        return except(Arrays.asList(ex));
    }
    
    private Domain() {}
    
    public static <T> Domain<T> of(Collection<? extends T> ts) {
        Domain<T> r = new Domain<>();
        for (T t : ts)
            r.add(t);
        return r;
    }
    
    /**
     * for Constant constructor
     */
    static <T> Domain<T> of(T t) {
        Domain<T> r = new Domain<>();
        r.add(t);
        return r;
    }

    @SafeVarargs
    public static <T> Domain<T> of(T... ts) {
        return of(Arrays.asList(ts));
    }
    
    public static Domain<Integer> range(int min, int max) {
        Domain.Builder<Integer> b = new Domain.Builder<>();
        for (int i = min; i <= max; ++i)
            b.add(i);
        return b.build();
    }
    
    public static class Builder<T> {
        private Domain<T> x = new Domain<>();
        public Domain<T> build() { Domain<T> r = x; x = null; return r; }
        public Builder<T> add(T a) { x.add(a); return this; }
        public int size() { return x.size(); }
        @Override public String toString() { return x.toString(); }
    }
    
    public Stream<T> stream(boolean parallel) {
    	return parallel ? set.parallelStream() : set.stream();
    }
    
//    public Stream<Domain<?>> stream() {
//    	return set.stream().map(x -> Domain.of(x));
//    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Domain))
            return false;
        @SuppressWarnings("unchecked")
        Domain<T> o = (Domain<T>)obj;
        return set.equals(o.set);
    }
    @Override public int hashCode() { return set.hashCode(); }
    @Override public String toString() { return set.toString(); }
    @Override public Iterator<T> iterator() { return set.iterator(); }
}
