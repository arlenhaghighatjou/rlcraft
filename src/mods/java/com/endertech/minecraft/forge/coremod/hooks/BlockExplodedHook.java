package com.endertech.minecraft.forge.coremod.hooks;

import com.endertech.minecraft.forge.coremod.ForgeClassTransformer;
import com.endertech.minecraft.forge.coremod.InstructList;
import com.endertech.minecraft.forge.coremod.descriptors.Classes;
import com.endertech.minecraft.forge.coremod.descriptors.MethodDescriptor;
import com.endertech.minecraft.forge.coremod.descriptors.Types;
import com.endertech.minecraft.forge.coremod.signatures.MethodSignature;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.objectweb.asm.tree.MethodInsnNode;

public class BlockExplodedHook extends ForgeClassTransformer {
   protected final MethodSignature handler = this.getDefaultHandlerWith(MethodDescriptor.VOID.withParameters(Classes.WORLD, Classes.BLOCK_POS, Classes.EXPLOSION, Classes.IBLOCK_STATE));
   protected final MethodSignature target;
   protected final MethodSignature targetNode;

   public BlockExplodedHook() {
      this.target = MethodSignature.of(Classes.BLOCK.name, "onBlockExploded", MethodDescriptor.VOID.withParameters(Classes.WORLD, Classes.BLOCK_POS, Classes.EXPLOSION));
      this.targetNode = MethodSignature.of(Classes.WORLD.name, "setBlockToAir", MethodDescriptor.of(Types.BOOLEAN).withParameters(Classes.BLOCK_POS));
   }

   public static void handle(World world, BlockPos pos, Explosion explosion, IBlockState oldState) {
   }

   protected MethodSignature getTargetMethod() {
      return this.target;
   }

   protected MethodSignature getHandler() {
      return this.handler;
   }

   protected boolean isProperInstruction(MethodInsnNode node) {
      return this.targetNode.complyWith(node);
   }

   protected boolean shouldSearchNextInstruction(MethodInsnNode lastProperNode) {
      return false;
   }

   protected void injectInstructions(InstructList instructions) {
      instructions.loadObjFrom(1, "world").loadObjFrom(2, "pos").invokeVirtual(MethodSignature.WORLD_GET_BLOCK_STATE).saveObjTo(4, "oldState").insertBefore();
      instructions.loadObjFrom(1, "world").loadObjFrom(2, "pos").loadObjFrom(3, "explosion").loadObjFrom(4, "oldState").invokeStatic(this.getHandler()).insertAfter();
   }
}
