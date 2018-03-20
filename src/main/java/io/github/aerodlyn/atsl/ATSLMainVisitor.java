package io.github.aerodlyn.atsl;

import io.github.aerodlyn.atsl.ATSLBaseVisitor;
import io.github.aerodlyn.atsl.ATSLParser;
import io.github.aerodlyn.atsl.ATSLParser.Statement_listContext;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ATSLMainVisitor extends ATSLBaseVisitor<ATSLValue> {
    private ATSLScope scope;
    private HashMap<String, ATSLFunction> functions;

    public ATSLMainVisitor() { this(new ATSLScope(), new HashMap<>()); }

    public ATSLMainVisitor(ATSLScope scope, HashMap<String, ATSLFunction> functions) {
        this.scope = scope;
        this.functions = functions;
    }

    @Override
    public ATSLValue visitProgram(ATSLParser.ProgramContext ctx) {
        String bId = ctx.ID (0).getText(), eId = ctx.ID (1).getText();

        if (!bId.equals(eId))
            throwError("Begin ID '" + bId + "' does not match end ID '" + eId + "'");

        visit(ctx.function_list());
        visit(ctx.statement_list());
        
        return null;
    }

    @Override
    public ATSLValue visitFunction(ATSLParser.FunctionContext ctx) {
        List<TerminalNode> parameters = ctx.id_list() != null 
            ? ctx.id_list().ID() : new ArrayList<>();
        Statement_listContext statements = ctx.statement_list();
        String id = ctx.name.getText() + parameters.size();

        functions.put(id, new ATSLFunction(parameters, statements));

        return null;
    }

    /* Begin Statement */
    @Override
    public ATSLValue visitStatementDeclaration(ATSLParser.StatementDeclarationContext ctx) {
        scope.setDeclaredType(ctx.TYPE_CONST() == null ? null : ctx.TYPE_CONST().getText());
        visit(ctx.assignment_list());

        return null;
    }

    @Override
    public ATSLValue visitStatementArrayDeclaration(ATSLParser.StatementArrayDeclarationContext ctx) {
        String[] decls = ctx.getText().split(",");

        if (ctx.TYPE_CONST() != null) {
            if (ctx.TYPE_CONST().getText().equals("array"))
                scope.setDeclaredType(null);

            else
                throw new UnsupportedOperationException("Arrays must be of type 'array'.");
        }

        for (int i = 0, j = 0; i < decls.length; i++) {
            String id = ctx.ID(i).getText();
            ATSLValue assign = decls[i].contains("<-") ? visit(ctx.expression(j++)) : null;
            int n = ((ATSLInteger) visit(ctx.index_term(i))).getValue();

            n = n == -1 ? 0 : n;

            /*table.createVariableArray(id, n);

            if (assign != null)
                table.addElementToArrayVariable(id, assign, n - 1);*/
        }

        return null;
    }

    @Override
    public ATSLValue visitStatementAssignment(ATSLParser.StatementAssignmentContext ctx) {
        scope.assignVariable(ctx.ID().getText(), visit(ctx.expression()));
        return null;
    }

    @Override
    public ATSLValue visitStatementArrayAssignment(ATSLParser.StatementArrayAssignmentContext ctx) {
        int n = ((ATSLInteger) visit(ctx.index_term())).getValue();
        // table.addElementToArrayVariable(ctx.ID().getText(), visit(ctx.expression()), n);

        return null;
    }

    @Override
    public ATSLValue visitStatementIf(ATSLParser.StatementIfContext ctx) {
        List<ATSLParser.Or_expressionContext> bools = ctx.or_expression();
        boolean visited = false;

        scope = new ATSLScope(scope);

        for (int i = 0; i < bools.size() && !visited; i++) {
            if ((Boolean) visit(bools.get(i)).getValue()) {
                visit(ctx.statement_list(i));
                visited = true;
            }
        }

        if (!visited && bools.size () + 1 == ctx.statement_list().size())
            visit(ctx.statement_list(bools.size()));

        scope = scope.getParent();
        return null;
    }

    @Override
    public ATSLValue visitStatementWhile(ATSLParser.StatementWhileContext ctx) {
        while (((ATSLBool) visit(ctx.or_expression())).getValue()) {
            scope = new ATSLScope(scope);
            visit(ctx.statement_list());
            scope = scope.getParent();
        }

        return null;
    }

    @Override
    public ATSLValue visitStatementForAssignment(ATSLParser.StatementForAssignmentContext ctx) {
        ATSLValue v1 = visit(ctx.expression(0)), v2 = visit(ctx.expression(1));
        String id = ctx.ID().getText();

        if (v1.getType() == ATSLValue.TYPE.INTEGER && v2.getType() == ATSLValue.TYPE.INTEGER) {
            int n1 = (Integer) v1.getValue(), n2 = (Integer) v2.getValue();

            if (!scope.isVariableInScope(id))
                scope.declareVariable(id, v1);

            for (; n1 <= n2; n1++) {
                scope.assignVariable(id, new ATSLInteger(n1));
                scope = new ATSLScope(scope);
                visit(ctx.statement_list());
                scope = scope.getParent();

                n1 = (Integer) scope.getVariable(id).getValue();
            }
        }

        return null;
    }

    @Override
    public ATSLValue visitStatementFor(ATSLParser.StatementForContext ctx) {
        String id = ctx.ID().getText();
        ATSLValue v1 = scope.getVariable(id), v2 = visit(ctx.expression());

        if (v1.getType() == ATSLValue.TYPE.INTEGER && v2.getType() == ATSLValue.TYPE.INTEGER) {
            int n1 = (Integer) v1.getValue(), n2 = (Integer) v2.getValue();

            for (; n1 <= n2; n1++) {
                scope.assignVariable(id, new ATSLInteger(n1));
                scope = new ATSLScope(scope);
                visit(ctx.statement_list());
                scope = scope.getParent();

                n1 = (Integer) scope.getVariable(id).getValue();
            }
        }

        return null;
    }

    @Override
    public ATSLValue visitStatementReturn(ATSLParser.StatementReturnContext ctx) {
        ATSLValue value = ctx.expression() != null ? visit(ctx.expression()) : null;
        
        throw new ATSLReturn(value);
    }

    @Override
    public ATSLValue visitStatementFunctionCall(ATSLParser.StatementFunctionCallContext ctx) {
        return visit(ctx.function_call());
    }
    /* End Statement */

    /* Begin Add Expression */
    @Override
    public ATSLValue visitAdd_expressionAddOp(ATSLParser.Add_expressionAddOpContext ctx) {
        ATSLValue v1 = visit (ctx.mult_expression()), v2 = visit (ctx.add_expression());

        switch (ctx.op.getText()) {
            case "+":
                return v1.add(v2);

            case "-":
                return v1.subtract(v2);
        }

        return null;
    }

    @Override
    public ATSLValue visitAdd_expressionMultExpression(ATSLParser.Add_expressionMultExpressionContext ctx) {
        return visit (ctx.mult_expression());
    }
    /* End Add Expression */

    /* Begin Mult Expression */
    @Override
    public ATSLValue visitMult_expressionMultOp(ATSLParser.Mult_expressionMultOpContext ctx) {
        ATSLValue v1 = visit (ctx.term()), v2 = visit (ctx.mult_expression());

        switch (ctx.op.getText()) {
            case "*":
                return v1.multiply(v2);

            case "/":
                return v1.divide(v2);
        }

        return null;
    }

    @Override
    public ATSLValue visitMult_expressionTerm(ATSLParser.Mult_expressionTermContext ctx) {
        return visit (ctx.term());
    }
    /* End Mult Expression */

    /* Begin Term */
    @Override
    public ATSLValue visitTermExpression(ATSLParser.TermExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public ATSLValue visitTermId(ATSLParser.TermIdContext ctx) {
        return scope.getVariable(ctx.ID().getText());
    }

    @Override
    public ATSLValue visitTermArray(ATSLParser.TermArrayContext ctx) {
        return null; // table.getVariableFromArray(ctx.ID().getText(), ((ATSLInteger) visit(ctx.index_term())).getValue());
    }

    @Override
    public ATSLValue visitTermInt(ATSLParser.TermIntContext ctx) {
        return new ATSLInteger(ctx.INT_CONST().getText());
    }

    @Override
    public ATSLValue visitTermReal(ATSLParser.TermRealContext ctx) {
        return new ATSLReal(ctx.REAL_CONST().getText());
    }

    @Override
    public ATSLValue visitTermString(ATSLParser.TermStringContext ctx) {
        String str = ctx.STRING_CONST().getText();
        return new ATSLString(str.substring(1, str.length() - 1));
    }
    /* End Term */

    /* Begin Bool Expression Or */
    @Override
    public ATSLValue visitOr_expressionOp(ATSLParser.Or_expressionOpContext ctx) {
        ATSLBool b1 = (ATSLBool) visit(ctx.and_expression(0)), b2 = (ATSLBool) visit(ctx.and_expression(1));

        return new ATSLBool(b1.getValue() || b2.getValue());
    }

    @Override
    public ATSLValue visitOr_expressionAnd(ATSLParser.Or_expressionAndContext ctx) {
        return visit(ctx.and_expression());
    }
    /* End Bool Expression Or */

    /* Begin Bool Expression And */
    @Override
    public ATSLValue visitAnd_expressionOp(ATSLParser.And_expressionOpContext ctx) {
        ATSLBool b1 = (ATSLBool) visit(ctx.bool_term(0)), b2 = (ATSLBool) visit(ctx.bool_term(1));

        return new ATSLBool(b1.getValue() && b2.getValue());
    }

    @Override
    public ATSLValue visitAnd_expressionTerm(ATSLParser.And_expressionTermContext ctx) {
        return visit(ctx.bool_term());
    }
    /* End Bool Expression Or */

    /* Begin Bool Term */
    @Override
    public ATSLValue visitBool_termExpression(ATSLParser.Bool_termExpressionContext ctx) {
        return visit(ctx.or_expression());
    }

    @Override
    public ATSLValue visitBool_termCompare(ATSLParser.Bool_termCompareContext ctx) {
        ATSLValue v1 = visit(ctx.add_expression(0)), v2 = visit(ctx.add_expression(1));
        return ATSLValue.compare(v1, v2, ATSLValue.COMPARE.mapOperator(ctx.REL_OP().getText()));
    }

    @Override
    public ATSLValue visitBool_termConst(ATSLParser.Bool_termConstContext ctx) {
        return new ATSLBool(ctx.b.getText());
    }

    @Override
    public ATSLValue visitBool_termNot(ATSLParser.Bool_termNotContext ctx) {
        return ATSLValue.not(visit(ctx.bool_term()));
    }
    /* End Bool Term */

    @Override
    public ATSLInteger visitIndex_term(ATSLParser.Index_termContext ctx) {
        ATSLValue value = ctx.expression() == null ? null : visit(ctx.expression());

        if (value == null)
            return new ATSLInteger(-1);

        else if (value instanceof ATSLInteger)
            return (ATSLInteger) value;

        throw new UnsupportedOperationException();
    }

    @Override
    public ATSLValue visitAssignment_list(ATSLParser.Assignment_listContext ctx) {
        for (int i = 0; i < ctx.assignment().size(); i++)
            scope.declareVariable(ctx.ID(i).getText(), visit(ctx.assignment(i)));

        return null;
    }

    @Override
    public ATSLValue visitAssignment(ATSLParser.AssignmentContext ctx) {
        return ctx.expression() == null ? new ATSLNone(scope.getDeclaredType()) : visit(ctx.expression());
    }

    /* Begin Function Call */
    @Override
    public ATSLValue visitFunctionCallId(ATSLParser.FunctionCallIdContext ctx) {
        ArrayList<ATSLValue> list = createParameterList(ctx.expression_list());
        String id = ctx.ID().getText() + list.size();

        return functions.get(id).invoke(list, functions, scope);
    }

    @Override
    public ATSLValue visitFunctionCallWrite(ATSLParser.FunctionCallWriteContext ctx) {
        ArrayList<ATSLValue> list = createParameterList(ctx.expression_list());

        for (ATSLValue v : list)
            System.out.print(v);

        System.out.println();

        return null;
    }

    @Override
    public ATSLValue visitFunctionCallDynamic(ATSLParser.FunctionCallDynamicContext ctx) {
        return new ATSLBool(scope.getVariable(ctx.ID().getText()).isDynamic());
    }

    @Override
    public ATSLValue visitFunctionCallCast(ATSLParser.FunctionCallCastContext ctx) {
        ATSLValue value = visit(ctx.expression());

        switch (ctx.TYPE_CONST().getText()) {
            case "int":
                return new ATSLInteger(((Number) value.value).intValue());

            case "real":
                return new ATSLReal(((Number) value.value).floatValue());

            case "string":
                return new ATSLString(value.toString());

            default:
                return null;
        }
    }

    @Override
    public ATSLValue visitFunctionCallTypeof(ATSLParser.FunctionCallTypeofContext ctx) {
        return new ATSLString(visit(ctx.expression()).getType().toString());
    }
    /* End Function Call */

    private void createParameterList(ATSLParser.Expression_listContext ctx, ArrayList<ATSLValue> list) {
        for (int i = 0; i < ctx.expression().size(); i++)
            list.add(visit(ctx.expression(i)));

        /*if (ctx.expression_list() != null)
            createParameterList(ctx.expression_list(), list);*/
    }

    private void throwError(String message) {
        System.err.println("ERROR: " + message + ".");
        System.exit(-1);
    }

    private ArrayList<ATSLValue> createParameterList(ATSLParser.Expression_listContext ctx) {
        ArrayList<ATSLValue> list = new ArrayList<>();
        
        if (ctx != null)
            createParameterList(ctx, list);
        
        return list;
    }
}
