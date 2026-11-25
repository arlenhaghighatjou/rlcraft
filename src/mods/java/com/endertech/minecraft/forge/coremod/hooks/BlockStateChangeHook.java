package com.endertech.minecraft.forge.coremod.hooks;

import com.endertech.minecraft.forge.coremod.ForgeClassTransformer;
import com.endertech.minecraft.forge.coremod.InstructList;
import com.endertech.minecraft.forge.coremod.descriptors.Classes;
import com.endertech.minecraft.forge.coremod.descriptors.MethodDescriptor;
import com.endertech.minecraft.forge.coremod.descriptors.Types;
import com.endertech.minecraft.forge.coremod.signatures.MethodSignature;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.tree.MethodInsnNode;

public abstract class BlockStateChangeHook extends ForgeClassTransformer {
   public final MethodSignature handler = this.getDefaultHandlerWith(MethodDescriptor.VOID.withParameters(Classes.WORLD, Classes.BLOCK_POS, Classes.IBLOCK_STATE, Classes.IBLOCK_STATE));
   private final MethodSignature target;
   private final MethodSignature targetNode;

   public BlockStateChangeHook() {
      this.target = MethodSignature.of(Classes.WORLD.name, "setBlockState", MethodDescriptor.of(Types.BOOLEAN).withParameters(Classes.BLOCK_POS.descriptor, Classes.IBLOCK_STATE.descriptor, Types.INT.descriptor));
      this.targetNode = MethodSignature.of(Classes.CHUNK.name, "setBlockState", MethodDescriptor.of(Classes.IBLOCK_STATE).withParameters(Classes.BLOCK_POS, Classes.IBLOCK_STATE));
   }

   public static void handle(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
   }

   protected void injectInstructions(InstructList instructions) {
      instructions.loadObjFrom(0, "world").loadObjFrom(1, "blockPos").loadObjFrom(6, "oldState").loadObjFrom(2, "newState").invokeStatic(this.getHandler()).insertAfter();
   }

   protected MethodSignature getHandler() {
      return this.handler;
   }

   protected MethodSignature getTargetMethod() {
      return this.target;
   }

   protected boolean isProperInstruction(MethodInsnNode node) {
      return this.targetNode.complyWith(node);
   }

   protected boolean shouldSearchNextInstruction(MethodInsnNode lastProperNode) {
      return false;
   }
}
