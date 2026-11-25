package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchBetterCombatCriticalsFix extends PatchManager {
   public PatchBetterCombatCriticalsFix() {
      super("BetterCombat Criticals Fix");
      this.add(new Patch(this, "bettercombat.mod.util.Helpers", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_attackItem = this.findMethod(clazzNode, new String[]{"attackTargetEntityItem"});
            if (m_attackItem == null) {
               throw new RuntimeException("Couldn't find attackTargetEntityItem in Helpers");
            } else {
               LocalVariableNode lvn_isCrit = TransformUtil.findLocalVariableWithName(m_attackItem, "isCrit");
               if (lvn_isCrit == null) {
                  throw new RuntimeException("Couldn't find local variable isCrit in attackTargetEntityItem");
               } else {
                  int isCrit_index = lvn_isCrit.index;
                  AbstractInsnNode anchor = this.first(m_attackItem);

                  while(anchor != null) {
                     anchor = ASMHelper.findNextInstructionWithOpcode(anchor, 54);
                     if (anchor != null && ((VarInsnNode)anchor).var == isCrit_index) {
                        break;
                     }
                  }

                  if (anchor == null) {
                     throw new RuntimeException("Couldn't find first ISTORE for isCrit in attackTargetEntityItem");
                  } else {
                     InsnList inject = new InsnList();
                     inject.add(new VarInsnNode(25, 0));
                     inject.add(new VarInsnNode(25, 1));
                     inject.add(new VarInsnNode(21, isCrit_index));
                     inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookBetterCombat", "hookCriticalHit", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;Z)Z", false));
                     inject.add(new VarInsnNode(54, isCrit_index));
                     this.insert(m_attackItem, anchor, inject);
                  }
               }
            }
         }
      });
   }
}
