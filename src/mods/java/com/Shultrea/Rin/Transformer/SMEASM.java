package com.Shultrea.Rin.Transformer;

import com.Shultrea.Rin.Transformer.helper.ASMHelper;
import com.Shultrea.Rin.Transformer.util.TransformUtil;
import javax.annotation.Nullable;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class SMEASM implements IClassTransformer {
   public SMEASM() {
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      if (transformedName.equals("net.minecraft.enchantment.EnchantmentHelper")) {
         System.out.println("Patching EnchantmentHelper");
         return this.patchEnchantmentHelper(basicClass);
      } else if (transformedName.equals("net.minecraft.enchantment.EnchantmentProtection")) {
         System.out.println("Patching EnchantmentProtection");
         return this.patchEnchantmentProtection(basicClass);
      } else {
         return basicClass;
      }
   }

   private byte[] patchEnchantmentHelper(byte[] basicClass) {
      ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);

      for(MethodNode m : clazzNode.methods) {
         if (!m.name.equals("applyArthropodEnchantments") && !m.name.equals("applyArthropodEnchantments")) {
            if (!m.name.equals("getFishingLuckBonus") && !m.name.equals("getFishingLuckBonus")) {
               if (!m.name.equals("getFishingSpeedBonus") && !m.name.equals("getFishingSpeedBonus")) {
                  if (m.name.equals("getLootingModifier") || m.name.equals("getLootingModifier")) {
                     this.hookAllIntegerReturn(m, "com/Shultrea/Rin/Hook/HookHelper", "modifyLootingModifier", "(ILnet/minecraft/entity/EntityLivingBase;)I", this.aload(0));
                  }
               } else {
                  this.hookAllIntegerReturn(m, "com/Shultrea/Rin/Hook/HookHelper", "modifyFishingSpeedBonus", "(ILnet/minecraft/item/ItemStack;)I", this.aload(0));
               }
            } else {
               this.hookAllIntegerReturn(m, "com/Shultrea/Rin/Hook/HookHelper", "modifyFishingLuckBonus", "(ILnet/minecraft/item/ItemStack;)I", this.aload(0));
            }
         } else {
            this.hookArthropod(m);
         }
      }

      return ASMHelper.writeClassToBytes(clazzNode, 3);
   }

   private byte[] patchEnchantmentProtection(byte[] basicClass) {
      ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);

      for(MethodNode m : clazzNode.methods) {
         if (m.name.equals("getBlastDamageReduction") || m.name.equals("getBlastDamageReduction")) {
            this.hookAllDoubleReturn(m, "com/Shultrea/Rin/Hook/HookHelper", "modifyBlastDamageReduction", "(DLnet/minecraft/entity/EntityLivingBase;)D", this.aload(0));
         }
      }

      return ASMHelper.writeClassToBytes(clazzNode, 3);
   }

   private void hookAllIntegerReturn(MethodNode m, String owner, String name, String desc, AbstractInsnNode... loads) {
      for(AbstractInsnNode anchor = m.instructions.getFirst(); anchor.getNext() != null; anchor = anchor.getNext()) {
         if (anchor.getOpcode() == 172) {
            InsnList insert = new InsnList();

            for(AbstractInsnNode node : loads) {
               insert.add(node);
            }

            insert.add(new MethodInsnNode(184, owner, name, desc, false));
            m.instructions.insertBefore(anchor, insert);
         }
      }

   }

   private void hookAllDoubleReturn(MethodNode m, String owner, String name, String desc, AbstractInsnNode... loads) {
      for(AbstractInsnNode anchor = m.instructions.getFirst(); anchor.getNext() != null; anchor = anchor.getNext()) {
         if (anchor.getOpcode() == 175) {
            InsnList insert = new InsnList();

            for(AbstractInsnNode node : loads) {
               insert.add(node);
            }

            insert.add(new MethodInsnNode(184, owner, name, desc, false));
            m.instructions.insertBefore(anchor, insert);
         }
      }

   }

   private VarInsnNode aload(int loadvalue) {
      return new VarInsnNode(25, loadvalue);
   }

   private void hookArthropod(MethodNode m) {
      InsnList insert = new InsnList();
      insert.add(new VarInsnNode(25, 0));
      insert.add(new VarInsnNode(25, 1));
      insert.add(new MethodInsnNode(184, "com/Shultrea/Rin/Hook/HookArthropod", "hookArthropod", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/Entity;)V", false));
      TransformUtil.insertBeforeFirst(m, insert);
   }

   private byte[] patchBetterCombat(byte[] basicClass) {
      ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);

      for(MethodNode m : clazzNode.methods) {
         if (m.name.equals("attackTargetEntityItem")) {
            this.hookBetterCombatArthropod(m);
         }
      }

      return ASMHelper.writeClassToBytes(clazzNode, 1);
   }

   private byte[] patchEntity(byte[] basicClass) {
      ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);

      for(MethodNode m : clazzNode.methods) {
         if (m.name.equals("applyEnchantments") || m.name.equals("applyEnchantments")) {
            this.hookEntityArthropod(m);
         }
      }

      return ASMHelper.writeClassToBytes(clazzNode, 1);
   }

   private byte[] patchPlayer(byte[] basicClass) {
      ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);

      for(MethodNode m : clazzNode.methods) {
         if (m.name.equals("attackTargetEntityWithCurrentItem") || m.name.equals("attackTargetEntityWithCurrentItem")) {
            this.hookPlayerArthropod(m);
         }
      }

      return ASMHelper.writeClassToBytes(clazzNode, 1);
   }

   private void hookEntityArthropod(MethodNode m) {
      AbstractInsnNode anchor = this.getArthropodInvoke(m);
      if (anchor != null) {
         InsnList insert = new InsnList();
         insert.add(new VarInsnNode(25, 1));
         insert.add(new VarInsnNode(25, 2));
         insert.add(new InsnNode(3));
         insert.add(this.getNewArthropodHook());
         m.instructions.insert(anchor, insert);
      }
   }

   private void hookPlayerArthropod(MethodNode m) {
      AbstractInsnNode anchor = this.getArthropodInvoke(m);
      if (anchor != null) {
         InsnList insert = new InsnList();
         insert.add(new VarInsnNode(25, 0));
         insert.add(new VarInsnNode(25, 1));
         insert.add(new InsnNode(3));
         insert.add(this.getNewArthropodHook());
         m.instructions.insert(anchor, insert);
      }
   }

   private void hookBetterCombatArthropod(MethodNode m) {
      AbstractInsnNode anchor = this.getArthropodInvoke(m);
      if (anchor != null) {
         InsnList insert = new InsnList();
         insert.add(new VarInsnNode(25, 0));
         insert.add(new VarInsnNode(25, 1));
         insert.add(new VarInsnNode(21, 2));
         insert.add(this.getNewArthropodHook());
         m.instructions.insert(anchor, insert);
      }
   }

   @Nullable
   private AbstractInsnNode getArthropodInvoke(MethodNode m) {
      return TransformUtil.findNextCallWithOpcodeAndName(m.instructions.getFirst(), 184, "applyArthropodEnchantments", "applyArthropodEnchantments");
   }

   private AbstractInsnNode getNewArthropodHook() {
      return new MethodInsnNode(184, "com/Shultrea/Rin/Hook/HookArthropod", "handleArthropod", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/entity/Entity;Z)V", false);
   }
}
