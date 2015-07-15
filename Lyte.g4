grammar Lyte;

program
	: (statement)* EOF
	;

designator
	: LeftBracket simpleStatement+ RightBracket
	| Period Identifier
	;
  
invokable
	: Identifier (designator | parameters)*
  | (Hashtag | Atpersand) designator (designator | parameters)*
	;

bindingExpression
	: leftBindingExpression
	| rightBindingExpression
	;

statement
	: simpleStatement
	| bindingExpression
	;

simpleStatement
	: pushable
	| infixExpression
	;

infixExpression
	: Backtick invokable Backtick simpleStatement
	;

rightBindingExpression
	: RightBind (invokable | LeftBracket invokableList RightBracket)
	;

leftBindingExpression
  : invokable LeftBind pushable
  ;

pushable
	: invokable
	| literal
  | lambdaExpression
	| block
	;

parameters
	: LeftParen parameterList? RightParen
	;

// Misc. Lists
key
  : stringLiteral
  | numericLiteral
  | LeftBracket simpleStatement+ RightBracket
  | Identifier
  ;

keyValuePair
  : (LeftBind | RightBind)? key Colon pushable
  ;

keyValueList
  : keyValuePair (Comma keyValuePair)*
  ;

lambdaArgsList
  : Identifier (Comma Identifier)*
  ;

valueList
  : (simpleStatement+ | range) (Comma (simpleStatement+ | range))*
  ;

parameterList
  : simpleStatement+ (Comma simpleStatement+)*
  ;

invokableList
	:	invokable (Comma invokable)*
	;

// Various Literals
literal
  : stringLiteral
  | numericLiteral
  | objectLiteral
  | arrayLiteral
  ;

stringLiteral
	: StringLiteral
  ;

arrayLiteral
  : Percent LeftBracket valueList? RightBracket
  ;

objectLiteral
  : Percent LeftBrace keyValueList? RightBrace
  ;

numericLiteral
  : DecimalLiteral
  | BinaryIntegerLiteral
  | HexIntegerLiteral
  | OctalIntegerLiteral
  ;

block
	: LeftBrace statement* RightBrace
	;

lambdaExpression
  : Atpersand LeftParen lambdaArgsList? RightParen block
  ;

range
	: pushable Colon (pushable Colon)? pushable
	;

// Misc. Symbols
LeftBind:    '<-';
RightBind:    '->';
LeftParen:    '(';
RightParen:   ')';
LeftBracket:  '[';
RightBracket: ']';
LeftBrace:    '{';
RightBrace:   '}';
Comma:        ',';
Period:       '.';
Backtick:     '`';
Colon:        ':';
Ellipsis:     '...';
Hashtag:      '#';
Atpersand:    '@';
VerticalBar:  '|';
Percent:			'%';
  
Identifier
  : IdentifierStart IdentifierPart*
	| Sign (IdentifierStart | Sign)*
  | (Hashtag | Atpersand | Percent) IdentifierPart+
  ;
  
fragment Sign
	: [-+]
	;

fragment IdentifierStart
  : [_~!$^&\*=|;<>?\/\\a-zA-Z]
  ;

fragment IdentifierPart
  : IdentifierStart
  | Percent
	| Sign
  | [0-9]
  ;

fragment DecimalDigit
  : [0-9]
  ;

fragment BinaryDigit
  : [01]
  ;

fragment HexDigit
  : [0-9a-fA-F]
  ;

fragment OctalDigit
  : [0-7]
  ;

fragment ExponentPart
  : [eE] [+-]? DecimalDigit+
  ;

fragment DecimalIntegerLiteral
  : '0'
  | [1-9] DecimalDigit*
  ;

DecimalLiteral
  : DecimalIntegerLiteral '.' DecimalDigit* ExponentPart?
  | '.' DecimalDigit+ ExponentPart?
  | DecimalIntegerLiteral ExponentPart?
	| Sign DecimalLiteral
  ;

HexIntegerLiteral
  : '0' [xX] HexDigit+
  ;

BinaryIntegerLiteral
  : '0' [bB] BinaryDigit+
  ;

OctalIntegerLiteral
  : '0' OctalDigit+
  ;

StringLiteral
  : '"' ( ~'"' | '\\' '"' )* '"'
  ;

WhiteSpaces
	: [\t\u000B\u000C\u0020\u00A0]+ -> skip
	;

Newline
  : ( '\r' '\n'?
    | '\n'
    ) -> skip
  ;

BlockComment
  :   '/*' .*? '*/'
      -> skip
  ;

LineComment
  :   '//' ~[\r\n]*
      -> skip
  ;
