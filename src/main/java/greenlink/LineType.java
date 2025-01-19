package greenlink;

import greenlink.types.AbstractValue;
import greenlink.types.FloatValue;
import greenlink.types.IntegerValue;
import greenlink.types.StringValue;

import java.util.Arrays;
import java.util.List;

public enum LineType {

    INT(new IntegerValue(), "integers.txt", "-?\\d+"),
    FLOAT(new FloatValue(), "floats.txt","-?\\d*\\.\\d+", "(?i)-?\\d+(\\.\\d+)?e-?\\d+"),
    STRING(new StringValue(), "strings.txt", "");

    private final String[] regexps;
    private final AbstractValue abstractValue;
    private String fileName;

    LineType(AbstractValue abstractValue, String fileName, String ... regexps) {
        this.regexps = regexps;
        this.abstractValue = abstractValue;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public Object parse(String line) {
        return abstractValue.parseValue(line);
    }

    public static LineType parseLineValue(String line) {
        List<LineType> list = Arrays.stream(LineType.values())
                .filter(lineType ->
                        Arrays.stream(lineType.regexps)
                                .anyMatch(line::matches))
                .toList();
        if (list.isEmpty()) return STRING;
        if (list.size() > 1) {
            System.err.println("??? Более одного типа для значения: " + line);
            Main.errors++;
        }
        return list.get(0);
    }
}
