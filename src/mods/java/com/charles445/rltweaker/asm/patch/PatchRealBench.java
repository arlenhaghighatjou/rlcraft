package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.TransformUtil;
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

public class PatchRealBench extends PatchManager {
   public PatchRealBench() {
      super("Real Bench");
      this.add(new Patch(this, "pw.prok.realbench.WorkbenchTile", 3) {
         public void patch(ClassNode c_WorkbenchTile) {
            FieldNode f_mResult = new FieldNode(327680, 4, "mResult", "Lnet/minecraft/util/NonNullList;", "Lnet/minecraft/item/ItemStack;", (Object)null);
            MethodNode m_init = this.findMethodWithDesc(c_WorkbenchTile, "()V", new String[]{"<init>"});
            MethodNode m_writeSlots = this.findMethod(c_WorkbenchTile, new String[]{"writeSlots"});
            MethodNode m_readSlots = this.findMethod(c_WorkbenchTile, new String[]{"readSlots"});
            c_WorkbenchTile.fields.add(f_mResult);
            InsnList insert = new InsnList();
            insert.add(new VarInsnNode(25, 0));
            insert.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookRealBench", "getResultSlotInit", "()Lnet/minecraft/util/NonNullList;", false));
            insert.add(new FieldInsnNode(181, "pw/prok/realbench/WorkbenchTile", "mResult", "Lnet/minecraft/util/NonNullList;"));
            TransformUtil.insertBeforeFirst(m_init, insert);
            insert = new InsnList();
            insert.add(new VarInsnNode(25, 1));
            insert.add(new VarInsnNode(25, 0));
            insert.add(new FieldInsnNode(180, "pw/prok/realbench/WorkbenchTile", "mResult", "Lnet/minecraft/util/NonNullList;"));
            insert.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookRealBench", "writeSlots", "(Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/util/NonNullList;)V", false));
            TransformUtil.insertBeforeFirst(m_writeSlots, insert);
            insert = new InsnList();
            insert.add(new VarInsnNode(25, 1));
            insert.add(new VarInsnNode(25, 0));
            insert.add(new FieldInsnNode(180, "pw/prok/realbench/WorkbenchTile", "mResult", "Lnet/minecraft/util/NonNullList;"));
            insert.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookRealBench", "readSlots", "(Lnet/minecraft/nbt/NBTTagCompound;Lnet/minecraft/util/NonNullList;)V", false));
            TransformUtil.insertBeforeFirst(m_readSlots, insert);
         }
      });
      this.add(new Patch(this, "net.minecraft.inventory.ContainerWorkbench", 3) {
         public void patch(ClassNode c_ContainerWorkbench) {
            MethodNode m_init = this.findMethodWithDesc(c_ContainerWorkbench, "(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", new String[]{"<init>"});
            boolean isRealBenchInstalled = false;

            for(AbstractInsnNode anchor = this.first(m_init); anchor != null; anchor = anchor.getNext()) {
               if (anchor.getType() == 5 && ((MethodInsnNode)anchor).owner.equals("pw/prok/realbench/asm/ASMHooks")) {
                  isRealBenchInstalled = true;
               }
            }

            if (!isRealBenchInstalled) {
               this.cancelled = true;
            } else {
               for(AbstractInsnNode var7 = this.first(m_init); var7 != null; var7 = var7.getNext()) {
                  if (var7.getOpcode() == 187) {
                     TypeInsnNode newAnchor = (TypeInsnNode)var7;
                     if (newAnchor.desc.equals("net/minecraft/inventory/InventoryCraftResult")) {
                        newAnchor.desc = "com/charles445/rltweaker/hook/HookRealBench$Result";
                     }
                  } else if (var7.getOpcode() == 183) {
                     MethodInsnNode initAnchor = (MethodInsnNode)var7;
                     if (initAnchor.name.equals("<init>") && initAnchor.owner.equals("net/minecraft/inventory/InventoryCraftResult")) {
                        initAnchor.owner = "com/charles445/rltweaker/hook/HookRealBench$Result";
                        initAnchor.desc = "(Lnet/minecraft/inventory/Container;)V";
                        InsnList qq = new InsnList();
                        qq.add(new VarInsnNode(25, 0));
                        m_init.instructions.insertBefore(initAnchor, qq);
                     }
                  }
               }

            }
         }
      });
      this.add(new Patch(this, "pw.prok.realbench.WorkbenchInventory", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_init = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/inventory/Container;IILnet/minecraft/entity/player/EntityPlayer;)V", new String[]{"<init>"});
            MethodInsnNode toCall = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_init), 182, "initRealBench");
            toCall.desc = "(I)V";
            InsnList inject = new InsnList();
            inject.add(new VarInsnNode(21, 2));
            inject.add(new VarInsnNode(21, 3));
            inject.add(new InsnNode(104));
            this.insertBefore(m_init, toCall, inject);
         }
      });
      this.add(new Patch(this, "pw.prok.realbench.asm.ASMHooks", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_dropBlockAsItem = this.findMethodWithDesc(clazzNode, "(Lnet/minecraft/world/World;IIILnet/minecraft/item/ItemStack;)V", new String[]{"dropBlockAsItem"});
            MethodInsnNode toCall = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_dropBlockAsItem), 183, "<init>");
            InsnList inject = new InsnList();
            inject.add(new FieldInsnNode(178, "java/lang/Integer", "MAX_VALUE", "I"));
            inject.add(TransformUtil.createObfMethodInsn(182, "net/minecraft/item/ItemStack", "splitStack", "(I)Lnet/minecraft/item/ItemStack;", false));
            this.insertBefore(m_dropBlockAsItem, toCall, inject);
         }
      });
   }
}
