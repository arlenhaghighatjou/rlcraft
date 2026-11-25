package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.ASMUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchRustic extends PatchManager {
   public PatchRustic() {
      this.add(new Patch(this, "rustic.common.blocks.fluids.ModFluids$12", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_affectPlayer = this.findMethod(clazzNode, new String[]{"affectPlayer"});
            this.insert(m_affectPlayer, ASMUtil.findMethodInsn(m_affectPlayer, 182, "net/minecraft/entity/player/EntityPlayer", "getActivePotionEffects", "getActivePotionEffects", "()Ljava/util/Collection;", 0), ASMUtil.listOf(new MethodInsnNode(184, "com/charles445/rltweaker/util/CollectionUtil", "copy", "(Ljava/util/Collection;)Ljava/util/Collection;", false)));
            this.insert(m_affectPlayer, ASMUtil.findInsnWithOpcode(m_affectPlayer, 5, 0), ASMUtil.listOf(new InsnNode(87), new FieldInsnNode(178, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ConfigRustic", "wildberryWineAmplifierMaximum", "I")));
            this.insert(m_affectPlayer, ASMUtil.findInsnWithOpcode(m_affectPlayer, 4, 1), ASMUtil.listOf(new InsnNode(87), new FieldInsnNode(178, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ConfigRustic", "wildberryWineAmplifierModifier", "I")));
            this.insert(m_affectPlayer, ASMUtil.findInsnWithOpcode(m_affectPlayer, 4, 2), ASMUtil.listOf(new InsnNode(87), new FieldInsnNode(178, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ConfigRustic", "wildberryWineAmplifierModifier", "I")));
         }
      });
      this.add(new Patch(this, "rustic.common.blocks.fluids.ModFluids$13", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_affectPlayer = this.findMethod(clazzNode, new String[]{"affectPlayer"});
            this.insert(m_affectPlayer, ASMUtil.findMethodInsn(m_affectPlayer, 182, "net/minecraft/entity/player/EntityPlayer", "getActivePotionEffects", "getActivePotionEffects", "()Ljava/util/Collection;", 0), ASMUtil.listOf(new MethodInsnNode(184, "com/charles445/rltweaker/util/CollectionUtil", "copy", "(Ljava/util/Collection;)Ljava/util/Collection;", false)));
            this.insert(m_affectPlayer, ASMUtil.findIntInsn(m_affectPlayer, 17, 12000, 0), ASMUtil.listOf(new InsnNode(87), new FieldInsnNode(178, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ConfigRustic", "wineDurationMaximum", "I")));
            this.insert(m_affectPlayer, ASMUtil.findIntInsn(m_affectPlayer, 17, 12000, 1), ASMUtil.listOf(new InsnNode(87), new FieldInsnNode(178, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ConfigRustic", "wineDurationMaximum", "I")));
            this.insert(m_affectPlayer, ASMUtil.findLdcInsn(m_affectPlayer, Float.valueOf(2400.0F), 0), ASMUtil.listOf(new InsnNode(87), new FieldInsnNode(178, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ConfigRustic", "wineDurationModifier", "I"), new InsnNode(134)));
            this.insert(m_affectPlayer, ASMUtil.findLdcInsn(m_affectPlayer, Double.valueOf(2400.0D), 0), ASMUtil.listOf(new InsnNode(88), new FieldInsnNode(178, "com/charles445/rltweaker/config/ModConfig", "server", "Lcom/charles445/rltweaker/config/ModConfig$ServerConfig;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ModConfig$ServerConfig", "rustic", "Lcom/charles445/rltweaker/config/ConfigRustic;"), new FieldInsnNode(180, "com/charles445/rltweaker/config/ConfigRustic", "wineDurationModifier", "I"), new InsnNode(135)));
         }
      });
   }
}
