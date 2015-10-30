package jp.saka1029.cspj.geometry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import jp.saka1029.cspj.problem.StringFormatter;

public class Printer {

    private List<Box> lines = new ArrayList<>();
    private Map<Point, String> strings = new TreeMap<>();

    public void draw(Point p, Object s) {
        strings.put(p, String.valueOf(s));
    }

    public void draw(int x, int y, Object s) {
        draw(new Point(x, y), String.valueOf(s));
    }
    
    public void draw(Point origin, Point size) {
        draw(new Box(origin, size));
    }
    
    public void draw(int x, int y, int width, int height) {
        draw(new Box(x, y, width, height));
    }
    
    public void draw(Box box) {
        lines.add(box);
    }

    static int width(char c) {
        return c <= 0xff ? 1 : 2;
    }

    static int width(String s) {
        int r = 0;
        for (int i = 0, w = s.length(); i < w; ++i)
            r += s.charAt(i) <= 0xff ? 1 : 2;
        return r;
    }

    static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; ++i)
            sb.append(s);
        return sb.toString();
    }

    static class Cell {
        boolean topLeft, top, left;
        String s = "";
        void string(String s) { this.s = s == null ? "" : s; }
        void horizontal() { topLeft = top = true; }
        void vertical() { topLeft = left = true; }
        String first(int width) {
            return (topLeft ? "+" : " ") + (top ? repeat("-", width) : repeat(" ", width));
        }
        String second(int width) {
            int w = width - width(s) + s.length();
            return (left ? "|" : " ") + String.format("%" + w + "s", s);
        }
    }

    Cell[][] cells(Box bound, int cellWidth) {
        Point topLeft = bound.topLeft;
        Point size = bound.size;
        Cell[][] cells = new Cell[size.y + 1][size.x + 1];
        for (int y = 0; y < size.y + 1; ++y)
            for (int x = 0; x < size.x + 1; ++x)
                cells[y][x] = new Cell();
        for (Entry<Point, String> e : strings.entrySet()) {
            Point k = e.getKey().minus(topLeft);
            cells[k.y][k.x].string(e.getValue());
        }
        for (Box e : lines) {
            Point k = e.topLeft.minus(topLeft);
            Point s = e.size;
            for (int x = k.x, wx = k.x + s.x; x < wx; ++x) {
                cells[k.y][x].horizontal();
                cells[k.y + s.y][x].horizontal();
            }
            for (int y = k.y, wy = k.y + s.y; y < wy; ++y) {
                cells[y][k.x].vertical();
                cells[y][k.x + s.x].vertical();
            }
            cells[k.y + s.y][k.x + s.x].topLeft = true;
        }
        return cells;
    }

    static final String NL = String.format("%n");
    
    static <T extends Comparable<T>> T max(T a, T b) {
        return a == null ? b : b == null ? a : a.compareTo(b) > 0 ? a : b;
    }

    static <T extends Comparable<T>> T min(T a, T b) {
        return a == null ? b : b == null ? a : a.compareTo(b) < 0 ? a : b;
    }

    void boundCellWidth(Box[] bound, int[] cellWidth) {
        if (strings.size() <= 0 && lines.size() <= 0)
            throw new IllegalStateException("nothing to draw");
        cellWidth[0] = 1;
        Point topLeft = null;
        Point bottomRight = null;
        for (Entry<Point, String> e : strings.entrySet()) {
            Point k = e.getKey();
            topLeft = min(topLeft, k);
            bottomRight = max(bottomRight, k);
            cellWidth[0] = Math.max(cellWidth[0], width(e.getValue()));
        }
        for (Box e : lines) {
            Point k = e.topLeft;
            topLeft = min(topLeft, k);
            bottomRight = max(bottomRight, k);
            bottomRight = max(bottomRight, k.plus(e.size));
        }
        Point size = bottomRight.minus(topLeft);
        bound[0] = new Box(topLeft, size);
    }
    
    String toString(Box bound, int cellWidth, Cell[][] cells) {
        Point topLeft = bound.topLeft;
        Point size = bound.size;
        StringFormatter sb = new StringFormatter();
        if (topLeft.x != 0 || topLeft.y != 0)
            sb.append("origin=%s ", topLeft);
        sb.append(String.format("size=%s%n", size));
        for (int y = 0, sy = cells.length; y < sy; ++y) {
            int mx = cells[y].length;
            for (int x = 0; x < mx; ++x)
                sb.append(cells[y][x].first(cellWidth));
            sb.append("%n");
            for (int x = 0; x < mx; ++x)
                sb.append(cells[y][x].second(cellWidth));
            sb.append("%n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        if (strings.size() <= 0 && lines.size() <= 0)
            throw new IllegalStateException("nothing to draw");
        Box[] bound = new Box[1];
        int[] cellWidth = new int[1];
        boundCellWidth(bound, cellWidth);
        Cell[][] cells = cells(bound[0], cellWidth[0]);
        return toString(bound[0], cellWidth[0], cells);
    }
    
    public static final float SVG_SCALE = 40;
    public static final float SVG_FONT_SIZE = 18;
    public static final float SVG_TEXT_OFFSET = (SVG_SCALE + SVG_FONT_SIZE) / 2F;
    
    public static final float SVG_OFFSET = 4;
    private float scale(int x) { return x * SVG_SCALE + SVG_OFFSET; }

    String escape(String s) {
    	return s.replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
    		.replaceAll(">", "&gt;")
    		.replaceAll("\"", "&quot;");
    }

    String toSVG(Box bound, int cellWidth, Cell[][] cells) {
        Point size =  bound.size;
        StringFormatter sf = new StringFormatter();
        sf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>%n");
        sf.append("<svg xmlns=\"http://www.w3.org/2000/svg\"%n")
            .append(" xmlns:xlink=\"http://www.w3.org/1999/xlink\"%n")
            .append(" viewbox=\"0 0 %1$s %2$s\" width=\"%1$s\" height=\"%2$s\" >%n",
                scale(size.x) + SVG_OFFSET, scale(size.y) + SVG_OFFSET);
        for (int y = 0, sy = cells.length; y < sy; ++y)
            for (int x = 0, sx = cells[y].length; x < sx; ++x) {
                Cell cell = cells[y][x];
                if (x + 1 < sx) {
                    String opt = cell.top ? "stroke-width=\"2\" stroke=\"black\"" : "stroke-dasharray=\"2\" stroke=\"gray\"";
                    sf.append("<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" style=\"stroke-linecap:square\" %s />%n",
                        scale(x), scale(y), scale(x + 1), scale(y), opt);
                }
                if (y + 1 < sy) {
                    String opt = cell.left ? "stroke-width=\"2\" stroke=\"black\"" : "stroke-dasharray=\"2\" stroke=\"gray\"";
                    sf.append("<line x1=\"%s\" y1=\"%s\" x2=\"%s\" y2=\"%s\" style=\"stroke-linecap:square\" %s />%n",
                        scale(x), scale(y), scale(x), scale(y + 1), opt);
                }
                if (!cell.s.equals(""))
                    sf.append("<text x=\"%s\" y=\"%s\" font-size=\"%s\" dominant-baseline=\"middle\" text-anchor=\"middle\">%s</text>%n",
                        scale(x) + SVG_SCALE / 2, scale(y) + SVG_SCALE / 2, SVG_FONT_SIZE, escape(cell.s));
            }
        sf.append("</svg>");
        return sf.toString();
    }

    public String toSVG() {
        Box[] bound = new Box[1];
        int[] cellWidth = new int[1];
        boundCellWidth(bound, cellWidth);
        Cell[][] cells = cells(bound[0], cellWidth[0]);
        return toSVG(bound[0], cellWidth[0], cells);
    }
    
    public void writeSVG(File out) throws IOException {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(out))) {
            w.write(toSVG());
        }
    }

}
