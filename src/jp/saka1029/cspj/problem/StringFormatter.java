package jp.saka1029.cspj.problem;

public class StringFormatter {

    private final StringBuilder sb = new StringBuilder();
    
    public StringFormatter append(String format, Object... args) {
        sb.append(String.format(format, args));
        return this;
    }
    
//    public StringFormatter append(String s) {
//        sb.append(s);
//        return this;
//    }
    
    @Override
    public String toString() {
        return sb.toString();
    }
}
