package pl.asie.foamfix.coremod.patches;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import pl.asie.patchy.TransformerFunction;

public class ClassGetSimpleNamePatch implements TransformerFunction<ClassVisitor> {
   public final Set<String> methods;

   public ClassGetSimpleNamePatch(String... methods) {
      this.methods = ImmutableSet.copyOf(methods);
   }

   public ClassVisitor apply(ClassVisitor visitor) {
      return new ClassGetSimpleNamePatch.FFClassVisitor(327680, visitor);
   }

   private class FFClassVisitor extends ClassVisitor {
      public FFClassVisitor(int api, ClassVisitor next) {
         super(api, next);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         return (MethodVisitor)(ClassGetSimpleNamePatch.this.methods.contains(name) ? new ClassGetSimpleNamePatch.FFMethodVisitor(this.api, this.cv.visitMethod(access, name, desc, signature, exceptions)) : this.cv.visitMethod(access, name, desc, signature, exceptions));
      }
   }

   private static class FFMethodVisitor extends MethodVisitor {
      public FFMethodVisitor(int api, MethodVisitor next) {
         super(api, next);
      }

      public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
         if (opcode == 182 && "getSimpleName".equals(name) && "java/lang/Class".equals(owner)) {
            System.out.println("Replaced getSimpleName with getName");
            super.visitMethodInsn(opcode, owner, "getName", desc, itf);
         } else {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
         }

      }
   }
}
