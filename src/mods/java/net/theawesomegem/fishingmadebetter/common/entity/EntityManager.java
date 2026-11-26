package net.theawesomegem.fishingmadebetter.common.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityManager {
   public static final String EntityFMBLavaFishHookName = "FMBLavaFishHook";
   public static final String EntityFMBVoidFishHookName = "FMBVoidFishHook";
   public static final ResourceLocation EntityFMBLavaFishHookResource = new ResourceLocation("fishingmadebetter:fmblavafishhook");
   public static final ResourceLocation EntityFMBVoidFishHookResource = new ResourceLocation("fishingmadebetter:fmbvoidfishhook");

   private EntityManager() {
   }

   public static void register() {
      EntityRegistry.registerModEntity(EntityFMBLavaFishHookResource, EntityFMBLavaFishHook.class, "FMBLavaFishHook", 0, "fishingmadebetter", 100, 1, true, EntityFMBLavaFishHook::new);
      EntityRegistry.registerModEntity(EntityFMBVoidFishHookResource, EntityFMBVoidFishHook.class, "FMBVoidFishHook", 1, "fishingmadebetter", 100, 1, true, EntityFMBVoidFishHook::new);
   }
}
