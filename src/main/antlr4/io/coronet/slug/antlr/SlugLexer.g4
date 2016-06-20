lexer grammar SlugLexer;

/*
 * Default lexer mode definition.
 */

OPEN_BRACE  : '{' ;
CLOSE_BRACE : '}' ;

OPEN_BRACKET  : '[' ;
CLOSE_BRACKET : ']' ;

SEMICOLON : ';' ;

SLUG : 'slug' ;

LIST : 'list' ;

MAP : 'map' ;

IDENTIFIER : NON_DIGIT ( DIGIT | NON_DIGIT )* ;

fragment DIGIT : [0-9] ;
fragment NON_DIGIT : [a-zA-Z_] ;

STARTDOCS : '/**' -> pushMode(DOCS) ;

WS           : [ \t\r\n]+    -> skip;
COMMENT      : '/*' ~[*] .*? '*/' -> skip;
LINE_COMMENT : '//' ~[\r\n]* -> skip;

/*
 * DOCS lexer mode definition.
 */

mode DOCS;

ENDDOCS: '*/' -> popMode ;
DOCSTRING : (~[*] | '*' ~[/])* ;
