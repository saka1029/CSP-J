package jp.saka1029.cspj.problem.old.singletype;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Domain<T> implements Iterable<T> {

	public static final Domain<Boolean> BOOL = of(false, true);

	private final Set<T> elements = new HashSet<>();

	private Domain() { }
	private void add(T e) { elements.add(e); }

	public T first() { return elements.iterator().next(); }
	public int size() { return elements.size(); }
	public boolean contains(Object e) { return elements.contains(e); }
	public boolean isBool() {
		if (elements.size() <= 0) throw new RuntimeException("empty domain");
		return elements.contains(true) || elements.contains(false);
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

	public static <T> Domain<T> of(Collection<? extends T> args) {
		Domain<T> r = new Domain<>();
		for (T e : args)
			r.add(e);
		return r;
	}

	@SafeVarargs
	public static <T> Domain<T> of(T... args) {
		return of(Arrays.asList(args));
	}
	
	public static Domain<Integer> range(int min, int max) {
		Domain<Integer> r = new Domain<>();
		for (int i = min; i <= max; ++i)
			r.add(i);
		return r;
	}

	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Domain<?>))
			return false;
		return ((Domain<?>)obj).elements.equals(elements);
	}

	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}
	
	@Override
	public String toString() {
		return elements.toString();
	}

	public static class Builder<T> {
		private Domain<T> domain = new Domain<>();
		public Domain<T> build() { Domain<T> r = domain; domain = null; return r; }
		public int size() { return domain.size(); }
		public Builder<T> add(T e) { domain.add(e); return this; }
	}
}
