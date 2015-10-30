package jp.saka1029.cspj.sequce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Sequence<E> {

    final Iterator<E> iterator;
    
    Sequence(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    public static <E> Sequence<E> of(Iterator<E> iterator) {
        return new Sequence<>(iterator);
    }
    
    public static <E> Sequence<E> of(Iterable<E> iterable) {
        return of(iterable.iterator());
    }
    
    @SafeVarargs
    public static <E> Sequence<E> of(E... elements) {
        return of(Arrays.asList(elements));
    }
    
    public static abstract class LookAheadIterator<E> implements Iterator<E> {

        private boolean initialized = false;
        private boolean hasMoreElements;
        protected E value = null;
        
        protected abstract boolean hasMoreElements();

        private void initialize() {
            hasMoreElements = hasMoreElements();
            initialized = true;
        }

        @Override
        public boolean hasNext() {
            if (!initialized)
                initialize();
            return hasMoreElements;
        }

        @Override
        public E next() {
            if (!initialized)
                initialize();
            if (!hasMoreElements)
                throw new NoSuchElementException();
            E r = value;
            hasMoreElements = hasMoreElements();
            return r;
        }
        
    }

    static class ConcatIterator<E> extends LookAheadIterator<E> {

        int index = 0;
        final Iterator<E>[] iterators;

        @SafeVarargs
        public ConcatIterator(Iterator<E>... iterators) {
            this.iterators = iterators;
        }

        public boolean hasMoreElements() {
            for (int size = iterators.length; index < size; ++index) {
                Iterator<E> iterator = iterators[index];
                if (iterator.hasNext()) {
                    value = iterator.next();
                    return true;
                }
            }
            return false;
        }

    }

    @SafeVarargs
    public static <E> Sequence<E> concat(Sequence<E>... sequences) {
        int length = sequences.length;
        @SuppressWarnings("unchecked")
        Iterator<E>[] iterators = (Iterator<E>[])new Iterator<?>[length];
        for (int i = 0; i < length; ++i)
            iterators[i] = sequences[i].iterator;
        return of(new ConcatIterator<>(iterators));
    }

    public List<E> toList() {
        List<E> r = new ArrayList<>();
        while (iterator.hasNext())
            r.add(iterator.next());
        return r;
    }

    public Sequence<E> filter(Predicate<? super E> predicate) {
        return new Sequence<>( new LookAheadIterator<E>() {
                
            @Override
            public boolean hasMoreElements() {
                while (iterator.hasNext())
                    if (predicate.test(value = iterator.next()))
                        return true;
                return false;
            }

        });
    }
    
    public <R> Sequence<R> map(Function<? super E, ? extends R> mapper) {
        return new Sequence<>(new LookAheadIterator<R>() {

            @Override
            public boolean hasMoreElements() {
                if (!iterator.hasNext())
                    return false;
                value = mapper.apply(iterator.next());
                return true;
            }

        });
    }
    
    public <R> Sequence<R> flatMap(Function<? super E, ? extends Sequence<R>> mapper) {
        return new Sequence<>(new LookAheadIterator<R>() {

            Iterator<R> mapped;
            
            @Override
            public boolean hasMoreElements() {
                while (true) {
                    if (mapped == null || !mapped.hasNext())
                        if (iterator.hasNext())
                            mapped = mapper.apply(iterator.next()).iterator;
                        else
                            return false;
                    if (mapped.hasNext()) {
                        value = mapped.next();
                        return true;
                    }
                }
            }
            
        });
    }

    public long count() {
        long count = 0;
        while (iterator.hasNext()) {
            ++count;
            iterator.next();
        }
        return count;
    }
    
    public boolean anyMatch(Predicate<? super E> predicate) {
        while (iterator.hasNext())
            if (predicate.test(iterator.next()))
                return true;
        return false;
    }
    
    public boolean allMatch(Predicate<? super E> predicate) {
        while (iterator.hasNext())
            if (!predicate.test(iterator.next()))
                return false;
        return true;
    }
    
    public Sequence<E> peek(Consumer<E> consumer) {
        return new Sequence<>(new LookAheadIterator<E>() {

            @Override
            public boolean hasMoreElements() {
                if (!iterator.hasNext())
                    return false;
                consumer.accept(value = iterator.next());
                return true;
            }

        });
    }

    public Optional<E> first() {
        if (iterator.hasNext())
            return Optional.of(iterator.next());
        return Optional.empty();
    }
    
    public Optional<E> max(Comparator<? super E> comparator) {
        boolean empty = true;
        E max = null;
        while (iterator.hasNext()) {
            E value = iterator.next();
            max = empty ? value : comparator.compare(value, max)  > 0 ? value : max;
            empty = false;
        }
        return empty ? Optional.empty() : Optional.of(max);
    }
    
    public Optional<E> min(Comparator<? super E> comparator) {
        boolean empty = true;
        E min = null;
        while (iterator.hasNext()) {
            E value = iterator.next();
            min = empty ? value : comparator.compare(value, min)  < 0 ? value : min;
            empty = false;
        }
        return empty ? Optional.empty() : Optional.of(min);
    }
    
    public Optional<E> reduce(BinaryOperator<E> accumulator) {
        boolean empty = true;
        E reduced = null;
        while (iterator.hasNext()) {
            E value = iterator.next();
            reduced = empty ? value : accumulator.apply(reduced, value);
            empty = false;
        }
        return empty ? Optional.empty() : Optional.of(reduced);
    }
    
    public E reduce(E identity, BinaryOperator<E> accumulator) {
        E reduced = identity;
        while (iterator.hasNext()) {
            E value = iterator.next();
            reduced = accumulator.apply(reduced, value);
        }
        return reduced;
    }
    
    public void difference(BiConsumer<E, E> difference) {
        boolean empty = true;
        E prev = null;
        while (iterator.hasNext()) {
            E current = iterator.next();
            if (!empty)
                difference.accept(prev, current);
            prev = current;
            empty = false;
        }
    }

    public <R> Sequence<R> difference(BiFunction<E, E, R> difference) {
        return new Sequence<>(new LookAheadIterator<R>() {
            
            boolean empty = true;
            E prev = null;

            @Override
            protected boolean hasMoreElements() {
                while (iterator.hasNext()) {
                    E current = iterator.next();
                    if (empty) {
                        prev = current;
                        empty = false;
                    } else {
                        value = difference.apply(prev, current);
                        prev = current;
                        return true;
                    }
                }
                return false;
            }

        });
    }
    
//    public Sequence<E> sorted() {
//        List<E> list = toList();
//        Collections.sort(list);
//        return of(list);
//    }
    
    public Sequence<E> sorted(Comparator<E> comparator) {
        List<E> list = toList();
        list.sort(comparator);
        return of(list);
    }
    
    public void allPairs(BiConsumer<E, E> pair) {
        List<E> list = toList();
        int size = list.size();
        for (int i = 0; i < size; ++i)
            for (int j = i + 1; j < size; ++j)
                pair.accept(list.get(i), list.get(j));
    }
    
    public <R> Sequence<R> allPairs(BiFunction<E, E, R> pair) {
        List<E> list = toList();
        int size = list.size();
        List<R> result = new ArrayList<>();
        for (int i = 0; i < size; ++i)
            for (int j = i + 1; j < size; ++j)
                result.add(pair.apply(list.get(i), list.get(j)));
        return of(result);
    }
}
