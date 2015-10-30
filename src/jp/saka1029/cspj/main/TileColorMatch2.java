package jp.saka1029.cspj.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import jp.saka1029.cspj.geometry.Matrix;
import jp.saka1029.cspj.problem.Domain;
import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

import org.jfree.graphics2d.svg.SVGGraphics2D;

public class TileColorMatch2 extends SolverMain {

	static final Logger logger = Logger.getLogger(TileColorMatch2.class.getName());
	
    enum Color {

        R(java.awt.Color.red),
        G(java.awt.Color.green),
        B(java.awt.Color.blue);

        public final java.awt.Color color;

        Color(java.awt.Color color) {
            this.color = color;
        }
    }
    
    static class Tile {

        final Color up, right, down, left;

        Tile(Color up, Color right, Color down, Color left) {
            this.up = up;
            this.right = right;
            this.down = down;
            this.left = left;
        }

        @Override
        public int hashCode() {
        	return Arrays.hashCode(new Color[] {up, right, down, left});
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Tile))
                return false;
            Tile o = (Tile)obj;
            return up == o.up && right == o.right && down == o.down && left == o.left;
        }
        
        Tile rotate() {
            return new Tile(right, down, left, up);
        }
        
        boolean same(Tile t) {
            if (equals(t)) return true;
            for (int i = 0; i < 3; ++i)
                if (equals(t = t.rotate()))
                    return true;
            return false;
        }
        
        boolean right(Tile t) { return right == t.left; }
        boolean down(Tile t) { return down == t.up; }

//        static final Stroke line = new BasicStroke();
        
        void draw(SVGGraphics2D g, int x, int y, int s) {
            int cx = x + s / 2;
            int cy = y + s / 2;
            g.setPaint(up.color); g.fillPolygon(new int[] {x, x + s, cx}, new int[] {y, y, cy}, 3);
            g.setPaint(right.color); g.fillPolygon(new int[] {x + s, x + s, cx}, new int[] {y, y + s, cy}, 3);
            g.setPaint(down.color); g.fillPolygon(new int[] {x, x + s, cx}, new int[] {y + s, y + s, cy}, 3);
            g.setPaint(left.color); g.fillPolygon(new int[] {x, x, cx}, new int[] {y, y + s, cy}, 3);
//            g.setStroke(line);
            g.setPaint(java.awt.Color.black); g.drawRect(x, y, s, s);
        }

        static Set<Tile> allTiles() {
            Set<Tile> tiles = new HashSet<>();
            for (Color c1 : Color.values())
                for (Color c2 : Color.values())
                    for (Color c3 : Color.values())
                        for (Color c4 : Color.values())
                            tiles.add(new Tile(c1, c2, c3, c4));
            return tiles;
        }
        
        static Map<Tile, Set<Tile>> groupTile(Set<Tile> all) {
        	Map<Tile, Set<Tile>> group = new HashMap<>();
        	for (Tile t : all) {
        		Entry<Tile, Set<Tile>> found = null;
        		for (Entry<Tile, Set<Tile>> e : group.entrySet())
        			if (t.same(e.getKey())) {
        				found = e;
        				break;
        			}
        		Set<Tile> set = null;
        		if (found != null)
        			set = found.getValue();
        		else 
        			group.put(t, set = new HashSet<>());
        		set.add(t);
        	}
        	return group;
        }

        @Override
        public String toString() {
            return String.format("Tile(%s %s %s %s)", up, right, down, left);
        }
    }
    
    Matrix<Variable<Tile>> shapes = new Matrix<>(6, 4);
    Matrix<Variable<Tile>> tiles = new Matrix<>(6, 4);

    static String name(String s, int x, int y) { return String.format("%s%d@%d", s, x, y); }

    @Override
    public void define() throws IOException {
    	Set<Tile> all = Tile.allTiles();
        Map<Tile, Set<Tile>> group = Tile.groupTile(all);
        Domain<Tile> shapesDomain = Domain.of(group.keySet());
        Domain<Tile> tilesDomain = Domain.of(all);
        int width = shapes.width;
        int height = shapes.height;
        for (int w = 0; w < width; ++w)
            for (int h = 0; h < height; ++h) {
            	Variable<Tile> shape = problem.variable(name("s", w, h), shapesDomain);
            	Variable<Tile> tile = problem.variable(name("t", w, h), tilesDomain);
                shapes.set(w, h, shape);
                tiles.set(w, h, tile);
                constraint("contains", (a, b) -> group.get(a).contains(b), shape, tile);
            }
        allDifferent(shapes.asList());
        for (int w = 0; w < width; ++w)
            for (int h = 0; h < height; ++h) {
//                if (w + 1 < width)
//                    problem.constraint(a -> ((Tile)a[0]).right((Tile)a[1]), "right",
//                        variables.get(w, h), variables.get(w + 1, h));
//                if (h + 1 < height)
//                    problem.constraint(a -> ((Tile)a[0]).down((Tile)a[1]), "down",
//                        variables.get(w, h), variables.get(w, h + 1));
                constraint("right", (a, b) -> a.right(b),
                    tiles.get(w, h), tiles.get((w + 1) % width, h));
                constraint("down", (a, b) -> a.down(b),
                    tiles.get(w, h), tiles.get(w, (h + 1) % height));
            }
    }

    @Override
    public boolean answer(int n, Result result) throws IOException {
        logger.info(result.toString());
        int width = tiles.width;
        int height = tiles.height;
        int size = 80;
        int margin = 4;
        SVGGraphics2D g = new SVGGraphics2D(width * size + margin * 2, height * size + margin * 2);
        for (int w = 0; w < width; ++w)
            for (int h = 0; h < height; ++h)
                result.get(tiles.get(w, h)).draw(g, w * size + margin, h * size + margin, size);
        String s = g.getSVGDocument();
        File file = new File(String.format("result/TileColorMatch/%s-%04d.svg", solver.getClass().getSimpleName(), n));
        try (Writer w = new OutputStreamWriter(new FileOutputStream(file), "utf-8")) {
            w.write(s);
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        new TileColorMatch2().parse(args).solve();
    }
}
