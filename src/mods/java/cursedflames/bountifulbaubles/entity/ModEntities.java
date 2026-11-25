package cursedflames.bountifulbaubles.entity;

import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {
   public static Entity beenade;

   public ModEntities() {
   }

   public static <T extends Entity> void register(String name, Class<T> c, int id) {
      EntityRegistry.registerModEntity(new ResourceLocation("bountifulbaubles", name), c, name, id, BountifulBaubles.instance, 128, 10, true);
   }

   public static <T extends Entity> void registerWithRenderer(String name, Class<T> c, Item i, int id) {
      register(name, c, id);
      BountifulBaubles.proxy.registerWithRenderer(name, c, i, id);
   }

   public static <T extends Entity> void registerWithRenderer(String name, Class<T> c, ResourceLocation texture, int id) {
      register(name, c, id);
      BountifulBaubles.proxy.registerWithRenderer(name, c, texture, id);
   }

   public static void registerEntities() {
      int id = 0;
      registerWithRenderer("grenade", EntityGrenade.class, ModItems.grenade, id++);
      registerWithRenderer("beenade", EntityBeenade.class, ModItems.beenade, id++);
      registerWithRenderer("bee", EntityBee.class, new ResourceLocation("bountifulbaubles", "textures/other/bee.png"), id++);
      register("flare", EntityFlare.class, id++);
      BountifulBaubles.proxy.registerEntityRenderingHandlers();
   }
}
