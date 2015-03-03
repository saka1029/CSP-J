package jp.saka1029.cspj.geometry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Board {

    public final NavigableMap<Point, Integer> numbers;
    public final Box box;
    
    public Board(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            TreeMap<Point, Integer> numbers = new TreeMap<>();
            int width = -1;
            int height = 0;
            int headerHeight = -1;
            int headerWidth = -1;
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                line = line.trim();
                line = line.replaceFirst("\\s*#.*$", "");
                if (line.equals("")) continue;
                String[] ss = line.split("[ \\t]+");
                if (height == 0 && ss.length == 1) {
                    if (headerHeight == -1) headerHeight = Integer.parseInt(ss[0]);
                    else if (headerWidth == -1) headerWidth = Integer.parseInt(ss[0]);
                    continue;
                }
                int w = ss.length;
                for (int x = 0; x < w; ++x)
                    if (ss[x].matches("\\d+"))
                        numbers.put(new Point(x, height), Integer.parseInt(ss[x]));
                if (width == -1)
                    width = w;
                else if (width != w)
                    throw new IOException(String.format("number of column %d is invalide in row %d", w, height));
                ++height;
            }
            if (headerHeight != -1 && headerHeight != height)
                throw new IOException(String.format("number of rows %d != %d", headerHeight, height));
            if (headerWidth != -1 && headerWidth != width)
                throw new IOException(String.format("number of cols %d != %d", headerWidth, width));
            this.numbers = Collections.unmodifiableNavigableMap(numbers);
            this.box = new Box(Point.ZERO, new Point(width, height));
        } 
    }
    
    @Override
    public String toString() {
        Printer printer = new Printer();
        printer.draw(box.topLeft, box.size);
        for (Entry<Point, Integer> e : numbers.entrySet())
            printer.draw(e.getKey(), e.getValue());
        return printer.toString();
    }

}
