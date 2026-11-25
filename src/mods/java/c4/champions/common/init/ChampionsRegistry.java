package c4.champions.common.init;

import c4.champions.common.entity.EntityArcticSpark;
import c4.champions.common.entity.EntityCinderSpark;
import c4.champions.common.entity.EntityJail;
import c4.champions.common.item.ItemChampionPlacer;
import c4.champions.common.potion.PotionInjured;
import c4.champions.common.potion.PotionJailed;
import c4.champions.common.potion.PotionPlague;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder("champions")
@EventBusSubscriber(
   modid = "champions"
)
public class ChampionsRegistry {
   @ObjectHolder("jailed")
   public static final Potion jailed = null;
   @ObjectHolder("plague")
   public static final Potion plague = null;
   @ObjectHolder("injured")
   public static final Potion injured = null;
   @ObjectHolder("champion_egg")
   public static final Item championEgg = null;

   public ChampionsRegistry() {
   }

   @SubscribeEvent
   public static void registerItem(Register<Item> evt) {
      evt.getRegistry().register(new ItemChampionPlacer());
   }

   @SubscribeEvent
   public static void registerPotion(Register<Potion> evt) {
      evt.getRegistry().registerAll(new Potion[]{new PotionJailed(), new PotionPlague(), new PotionInjured()});
   }

   @SubscribeEvent
   public static void registerEntity(Register<EntityEntry> evt) {
      int id = 1;
      EntityEntry entry = EntityEntryBuilder.create().entity(EntityJail.class).id(new ResourceLocation("champions", "entity_jail"), id++).name("champions.entity_jail").tracker(160, Integer.MAX_VALUE, false).build();
      EntityEntry entry1 = EntityEntryBuilder.create().entity(EntityArcticSpark.class).id(new ResourceLocation("champions", "arctic_spark"), id++).name("champions.arctic_spark").tracker(80, 3, true).build();
      EntityEntry entry2 = EntityEntryBuilder.create().entity(EntityCinderSpark.class).id(new ResourceLocation("champions", "cinder_spark"), id++).name("champions.cinder_spark").tracker(80, 3, true).build();
      evt.getRegistry().registerAll(new EntityEntry[]{entry, entry1, entry2});
   }
}
