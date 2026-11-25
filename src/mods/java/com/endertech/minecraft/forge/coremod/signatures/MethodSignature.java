package com.endertech.minecraft.forge.coremod.signatures;

import com.endertech.minecraft.forge.coremod.descriptors.Classes;
import com.endertech.minecraft.forge.coremod.descriptors.Descriptor;
import com.endertech.minecraft.forge.coremod.descriptors.MethodDescriptor;
import com.endertech.minecraft.forge.coremod.descriptors.Types;
import com.endertech.minecraft.forge.coremod.names.ClassName;
import com.endertech.minecraft.forge.coremod.names.MethodName;
import com.endertech.minecraft.forge.coremod.names.Name;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodSignature extends Signature {
   public static final MethodSignature ENTITY_ON_ENTITY_UPDATE;
   public static final MethodSignature ENTITY_ON_UPDATE;
   public static final MethodSignature FLUID_TANK_GET_FLUID;
   public static final MethodSignature FLUID_TANK_DRAIN;
   public static final MethodSignature ITEM_STACK_GET_COUNT;
   public static final MethodSignature ITEM_STACK_SET_COUNT;
   public static final MethodSignature ITEM_STACK_SHRINK;
   public static final MethodSignature ITEM_STACK_SPLIT_STACK;
   public static final MethodSignature I_TICKABLE_UPDATE = of(ClassName.from("net.minecraft.util.ITickable"), "update", "update", MethodDescriptor.VOID);
   public static final MethodSignature I_INVENTORY_DECR_STACK_SIZE;
   public static final MethodSignature I_INVENTORY_GET_STACK_IN_SLOT;
   public static final MethodSignature NON_NULL_LIST_GET;
   public static final MethodSignature TEXTURE_MANAGER_BIND_TEXTURE;
   public static final MethodSignature WORLD_GET_BLOCK_STATE;

   protected MethodSignature(ClassName className, MethodName methodName, Descriptor descriptor) {
      super(className, methodName, descriptor);
   }

   private MethodSignature(ClassName className, Name methodName, Descriptor descriptor) {
      super(className, methodName, descriptor);
   }

   public static MethodSignature of(ClassName className, MethodName methodName, MethodDescriptor descriptor) {
      return new MethodSignature(className, methodName, descriptor);
   }

   public static MethodSignature of(ClassName className, String methodNormalName, String methodSrgName, MethodDescriptor descriptor) {
      return new MethodSignature(className, MethodName.of(className, methodNormalName, methodSrgName), descriptor);
   }

   public static MethodSignature of(ClassName className, String methodName, MethodDescriptor descriptor) {
      return new MethodSignature(className, MethodName.of(className, methodName), descriptor);
   }

   public static MethodSignature of(String className, String methodName, MethodDescriptor descriptor) {
      return of(ClassName.from(className), methodName, descriptor);
   }

   public boolean complyWith(MethodNode method) {
      return this.name.complyWith(method.name) && this.descriptor.complyWith(method.desc);
   }

   public boolean complyWith(MethodInsnNode instruct) {
      return this.className.complyWith(instruct.owner) && this.name.complyWith(instruct.name) && this.descriptor.complyWith(instruct.desc);
   }

   public MethodSignature with(String className) {
      return new MethodSignature(ClassName.from(className), this.name, this.descriptor);
   }

   static {
      ENTITY_ON_ENTITY_UPDATE = of(Classes.ENTITY.name, "onEntityUpdate", "onEntityUpdate", MethodDescriptor.VOID);
      ENTITY_ON_UPDATE = of(Classes.ENTITY.name, "onUpdate", "onUpdate", MethodDescriptor.VOID);
      FLUID_TANK_GET_FLUID = of(Classes.FLUID_TANK.name, "getFluid", MethodDescriptor.of(Classes.FLUID_STACK));
      FLUID_TANK_DRAIN = of(Classes.FLUID_TANK.name, "drain", MethodDescriptor.of(Classes.FLUID_STACK).withParameters(Types.INT, Types.BOOLEAN));
      ITEM_STACK_GET_COUNT = of(Classes.ITEM_STACK.name, "getCount", "getCount", MethodDescriptor.of(Types.INT));
      ITEM_STACK_SET_COUNT = of(Classes.ITEM_STACK.name, "setCount", "setCount", MethodDescriptor.VOID.withParameters(Types.INT));
      ITEM_STACK_SHRINK = of(Classes.ITEM_STACK.name, "shrink", "shrink", MethodDescriptor.VOID.withParameters(Types.INT));
      ITEM_STACK_SPLIT_STACK = of(Classes.ITEM_STACK.name, "splitStack", "splitStack", MethodDescriptor.of(Classes.ITEM_STACK).withParameters(Types.INT));
      I_INVENTORY_DECR_STACK_SIZE = of(Classes.IINVENTORY.name, "decrStackSize", "decrStackSize", MethodDescriptor.of(Classes.ITEM_STACK).withParameters(Types.INT, Types.INT));
      I_INVENTORY_GET_STACK_IN_SLOT = of(Classes.IINVENTORY.name, "getStackInSlot", "getStackInSlot", MethodDescriptor.of(Classes.ITEM_STACK).withParameters(Types.INT));
      NON_NULL_LIST_GET = of(Classes.NON_NULL_LIST.name, "get", MethodDescriptor.of(Classes.OBJECT).withParameters(Types.INT));
      TEXTURE_MANAGER_BIND_TEXTURE = of(Classes.TEXTURE_MANAGER.name, "bindTexture", "bindTexture", MethodDescriptor.VOID.withParameters(Classes.RESOURCE_LOCATION));
      WORLD_GET_BLOCK_STATE = of(Classes.WORLD.name, "getBlockState", "getBlockState", MethodDescriptor.of(Classes.IBLOCK_STATE).withParameters(Classes.BLOCK_POS));
   }
}
