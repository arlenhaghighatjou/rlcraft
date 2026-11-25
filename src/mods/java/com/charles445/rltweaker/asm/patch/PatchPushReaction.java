package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.ClassDisplayer;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchPushReaction extends PatchManager {
   public PatchPushReaction() {
      super("Push Reaction");
      this.add(new Patch(this, "net.minecraft.entity.Entity", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_getPushReaction = this.findMethod(clazzNode, new String[]{"getPushReaction", "getPushReaction"});
            if (m_getPushReaction == null) {
               throw new RuntimeException("Couldn't find method getPushReaction or getPushReaction");
            } else {
               FieldInsnNode fieldStatic = TransformUtil.findPreviousFieldWithOpcodeAndName(this.last(m_getPushReaction), 178, "NORMAL");
               if (fieldStatic == null) {
                  ClassDisplayer.instance.printMethod(m_getPushReaction);
                  throw new RuntimeException("Couldn't find NORMAL getstatic in getPushReaction, observe above for mod compatibility errors");
               } else {
                  while(!fieldStatic.owner.equals("net/minecraft/block/material/EnumPushReaction")) {
                     fieldStatic = TransformUtil.findPreviousFieldWithOpcodeAndName(fieldStatic, 178, "NORMAL");
                     if (fieldStatic == null) {
                        ClassDisplayer.instance.printMethod(m_getPushReaction);
                        throw new RuntimeException("Couldn't find EnumPushReaction specific NORMAL getstatic in getPushReaction, observe above for mod compatibility errors");
                     }
                  }

                  if (ASMHelper.findNextInstruction(fieldStatic).getOpcode() != 176) {
                     ClassDisplayer.instance.printMethod(m_getPushReaction);
                     throw new RuntimeException("Last EnumPushReaction.NORMAL was not the return value, somehow, observe above for mod compatibility errors");
                  } else {
                     AbstractInsnNode anchor = fieldStatic.getPrevious();
                     m_getPushReaction.instructions.remove(anchor.getNext());
                     InsnList inject = new InsnList();
                     inject.add(new VarInsnNode(25, 0));
                     inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookMinecraft", "hookPushReaction", "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/block/material/EnumPushReaction;", false));
                     this.insert(m_getPushReaction, anchor, inject);
                  }
               }
            }
         }
      });
   }
}
