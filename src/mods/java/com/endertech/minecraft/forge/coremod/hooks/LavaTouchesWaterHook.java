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

public class LavaTouchesWaterHook extends ForgeClassTransformer {
   protected final MethodSignature handler = this.getDefaultHandlerWith(MethodDescriptor.VOID.withParameters(Classes.WORLD, Classes.BLOCK_POS, Classes.IBLOCK_STATE));
   protected final MethodSignature target;
   protected final MethodSignature targetNode;

   public LavaTouchesWaterHook() {
      this.target = MethodSignature.of(Classes.BLOCK_LIQUID.name, "checkForMixing", MethodDescriptor.of(Types.BOOLEAN).withParameters(Classes.WORLD, Classes.BLOCK_POS, Classes.IBLOCK_STATE));
      this.targetNode = MethodSignature.of(Classes.BLOCK_LIQUID.name, "triggerMixEffects", MethodDescriptor.VOID.withParameters(Classes.WORLD, Classes.BLOCK_POS));
   }

   public static void handle(World world, BlockPos pos, IBlockState oldState) {
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
      return true;
   }

   protected void injectInstructions(InstructList instructions) {
      instructions.loadObjFrom(1, "world").loadObjFrom(2, "pos").loadObjFrom(3, "oldState").invokeStatic(this.getHandler()).insertAfter();
   }
}
