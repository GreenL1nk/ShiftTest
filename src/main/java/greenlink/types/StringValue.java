package greenlink.types;

public class StringValue extends AbstractValue {
    @Override
    public String parseValue(String value) {
        return value;
    }
}
