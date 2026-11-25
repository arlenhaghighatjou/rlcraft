package svenhjol.charm.base;

import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import svenhjol.meson.asm.ClassNameMap;
import svenhjol.meson.asm.MesonClassTransformer;

public class CharmClassTransformer extends MesonClassTransformer {
   private static final String ASM_HOOKS = "svenhjol/charm/base/ASMHooks";
   private static int countTransformTileEntityBeacon;
   private static int countTransformISpecialArmor;
   private static int countTransformContainerRepair;

   public CharmClassTransformer() {
   }

   private static byte[] transformContainerFurnace(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"ContainerFurnace"})) {
         return basicClass;
      } else {
         log("Transforming ContainerFurnace");
         MesonClassTransformer.MethodSignature init = new MesonClassTransformer.MethodSignature("<init>", "<init>", "", "(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/inventory/IInventory;)V");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(init, combine((node) -> {
            return node.getOpcode() == 187;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new TypeInsnNode(187, "svenhjol/charm/tweaks/inventory/SlotFurnaceInput"));
            method.instructions.insert(node, newInstructions);
            method.instructions.remove(node);
            return true;
         }))});
         transClass = transform(transClass, new Pair[]{Pair.of(init, combine((node) -> {
            return node.getOpcode() == 183 && node.getNext().getOpcode() == 182;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new MethodInsnNode(183, "svenhjol/charm/tweaks/inventory/SlotFurnaceInput", "<init>", "(Lnet/minecraft/inventory/IInventory;III)V", false));
            method.instructions.insert(node, newInstructions);
            method.instructions.remove(node);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformTileEntityFurnace(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"TileEntityFurnace"})) {
         return basicClass;
      } else {
         log("Transforming TileEntityFurnace");
         MesonClassTransformer.MethodSignature smeltItem = new MesonClassTransformer.MethodSignature("smeltItem", "smeltItem", "o", "()V");
         MesonClassTransformer.MethodSignature isItemValidForSlot = new MesonClassTransformer.MethodSignature("isItemValidForSlot", "isItemValidForSlot", "b", "(ILnet/minecraft/item/ItemStack;)Z");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(smeltItem, combine((node) -> {
            return node.getOpcode() == 58 && ((VarInsnNode)node).var == 2;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(25, 1));
            newInstructions.add(new VarInsnNode(25, 2));
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "changeSmeltingResult", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", false));
            newInstructions.add(new VarInsnNode(58, 2));
            method.instructions.insert(node, newInstructions);
            return true;
         }))});
         transClass = transform(transClass, new Pair[]{Pair.of(isItemValidForSlot, combine((node) -> {
            return node.getOpcode() == 21 && ((VarInsnNode)node).var == 1;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(21, 1));
            LabelNode label = new LabelNode();
            newInstructions.add(new JumpInsnNode(154, label));
            newInstructions.add(new VarInsnNode(25, 2));
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "canSmelt", "(Lnet/minecraft/item/ItemStack;)Z", false));
            newInstructions.add(new InsnNode(172));
            newInstructions.add(label);
            method.instructions.insertBefore(node, newInstructions);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformItemChorusFruit(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"ItemChorusFruit"})) {
         return basicClass;
      } else {
         log("Transforming ItemChorusFruit");
         MesonClassTransformer.MethodSignature onItemUseFinish = new MesonClassTransformer.MethodSignature("onItemUseFinish", "onItemUseFinish", "a", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/item/ItemStack;");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(onItemUseFinish, combine((node) -> {
            return node.getOpcode() == 154;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(25, 2));
            newInstructions.add(new VarInsnNode(25, 3));
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "onChorusFruitEaten", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;)Z", false));
            newInstructions.add(new JumpInsnNode(154, ((JumpInsnNode)node).label));
            method.instructions.insert(node, newInstructions);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformItemToolMaterial(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"ItemToolMaterial"})) {
         return basicClass;
      } else {
         log("Transforming ItemToolMaterial");
         MesonClassTransformer.MethodSignature getMaxUses = new MesonClassTransformer.MethodSignature("getMaxUses", "getMaxUses", "a", "()I");
         MesonClassTransformer.MethodSignature getEfficiency = new MesonClassTransformer.MethodSignature("getEfficiency", "getEfficiency", "b", "()F");
         MesonClassTransformer.MethodSignature getAttackDamage = new MesonClassTransformer.MethodSignature("getAttackDamage", "getAttackDamage", "c", "()F");
         MesonClassTransformer.MethodSignature getHarvestLevel = new MesonClassTransformer.MethodSignature("getHarvestLevel", "getHarvestLevel", "d", "()I");
         MesonClassTransformer.MethodSignature getEnchantability = new MesonClassTransformer.MethodSignature("getEnchantability", "getEnchantability", "e", "()I");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(getMaxUses, combine((node) -> {
            return node.getOpcode() == 180;
         }, (method, node) -> {
            InsnList before = new InsnList();
            InsnList after = new InsnList();
            before.add(new VarInsnNode(25, 0));
            method.instructions.insertBefore(node, before);
            after.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "getToolMaterialMaxUses", "(Lnet/minecraft/item/Item$ToolMaterial;I)I", false));
            method.instructions.insert(node, after);
            return true;
         }))});
         transClass = transform(transClass, new Pair[]{Pair.of(getEfficiency, combine((node) -> {
            return node.getOpcode() == 180;
         }, (method, node) -> {
            InsnList before = new InsnList();
            InsnList after = new InsnList();
            before.add(new VarInsnNode(25, 0));
            method.instructions.insertBefore(node, before);
            after.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "getToolMaterialEfficiency", "(Lnet/minecraft/item/Item$ToolMaterial;F)F", false));
            method.instructions.insert(node, after);
            return true;
         }))});
         transClass = transform(transClass, new Pair[]{Pair.of(getAttackDamage, combine((node) -> {
            return node.getOpcode() == 180;
         }, (method, node) -> {
            InsnList before = new InsnList();
            InsnList after = new InsnList();
            before.add(new VarInsnNode(25, 0));
            method.instructions.insertBefore(node, before);
            after.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "getToolMaterialAttackDamage", "(Lnet/minecraft/item/Item$ToolMaterial;F)F", false));
            method.instructions.insert(node, after);
            return true;
         }))});
         transClass = transform(transClass, new Pair[]{Pair.of(getHarvestLevel, combine((node) -> {
            return node.getOpcode() == 180;
         }, (method, node) -> {
            InsnList before = new InsnList();
            InsnList after = new InsnList();
            before.add(new VarInsnNode(25, 0));
            method.instructions.insertBefore(node, before);
            after.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "getToolMaterialHarvestLevel", "(Lnet/minecraft/item/Item$ToolMaterial;I)I", false));
            method.instructions.insert(node, after);
            return true;
         }))});
         transClass = transform(transClass, new Pair[]{Pair.of(getEnchantability, combine((node) -> {
            return node.getOpcode() == 180;
         }, (method, node) -> {
            InsnList before = new InsnList();
            InsnList after = new InsnList();
            before.add(new VarInsnNode(25, 0));
            method.instructions.insertBefore(node, before);
            after.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "getToolMaterialEnchantability", "(Lnet/minecraft/item/Item$ToolMaterial;I)I", false));
            method.instructions.insert(node, after);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformTileEntityBeacon(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"TileEntityBeacon"})) {
         return basicClass;
      } else {
         log("Transforming TileEntityBeacon");
         MesonClassTransformer.MethodSignature addEffectsToPlayers = new MesonClassTransformer.MethodSignature("addEffectsToPlayers", "addEffectsToPlayers", "E", "()V");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(addEffectsToPlayers, combine((node) -> {
            return node.getOpcode() == 25 && ((VarInsnNode)node).var == 9 && ++countTransformTileEntityBeacon == 2;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(25, 0));
            newInstructions.add(new FieldInsnNode(180, "net/minecraft/tileentity/TileEntityBeacon", obf() ? "world" : "world", "Lnet/minecraft/world/World;"));
            newInstructions.add(new VarInsnNode(25, 8));
            newInstructions.add(new VarInsnNode(25, 0));
            newInstructions.add(new FieldInsnNode(180, "net/minecraft/tileentity/TileEntityBeacon", obf() ? "primaryEffect" : "primaryEffect", "Lnet/minecraft/potion/Potion;"));
            newInstructions.add(new VarInsnNode(25, 0));
            newInstructions.add(new FieldInsnNode(180, "net/minecraft/tileentity/TileEntityBeacon", obf() ? "secondaryEffect" : "secondaryEffect", "Lnet/minecraft/potion/Potion;"));
            newInstructions.add(new VarInsnNode(21, 4));
            newInstructions.add(new VarInsnNode(21, 3));
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "addBeaconEffect", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/AxisAlignedBB;Lnet/minecraft/potion/Potion;Lnet/minecraft/potion/Potion;II)V", false));
            method.instructions.insertBefore(node, newInstructions);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformTileEntityShulkerBox(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"TileEntityShulkerBox"})) {
         return basicClass;
      } else {
         log("Transforming TileEntityShulkerBox");
         MesonClassTransformer.MethodSignature canInsertItem = new MesonClassTransformer.MethodSignature("canInsertItem", "canInsertItem", "a", "(ILnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumFacing;)Z");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(canInsertItem, combine((node) -> {
            return node.getOpcode() == 25 && ((VarInsnNode)node).var == 2;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(25, 2));
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "canInsertItemIntoShulkerBox", "(Lnet/minecraft/item/ItemStack;)Z", false));
            newInstructions.add(new InsnNode(172));
            method.instructions = newInstructions;
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformSlotShulkerBox(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"SlotShulkerBox"})) {
         return basicClass;
      } else {
         log("Transforming SlotShulkerBox");
         MesonClassTransformer.MethodSignature isItemValid = new MesonClassTransformer.MethodSignature("isItemValid", "canInsertItem", "a", "(Lnet/minecraft/item/ItemStack;)Z");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(isItemValid, combine((node) -> {
            return node.getOpcode() == 25 && ((VarInsnNode)node).var == 1;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(25, 1));
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "canInsertItemIntoShulkerBox", "(Lnet/minecraft/item/ItemStack;)Z", false));
            newInstructions.add(new InsnNode(172));
            method.instructions = newInstructions;
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformItemHandlerHelper(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"ItemHandlerHelper"})) {
         return basicClass;
      } else {
         log("Transforming ItemHandlerHelper");
         MesonClassTransformer.MethodSignature insertItem = new MesonClassTransformer.MethodSignature("insertItem", "insertItem", "", "(Lnet/minecraftforge/items/IItemHandler;Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/item/ItemStack;");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(insertItem, combine((node) -> {
            return node.getOpcode() == 25 && ((VarInsnNode)node).var == 0;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(25, 0));
            newInstructions.add(new VarInsnNode(25, 1));
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "canInsertItemIntoShulkerBox", "(Lnet/minecraftforge/items/IItemHandler;Lnet/minecraft/item/ItemStack;)Z", false));
            LabelNode label = new LabelNode();
            newInstructions.add(new JumpInsnNode(154, label));
            newInstructions.add(new VarInsnNode(25, 1));
            newInstructions.add(new InsnNode(176));
            newInstructions.add(label);
            method.instructions.insertBefore(node, newInstructions);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformISpecialArmor(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"ISpecialArmor"})) {
         return basicClass;
      } else {
         log("Transforming ISpecialArmor");
         MesonClassTransformer.MethodSignature applyArmor = new MesonClassTransformer.MethodSignature("applyArmor", "applyArmor", "", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(applyArmor, combine((node) -> {
            return node.getOpcode() == 25 && ((VarInsnNode)node).var == 1 && node.getNext().getOpcode() == 21 && ((VarInsnNode)node.getNext()).var == 12 && ++countTransformISpecialArmor == 2;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(25, 0));
            newInstructions.add(new VarInsnNode(25, 1));
            newInstructions.add(new VarInsnNode(21, 12));
            newInstructions.add(new MethodInsnNode(182, "net/minecraft/util/NonNullList", "get", "(I)Ljava/lang/Object;", false));
            newInstructions.add(new TypeInsnNode(192, "net/minecraft/item/ItemStack"));
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "canArmorBeSalvaged", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;)V", false));
            method.instructions.insertBefore(node, newInstructions);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformBrewingRecipeRegistry(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"BrewingRecipeRegistry"})) {
         return basicClass;
      } else {
         log("Transforming BrewingRecipeRegistry");
         MesonClassTransformer.MethodSignature isValidInput = new MesonClassTransformer.MethodSignature("isValidInput", "isValidInput", "", "(Lnet/minecraft/item/ItemStack;)Z");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(isValidInput, combine((node) -> {
            return node.getOpcode() == 159;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new JumpInsnNode(162, ((JumpInsnNode)node).label));
            method.instructions.insertBefore(node, newInstructions);
            method.instructions.remove(node);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformContainerRepair(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"ContainerRepair"})) {
         return basicClass;
      } else {
         log("Transforming ContainerRepair");
         MesonClassTransformer.MethodSignature updateRepairOutput = new MesonClassTransformer.MethodSignature("updateRepairOutput", "updateRepairOutput", "e", "()V");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(updateRepairOutput, combine((node) -> {
            return node.getOpcode() == 3 && node.getPrevious().getOpcode() == 25 && ((VarInsnNode)node.getPrevious()).var == 0 && ++countTransformContainerRepair == 2;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new InsnNode(4));
            method.instructions.insert(node, newInstructions);
            method.instructions.remove(node);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformContainerRepair2(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"ContainerRepair2"})) {
         return basicClass;
      } else {
         log("Transforming ContainerRepair2");
         MesonClassTransformer.MethodSignature canTakeStack = new MesonClassTransformer.MethodSignature("canTakeStack", "canTakeStack", "a", "(Lnet/minecraft/entity/player/EntityPlayer;)Z");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(canTakeStack, combine((node) -> {
            return node.getOpcode() == 158;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new JumpInsnNode(155, ((JumpInsnNode)node).label));
            method.instructions.insertBefore(node, newInstructions);
            method.instructions.remove(node);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformLayerArmorBase(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"LayerArmorBase"})) {
         return basicClass;
      } else {
         log("Transforming LayerArmorBase");
         MesonClassTransformer.MethodSignature renderArmorLayer = new MesonClassTransformer.MethodSignature("renderArmorLayer", "renderArmorLayer", "a", "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFFLnet/minecraft/inventory/EntityEquipmentSlot;)V");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(renderArmorLayer, combine((node) -> {
            return node.getOpcode() == 58;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(25, 1));
            newInstructions.add(new VarInsnNode(25, 10));
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "isArmorInvisible", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Z", false));
            LabelNode label = new LabelNode();
            newInstructions.add(new JumpInsnNode(153, label));
            newInstructions.add(new InsnNode(177));
            newInstructions.add(label);
            method.instructions.insert(node, newInstructions);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformEntityPlayer(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"EntityPlayer"})) {
         return basicClass;
      } else {
         log("Transforming EntityPlayer");
         MesonClassTransformer.MethodSignature getArmorVisibility = new MesonClassTransformer.MethodSignature("getArmorVisibility", "getArmorVisibility", "cW", "()F");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(getArmorVisibility, combine((node) -> {
            return node.getOpcode() == 132;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(25, 0));
            newInstructions.add(new VarInsnNode(25, 3));
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "isArmorInvisible", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Z", false));
            LabelNode label = new LabelNode();
            newInstructions.add(new JumpInsnNode(154, label));
            newInstructions.add(new IincInsnNode(1, 1));
            newInstructions.add(label);
            method.instructions.insertBefore(node, newInstructions);
            method.instructions.remove(node);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformEntityWither(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"EntityWither"})) {
         return basicClass;
      } else {
         log("Transforming EntityWither");
         MesonClassTransformer.MethodSignature canDestroyBlock = new MesonClassTransformer.MethodSignature("canDestroyBlock", "canDestroyBlock", "a", "(Lnet/minecraft/block/Block;)Z");
         byte[] transClass = transform(basicClass, new Pair[]{Pair.of(canDestroyBlock, combine((node) -> {
            return node.getOpcode() == 25 && ((VarInsnNode)node).var == 0;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(25, 0));
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "canWitherDestroyBlock", "(Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;", false));
            newInstructions.add(new VarInsnNode(58, 0));
            method.instructions.insertBefore(node, newInstructions);
            return true;
         }))});
         return transClass;
      }
   }

   private static byte[] transformStructureVillagePiecesVillage(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"StructureVillagePieces"})) {
         return basicClass;
      } else {
         log("Transforming StructureVillagePieces$Village");
         MesonClassTransformer.MethodSignature biomeDoor = new MesonClassTransformer.MethodSignature("biomeDoor", "biomeDoor", "i", "()Lnet/minecraft/block/BlockDoor;");
         return transform(basicClass, new Pair[]{Pair.of(biomeDoor, combine((node) -> {
            return node.getOpcode() == 25;
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new VarInsnNode(25, 0));
            newInstructions.add(new FieldInsnNode(180, "net/minecraft/world/gen/structure/StructureVillagePieces$Village", "startPiece", "Lnet/minecraft/world/gen/structure/StructureVillagePieces$Start;"));
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "villageDoorsForBiome", "(Lnet/minecraft/world/gen/structure/StructureVillagePieces$Start;)Lnet/minecraft/block/BlockDoor;", false));
            newInstructions.add(new InsnNode(176));
            method.instructions = newInstructions;
            return true;
         }))});
      }
   }

   private static byte[] transformStructureStart(byte[] basicClass) {
      if (!checkTransformers(CharmLoadingPlugin.configAsm, new String[]{"StructureStart"})) {
         return basicClass;
      } else {
         log("Transforming StructureStart");
         MesonClassTransformer.MethodSignature generateStructure = new MesonClassTransformer.MethodSignature("generateStructure", "generateStructure", "a", "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)V");
         byte[] transformClass = transform(basicClass, new Pair[]{Pair.of(generateStructure, combine((node) -> {
            return node.getOpcode() == 182 && checkDesc(((MethodInsnNode)node).desc, "(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)Z");
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new MethodInsnNode(184, "svenhjol/charm/base/ASMHooks", "addComponentParts", "(Lnet/minecraft/world/gen/structure/StructureComponent;Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/world/gen/structure/StructureBoundingBox;)Z", false));
            method.instructions.insert(node, newInstructions);
            method.instructions.remove(node);
            return true;
         }))});
         return transformClass;
      }
   }

   static {
      CLASS_MAPPINGS = new ClassNameMap(new String[]{"net/minecraft/util/NonNullList", "fi", "net/minecraft/block/BlockDoor", "aqa", "net/minecraft/client/renderer/entity/layers/LayerArmorBase", "cbp", "net/minecraft/entity/Entity", "vg", "net/minecraft/entity/EntityLivingBase", "vp", "net/minecraft/entity/boss/EntityWither", "abx", "net/minecraft/entity/player/EntityPlayer", "aed", "net/minecraft/entity/player/EntityPlayer$SleepResult", "aed$a", "net/minecraft/entity/player/EntityPlayerMP", "oq", "net/minecraft/entity/player/InventoryPlayer", "aec", "net/minecraft/init/Blocks", "aox", "net/minecraft/inventory/ContainerBrewingStand", "afu", "net/minecraft/inventory/ContainerBrewingStand$Potion", "afu$c", "net/minecraft/inventory/ContainerFurnace", "agd", "net/minecraft/inventory/ContainerRepair", "afs", "net/minecraft/inventory/ContainerRepair$2", "afs$2", "net/minecraft/inventory/EntityEquipmentSlot", "vl", "net/minecraft/inventory/IInventory", "tv", "net/minecraft/inventory/SlotShulkerBox", "agq", "net/minecraft/item/Item$ToolMaterial", "ain$a", "net/minecraft/item/ItemChorusFruit", "ahk", "net/minecraft/potion/Potion", "uz", "net/minecraft/tileentity/TileEntityBeacon", "avh", "net/minecraft/tileentity/TileEntityFurnace", "avu", "net/minecraft/tileentity/TileEntityShulkerBox", "awb", "net/minecraft/util/math/AxisAlignedBB", "bhb", "net/minecraft/util/math/BlockPos", "et", "net/minecraft/world/gen/structure/MapGenVillage$Start", "bca$a", "net/minecraft/world/gen/structure/StructureBoundingBox", "bbg", "net/minecraft/world/gen/structure/StructureComponent", "bbx", "net/minecraft/world/gen/structure/StructureStart", "bby", "net/minecraft/world/gen/structure/StructureVillagePieces$Start", "bcb$k", "net/minecraft/world/gen/structure/StructureVillagePieces$Village", "bcb$n", "net/minecraft/world/World", "amu", "net/minecraft/util/DamageSource", "ur", "net/minecraft/item/ItemStack", "aip"});
      transformers.put("net.minecraft.item.Item$ToolMaterial", CharmClassTransformer::transformItemToolMaterial);
      transformers.put("net.minecraftforge.items.ItemHandlerHelper", CharmClassTransformer::transformItemHandlerHelper);
      transformers.put("net.minecraftforge.common.ISpecialArmor$ArmorProperties", CharmClassTransformer::transformISpecialArmor);
      transformers.put("net.minecraftforge.common.brewing.BrewingRecipeRegistry", CharmClassTransformer::transformBrewingRecipeRegistry);
      transformers.put("net.minecraft.inventory.ContainerFurnace", CharmClassTransformer::transformContainerFurnace);
      transformers.put("net.minecraft.inventory.ContainerRepair", CharmClassTransformer::transformContainerRepair);
      transformers.put("net.minecraft.inventory.ContainerRepair$2", CharmClassTransformer::transformContainerRepair2);
      transformers.put("net.minecraft.inventory.SlotShulkerBox", CharmClassTransformer::transformSlotShulkerBox);
      transformers.put("net.minecraft.entity.boss.EntityWither", CharmClassTransformer::transformEntityWither);
      transformers.put("net.minecraft.entity.player.EntityPlayer", CharmClassTransformer::transformEntityPlayer);
      transformers.put("net.minecraft.item.ItemChorusFruit", CharmClassTransformer::transformItemChorusFruit);
      transformers.put("net.minecraft.client.renderer.entity.layers.LayerArmorBase", CharmClassTransformer::transformLayerArmorBase);
      transformers.put("net.minecraft.world.gen.structure.StructureStart", CharmClassTransformer::transformStructureStart);
      transformers.put("net.minecraft.world.gen.structure.StructureVillagePieces$Village", CharmClassTransformer::transformStructureVillagePiecesVillage);
      transformers.put("net.minecraft.tileentity.TileEntityBeacon", CharmClassTransformer::transformTileEntityBeacon);
      transformers.put("net.minecraft.tileentity.TileEntityFurnace", CharmClassTransformer::transformTileEntityFurnace);
      transformers.put("net.minecraft.tileentity.TileEntityShulkerBox", CharmClassTransformer::transformTileEntityShulkerBox);
   }
}
