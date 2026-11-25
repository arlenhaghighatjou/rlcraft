package vazkii.quark.base.asm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.util.TraceMethodVisitor;

public class ClassTransformer implements IClassTransformer, Opcodes {
   private static final String ASM_HOOKS = "vazkii/quark/base/asm/ASMHooks";
   private static final Map<String, ClassTransformer.Transformer> transformers = new HashMap();
   private static boolean debugLog = false;

   public ClassTransformer() {
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      if (transformers.containsKey(transformedName)) {
         log("Transforming " + transformedName);
         return (byte[])((ClassTransformer.Transformer)transformers.get(transformedName)).apply(basicClass);
      } else {
         return basicClass;
      }
   }

   private static byte[] transformModelBiped(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("setRotationAngles", "setRotationAngles", "(FFFFFFLnet/minecraft/entity/Entity;)V");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return node.getOpcode() == 177;
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 7));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "updateEmotes", "(Lnet/minecraft/entity/Entity;)V", false));
         method.instructions.insertBefore(node, newInstructions);
         return true;
      })));
   }

   private static byte[] transformRenderItem(byte[] basicClass) {
      ClassTransformer.MethodSignature sig1 = new ClassTransformer.MethodSignature("renderItem", "renderItem", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V");
      ClassTransformer.MethodSignature sig2 = new ClassTransformer.MethodSignature("renderEffect", "renderEffect", "(Lnet/minecraft/client/renderer/block/model/IBakedModel;)V");
      byte[] transClass = transform(basicClass, forMethod(sig1, (method) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "setColorRuneTargetStack", "(Lnet/minecraft/item/ItemStack;)V", false));
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }), forMethod(sig2, combine((node) -> {
         return node.getOpcode() == 18 && ((LdcInsnNode)node).cst.equals(Integer.valueOf(-8372020));
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "getRuneColor", "(I)I", false));
         method.instructions.insert(node, newInstructions);
         return false;
      })));
      return transClass;
   }

   private static byte[] transformLayerArmorBase(byte[] basicClass) {
      ClassTransformer.MethodSignature sig1 = new ClassTransformer.MethodSignature("renderArmorLayer", "renderArmorLayer", "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFFLnet/minecraft/inventory/EntityEquipmentSlot;)V");
      ClassTransformer.MethodSignature sig2 = new ClassTransformer.MethodSignature("renderEnchantedGlint", "renderEnchantedGlint", "(Lnet/minecraft/client/renderer/entity/RenderLivingBase;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/model/ModelBase;FFFFFFF)V");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("color", "color", "(FFFF)V");
      byte[] transClass = transform(basicClass, forMethod(sig1, (method) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new VarInsnNode(25, 9));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "setColorRuneTargetStack", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/inventory/EntityEquipmentSlot;)V", false));
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }));
      if (!hasOptifine(sig2.toString())) {
         transClass = transform(transClass, forMethod(sig2, combine((node) -> {
            return node.getOpcode() == 184 && target.matches((MethodInsnNode)node);
         }, (method, node) -> {
            InsnList newInstructions = new InsnList();
            newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "applyRuneColor", "()V", false));
            method.instructions.insert(node, newInstructions);
            return false;
         })));
      }

      return transClass;
   }

   private static byte[] transformEntityBoat(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("attackEntityFrom", "attackEntityFrom", "(Lnet/minecraft/util/DamageSource;F)Z");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("dropItemWithOffset", "dropItemWithOffset", "(Lnet/minecraft/item/Item;IF)Lnet/minecraft/entity/item/EntityItem;");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return node.getOpcode() == 182 && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "boatDrops", "(Lnet/minecraft/entity/item/EntityBoat;)V", false));
         method.instructions.insert(node, newInstructions);
         return true;
      })));
   }

   private static byte[] transformEntityMinecart(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("killMinecart", "killMinecart", "(Lnet/minecraft/util/DamageSource;)V");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("entityDropItem", "entityDropItem", "(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/item/EntityItem;");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return node.getOpcode() == 182 && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "minecartDrops", "(Lnet/minecraft/entity/item/EntityMinecart;)V", false));
         method.instructions.insert(node, newInstructions);
         return true;
      })));
   }

   private static byte[] transformRenderManager(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("renderEntity", "renderEntity", "(Lnet/minecraft/entity/Entity;DDDFFZ)V");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("doRender", "doRender", "(Lnet/minecraft/entity/Entity;DDDFF)V");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return node.getOpcode() == 182 && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 11));
         newInstructions.add(new VarInsnNode(24, 2));
         newInstructions.add(new VarInsnNode(24, 4));
         newInstructions.add(new VarInsnNode(24, 6));
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new VarInsnNode(23, 9));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "renderChain", "(Lnet/minecraft/client/renderer/entity/Render;DDDLnet/minecraft/entity/Entity;F)V", false));
         method.instructions.insert(node, newInstructions);
         return true;
      })));
   }

   private static byte[] transformRenderBoat(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("doRender", "renderMultipass", "(Lnet/minecraft/entity/item/EntityBoat;DDDFF)V");
      ClassTransformer.MethodSignature targetA = new ClassTransformer.MethodSignature("render", "render", "(Lnet/minecraft/entity/Entity;FFFFFF)V");
      ClassTransformer.MethodSignature targetB = new ClassTransformer.MethodSignature("renderMultipass", "renderMultipass", "(Lnet/minecraft/entity/Entity;FFFFFF)V");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return (node.getOpcode() == 182 || node.getOpcode() == 185) && (targetA.matches((MethodInsnNode)node) || targetB.matches((MethodInsnNode)node));
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new VarInsnNode(23, 9));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "renderBannerOnBoat", "(Lnet/minecraft/entity/item/EntityBoat;F)V", false));
         method.instructions.insert(node, newInstructions);
         return true;
      })));
   }

   private static byte[] transformBlockPistonBase(byte[] basicClass) {
      ClassTransformer.MethodSignature sig1 = new ClassTransformer.MethodSignature("canPush", "canPush", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;ZLnet/minecraft/util/EnumFacing;)Z");
      ClassTransformer.MethodSignature sig2 = new ClassTransformer.MethodSignature("doMove", "doMove", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Z)Z");
      ClassTransformer.MethodSignature sig3 = new ClassTransformer.MethodSignature("checkForMove", "checkForMove", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V");
      String targetClazz = "net/minecraft/block/state/BlockPistonStructureHelper";
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("hasTileEntity", "", "(Lnet/minecraft/block/state/IBlockState;)Z");
      ClassTransformer.MethodSignature target2 = new ClassTransformer.MethodSignature("getBlocksToMove", "getBlocksToMove", "()Ljava/util/List;");
      ClassTransformer.MethodSignature target3 = new ClassTransformer.MethodSignature("<init>", "", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Z)V");
      return transform(basicClass, forMethod(sig1, combine((node) -> {
         return node.getOpcode() == 182 && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "shouldPistonMoveTE", "(ZLnet/minecraft/block/state/IBlockState;)Z", false));
         method.instructions.insert(node, newInstructions);
         return true;
      })), forMethod(sig2, combine((node) -> {
         return node.getOpcode() == 182 && ((MethodInsnNode)node).owner.equals(targetClazz) && target2.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 5));
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new VarInsnNode(25, 3));
         newInstructions.add(new VarInsnNode(21, 4));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "postPistonPush", "(Lnet/minecraft/block/state/BlockPistonStructureHelper;Lnet/minecraft/world/World;Lnet/minecraft/util/EnumFacing;Z)V", false));
         method.instructions.insertBefore(node, newInstructions);
         return true;
      })), forMethod(sig2, combine((node) -> {
         return node.getOpcode() == 183 && ((MethodInsnNode)node).owner.equals(targetClazz) && target3.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new VarInsnNode(25, 2));
         newInstructions.add(new VarInsnNode(25, 3));
         newInstructions.add(new VarInsnNode(21, 4));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "transformStructureHelper", "(Lnet/minecraft/block/state/BlockPistonStructureHelper;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Z)Lnet/minecraft/block/state/BlockPistonStructureHelper;", false));
         method.instructions.insert(node, newInstructions);
         return true;
      })), forMethod(sig3, combine((node) -> {
         return node.getOpcode() == 183 && ((MethodInsnNode)node).owner.equals(targetClazz) && target3.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new VarInsnNode(25, 2));
         newInstructions.add(new VarInsnNode(25, 4));
         newInstructions.add(new InsnNode(4));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "transformStructureHelper", "(Lnet/minecraft/block/state/BlockPistonStructureHelper;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Z)Lnet/minecraft/block/state/BlockPistonStructureHelper;", false));
         method.instructions.insert(node, newInstructions);
         return true;
      })));
   }

   private static byte[] transformContainerWorkbench(byte[] basicClass) {
      return transformTransferStackInSlot(basicClass, "getMinInventoryBoundaryCrafting", "getMaxInventoryBoundaryCrafting");
   }

   private static byte[] transformContainerMerchant(byte[] basicClass) {
      return transformTransferStackInSlot(basicClass, "getMinInventoryBoundaryVillager", "getMaxInventoryBoundaryVillager");
   }

   private static byte[] transformTransferStackInSlot(byte[] basicClass, String firstHook, String secondHook) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("transferStackInSlot", "transferStackInSlot", "(Lnet/minecraft/entity/player/EntityPlayer;I)Lnet/minecraft/item/ItemStack;");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("mergeItemStack", "mergeItemStack", "(Lnet/minecraft/item/ItemStack;IIZ)Z");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return node.getOpcode() == 182 && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new InsnNode(91));
         newInstructions.add(new InsnNode(87));
         newInstructions.add(new InsnNode(92));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", firstHook, "(II)I", false));
         newInstructions.add(new InsnNode(91));
         newInstructions.add(new InsnNode(87));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", secondHook, "(II)I", false));
         newInstructions.add(new InsnNode(91));
         newInstructions.add(new InsnNode(87));
         newInstructions.add(new InsnNode(91));
         newInstructions.add(new InsnNode(87));
         method.instructions.insertBefore(node, newInstructions);
         return false;
      })));
   }

   private static byte[] transformTileEntityPiston(byte[] basicClass) {
      ClassTransformer.MethodSignature clearPistonTileEntitySig = new ClassTransformer.MethodSignature("clearPistonTileEntity", "clearPistonTileEntity", "()V");
      ClassTransformer.MethodSignature updateSig = new ClassTransformer.MethodSignature("update", "update", "()V");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("setBlockState", "setBlockState", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z");
      ClassTransformer.MethodAction setPistonBlockAction = combine((node) -> {
         return node.getOpcode() == 182 && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "setPistonBlock", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z", false));
         method.instructions.insert(node, newInstructions);
         method.instructions.remove(node);
         return true;
      });
      ClassTransformer.MethodAction onUpdateAction = (method) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "onPistonUpdate", "(Lnet/minecraft/tileentity/TileEntityPiston;)V", false));
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      };
      byte[] transClass = transform(basicClass, forMethod(updateSig, onUpdateAction));
      transClass = transform(transClass, forMethod(clearPistonTileEntitySig, setPistonBlockAction));
      transClass = transform(transClass, forMethod(updateSig, setPistonBlockAction));
      return transClass;
   }

   private static byte[] transformTileEntityPistonRenderer(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("render", "render", "(Lnet/minecraft/tileentity/TileEntityPiston;DDDFIF)V");
      new ClassTransformer.MethodSignature("draw", "draw", "()V");
      return transform(basicClass, forMethod(sig, (method) -> {
         InsnList newInstructions = new InsnList();
         LabelNode escape = new LabelNode();
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new VarInsnNode(24, 2));
         newInstructions.add(new VarInsnNode(24, 4));
         newInstructions.add(new VarInsnNode(24, 6));
         newInstructions.add(new VarInsnNode(23, 8));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "renderPistonBlock", "(Lnet/minecraft/tileentity/TileEntityPiston;DDDF)Z", false));
         newInstructions.add(new JumpInsnNode(153, escape));
         newInstructions.add(new InsnNode(177));
         newInstructions.add(escape);
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }));
   }

   private static byte[] transformWorldServer(byte[] basicClass) {
      ClassTransformer.MethodSignature sig1 = new ClassTransformer.MethodSignature("areAllPlayersAsleep", "areAllPlayersAsleep", "()Z");
      ClassTransformer.MethodSignature sig2 = new ClassTransformer.MethodSignature("wakeAllPlayers", "wakeAllPlayers", "()V");
      return transform(basicClass, forMethod(sig1, (method) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "isEveryoneAsleep", "(Lnet/minecraft/world/World;)I", false));
         newInstructions.add(new InsnNode(89));
         LabelNode label = new LabelNode();
         newInstructions.add(new JumpInsnNode(153, label));
         newInstructions.add(new InsnNode(4));
         newInstructions.add(new InsnNode(100));
         newInstructions.add(new InsnNode(172));
         newInstructions.add(label);
         newInstructions.add(new InsnNode(87));
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }), forMethod(sig2, (method) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "whenPlayersWake", "(Lnet/minecraft/world/WorldServer;)V", false));
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }));
   }

   private static byte[] transformBlockModelRenderer(byte[] basicClass) {
      ClassTransformer.MethodSignature sig1 = new ClassTransformer.MethodSignature("renderQuadsFlat", "renderQuadsFlat", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;IZLnet/minecraft/client/renderer/BufferBuilder;Ljava/util/List;Ljava/util/BitSet;)V");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("putPosition", "putPosition", "(DDD)V");
      return hasOptifine(sig1.toString()) ? basicClass : transform(basicClass, forMethod(sig1, combine((node) -> {
         return node.getOpcode() == 182 && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new VarInsnNode(25, 2));
         newInstructions.add(new VarInsnNode(25, 3));
         newInstructions.add(new VarInsnNode(25, 6));
         newInstructions.add(new VarInsnNode(25, 18));
         newInstructions.add(new VarInsnNode(21, 4));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "putColorsFlat", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/client/renderer/block/model/BakedQuad;I)V", false));
         method.instructions.insertBefore(node, newInstructions);
         return true;
      })));
   }

   private static byte[] transformRecipeAddPattern(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("matches", "matches", "(Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/world/World;)Z");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("getPatterns", "getPatterns", "(Lnet/minecraft/item/ItemStack;)I");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return node.getOpcode() == 184 && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "shiftLayerCountDown", "(I)I", false));
         method.instructions.insert(node, newInstructions);
         return true;
      })));
   }

   private static byte[] transformItemBanner(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("appendHoverTextFromTileEntityTag", "appendHoverTextFromTileEntityTag", "(Lnet/minecraft/item/ItemStack;Ljava/util/List;)V");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return node.getOpcode() == 16 && ((IntInsnNode)node).operand == 6;
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "shiftLayerCountUp", "(I)I", false));
         method.instructions.insert(node, newInstructions);
         return true;
      })));
   }

   private static byte[] transformRender(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("renderEntityOnFire", "renderEntityOnFire", "(Lnet/minecraft/entity/Entity;DDDF)V");
      return transform(basicClass, forMethod(sig, (method) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new VarInsnNode(24, 2));
         newInstructions.add(new VarInsnNode(24, 4));
         newInstructions.add(new VarInsnNode(24, 6));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "renderFire", "(Lnet/minecraft/entity/Entity;DDD)Z", false));
         LabelNode label = new LabelNode();
         newInstructions.add(new JumpInsnNode(153, label));
         newInstructions.add(new InsnNode(177));
         newInstructions.add(label);
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }));
   }

   private static byte[] transformEntityAITarget(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("isSuitableTarget", "isSuitableTarget", "(Lnet/minecraft/entity/EntityLiving;Lnet/minecraft/entity/EntityLivingBase;ZZ)Z");
      return transform(basicClass, forMethod(sig, (method) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "hasWitchHat", "(Lnet/minecraft/entity/EntityLiving;Lnet/minecraft/entity/EntityLivingBase;)Z", false));
         LabelNode label = new LabelNode();
         newInstructions.add(new JumpInsnNode(153, label));
         newInstructions.add(new InsnNode(3));
         newInstructions.add(new InsnNode(172));
         newInstructions.add(label);
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }));
   }

   private static byte[] transformGuiContainer(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("drawScreen", "drawScreen", "(IIF)V");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("drawGuiContainerForegroundLayer", "drawGuiContainerForegroundLayer", "(II)V");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return (node.getOpcode() == 182 || node.getOpcode() == 183) && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "drawInvalidSlotOverlays", "(Lnet/minecraft/client/gui/inventory/GuiContainer;)V", false));
         method.instructions.insertBefore(node, newInstructions);
         return false;
      })));
   }

   private static byte[] transformEntity(byte[] basicClass) {
      ClassTransformer.MethodSignature sig1 = new ClassTransformer.MethodSignature("move", "move", "(Lnet/minecraft/entity/MoverType;DDD)V");
      ClassTransformer.MethodSignature sig2 = new ClassTransformer.MethodSignature("onEntityUpdate", "onEntityUpdate", "()V");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("doBlockCollisions", "doBlockCollisions", "()V");
      return transform(basicClass, forMethod(sig1, (method) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "recordMotion", "(Lnet/minecraft/entity/Entity;)V", false));
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }, combine((node) -> {
         return (node.getOpcode() == 182 || node.getOpcode() == 183) && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         int d2 = 14;
         int d3 = 16;
         int d4 = 18;

         for(LocalVariableNode lvn : method.localVariables) {
            String var8 = lvn.name;
            byte var9 = -1;
            switch(var8.hashCode()) {
            case 3150:
               if (var8.equals("d2")) {
                  var9 = 0;
               }
               break;
            case 3151:
               if (var8.equals("d3")) {
                  var9 = 1;
               }
               break;
            case 3152:
               if (var8.equals("d4")) {
                  var9 = 2;
               }
            }

            switch(var9) {
            case 0:
               d2 = lvn.index;
               break;
            case 1:
               d3 = lvn.index;
               break;
            case 2:
               d4 = lvn.index;
            }
         }

         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new VarInsnNode(24, d2));
         newInstructions.add(new VarInsnNode(24, d3));
         newInstructions.add(new VarInsnNode(24, d4));
         newInstructions.add(new VarInsnNode(24, 2));
         newInstructions.add(new VarInsnNode(24, 4));
         newInstructions.add(new VarInsnNode(24, 6));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "applyCollisionLogic", "(Lnet/minecraft/entity/Entity;DDDDDD)V", false));
         method.instructions.insert(node, newInstructions);
         return false;
      })), forMethod(sig2, (method) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "onEntityUpdate", "(Lnet/minecraft/entity/Entity;)V", false));
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }));
   }

   private static byte[] transformEntityItem(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("onUpdate", "onUpdate", "()V");
      return transform(basicClass, forMethod(sig, (method) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "ensureUpdatedItemAge", "(Lnet/minecraft/entity/item/EntityItem;)V", false));
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }));
   }

   private static byte[] transformBeaconButton(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("drawButton", "drawButton", "(Lnet/minecraft/client/Minecraft;IIF)V");
      ClassTransformer.MethodSignature checkAgainst = new ClassTransformer.MethodSignature("", "drawButtonForegroundLayer", "(II)V");
      return transform(basicClass, inject(sig, (method) -> {
         boolean deobf = method.name.equals(sig.srgName);
         InsnList newInstructions = new InsnList();
         LabelNode skipSuper = new LabelNode();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new InsnNode(89));
         newInstructions.add(new FieldInsnNode(180, "net/minecraft/client/gui/inventory/GuiBeacon$PowerButton", deobf ? "this$0" : "field_146150_o", "Lnet/minecraft/client/gui/inventory/GuiBeacon;"));
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new VarInsnNode(21, 2));
         newInstructions.add(new VarInsnNode(21, 3));
         newInstructions.add(new VarInsnNode(23, 4));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "renderBeaconButton", "(Lnet/minecraft/client/gui/GuiButton;Lnet/minecraft/client/gui/inventory/GuiBeacon;Lnet/minecraft/client/Minecraft;IIF)Z", false));
         newInstructions.add(new JumpInsnNode(154, skipSuper));
         newInstructions.add(new InsnNode(177));
         newInstructions.add(skipSuper);
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }, (clazz, method) -> {
         boolean deobf = true;

         for(MethodNode node : clazz.methods) {
            if (checkAgainst.matches(node)) {
               deobf = false;
               break;
            }
         }

         new InsnList();
         Label begin = new Label();
         Label end = new Label();
         Label skipSuper = new Label();
         method.visitParameter("this", 32768);
         method.visitParameter("mc", 32768);
         method.visitParameter("mouseX", 32768);
         method.visitParameter("mouseY", 32768);
         method.visitParameter("partialTicks", 32768);
         method.visitCode();
         method.visitLabel(begin);
         method.visitVarInsn(25, 0);
         method.visitInsn(89);
         method.visitFieldInsn(180, "net/minecraft/client/gui/inventory/GuiBeacon$PowerButton", deobf ? "this$0" : "field_146150_o", "Lnet/minecraft/client/gui/inventory/GuiBeacon;");
         method.visitVarInsn(25, 1);
         method.visitVarInsn(21, 2);
         method.visitVarInsn(21, 3);
         method.visitVarInsn(23, 4);
         method.visitMethodInsn(184, "vazkii/quark/base/asm/ASMHooks", "renderBeaconButton", "(Lnet/minecraft/client/gui/GuiButton;Lnet/minecraft/client/gui/inventory/GuiBeacon;Lnet/minecraft/client/Minecraft;IIF)Z", false);
         method.visitJumpInsn(154, skipSuper);
         method.visitVarInsn(25, 0);
         method.visitVarInsn(25, 1);
         method.visitVarInsn(21, 2);
         method.visitVarInsn(21, 3);
         method.visitVarInsn(23, 4);
         method.visitMethodInsn(183, "net/minecraft/client/gui/inventory/GuiBeacon$Button", deobf ? sig.funcName : sig.srgName, sig.funcDesc, false);
         method.visitLabel(skipSuper);
         method.visitInsn(177);
         method.visitLabel(end);
         method.visitLocalVariable("this", "Lnet/minecraft/client/gui/inventory/GuiBeacon$PowerButton;", (String)null, begin, end, 0);
         method.visitLocalVariable("mc", "Lnet/minecraft/client/Minecraft;", (String)null, begin, end, 1);
         method.visitLocalVariable("mouseX", "I", (String)null, begin, end, 2);
         method.visitLocalVariable("mouseY", "I", (String)null, begin, end, 3);
         method.visitLocalVariable("partialTicks", "F", (String)null, begin, end, 4);
         method.visitMaxs(5, 5);
         method.visitEnd();
         return true;
      }));
   }

   private static byte[] transformEntityRenderer(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("renderWorldPass", "renderWorldPass", "(IFJ)V");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("setupCameraTransform", "setupCameraTransform", "(FI)V");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return (node.getOpcode() == 182 || node.getOpcode() == 183) && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new VarInsnNode(23, 2));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "renderNausea", "(Lnet/minecraft/client/renderer/EntityRenderer;F)V", false));
         method.instructions.insert(node, newInstructions);
         return false;
      })));
   }

   private static byte[] transformSharpness(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("canApply", "canApply", "(Lnet/minecraft/item/ItemStack;)Z");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return node.getOpcode() == 172;
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "canSharpnessApply", "(Lnet/minecraft/item/ItemStack;)Z", false));
         newInstructions.add(new InsnNode(128));
         method.instructions.insertBefore(node, newInstructions);
         return false;
      })));
   }

   private static byte[] transformEnchantment(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("canApply", "canApply", "(Lnet/minecraft/item/ItemStack;)Z");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return node.getOpcode() == 172;
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new VarInsnNode(25, 1));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "canFortuneApply", "(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)Z", false));
         newInstructions.add(new InsnNode(128));
         method.instructions.insertBefore(node, newInstructions);
         return false;
      })));
   }

   private static byte[] transformItemStack(byte[] basicClass) {
      ClassTransformer.MethodSignature sig1 = new ClassTransformer.MethodSignature("getTextComponent", "getTextComponent", "()Lnet/minecraft/util/text/ITextComponent;");
      return transform(basicClass, forMethod(sig1, combine((node) -> {
         return node.getOpcode() == 176;
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "createStackComponent", "(Lnet/minecraft/util/text/ITextComponent;)Lnet/minecraft/util/text/ITextComponent;", false));
         method.instructions.insertBefore(node, newInstructions);
         return false;
      })));
   }

   private static byte[] transformLightUtil(byte[] basicClass) {
      ClassTransformer.MethodSignature sig1 = new ClassTransformer.MethodSignature("renderQuadColor", "", "(Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/client/renderer/block/model/BakedQuad;I)V");
      return transform(basicClass, forMethod(sig1, (method) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(21, 2));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "transformQuadRenderColor", "(I)I", false));
         newInstructions.add(new VarInsnNode(54, 2));
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }));
   }

   private static byte[] transformDynamicLiquid(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("isBlocked", "isBlocked", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return node.getOpcode() == 172;
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 4));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "isBlockNotBrokenByWater", "(Lnet/minecraft/block/Block;)Z", false));
         newInstructions.add(new InsnNode(128));
         method.instructions.insertBefore(node, newInstructions);
         return false;
      })));
   }

   private static byte[] transformEnchantmentHelper(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("getEnchantments", "getEnchantments", "(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("getEnchantmentTagList", "getEnchantmentTagList", "()Lnet/minecraft/nbt/NBTTagList;");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return (node.getOpcode() == 182 || node.getOpcode() == 183) && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "getEnchantmentsForStack", "(Lnet/minecraft/nbt/NBTTagList;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NBTTagList;", false));
         method.instructions.insert(node, newInstructions);
         return false;
      })));
   }

   private static byte[] transformLayerEntityOnShoulder(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("renderEntityOnShoulder", "renderEntityOnShoulder", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/UUID;Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/client/renderer/entity/RenderLivingBase;Lnet/minecraft/client/model/ModelBase;Lnet/minecraft/util/ResourceLocation;Ljava/lang/Class;FFFFFFFZ)Lnet/minecraft/client/renderer/entity/layers/LayerEntityOnShoulder$DataHolder;");
      ClassTransformer.MethodSignature target = new ClassTransformer.MethodSignature("bindTexture", "bindTexture", "(Lnet/minecraft/util/ResourceLocation;)V");
      return transform(basicClass, forMethod(sig, combine((node) -> {
         return (node.getOpcode() == 182 || node.getOpcode() == 183) && target.matches((MethodInsnNode)node);
      }, (method, node) -> {
         InsnList newInstructions = new InsnList();
         newInstructions.add(new VarInsnNode(25, 3));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "replaceParrotTexture", "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/util/ResourceLocation;", false));
         method.instructions.insertBefore(node, newInstructions);
         return false;
      })));
   }

   private static byte[] transformDamageSource(byte[] basicClass) {
      ClassTransformer.MethodSignature sig = new ClassTransformer.MethodSignature("causePlayerDamage", "causePlayerDamage", "(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/util/DamageSource;");
      return transform(basicClass, forMethod(sig, (method) -> {
         InsnList newInstructions = new InsnList();
         LabelNode jump = new LabelNode();
         newInstructions.add(new VarInsnNode(25, 0));
         newInstructions.add(new MethodInsnNode(184, "vazkii/quark/base/asm/ASMHooks", "createPlayerDamage", "(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/util/DamageSource;", false));
         newInstructions.add(new InsnNode(89));
         newInstructions.add(new JumpInsnNode(198, jump));
         newInstructions.add(new InsnNode(176));
         newInstructions.add(jump);
         newInstructions.add(new InsnNode(87));
         method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
         return true;
      }));
   }

   private static byte[] transform(byte[] basicClass, ClassTransformer.TransformerAction... methods) {
      ClassReader reader;
      try {
         reader = new ClassReader(basicClass);
      } catch (NullPointerException var9) {
         return basicClass;
      }

      ClassNode node = new ClassNode();
      reader.accept(node, 0);
      if (debugLog) {
         log(getNodeString(node));
      }

      boolean didAnything = false;

      for(ClassTransformer.TransformerAction pair : methods) {
         didAnything |= pair.test(node);
      }

      if (didAnything) {
         ClassWriter writer = new ClassTransformer.SafeClassWriter(3);
         if (debugLog) {
            log(getNodeString(node));
         }

         debugLog = false;
         node.accept(writer);
         return writer.toByteArray();
      } else {
         return basicClass;
      }
   }

   public static boolean findMethodAndTransform(ClassNode node, ClassTransformer.MethodSignature sig, ClassTransformer.MethodAction predicate, boolean logResult) {
      for(MethodNode method : node.methods) {
         if (sig.matches(method)) {
            log("Located Method, patching...");
            boolean finish = predicate.test(method);
            if (logResult) {
               log("Patch result: " + finish);
            }

            debugMethod(method, finish);
            return finish;
         }
      }

      if (logResult) {
         log("Failed to locate the method!");
      }

      return false;
   }

   public static void debugMethod(MethodNode node, boolean result) {
      if (!result) {
         String nodeString = getNodeString(node);
         log("============= Please report this to Quark! =============\n" + nodeString.substring(0, nodeString.length() - 1));
         log("============= Please report this to Quark! =============");
      }

   }

   public static ClassTransformer.MethodAction combine(ClassTransformer.NodeFilter filter, ClassTransformer.NodeAction action) {
      return (node) -> {
         return applyOnNode(node, filter, action);
      };
   }

   public static boolean applyOnNode(MethodNode method, ClassTransformer.NodeFilter filter, ClassTransformer.NodeAction action) {
      Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
      boolean didAny = false;

      while(iterator.hasNext()) {
         AbstractInsnNode anode = (AbstractInsnNode)iterator.next();
         if (filter.test(anode)) {
            log("Located patch target node " + getNodeString(anode));
            didAny = true;
            if (action.test(method, anode)) {
               break;
            }
         }
      }

      return didAny;
   }

   private static void log(String str) {
      LogManager.getLogger("Quark ASM").info(str);
   }

   private static String getNodeString(ClassNode node) {
      StringWriter sw = new StringWriter();
      PrintWriter printer = new PrintWriter(sw);
      TraceClassVisitor visitor = new TraceClassVisitor(printer);
      node.accept(visitor);
      return sw.toString();
   }

   private static String getNodeString(MethodNode node) {
      Printer printer = new Textifier();
      TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
      node.accept(visitor);
      StringWriter sw = new StringWriter();
      printer.print(new PrintWriter(sw));
      printer.getText().clear();
      return sw.toString();
   }

   private static String getNodeString(AbstractInsnNode node) {
      Printer printer = new Textifier();
      TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
      node.accept(visitor);
      StringWriter sw = new StringWriter();
      printer.print(new PrintWriter(sw));
      printer.getText().clear();
      return sw.toString().replaceAll("\n", "").trim();
   }

   private static boolean hasOptifine(String msg) {
      try {
         if (Class.forName("optifine.OptiFineTweaker") != null) {
            log("Optifine Detected. Disabling Patch for " + msg);
            return true;
         }
      } catch (ClassNotFoundException var2) {
         ;
      }

      return false;
   }

   private static ClassTransformer.TransformerAction forMethod(ClassTransformer.MethodSignature sig, ClassTransformer.MethodAction... actions) {
      return new ClassTransformer.MethodTransformerAction(sig, actions);
   }

   private static ClassTransformer.TransformerAction inject(ClassTransformer.MethodSignature sig, ClassTransformer.MethodAction ifPresent, ClassTransformer.NewMethodAction... actions) {
      return new ClassTransformer.MethodInjectorAction(sig, ifPresent, actions);
   }

   static {
      transformers.put("net.minecraft.client.model.ModelBiped", ClassTransformer::transformModelBiped);
      transformers.put("net.minecraft.client.renderer.RenderItem", ClassTransformer::transformRenderItem);
      transformers.put("net.minecraft.client.renderer.entity.layers.LayerArmorBase", ClassTransformer::transformLayerArmorBase);
      transformers.put("net.minecraft.client.renderer.entity.RenderBoat", ClassTransformer::transformRenderBoat);
      transformers.put("net.minecraft.entity.item.EntityBoat", ClassTransformer::transformEntityBoat);
      transformers.put("net.minecraft.entity.item.EntityMinecart", ClassTransformer::transformEntityMinecart);
      transformers.put("net.minecraft.client.renderer.entity.RenderManager", ClassTransformer::transformRenderManager);
      transformers.put("net.minecraft.block.BlockPistonBase", ClassTransformer::transformBlockPistonBase);
      transformers.put("net.minecraft.inventory.ContainerWorkbench", ClassTransformer::transformContainerWorkbench);
      transformers.put("net.minecraft.inventory.ContainerMerchant", ClassTransformer::transformContainerMerchant);
      transformers.put("net.minecraft.tileentity.TileEntityPiston", ClassTransformer::transformTileEntityPiston);
      transformers.put("net.minecraft.client.renderer.tileentity.TileEntityPistonRenderer", ClassTransformer::transformTileEntityPistonRenderer);
      transformers.put("net.minecraft.world.WorldServer", ClassTransformer::transformWorldServer);
      transformers.put("net.minecraft.client.renderer.BlockModelRenderer", ClassTransformer::transformBlockModelRenderer);
      transformers.put("net.minecraft.item.crafting.RecipesBanners$RecipeAddPattern", ClassTransformer::transformRecipeAddPattern);
      transformers.put("net.minecraft.item.ItemBanner", ClassTransformer::transformItemBanner);
      transformers.put("net.minecraft.client.renderer.entity.Render", ClassTransformer::transformRender);
      transformers.put("net.minecraft.entity.ai.EntityAITarget", ClassTransformer::transformEntityAITarget);
      transformers.put("net.minecraft.client.gui.inventory.GuiContainer", ClassTransformer::transformGuiContainer);
      transformers.put("net.minecraft.entity.Entity", ClassTransformer::transformEntity);
      transformers.put("net.minecraft.entity.item.EntityItem", ClassTransformer::transformEntityItem);
      transformers.put("net.minecraft.client.gui.inventory.GuiBeacon$PowerButton", ClassTransformer::transformBeaconButton);
      transformers.put("net.minecraft.client.renderer.EntityRenderer", ClassTransformer::transformEntityRenderer);
      transformers.put("net.minecraft.enchantment.EnchantmentDamage", ClassTransformer::transformSharpness);
      transformers.put("net.minecraft.util.DamageSource", ClassTransformer::transformDamageSource);
      transformers.put("net.minecraft.item.ItemStack", ClassTransformer::transformItemStack);
      transformers.put("net.minecraftforge.client.model.pipeline.LightUtil", ClassTransformer::transformLightUtil);
      transformers.put("net.minecraft.enchantment.Enchantment", ClassTransformer::transformEnchantment);
      transformers.put("net.minecraft.block.BlockDynamicLiquid", ClassTransformer::transformDynamicLiquid);
      transformers.put("net.minecraft.enchantment.EnchantmentHelper", ClassTransformer::transformEnchantmentHelper);
      transformers.put("net.minecraft.client.renderer.entity.layers.LayerEntityOnShoulder", ClassTransformer::transformLayerEntityOnShoulder);
   }

   private interface MethodAction extends Predicate<MethodNode> {
   }

   private static class MethodInjectorAction implements ClassTransformer.TransformerAction {
      private final ClassTransformer.MethodSignature sig;
      private final ClassTransformer.MethodAction ifPresent;
      private final ClassTransformer.NewMethodAction[] actions;

      public MethodInjectorAction(ClassTransformer.MethodSignature sig, ClassTransformer.MethodAction ifPresent, ClassTransformer.NewMethodAction[] actions) {
         this.sig = sig;
         this.ifPresent = ifPresent;
         this.actions = actions;
      }

      public boolean test(ClassNode classNode) {
         boolean didAnything = ClassTransformer.findMethodAndTransform(classNode, this.sig, this.ifPresent, false);
         if (!didAnything) {
            ClassTransformer.log("Could not locate, injecting method (" + this.sig + ")");
            MethodVisitor method = classNode.visitMethod(1, LoadingPlugin.runtimeDeobfEnabled ? this.sig.srgName : this.sig.funcName, this.sig.funcDesc, (String)null, (String[])null);

            for(ClassTransformer.NewMethodAction action : this.actions) {
               boolean finish = action.test(classNode, method);
               ClassTransformer.log("Patch result: " + finish);
            }
         } else {
            ClassTransformer.log("Patch result: true");
         }

         return true;
      }
   }

   public static class MethodSignature {
      private final String funcName;
      private final String srgName;
      private final String funcDesc;

      public MethodSignature(String funcName, String srgName, String funcDesc) {
         this.funcName = funcName;
         this.srgName = srgName;
         this.funcDesc = funcDesc;
      }

      public String toString() {
         return "Names [" + this.funcName + ", " + this.srgName + "] Descriptor " + this.funcDesc;
      }

      public boolean matches(String methodName, String methodDesc) {
         return (methodName.equals(this.funcName) || methodName.equals(this.srgName)) && methodDesc.equals(this.funcDesc);
      }

      public boolean matches(MethodNode method) {
         return this.matches(method.name, method.desc);
      }

      public boolean matches(MethodInsnNode method) {
         return this.matches(method.name, method.desc);
      }

      public String mappedName(String owner) {
         return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, this.srgName, this.funcDesc);
      }
   }

   private static class MethodTransformerAction implements ClassTransformer.TransformerAction {
      private final ClassTransformer.MethodSignature sig;
      private final ClassTransformer.MethodAction[] actions;

      public MethodTransformerAction(ClassTransformer.MethodSignature sig, ClassTransformer.MethodAction[] actions) {
         this.sig = sig;
         this.actions = actions;
      }

      public boolean test(ClassNode classNode) {
         boolean didAnything = false;
         ClassTransformer.log("Applying Transformation to method (" + this.sig + ")");

         for(ClassTransformer.MethodAction action : this.actions) {
            didAnything |= ClassTransformer.findMethodAndTransform(classNode, this.sig, action, true);
         }

         return didAnything;
      }
   }

   private interface NewMethodAction extends BiPredicate<ClassNode, MethodVisitor> {
   }

   private interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> {
   }

   private interface NodeFilter extends Predicate<AbstractInsnNode> {
   }

   public static class SafeClassWriter extends ClassWriter {
      public SafeClassWriter(int flags) {
         super(flags);
      }

      protected String getCommonSuperClass(String type1, String type2) {
         ClassLoader classLoader = Launch.classLoader;

         Class<?> c;
         Class<?> d;
         try {
            c = Class.forName(type1.replace('/', '.'), false, classLoader);
            d = Class.forName(type2.replace('/', '.'), false, classLoader);
         } catch (Exception var7) {
            throw new RuntimeException(var7.toString());
         }

         if (c.isAssignableFrom(d)) {
            return type1;
         } else if (d.isAssignableFrom(c)) {
            return type2;
         } else if (!c.isInterface() && !d.isInterface()) {
            while(true) {
               c = c.getSuperclass();
               if (c.isAssignableFrom(d)) {
                  break;
               }
            }

            return c.getName().replace('.', '/');
         } else {
            return "java/lang/Object";
         }
      }
   }

   private interface Transformer extends Function<byte[], byte[]> {
   }

   private interface TransformerAction extends Predicate<ClassNode> {
   }
}
