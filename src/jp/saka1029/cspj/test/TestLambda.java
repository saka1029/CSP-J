package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.Ignore;
import org.junit.Test;

public class TestLambda {

	@Test
	public void test() {
		Stream.of(1, 2, 3, 4, 5, 6, 7, 8)
			.map(x -> x % 2 == 0 ? x : null)
			.filter(x -> x != null)
			.forEach(System.out::println);
	}
	
	static abstract class Tree {
		final String name;
		Tree(String name) {
			this.name = name;
		}
		@Override public String toString() { return String.format("Tree %s", name); }
	}
	
	static class Node extends Tree {
		final List<Tree> children = new ArrayList<>();
		Node(String name) { super(name); }
		Node add(Tree child) { children.add(child); return this; }
		@Override public String toString() { return String.format("Node %s", name); }
	}
	
	static class Leaf extends Tree {
		Leaf(String name) { super(name); }
		@Override public String toString() { return String.format("Leaf %s", name); }
	}
	
	static Function<Tree, Stream<String>> traverse;
	static {
        traverse = x -> x instanceof Node ?
        	Stream.concat(Stream.of(x.name), ((Node)x).children.stream().flatMap(traverse)) :
        	Stream.of(x.name);
	}

	static final Function<Tree, Stream<String>> traverse2 = new Function<TestLambda.Tree, Stream<String>>() {
		@Override
		public Stream<String> apply(Tree t) {
			Stream<String> self = Stream.of(t.name);
			if (t instanceof Node)
				return Stream.concat(self, ((Node)t).children.stream().flatMap(this));
			else
				return self;
		}
	};

	@Test
	public void testTree() {
		Tree root =
            new Node("0")
				.add(new Node("00")
					.add(new Node("000")
						.add(new Leaf("0000"))
						.add(new Leaf("0001"))))
				.add(new Node("01")
					.add(new Leaf("010"))
					.add(new Leaf("011")))
		;
		Stream.of(root)
			.flatMap(traverse2)
			.forEach(System.out::println);
	}
	
	static Function<Integer, Stream<Integer>> f;
	static {
		f = x -> Stream.concat(
            Stream.of(x),
            Stream.of(x + 1)
                .filter(n -> n % x != 0)
                .flatMap(f));
	}

	@Ignore
	@Test
	public void testPrime() {
		Stream.of(2)
			.flatMap(f)
			.forEach(System.out::println);
	}

}
