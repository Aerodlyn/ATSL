package io.github.aerodlyn.atsl;

public class ATSLBool extends ATSLValue {
    public ATSLBool(boolean value) {
        this.value = value;
        type = TYPE.BOOLEAN;
    }

    public ATSLBool(String value) { this(Boolean.parseBoolean(value)); }
    
    @Override
    public ATSLValue add(ATSLValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ATSLValue divide(ATSLValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ATSLValue multiply(ATSLValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ATSLValue subtract(ATSLValue value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean getValue() {
        return (Boolean) value;
    }
}
