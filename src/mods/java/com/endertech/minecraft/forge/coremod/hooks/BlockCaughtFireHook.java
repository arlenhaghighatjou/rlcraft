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

public class BlockCaughtFireHook extends ForgeClassTransformer {
   protected final MethodSignature handler = this.getDefaultHandlerWith(MethodDescriptor.VOID.withParameters(Classes.WORLD, Classes.BLOCK_POS));
   protected final MethodSignature target;
   protected final MethodSignature targetNode;
   private final int targetNodeIndex;
   private int curNodeIndex;

   public BlockCaughtFireHook() {
      this.target = MethodSignature.of(Classes.BLOCK_FIRE.name, "tryCatchFire", MethodDescriptor.VOID.withParameters(Classes.WORLD.descriptor, Classes.BLOCK_POS.descriptor, Types.INT.descriptor, Classes.RANDOM.descriptor, Types.INT.descriptor, Classes.ENUM_FACING.descriptor));
      this.targetNode = MethodSignature.of(Classes.WORLD.name, "getBlockState", MethodDescriptor.of(Classes.IBLOCK_STATE).withParameters(Classes.BLOCK_POS));
      this.targetNodeIndex = 2;
      this.curNodeIndex = 0;
   }

   public static void handle(World world, BlockPos pos) {
   }

   protected MethodSignature getTargetMethod() {
      return this.target;
   }

   protected MethodSignature getHandler() {
      return this.handler;
   }

   protected boolean isProperInstruction(MethodInsnNode node) {
      if (this.targetNode.complyWith(node)) {
         ++this.curNodeIndex;
      }

      return this.curNodeIndex == 2;
   }

   protected boolean shouldSearchNextInstruction(MethodInsnNode lastProperNode) {
      return this.curNodeIndex < 2;
   }

   protected void injectInstructions(InstructList instructions) {
      instructions.loadObjFrom(1, "world").loadObjFrom(2, "pos").invokeStatic(this.getHandler()).insertBefore();
   }
}
