grammar ATSL;

AND         : 'and'     | '&&'  ;
OR          : 'or'      | '||'  ;
BEGIN       : 'begin'           ;
END         : 'end'             ;
SCRIPT      : 'script'          ;
CALL        : 'call'            ;
FUNCTION    : 'function'        ;
IS          : 'is'      | '<-'  ;
TO          : 'to'              ;
NOT         : 'not'     | '!'   ;
AS          : 'as'              ;
WITH        : 'with'            ;
IF          : 'if'              ;
ELSE        : 'else'            ;
WHILE       : 'while'           ;
FOR         : 'for'             ;
THEN        : 'then'            ;
VAR         : 'var'             ;
TRUE        : 'true'            ;
FALSE       : 'false'           ;
WRITE       : 'write'           ;
READ        : 'read'            ;
TYPEOF      : 'typeof'          ;
DYNAMIC     : 'dynamic'         ;

TYPE_CONST 
    : 'array' 
    | 'bool' 
    | 'int' 
    | 'none' 
    | 'object'
    | 'real' 
    | 'string'
    ;

TYPE : '<' TYPE_CONST '>' ;

REL_OP
    : '<'
    | '<='
    | '>'
    | '>='
    | '=='
    | '!='
    ;

program
    : BEGIN SCRIPT ID
        function_list statement_list
        END SCRIPT ID
    ;

function_list
    : function function_list
    |
    ;

function
    : BEGIN FUNCTION name = ID (AS TYPE_CONST)?
        (WITH TYPE_CONST? id_list)?
        statement_list END FUNCTION ID
    ;

statement_list
    : statement statement_list
    |
    ;

statement
    : (TYPE_CONST | VAR) assignment_list                            #statementDeclaration
    | (TYPE_CONST | VAR) ID '[' index_term ']' (IS expression)?
        (',' ID '[' index_term ']' (IS expression)?)*               #statementArrayDeclaration
    | ID IS expression                                              #statementAssignment
    | ID '[' index_term ']' IS expression                           #statementArrayAssignment
    // | CALL ID (IS expression (',' expression)*)?                    #statementFunctionCall
    | BEGIN IF or_expression THEN
        statement_list (ELSE IF or_expression THEN
        statement_list)* (ELSE THEN statement_list)?
        END IF                                                      #statementIf
    | BEGIN WHILE or_expression THEN
        statement_list END WHILE                                    #statementWhile
    | BEGIN FOR ID IS expression TO expression
        THEN statement_list END FOR                                 #statementForAssignment
    | BEGIN FOR ID TO expression
        THEN statement_list END FOR                                 #statementFor
    | function_call                                                 #statementFunctionCall
    // | WRITE expression                                              #statementWrite
    | READ ID                                                       #statementRead
    ;

expression
    : add_expression
    | or_expression
    ;

add_expression
    : mult_expression op = ('+' | '-') add_expression   #add_expressionAddOp // Maybe rearrange
    | mult_expression                                   #add_expressionMultExpression
    ;

mult_expression
    : term op = ('*' | '/') mult_expression #mult_expressionMultOp
    | term                                  #mult_expressionTerm
    ;

term
    : '(' expression ')'                            #termExpression
    | ID                                            #termId
    | ID '[' index_term ']'                         #termArray
    | INT_CONST                                     #termInt
    | REAL_CONST                                    #termReal
    | STRING_CONST                                  #termString
    | function_call                                 #termFunctionCall
    ;

or_expression
    : and_expression OR and_expression  #or_expressionOp
    | and_expression                    #or_expressionAnd
    ;

and_expression
    : bool_term AND bool_term   #and_expressionOp
    | bool_term                 #and_expressionTerm
    ;

bool_term
    : '(' or_expression ')'                             #bool_termExpression
    | add_expression REL_OP add_expression              #bool_termCompare
    | b = (TRUE | FALSE)                                #bool_termConst
    | NOT bool_term                                     #bool_termNot
    ;

index_term returns [ATSLInteger value]
    : expression
    |
    ;

assignment_list
    : ID assignment (',' ID assignment)*
    ;

assignment
    : IS expression
    |
    ;

expression_list
    : expression (| ',' expression_list)
    ;

id_list
    : ID (',' ID)*
    ;

function_call
    : CALL WRITE IS expression_list #functionCallWrite
    | CALL ID (IS expression_list)? #functionCallId
    | CALL ID IS expression_list    #functionCallId
    | CALL DYNAMIC IS ID            #functionCallDynamic
    | CALL TYPE_CONST IS expression #functionCallCast
    | CALL TYPEOF IS expression     #functionCallTypeof
    ;

INT_CONST : '-'? POSITIVE_CONST ;
POSITIVE_CONST : ('0' | NON_ZERO_DIGIT DIGIT*) ;

REAL_CONST : INT_CONST '.' DIGIT+ ;

STRING_CONST : ('"' ~[\n\r]*? '"') | ('\'' ~[\n\r]*? '\'') ; 

NON_ZERO_DIGIT : [1-9] ;
DIGIT : NON_ZERO_DIGIT | '0' ;

ID : [a-zA-Z][a-zA-Z0-9_$]* ;
SC : '--' ~[\n\r]* -> skip ;
MC : '!-' .*? '-!' -> skip ;
WS : [ \t\n\r] -> channel (HIDDEN) ;
