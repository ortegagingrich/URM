# URM
This repository contains some code that I made several years ago as an interesting side project in a class on Computability and
Complexity which I took during the Fall of 2013 and recently decided to clean up a bit (and fix some bugs).  It contains a basic
"compiler" which converts code written in a (very primitive) scripting language of my own invention (which I call U++) to URM
machine code.

An Unlimited Register Machine (URM), as described in Nigel J. Cutland's book "Computability: An introduction to recursive
function theory" is an idealized "computer" consisting of a countably infinite number of "registers", each of which can store
one non-negative integer value, regardless of size (hence, the word "unlimited").  The registers are numbered starting with one
and as many can be used as are needed.  At the start of a program, all but some finite number of consecutive registers (which
contain input parameters) are initialized as zero.  When (and if) a program stops, the integer contained in the first register
is said to be the "returned" value.

URM programs consist of commands on individual lines (which are typically numbered), each with a single command.  There are four
commands which a URM may accept:

1) Z(n)    : Sets the contents of the nth register to zero.
2) S(n)    : Increments the contents of the nth register by one.
3) T(m,n)  : Copies the contents of the mth register to the nth one.
4) J(m,n,i): Checks if the contents of registers m and n are equal.  If they are, it jumps to the ith instruction.  If i==0, the
             program ends.

Instructions are carried out sequentially, unless, of course, a jump command is encountered.  The program ends either when the last line of the program is reached, or when a successful jump command to line zero is executed.  It is possible to prove that a URM with the commands Z, S and J is computationally equivalent to a Turing Machine.
