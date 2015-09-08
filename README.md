<div class="WordSection1">

<div style="border:none;border-bottom:solid #4F81BD 1.0pt;padding:0in 0in 4.0pt 0in">

Lytescript (Revision 1) Specification

</div>

# Abstract

Lytescript (also referred to as Lyte) is a stack-based, multi-paradigm programming language that strives to have as little syntax as possible while still maintaining flexibility. With its “batteries-included” philosophy towards its standard library, this means that the language is easy to learn but powerful nevertheless. Lyte is primarily a mixture of the functional, loosely-typed and object-oriented programming language paradigms each of which affect the language differently. For example, it considers everything to be an object and reduces all arithmetic operations to function-calls.

# Syntax Overview

## In-line Functions

![{ /* Function Body */ }
@( /* Arguments */ ) { /* Function Body */ }
](Lytescript_files/image001.png)

Lytescript provides two ways to create in-line functions, also called blocks, which simply create a new block and push it onto the stack. The first of these is simply the function’s body surrounded by curly-brackets whereas the second method denotes a set of named arguments. The latter is especially useful whenever arguments that would normally be lost after being popped off of the stack need to be used multiple times.

## Function Invocation

![F(a, b)   // Traditional
b a F     // Stack-based
b `F` a   // Infix notation

](Lytescript_files/image002.png)

Lytescript provides multiple ways to invoke a function for the sake of code readability. The first of which is the “traditional” method which “invisibly” wraps each of its arguments in blocks and pushes them onto the stack in reverse-order. The stack-based method is the simplest way to invoke a function, as it simply assumes that the programmer has prepared the arguments on the stack and invokes the function. Lastly, using infix notation swaps the order of execution of the statement surrounded by back ticks and the following statement. Because of these properties, all three of the above examples are equivalent. Note, the infix method does not imply anything about the order of operations besides the aforementioned swapping; the limitation of this can be seen in the example below.

![4 `+` 2 `*` 6 /* This results in 36 rather than the result accounting for
                 the order of operations, 16 */
](Lytescript_files/image003.png)

**<span style="font-size:13.0pt;line-height:115%;font-family:&quot;Cambria&quot;,serif;
color:#4F81BD">  
</span>**

## Primitives

### Numbers & Strings

![4 4.0 0b100 0x4 04 4E0
"An example string"
](Lytescript_files/image004.png)

Lytescript provides several ways to represent a number, from right to left (in the above example) integer, floating point, binary, hexadecimal, octal and scientific. Each of these will push a representative Number object onto the stack. Strings are single-line chunks of text surrounded by double-quotes that allow for Javascript-style escapes.

### Objects

![%{ Key: Value, Key: Value, ... }](Lytescript_files/image005.png)

Objects are sets of key-value pairs (aka maps) that allow for keys that are valid identifiers, numbers or strings while values can be of any type. For more information about valid keys and different kinds of properties see <u>Working with Objects</u>.

### Lists

![%[ (Value | Range), (Value | Range), ... ]](Lytescript_files/image006.png)

Lists are integer-indexed collections of values of any type and ranges.

### Ranges

![Start:Finish
Start:Step:Finish
](Lytescript_files/image007.png)

Ranges are lists of numbers over that step by 1, -1 or a given amount over a given range (inclusive). They can only be created within Lists.

## Assignment Operations

![Destination <- value
-> Destination
-> [First, Second, Third, ...]
](Lytescript_files/image008.png)

Like infix notation, the first of these assignment operations swaps the order of execution for readability purposes (in this case pushing the value onto the stack before popping it into the destination, it is important to note this only swaps the order of one statement so things like <span style="font-size:10.0pt;line-height:115%;font-family:&quot;Courier New&quot;;background:
#F2F2F2">i <- i <span style="color:#548DD4">`+`</span> <span style="color:#E36C0A">1</span></span> will produce unexpected results). This syntax is typically used to initialize objects, templates or global functions. The second method simply pops a value off of the stack into the destination and should generally be used for updating/setting any other values. Finally, the third method binds to one (or more) destinations, popping the values into their destinations in left-to-right order.  

## Accessing List Elements and Object Properties

![object[key/index] –or- list[index]
object.key –or- list.key
#key –or- @key
#[index/key] –or- @[index/key]
](Lytescript_files/image009.png)

Lytescript provides a few different ways to index an object/list, the first two being fairly self-explanatory (as they mimic the behavior of many other languages). The latter two provide a way to access a specific object, the <span style="font-size:10.0pt;line-height:115%;font-family:
&quot;Courier New&quot;">#</span> prefix accesses the object on top of the stack whereas the <span style="font-size:10.0pt;line-height:115%;font-family:&quot;Courier New&quot;">@</span> prefix accesses <span style="font-size:10.0pt;line-height:115%;font-family:&quot;Courier New&quot;"></span> the object set as the scope’s “self.”

## A Brief Example/Recap

![HelloWorld <- @(times) {
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
](Lytescript_files/image010.png)

![](Lytescript_files/image011.jpg)Upon first looking at this example, one might be confused by the sudden appearance of things that resemble keywords. These “keywords” are actually functions from within Lyte’s Standard Library. Based on its output, it’s pretty easy to see the <span style="font-size:10.0pt;line-height:115%;font-family:&quot;Courier New&quot;">HelloWorld</span> function just recursively prints a hello world message. Next, we can see that <span style="font-size:10.0pt;line-height:115%;font-family:&quot;Courier New&quot;">Fido</span> is an object with a list of <span style="font-size:10.0pt;line-height:115%;
font-family:&quot;Courier New&quot;">tricks</span>. When the <span style="font-size:10.0pt;
line-height:115%;font-family:&quot;Courier New&quot;">doTricks</span> function is invoked, it iterates through this list and “performs” each of the tricks.

# Standard Library Overview

Lyte’s standard library is made up of several packages that provide over 100 different functions. For ease of use, some of these functions are accessible via aliases in the global scope (such as <span style="font-size:
10.0pt;line-height:115%;font-family:&quot;Courier New&quot;">Lyte.Math.Add</span> which has the alias <span style="font-size:10.0pt;line-height:115%;font-family:&quot;Courier New&quot;">+</span>).

## The Core Package

<table class="MsoNormalTable" border="0" cellspacing="0" cellpadding="0" width="631" style="width:473.25pt;margin-left:4.65pt;border-collapse:collapse">

<tbody>

<tr style="height:20.25pt">

<td width="167" style="width:125.0pt;border-top:solid #548DD4 1.5pt;border-left:
  none;border-bottom:solid #548DD4 1.5pt;border-right:none;background:#366092;
  padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Full Path</span>**

</td>

<td width="84" style="width:62.9pt;border-top:solid #548DD4 1.5pt;border-left:
  none;border-bottom:solid #548DD4 1.5pt;border-right:none;background:#366092;
  padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Alias</span>**

</td>

<td width="380" style="width:285.35pt;border-top:solid #548DD4 1.5pt;
  border-left:none;border-bottom:solid #548DD4 1.5pt;border-right:none;
  background:#366092;padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Brief</span>**

</td>

</tr>

<tr style="height:15.75pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.And</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">And</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Computes the logical and of two values</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Apply</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Apply</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Applies the block on the stack</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.BitwiseAnd</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black"> </span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Computes the bitwise and of two numerical  values</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.BitwiseNot</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black"> </span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Computes the bitwise not of a numerical  value</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.BitwiseOr</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black"> </span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Computes the bitwise or of two numerical  values</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.BitwiseXor</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black"> </span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Computes the bitwise xor of two numerical  values</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Dig</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Dig</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Digs up the nth value to the top of the stack (Dig(2) is equivalent to Swap)</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Equal</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">==</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Tests if two values are unequal, coercing their types if needed</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.EqualStrict</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">===</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Tests if two values are strictly equal, in other words if they are of the same type and value</span>

</td>

</tr>

<tr style="height:48.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:48.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.For</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:48.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">For</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:48.0pt">

<span style="color:black">Takes either a list or two numbers and a function, iterates through the values in the range (inclusive) or through each element in a list, pushing the current value onto the stack then calling the function each time</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.If</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">If</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">If the first argument is truthy execute the second argument otherwise execute the third</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Import</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Import</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Imports an external class, see <u>Importing Classes & Packages</u> for more details.</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.IsNull</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Null?</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Checks to see if a value is null</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.IsUndefined</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Undefined?</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Checks to see if a value is null</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Not</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Not</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Computes the logical not of a value.</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.NotEqual</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">!=</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Tests if two values are equal, coercing their types if needed</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.NotEqualStrict</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">!==</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Tests if two values are strictly unequal, in other words if they are of the same type and value</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Null</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Null</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Returns the null value</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Or</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Or</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Computes the logical or of two values</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Pop</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Pop</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Pops the value on top of the stack</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Same</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Same?</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Checks if two values are references of the same object.</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Swap</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Swap</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Swaps the top two values on the stack.</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.ToBool</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">ToBool</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Coerces the value on top of the stack into a Boolean</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.ToNumber</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">ToNumber</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Coerces the value on top of the stack into a Number</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.ToString</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">ToString</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Coerces the value on top of the stack into a String</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.TypeOf</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Type?</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Returns the type of the value on top of the stack (popping it in the process)</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Undefined</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Undefined</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Returns the undefined value</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Unless</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Unless</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Unless the return argument is truthy invoke the second argument otherwise execute the third</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Until</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">Until</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Until the return value of the first argument is truthy, invoke the second argument</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.Version</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black"> </span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Returns the Current Version of Lytescript you are using (as a String)</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="167" style="width:125.0pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;
  color:white">Lyte.Core.While</span>**

</td>

<td width="84" style="width:62.9pt;border-top:none;border-left:none;border-bottom:
  solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;background:#DBE5F1;
  padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;font-family:&quot;Courier New&quot;;color:black">While</span>

</td>

<td width="380" style="width:285.35pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">While the return value of the first argument is truthy, invoke the second argument</span>

</td>

</tr>

</tbody>

</table>

## The Math Package

Note: The Math package is pre-imported into the global scope eliminating the need for an **<span style="font-size:10.0pt;line-height:115%;
font-family:&quot;Courier New&quot;;color:#365F91;background:#F2F2F2">Import</span>**<span style="font-size:10.0pt;line-height:115%;font-family:&quot;Courier New&quot;;background:
#F2F2F2">(<span style="color:#943634">"Lyte.Math"</span>)</span> <span style="font-size:10.0pt;line-height:115%;font-family:&quot;Courier New&quot;"></span> call.

## The Testing Package

<table class="MsoNormalTable" border="0" cellspacing="0" cellpadding="0" width="631" style="width:473.25pt;margin-left:4.65pt;border-collapse:collapse">

<tbody>

<tr style="height:20.25pt">

<td width="262" style="width:196.85pt;border-top:solid #548DD4 1.5pt;
  border-left:none;border-bottom:solid #548DD4 1.5pt;border-right:none;
  background:#366092;padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Full Path</span>**

</td>

<td width="87" style="width:65.1pt;border-top:solid #548DD4 1.5pt;border-left:
  none;border-bottom:solid #548DD4 1.5pt;border-right:none;background:#366092;
  padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Alias</span>**

</td>

<td width="282" style="width:211.3pt;border-top:solid #548DD4 1.5pt;border-left:
  none;border-bottom:solid #548DD4 1.5pt;border-right:none;background:#366092;
  padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Brief</span>**

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertDefined</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts that a value is defined</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertEquals</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts that two values are equal</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertEqualsStrict</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts that two values are strictly equal</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertFalse</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts that a value is falsey</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertNotEquals</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts two values are not equal</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertNotEqualsStrict</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts two values are not strictly equal</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertNotNull</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts that a value is not null</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertDifferent</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts that two values are references to different objects</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertNull</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts that a value is null</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertRaises</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts that a block raises an error</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertSame</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts that two values are references to the same object</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertTrue</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts that a value is truthy</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.AssertUndefined</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Asserts that a value is undefined</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.Fail</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Causes a test to fail</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Test.Test</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Runs a set of tests, See <u>Unit Testing</u> for more details.</span>

</td>

</tr>

</tbody>

</table>

## The System Package

<table class="MsoNormalTable" border="0" cellspacing="0" cellpadding="0" width="631" style="width:473.25pt;margin-left:4.65pt;border-collapse:collapse">

<tbody>

<tr style="height:20.25pt">

<td width="262" style="width:196.85pt;border-top:solid #548DD4 1.5pt;
  border-left:none;border-bottom:solid #548DD4 1.5pt;border-right:none;
  background:#366092;padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Full Path</span>**

</td>

<td width="87" style="width:65.1pt;border-top:solid #548DD4 1.5pt;border-left:
  none;border-bottom:solid #548DD4 1.5pt;border-right:none;background:#366092;
  padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Alias</span>**

</td>

<td width="282" style="width:211.3pt;border-top:solid #548DD4 1.5pt;border-left:
  none;border-bottom:solid #548DD4 1.5pt;border-right:none;background:#366092;
  padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Brief</span>**

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.System.Beep</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Beeps with a given frequency for a given duration (ms)</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.System.CurrentDirectory</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Gets the current system directory</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.System.Execute</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Execute a shell command</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.System.Exit</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Exit the VM completely (with an optional integer argument for the return code)</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.System.PathSeperator</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">A platform-specific separator character used to separate path strings in environment variables.</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.System.Platform</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black"> Gets the current platform identifier and version number.</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="262" style="width:196.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.System.Seperator</span>**

</td>

<td width="87" valign="top" style="width:65.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="282" style="width:211.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black"> Provides a platform-specific character used to separate directory levels in a path string that reflects a hierarchical file system organization.</span>

</td>

</tr>

</tbody>

</table>

## The Utilities Package

<table class="MsoNormalTable" border="0" cellspacing="0" cellpadding="0" width="631" style="width:473.25pt;margin-left:4.65pt;border-collapse:collapse">

<tbody>

<tr style="height:20.25pt">

<td width="206" style="width:154.85pt;border-top:solid #548DD4 1.5pt;
  border-left:none;border-bottom:solid #548DD4 1.5pt;border-right:none;
  background:#366092;padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Full Path</span>**

</td>

<td width="102" style="width:76.85pt;border-top:solid #548DD4 1.5pt;border-left:
  none;border-bottom:solid #548DD4 1.5pt;border-right:none;background:#366092;
  padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Alias</span>**

</td>

<td width="322" style="width:241.55pt;border-top:solid #548DD4 1.5pt;
  border-left:none;border-bottom:solid #548DD4 1.5pt;border-right:none;
  background:#366092;padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Brief</span>**

</td>

</tr>

<tr style="height:15.0pt">

<td width="206" style="width:154.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Util.CharToInt</span>**

</td>

<td width="322" style="width:241.55pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Converts a Character to its numerical, Unicode representation.</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="206" style="width:154.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Util.Concatenate</span>**

</td>

<td width="102" valign="top" style="width:76.85pt;border-top:none;border-left:
  none;border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">++</span>

</td>

<td width="322" style="width:241.55pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Concatenates two lists resulting in a third, new value</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="206" style="width:154.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Util.EscapeString</span>**

</td>

<td width="322" style="width:241.55pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Escapes any special characters within a string back to their “safe” representation.</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="206" style="width:154.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Util.Instantiate</span>**

</td>

<td width="102" valign="top" style="width:76.85pt;border-top:none;border-left:
  none;border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">Instantiate</span>

</td>

<td width="322" style="width:241.55pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black"> Instantiates an object template, calling its __constructor. For more details see <u>Template & Mixins</u>.</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="206" style="width:154.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Util.IntToChar</span>**

</td>

<td width="322" style="width:241.55pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Converts a numerical, Unicode value to its Character representation.</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="206" style="width:154.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Util.IsMixedWith</span>**

</td>

<td width="102" valign="top" style="width:76.85pt;border-top:none;border-left:
  none;border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">MixedWith?</span>

</td>

<td width="322" style="width:241.55pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Checks to see if an object is mixed with a mixin.  For more details see <u>Template & Mixins</u>.</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="206" style="width:154.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Util.MakeList</span>**

</td>

<td width="102" valign="top" style="width:76.85pt;border-top:none;border-left:
  none;border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">MakeList</span>

</td>

<td width="322" style="width:241.55pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Makes a list from _all_ of the values presently on the stack.</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="206" style="width:154.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Util.MixWith</span>**

</td>

<td width="102" valign="top" style="width:76.85pt;border-top:none;border-left:
  none;border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">MixWith</span>

</td>

<td width="322" style="width:241.55pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Mixes an object with a mixin. For more details see <u>Template & Mixins</u>.</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="206" style="width:154.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Util.UnescapeString</span>**

</td>

<td width="322" style="width:241.55pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black"> Unescapes any special characters within a string.</span>

</td>

</tr>

</tbody>

</table>

<span style="font-size:11.0pt;line-height:115%;font-family:&quot;Calibri&quot;,sans-serif">  
</span>

**<span style="font-size:13.0pt;line-height:115%;
font-family:&quot;Cambria&quot;,serif;color:#4F81BD"> </span>**

## The Error Package

<table class="MsoNormalTable" border="0" cellspacing="0" cellpadding="0" width="631" style="width:473.25pt;margin-left:4.65pt;border-collapse:collapse">

<tbody>

<tr style="height:20.25pt">

<td width="206" style="width:154.85pt;border-top:solid #548DD4 1.5pt;
  border-left:none;border-bottom:solid #548DD4 1.5pt;border-right:none;
  background:#366092;padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Full Path</span>**

</td>

<td width="94" style="width:70.85pt;border-top:solid #548DD4 1.5pt;border-left:
  none;border-bottom:solid #548DD4 1.5pt;border-right:none;background:#366092;
  padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Alias</span>**

</td>

<td width="330" style="width:247.55pt;border-top:solid #548DD4 1.5pt;
  border-left:none;border-bottom:solid #548DD4 1.5pt;border-right:none;
  background:#366092;padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Brief</span>**

</td>

</tr>

<tr style="height:15.75pt">

<td width="206" style="width:154.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Error.Raise</span>**

</td>

<td width="94" valign="top" style="width:70.85pt;border-top:none;border-left:
  none;border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">Raise</span>

</td>

<td width="330" style="width:247.55pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Raises a value as an error</span>

</td>

</tr>

<tr style="height:15.0pt">

<td width="206" style="width:154.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Error.Try</span>**

</td>

<td width="94" valign="top" style="width:70.85pt;border-top:none;border-left:
  none;border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">Try</span>

</td>

<td width="330" style="width:247.55pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.0pt">

<span style="color:black">Tries to invoke the first block on the stack but if an error is raised by it invokes the second block, pushing the raised error object onto the stack.</span>

</td>

</tr>

</tbody>

</table>

## The Reflection Package

<table class="MsoNormalTable" border="0" cellspacing="0" cellpadding="0" width="631" style="width:473.25pt;margin-left:4.65pt;border-collapse:collapse">

<tbody>

<tr style="height:20.25pt">

<td width="222" style="width:166.85pt;border-top:solid #548DD4 1.5pt;
  border-left:none;border-bottom:solid #548DD4 1.5pt;border-right:none;
  background:#366092;padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Full Path</span>**

</td>

<td width="93" style="width:70.1pt;border-top:solid #548DD4 1.5pt;border-left:
  none;border-bottom:solid #548DD4 1.5pt;border-right:none;background:#366092;
  padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Alias</span>**

</td>

<td width="315" style="width:236.3pt;border-top:solid #548DD4 1.5pt;border-left:
  none;border-bottom:solid #548DD4 1.5pt;border-right:none;background:#366092;
  padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Brief</span>**

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<s><span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Reflect.Clone</span></s>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

_<span style="color:black">Removed from Revision 1 of the Specification</span>_

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<s><span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Reflect.DeepClone</span></s>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.5pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

_<span style="color:black">Removed from Revision 1 of the Specification</span>_

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Reflect.Finalize</span>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.5pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">Finalize</span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Finalizes a variable (so that it cannot be changed)</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Reflect.Get</span>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Resolves the raw value of a variable (without applying it)</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.Reflect.GetProperties</span>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;"> </span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Gets a list of the properties of a value</span>

</td>

</tr>

</tbody>

</table>

## The Input/output Package

<table class="MsoNormalTable" border="0" cellspacing="0" cellpadding="0" width="631" style="width:473.25pt;margin-left:4.65pt;border-collapse:collapse">

<tbody>

<tr style="height:20.25pt">

<td width="222" style="width:166.85pt;border-top:solid #548DD4 1.5pt;
  border-left:none;border-bottom:solid #548DD4 1.5pt;border-right:none;
  background:#366092;padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Full Path</span>**

</td>

<td width="93" style="width:70.1pt;border-top:solid #548DD4 1.5pt;border-left:
  none;border-bottom:solid #548DD4 1.5pt;border-right:none;background:#366092;
  padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Alias</span>**

</td>

<td width="315" style="width:236.3pt;border-top:solid #548DD4 1.5pt;border-left:
  none;border-bottom:solid #548DD4 1.5pt;border-right:none;background:#366092;
  padding:0in 5.4pt 0in 5.4pt;height:20.25pt">

**<span style="font-size:14.0pt;color:white">Brief</span>**

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.IO.Echo</span>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.5pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">Echo</span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Writes a string to StdOut or another stream (if given)</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.IO.EchoLn</span>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.5pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">EchoLn</span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Writes a string to StdOut or another stream (if given) with an endline character</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.IO.OpenFile</span>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.5pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">OpenFile</span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Opens a file (whose path is given by the first argument) in the configuration given by the second argument. See Inputs & Outputs.</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.IO.Read</span>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.5pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">Read</span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Reads a string from StdIn</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.IO.ReadLn</span>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.5pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">ReadLn</span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Reads a line from StdIn</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.IO.StdIn</span>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.5pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">StdIn</span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Returns the Standard Input Stream</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.IO.StdErr</span>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.5pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">StdErr</span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Returns the Standard Error Stream</span>

</td>

</tr>

<tr style="height:15.75pt">

<td width="222" style="width:166.85pt;border:none;border-right:solid #548DD4 1.0pt;
  background:#548DD4;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

**<span style="font-size:10.0pt;line-height:115%;
  font-family:&quot;Courier New&quot;;color:white">Lyte.IO.StdOut</span>**

</td>

<td width="93" valign="top" style="width:70.1pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="font-size:10.0pt;line-height:115%;font-family:
  &quot;Courier New&quot;">StdOut</span>

</td>

<td width="315" style="width:236.3pt;border-top:none;border-left:none;
  border-bottom:solid #548DD4 1.0pt;border-right:solid #548DD4 1.0pt;
  background:#DBE5F1;padding:0in 5.4pt 0in 5.4pt;height:15.75pt">

<span style="color:black">Returns the Standard Output Stream</span>

</td>

</tr>

</tbody>

</table>

# Working with Objects

## A Word of Caution

In Lytescript, the underlying type for all keys is <span style="font-size:10.0pt;line-height:115%;font-family:&quot;Courier New&quot;">string</span> <span style="font-size:10.0pt;line-height:115%"></span> which means one should exercise caution when using numeric strings with trailing zeros as a key (such as in the case of <span style="font-size:10.0pt;line-height:115%;font-family:
&quot;Courier New&quot;;background:#F2F2F2">obj[<span style="color:#943634">“1.0”</span>]</span>). This is because if one coerces a number into a string it would not have the trailing zeros and consequently would not be a valid key for the object.

## Computed Property Names

![%{
   [Statements]: Value
 }
](Lytescript_files/image012.png)

Lytescript allows for computed property names at the time of an object’s initialization by simply wrapping the statements in brackets.

## Getters/Setters

![%{
   <- Getter: Block,
   -> Setter: Block
 }
](Lytescript_files/image013.png)

At the time of an object’s initialization, one can override the default getting/setting behavior of a property using a getter/setter pair. Instead of getting/setting the objects properties directly, Lyte will invoke the block. In the case of a Getter it expects the block to push a value onto the stack whereas in the case of a Setter it expects the block to pop the new value off of the stack.

# Type Coercion

# Templates & Mixins

# Importing Classes & Packages

# Unit Testing

</div>
