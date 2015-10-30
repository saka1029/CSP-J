package jp.saka1029.cspj.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import jp.saka1029.cspj.geometry.Matrix;
import jp.saka1029.cspj.problem.Domain;
import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

import org.jfree.graphics2d.svg.SVGGraphics2D;

public class TileColorMatch extends SolverMain {

	static final Logger logger = Logger.getLogger(TileColorMatch.class.getName());
	
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
        Tile base = null;

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
            if (base != null && t.base != null)
                return base == t.base;
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

        static List<Tile> allTiles() {
            List<Tile> tiles = new ArrayList<>();
            for (Color c1 : Color.values())
                for (Color c2 : Color.values())
                    for (Color c3 : Color.values())
                        for (Color c4 : Color.values())
                            tiles.add(new Tile(c1, c2, c3, c4));
            List<Tile> bases = new ArrayList<>();
            for (Tile e : tiles) {
                boolean found = false;
                for (Tile b : bases)
                    if (e.same(b)) {
                        e.base = b;
                        found = true;
                        break;
                    }
                if (!found)
                    bases.add(e.base = e);
            }
            return tiles;
        }

        @Override
        public String toString() {
            return String.format("Tile(%s %s %s %s)", up, right, down, left);
        }
    }
    
    Matrix<Variable<Tile>> variables = new Matrix<>(6, 4);

    static String name(int x, int y) { return String.format("%d@%d", x, y); }

    @Override
    public void define() throws IOException {
        List<Tile> tiles = Tile.allTiles();
//        Domain<Tile> first = Domain.of(tiles.get(0));
        Domain<Tile> dom = Domain.of(tiles);
        int width = variables.width;
        int height = variables.height;
        for (int w = 0; w < width; ++w)
            for (int h = 0; h < height; ++h)
                variables.set(w, h, problem.variable(name(w, h), dom));
//                	w == 0 && h == 0 ? first : dom));
        constraint(mapPair("notSame", (a, b) -> !a.same(b), variables.asList()));
        for (int w = 0; w < width; ++w)
            for (int h = 0; h < height; ++h) {
//                if (w + 1 < width)
//                    problem.constraint(a -> ((Tile)a[0]).right((Tile)a[1]), "right",
//                        variables.get(w, h), variables.get(w + 1, h));
//                if (h + 1 < height)
//                    problem.constraint(a -> ((Tile)a[0]).down((Tile)a[1]), "down",
//                        variables.get(w, h), variables.get(w, h + 1));
            	// 長方形の上下および左右の辺は連結しているものとする。（円環面上に配置する）
                constraint("right", (a, b) -> a.right(b),
                    variables.get(w, h), variables.get((w + 1) % width, h));
                constraint("down", (a, b) -> a.down(b),
                    variables.get(w, h), variables.get(w, (h + 1) % height));
            }
        // 左上すみのタイルを決め打ちする。
        Tile first = tiles.get(0);
        constraint("topLeft", a -> a.equals(first), variables.get(0, 0));
    }

    @Override
    public boolean answer(int n, Result result) throws IOException {
        logger.info(result.toString());
        int width = variables.width;
        int height = variables.height;
        int size = 80;
        int margin = 4;
        SVGGraphics2D g = new SVGGraphics2D(width * size + margin * 2, height * size + margin * 2);
        for (int w = 0; w < width; ++w)
            for (int h = 0; h < height; ++h)
                result.get(variables.get(w, h)).draw(g, w * size + margin, h * size + margin, size);
        String s = g.getSVGDocument();
        File file = new File(String.format("result/TileColorMatch/%s-%04d.svg", solver.getClass().getSimpleName(), n));
        try (Writer w = new OutputStreamWriter(new FileOutputStream(file), "utf-8")) {
            w.write(s);
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        new TileColorMatch().parse(args).solve();
    }
}
