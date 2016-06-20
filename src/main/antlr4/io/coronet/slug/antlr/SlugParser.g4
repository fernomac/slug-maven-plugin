parser grammar SlugParser;

options { tokenVocab = SlugLexer; }

slug
 : docs
   SLUG name OPEN_BRACE
     fields
   CLOSE_BRACE
   EOF
 ;

fields
 : field*
 ;

field
 : docs?
   type name SEMICOLON
 ;

type
 : name
 | list
 | map
 ;

list
 : LIST OPEN_BRACKET type CLOSE_BRACKET
 ;

map
 : MAP OPEN_BRACKET type CLOSE_BRACKET
 ;

name
 : IDENTIFIER
 ;

docs
 : STARTDOCS
   docstring
   ENDDOCS
 ;

docstring
 : DOCSTRING
 ;

