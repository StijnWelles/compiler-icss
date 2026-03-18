grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f]; // Todo add support for #000, #bbb as short for #000000, #bbbbbb according to the CSS standard

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';


//--- PARSER: ---
stylesheet: statement* EOF;

statement: stylerule | variable;
stylerule: selector OPEN_BRACE property* CLOSE_BRACE; // todo multiple selectors with comma seperators
variableName: CAPITAL_IDENT;
variable: variableName ASSIGNMENT_OPERATOR additiveExpression SEMICOLON;


// Properties in rule
property: declaration | if_clause;
property_name: LOWER_IDENT;
declaration: property_name COLON additiveExpression SEMICOLON;
literal: COLOR #colorLiteral
       | PIXELSIZE #pixelLiteral
       | PERCENTAGE #percentageLiteral
       | SCALAR #scalarLiteral
       | TRUE #trueLiteral
       | FALSE #falseLiteral
       | CAPITAL_IDENT #variableReferenceLiteral;


// Conditions
if_clause: IF BOX_BRACKET_OPEN literal BOX_BRACKET_CLOSE
            OPEN_BRACE property* CLOSE_BRACE
            else_clause?;
else_clause: ELSE OPEN_BRACE property* CLOSE_BRACE;


//
selector
    : LOWER_IDENT #tagSelector
    | CLASS_IDENT #classSelector
    | ID_IDENT #idSelector;

// Math
multiplicativeExpression
    : literal #litExpression
    | multiplicativeExpression MUL literal #multExpression;
additiveExpression
    : multiplicativeExpression #ignore
    | additiveExpression PLUS multiplicativeExpression #plusExpression
    | additiveExpression MIN multiplicativeExpression #minExpression;
