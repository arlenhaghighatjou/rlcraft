package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.ASMUtil;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchCurePotion extends PatchManager {
   public PatchCurePotion() {
      super("Patch Cure Potion");
      this.add(new Patch(this, "com.tmtravlr.potioncore.potion.PotionPotionSickness", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_lambda$performEffect$0 = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/entity/EntityLivingBase;)V", new String[]{"lambda$performEffect$0"});
            InsnList inject = new InsnList();
            inject.add(new VarInsnNode(25, 0));
            inject.add(new FieldInsnNode(178, "com/tmtravlr/potioncore/PotionCoreEffects", "BAD_EFFECTS", "Ljava/util/ArrayList;"));
            inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "addPotionSicknessEffect", "(Lnet/minecraft/entity/EntityLivingBase;Ljava/util/List;)V", false));
            inject.add(new InsnNode(177));
            TransformUtil.insertBeforeFirst(m_lambda$performEffect$0, inject);
         }
      });
      this.add(new Patch(this, "com.tmtravlr.potioncore.potion.PotionCure", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_clearNegativeEffects = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/entity/EntityLivingBase;)V", new String[]{"clearNegativeEffects"});
            LabelNode label = new LabelNode();
            InsnList inject = new InsnList();
            inject.add(new VarInsnNode(25, 1));
            inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "isCureDisabled", "(Lnet/minecraft/entity/EntityLivingBase;)Z", false));
            inject.add(new JumpInsnNode(153, label));
            inject.add(new InsnNode(177));
            inject.add(label);
            TransformUtil.insertBeforeFirst(m_clearNegativeEffects, inject);
            JumpInsnNode toCall = (JumpInsnNode)TransformUtil.findNextInsnWithType(TransformUtil.findNextCallWithOpcodeAndName(this.first(m_clearNegativeEffects), 182, "isBadEffect", "isBadEffect"), JumpInsnNode.class);
            InsnList inject1 = new InsnList();
            inject1.add(new VarInsnNode(25, 5));
            inject1.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "isCurable", "(Lnet/minecraft/potion/PotionEffect;)Z", false));
            inject1.add(new JumpInsnNode(153, toCall.label));
            this.insert(m_clearNegativeEffects, toCall, inject1);
         }
      });
      this.add(new Patch(this, "net.minecraft.potion.PotionEffect", 3) {
         public void patch(ClassNode clazzNode) {
            clazzNode.interfaces.add("com/charles445/rltweaker/hook/HookPotionCore$Incurable");
            clazzNode.fields.add(new FieldNode(2, "incurable", "Z", (String)null, false));
            MethodNode methodIsCurable = new MethodNode(1, "isIncurable", "()Z", (String)null, (String[])null);
            methodIsCurable.instructions.add(new VarInsnNode(25, 0));
            methodIsCurable.instructions.add(new FieldInsnNode(180, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
            methodIsCurable.instructions.add(new InsnNode(172));
            clazzNode.methods.add(methodIsCurable);
            MethodNode methodSetCurable = new MethodNode(1, "setIncurable", "(Z)V", (String)null, (String[])null);
            methodSetCurable.instructions.add(new VarInsnNode(25, 0));
            methodSetCurable.instructions.add(new VarInsnNode(21, 1));
            methodSetCurable.instructions.add(new FieldInsnNode(181, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
            methodSetCurable.instructions.add(new InsnNode(177));
            clazzNode.methods.add(methodSetCurable);
            MethodNode m = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/potion/PotionEffect;)V", new String[]{"<init>"});
            AbstractInsnNode toCall = TransformUtil.findPreviousInsnWithType(ASMHelper.findLastInstructionWithOpcode(m, 177), LabelNode.class);
            InsnList inject = new InsnList();
            inject.add(new VarInsnNode(25, 0));
            inject.add(new VarInsnNode(25, 1));
            inject.add(new FieldInsnNode(180, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
            inject.add(new FieldInsnNode(181, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
            this.insert(m, toCall, inject);
            m = this.findMethod(clazzNode, new String[]{"combine", "combine"});
            LabelNode label1 = new LabelNode();
            LabelNode label2 = new LabelNode();
            InsnList inject = new InsnList();
            inject.add(new VarInsnNode(25, 0));
            inject.add(TransformUtil.createObfFieldInsn(180, "net/minecraft/potion/PotionEffect", "amplifier", "I"));
            inject.add(new VarInsnNode(25, 1));
            inject.add(TransformUtil.createObfFieldInsn(180, "net/minecraft/potion/PotionEffect", "amplifier", "I"));
            inject.add(new JumpInsnNode(161, label1));
            inject.add(new VarInsnNode(25, 0));
            inject.add(TransformUtil.createObfFieldInsn(180, "net/minecraft/potion/PotionEffect", "amplifier", "I"));
            inject.add(new VarInsnNode(25, 1));
            inject.add(TransformUtil.createObfFieldInsn(180, "net/minecraft/potion/PotionEffect", "amplifier", "I"));
            inject.add(new JumpInsnNode(160, label2));
            inject.add(new VarInsnNode(25, 0));
            inject.add(TransformUtil.createObfFieldInsn(180, "net/minecraft/potion/PotionEffect", "duration", "I"));
            inject.add(new VarInsnNode(25, 1));
            inject.add(TransformUtil.createObfFieldInsn(180, "net/minecraft/potion/PotionEffect", "duration", "I"));
            inject.add(new JumpInsnNode(162, label2));
            inject.add(label1);
            inject.add(new VarInsnNode(25, 0));
            inject.add(new VarInsnNode(25, 1));
            inject.add(new FieldInsnNode(180, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
            inject.add(new FieldInsnNode(181, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
            inject.add(label2);
            TransformUtil.insertBeforeFirst(m, inject);
            m = this.findMethod(clazzNode, new String[]{"toString"});
            AbstractInsnNode toCall = TransformUtil.findPreviousInsnWithType(ASMHelper.findLastInstructionWithOpcode(m, 176), LabelNode.class);
            label2 = new LabelNode();
            inject = new InsnList();
            inject.add(new VarInsnNode(25, 0));
            inject.add(new FieldInsnNode(180, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
            inject.add(new JumpInsnNode(153, label2));
            inject.add(new TypeInsnNode(187, "java/lang/StringBuilder"));
            inject.add(new InsnNode(89));
            inject.add(new VarInsnNode(25, 1));
            inject.add(new MethodInsnNode(183, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false));
            inject.add(new LdcInsnNode(", Incurable: true"));
            inject.add(new MethodInsnNode(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
            inject.add(new MethodInsnNode(182, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false));
            inject.add(new VarInsnNode(58, 1));
            inject.add(label2);
            this.insert(m, toCall, inject);
            m = this.findMethod(clazzNode, new String[]{"equals"});
            toCall = ASMHelper.findLastInstructionWithOpcode(m, 172);
            toCall = ASMHelper.findPreviousInstructionWithOpcode(toCall, 3);
            label2 = (LabelNode)TransformUtil.findPreviousInsnWithType(toCall, LabelNode.class);
            AbstractInsnNode toCall = ASMHelper.findPreviousInstructionWithOpcode(label2, 4);
            InsnList inject = new InsnList();
            inject.add(new VarInsnNode(25, 0));
            inject.add(new FieldInsnNode(180, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
            inject.add(new VarInsnNode(25, 2));
            inject.add(new FieldInsnNode(180, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
            inject.add(new JumpInsnNode(160, label2));
            this.insertBefore(m, toCall, inject);
            m = this.findMethod(clazzNode, new String[]{"hashCode"});
            toCall = TransformUtil.findPreviousInsnWithType(ASMHelper.findLastInstructionWithOpcode(m, 172), LabelNode.class);
            label2 = new LabelNode();
            LabelNode label1 = new LabelNode();
            inject = new InsnList();
            inject.add(new IntInsnNode(16, 31));
            inject.add(new VarInsnNode(21, 1));
            inject.add(new InsnNode(104));
            inject.add(new VarInsnNode(25, 0));
            inject.add(new FieldInsnNode(180, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
            inject.add(new JumpInsnNode(153, label2));
            inject.add(new InsnNode(4));
            inject.add(new JumpInsnNode(167, label1));
            inject.add(label2);
            inject.add(new InsnNode(3));
            inject.add(label1);
            inject.add(new InsnNode(96));
            inject.add(new IntInsnNode(54, 1));
            this.insert(m, toCall, inject);
            m = this.findMethod(clazzNode, new String[]{"writeCustomPotionEffectToNBT", "writeCustomPotionEffectToNBT"});
            toCall = ASMHelper.findLastInstructionWithOpcode(m, 176);
            InsnList inject = new InsnList();
            inject.add(new InsnNode(89));
            inject.add(new LdcInsnNode("Incurable"));
            inject.add(new VarInsnNode(25, 0));
            inject.add(new FieldInsnNode(180, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
            inject.add(TransformUtil.createObfMethodInsn(182, "net/minecraft/nbt/NBTTagCompound", "setBoolean", "(Ljava/lang/String;Z)V", false));
            this.insertBefore(m, toCall, inject);
            m = this.findMethod(clazzNode, new String[]{"readCustomPotionEffectFromNBT", "readCustomPotionEffectFromNBT"});
            toCall = ASMHelper.findLastInstructionWithOpcode(m, 176);
            inject = new InsnList();
            inject.add(new InsnNode(89));
            inject.add(new VarInsnNode(25, 0));
            inject.add(new LdcInsnNode("Incurable"));
            inject.add(TransformUtil.createObfMethodInsn(182, "net/minecraft/nbt/NBTTagCompound", "getBoolean", "(Ljava/lang/String;)Z", false));
            inject.add(new FieldInsnNode(181, "net/minecraft/potion/PotionEffect", "incurable", "Z"));
            this.insertBefore(m, toCall, inject);
         }
      });
      this.add(new Patch(this, "rustic.common.potions.PotionTipsy", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_performEffect = this.findMethod(clazzNode, new String[]{"performEffect", "performEffect"});
            this.insertBefore(m_performEffect, ASMUtil.findMethodInsn(m_performEffect, 182, "net/minecraft/entity/EntityLivingBase", "addPotionEffect", "addPotionEffect", "(Lnet/minecraft/potion/PotionEffect;)V", 0), ASMUtil.listOf(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "onAddTipsyEffect", "(Lnet/minecraft/potion/PotionEffect;)Lnet/minecraft/potion/PotionEffect;", false)));
            this.insertBefore(m_performEffect, ASMUtil.findMethodInsn(m_performEffect, 182, "net/minecraft/entity/EntityLivingBase", "addPotionEffect", "addPotionEffect", "(Lnet/minecraft/potion/PotionEffect;)V", 1), ASMUtil.listOf(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "onAddTipsyEffect", "(Lnet/minecraft/potion/PotionEffect;)Lnet/minecraft/potion/PotionEffect;", false)));
            this.insertBefore(m_performEffect, ASMUtil.findMethodInsn(m_performEffect, 182, "net/minecraft/entity/EntityLivingBase", "addPotionEffect", "addPotionEffect", "(Lnet/minecraft/potion/PotionEffect;)V", 2), ASMUtil.listOf(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "onAddTipsyEffect", "(Lnet/minecraft/potion/PotionEffect;)Lnet/minecraft/potion/PotionEffect;", false)));
            this.insertBefore(m_performEffect, ASMUtil.findMethodInsn(m_performEffect, 182, "net/minecraft/entity/EntityLivingBase", "addPotionEffect", "addPotionEffect", "(Lnet/minecraft/potion/PotionEffect;)V", 3), ASMUtil.listOf(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "onAddTipsyEffect", "(Lnet/minecraft/potion/PotionEffect;)Lnet/minecraft/potion/PotionEffect;", false)));
            this.insertBefore(m_performEffect, ASMUtil.findMethodInsn(m_performEffect, 182, "net/minecraft/entity/EntityLivingBase", "addPotionEffect", "addPotionEffect", "(Lnet/minecraft/potion/PotionEffect;)V", 4), ASMUtil.listOf(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookPotionCore", "onAddTipsyEffect", "(Lnet/minecraft/potion/PotionEffect;)Lnet/minecraft/potion/PotionEffect;", false)));
         }
      });
   }
}
