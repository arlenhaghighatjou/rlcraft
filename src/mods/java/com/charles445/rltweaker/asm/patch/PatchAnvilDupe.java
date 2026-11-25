package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.helper.ASMHelper;
import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchAnvilDupe extends PatchManager {
   public PatchAnvilDupe() {
      super("Anvil Dupe Fix");
      this.add(new Patch(this, "net.minecraftforge.common.ForgeHooks", 1) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_onAnvilChange = this.findMethod(clazzNode, new String[]{"onAnvilChange"});
            if (m_onAnvilChange == null) {
               throw new RuntimeException("Couldn't find onAnvilChange");
            } else {
               AbstractInsnNode anchor = TransformUtil.findNextCallWithOpcodeAndName(this.first(m_onAnvilChange), 182, "post");
               if (anchor == null) {
                  throw new RuntimeException("Couldn't find post in onAnvilChange");
               } else {
                  anchor = ASMHelper.findNextInstructionWithOpcode(anchor, 3);
                  if (anchor == null) {
                     throw new RuntimeException("Couldn't find next ICONST_0 in onAnvilChange");
                  } else if (anchor.getNext().getOpcode() != 172) {
                     throw new RuntimeException("Unexpected ICONST_0 in onAnvilChange not paired with IRETURN");
                  } else {
                     InsnList inject = new InsnList();
                     inject.add(new VarInsnNode(25, 0));
                     inject.add(new VarInsnNode(25, 3));
                     inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookMinecraft", "clearAnvilResult", "(Lnet/minecraft/inventory/ContainerRepair;Lnet/minecraft/inventory/IInventory;)V", false));
                     this.insertBefore(m_onAnvilChange, anchor, inject);
                  }
               }
            }
         }
      });
   }
}
