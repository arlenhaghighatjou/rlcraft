package com.endertech.minecraft.forge.coremod;

import com.endertech.minecraft.forge.coremod.names.ClassName;
import com.endertech.minecraft.forge.coremod.signatures.FieldSignature;
import com.endertech.minecraft.forge.coremod.signatures.MethodSignature;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class InstructList {
   public final MethodNode method;
   public final AbstractInsnNode instruct;
   protected InsnList list = new InsnList();
   protected boolean forceNormalNames = false;

   public InstructList(MethodNode method, AbstractInsnNode instruct) {
      this.method = method;
      this.instruct = instruct;
   }

   public static InstructList.VarNode createObjVar(String name, int index) {
      return new InstructList.VarNode(index, name, 25, 58);
   }

   public static InstructList.VarNode createIntVar(String name, int index) {
      return new InstructList.VarNode(index, name, 21, 54);
   }

   public static InstructList.VarNode createLongVar(String name, int index) {
      return new InstructList.VarNode(index, name, 22, 55);
   }

   public static InstructList.VarNode createFloatVar(String name, int index) {
      return new InstructList.VarNode(index, name, 23, 56);
   }

   public static InstructList.VarNode createDoubleVar(String name, int index) {
      return new InstructList.VarNode(index, name, 24, 57);
   }

   public static LabelNode createLabel() {
      return new LabelNode();
   }

   public void logInfo(String msg, Object... params) {
      String loggerName = this.getClass().getSimpleName();
      LogManager.getLogger(loggerName).debug("\t\t\t" + msg, params);
   }

   public void logInstructInfo(String instructName, Object parameter, @Nullable String comment) {
      String msg = instructName + " {}";
      if (comment != null && !comment.isEmpty()) {
         msg = msg + " // " + comment;
      }

      this.logInfo(msg, parameter);
   }

   public InstructList add(AbstractInsnNode instruct) {
      this.list.add(instruct);
      return this;
   }

   public InstructList loadThis() {
      return this.loadObjFrom(0, "this");
   }

   public InstructList loadObjFrom(int varIndex, String comment) {
      this.list.add(new VarInsnNode(25, varIndex));
      this.logInstructInfo("ALOAD", Integer.valueOf(varIndex), comment);
      return this;
   }

   public InstructList saveObjTo(int varIndex, String comment) {
      this.list.add(new VarInsnNode(58, varIndex));
      this.logInstructInfo("ASTORE", Integer.valueOf(varIndex), comment);
      return this;
   }

   public InstructList loadIntFrom(int varIndex, String comment) {
      this.list.add(new VarInsnNode(21, varIndex));
      this.logInstructInfo("ILOAD", Integer.valueOf(varIndex), comment);
      return this;
   }

   public InstructList loadVar(InstructList.VarNode variable) {
      this.list.add(new VarInsnNode(variable.loadOpCode, variable.index));
      this.logInstructInfo("LOAD", Integer.valueOf(variable.index), variable.name);
      return this;
   }

   public InstructList saveIntTo(int varIndex, String comment) {
      this.list.add(new VarInsnNode(54, varIndex));
      this.logInstructInfo("ISTORE", Integer.valueOf(varIndex), comment);
      return this;
   }

   public InstructList storeVar(InstructList.VarNode variable) {
      this.list.add(new VarInsnNode(variable.storeOpCode, variable.index));
      this.logInstructInfo("STORE", Integer.valueOf(variable.index), variable.name);
      return this;
   }

   public InstructList pushInt(int value) {
      if (value >= 0 && value <= 5) {
         int opcode = 3 + value;
         this.list.add(new InsnNode(opcode));
         this.logInfo("ICONST_" + value);
      } else {
         this.list.add(new LdcInsnNode(value));
         this.logInfo("PUSH" + value);
      }

      return this;
   }

   public InstructList loadFromArray(int index) {
      this.pushInt(index);
      this.list.add(new InsnNode(50));
      this.logInfo("AALOAD");
      return this;
   }

   public InstructList invertStackValue() {
      LabelNode labelSet1 = new LabelNode();
      LabelNode labelEnd = new LabelNode();
      this.list.add(new JumpInsnNode(153, labelSet1));
      this.list.add(new InsnNode(3));
      this.list.add(new JumpInsnNode(167, labelEnd));
      this.list.add(labelSet1);
      this.list.add(new InsnNode(4));
      this.list.add(labelEnd);
      this.logInfo("INVERT STACK VALUE");
      return this;
   }

   protected boolean shouldObfuscate() {
      return CorePlugin.obfuscatedCode() && !this.forceNormalNames;
   }

   protected MethodInsnNode methodNode(MethodSignature method, int opcode, boolean isInterface) {
      boolean obfuscated = this.shouldObfuscate();
      return new MethodInsnNode(opcode, method.className.toString(obfuscated), method.name.toString(obfuscated), method.descriptor.toString(obfuscated), isInterface);
   }

   protected MethodInsnNode methodNode(MethodSignature method, int opcode) {
      return this.methodNode(method, opcode, false);
   }

   public InstructList invokeStatic(MethodSignature method) {
      this.list.add(this.methodNode(method, 184));
      this.logInfo("INVOKESTATIC {}", method.getSimpleJavaName());
      return this;
   }

   public InstructList invokeVirtual(MethodSignature method) {
      this.list.add(this.methodNode(method, 182));
      this.logInfo("INVOKEVIRTUAL {}", method.getSimpleJavaName());
      return this;
   }

   public InstructList invokeInterface(MethodSignature method) {
      this.list.add(this.methodNode(method, 185, true));
      this.logInfo("INVOKEINTERFACE {}", method.getSimpleJavaName());
      return this;
   }

   public InstructList invokeSpecial(MethodSignature method) {
      this.list.add(this.methodNode(method, 183));
      this.logInfo("INVOKESPECIAL {}", method.getSimpleJavaName());
      return this;
   }

   public InstructList checkCast(ClassName className) {
      String desc = this.shouldObfuscate() ? className.obfuscated : className.normal;
      this.list.add(new TypeInsnNode(192, desc));
      this.logInfo("CHECKCAST {} ({})", desc, className.normal);
      return this;
   }

   public InstructList dup() {
      this.list.add(new InsnNode(89));
      this.logInfo("DUP");
      return this;
   }

   public InstructList dup2values() {
      this.list.add(new InsnNode(92));
      this.logInfo("DUP2");
      return this;
   }

   public InstructList dupValue2() {
      this.list.add(new InsnNode(92));
      this.list.add(new InsnNode(87));
      this.logInfo("DUP_VALUE2");
      return this;
   }

   public InstructList dupValue3() {
      this.list.add(new InsnNode(93));
      this.list.add(new InsnNode(88));
      this.list.add(new InsnNode(91));
      this.logInfo("DUP_VALUE3");
      return this;
   }

   public InstructList pop() {
      this.list.add(new InsnNode(87));
      this.logInfo("POP");
      return this;
   }

   public InstructList swap() {
      this.list.add(new InsnNode(95));
      this.logInfo("SWAP");
      return this;
   }

   public InstructList subInt() {
      this.list.add(new InsnNode(100));
      this.logInfo("ISUB");
      return this;
   }

   public InstructList subFloat() {
      this.list.add(new InsnNode(102));
      this.logInfo("FSUB");
      return this;
   }

   protected FieldInsnNode fieldNode(FieldSignature field, int opcode) {
      boolean obfuscated = this.shouldObfuscate();
      return new FieldInsnNode(opcode, field.className.toString(obfuscated), field.name.toString(obfuscated), field.descriptor.toString(obfuscated));
   }

   public InstructList getField(FieldSignature field) {
      this.list.add(this.fieldNode(field, 180));
      this.logInfo("GETFIELD {}", field.name.normal);
      return this;
   }

   public InstructList ifNotNullReturn() {
      return this.ifNotNullReturn(177);
   }

   public InstructList ifNotNullReturnObj() {
      this.dup();
      return this.ifNotNullReturn(176);
   }

   public InstructList ifNotNullReturnInt() {
      this.dup();
      return this.ifNotNullReturn(172);
   }

   public InstructList ifNotNullReplaceStackValue() {
      LabelNode label = new LabelNode();
      this.dup().ifNullJumpTo(label).swap().add(label).pop();
      return this;
   }

   public InstructList ifNotNullReturn(int returnOpcode) {
      LabelNode label = new LabelNode();
      this.ifNullJumpTo(label).add(new InsnNode(returnOpcode)).add(label);
      this.logInfo("IFNONNULL RETURN");
      return this;
   }

   public InstructList ifNullJumpTo(LabelNode label) {
      this.add(new JumpInsnNode(198, label));
      this.logInfo("IFNULL JUMP TO LABEL");
      return this;
   }

   public InstructList ifZeroJumpTo(LabelNode label) {
      this.add(new JumpInsnNode(153, label));
      this.logInfo("IFEQ JUMP TO LABEL");
      return this;
   }

   public InstructList ifNotZeroJumpTo(LabelNode label) {
      this.add(new JumpInsnNode(154, label));
      this.logInfo("IFNE JUMP TO LABEL");
      return this;
   }

   public InstructList insertBefore() {
      this.method.instructions.insertBefore(this.instruct, this.list);
      this.logInfo("insertBefore ", this.instruct);
      return this;
   }

   public InstructList insertAfter() {
      this.method.instructions.insert(this.instruct, this.list);
      this.logInfo("insertAfter ", this.instruct);
      return this;
   }

   public InstructList clear() {
      this.list.clear();
      this.logInfo("clear");
      return this;
   }

   public InstructList forceNormalNames() {
      this.forceNormalNames = true;
      return this;
   }

   public static class VarNode {
      public final int index;
      public final String name;
      protected final int loadOpCode;
      protected final int storeOpCode;

      public VarNode(int index, String name, int loadOpCode, int storeOpCode) {
         this.index = index;
         this.name = name;
         this.loadOpCode = loadOpCode;
         this.storeOpCode = storeOpCode;
      }
   }
}
