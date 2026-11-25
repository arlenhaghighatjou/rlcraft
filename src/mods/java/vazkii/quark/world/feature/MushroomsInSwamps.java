package vazkii.quark.world.feature;

import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import vazkii.quark.base.module.Feature;

public class MushroomsInSwamps extends Feature {
   private final WorldGenerator bigMushroomGen = new WorldGenBigMushroom();
   public static double bigMushroomsPerChunk;

   public MushroomsInSwamps() {
   }

   public void setupConfig() {
      bigMushroomsPerChunk = this.loadPropDouble("Big Mushroom count per chunk", "Must be an integer if above 1. If below 1, works as a chance to generate one per chunk.", 0.5D);
   }

   @SubscribeEvent
   public void decorate(Decorate event) {
      World world = event.getWorld();
      if (event.getPlacementPos() != null) {
         Biome biome = world.getBiome(event.getPlacementPos());
         Random rand = event.getRand();
         if ((biome == Biomes.SWAMPLAND || biome == Biomes.MUTATED_SWAMPLAND) && event.getType() == EventType.BIG_SHROOM) {
            if (bigMushroomsPerChunk < 1.0D && rand.nextDouble() > bigMushroomsPerChunk) {
               return;
            }

            int amount = (int)Math.max(1.0D, bigMushroomsPerChunk);

            for(int i = 0; i < amount; ++i) {
               int x = rand.nextInt(16) + 8;
               int y = rand.nextInt(16) + 8;
               this.bigMushroomGen.generate(world, rand, world.getHeight(event.getPlacementPos().add(x, 0, y)));
            }

            event.setResult(Result.DENY);
         }
      }

   }

   public boolean hasTerrainSubscriptions() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Big Mushrooms In Swamps";
   }
}
