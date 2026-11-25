package com.Shultrea.Rin.Transformer.util;

import com.Shultrea.Rin.Transformer.helper.ASMHelper;
import javax.annotation.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class TransformUtil {
   public TransformUtil() {
   }

   @Nullable
   public static FieldInsnNode findNextFieldWithOpcodeAndName(AbstractInsnNode anchor, int opcode, String... names) {
      for(AbstractInsnNode search = ASMHelper.findNextInstructionWithOpcode(anchor, opcode); search != null; search = ASMHelper.findNextInstructionWithOpcode(search, opcode)) {
         if (search.getOpcode() == opcode) {
            for(String name : names) {
               if (name.equals(((FieldInsnNode)search).name)) {
                  return (FieldInsnNode)search;
               }
            }
         }
      }

      return null;
   }

   @Nullable
   public static FieldInsnNode findPreviousFieldWithOpcodeAndName(AbstractInsnNode anchor, int opcode, String... names) {
      for(AbstractInsnNode search = ASMHelper.findPreviousInstructionWithOpcode(anchor, opcode); search != null; search = ASMHelper.findPreviousInstructionWithOpcode(search, opcode)) {
         if (search.getOpcode() == opcode) {
            for(String name : names) {
               if (name.equals(((FieldInsnNode)search).name)) {
                  return (FieldInsnNode)search;
               }
            }
         }
      }

      return null;
   }

   @Nullable
   public static IntInsnNode findNextIntInsnNodeWithValue(AbstractInsnNode anchor, int value) {
      for(AbstractInsnNode search = ASMHelper.findNextInstruction(anchor); search != null; search = ASMHelper.findNextInstruction(search)) {
         if (search.getType() == 1 && value == ((IntInsnNode)search).operand) {
            return (IntInsnNode)search;
         }
      }

      return null;
   }

   @Nullable
   public static IntInsnNode findPreviousIntInsnNodeWithValue(AbstractInsnNode anchor, int value) {
      for(AbstractInsnNode search = ASMHelper.findPreviousInstruction(anchor); search != null; search = ASMHelper.findPreviousInstruction(search)) {
         if (search.getType() == 1 && value == ((IntInsnNode)search).operand) {
            return (IntInsnNode)search;
         }
      }

      return null;
   }

   @Nullable
   public static MethodInsnNode findNextCallWithOpcodeAndName(AbstractInsnNode anchor, int opcode, String... names) {
      for(AbstractInsnNode search = ASMHelper.findNextInstructionWithOpcode(anchor, opcode); search != null; search = ASMHelper.findNextInstructionWithOpcode(search, opcode)) {
         if (search.getOpcode() == opcode) {
            for(String name : names) {
               if (name.equals(((MethodInsnNode)search).name)) {
                  return (MethodInsnNode)search;
               }
            }
         }
      }

      return null;
   }

   @Nullable
   public static MethodInsnNode findPreviousCallWithOpcodeAndName(AbstractInsnNode anchor, int opcode, String... names) {
      for(AbstractInsnNode search = ASMHelper.findPreviousInstructionWithOpcode(anchor, opcode); search != null; search = ASMHelper.findPreviousInstructionWithOpcode(search, opcode)) {
         if (search.getOpcode() == opcode) {
            for(String name : names) {
               if (name.equals(((MethodInsnNode)search).name)) {
                  return (MethodInsnNode)search;
               }
            }
         }
      }

      return null;
   }

   public static void insertBeforeFirst(MethodNode mNode, InsnList insnList) {
      mNode.instructions.insertBefore(ASMHelper.findFirstInstruction(mNode), insnList);
   }

   @Nullable
   public static LocalVariableNode findLocalVariableWithDesc(MethodNode methodNode, String desc) {
      int count = 0;
      LocalVariableNode result = null;

      for(LocalVariableNode lvNode : methodNode.localVariables) {
         if (lvNode.desc.equals(desc)) {
            result = lvNode;
            ++count;
         }
      }

      return result;
   }

   @Nullable
   public static LocalVariableNode findLocalVariableWithName(MethodNode methodNode, String name) {
      int count = 0;
      LocalVariableNode result = null;

      for(LocalVariableNode lvNode : methodNode.localVariables) {
         if (lvNode.name.equals(name)) {
            result = lvNode;
            ++count;
         }
      }

      return result;
   }

   @Nullable
   public static LocalVariableNode createNewLocalVariable(MethodNode methodNode, String name, String desc) {
      int index = 0;

      for(LocalVariableNode lvn : methodNode.localVariables) {
         if (lvn.index >= index) {
            index = lvn.index + 1;
         }
      }

      AbstractInsnNode anchor = methodNode.instructions.getFirst();
      LabelNode l1 = null;

      LabelNode l2;
      for(l2 = null; anchor != null; anchor = anchor.getNext()) {
         if (anchor instanceof LabelNode) {
            if (l1 == null) {
               l1 = (LabelNode)anchor;
            }

            l2 = (LabelNode)anchor;
         }
      }

      if (l1 == null | l2 == null) {
         return null;
      } else {
         return new LocalVariableNode(name, desc, (String)null, l1, l2, index);
      }
   }
}
