package lykrast.defiledlands.common.init;

import java.util.ArrayList;
import java.util.List;
import lykrast.defiledlands.client.render.entity.RenderBlastemFruit;
import lykrast.defiledlands.client.render.entity.RenderBlastemFruitBlazing;
import lykrast.defiledlands.client.render.entity.RenderBookWyrm;
import lykrast.defiledlands.client.render.entity.RenderDestroyer;
import lykrast.defiledlands.client.render.entity.RenderHost;
import lykrast.defiledlands.client.render.entity.RenderMourner;
import lykrast.defiledlands.client.render.entity.RenderRavagerProjectile;
import lykrast.defiledlands.client.render.entity.RenderScuttler;
import lykrast.defiledlands.client.render.entity.RenderShambler;
import lykrast.defiledlands.client.render.entity.RenderShamblerTwisted;
import lykrast.defiledlands.client.render.entity.RenderSlimeDefiled;
import lykrast.defiledlands.common.entity.boss.EntityDestroyer;
import lykrast.defiledlands.common.entity.boss.EntityMourner;
import lykrast.defiledlands.common.entity.monster.EntityHost;
import lykrast.defiledlands.common.entity.monster.EntityScuttler;
import lykrast.defiledlands.common.entity.monster.EntityShambler;
import lykrast.defiledlands.common.entity.monster.EntityShamblerTwisted;
import lykrast.defiledlands.common.entity.monster.EntitySlimeDefiled;
import lykrast.defiledlands.common.entity.passive.EntityBookWyrm;
import lykrast.defiledlands.common.entity.projectile.EntityBlastemFruit;
import lykrast.defiledlands.common.entity.projectile.EntityBlastemFruitBlazing;
import lykrast.defiledlands.common.entity.projectile.EntityRavagerProjectile;
import lykrast.defiledlands.common.util.DungeonDefiledList;
import lykrast.defiledlands.common.world.feature.WorldGenDungeonsDefiled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class ModEntities {
   private static int id = 1;
   private static List<EntityEntry> entityList = new ArrayList();

   public ModEntities() {
   }

   public static void init() {
      registerEntity(EntityShambler.class, "shambler", 1513239, 15461355);
      LootTableList.register(EntityShambler.LOOT);
      registerEntity(EntityShamblerTwisted.class, "shambler_twisted", 1513239, 16133678);
      LootTableList.register(EntityShamblerTwisted.LOOT);
      registerEntity(EntityScuttler.class, "scuttler", 2168867, 13503487);
      LootTableList.register(EntityScuttler.LOOT);
      registerEntity(EntityHost.class, "host", 3815994, 13136800);
      LootTableList.register(EntityHost.LOOT);
      registerEntity(EntitySlimeDefiled.class, "slime_defiled", 12479889, 13136800);
      LootTableList.register(EntitySlimeDefiled.LOOT);
      registerEntity(EntityBookWyrm.class, "book_wyrm", 4271169, 16710116);
      LootTableList.register(EntityBookWyrm.LOOT);
      registerEntity(EntityDestroyer.class, "the_destroyer", 11119017, 16382457);
      LootTableList.register(EntityDestroyer.LOOT);
      registerEntity(EntityMourner.class, "the_mourner", 2500134, 16382457);
      LootTableList.register(EntityMourner.LOOT);
      registerProjectile(EntityBlastemFruit.class, "blastem_fruit");
      registerProjectile(EntityBlastemFruitBlazing.class, "blastem_fruit_blazing");
      registerProjectile(EntityRavagerProjectile.class, "ravager_projectile", 3);
      DungeonDefiledList.addDungeonMob("scuttler", 160);
      DungeonDefiledList.addDungeonMob("host", 120);
      DungeonDefiledList.addDungeonMob("shambler_twisted", 20);
      LootTableList.register(WorldGenDungeonsDefiled.LOOT);
   }

   @SubscribeEvent
   public static void registerEntities(Register<EntityEntry> event) {
      for(EntityEntry e : entityList) {
         event.getRegistry().register(e);
      }

      entityList = null;
   }

   public static void registerEntity(Class<? extends Entity> entityClass, String name, int colorBack, int colorFront) {
      EntityEntryBuilder<Entity> builder = EntityEntryBuilder.create().entity(entityClass).name("defiledlands." + name).id(new ResourceLocation("defiledlands", name), id++).tracker(64, 3, true).egg(colorBack, colorFront);
      entityList.add(builder.build());
   }

   public static void registerProjectile(Class<? extends Entity> entityClass, String name) {
      registerProjectile(entityClass, name, 10);
   }

   public static void registerProjectile(Class<? extends Entity> entityClass, String name, int updateRate) {
      EntityEntryBuilder<Entity> builder = EntityEntryBuilder.create().entity(entityClass).name("defiledlands." + name).id(new ResourceLocation("defiledlands", name), id++).tracker(64, updateRate, true);
      entityList.add(builder.build());
   }

   @SideOnly(Side.CLIENT)
   public static void initModels() {
      RenderingRegistry.registerEntityRenderingHandler(EntityShambler.class, RenderShambler::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityShamblerTwisted.class, RenderShamblerTwisted::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityScuttler.class, RenderScuttler::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityDestroyer.class, RenderDestroyer::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityMourner.class, RenderMourner::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityHost.class, RenderHost::new);
      RenderingRegistry.registerEntityRenderingHandler(EntitySlimeDefiled.class, RenderSlimeDefiled::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityBookWyrm.class, RenderBookWyrm::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityBlastemFruit.class, (m) -> {
         return new RenderBlastemFruit(m, ModItems.blastemFruit, Minecraft.getMinecraft().getRenderItem());
      });
      RenderingRegistry.registerEntityRenderingHandler(EntityBlastemFruitBlazing.class, (m) -> {
         return new RenderBlastemFruitBlazing(m, ModItems.blastemFruitBlazing, Minecraft.getMinecraft().getRenderItem());
      });
      RenderingRegistry.registerEntityRenderingHandler(EntityRavagerProjectile.class, (m) -> {
         return new RenderRavagerProjectile(m, ModItems.pelletUmbrium, Minecraft.getMinecraft().getRenderItem());
      });
   }
}
