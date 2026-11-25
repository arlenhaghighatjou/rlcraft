package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.ASMUtil;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchPotionCoreResistance extends PatchManager {
   public PatchPotionCoreResistance() {
      super("Patch Potion Core Resistance");
      this.add(new Patch(this, "com.tmtravlr.potioncore.PotionCoreAttributes", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_clinit = this.findMethodWithDesc(clazzNode, "()V", new String[]{"<clinit>"});
            AbstractInsnNode target = ASMUtil.findMethodInsn(m_clinit, 182, "net/minecraft/entity/ai/attributes/RangedAttribute", "setShouldWatch", "setShouldWatch", "(Z)Lnet/minecraft/entity/ai/attributes/BaseAttribute;", 3);
            target = target.getPrevious();
            m_clinit.instructions.insertBefore(target, ASMUtil.listOf(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "resistance_createAttribute", "(Lnet/minecraft/entity/ai/attributes/RangedAttribute;)Lnet/minecraft/entity/ai/attributes/RangedAttribute;", false)));
         }
      });
      this.add(new Patch(this, "com.tmtravlr.potioncore.PotionCoreEffects", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_loadPotionEffects = this.findMethodWithDesc(clazzNode, "(Lnet/minecraftforge/registries/IForgeRegistry;)V", new String[]{"loadPotionEffects"});
            MethodInsnNode i_registerPotionAttributeModifier = ASMUtil.findMethodInsn(m_loadPotionEffects, 182, "net/minecraft/potion/Potion", "registerPotionAttributeModifier", "registerPotionAttributeModifier", "(Lnet/minecraft/entity/ai/attributes/IAttribute;Ljava/lang/String;DI)Lnet/minecraft/potion/Potion;", 0);
            MethodInsnNode redirect = new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "resistance_registerPotionAttributeModifier", "(Lnet/minecraft/potion/Potion;Lnet/minecraft/entity/ai/attributes/IAttribute;Ljava/lang/String;DI)Lnet/minecraft/potion/Potion;", false);
            m_loadPotionEffects.instructions.set(i_registerPotionAttributeModifier, redirect);
         }
      });
      this.add(new Patch(this, "com.tmtravlr.potioncore.potion.PotionVulnerable", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_registerPotionAttributeModifiers = this.findMethodWithDesc(clazzNode, "()V", new String[]{"registerPotionAttributeModifiers"});
            MethodInsnNode i_registerPotionAttributeModifier = ASMUtil.findMethodInsn(m_registerPotionAttributeModifiers, 182, "com/tmtravlr/potioncore/potion/PotionVulnerable", "registerPotionAttributeModifier", "registerPotionAttributeModifier", "(Lnet/minecraft/entity/ai/attributes/IAttribute;Ljava/lang/String;DI)Lnet/minecraft/potion/Potion;", 0);
            MethodInsnNode redirect = new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "vulnerable_registerPotionAttributeModifier", "(Lnet/minecraft/potion/Potion;Lnet/minecraft/entity/ai/attributes/IAttribute;Ljava/lang/String;DI)Lnet/minecraft/potion/Potion;", false);
            m_registerPotionAttributeModifiers.instructions.set(i_registerPotionAttributeModifier, redirect);
         }
      });
      this.add(new Patch(this, "com.tmtravlr.potioncore.PotionCoreEventHandler", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_onLivingHurt = this.findMethodWithDesc(clazzNode, "(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;)V", new String[]{"onLivingHurt"});
            MethodInsnNode i_getAdjustedDamageResistanceAttribute = ASMUtil.findMethodInsn(m_onLivingHurt, 184, "com/tmtravlr/potioncore/PotionCoreAttributes", "getAdjustedDamageResistanceAttribute", "(Lnet/minecraft/entity/EntityLivingBase;)D", 0);
            MethodInsnNode redirect = new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "getAdjustedDamageResistanceAttribute", "(Lnet/minecraft/entity/EntityLivingBase;)D", false);
            m_onLivingHurt.instructions.set(i_getAdjustedDamageResistanceAttribute, redirect);
         }
      });
      this.add(new Patch(this, "net.minecraft.entity.EntityLivingBase", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_applyPotionDamageCalculations = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/util/DamageSource;F)F", new String[]{"applyPotionDamageCalculations", "applyPotionDamageCalculations"});
            AbstractInsnNode target = ASMUtil.findMethodInsn(m_applyPotionDamageCalculations, 182, "net/minecraft/entity/EntityLivingBase", "isPotionActive", "isPotionActive", "(Lnet/minecraft/potion/Potion;)Z", 0);
            target = TransformUtil.findPreviousInsnWithType(target, LabelNode.class);
            AbstractInsnNode target1 = ASMUtil.findInsnWithOpcode(m_applyPotionDamageCalculations, 157, 0);
            target1 = TransformUtil.findPreviousInsnWithType(target1, LabelNode.class);
            this.insert(m_applyPotionDamageCalculations, target, ASMUtil.listOf(new VarInsnNode(23, 2), new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "preResistancePotionCalculation", "(F)V", false)));
            this.insert(m_applyPotionDamageCalculations, target1, ASMUtil.listOf(new VarInsnNode(25, 0), new VarInsnNode(23, 2), new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "postResistancePotionCalculation", "(Lnet/minecraft/entity/EntityLivingBase;F)F", false), new VarInsnNode(56, 2)));
         }
      });
      this.add(new Patch(this, "ichttt.mods.firstaid.common.util.ArmorUtils", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_applyPotionDamageCalculations = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/DamageSource;F)F", new String[]{"applyGlobalPotionModifiers"});
            AbstractInsnNode target = ASMUtil.findMethodInsn(m_applyPotionDamageCalculations, 182, "net/minecraft/entity/player/EntityPlayer", "isPotionActive", "isPotionActive", "(Lnet/minecraft/potion/Potion;)Z", 0);
            target = TransformUtil.findPreviousInsnWithType(target, LabelNode.class);
            AbstractInsnNode target1 = ASMUtil.findInsnWithOpcode(m_applyPotionDamageCalculations, 157, 0);
            target1 = TransformUtil.findPreviousInsnWithType(target1, LabelNode.class);
            this.insert(m_applyPotionDamageCalculations, target, ASMUtil.listOf(new VarInsnNode(23, 2), new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "preResistancePotionCalculation", "(F)V", false)));
            this.insert(m_applyPotionDamageCalculations, target1, ASMUtil.listOf(new VarInsnNode(25, 0), new VarInsnNode(23, 2), new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "postResistancePotionCalculation", "(Lnet/minecraft/entity/EntityLivingBase;F)F", false), new VarInsnNode(56, 2)));
         }
      });
      this.add(new Patch(this, "com.tmtravlr.potioncore.PotionCoreEventHandlerClient", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_renderOverlaysPre = this.findMethodWithDesc(clazzNode, "(Lnet/minecraftforge/client/event/RenderGameOverlayEvent$Pre;)V", new String[]{"renderOverlaysPre"});
            MethodNode m_renderOverlaysPost = this.findMethodWithDesc(clazzNode, "(Lnet/minecraftforge/client/event/RenderGameOverlayEvent$Post;)V", new String[]{"renderOverlaysPost"});
            AbstractInsnNode target = ASMUtil.findMethodInsn(m_renderOverlaysPre, 185, "net/minecraft/entity/ai/attributes/IAttributeInstance", "getAttributeValue", "getAttributeValue", "()D", 0);
            target = target.getNext();
            target = target.getNext();
            AbstractInsnNode target1 = ASMUtil.findMethodInsn(m_renderOverlaysPost, 185, "net/minecraft/entity/ai/attributes/IAttributeInstance", "getAttributeValue", "getAttributeValue", "()D", 1);
            target1 = target1.getNext();
            target1 = target1.getNext();
            this.insert(m_renderOverlaysPre, target, ASMUtil.listOf(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "getActualResistance", "(D)D", false)));
            this.insert(m_renderOverlaysPost, target1, ASMUtil.listOf(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "getActualResistance", "(D)D", false)));
         }
      });
   }
}
