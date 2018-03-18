package io.github.aerodlyn.atsl;

import java.util.HashMap;

import io.github.aerodlyn.atsl.ATSLValue.TYPE;

public class ATSLScope {
    private ATSLScope parent;
    private HashMap<String, ATSLValue> variables;
    private TYPE declaredType;

    public ATSLScope() { this(null); }

    public ATSLScope(ATSLScope parent) {
        this.parent = parent;

        variables = new HashMap<>();
    }

    public boolean isGlobalScope() { return parent == null; }

    public boolean isVariableInScope(String id) {
        try {
            getVariable(id);
        }

        catch (ATSLException ex) {
            return false;
        }

        return true;
    }

    public void assignVariable(String id, ATSLValue value) {
        ATSLValue var = variables.get(id);

        if (var != null) {
            if (!var.isDynamic() && var.getType() != value.getType())
                throw ATSLException.variableIsNotDynamic(id);

            // value.dynamic = var.dynamic; // Need to copy
            variables.replace(id, var, value);
        }

        else if (parent != null)
            parent.assignVariable(id, value);

        else
            throw ATSLException.variableAlreadyExists(id);
    }

    public void declareVariable(String id, ATSLValue value) {
        ATSLValue var = value.copy();
        var.dynamic = declaredType == null;

        if (declaredType != null && value.getType() != declaredType)
            throw ATSLException.variableIsNotDynamic(id);

        if (!variables.containsKey(id))
            variables.put(id, var);

        else
            throw new UnsupportedOperationException();
    }

    public void setDeclaredType(String type) { 
        if (type != null) {
            switch (type) {
                case "array":
                    declaredType = ATSLValue.TYPE.ARRAY;
                    break;

                case "bool":
                    declaredType = ATSLValue.TYPE.BOOLEAN;
                    break;

                case "int":
                    declaredType = ATSLValue.TYPE.INTEGER;
                    break;

                case "none":
                    throw new UnsupportedOperationException("Cannot declare a variable to have type 'none'.");

                case "object":
                    declaredType = ATSLValue.TYPE.OBJECT;
                    break;

                case "real":
                    declaredType = ATSLValue.TYPE.REAL;
                    break;
            }
        }

        else
            declaredType = null;
    }

    public ATSLScope getCopy() { 
        ATSLScope scope = new ATSLScope(parent);
        scope.variables = new HashMap<>(variables);

        return scope;
     }

    public ATSLScope getParent() { return parent; }

    public ATSLValue getVariable(String id) {
        ATSLValue value = variables.get(id);

        if (value != null)
            return value;

        else if (!isGlobalScope())
            return parent.getVariable(id);
        
        throw ATSLException.variableDoesNotExist(id);
    }

    public TYPE getDeclaredType() { return declaredType; }
}
