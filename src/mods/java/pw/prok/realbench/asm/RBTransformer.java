package pw.prok.realbench.asm;

import java.util.Iterator;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class RBTransformer implements IClassTransformer {
   public static final Logger LOG = LogManager.getLogger("RB-Transformer");

   public RBTransformer() {
   }

   public static void info(String format, Object... args) {
      LOG.info(String.format(format, args));
   }

   public static void error(String format, Object... args) {
      LOG.error(String.format(format, args));
   }

   public byte[] transform(String name, String dev, byte[] bytes) {
      boolean client = FMLLaunchHandler.side() == Side.CLIENT;
      if ("net.minecraft.block.BlockWorkbench".equals(dev)) {
         boolean obf = !name.equals(dev);
         String _isOpaqueCube = obf ? "isOpaqueCube" : "isOpaqueCube";
         String _createNewTileEntity = obf ? "createNewTileEntity" : "createNewTileEntity";
         String _breakBlock = obf ? "breakBlock" : "breakBlock";
         info("Injecting into %s (%s)", name, dev);
         ClassReader reader = new ClassReader(bytes);
         ClassWriter writer = new ClassWriter(3);
         ClassNode node = new ClassNode();
         reader.accept(node, 8);
         info(" - ITileEntityProvider");
         node.interfaces.add("net/minecraft/block/ITileEntityProvider");
         info(" - createNewTileEntity");
         MethodNode createNewTileEntity = new MethodNode(327680, 1, _createNewTileEntity, "(Lnet/minecraft/world/World;I)Lnet/minecraft/tileentity/TileEntity;", (String)null, (String[])null);
         InsnList insn = createNewTileEntity.instructions;
         insn.add(new MethodInsnNode(184, "pw/prok/realbench/asm/ASMHooks", "createNewTileEntity", "()Lpw/prok/realbench/WorkbenchTile;", false));
         insn.add(new InsnNode(176));
         node.methods.add(createNewTileEntity);
         info(" - breakBlock");
         MethodNode breakBlock = new MethodNode(327680, 1, _breakBlock, "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", (String)null, (String[])null);
         insn = breakBlock.instructions;
         insn.add(new VarInsnNode(25, 1));
         insn.add(new VarInsnNode(25, 2));
         insn.add(new MethodInsnNode(184, "pw/prok/realbench/asm/ASMHooks", "breakBlock", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", false));
         insn.add(new VarInsnNode(25, 0));
         insn.add(new VarInsnNode(25, 1));
         insn.add(new VarInsnNode(25, 2));
         insn.add(new VarInsnNode(25, 3));
         insn.add(new MethodInsnNode(183, "net/minecraft/block/Block", _breakBlock, "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", false));
         insn.add(new InsnNode(177));
         node.methods.add(breakBlock);
         info(" - <clinit>");
         MethodNode clinit = new MethodNode(327680, 9, "<clinit>", "()V", (String)null, (String[])null);
         insn = clinit.instructions;
         insn.add(new MethodInsnNode(184, "pw/prok/realbench/asm/ASMHooks", "staticInit", "()V", false));
         insn.add(new InsnNode(177));
         node.methods.add(clinit);
         if (client) {
            info(" - isOpaqueCube");
            MethodNode isOpaqueCube = new MethodNode(327680, 1, _isOpaqueCube, "(Lnet/minecraft/block/state/IBlockState;)Z", (String)null, (String[])null);
            insn = isOpaqueCube.instructions;
            insn.add(new InsnNode(3));
            insn.add(new InsnNode(172));
            node.methods.add(isOpaqueCube);
         }

         node.accept(writer);
         info(" - Done");
         return writer.toByteArray();
      } else if ("net.minecraft.inventory.ContainerWorkbench".equals(dev)) {
         boolean obf = !name.equals(dev);
         String _onContainerClosed = obf ? "onContainerClosed" : "onContainerClosed";
         String _onContainerClosed_notch = obf ? "b" : "onContainerClosed";
         String _onContainerClosed_notch_desc = obf ? "(Laed;)V" : "(Lnet/minecraft/entity/player/EntityPlayer;)V";
         String _inventoryCrafting_notch = obf ? "afy" : "net/minecraft/inventory/InventoryCrafting";
         String _container_notch = obf ? "afr" : "net/minecraft/inventory/Container";
         String _craftMatrix = obf ? "craftMatrix" : "craftMatrix";
         String _closeInventory = obf ? "closeInventory" : "closeInventory";
         String _containerWorkbench_world = obf ? "world" : "world";
         String _containerWorkbench_player = "player";
         String _containerWorkbench_result = obf ? "craftResult" : "craftResult";
         info("Injecting into %s (%s)", name, dev);
         ClassReader reader = new ClassReader(bytes);
         ClassWriter writer = new ClassWriter(3);
         ClassNode node = new ClassNode();
         reader.accept(node, 0);
         info(" - mRealBenchTile");
         node.fields.add(new FieldNode(327680, 1, "mRealBenchTile", "Lpw/prok/realbench/WorkbenchTile;", (String)null, (Object)null));
         int hooks = 0;

         for(MethodNode method : node.methods) {
            if ("<init>".equals(method.name)) {
               info(" - <init>");
               ++hooks;
               InsnList insn = method.instructions;
               Iterator<AbstractInsnNode> n = insn.iterator();

               while(n.hasNext()) {
                  AbstractInsnNode n1 = (AbstractInsnNode)n.next();
                  if (n1.getOpcode() == 187) {
                     TypeInsnNode n2 = (TypeInsnNode)n1;
                     if (_inventoryCrafting_notch.equals(n2.desc)) {
                        info(" - WorkbenchInventory[NEW]");
                        ++hooks;
                        n2.desc = "pw/prok/realbench/WorkbenchInventory";
                     }
                  } else if (n1.getOpcode() == 183) {
                     MethodInsnNode n2 = (MethodInsnNode)n1;
                     if (_inventoryCrafting_notch.equals(n2.owner) && "<init>".equals(n2.name)) {
                        info(" - WorkbenchInventory[INVOKESPECIAL]");
                        ++hooks;
                        n2.owner = "pw/prok/realbench/WorkbenchInventory";
                     } else if (_container_notch.equals(n2.owner) && "<init>".equals(n2.name)) {
                        info(" - WorkbenchTile[PUTFIELD]");
                        ++hooks;
                        InsnList n3 = new InsnList();
                        n3.add(new VarInsnNode(25, 0));
                        n3.add(new VarInsnNode(25, 2));
                        n3.add(new VarInsnNode(25, 3));
                        n3.add(new MethodInsnNode(184, "pw/prok/realbench/asm/ASMHooks", "getTile", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lpw/prok/realbench/WorkbenchTile;", false));
                        n3.add(new FieldInsnNode(181, "net/minecraft/inventory/ContainerWorkbench", "mRealBenchTile", "Lpw/prok/realbench/WorkbenchTile;"));
                        insn.insert(n2, n3);
                     }
                  } else if (n1.getOpcode() == 177) {
                     info(" - ContainerWorkbench[REFRESH_MATRIX]");
                     ++hooks;
                     InsnList n3 = new InsnList();
                     n3.add(new VarInsnNode(25, 0));
                     n3.add(new InsnNode(89));
                     n3.add(new FieldInsnNode(180, "net/minecraft/inventory/ContainerWorkbench", _containerWorkbench_world, "Lnet/minecraft/world/World;"));
                     n3.add(new VarInsnNode(25, 0));
                     n3.add(new FieldInsnNode(180, "net/minecraft/inventory/ContainerWorkbench", "player", "Lnet/minecraft/entity/player/EntityPlayer;"));
                     n3.add(new VarInsnNode(25, 0));
                     n3.add(new FieldInsnNode(180, "net/minecraft/inventory/ContainerWorkbench", _craftMatrix, "Lnet/minecraft/inventory/InventoryCrafting;"));
                     n3.add(new VarInsnNode(25, 0));
                     n3.add(new FieldInsnNode(180, "net/minecraft/inventory/ContainerWorkbench", _containerWorkbench_result, "Lnet/minecraft/inventory/InventoryCraftResult;"));
                     n3.add(new MethodInsnNode(182, "net/minecraft/inventory/ContainerWorkbench", "slotChangedCraftingGrid", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/inventory/InventoryCraftResult;)V", false));
                     insn.insertBefore(n1, n3);
                  }
               }
            } else if (_onContainerClosed_notch.equals(method.name) && _onContainerClosed_notch_desc.equals(method.desc)) {
               info(" - onContainerClosed");
               ++hooks;
               InsnList insn = method.instructions;
               insn.clear();
               insn.add(new VarInsnNode(25, 0));
               insn.add(new VarInsnNode(25, 1));
               insn.add(new MethodInsnNode(183, "net/minecraft/inventory/Container", _onContainerClosed, "(Lnet/minecraft/entity/player/EntityPlayer;)V", false));
               insn.add(new VarInsnNode(25, 0));
               insn.add(new FieldInsnNode(180, "net/minecraft/inventory/ContainerWorkbench", _craftMatrix, "Lnet/minecraft/inventory/InventoryCrafting;"));
               insn.add(new VarInsnNode(25, 1));
               insn.add(new MethodInsnNode(185, "net/minecraft/inventory/IInventory", _closeInventory, "(Lnet/minecraft/entity/player/EntityPlayer;)V", true));
               insn.add(new InsnNode(177));
            }
         }

         requireHooks(dev, hooks, 6);
         node.accept(writer);
         info(" - Done");
         return writer.toByteArray();
      } else if (!"pw.prok.realbench.asm.ASMHooks".equals(dev)) {
         if ("pw.prok.realbench.WorkbenchInventory".equals(dev)) {
            info("Injecting into %s (%s)", name, dev);
            ClassReader reader = new ClassReader(bytes);
            ClassWriter writer = new ClassWriter(3);
            ClassNode node = new ClassNode();
            reader.accept(node, 8);
            info(" - <init>");
            MethodNode init = new MethodNode(327680, 1, "<init>", "(Lnet/minecraft/inventory/Container;IILnet/minecraft/entity/player/EntityPlayer;)V", (String)null, (String[])null);
            InsnList insn = init.instructions;
            insn.add(new VarInsnNode(25, 0));
            insn.add(new VarInsnNode(25, 1));
            insn.add(new VarInsnNode(21, 2));
            insn.add(new VarInsnNode(21, 3));
            insn.add(new VarInsnNode(25, 4));
            insn.add(new MethodInsnNode(183, "net/minecraft/inventory/InventoryCrafting", "<init>", "(Lnet/minecraft/inventory/Container;IILnet/minecraft/entity/player/EntityPlayer;)V", false));
            insn.add(new VarInsnNode(25, 0));
            insn.add(new MethodInsnNode(182, "pw/prok/realbench/WorkbenchInventory", "initRealBench", "()V", false));
            insn.add(new InsnNode(177));
            node.methods.add(init);
            node.accept(writer);
            info(" - Done");
            return writer.toByteArray();
         } else {
            return bytes;
         }
      } else {
         info("Injecting into %s (%s)", name, dev);
         ClassReader reader = new ClassReader(bytes);
         ClassWriter writer = new ClassWriter(3);
         ClassNode node = new ClassNode();
         reader.accept(node, 8);
         int hooks = 0;

         for(MethodNode method : node.methods) {
            if ("getTile".equals(method.name)) {
               info(" - getTile");
               ++hooks;
               method.access &= -257;
               InsnList insn = method.instructions;
               insn.clear();
               insn.add(new VarInsnNode(25, 0));
               insn.add(new FieldInsnNode(180, "net/minecraft/inventory/ContainerWorkbench", "mRealBenchTile", "Lpw/prok/realbench/WorkbenchTile;"));
               insn.add(new InsnNode(176));
               break;
            }
         }

         requireHooks(dev, hooks, 1);
         node.accept(writer);
         info(" - Done");
         return writer.toByteArray();
      }
   }

   private static void requireHooks(String clazz, int hooks, int requiredHooks) {
      if (hooks != requiredHooks) {
         error("Missing hooks for class %s: should be %d, but actually %d", clazz, requiredHooks, hooks);
      }

   }
}
