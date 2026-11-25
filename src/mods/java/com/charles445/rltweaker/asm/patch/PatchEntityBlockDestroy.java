package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.TransformUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchEntityBlockDestroy extends PatchManager {
   public PatchEntityBlockDestroy() {
      super("Entity Breakable");
      this.add(new Patch(this, "net.minecraft.block.Block", 3) {
         public void patch(ClassNode clazzNode) {
            MethodNode m_canEntityDestroy = this.findMethod(clazzNode, new String[]{"canEntityDestroy"});
            if (m_canEntityDestroy == null) {
               throw new RuntimeException("Couldn't findMethod canEntityDestroy");
            } else {
               InsnList inject = new InsnList();
               LabelNode continueLabel = new LabelNode();
               inject.add(new VarInsnNode(25, 0));
               inject.add(new VarInsnNode(25, 1));
               inject.add(new VarInsnNode(25, 2));
               inject.add(new VarInsnNode(25, 3));
               inject.add(new VarInsnNode(25, 4));
               inject.add(new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookMinecraft", "shouldEntityDestroyBlock", "(Lnet/minecraft/block/Block;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)Z", false));
               inject.add(new JumpInsnNode(154, continueLabel));
               inject.add(new InsnNode(3));
               inject.add(new InsnNode(172));
               inject.add(continueLabel);
               TransformUtil.insertBeforeFirst(m_canEntityDestroy, inject);
            }
         }
      });
   }
}
