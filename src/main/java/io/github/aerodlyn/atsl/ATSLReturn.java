package io.github.aerodlyn.atsl;

public class ATSLReturn extends RuntimeException {
	private static final long serialVersionUID = 1L;

    private ATSLValue returnValue;

    public ATSLReturn(ATSLValue value) {
        returnValue = value;
    }

    public final ATSLValue getReturnValue() { return returnValue; }
}
