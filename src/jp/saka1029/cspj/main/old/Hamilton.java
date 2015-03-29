package jp.saka1029.cspj.main.old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.saka1029.cspj.problem.old.ConstraintFunction;
import jp.saka1029.cspj.problem.old.Domain;
import jp.saka1029.cspj.problem.old.Log;
import jp.saka1029.cspj.problem.old.StringFormatter;
import jp.saka1029.cspj.solver.old.Result;
import jp.saka1029.cspj.solver.old.SolverMain;

/**
 * ハミルトン閉路問題
 * 
 * ハミルトン閉路問題とは，グラフのすべての頂点を 1 度だけとおる閉路が存在するか否かをとう問題です。
 * グラフのすべての頂点を 1 度だけとおる閉路をハミルトン閉路といいます。
 * グラフがハミルトン閉路をもつための必要十分条件は，まだ，よくわかっていません。
 * したがって， ハミルトン閉路問題をとく効率のよいアルゴリズムもまだ発見されていません。
 * 図 13-1b に正十二面体を表すグラフに対するハミルトン閉路をしめします。
 * ハミルトン閉路問題の変形として，
 * グラフのすべての頂点を 1 度だけとおる道が存在するか否かをとう問題があります。
 * グラフのすべての頂点を 1 度だけとおる道をハミルトン道といいます。
 *
 */
public class Hamilton extends SolverMain {

    static class Node {

        public final String name;
        public final Set<Node> neighbors = new HashSet<>();
        
        public Node(String name) {
            this.name = name;
        }
        
        public void addNeighbors(Node... nodes) {
            for (Node node : nodes) {
                neighbors.add(node);
                node.neighbors.add(this);
            }
        }
        
        @Override
        public String toString() {
            StringFormatter sf = new StringFormatter();
            sf.append("Node(%s <->", name);
            for (Node n : neighbors)
                sf.append(" %s", n.name);
            sf.append(")");
            return sf.toString();
        }
    }
    
    public List<Node> graph() {
        Node a0 = new Node("a0");
        Node a1 = new Node("a1");
        Node a2 = new Node("a2");
        Node a3 = new Node("a3");
        Node a4 = new Node("a4");
        Node b0 = new Node("b0");
        Node b1 = new Node("b1");
        Node b2 = new Node("b2");
        Node b3 = new Node("b3");
        Node b4 = new Node("b4");
        Node c0 = new Node("c0");
        Node c1 = new Node("c1");
        Node c2 = new Node("c2");
        Node c3 = new Node("c3");
        Node c4 = new Node("c4");
        Node d0 = new Node("d0");
        Node d1 = new Node("d1");
        Node d2 = new Node("d2");
        Node d3 = new Node("d3");
        Node d4 = new Node("d4");
        a0.addNeighbors(a1, a4, b0);
        a1.addNeighbors(a2, b1);
        a2.addNeighbors(a3, b2);
        a3.addNeighbors(a4, b3);
        a4.addNeighbors(b4);
        b0.addNeighbors(c0, c4);
        b1.addNeighbors(c0, c1);
        b2.addNeighbors(c1, c2);
        b3.addNeighbors(c2, c3);
        b4.addNeighbors(c3, c4);
        c0.addNeighbors(d0);
        c1.addNeighbors(d1);
        c2.addNeighbors(d2);
        c3.addNeighbors(d3);
        c4.addNeighbors(d4);
        d0.addNeighbors(d1);
        d1.addNeighbors(d2);
        d2.addNeighbors(d3);
        d3.addNeighbors(d4);
        d4.addNeighbors(d0);
        List<Node> nodes = new ArrayList<>();
        nodes.add(a0); nodes.add(a1); nodes.add(a2); nodes.add(a3); nodes.add(a4);
        nodes.add(b0); nodes.add(b1); nodes.add(b2); nodes.add(b3); nodes.add(b4);
        nodes.add(c0); nodes.add(c1); nodes.add(c2); nodes.add(c3); nodes.add(c4);
        nodes.add(d0); nodes.add(d1); nodes.add(d2); nodes.add(d3); nodes.add(d4);
        for (Node node : nodes) {
            Log.info(node);
            assert node.neighbors.size() == 3;
        }
        return nodes;
    }
    
    static class Path {
        final Node node;
        final Set<Node> neighbors = new HashSet<>();
        Path(Node node, Node in, Node out) {
            this.node = node;
            neighbors.add(in);
            neighbors.add(out);
        }
    }

    List<Node> nodes = graph();

    @Override
    public void define() throws IOException {
        for (Node node : nodes) {
            Domain.Builder<Path> b = new Domain.Builder<>();
            for (Node in : node.neighbors)
                for (Node out : node.neighbors) {
                    if (in.equals(out)) continue;
                    b.add(new Path(node, in, out));
                }
            problem.variable(node.name, b.build());
        }
        ConstraintFunction<Boolean> f =
            a -> {
                Path l = (Path)a[0];
                Path r = (Path)a[1];
                return l.neighbors.contains(r.node) == r.neighbors.contains(l.node);
            };
        for (Node node : nodes)
            for (Node neighbor : node.neighbors)
                problem.constraint(f, "linkOrNot", problem.variable(node.name), problem.variable(neighbor.name));
    }

    void walk(Path from, Result result, List<String> path) {
        path.add(from.node.name);
        for (Node n : from.neighbors)
            if (!path.contains(n.name)) {
                walk((Path)result.get(problem.variable(n.name)), result, path);
                break;
            }
    }

    @Override
    public boolean answer(int n, Result result) throws IOException {
        List<String> path = new ArrayList<>();
        walk((Path)result.get(problem.variable("a0")), result, path);
        Log.info("%s", path);
        assert path.size() == nodes.size();
        return false;
    }

}
