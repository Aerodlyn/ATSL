package io.github.aerodlyn.atsl;

import java.util.HashMap;
import java.util.List;

import io.github.aerodlyn.atsl.ATSLParser.Statement_listContext;
import io.github.aerodlyn.atsl.ATSLValue.TYPE;

import org.antlr.v4.runtime.tree.TerminalNode;

public class ATSLFunction {
    private TYPE returnType;

    private List<TerminalNode> parameters;
    private Statement_listContext statements;

    public ATSLFunction(TYPE type, List<TerminalNode> parameters, Statement_listContext statements) {
        returnType = type;

        this.parameters = parameters;
        this.statements = statements;
    }

    public ATSLValue invoke(List<ATSLValue> parameters, HashMap<String, ATSLFunction> functions, ATSLScope scope) {
        scope = new ATSLScope(scope);
        ATSLMainVisitor visitor = new ATSLMainVisitor(scope, functions);

        for (int i = 0; i < parameters.size(); i++) {
            ATSLValue param = parameters.get(i);

            scope.setDeclaredType(param.getType());
            scope.declareVariable(this.parameters.get(i).getText(), param);
        }

        ATSLValue returnValue = null;
        
        try {
            visitor.visit(statements);
        }

        catch (ATSLReturn ex) {
            returnValue = ex.getReturnValue();

            if (!returnType.isTypeCompatibleWith(returnValue.getType()))
                throw new UnsupportedOperationException();
        }
        
        return returnValue;
    }
}
