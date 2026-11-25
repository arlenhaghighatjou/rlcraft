package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ObfHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchLycanitesDupe extends PatchManager {
   public PatchLycanitesDupe() {
      super("Lycanites Dupe Patch");
      this.add(new Patch(this, "com.lycanitesmobs.core.inventory.InventoryCreature", 3) {
         public void patch(ClassNode clazzNode) {
            this.announce("Patching InventoryCreature");
            this.announce("If your game crashes immediately after this, change the config rltweaker/patches/lycanitesPetDupeFix to false!");
            AbstractInsnNode anchor = null;
            String deathsave = "DEATHSAVE";
            String deathsave_T = "I";

            for(FieldNode f : clazzNode.fields) {
               if (f != null && f.name.equals("DEATHSAVE")) {
                  System.out.println("Dupe has already been patched! Skipping...");
                  this.cancelled = true;
                  return;
               }
            }

            clazzNode.fields.add(new FieldNode(1, deathsave, deathsave_T, (String)null, Integer.valueOf(0)));
            MethodNode m_writeToNBT = this.findMethod(clazzNode, new String[]{"writeToNBT"});
            anchor = this.first(m_writeToNBT);
            LabelNode wtnSetContinueNode = new LabelNode();
            InsnList wtnInsert = new InsnList();
            wtnInsert.add(new VarInsnNode(25, 0));
            wtnInsert.add(new FieldInsnNode(180, "com/lycanitesmobs/core/inventory/InventoryCreature", deathsave, deathsave_T));
            wtnInsert.add(new InsnNode(5));
            wtnInsert.add(new JumpInsnNode(161, wtnSetContinueNode));
            wtnInsert.add(new InsnNode(177));
            wtnInsert.add(wtnSetContinueNode);
            this.insert(m_writeToNBT, anchor, wtnInsert);
            InsnList wotInsert = new InsnList();
            anchor = this.previous(this.last(m_writeToNBT));
            LabelNode wotContinueNode = new LabelNode();
            wotInsert.add(new VarInsnNode(25, 0));
            wotInsert.add(new FieldInsnNode(180, "com/lycanitesmobs/core/inventory/InventoryCreature", "creature", "Lcom/lycanitesmobs/core/entity/BaseCreatureEntity;"));
            wotInsert.add(new MethodInsnNode(182, "net/minecraft/entity/EntityLivingBase", ObfHelper.isObfuscated() ? "isEntityAlive" : "isEntityAlive", "()Z", false));
            wotInsert.add(new JumpInsnNode(157, wotContinueNode));
            wotInsert.add(new VarInsnNode(25, 0));
            wotInsert.add(new VarInsnNode(25, 0));
            wotInsert.add(new FieldInsnNode(180, "com/lycanitesmobs/core/inventory/InventoryCreature", deathsave, deathsave_T));
            wotInsert.add(new InsnNode(4));
            wotInsert.add(new InsnNode(96));
            wotInsert.add(new FieldInsnNode(181, "com/lycanitesmobs/core/inventory/InventoryCreature", deathsave, deathsave_T));
            wotInsert.add(wotContinueNode);
            this.insert(m_writeToNBT, anchor, wotInsert);
            MethodNode m_getStackInSlot = this.findMethod(clazzNode, new String[]{"getStackInSlot", "getStackInSlot"});
            anchor = this.first(m_getStackInSlot);
            anchor = this.previous(TransformUtil.findNextFieldWithOpcodeAndName(anchor, 180, "inventoryContents"));
            LabelNode gsisContinueNode = new LabelNode();
            InsnList gsisInsert = new InsnList();
            gsisInsert.add(new VarInsnNode(25, 0));
            gsisInsert.add(new FieldInsnNode(180, "com/lycanitesmobs/core/inventory/InventoryCreature", deathsave, deathsave_T));
            gsisInsert.add(new InsnNode(5));
            gsisInsert.add(new JumpInsnNode(161, gsisContinueNode));
            gsisInsert.add(new FieldInsnNode(178, "net/minecraft/item/ItemStack", ObfHelper.isObfuscated() ? "EMPTY" : "EMPTY", "Lnet/minecraft/item/ItemStack;"));
            gsisInsert.add(new InsnNode(176));
            gsisInsert.add(gsisContinueNode);
            this.insert(m_getStackInSlot, anchor, gsisInsert);
            this.previous(this.last(m_writeToNBT));
            MethodNode m_onInventoryChanged = this.findMethod(clazzNode, new String[]{"onInventoryChanged"});
            anchor = this.first(m_onInventoryChanged);
            LabelNode oicContinueNode = new LabelNode();
            InsnList oicInsert = new InsnList();
            oicInsert.add(new VarInsnNode(25, 0));
            oicInsert.add(new FieldInsnNode(180, "com/lycanitesmobs/core/inventory/InventoryCreature", deathsave, deathsave_T));
            oicInsert.add(new InsnNode(5));
            oicInsert.add(new JumpInsnNode(161, oicContinueNode));
            oicInsert.add(new InsnNode(177));
            oicInsert.add(oicContinueNode);
            this.insert(m_onInventoryChanged, anchor, oicInsert);
         }
      });
   }
}
