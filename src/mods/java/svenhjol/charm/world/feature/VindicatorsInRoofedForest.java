package svenhjol.charm.world.feature;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.init.Biomes;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import svenhjol.meson.Feature;

public class VindicatorsInRoofedForest extends Feature {
   public static int weight;
   public static double johnny;

   public VindicatorsInRoofedForest() {
   }

   public String getDescription() {
      return "Vindicators can be found wandering in Roofed Forest biomes.";
   }

   public void configure() {
      super.configure();
      weight = this.propInt("Spawn weight", "The higher this value, the more vindicators will spawn.", 90);
      johnny = this.propDouble("Chance for Johnny", "Chance (out of 1.0) of a vindicator spawning as Johnny.", 0.05D);
   }

   public void init(FMLInitializationEvent event) {
      SpawnListEntry vindicatorEntry = new SpawnListEntry(EntityVindicator.class, weight, 1, 3);
      Biome roofedForest = Biomes.ROOFED_FOREST;
      roofedForest.getSpawnableList(EnumCreatureType.MONSTER).add(vindicatorEntry);
   }

   @SubscribeEvent
   public void onSpawn(CheckSpawn event) {
      if (!event.isSpawner() && event.getEntityLiving() instanceof EntityVindicator && event.getResult() != Result.DENY && event.getEntityLiving().world instanceof WorldServer) {
         if (event.getEntityLiving().getPosition().getY() < event.getEntityLiving().world.getSeaLevel()) {
            event.setResult(Result.DENY);
         }

         if ((double)event.getWorld().rand.nextFloat() <= johnny) {
            event.getEntityLiving().setCustomNameTag("Johnny");
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
