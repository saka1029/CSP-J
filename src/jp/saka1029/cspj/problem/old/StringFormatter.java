package jp.saka1029.cspj.problem.old;

public class StringFormatter {
    
    private final StringBuilder sb = new StringBuilder();
    
    public StringFormatter append(String format, Object... args) {
        sb.append(String.format(format, args));
        return this;
    }
    
    public void clear() {
        sb.setLength(0);
    }

    @Override
    public String toString() {
        return sb.toString();
    }

}
