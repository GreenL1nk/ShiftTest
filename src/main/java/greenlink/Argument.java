package greenlink;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum Argument {

    PATH("-o", true),
    PREFIX("-p", true),
    ADD("-a", false),
    SHORT_STAT("-s", false),
    FULL_STAT("-f", false),
    ROUND("-r", true),
    TIME("-t", false),
    ;

    private final String arg;
    private final boolean haveValue;

    Argument(String arg, boolean haveValue) {
        this.arg = arg;
        this.haveValue = haveValue;
    }

    public String getArg() {
        return arg;
    }

    public boolean isHaveValue() {
        return haveValue;
    }

    @Nullable
    public static Argument findArgument(String arg) {
        return Arrays.stream(Argument.values())
                .filter(argument -> argument.getArg().equalsIgnoreCase(arg))
                .findFirst()
                .orElse(null);
    }
}
