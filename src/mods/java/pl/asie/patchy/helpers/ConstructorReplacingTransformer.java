package pl.asie.patchy.helpers;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import pl.asie.patchy.TransformerFunction;

public class ConstructorReplacingTransformer implements TransformerFunction<ClassVisitor> {
   private final String from;
   private final String to;
   private final Set<String> methods;

   public ConstructorReplacingTransformer(String from, String to, String... methods) {
      this.from = from.replace('.', '/');
      this.to = to.replace('.', '/');
      this.methods = ImmutableSet.copyOf(methods);
   }

   public ClassVisitor apply(ClassVisitor visitor) {
      return new ConstructorReplacingTransformer.FFClassVisitor(327680, visitor);
   }

   private class FFClassVisitor extends ClassVisitor {
      public FFClassVisitor(int api, ClassVisitor next) {
         super(api, next);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         return (MethodVisitor)(!ConstructorReplacingTransformer.this.methods.isEmpty() && !ConstructorReplacingTransformer.this.methods.contains(name) ? this.cv.visitMethod(access, name, desc, signature, exceptions) : ConstructorReplacingTransformer.this.new FFMethodVisitor(this.api, this.cv.visitMethod(access, name, desc, signature, exceptions)));
      }
   }

   private class FFMethodVisitor extends MethodVisitor {
      public FFMethodVisitor(int api, MethodVisitor next) {
         super(api, next);
      }

      public void visitTypeInsn(int opcode, String type) {
         if (opcode == 187 && ConstructorReplacingTransformer.this.from.equals(type)) {
            System.out.println("Replaced NEW for " + ConstructorReplacingTransformer.this.from + " to " + ConstructorReplacingTransformer.this.to);
            super.visitTypeInsn(opcode, ConstructorReplacingTransformer.this.to);
         } else {
            super.visitTypeInsn(opcode, type);
         }

      }

      public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
         if (opcode == 183 && "<init>".equals(name) && ConstructorReplacingTransformer.this.from.equals(owner)) {
            System.out.println("Replaced INVOKESPECIAL for " + ConstructorReplacingTransformer.this.from + " to " + ConstructorReplacingTransformer.this.to);
            super.visitMethodInsn(opcode, ConstructorReplacingTransformer.this.to, name, desc, itf);
         } else {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
         }

      }
   }
}
