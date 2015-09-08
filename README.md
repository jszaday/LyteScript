# Lytescript (Revision 1) Specification

**Note**, this document was converted from a Microsoft Word document and doesn't have all of the pretty formatting (and full function reference) that the .docx file (in master) has.

# Abstract
Lytescript (also referred to as Lyte) is a stack-based, multi-paradigm programming language that strives to have as little syntax as possible while still maintaining flexibility. With its batteries-included philosophy towards its standard library, this means that the language is easy to learn but powerful nevertheless. Lyte is primarily a mixture of the functional, loosely-typed and object-oriented programming language paradigms each of which affect the language differently. For example, it considers everything to be an object and reduces all arithmetic operations to function-calls.

## In-line Functions

    { /* Function Body */ }
    @( /* Arguments */ ) { /* Function Body */ }

Lytescript provides two ways to create in-line functions, also called blocks, which simply create a new block and push it onto the stack. The first of these is simply the functions body surrounded by curly-brackets whereas the second method denotes a set of named arguments. The latter is especially useful whenever arguments that would normally be lost after being popped off of the stack need to be used multiple times.

## Function Invocation

    F(a, b)   // Traditional
    b a F     // Stack-based
    b `F` a   // Infix notation

Lytescript provides multiple ways to invoke a function for the sake of code readability. The first of which is the traditional method which invisibly wraps each of its arguments in blocks and pushes them onto the stack in reverse-order. The stack-based method is the simplest way to invoke a function, as it simply assumes that the programmer has prepared the arguments on the stack and invokes the function. Lastly, using infix notation swaps the order of execution of the statement surrounded by back ticks and the following statement. Because of these properties, all three of the above examples are equivalent. Note, the infix method does not imply anything about the order of operations besides the aforementioned swapping; the limitation of this can be seen in the example below.

    4 `+` 2 `*` 6 /* This results in 36 rather than the result accounting for
                     the order of operations, 16 */

## Primitives

### Numbers & Strings

    4 4.0 0b100 0x4 04 4E0
    "An example string"

Lytescript provides several ways to represent a number, from right to left (in the above example) integer, floating point, binary, hexadecimal, octal and scientific. Each of these will push a representative Number object onto the stack. Strings are single-line chunks of text surrounded by double-quotes that allow for Javascript-style escapes.

### Objects

    %{ Key: Value, Key: Value, ... }

Objects are sets of key-value pairs (aka maps) that allow for keys that are valid identifiers, numbers or strings while values can be of any type. For more information about valid keys and different kinds of properties see _Working with Objects_.

### Lists

    %[ (Value | Range), (Value | Range), ... ]

Lists are integer-indexed collections of values of any type and ranges.

### Ranges

    Start:Finish
    Start:Step:Finish

Ranges are lists of numbers over that step by 1, -1 or a given amount over a given range (inclusive). They can only be created within Lists.

## Assignment Operations

    Destination <- value
    -> Destination
    -> [First, Second, Third, ...]

Like infix notation, the first of these assignment operations swaps the order of execution for readability purposes (in this case pushing the value onto the stack before popping it into the destination, it is important to note this only swaps the order of one statement so things like i <\- i `+` 1 will produce unexpected results). This syntax is typically used to initialize objects, templates or global functions. The second method simply pops a value off of the stack into the destination and should generally be used for updating/setting any other values. Finally, the third method binds to one (or more) destinations, popping the values into their destinations in left-to-right order.  

## Accessing List Elements and Object Properties

    object[key -or- index]

Lytescript provides a few different ways to index an object/list, the first two being fairly self-explanatory (as they mimic the behavior of many other languages). The latter two provide a way to access a specific object, the # prefix accesses the object on top of the stack whereas the @ prefix accesses the object set as the scopes self.

## A Brief Example/Recap

    HelloWorld <- @(times) {
      If(times `>` 1, HelloWorld(times `-` 1), {})
      EchoLn("Hello, World!")
    }
    HelloWorld(5)
    Fido <- %{
      name: "Fido",
      tricks: %[
        "play dead",
        "chase " `++` @name `++` "'s tail",
        "sit",
        "roll over"
      ],
      doTricks: {
        For(@tricks, @(trick) {
          EchoLn(@name `++` " performed the trick " `++` trick `++` "!")
        })
      }
    }
    
    Fido.doTricks()

Upon first looking at this example, one might be confused by the sudden appearance of things that resemble keywords. These keywords are actually functions from within Lytes Standard Library. Based on its output, its pretty easy to see the HelloWorld function just recursively prints a hello world message. Next, we can see that Fido is an object with a list of tricks. When the doTricks function is invoked, it iterates through this list and performs each of the tricks.

Lytes standard library is made up of several packages that provide over 100 different functions. For ease of use, some of these functions are accessible via aliases in the global scope (such as Lyte.Math.Add which has the alias +).

# Standard Library Overview

Lyte’s standard library is made up of several packages that provide over 100 different functions. For ease of use, some of these functions are accessible via aliases in the global scope (such as `Lyte.Math.Add` which has the alias `+`).

# A Note About the Status of the Docs
For the full reference, please consult the word file in the master branch. It does not have a lot of the standard methods (for Objects) so for now, the best way to see those would be to use Reflection. If you are still confused, consult the example programs (which might help). I am working on improving our docs but it is a tedious process that tends to get depriotized.
