package io.github.aerodlyn.atsl;

public class ATSLString extends ATSLValue {
    public ATSLString(String value) {
        this.value = value;
        type = TYPE.STRING;
    }

    public ATSLValue add(ATSLValue value) { 
        if (value instanceof ATSLString) {
            String s1 = (String) this.value, s2 = (String) value.value;

            return new ATSLString(s1 + s2);
        }
        
        throw new UnsupportedOperationException();
    }

    public ATSLValue divide(ATSLValue value) { throw new UnsupportedOperationException(); }

    public ATSLValue multiply(ATSLValue value) { throw new UnsupportedOperationException(); }

    public ATSLValue subtract(ATSLValue value) { 
        if (value instanceof ATSLString) {
            String s1 = (String) this.value, s2 = (String) value.value;

            return new ATSLString(s1.replaceAll(s2, ""));
        }

        throw new UnsupportedOperationException();
    }

    public String getValue() { return (String) value; }
}
