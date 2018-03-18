package io.github.aerodlyn.atsl;

public class ATSLException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    private ATSLException() {}

    private ATSLException(String message) { super(message); }

    public static ATSLException variableAlreadyExists(String id) {
        return new ATSLException("ERROR: Variable '" + id + "' has already been declared.");
    }

    public static ATSLException variableDoesNotExist(String id) {
        return new ATSLException("ERROR: Variable '" + id + "' has not been declared.");
    }

    public static ATSLException variableIsNotDynamic(String id) {
        return new ATSLException("ERROR: Variable '" + id + "' is a static variable; cannot reassign type.");
    }
}
