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

Instructions are carried out sequentially, unless, of course, a jump command is encountered.  The program ends either when the last 
line of the program is reached, or when a successful jump command to line zero is executed.  It is possible to prove that a URM with 
the commands Z, S and J is computationally equivalent to a Turing Machine.

The following small example code computes the product of two input integers initially placed in registers 1 and 2.

```
1:J(1,3,9)
2:Z(4)
3:J(2,4,7)
4:S(5)
5:S(4)
6:J(1,1,3)
7:S(3)
8:J(1,1,1)
9:T(5,1)
```

Of course, it is impratical to write programs much larger than this, so typically proofs of the computability of certain functions (i.e. that 
there exists a URM program that can compute it) is done by combining functions already known to be computable through substitution,
recursion and minimalization.  (These are computationally equivalent, respectively, to function calls, for loops and while loops, all of 
which can be implemented systematically using only the four above commands.)  However, after seeing a proof of the existance of a 
special program called a Universal URM program, which can compute any of the countably infinite set of possible URM programs,
I became a curious as to how long such a program would be.

The U++ "compiler" contained in this repository is the result of this curiosity.  As stated, it takes code written in my horrible scripting
language and turns it into URM Code.  It can be used to produce URM code easily for much more complicated functions.  For example,
the U++ script

```
function add(var1,var2)
	int xv,zero
	xv=var1
	zero=0
	for i,zero,var2
		xv++
	end
	return xv
end


function mult(var1,var2)
	int xv,zero
	xv=0
	zero=0
	for i,zero,var2
		xv=add(xv,var1)
	end
	return xv
end


function divides?(var1,var2)
	int xv,zero,prod
	zero=0
	xv=0
	for i,zero,var2
		prod=mult(i,var1)
		if(prod==var2)
			xv++
		else
		end
	end
	return xv
end

function prime?(var1)
	int two,xv,check,one
	one=1
	two=2
	xv=1
	if(var1<two)
		xv=0
	else
		for i,two,var1
			check=divides?(i,var1)
			if(check==one)
				xv=0
			else
			end
		end
	end
	return xv
end

int in,out
par in
out=prime?(in)
return out
```

which determines if the given input is prime, compiles to generate the following URM code:

```
1:T(1,200001)
2:T(200001,200003)
3:Z(200007)
4:S(200007)
5:Z(200004)
6:S(200004)
7:S(200004)
8:Z(200005)
9:S(200005)
10:T(200003,100)
11:T(200004,101)
12:J(101,200003,19)
13:S(100)
14:S(101)
15:J(100,200004,17)
16:J(1,1,12)
17:Z(200005)
18:J(1,1,59)
19:T(200004,15000)
20:J(15000,200003,59)
21:T(15000,200008)
22:T(200003,200009)
23:Z(200011)
24:Z(200010)
25:T(200011,15100)
26:J(15100,200009,52)
27:T(15100,200013)
28:T(200008,200014)
29:Z(200015)
30:Z(200016)
31:T(200016,15200)
32:J(15200,200014,45)
33:T(200015,200017)
34:T(200013,200018)
35:T(200017,200019)
36:Z(200020)
37:T(200020,15300)
38:J(15300,200018,42)
39:S(200019)
40:S(15300)
41:J(1,1,38)
42:T(200019,200015)
43:S(15200)
44:J(1,1,32)
45:T(200015,200012)
46:J(200012,200009,48)
47:J(1,1,50)
48:S(200010)
49:J(1,1,50)
50:S(15100)
51:J(1,1,26)
52:T(200010,200006)
53:J(200006,200007,55)
54:J(1,1,57)
55:Z(200005)
56:J(1,1,57)
57:S(15000)
58:J(1,1,20)
59:T(200005,200002)
60:T(200002,1)
61:J(1,1,0)
```
