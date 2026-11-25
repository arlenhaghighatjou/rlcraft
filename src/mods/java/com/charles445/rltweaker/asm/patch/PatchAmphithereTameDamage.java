package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.ASMUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchAmphithereTameDamage extends PatchManager {
   public PatchAmphithereTameDamage() {
      super("Patch Amphithere Tame Damage");
      this.add(new Patch(this, "com.github.alexthe666.iceandfire.entity.EntityAmphithere", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_updatePassenger = this.findMethod(clazzNode, new String[]{"updatePassenger", "updatePassenger"});
            this.insert(m_updatePassenger, ASMUtil.findInsnWithOpcode(m_updatePassenger, 12, 0), ASMUtil.listOf(new InsnNode(87), new FieldInsnNode(178, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "iceandfire", "Lcom/charles445/rltweaker/config/ConfigIceAndFire;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ConfigIceAndFire", "amphithereTameDamage", "F")));
         }
      });
   }
}
