package io.github.aerodlyn.atsl;

import java.util.HashMap;

public class ATSLSymbolTable {
    private boolean dynamicallySetVariables;

    private int scope;

    private HashMap<String, ATSLValue> functions;
    private HashMap<Integer, HashMap<String, ATSLValue>> variables;

    public ATSLSymbolTable() {
        dynamicallySetVariables = true;
        variables = new HashMap<>();
        promoteScope();
    }

    public final boolean hasVariable(String id) {
        ATSLValue value = null;

        for (int i = scope; i >= 1 && value == null; i--)
            value = variables.get(i).get(id);

        return value != null;
    }

    public final void addElementToArrayVariable(String id, ATSLValue value, int index) {
        ATSLValue array = getVariable(id);

        if (array instanceof ATSLArray) {
            ((ATSLArray) array).putElement(index, value);
            reassignVariable(id, array);
        }

        else
            throw new UnsupportedOperationException("ERROR: Variable '" + id + "' is not an array.");
    }

    public final void createVariable(String id, ATSLValue value) {
        HashMap<String, ATSLValue> map = variables.get(scope);
        value.dynamic = dynamicallySetVariables;

        if (map.containsKey(id))
            throw new UnsupportedOperationException("ERROR: Variable '" + id + "' has already been declared.");

        else
            map.put(id, value);
    }

    public final void createVariableArray(String id, int capacity) {
        ATSLArray array = new ATSLArray(capacity);
        array.dynamic = dynamicallySetVariables;

        createVariable(id, array);
    }

    public final void demoteScope() {
        if (scope > 1)
            variables.remove(scope--);
    }

    public final void promoteScope() { variables.put(++scope, new HashMap<>()); }

    public final void reassignVariable(String id, ATSLValue value) {
        HashMap<String, ATSLValue> map = null;
        ATSLValue v = null;

        for (int i = scope; i >= 1 && map == null; i--) {
            if (variables.get(i).containsKey(id))
                map = variables.get(i);
        }

        if (map == null)
            throw new UnsupportedOperationException("Variable '" + id + "' has not been declared.");

        v = map.get(id);
        value.dynamic = v.dynamic;

        if (!v.isDynamic() && v.getType() != value.getType())
            throw new UnsupportedOperationException("Cannot reassign a static variable with a different type.");

        map.replace(id, value);
    }

    public void setDynamicState(boolean bool) { dynamicallySetVariables = bool; }

    public final ATSLValue getVariable(String id) {
        ATSLValue value = null;

        for (int i = scope; i >= 1 && value == null; i--)
            value = variables.get(i).get(id);

        if (value != null)
            return value;

        throw new UnsupportedOperationException("ERROR: Variable '" + id + "' has not been declared.");
    }

    public final ATSLValue getVariableFromArray(String id, int index) {
        ATSLValue array = getVariable(id);

        if (array instanceof ATSLArray)
            return ((ATSLArray) array).getElement(index);

        else
            throw new UnsupportedOperationException("ERROR: Variable '" + id + "' is not an array.");
    }
}
