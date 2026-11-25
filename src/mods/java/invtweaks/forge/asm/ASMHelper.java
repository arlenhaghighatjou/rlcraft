package invtweaks.forge.asm;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ASMHelper {
   public ASMHelper() {
   }

   public static void generateBooleanMethodConst(@NotNull ClassNode clazz, String name, boolean retval) {
      MethodNode method = new MethodNode(262144, 4097, name, "()Z", (String)null, (String[])null);
      InsnList code = method.instructions;
      code.add(new InsnNode(retval ? 4 : 3));
      code.add(new InsnNode(172));
      clazz.methods.add(method);
   }

   public static void generateIntegerMethodConst(@NotNull ClassNode clazz, String name, short retval) {
      MethodNode method = new MethodNode(262144, 4097, name, "()I", (String)null, (String[])null);
      InsnList code = method.instructions;
      if (retval >= -128 && retval <= 127) {
         code.add(new IntInsnNode(16, retval));
      } else {
         code.add(new IntInsnNode(17, retval));
      }

      code.add(new InsnNode(172));
      clazz.methods.add(method);
   }

   public static void generateSelfForwardingMethod(@NotNull ClassNode clazz, String name, String forwardname, @NotNull Type rettype) {
      MethodNode method = new MethodNode(262144, 4097, name, "()" + rettype.getDescriptor(), (String)null, (String[])null);
      populateSelfForwardingMethod(method, forwardname, rettype, Type.getObjectType(clazz.name));
      clazz.methods.add(method);
   }

   public static void generateForwardingToStaticMethod(@NotNull ClassNode clazz, String name, String forwardname, @NotNull Type rettype, @NotNull Type fowardtype) {
      MethodNode method = new MethodNode(262144, 4097, name, "()" + rettype.getDescriptor(), (String)null, (String[])null);
      populateForwardingToStaticMethod(method, forwardname, rettype, Type.getObjectType(clazz.name), fowardtype);
      clazz.methods.add(method);
   }

   public static void generateForwardingToStaticMethod(@NotNull ClassNode clazz, String name, String forwardname, @NotNull Type rettype, @NotNull Type fowardtype, @NotNull Type thistype) {
      MethodNode method = new MethodNode(262144, 4097, name, "()" + rettype.getDescriptor(), (String)null, (String[])null);
      populateForwardingToStaticMethod(method, forwardname, rettype, thistype, fowardtype);
      clazz.methods.add(method);
   }

   public static void replaceSelfForwardingMethod(@NotNull MethodNode method, String forwardname, @NotNull Type thistype) {
      Type methodType = Type.getMethodType(method.desc);
      method.instructions.clear();
      populateSelfForwardingMethod(method, forwardname, methodType.getReturnType(), thistype);
   }

   public static void populateForwardingToStaticMethod(@NotNull MethodNode method, String forwardname, @NotNull Type rettype, @NotNull Type thistype, @NotNull Type forwardtype) {
      InsnList code = method.instructions;
      code.add(new VarInsnNode(thistype.getOpcode(21), 0));
      code.add(new MethodInsnNode(184, forwardtype.getInternalName(), forwardname, Type.getMethodDescriptor(rettype, new Type[]{thistype}), false));
      code.add(new InsnNode(rettype.getOpcode(172)));
   }

   public static void populateSelfForwardingMethod(@NotNull MethodNode method, String forwardname, @NotNull Type rettype, @NotNull Type thistype) {
      InsnList code = method.instructions;
      code.add(new VarInsnNode(thistype.getOpcode(21), 0));
      code.add(new MethodInsnNode(182, thistype.getInternalName(), forwardname, "()" + rettype.getDescriptor(), false));
      code.add(new InsnNode(rettype.getOpcode(172)));
   }
}
