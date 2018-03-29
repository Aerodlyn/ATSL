package io.github.aerodlyn.atsl;

import java.util.Arrays;

public abstract class ATSLValue {
    public enum COMPARE { 
        LTH(), LEQ(), GTH(), GEQ(), EQT(), NEQ();

        public static COMPARE mapOperator(String op) {
            switch (op) {
                case "<":
                    return LTH;

                case "<=":
                    return LEQ;

                case ">":
                    return GTH;

                case ">=":
                    return GEQ;

                case "==":
                    return EQT;

                case "!=":
                    return NEQ;

                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    public enum TYPE { 
        ARRAY(), BOOLEAN(), INTEGER(), NONE(), OBJECT(), REAL(), STRING();

        public boolean isTypeCompatibleWith(TYPE type) { 
            switch (this) {
                case INTEGER:
                    return type == INTEGER || type == REAL;

                case NONE:
                    return type == NONE;

                case REAL:
                    return type == INTEGER || type == REAL;

                default:
                    return false;
            }
        }

        public static TYPE convertStringToType(String value) {
            switch (value) {
                case "array":
                    return ARRAY;

                case "bool":
                    return BOOLEAN;

                case "int":
                    return INTEGER;

                case "none":
                    return NONE;

                case "object":
                    return OBJECT;

                case "real":
                    return REAL;

                case "string":
                    return STRING;

                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    protected boolean dynamic = true;

    protected Object value;
    protected TYPE type;

    public static final boolean isArithmeticValid(ATSLValue v1, ATSLValue v2) {
        return v1 instanceof ATSLNumber && v2 instanceof ATSLNumber;
    }

    public final boolean isDynamic() { return dynamic; }    

    public final static ATSLBool compare(ATSLValue v1, ATSLValue v2, COMPARE op) {
        if (isArithmeticValid(v1, v2)) {
            float n1 = ((Number) v1.value).floatValue(), n2 = ((Number) v2.value).floatValue();

            switch (op) {
                case LTH:
                    return new ATSLBool(n1 < n2);

                case LEQ:
                    return new ATSLBool(n1 <= n2);

                case GTH:
                    return new ATSLBool(n1 > n2);

                case GEQ:
                    return new ATSLBool(n1 >= n2);

                case EQT:
                    return new ATSLBool(n1 == n2);

                case NEQ:
                    return new ATSLBool(n1 != n2);
            }
        }

        else if (v1 instanceof ATSLBool && v2 instanceof ATSLBool) {
            Boolean b1 = (Boolean) v1.value, b2 = (Boolean) v2.value;

            switch (op) {
                case EQT:
                    return new ATSLBool(b1 == b2);

                case NEQ:
                    return new ATSLBool(b1 != b2);

                default:
                    throw new UnsupportedOperationException();
            }
        }

        throw new UnsupportedOperationException();
    }

    public abstract ATSLValue add(ATSLValue value); 

    public final ATSLValue copy() {
        ATSLValue value = null;

        switch (type) {
            case ARRAY:
                value = new ATSLArray(null);
                break;

            case BOOLEAN:
                value = new ATSLBool(false);
                break;

            case INTEGER:
                value = new ATSLInteger(0);
                break;

            case REAL:
                value = new ATSLReal(0f);
                break;

            case STRING:
                value = new ATSLString(null);
                break;

            default:
                throw new UnsupportedOperationException();
        }

        value.dynamic = dynamic;
        value.type = type;
        value.value = this.value;

        return value;
    }

    public abstract ATSLValue divide(ATSLValue value);

    public abstract ATSLValue multiply(ATSLValue value);

    public static ATSLValue not(ATSLValue value) {
        if (value instanceof ATSLBool) {
            ATSLBool b = (ATSLBool) value;
            return new ATSLBool(!b.getValue());
        }

        throw new UnsupportedOperationException();
    }

    public abstract ATSLValue subtract(ATSLValue value);

    public abstract Object getValue();

    public final String toString() {
        return this instanceof ATSLArray ? Arrays.toString((ATSLValue[]) value) : value.toString();
    }

    public final TYPE getType() { return type; }
}
