package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Categories.ExcludeCategory;

import jp.saka1029.cspj.geometry.Array;

public class TestArray {

    static <T> T p(T a) {
        System.out.println(a);
        return a;
    }

    @Test
    public void testArrayWithSize() {
        Array<Integer> a = new Array<>(3);
        assertEquals(Array.of(null, null, null), p(a));
    }

    @Test
    public void testOfFunction() {
        Array<Integer> a = Array.of(5, x -> x * 10);
        assertEquals(Array.of(0, 10, 20, 30, 40), p(a));
        assertEquals(Array.of(20, 30), p(a.slice(2, 2)));
    }

    @Test
    public void testSlice() {
        Array<Integer> a = Array.of(0, 1, 2, 3, 4, 5, 6);
        assertEquals(Array.of(0, 1, 2), a.slice(0, 3));
        assertEquals(Array.of(3, 4, 5, 6), a.slice(3, 4));
        assertEquals(Array.of(2, 3), p(a.slice(1, 4).slice(1, 2)));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIndexHigher() {
        Array<Integer> a = Array.of(1, 2, 3);
        assertEquals(3, (int)a.slice(1, 2).get(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIndexLower() {
        Array<Integer> a = Array.of(1, 2, 3);
        assertEquals(1, (int)a.slice(1, 2).get(-1));
    }

}
