/*
 * Lexer Rules
 */
lexer grammar StupsLexer;

@header {
package lexer;
}

// Fragments
fragment FIRST_DIGIT : [1-9] ;
fragment DIGIT : [0-9] ;

fragment LOWERCASE : [a-z] ;
fragment UPPERCASE : [A-Z] ;
fragment LETTER : LOWERCASE | UPPERCASE ;

fragment LETTER_DIGIT : LETTER | DIGIT ;

fragment WHITE : [\t\r\n\u0020] ; // HORIZONTAL TAB, CARRIAGE RETURN, LINE FEED, SPACE
fragment ANY : [\u0000-\u007F] ;
fragment ANY_NOBREAK : ~[\r\n] ;
fragment ANY_NOWHITE : ~[\t\r\n\u0020] ;

// Discard
WHITESPACE : WHITE+ -> skip ;
MULTILINE_COMMENT : '/*' ANY* '*/' -> skip ;
LINE_COMMENT : '//' ANY_NOBREAK* -> skip ;

// Keywords
CLASS : 'class' ;
PUBLIC : 'public' ;
STATIC : 'static' ;

VOID_TYPE : 'void' ;
BOOLEAN_TYPE : 'boolean' ;
STRING_TYPE : 'String' ;

IF : 'if' ;
ELSE : 'else' ;
WHILE : 'while' ;

PRINTLN : 'System.out.println' ;

// Operators
ASSIGN : '=' ;

ADD : '+' ;
SUB : '-' ;
MUL : '*' ;
DIV : '/' ;
MOD : '%' ;

NOT : '!' ;
AND : '&&' ;
OR : '||' ;

EQUAL : '==' ;
NOT_EQUAL : '!=' ;
LESS : '<' ;
LESS_EQUAL : '<=' ;
GREATER : '>' ;
GREATER_EQUAL : '>=' ;

// Structural
L_PAREN : '(' ;
R_PAREN : ')' ;
L_BRACE : '{' ;
R_BRACE : '}' ;
L_BRACKET : '[' ;
R_BRACKET : ']' ;
SEMICOLON : ';' ;
COMMA : ',' ;
DOT : '.' ;

// Literals
INTEGER_LIT : '-'? DIGIT+ ;
STRING_LIT : '"' ANY_NOBREAK* '"' ;
BOOLEAN_LIT : 'true' | 'false' ;

// Identifier
IDENTIFIER : LETTER LETTER_DIGIT* ;
