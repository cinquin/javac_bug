import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.util.TraceFieldVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Textifier;

//Adapted from http://web.cs.ucla.edu/~msb/cs239-tutorial/
public class VisitBytecode {

	static class MyClassVisitor extends ClassVisitor {

		class MyTraceMethodVisitor extends MethodVisitor {
			public MyTraceMethodVisitor(MethodVisitor mv) {
				super(Opcodes.ASM5, mv);
			}

			@Override
			public void visitMaxs(int maxStack, int maxLocals) {
			}
		}

		public MyClassVisitor(ClassVisitor cv) {
			super(Opcodes.ASM5, cv);
		}

		@Override
		public FieldVisitor visitField(int access, String name, String desc,
				String signature, Object value) {
			return new TraceFieldVisitor(new FieldNode(access, name, desc, signature, value), new Textifier());
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {

			return new MyTraceMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));

		}
	}

	public static void main(final String args[]) throws Exception {
		FileInputStream is = new FileInputStream(args[0]);

		ClassReader cr = new ClassReader(is);
		PrintWriter printWriter = new PrintWriter(System.out);
		TraceClassVisitor traceClassVisitor = new TraceClassVisitor(printWriter);
		MyClassVisitor myClassVisitor = new MyClassVisitor(traceClassVisitor);
		cr.accept(myClassVisitor, 0);

		ClassWriter cw = new ClassWriter(0);
		cr.accept(cw, 0);

		FileOutputStream fos = new FileOutputStream(args[1]);
		fos.write(cw.toByteArray());
		fos.close();
	}
}
