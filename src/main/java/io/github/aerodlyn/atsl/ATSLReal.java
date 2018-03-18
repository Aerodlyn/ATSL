package io.github.aerodlyn.atsl;

public class ATSLReal extends ATSLNumber {
    public ATSLReal(float value) {
        super(value);
        type = TYPE.REAL;
    }

    public ATSLReal(String value) { this(Float.parseFloat(value)); }

    @Override
    public Float getValue() {
        return (Float) value;
    }
}
