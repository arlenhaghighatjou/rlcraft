package svenhjol.charm.world.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces.Church;
import net.minecraft.world.gen.structure.StructureVillagePieces.Field1;
import net.minecraft.world.gen.structure.StructureVillagePieces.Field2;
import net.minecraft.world.gen.structure.StructureVillagePieces.Hall;
import net.minecraft.world.gen.structure.StructureVillagePieces.House1;
import net.minecraft.world.gen.structure.StructureVillagePieces.House2;
import net.minecraft.world.gen.structure.StructureVillagePieces.House3;
import net.minecraft.world.gen.structure.StructureVillagePieces.House4Garden;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraft.world.gen.structure.StructureVillagePieces.Well;
import net.minecraft.world.gen.structure.StructureVillagePieces.WoodHut;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.charm.world.decorator.inner.VillageInnerDecorator;
import svenhjol.charm.world.decorator.outer.Barrels;
import svenhjol.charm.world.decorator.outer.Crops;
import svenhjol.charm.world.decorator.outer.Erosion;
import svenhjol.charm.world.decorator.outer.Flowers;
import svenhjol.charm.world.decorator.outer.Lights;
import svenhjol.charm.world.decorator.outer.Mobs;
import svenhjol.charm.world.decorator.outer.Mushrooms;
import svenhjol.charm.world.decorator.outer.Pumpkins;
import svenhjol.charm.world.decorator.outer.Trees;
import svenhjol.meson.Feature;
import svenhjol.meson.Meson;
import svenhjol.meson.decorator.MesonOuterDecorator;
import svenhjol.meson.event.StructureEventBase;
import svenhjol.meson.helper.WorldHelper;

public class VillageDecorations extends Feature {
   public static boolean armorStands;
   public static boolean torches;
   public static boolean beds;
   public static boolean storage;
   public static boolean itemFrames;
   public static boolean functionalBlocks;
   public static boolean decorativeBlocks;
   public static boolean carpet;
   public static double cropsChance;
   public static double flowersChance;
   public static double lightsChance;
   public static double mobsChance;
   public static double barrelsChance;
   public static double pumpkinsChance;
   public static double treesChance;
   public static double mushroomsChance;
   public static boolean treesHaveVines;
   public static boolean zombieVillageErosion;
   public static boolean vanillaTools;
   public static double golemsWeight;
   public static double barrelsWeight;
   public static double pumpkinsWeight;
   public static double mobsWeight;
   public static int erosionDamage;
   public static float common = 0.88F;
   public static float uncommon = 0.35F;
   public static float valuable = 0.05F;
   public static float rare = 0.005F;
   public static Map<Integer, Map<ChunkPos, Long>> villageChunks = new HashMap();
   public static List<ChunkPos> villageInfested = new ArrayList();

   public VillageDecorations() {
   }

   public String getDescription() {
      return "Improves village aesthetics with internal and external decoration.\nHouses and huts are populated with functional and decorative blocks according to a career/profession.\nVillage area can have more trees, flowers, crops, mobs and lights.";
   }

   public void configure() {
      super.configure();
      common = (float)this.propDouble("Common chance", "Chance (out of 1.0) of items and blocks considered 'common' to spawn.", 0.9D);
      uncommon = (float)this.propDouble("Uncommon chance", "Chance (out of 1.0) of items and blocks considered 'uncommon' to spawn.", 0.35D);
      valuable = (float)this.propDouble("Valuable chance", "Chance (out of 1.0) of items and blocks considered 'valuable' to spawn.", 0.05D);
      rare = (float)this.propDouble("Rare chance", "Chance (out of 1.0) of items and blocks considered 'rare' to spawn.", 0.005D);
      armorStands = this.propBoolean("Armor Stands", "Armor Stands appear in village houses.", true);
      torches = this.propBoolean("Torches", "Torches appear in village houses and around fields to stop water freezing.", true);
      beds = this.propBoolean("Beds", "Beds appear in village houses.", true);
      storage = this.propBoolean("Storage", "Chests, crates and barrels (if enabled) appear in village houses, with loot specific to the house theme.", true);
      itemFrames = this.propBoolean("Item Frames", "Item frames appear on the walls of village houses, with items specific to the house theme.", true);
      functionalBlocks = this.propBoolean("Functional Blocks", "Functional / Interactive blocks appear in village houses, such as anvils, cauldrons and crafting tables.", true);
      decorativeBlocks = this.propBoolean("Decorative Blocks", "Decorative blocks appear in village houses, such as bookshelves, polished stone and planks.", true);
      carpet = this.propBoolean("Carpet", "Coloured rugs appear in village houses and on the top of the village well.", true);
      cropsChance = this.propDouble("Crops outside", "Chance (out of 1.0) of a village having tilled soil, crops and water wells within its boundary.", 0.1D);
      flowersChance = this.propDouble("Flowers outside", "Chance (out of 1.0) of a village having more flowers (up to 3 types chosen based on village seed) within its boundary.", 0.9D);
      lightsChance = this.propDouble("Lights outside", "Chance (out of 1.0) of a village having torches and lanterns scattered within its boundary.", 0.8D);
      mobsChance = this.propDouble("Mobs outside", "Chance (out of 1.0) of a village having more dogs, cats and golems within its boundary.\nIf the village is in a snowy biome, this option lets snow golems spawn.", 0.75D);
      barrelsChance = this.propDouble("Barrels and composters outside", "Chance (out of 1.0) of a village having barrels containing farming-related loot and composters scattered within its boundary.", 0.6D);
      pumpkinsChance = this.propDouble("Pumpkins outside", "Chance (out of 1.0) of a village having pumpkins (and rarely melons) scattered within its boundary.", 0.6D);
      treesChance = this.propDouble("Trees outside", "Chance (out of 1.0) of a village having different types of trees spawn within its boundary.", 0.75D);
      mushroomsChance = this.propDouble("Mushrooms outside", "Chance (out of 1.0) of a village having mushrooms spawn, including large mushrooms, within its boundary.", 0.75D);
      golemsWeight = this.propDouble("Extra golem weight", "Chance (out of 1.0) of a chunk within the village boundary spawning a golem.\nThis is only valid if the 'Mobs outside' config option allows it.", 0.1D);
      barrelsWeight = this.propDouble("Extra barrels weight", "Chance (out of 1.0) of a chunk within the village boundary spawning a barrel.\nThis is only valid if the 'Barrels outside' config option allows it.", 0.7D);
      pumpkinsWeight = this.propDouble("Extra pumpkins weight", "Chance (out of 1.0) of a chunk within the village boundary spawning some pumpkins.\nThis is only valid if the 'Pumpkins outside' config option allows it.", 0.7D);
      mobsWeight = this.propDouble("Extra mobs weight", "Chance (out of 1.0) of a chunk allowing any kind of mob generation.\nThe greater the chance, the more likely a mob will be considered to spawn.\nThis is only valid is the 'Mobs outside' config option allows it.", 0.75D);
      treesHaveVines = this.propBoolean("Trees can have vines", "If true, trees that spawn within the village boundary can have vines hanging from them.\nThis is only valid if the 'Trees outside' config option allows it.\nNOTE: chance for trees to spawn with cocoa beans when vines are enabled.", false);
      zombieVillageErosion = this.propBoolean("Zombie villages are eroded", "If true, zombie villages have damaged and eroded buildings and structures.", true);
      erosionDamage = this.propInt("Zombie village erosion damage", "Number of passes that the generator will erode structures in a zombie village chunk.", 512);
      vanillaTools = this.propBoolean("Vanilla tools in frames and stands", "If true, vanilla tools can spawn in item frames and armor stands.\nThis is only valid if the 'Item frames' and 'Armor stands' config option allows it.", true);
   }

   @SubscribeEvent
   public void onPopulate(Populate event) {
      if (event != null && event.isHasVillageGenerated()) {
         World world = event.getWorld();
         ChunkPos chunk = new ChunkPos(event.getChunkX(), event.getChunkZ());
         int dim = world.provider.getDimension();
         if (!villageChunks.containsKey(Integer.valueOf(dim))) {
            villageChunks.put(Integer.valueOf(dim), new HashMap());
         }

         if (villageChunks.isEmpty() || ((Map)villageChunks.get(Integer.valueOf(dim))).isEmpty() || ((Map)villageChunks.get(Integer.valueOf(dim))).get(chunk) == null) {
            long seed = WorldHelper.getNearestVillageSeed(world, new BlockPos(chunk.x << 4, 0, chunk.z << 4));
            if (seed == 0L) {
               Meson.debug("Failed to get the seed for the nearest village...");
               return;
            }

            ((Map)villageChunks.get(Integer.valueOf(dim))).put(chunk, Long.valueOf(seed));
         }
      }

   }

   @SubscribeEvent
   public void onDecorate(Decorate event) {
      int dim = event.getWorld().provider.getDimension();
      if (villageChunks.containsKey(Integer.valueOf(dim)) && ((Map)villageChunks.get(Integer.valueOf(dim))).get(event.getChunkPos()) != null && event.getType().equals(EventType.FLOWERS)) {
         World world = event.getWorld();
         ChunkPos chunk = event.getChunkPos();
         BlockPos pos = new BlockPos(chunk.x << 4, 0, chunk.z << 4);
         EaglercraftRandom eventRand = event.getRand();
         EaglercraftRandom rand = new EaglercraftRandom();
         long villageSeed = ((Long)((Map)villageChunks.get(Integer.valueOf(dim))).get(chunk)).longValue();
         rand.setSeed(villageSeed);
         List<ChunkPos> chunks = new ArrayList();

         for(ChunkPos c : ((Map)villageChunks.get(Integer.valueOf(dim))).keySet()) {
            if (((Long)((Map)villageChunks.get(Integer.valueOf(dim))).get(c)).longValue() == villageSeed) {
               chunks.add(c);
            }
         }

         List<MesonOuterDecorator> decorators = new ArrayList();
         if ((double)rand.nextFloat() <= flowersChance) {
            decorators.add(new Flowers(world, pos, rand, chunks));
         }

         if ((double)rand.nextFloat() <= lightsChance) {
            decorators.add(new Lights(world, pos, eventRand, chunks));
         }

         if ((double)rand.nextFloat() <= mobsChance) {
            decorators.add(new Mobs(world, pos, eventRand, chunks));
         }

         if ((double)rand.nextFloat() <= cropsChance) {
            decorators.add(new Crops(world, pos, eventRand, chunks));
         }

         if ((double)rand.nextFloat() <= barrelsChance) {
            decorators.add(new Barrels(world, pos, eventRand, chunks));
         }

         if ((double)rand.nextFloat() <= pumpkinsChance) {
            decorators.add(new Pumpkins(world, pos, eventRand, chunks));
         }

         if ((double)rand.nextFloat() <= treesChance) {
            decorators.add(new Trees(world, pos, eventRand, chunks));
         }

         if ((double)rand.nextFloat() <= mushroomsChance) {
            decorators.add(new Mushrooms(world, pos, eventRand, chunks));
         }

         if (zombieVillageErosion && villageInfested.contains(chunk)) {
            decorators.add(new Erosion(world, pos, rand, chunks));
         }

         decorators.forEach(MesonOuterDecorator::generate);
         ((Map)villageChunks.get(Integer.valueOf(dim))).remove(chunk);
      }

   }

   @SubscribeEvent
   public void onAddComponentParts(StructureEventBase.Post event) {
      if (event.getComponent() instanceof Village && !event.getWorld().isRemote) {
         World world = event.getWorld();
         StructureBoundingBox box = event.getBox();
         Village component = (Village)event.getComponent();
         VillageInnerDecorator decorator = null;
         if (component.getClass() == Church.class) {
            decorator = new VillageInnerDecorator.Church(component, world, box);
         }

         if (component.getClass() == Field2.class) {
            decorator = new VillageInnerDecorator.Field1(component, world, box);
         }

         if (component.getClass() == Field1.class) {
            decorator = new VillageInnerDecorator.Field2(component, world, box);
         }

         if (component.getClass() == Hall.class) {
            decorator = new VillageInnerDecorator.Hall(component, world, box);
         }

         if (component.getClass() == House1.class) {
            decorator = new VillageInnerDecorator.House1(component, world, box);
         }

         if (component.getClass() == House2.class) {
            decorator = new VillageInnerDecorator.House2(component, world, box);
         }

         if (component.getClass() == House3.class) {
            decorator = new VillageInnerDecorator.House3(component, world, box);
         }

         if (component.getClass() == House4Garden.class) {
            decorator = new VillageInnerDecorator.House4(component, world, box);
         }

         if (component.getClass() == WoodHut.class) {
            decorator = new VillageInnerDecorator.WoodHut(component, world, box);
         }

         if (component instanceof Well) {
            decorator = new VillageInnerDecorator.Well(component, world, box);
         }

         if (decorator != null) {
            if (decorator.isZombieInfested()) {
               villageInfested.add(decorator.getChunkPos());
            }

            decorator.generate();
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean hasTerrainSubscriptions() {
      return true;
   }
}
