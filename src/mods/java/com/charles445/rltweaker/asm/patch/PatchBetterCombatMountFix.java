package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchBetterCombatMountFix extends PatchManager {
   public PatchBetterCombatMountFix() {
      super("BetterCombat Mount Fix");
      this.add(new Patch(this, "bettercombat.mod.client.handler.EventHandlersClient", 3) {
         public void patch(ClassNode c_EventHandlersClient) {
            MethodNode m_getMouseOverExtended = this.findMethod(c_EventHandlersClient, new String[]{"getMouseOverExtended"});
            MethodInsnNode hookCaller = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_getMouseOverExtended), 182, "canBeCollidedWith", "canBeCollidedWith");
            if (hookCaller == null) {
               throw new RuntimeException("Couldn't find canBeCollidedWith or canBeCollidedWith");
            } else {
               LocalVariableNode lvn_rvEntity = TransformUtil.findLocalVariableWithName(m_getMouseOverExtended, "rvEntity");
               if (lvn_rvEntity == null) {
                  throw new RuntimeException("Couldn't find local variable rvEntity");
               } else {
                  m_getMouseOverExtended.instructions.insertBefore(hookCaller, new VarInsnNode(25, lvn_rvEntity.index));
                  hookCaller.setOpcode(184);
                  hookCaller.owner = "com/charles445/rltweaker/hook/HookBetterCombat";
                  hookCaller.name = "strictCollisionCheck";
                  hookCaller.desc = "(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;)Z";
               }
            }
         }
      });
   }
}
