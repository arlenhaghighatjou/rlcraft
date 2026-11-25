package pl.asie.foamfix.coremod.patches;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import pl.asie.patchy.TransformerFunction;

public class GhostBusterBlockStateAccessPatch implements TransformerFunction<ClassVisitor> {
   public final Set<String> methods;

   public GhostBusterBlockStateAccessPatch(String... methods) {
      this.methods = ImmutableSet.copyOf(methods);
   }

   public ClassVisitor apply(ClassVisitor visitor) {
      return new GhostBusterBlockStateAccessPatch.FFClassVisitor(327680, visitor);
   }

   private class FFClassVisitor extends ClassVisitor {
      private String className;

      public FFClassVisitor(int api, ClassVisitor next) {
         super(api, next);
      }

      public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
         this.className = name;
         super.visit(version, access, name, signature, superName, interfaces);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         return (MethodVisitor)(GhostBusterBlockStateAccessPatch.this.methods.contains(name) ? new GhostBusterBlockStateAccessPatch.FFMethodVisitor(this.api, this.className, name, this.cv.visitMethod(access, name, desc, signature, exceptions)) : this.cv.visitMethod(access, name, desc, signature, exceptions));
      }
   }

   private static class FFMethodVisitor extends MethodVisitor {
      private final String className;
      private final String methodName;

      public FFMethodVisitor(int api, String className, String methodName, MethodVisitor next) {
         super(api, next);
         this.className = className;
         this.methodName = methodName;
      }

      public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
         if (opcode != 182 || !owner.startsWith("net/minecraft/world/") || !"getBlockState".equals(name) && !"getBlockState".equals(name)) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
         } else {
            System.out.println("Added ghost buster patch (getBlockState call wrapped) in " + this.className + " " + this.methodName + " (" + owner + " " + name + " " + desc + ")");
            super.visitMethodInsn(184, "pl/asie/foamfix/ghostbuster/GhostBusterSafeAccessors", "getBlockState", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;", false);
         }

      }
   }
}
