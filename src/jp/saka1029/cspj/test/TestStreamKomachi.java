package jp.saka1029.cspj.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

public class TestStreamKomachi {

    /**
     * 投稿
     * http://qiita.com/saka1029/items/10afef21be2b52ef0231
     */
    static <T> List<T> cons(T head, List<T> tail) {
        List<T> list = new ArrayList<>();
        list.add(head);
        list.addAll(tail);
        return list;
    }

    static Stream<List<String>> split(String s) {
        if (s.length() <= 0) return Stream.empty();
        Stream<List<String>> b = Stream.of(Arrays.asList(s), Arrays.asList("-" + s));
        for (int i = 1, size = s.length(); i <= size; ++i) {
            String head = s.substring(0, i);
            b = Stream.concat(b,
                split(s.substring(i))
                    .flatMap(l -> Stream.of(cons(head, l), cons("-" + head, l))));
        }
        return b;
    }

    @Test
    public void q5() {
        split("123456789")
            .filter(l -> l.stream()
                .map(s -> Integer.parseInt(s))
                .reduce(0, (a, b) -> a + b) == 100)
            .forEach(l -> System.out.println(l));
    }

    /**
     * Consバージョン
     */
    static class Cons<T> {
        public final T head;
        public final Cons<T> tail;
        Cons(T head, Cons<T> tail) { this.head = head; this.tail = tail; }
        Cons(T head) { this(head, null); }
        @Override public String toString() { return " " + head + (tail == null ? "" : tail.toString()); }
        Stream<T> stream() {
            Stream.Builder<T> b = Stream.builder();
            for (Cons<T> c = this; c != null; c = c.tail)
                b.add(c.head);
            return b.build();
        }
    }

    static Stream<Cons<String>> splitc(String s) {
        if (s.length() <= 0) return Stream.empty();
        Stream<Cons<String>> b = Stream.of(new Cons<>(s), new Cons<>("-" + s));
        for (int i = 1, size = s.length(); i <= size; ++i) {
            String head = s.substring(0, i);
            b = Stream.concat(b,
                splitc(s.substring(i))
                    .flatMap(l -> Stream.of(new Cons<>(head, l), new Cons<>("-" + head, l))));
        }
        return b;
    }

    @Test
    public void testSplitc() {
        splitc("123456789")
//            .peek(System.out::println)
            .filter(c -> c.stream()
                .map(s -> Integer.parseInt(s))
                .reduce(0, (a, b) -> a + b) == 100)
            .forEach(System.out::println);
    }

    /*
     * リストのリストバージョン
     */
    static List<List<String>> splitList(String s) {
        if (s.length() <= 0) return new ArrayList<>();
        List<List<String>> b = new ArrayList<>();
        b.add(Arrays.asList(s));
        b.add(Arrays.asList("-" + s));
        for (int i = 1, size = s.length(); i <= size; ++i) {
            String head = s.substring(0, i);
            for (List<String> l : splitList(s.substring(i))) {
                b.add(cons(head, l));
                b.add(cons("-" + head, l));
            }
        }
        return b;
    }

    @Test
    public void testSplitList() {
//        for (List<String> l : splitList("123456789"))
//            System.out.println(l);
        splitList("123456789").stream()
            .filter(l -> l.stream()
                .map(s -> Integer.parseInt(s))
                .reduce(0, (a, b) -> a + b) == 100)
            .forEach(l -> System.out.println(l));
    }
}
