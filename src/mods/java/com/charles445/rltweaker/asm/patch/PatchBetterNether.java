package com.charles445.rltweaker.asm.patch;

import com.charles445.rltweaker.asm.util.ASMUtil;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PatchBetterNether extends PatchManager {
   public PatchBetterNether() {
      this.add(new Patch(this, "paulevs.betternether.blocks.BlockChestOfDrawers", 3) {
         public void patch(ClassNode clazzNode) {
            String m_breakBlock_name = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName("net/minecraft/block/Block", "breakBlock", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V");
            MethodNode m_breakBlock = new MethodNode(1, m_breakBlock_name, "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", (String)null, (String[])null);
            m_breakBlock.instructions.add(ASMUtil.listOf(new VarInsnNode(25, 0), new VarInsnNode(25, 1), new VarInsnNode(25, 2), new MethodInsnNode(184, "com/charles445/rltweaker/hook/HookBetterNether", "drop", "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", false), new VarInsnNode(25, 0), new VarInsnNode(25, 1), new VarInsnNode(25, 2), new VarInsnNode(25, 3), new MethodInsnNode(183, clazzNode.superName, m_breakBlock_name, "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", false), new InsnNode(177)));
            clazzNode.methods.add(m_breakBlock);
         }
      });
   }
}
