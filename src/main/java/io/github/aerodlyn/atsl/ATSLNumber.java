package io.github.aerodlyn.atsl;

public class ATSLNumber extends ATSLValue {
    protected ATSLNumber(Number value) {
        this.value = value;
    }

    @Override
    public final ATSLValue add(ATSLValue value) {
        if (isArithmeticValid(this, value)) {
            Number n1 = (Number) this.value, n2 = (Number) value.value;

            if (this instanceof ATSLInteger && value instanceof ATSLInteger)
                return new ATSLInteger(n1.intValue() + n2.intValue());

            else
                return new ATSLReal(n1.floatValue() + n2.floatValue());
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public final ATSLValue divide(ATSLValue value) {
        if (isArithmeticValid(this, value)) {
            Number n1 = (Number) this.value, n2 = (Number) value.value;

            if (this instanceof ATSLInteger && value instanceof ATSLInteger)
                return new ATSLInteger(n1.intValue() / n2.intValue());

            else
                return new ATSLReal(n1.floatValue() / n2.floatValue());
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public final ATSLValue multiply(ATSLValue value) {
        if (isArithmeticValid(this, value)) {
            Number n1 = (Number) this.value, n2 = (Number) value.value;

            if (this instanceof ATSLInteger && value instanceof ATSLInteger)
                return new ATSLInteger(n1.intValue() * n2.intValue());

            else
                return new ATSLReal(n1.floatValue() * n2.floatValue());
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public final ATSLValue subtract(ATSLValue value) {
        if (isArithmeticValid(this, value)) {
            Number n1 = (Number) this.value, n2 = (Number) value.value;

            if (this instanceof ATSLInteger && value instanceof ATSLInteger)
                return new ATSLInteger(n1.intValue() - n2.intValue());

            else
                return new ATSLReal(n1.floatValue() - n2.floatValue());
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public Number getValue() {
        return (Number) value;
    }
}
