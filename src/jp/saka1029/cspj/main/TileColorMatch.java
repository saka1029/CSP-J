package jp.saka1029.cspj.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import jp.saka1029.cspj.geometry.Matrix;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

import org.jfree.graphics2d.svg.SVGGraphics2D;

public class TileColorMatch extends SolverMain {

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

        final Color top, right, bottom, left;
        Tile base = null;

        Tile(Color top, Color right, Color bottom, Color left) {
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.left = left;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Tile))
                return false;
            Tile o = (Tile)obj;
            return top == o.top && right == o.right && bottom == o.bottom && left == o.left;
        }
        
        Tile rotate() {
            return new Tile(right, bottom, left, top);
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
        boolean bottom(Tile t) { return bottom == t.top; }

//        static final Stroke line = new BasicStroke();
        
        void draw(SVGGraphics2D g, int x, int y, int s) {
            int cx = x + s / 2;
            int cy = y + s / 2;
            g.setPaint(top.color); g.fillPolygon(new int[] {x, x + s, cx}, new int[] {y, y, cy}, 3);
            g.setPaint(right.color); g.fillPolygon(new int[] {x + s, x + s, cx}, new int[] {y, y + s, cy}, 3);
            g.setPaint(bottom.color); g.fillPolygon(new int[] {x, x + s, cx}, new int[] {y + s, y + s, cy}, 3);
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
            return String.format("Tile(%s %s %s %s)", top, right, bottom, left);
        }
    }
    
    Matrix<Variable<Tile>> variables = new Matrix<>(6, 4);

    static String name(int x, int y) { return String.format("%d@%d", x, y); }

    @Override
    public void define() throws IOException {
        List<Tile> tiles = Tile.allTiles();
        Domain<Tile> dom = Domain.of(tiles);
        int width = variables.width;
        int height = variables.height;
        for (int w = 0; w < width; ++w)
            for (int h = 0; h < height; ++h)
                variables.set(w, h, problem.variable(name(w, h), dom));
        problem.forEachPairs(a -> !((Tile)a[0]).same((Tile)a[1]), "notSame", variables.asList());
        for (int w = 0; w < width; ++w)
            for (int h = 0; h < height; ++h) {
//                if (w + 1 < width)
//                    problem.constraint(a -> ((Tile)a[0]).right((Tile)a[1]), "right",
//                        variables.get(w, h), variables.get(w + 1, h));
//                if (h + 1 < height)
//                    problem.constraint(a -> ((Tile)a[0]).bottom((Tile)a[1]), "bottom",
//                        variables.get(w, h), variables.get(w, h + 1));
                problem.constraint(a -> ((Tile)a[0]).right((Tile)a[1]), "right",
                    variables.get(w, h), variables.get((w + 1) % width, h));
                problem.constraint(a -> ((Tile)a[0]).bottom((Tile)a[1]), "bottom",
                    variables.get(w, h), variables.get(w, (h + 1) % height));
            }
    }

    @Override
    public boolean answer(int n, Result result) throws IOException {
        Log.info(result);
        int width = variables.width;
        int height = variables.height;
        int size = 80;
        int margin = 4;
        SVGGraphics2D g = new SVGGraphics2D(width * size + margin * 2, height * size + margin * 2);
        for (int w = 0; w < width; ++w)
            for (int h = 0; h < height; ++h)
                ((Tile)result.get(variables.get(w, h))).draw(g, w * size + margin, h * size + margin, size);
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
