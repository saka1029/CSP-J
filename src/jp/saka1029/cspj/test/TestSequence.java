package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import jp.saka1029.cspj.sequce.Sequence;

public class TestSequence {

    @Test
    public void testToList() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        assertEquals(list,
            Sequence.of(list)
                .toList());
    }

    @Test
    public void testArray() {
        assertEquals(Arrays.asList(1, 2, 3),
            Sequence.of(1, 2, 3)
                .toList());
    }

    @Test
    public void testFilter() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        assertEquals(Arrays.asList(2, 4),
            Sequence.of(list)
                .filter(i -> i % 2 == 0)
                .toList());
        assertEquals(Arrays.asList(4),
            Sequence.of(list)
                .filter(i -> i % 2 == 0)
                .filter(i -> i > 3)
                .toList());
        assertEquals(Arrays.asList(1, 3, 5),
            Sequence.of(list)
                .filter(i -> i % 2 != 0)
                .toList());
    }

    @Test
    public void testEmpty() {
        List<Integer> list = Arrays.asList();
        assertEquals(Arrays.asList(),
            Sequence.of(list)
                .filter(i -> i % 2 == 0)
                .toList());
    }

    @Test
    public void testMap() {
        assertEquals(Arrays.asList(2, 4, 6),
            Sequence.of(1, 2, 3)
                .map(i -> i * 2)
                .toList());
        List<Integer> list = Arrays.asList();
        assertEquals(Arrays.asList(),
            Sequence.of(list)
                .map(i -> i * 2)
                .toList());
        assertEquals(Arrays.asList("2", "4", "6"),
            Sequence.of(2, 4, 6)
                .map(i -> i + "")
                .toList());
    }

    @Test
    public void testMapFilter() {
        assertEquals(Arrays.asList(4, 8),
            Sequence.of(1, 2, 3, 4, 5)
                .filter(i -> i % 2 == 0)
                .map(i -> i * 2)
                .toList());
    }

    @Test
    public void testCount() {
        assertEquals(5,
            Sequence.of(1, 2, 3, 4, 5)
                .count());
    }
    
    @Test
    public void testMax() {
        assertEquals(5,
            Sequence.of(1, 4, 5, 2, 3)
                .max(Integer::compare).get().intValue());
        Sequence<Integer> s = Sequence.of();
        assertFalse(s.max(Integer::compare).isPresent());
    }
    
    @Test
    public void testMin() {
        assertEquals(1,
            Sequence.of(4, 5, 2, 1, 3)
                .min(Integer::compare).get().intValue());
        Sequence<Integer> s = Sequence.of();
        assertFalse(s.min(Integer::compare).isPresent());
    }

    @Test
    public void testReduce() {
        assertEquals(Integer.valueOf(15),
            Sequence.of(4, 5, 2, 1, 3)
                .reduce((a, b) -> a + b).get());
        assertEquals(Integer.valueOf(15),
            Sequence.of(4, 5, 2, 1, 3)
                .reduce(0, (a, b) -> a + b));
        Sequence<Integer> s = Sequence.of();
        assertFalse(
             s.reduce((a, b) -> a + b).isPresent());
        assertEquals(Integer.valueOf(0),
             s.reduce(0, (a, b) -> a + b));
    }

    @Test
    public void testAnyMatch() {
        assertTrue(Sequence.of(0, 1, 2)
            .anyMatch(i -> i > 1));
        assertFalse(Sequence.of(0, 1, 2)
            .anyMatch(i -> i > 2));
    }

    @Test
    public void testAllMatch() {
        assertTrue(Sequence.of(0, 1, 2)
            .allMatch(i -> i >= 0));
        assertFalse(Sequence.of(0, 1, 2)
            .allMatch(i -> i > 2));
    }

    @Test
    public void testFirst() {
        assertEquals(0,
            Sequence.of(0, 1, 2)
                .first().get().intValue());
        assertFalse(Sequence.of()
            .first().isPresent());
    }

    @Test
    public void testPeek() {
        List<Integer> a = new ArrayList<>();
        assertEquals(Arrays.asList(4, 5, 2, 1, 3),
            Sequence.of(4, 5, 2, 1, 3)
                .peek(i -> a.add(i))
                .toList());
        assertEquals(Arrays.asList(4, 5, 2, 1, 3), a);
    }
    
    @Test
    public void testConcat() {
        assertEquals(Arrays.asList(1, 2, 3, 4, 5),
            Sequence.concat(Sequence.of(1, 2, 3), Sequence.of(4, 5)).toList());
    }
    
    @Test
    public void testFlatMap() {
        assertEquals(Arrays.asList(1, 2, 2, 4, 3, 6, 4, 8),
            Sequence.of(1, 2, 3, 4)
                .flatMap(i -> Sequence.of(i, i * 2))
                .toList());
        assertEquals(Arrays.asList(),
            Sequence.of(1, 2, 3, 4)
                .flatMap(i -> Sequence.of())
                .toList());
    }
    
    @Test
    public void testDifference() {
        assertEquals(Arrays.asList(3, 5, 7, 9),
            Sequence.of(1, 2, 3, 4, 5)
                .difference((a, b) -> a + b)
                .toList());
        assertEquals(Arrays.asList(3),
            Sequence.of(1, 2)
                .difference((a, b) -> a + b)
                .toList());
        assertEquals(Arrays.asList(),
            Sequence.of(1)
                .difference((a, b) -> a + b)
                .toList());
        Sequence<Integer> empty = Sequence.of();
        assertEquals(Arrays.asList(),
            empty
                .difference((a, b) -> a + b)
                .toList());
    }
    
    private void differenceConsumer(List<Integer> expected, List<Integer> values) {
        List<Integer> list = new ArrayList<>();
        Sequence.of(values)
            .difference((a, b) -> { list.add(a + b); });
        assertEquals(expected, list);
    }

    @Test
    public void testDifferenceConsumer() {
        differenceConsumer(Arrays.asList(3, 5, 7, 9), Arrays.asList(1, 2, 3, 4, 5));
        differenceConsumer(Arrays.asList(3), Arrays.asList(1, 2));
        differenceConsumer(Arrays.asList(), Arrays.asList(1));
        differenceConsumer(Arrays.asList(), Arrays.asList());
    }
    
    @Test
    public void testSorted() {
        assertEquals(Arrays.asList(1, 2, 3, 4, 5),
            Sequence.of(5, 4, 1, 2, 3)
                .sorted(Integer::compare)
                .toList());
    }

    @Test
    public void testAllPairs() {
        assertEquals(Arrays.asList(3, 4, 5),
            Sequence.of(1, 2, 3)
                .allPairs((a, b) -> a + b)
                .toList());
    }

    @Test
    public void testAllPairsConsumer() {
        Sequence.of(1, 2, 3)
            .allPairs((a, b) -> { System.out.printf("%s-%s%n", a, b); });
    }
    
    @Test(expected = NoSuchElementException.class)
    public void testLokkAheadIteratorEmpty() {
        Iterator<Integer> iterator = new Sequence.LookAheadIterator<Integer>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }
        };
        assertEquals(1, iterator.next().intValue());
    }
}
