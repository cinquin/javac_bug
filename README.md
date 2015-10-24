The aim of this code is to reproduce a bug in `javac`, the Java compiler included in the
Java Development Kit. This bug does not appear to lead to incorrect bytecode execution,
but it causes the [ASM bytecode engineering library](http://asm.ow2.org) to fail. A number
of tools, such as Findbugs and JaCoCo rely on ASM for bytecode analysis and thus also fail
on affected bytecode.

Stack traces of exceptions caused by the bug include the following:

``
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: 42
	at org.objectweb.asm.ClassReader.readLabel(ClassReader.java:2174)
	at org.objectweb.asm.ClassReader.readTypeAnnotations(ClassReader.java:1598)
	at org.objectweb.asm.ClassReader.readCode(ClassReader.java:1184)
	at org.objectweb.asm.ClassReader.readMethod(ClassReader.java:1017)
	at org.objectweb.asm.ClassReader.accept(ClassReader.java:693)
	at org.objectweb.asm.ClassReader.accept(ClassReader.java:506)
``

The bug results in an incorrect value of the `length` field in the `localvar_target`
item (page 147 of the [Java Virtual Machine Specification, Java SE 8 edition](https://docs.oracle.com/javase/specs/jvms/se8/jvms8.pdf)
). The value of the `length` field ends up exceeding the length of the method bytecode,
which trips up ASM because the `labels` array is pre-initialized based on that bytecode
length. It appears that a combination of a type annotation and lambda code might be
required to trigger the `javac` bug.

The Eclipse Java Compiler is unaffected.

To reproduce the bug, run `./check_bug` from the repository root.