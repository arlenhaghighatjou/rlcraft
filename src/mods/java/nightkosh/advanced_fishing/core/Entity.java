package nightkosh.advanced_fishing.core;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import nightkosh.advanced_fishing.entity.item.EntityFireproofItem;
import nightkosh.advanced_fishing.entity.projectile.EntityCustomFishHook;
import nightkosh.advanced_fishing.entity.projectile.EntityLavaFishHook;

public class Entity {
   private static int mobId = 0;
   public static final String CUSTOM_FISH_HOOK_NAME = "CustomFishHook";
   public static final String LAVA_FISH_HOOK_NAME = "LavaFishHook";
   public static final String FIREPROOF_ITEM_NAME = "FireproofItem";
   public static final ResourceLocation CUSTOM_FISH_HOOK_ID = new ResourceLocation("advanced-fishing:custom_fish_hook");
   public static final ResourceLocation LAVA_FISH_HOOK_ID = new ResourceLocation("advanced-fishing:lava_fish_hook");
   public static final ResourceLocation FIREPROOF_ITEM_ID = new ResourceLocation("advanced-fishing:fireproof_item");

   private Entity() {
   }

   public static void registration() {
      registerModEntity(CUSTOM_FISH_HOOK_ID, EntityCustomFishHook.class, "CustomFishHook");
      registerModEntity(LAVA_FISH_HOOK_ID, EntityLavaFishHook.class, "LavaFishHook");
      registerModEntity(FIREPROOF_ITEM_ID, EntityFireproofItem.class, "FireproofItem");
   }

   private static void registerModEntity(ResourceLocation resource, Class<? extends net.minecraft.entity.Entity> entityClass, String entityName) {
      EntityRegistry.registerModEntity(resource, entityClass, entityName, mobId, "advanced-fishing", 100, 1, true);
      ++mobId;
   }
}
