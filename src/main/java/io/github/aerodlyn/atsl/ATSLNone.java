package io.github.aerodlyn.atsl;

public class ATSLNone extends ATSLValue {
    public ATSLNone(TYPE type) {
        value = "none";
        this.type = type != null ? type : TYPE.NONE;
    }

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
    public Object getValue() {
        return null;
    }
}
