package com.endertech.minecraft.forge.coremod.hooks;

import com.endertech.minecraft.forge.coremod.ForgeClassTransformer;
import com.endertech.minecraft.forge.coremod.InstructList;
import com.endertech.minecraft.forge.coremod.descriptors.Classes;
import com.endertech.minecraft.forge.coremod.descriptors.MethodDescriptor;
import com.endertech.minecraft.forge.coremod.signatures.MethodSignature;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.MethodInsnNode;

public abstract class RenderItemHook extends ForgeClassTransformer {
   private final MethodSignature handler;
   private final MethodSignature target;
   private final MethodDescriptor renderModelNode;

   public RenderItemHook() {
      this.handler = this.getDefaultHandlerWith(MethodDescriptor.VOID.withParameters(Classes.ITEM_STACK.descriptor));
      this.target = MethodSignature.of(Classes.RENDER_ITEM.name, "renderItem", MethodDescriptor.VOID.withParameters(Classes.ITEM_STACK.descriptor, Classes.IBAKED_MODEL.descriptor));
      this.renderModelNode = MethodDescriptor.VOID.withParameters(Classes.IBAKED_MODEL.descriptor, Classes.ITEM_STACK.descriptor);
   }

   public static void handler(ItemStack stack) {
   }

   protected void injectInstructions(InstructList instructions) {
      instructions.loadObjFrom(1, "itemstack").invokeStatic(this.getHandler()).insertAfter();
   }

   protected MethodSignature getTargetMethod() {
      return this.target;
   }

   protected MethodSignature getHandler() {
      return this.handler;
   }

   protected boolean isProperInstruction(MethodInsnNode instructNode) {
      return this.renderModelNode.complyWith(instructNode.desc);
   }

   protected boolean shouldSearchNextInstruction(MethodInsnNode lastProperNode) {
      return false;
   }
}
