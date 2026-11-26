package de.mennomax.astikorcarts.init;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import de.mennomax.astikorcarts.client.render.RenderCargoCart;
import de.mennomax.astikorcarts.client.render.RenderMobCart;
import de.mennomax.astikorcarts.client.render.RenderPlowCart;
import de.mennomax.astikorcarts.entity.EntityCargoCart;
import de.mennomax.astikorcarts.entity.EntityMobCart;
import de.mennomax.astikorcarts.entity.EntityPlowCart;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import java.util.function.Function;

@ObjectHolder("astikorcarts")
public class ModEntities {
   public ModEntities() {
   }

   public static void registerRenders() {
      RenderingRegistry.registerEntityRenderingHandler(EntityCargoCart.class, RenderCargoCart::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityPlowCart.class, RenderPlowCart::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityMobCart.class, RenderMobCart::new);
   }

   public static class EntityRegistrationHandler {
      private static int id = 0;

      public EntityRegistrationHandler() {
      }

      public static void register(EventBus bus) {
         bus.registerGeneric(Register.class, EntityEntry.class, EntityRegistrationHandler::registerEntities);
      }

      public static void registerEntities(Register<EntityEntry> event) {
         event.getRegistry().registerAll(new EntityEntry[]{createEntry(EntityCargoCart.class, EntityCargoCart::new, "cargocart", 80, 3, false), createEntry(EntityPlowCart.class, EntityPlowCart::new, "plowcart", 80, 3, false), createEntry(EntityMobCart.class, EntityMobCart::new, "mobcart", 80, 3, false)});
      }

      private static <T extends Entity> EntityEntry createEntry(Class<T> entityClass, Function<World, T> factory, String name, int trackingRange, int updateFrequency, boolean sendVelocityUpdates) {
         ResourceLocation resourceLocation = new ResourceLocation("astikorcarts", name);
         return EntityEntryBuilder.<T>create().entity(entityClass).factory(factory).id(resourceLocation, id++).name(resourceLocation.toString()).tracker(trackingRange, updateFrequency, sendVelocityUpdates).build();
      }
   }
}
