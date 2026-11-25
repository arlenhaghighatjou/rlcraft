package com.endertech.minecraft.forge.coremod.hooks;

import com.endertech.minecraft.forge.coremod.ForgeClassTransformer;
import com.endertech.minecraft.forge.coremod.InstructList;
import com.endertech.minecraft.forge.coremod.descriptors.Classes;
import com.endertech.minecraft.forge.coremod.descriptors.MethodDescriptor;
import com.endertech.minecraft.forge.coremod.signatures.MethodSignature;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.objectweb.asm.tree.MethodInsnNode;

public abstract class BlockRandomTickHook extends ForgeClassTransformer {
   protected final MethodSignature handler = this.getDefaultHandlerWith(MethodDescriptor.of(Classes.BOOLEAN).withParameters(Classes.WORLD, Classes.BLOCK_POS));
   protected final MethodSignature target;

   public BlockRandomTickHook() {
      this.target = MethodSignature.of(Classes.BLOCK.name, "randomTick", MethodDescriptor.VOID.withParameters(Classes.WORLD, Classes.BLOCK_POS, Classes.IBLOCK_STATE, Classes.RANDOM));
   }

   @Nullable
   public static Boolean handle(World world, BlockPos pos) {
      return null;
   }

   protected void injectInstructions(InstructList instructions) {
      instructions.loadObjFrom(1, "world").loadObjFrom(2, "pos").invokeStatic(this.getHandler()).ifNotNullReturn().insertBefore();
   }

   protected MethodSignature getTargetMethod() {
      return this.target;
   }

   protected MethodSignature getHandler() {
      return this.handler;
   }

   protected boolean isProperInstruction(MethodInsnNode node) {
      return true;
   }

   protected boolean shouldSearchNextInstruction(MethodInsnNode lastProperNode) {
      return false;
   }
}
