package com.charles445.rltweaker.asm.util;

import java.util.NoSuchElementException;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ASMUtil {
   public static final Logger LOGGER = LogManager.getLogger();

   public ASMUtil() {
   }

   public static void printMethodInstructions(MethodNode methodNode) {
      StringBuilder sb = new StringBuilder();
      int i = 0;

      for(AbstractInsnNode instruction : methodNode.instructions.toArray()) {
         sb.append('\n').append(insnToString(i++, instruction));
      }

      LOGGER.info(sb);
   }

   public static String insnToString(int index, AbstractInsnNode insn) {
      StringBuilder sb = new StringBuilder();
      sb.append(index);

      while(sb.length() < 5) {
         sb.append(' ');
      }

      sb.append(insn.getOpcode());

      while(sb.length() < 9) {
         sb.append(' ');
      }

      sb.append(insn.getClass().getSimpleName());

      while(sb.length() < 24) {
         sb.append(' ');
      }

      switch(insn.getType()) {
      case 1:
         sb.append(' ').append(((IntInsnNode)insn).operand);
         break;
      case 2:
         sb.append(' ').append(((VarInsnNode)insn).var);
         break;
      case 3:
         sb.append(' ').append(((TypeInsnNode)insn).desc);
         break;
      case 4:
         sb.append(' ').append(((FieldInsnNode)insn).owner).append(' ').append(((FieldInsnNode)insn).name).append(' ').append(((FieldInsnNode)insn).desc);
         break;
      case 5:
         sb.append(' ').append(((MethodInsnNode)insn).owner).append(' ').append(((MethodInsnNode)insn).name).append(' ').append(((MethodInsnNode)insn).desc);
      case 6:
      case 10:
      case 11:
      case 12:
      case 13:
      default:
         break;
      case 7:
         sb.append(' ').append(((JumpInsnNode)insn).label.getLabel());
         break;
      case 8:
         sb.append(' ').append(((LabelNode)insn).getLabel());
         break;
      case 9:
         sb.append(' ').append(((LdcInsnNode)insn).cst);
         break;
      case 14:
         sb.append(' ').append(((FrameNode)insn).local).append(' ').append(((FrameNode)insn).stack);
         break;
      case 15:
         sb.append(' ').append(((LineNumberNode)insn).line);
      }

      return sb.toString();
   }

   public static AbstractInsnNode findInsnWithOpcode(MethodNode methodNode, int opcode, int ordinal) {
      return findInsn(methodNode, (Class)null, (insn) -> {
         return insn.getOpcode() == opcode;
      }, ordinal);
   }

   public static <T extends AbstractInsnNode> T findInsnWithOpcode(MethodNode methodNode, Class<T> type, int opcode, int ordinal) {
      return (T)findInsn(methodNode, type, (insn) -> {
         return (T)(insn.getOpcode() == opcode);
      }, ordinal);
   }

   public static <T extends AbstractInsnNode> T findInsnWithType(MethodNode methodNode, Class<T> type, int ordinal) {
      return (T)findInsn(methodNode, type, (Predicate)null, ordinal);
   }

   public static LdcInsnNode findLdcInsn(MethodNode methodNode, Object cst, int ordinal) {
      return (LdcInsnNode)findInsn(methodNode, LdcInsnNode.class, (insn) -> {
         return insn.cst.equals(cst);
      }, ordinal);
   }

   public static IntInsnNode findIntInsn(MethodNode methodNode, int opcode, int operand, int ordinal) {
      return (IntInsnNode)findInsn(methodNode, IntInsnNode.class, (insn) -> {
         return insn.getOpcode() == opcode && insn.operand == operand;
      }, ordinal);
   }

   public static MethodInsnNode findMethodInsn(MethodNode methodNode, int opcode, String owner, String name, String desc, int ordinal) {
      return findMethodInsn(methodNode, opcode, owner, name, (String)null, desc, ordinal);
   }

   public static MethodInsnNode findMethodInsn(MethodNode methodNode, int opcode, String owner, String obfName, String name, String desc, int ordinal) {
      return (MethodInsnNode)findInsn(methodNode, MethodInsnNode.class, (insn) -> {
         return insn.getOpcode() == opcode && insn.owner.equals(owner) && (insn.name.equals(obfName) || insn.name.equals(name)) && insn.desc.equals(desc);
      }, ordinal);
   }

   public static FieldInsnNode findFieldInsn(MethodNode methodNode, int opcode, String owner, String name, String desc, int ordinal) {
      return findFieldInsn(methodNode, opcode, owner, name, (String)null, desc, ordinal);
   }

   public static FieldInsnNode findFieldInsn(MethodNode methodNode, int opcode, String owner, String obfName, String name, String desc, int ordinal) {
      return (FieldInsnNode)findInsn(methodNode, FieldInsnNode.class, (insn) -> {
         return insn.getOpcode() == opcode && insn.owner.equals(owner) && (insn.name.equals(obfName) || insn.name.equals(name)) && insn.desc.equals(desc);
      }, ordinal);
   }

   public static <T extends AbstractInsnNode> T findInsn(MethodNode methodNode, Class<T> type, Predicate<T> predicate, int ordinal) {
      int i = 0;

      AbstractInsnNode insn;
      for(insn = methodNode.instructions.getFirst(); insn != null && (type != null && !type.isInstance(insn) || predicate != null && !predicate.test(insn) || i++ != ordinal); insn = insn.getNext()) {
         ;
      }

      if (insn == null) {
         throw new NoSuchElementException();
      } else {
         return (T)insn;
      }
   }

   public static InsnList listOf(AbstractInsnNode... nodes) {
      InsnList list = new InsnList();

      for(AbstractInsnNode node : nodes) {
         list.add(node);
      }

      return list;
   }
}
