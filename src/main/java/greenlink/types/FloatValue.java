package greenlink.types;

import java.math.BigDecimal;

public class FloatValue extends AbstractValue {
    @Override
    public BigDecimal parseValue(String value) {
        return new BigDecimal(value);
    }
}
