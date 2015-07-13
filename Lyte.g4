grammar Lyte;

program
	: (statement Semicolon?)* EOF
	;

designator
	: LeftBracket simpleStatement+ RightBracket
	| Period Identifier
	;

invokable
	: Identifier (designator | parameters)*
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
	: LeftParen parameterList? Comma? RightParen
	;

// Misc. Lists
keyValuePair
  : (Identifier | stringLiteral | numericLiteral) Colon pushable
  ;

keyValueList
  : keyValuePair (Comma keyValuePair)*
  ;

lambdaArgsList
  : Identifier (Comma Identifier)*
  ;

valueList
  : pushable (Comma pushable)*
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
	| range
  ;

stringLiteral
	: StringLiteral
  ;

arrayLiteral
  : Percent LeftBracket valueList? Comma? RightBracket
  ;

objectLiteral
  : Percent LeftBrace keyValueList? Comma? RightBrace
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
	: Percent LeftBracket pushable Colon (pushable Colon)? pushable RightBracket
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
Semicolon:    ';';

Identifier
  : IdentifierStart IdentifierPart*
	| Sign (IdentifierStart | Sign)*
  | Atpersand IdentifierPart+
	| Percent IdentifierPart+
  ;

fragment Sign
	: [-+]
	;

fragment IdentifierStart
  : [_~!$^&\*=|<>?\/\\#a-zA-Z]
  ;

fragment IdentifierPart
  : IdentifierStart
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
