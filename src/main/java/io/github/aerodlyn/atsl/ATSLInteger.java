package io.github.aerodlyn.atsl;

public class ATSLInteger extends ATSLNumber {
    public ATSLInteger(int value) {
        super(value);
        type = TYPE.INTEGER;
    }

    public ATSLInteger(String value) { this(Integer.parseInt(value)); }

    @Override
    public Integer getValue() {
        return (Integer) value;
    }
}
