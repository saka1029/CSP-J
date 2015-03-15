package jp.saka1029.cspj.problem.old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Util {
    
    private Util() {}
    
    public static String spaces(int n) {
        return String.format("%" + (n <= 0 ? "" : n) + "s", "");
    }
    
    public static <A> List<A> array(int size) {
        List<A> list = new ArrayList<>(size);
        for (int i= 0; i < size; ++i)
            list.add(null);
        return list;
    }
    
    public static <A> List<List<A>> array(int rows, int cols) {
        List<List<A>> r = array(rows);
        for (int i = 0; i < rows; ++i) {
            List<A> a = array(cols);
            r.set(i, a);
        }
        return r;
    }
    
    public static Object[] asArray(Collection<?> list) {
        int size = list.size();
        Object[] r = new Object[size];
        int i = 0;
        for (Object e : list)
            r[i++] = e;
        return r;
    }

    public static String format(String format, Collection<?> arguments) {
        return String.format(format, asArray(arguments));
    }

    public static String join(String prefix, String separator, String suffix, Object... list) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        String sep = "";
        for (Object e : list) {
            sb.append(sep).append(e);
            sep = separator;
        }
        sb.append(suffix);
        return sb.toString();
    }


    public static String join(String prefix, String separator, String suffix, Collection<?> list) {
        return join(prefix, separator, suffix, asArray(list));
    }
}
