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
            throw ATSLException.variableDoesNotExist(id);
    }

    public void assignVariableInArray(int index, String id, ATSLValue value) {
        ATSLValue var = variables.get(id);

        if (var != null) {
            if (var instanceof ATSLArray) {
                ATSLArray arr = (ATSLArray) var;
                TYPE type = arr.getContainedType();

                if (type != TYPE.NONE && type != value.getType())
                    throw new UnsupportedOperationException();

                arr.putElement(index, value);
            }

            /*if (!var.isDynamic() && var.getType() != value.getType())
                throw ATSLException.variableIsNotDynamic(id);

            if (var instanceof ATSLArray) {
                ATSLArray arr = (ATSLArray) var;

                arr.putElement(index, value);
            }*/
        }

        else if (parent != null)
            parent.assignVariableInArray(index, id, value);

        else
            throw ATSLException.variableDoesNotExist(id);
    }

    public void declareVariable(String id, ATSLValue value) {
        ATSLValue var = value; // .copy();
        var.dynamic = declaredType == TYPE.NONE;

        if (!value.isDynamic() && value.getType() != declaredType && value.getType() != TYPE.ARRAY)
            throw ATSLException.variableIsNotDynamic(id);

        if (!variables.containsKey(id))
            variables.put(id, var);

        else
            throw ATSLException.variableAlreadyExists(id);
    }

    public void setDeclaredType(TYPE type) { declaredType = type; }

    public ATSLScope getCopy() { 
        ATSLScope scope = new ATSLScope(parent);
        scope.variables = new HashMap<>(variables);

        return scope;
     }

    public ATSLScope getParent() { return parent; }

    public ATSLValue getValueInArray(int index, String id) {
        ATSLValue var = variables.get(id);

        if (var != null) {
            if (var instanceof ATSLArray)
                return ((ATSLArray) var).getElement(index);

            throw new UnsupportedOperationException();
        }

        else if (!isGlobalScope())
            return parent.getValueInArray(index, id);
        
        throw ATSLException.variableDoesNotExist(id);
    }

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
