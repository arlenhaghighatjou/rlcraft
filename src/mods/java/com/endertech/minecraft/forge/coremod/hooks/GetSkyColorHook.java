package com.endertech.minecraft.forge.coremod.hooks;

import com.endertech.minecraft.forge.coremod.ForgeClassTransformer;
import com.endertech.minecraft.forge.coremod.InstructList;
import com.endertech.minecraft.forge.coremod.descriptors.Classes;
import com.endertech.minecraft.forge.coremod.descriptors.MethodDescriptor;
import com.endertech.minecraft.forge.coremod.descriptors.Types;
import com.endertech.minecraft.forge.coremod.signatures.MethodSignature;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.tree.MethodInsnNode;

public abstract class GetSkyColorHook extends ForgeClassTransformer {
   protected final MethodSignature handler = this.getDefaultHandlerWith(MethodDescriptor.of(Classes.VEC3D).withParameters(Classes.VEC3D, Classes.ENTITY));
   protected final MethodSignature target;
   protected final MethodSignature targetNode;

   public GetSkyColorHook() {
      this.target = MethodSignature.of(Classes.WORLD.name, "getSkyColor", MethodDescriptor.of(Classes.VEC3D).withParameters(Classes.ENTITY.descriptor, Types.FLOAT.descriptor));
      this.targetNode = MethodSignature.of(Classes.WORLD_PROVIDER.name, "getSkyColor", MethodDescriptor.of(Classes.VEC3D).withParameters(Classes.ENTITY.descriptor, Types.FLOAT.descriptor));
   }

   public static Vec3d handle(Vec3d vanillaColor, Entity entity) {
      return vanillaColor;
   }

   protected void injectInstructions(InstructList instructions) {
      instructions.loadObjFrom(1, "entity").invokeStatic(this.getHandler()).insertAfter();
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
