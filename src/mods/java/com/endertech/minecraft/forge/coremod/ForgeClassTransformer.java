package com.endertech.minecraft.forge.coremod;

import com.endertech.common.Args;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.coremod.descriptors.MethodDescriptor;
import com.endertech.minecraft.forge.coremod.names.ClassName;
import com.endertech.minecraft.forge.coremod.signatures.MethodSignature;
import java.util.Iterator;
import net.minecraft.launchwrapper.IClassTransformer;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class ForgeClassTransformer implements IClassTransformer {
   public static final String HANDLE_METHOD_NAME = "handle";

   public ForgeClassTransformer() {
   }

   public static String instructToString(MethodInsnNode node) {
      return node.getClass().getSimpleName() + Args.group(Args.get("opcode", Integer.valueOf(node.getOpcode())), Args.get("owner", node.owner), Args.get("name", node.name), Args.get("descriptor", node.desc));
   }

   public static String methodToString(MethodNode method) {
      return method.name + method.desc;
   }

   protected void logInfo(String msg, Object... params) {
      String loggerName = this.getClass().getSimpleName();
      LogManager.getLogger(loggerName).debug(msg, params);
   }

   public ClassNode getClassNode(byte[] basicClass) {
      ClassReader reader = new ClassReader(basicClass);
      ClassNode node = new ClassNode();
      reader.accept(node, 0);
      return node;
   }

   public byte[] getByteCode(ClassNode classNode) {
      int flags = 1;
      if (!this.isCompatibilityMode()) {
         flags |= 2;
      }

      ClassWriter writer = new ClassWriter(flags);
      classNode.accept(writer);
      return writer.toByteArray();
   }

   public boolean isCompatibilityMode() {
      return false;
   }

   protected MethodSignature getDefaultHandlerWith(MethodDescriptor descriptor) {
      return MethodSignature.of(this.getClass().getName(), "handle", descriptor);
   }

   protected abstract MethodSignature getTargetMethod();

   protected abstract MethodSignature getHandler();

   protected abstract boolean isProperInstruction(MethodInsnNode var1);

   protected abstract boolean shouldSearchNextInstruction(MethodInsnNode var1);

   protected abstract void injectInstructions(InstructList var1);

   protected byte[] transformClass(byte[] classByteCode) {
      this.logInfo("Transforming class: {}", this.getTargetMethod().className);
      ClassNode clazz = this.getClassNode(classByteCode);
      boolean result = false;
      this.logInfo("\tsearching method: {}", this.getTargetMethod().name);

      for(MethodNode method : clazz.methods) {
         if (this.getTargetMethod().complyWith(method)) {
            this.logInfo("\ttransforming method: {}", methodToString(method));
            result = this.transformMethod(method);
            break;
         }

         if (ForgeMain.isDebugMode()) {
            this.logInfo("\t--> method: {}", methodToString(method));
         }
      }

      this.logInfo("Transformation result: {}", result);
      return result ? this.getByteCode(clazz) : classByteCode;
   }

   protected boolean transformMethod(MethodNode method) {
      Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
      boolean result = false;

      while(iterator.hasNext()) {
         AbstractInsnNode node = (AbstractInsnNode)iterator.next();
         if (node instanceof MethodInsnNode) {
            MethodInsnNode instruct = (MethodInsnNode)node;
            if (this.isProperInstruction(instruct)) {
               this.logInfo("\t\tfound instruction: {}", instructToString(instruct));
               InstructList instructions = new InstructList(method, instruct);
               this.injectInstructions(instructions);
               result = true;
               if (!this.shouldSearchNextInstruction(instruct)) {
                  break;
               }
            } else if (ForgeMain.isDebugMode()) {
               this.logInfo("\t\t--> {}", instructToString(instruct));
            }
         }
      }

      return result;
   }

   public byte[] transform(String obfClassName, String normalClassName, byte[] classByteCode) {
      ClassName targetClassName = this.getTargetMethod().className;
      return targetClassName.java.equals(normalClassName) ? this.transformClass(classByteCode) : classByteCode;
   }
}
