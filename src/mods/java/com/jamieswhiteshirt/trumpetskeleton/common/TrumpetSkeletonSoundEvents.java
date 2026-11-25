package com.jamieswhiteshirt.trumpetskeleton.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraft.util.SoundEvent;

public class TrumpetSkeletonSoundEvents {
   public static SoundEvent ENTITY_TRUMPET_SKELETON_AMBIENT = null;
   public static SoundEvent E_PARROT_IM_TRUMPET_SKELETON = null;
   public static SoundEvent ITEM_TRUMPET_USE = null;

   public TrumpetSkeletonSoundEvents() {
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(SoundEvent.class, new ResourceLocation("trumpetskeleton:entity.trumpet_skeleton.ambient"), v -> ENTITY_TRUMPET_SKELETON_AMBIENT = v);
      ObjectHolderRegistry.INSTANCE.register(SoundEvent.class, new ResourceLocation("trumpetskeleton:entity.parrot.imitate.trumpet_skeleton"), v -> E_PARROT_IM_TRUMPET_SKELETON = v);
      ObjectHolderRegistry.INSTANCE.register(SoundEvent.class, new ResourceLocation("trumpetskeleton:item.trumpet.use"), v -> ITEM_TRUMPET_USE = v);
   }
}
