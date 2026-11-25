package com.endertech.minecraft.forge.coremod.hooks;

import com.endertech.minecraft.forge.coremod.ForgeClassTransformer;
import com.endertech.minecraft.forge.coremod.InstructList;
import com.endertech.minecraft.forge.coremod.descriptors.Classes;
import com.endertech.minecraft.forge.coremod.descriptors.MethodDescriptor;
import com.endertech.minecraft.forge.coremod.descriptors.Types;
import com.endertech.minecraft.forge.coremod.signatures.MethodSignature;
import org.objectweb.asm.tree.MethodInsnNode;

public abstract class FurnaceFuelBurnedHook extends ForgeClassTransformer {
   protected MethodSignature handler = this.getDefaultHandlerWith(MethodDescriptor.VOID.withParameters(Classes.TILE_ENTITY, Classes.ITEM_STACK));
   protected final MethodSignature target;
   protected final MethodSignature targetNode;

   public FurnaceFuelBurnedHook() {
      this.target = MethodSignature.of(Classes.TILE_ENTITY_FURNACE.name, "update", MethodDescriptor.VOID);
      this.targetNode = MethodSignature.of(Classes.ITEM_STACK.name, "shrink", MethodDescriptor.VOID.withParameters(Types.INT.descriptor));
   }

   protected void injectInstructions(InstructList instructions) {
      instructions.pop().dup().loadThis().swap().invokeStatic(this.getHandler()).pushInt(1).insertBefore();
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
