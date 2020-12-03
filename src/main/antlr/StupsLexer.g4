/*
 * Lexer Rules
 */
lexer grammar StupsLexer;

@header {
package lexer;
}

fragment FIRST_DIGIT : [1-9] ;
fragment DIGIT : [0-9] ;

NUMBER : FIRST_DIGIT DIGIT* ;
WHITESPACE : ' ' -> skip ;
