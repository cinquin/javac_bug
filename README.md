The aim of this code is to reproduce a bug in `javac`, the Java compiler included in the
Java Development Kit. This bug does not appear to lead to incorrect bytecode execution,
but it causes the [ASM bytecode engineering library](http://asm.ow2.org) to fail. A number
of tools, such as Findbugs and JaCoCo, rely on ASM for bytecode analysis and thus also
fail on affected bytecode.

This was investigated by Oracle as [OpenJDK bug 8144185](https://bugs.openjdk.java.net/browse/JDK-8144185),
and is fixed in JDK 8u172.

Stack traces of ASM exceptions caused by the bug include the following (ASM v5.0.2):

```
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: 42
	at org.objectweb.asm.ClassReader.readLabel(ClassReader.java:2174)
	at org.objectweb.asm.ClassReader.readTypeAnnotations(ClassReader.java:1598)
	at org.objectweb.asm.ClassReader.readCode(ClassReader.java:1184)
	at org.objectweb.asm.ClassReader.readMethod(ClassReader.java:1017)
	at org.objectweb.asm.ClassReader.accept(ClassReader.java:693)
	at org.objectweb.asm.ClassReader.accept(ClassReader.java:506)
```

The bug results in an incorrect value of the `length` field in the `localvar_target`
item (page 147 of the [Java Virtual Machine Specification, Java SE 8 edition](https://docs.oracle.com/javase/specs/jvms/se8/jvms8.pdf)).
The value of the `length` field ends up exceeding the length of the method bytecode,
which trips up ASM because the `labels` array is pre-initialized based on that bytecode
length. It appears that a combination of a type annotation and lambda code might be
required to trigger the `javac` bug.

The Eclipse Java Compiler is unaffected.

To reproduce the bug, run `./check_bug` from the repository root.

Relevant excerpt of `javap -v bin.ReproduceJavacBug`, after compilation with `javac`
1.8.0\_65:

<pre>
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=1
         0: new           #2                  // class java/util/ArrayList
         3: dup
         4: invokespecial #3                  // Method java/util/ArrayList."<init>":()V
         7: astore_1
         8: aload_1
         9: invokeinterface #4,  1            // InterfaceMethod java/util/Collection.stream:()Ljava/util/stream/Stream;
        14: aload_1
        15: invokedynamic #5,  0              // InvokeDynamic #0:accept:(Ljava/util/Collection;)Ljava/util/function/Consumer;
        20: invokeinterface #6,  2            // InterfaceMethod java/util/stream/Stream.forEach:(Ljava/util/function/Consumer;)V
        <b>25: return</b>
      LineNumberTable:
        line 29: 0
        line 30: 8
        line 36: 25
      RuntimeInvisibleTypeAnnotations:
        0: #30(): LOCAL_VARIABLE, {start_pc=0, <b>length=42</b>, index=0}, location=[TYPE_ARGUMENT(0)]
</pre>

`length=42` extends beyond the return statement at offset 25.
