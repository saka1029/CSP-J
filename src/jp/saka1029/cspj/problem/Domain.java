package jp.saka1029.cspj.problem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class Domain<T> implements Iterable<T> {

	private final Set<T> elements = new HashSet<>();
	
	private Domain() {}
	
	public int size() { return elements.size(); }
	public boolean contains(Object e) { return elements.contains(e); }
	public T first() { return elements.iterator().next(); }
	private void add(T e) { elements.add(e); }
	@Override public int hashCode() { return elements.hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Domain<?>))
			return false;
		return ((Domain<?>)obj).elements.equals(elements);
	}
	public Iterable<Domain<T>> singleDomains() {
		return new Iterable<Domain<T>>() {
			@Override
			public Iterator<Domain<T>> iterator() {
				return new Iterator<Domain<T>>() {
                    Iterator<T> iterator = elements.iterator();
					@Override public boolean hasNext() { return iterator.hasNext(); }
					@Override public Domain<T> next() { return Domain.of(iterator.next()); }
				};
			}
		};
	}

    public Stream<T> stream(boolean parallel) {
    	return parallel ? elements.parallelStream() : elements.stream();
    }
    	
    public List<Object> asList() {
    	return new ArrayList<>(elements);
    }

	public static <T> Domain<T> of(Collection<? extends T> elements) {
		Domain<T> r = new Domain<>();
		for (T e : elements)
			r.add(e);
		return r;
	}

	@SafeVarargs
	public static <T> Domain<T> of(T... elements) {
		Domain<T> r = new Domain<>();
		for (T e : elements)
			r.add(e);
		return r;
	}
	
	public static <T> Domain<T> of(int size, Function<Integer, T> initializer) {
		Domain<T> r = new Domain<>();
		for (int i = 0; i < size; ++i)
			r.add(initializer.apply(i));
		return r;
	}
	
	public static Domain<Integer> range(int min, int max) {
		Domain<Integer> r = new Domain<>();
		for (int i= min; i <= max; ++i)
			r.add(i);
		return r;
	}
	
	public static class Builder<T> {
		Domain<T> d = new Domain<>();
		public Domain<T> build() { Domain<T> r = d; d = null; return r; }
		public Builder<T> add(T e) { d.add(e); return this; }
		public int size() { return d.size(); }
		@Override public String toString() { return d.toString(); }
	}
	
	@Override public Iterator<T> iterator() { return elements.iterator(); }
	@Override public String toString() { return elements.toString(); }
}
