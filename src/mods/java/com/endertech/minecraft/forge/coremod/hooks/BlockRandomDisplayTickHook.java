package com.endertech.minecraft.forge.coremod.hooks;

import com.endertech.minecraft.forge.coremod.ForgeClassTransformer;
import com.endertech.minecraft.forge.coremod.InstructList;
import com.endertech.minecraft.forge.coremod.descriptors.Classes;
import com.endertech.minecraft.forge.coremod.descriptors.MethodDescriptor;
import com.endertech.minecraft.forge.coremod.descriptors.Types;
import com.endertech.minecraft.forge.coremod.signatures.MethodSignature;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.tree.MethodInsnNode;

public abstract class BlockRandomDisplayTickHook extends ForgeClassTransformer {
   protected final MethodSignature handler = this.getDefaultHandlerWith(MethodDescriptor.VOID.withParameters(Classes.WORLD, Classes.BLOCK_POS));
   protected final MethodSignature target;
   protected final MethodSignature targetNode;

   public BlockRandomDisplayTickHook() {
      this.target = MethodSignature.of(Classes.WORLD_CLIENT.name, "showBarrierParticles", MethodDescriptor.VOID.withParameters(Types.INT.descriptor, Types.INT.descriptor, Types.INT.descriptor, Types.INT.descriptor, Classes.RANDOM.descriptor, Types.BOOLEAN.descriptor, Classes.BLOCK_POS$MUTABLE.descriptor));
      this.targetNode = MethodSignature.of(Classes.BLOCK.name, "randomDisplayTick", MethodDescriptor.VOID.withParameters(Classes.IBLOCK_STATE, Classes.WORLD, Classes.BLOCK_POS, Classes.RANDOM));
   }

   public static void handle(World world, BlockPos pos) {
   }

   protected void injectInstructions(InstructList instructions) {
      instructions.loadObjFrom(0, "world").loadObjFrom(7, "pos").invokeStatic(this.getHandler()).insertBefore();
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
}
