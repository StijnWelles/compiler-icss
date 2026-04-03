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
fragment HEX: [0-9a-f];
fragment COLOR_6: '#' HEX HEX HEX HEX HEX HEX;
fragment COLOR_8: '#' HEX HEX HEX HEX HEX HEX HEX HEX;
fragment COLOR_3: '#' HEX HEX HEX;
fragment COLOR_4: '#' HEX HEX HEX HEX;
COLOR: COLOR_6 | COLOR_8 | COLOR_3 | COLOR_4;

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

// Inline comments (// ...)
LINE_COMMENT: '//' ~[\r\n]* -> skip;
// Block comments (/* ... */)
BLOCK_COMMENT: '/*' .*? '*/' -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
DIV: '/';
ASSIGNMENT_OPERATOR: ':=';


//--- PARSER: ---
stylesheet: statement* EOF;

statement: stylerule | if_clause | variable;
stylerule: multiple_selectors OPEN_BRACE property* CLOSE_BRACE;
variableName: CAPITAL_IDENT;
variable: variableName ASSIGNMENT_OPERATOR expression SEMICOLON;


// Properties in rule
property: declaration | if_clause | variable;
property_name: LOWER_IDENT;
declaration: property_name COLON expression SEMICOLON;
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


// Selectors
multiple_selectors: selector (',' selector)*;
selector
    : LOWER_IDENT #tagSelector
    | CLASS_IDENT #classSelector
    | ID_IDENT #idSelector;

// Math
expression: literal #litExpression
          | expression MUL expression #multExpression
          | expression DIV expression #divExpression
          | expression PLUS expression #plusExpression
          | expression MIN expression #minExpression;