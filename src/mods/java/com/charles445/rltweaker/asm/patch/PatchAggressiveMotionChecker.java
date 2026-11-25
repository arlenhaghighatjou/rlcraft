package com.charles445.rltweaker.asm.patch;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchAggressiveMotionChecker extends PatchManager {
   public PatchAggressiveMotionChecker() {
      super("Aggressive Motion Checker");
      this.add(new Patch(this, "net.minecraft.entity.monster.EntityMob", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_attackEntityAsMob = this.findMethod(clazzNode, new String[]{"attackEntityAsMob", "attackEntityAsMob"});
            if (m_attackEntityAsMob == null) {
               throw new RuntimeException("Couldn't find attackEntityAsMob or attackEntityAsMob");
            } else {
               InsnList inject = new InsnList();
               inject.add(new VarInsnNode(25, 0));
               inject.add(PatchAggressiveMotionChecker.this.hookMotionCheck());
               this.insert(m_attackEntityAsMob, this.first(m_attackEntityAsMob), inject);
            }
         }
      });
      this.add(new Patch(this, "net.minecraft.entity.Entity", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_move = this.findMethod(clazzNode, new String[]{"move", "move"});
            if (m_move == null) {
               throw new RuntimeException("Couldn't find move or move");
            } else {
               InsnList inject = new InsnList();
               inject.add(new VarInsnNode(25, 0));
               inject.add(PatchAggressiveMotionChecker.this.hookMotionCheck());
               this.insert(m_move, this.first(m_move), inject);
            }
         }
      });
   }

   private MethodInsnNode hookMotionCheck() {
      return new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookMinecraft", "aggressiveMotionCheck", "(Lnet/minecraft/entity/Entity;)V", false);
   }
}
