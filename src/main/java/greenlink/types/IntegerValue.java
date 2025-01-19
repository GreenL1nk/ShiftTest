package greenlink.types;

import java.math.BigInteger;

public class IntegerValue extends AbstractValue {
    @Override
    public BigInteger parseValue(String value) {
        return new BigInteger(value);
    }
}
