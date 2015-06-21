
Chapter 26. Fractal Land

From:
  Killer Game Programming in Java
  Andrew Davison
  O'Reilly, May 2005
  ISBN: 0-596-00730-2
  http://www.oreilly.com/catalog/killergame/
  Web Site for the book: http://fivedots.coe.psu.ac.th/~ad/jg

Contact Address:
  Dr. Andrew Davison
  Dept. of Computer Engineering
  Prince of Songkla University
  Hat Yai, Songkhla 90112, Thailand
  E-mail: ad@fivedots.coe.psu.ac.th


If you use this code, please mention my name, and include a link
to the book's Web site.

Thanks,
  Andrew


============================
FractalLand3D.java

-----
Compilation:

$ javac *.java
    // you must have Java 3D installed for the compilation to succeed;
    // if you get "Warning" messages, please see the note below


Java 3D is available from http://java.sun.com/products/java-media/3D/

-----
Execution: 

The application takes an optional 'flatness' value 
argument, that is limited to be between 1.6 and 2.5 (the
default is 2.3). A smaller value makes the landscape more rocky.

e.g.

$ java Fractaland3D
     - means use a flatness value of 2.3 which is fairly flat

$ java FractalLand3D 1.6
     - makes a rocky landscape
 

(Note. The fog effect has been compiled into this code. To
remove it, then comment out the call to addFog() in 
createSceneGraph() in WrapFractalLand3D.java.)

=========

There is a subdirectory PlaneEquation/ which contains a
simple application for calculating y- positions on quads.
See the readme.txt file there for more detail.

-----------
Note on "unchecked or unsafe operation" Warnings

As explained in chapter 3, I have not used J2SE 5.0's type-safe 
collections, so that this code will compile in early versions of
J2SE (e.g. version 1.4).

The warning messages are always related to my use of collections
(e.g. ArrayList) without specifying a type for the objects they will
contain at run time.

No. of Warnings generated in J2SE 5.0 for the examples:
/FractalLand3D: 5 warnings

---------
Last updated: 19th April 2005