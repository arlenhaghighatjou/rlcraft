package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class PatchEnchant extends PatchManager {
   private static final String owner_HookEnchant = "com/charles445/rltweaker/hook/HookEnchant";

   public PatchEnchant() {
      super("Enchant");
      this.add(new Patch(this, "net.minecraft.enchantment.EnchantmentHelper", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_getEnchantmentDatas = this.findMethod(clazzNode, new String[]{"getEnchantmentDatas", "getEnchantmentDatas"});
            if (m_getEnchantmentDatas == null) {
               throw new RuntimeException("Couldn't find getEnchantmentDatas or getEnchantmentDatas");
            } else {
               AbstractInsnNode aret = ASMHelper.findLastInstructionWithOpcode(m_getEnchantmentDatas, 176);
               if (aret == null) {
                  throw new RuntimeException("Couldn't find ARETURN in getEnchantmentDatas");
               } else {
                  m_getEnchantmentDatas.instructions.insertBefore(aret, new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookEnchant", "restrictEnchantmentDatas", "(Ljava/util/List;)Ljava/util/List;", false));
               }
            }
         }
      });
      this.add(new Patch(this, "net.minecraft.entity.passive.EntityVillager$ListEnchantedBookForEmeralds", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_addMerchantRecipe = this.findMethod(clazzNode, new String[]{"addMerchantRecipe", "addMerchantRecipe"});
            if (m_addMerchantRecipe == null) {
               throw new RuntimeException("Couldn't find addMerchantRecipe or addMerchantRecipe");
            } else {
               MethodInsnNode getRandomObjectNode = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_addMerchantRecipe), 182, "getRandomObject", "getRandomObject");
               if (getRandomObjectNode == null) {
                  throw new RuntimeException("Couldn't find getRandomObject in addMerchantRecipe");
               } else {
                  getRandomObjectNode.setOpcode(184);
                  getRandomObjectNode.owner = "com/charles445/rltweaker/hook/HookEnchant";
                  getRandomObjectNode.name = "getRandomRestricted";
                  getRandomObjectNode.desc = "(Ljava/lang/Object;Ljava/util/Random;)Lnet/minecraft/enchantment/Enchantment;";
               }
            }
         }
      });
      this.add(new Patch(this, "net.minecraft.world.storage.loot.functions.EnchantRandomly", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_apply = this.findMethod(clazzNode, new String[]{"apply", "apply"});
            AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_apply), 182, "canApply", "canApply");
            if (anchor == null) {
               throw new RuntimeException("Couldn't find canApply or canApply");
            } else {
               MethodInsnNode addNode = TransformUtil.findNextCallWithOpcodeAndName(anchor, 185, "add");
               if (addNode == null) {
                  throw new RuntimeException("Couldn't find add in apply");
               } else if (!addNode.owner.equals("java/util/List")) {
                  throw new RuntimeException("Unexpected add found in apply: " + addNode.owner);
               } else {
                  addNode.setOpcode(184);
                  addNode.owner = "com/charles445/rltweaker/hook/HookEnchant";
                  addNode.name = "addEnchantmentRestricted";
                  addNode.desc = "(Ljava/util/List;Lnet/minecraft/enchantment/Enchantment;)Z";
               }
            }
         }
      });
   }
}
