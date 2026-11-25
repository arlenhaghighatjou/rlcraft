package rtg.api.world;

import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rtg.api.util.Logger;
import rtg.api.util.noise.CellularNoise;
import rtg.api.util.noise.OpenSimplexNoise;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.util.noise.SpacedCellularNoise;
import rtg.api.world.gen.RTGChunkGenSettings;

@EventBusSubscriber(
   modid = "rtg"
)
public final class RTGWorld {
   public static final float ACTUAL_RIVER_PROPORTION = 0.09375F;
   public static final float RIVER_FLATTENING_ADDEND = 0.10344828F;
   private static final double RIVER_LARGE_BEND_SIZE_BASE = 140.0D;
   private static final double RIVER_SMALL_BEND_SIZE_BASE = 30.0D;
   private static final double RIVER_SEPARATION_BASE = 975.0D;
   private static final double RIVER_VALLEY_LEVEL_BASE = 0.3111111111111111D;
   private static final float LAKE_FREQUENCY_BASE = 649.0F;
   private static final float LAKE_SHORE_LEVEL_BASE = 0.035F;
   private static final float LAKE_DEPRESSION_LEVEL = 0.15F;
   private static final float LAKE_BEND_SIZE_LARGE = 80.0F;
   private static final float LAKE_BEND_SIZE_MEDIUM = 30.0F;
   private static final float LAKE_BEND_SIZE_SMALL = 12.0F;
   private static final int SIMPLEX_INSTANCE_COUNT = 10;
   private static final int CELLULAR_INSTANCE_COUNT = 5;
   private static final Map<World, RTGWorld> INSTANCE_CACHE = new HashMap();
   private final World world;
   private final RTGChunkGenSettings generatorSettings;
   private final SimplexNoise[] simplexNoiseInstances = new SimplexNoise[10];
   private final CellularNoise[] cellularNoiseInstances = new CellularNoise[5];
   private EaglercraftRandom generatorRandom = null;

   private RTGWorld(World world) {
      this.world = world;
      this.generatorSettings = RTGChunkGenSettings.Factory.jsonToFactory(world.getWorldInfo().getGeneratorOptions()).build();

      for(int i = 0; i < 10; ++i) {
         this.simplexNoiseInstances[i] = new OpenSimplexNoise(this.seed() + (long)i);
      }

      for(int i = 0; i < 5; ++i) {
         this.cellularNoiseInstances[i] = new SpacedCellularNoise(this.seed() + (long)i);
      }

   }

   public static RTGWorld getInstance(World world) {
      if (!INSTANCE_CACHE.containsKey(world)) {
         INSTANCE_CACHE.put(world, new RTGWorld(world));
      }

      return (RTGWorld)INSTANCE_CACHE.get(world);
   }

   public void setRandom(EaglercraftRandom random) {
      if (this.generatorRandom == null) {
         this.generatorRandom = random;
      }

   }

   public World world() {
      return this.world;
   }

   public RTGChunkGenSettings getGeneratorSettings() {
      return this.generatorSettings;
   }

   public long seed() {
      return this.world().getSeed();
   }

   public EaglercraftRandom rand() {
      return this.generatorRandom;
   }

   public long getChunkSeed(int chunkX, int chunkZ) {
      long seed = this.world().getSeed();
      EaglercraftRandom rand = new EaglercraftRandom(seed);
      return (long)chunkX * (rand.nextLong() / 2L * 2L + 1L) + (long)chunkZ * (rand.nextLong() / 2L * 2L + 1L) ^ seed;
   }

   public SimplexNoise simplexInstance(int index) {
      if (index >= this.simplexNoiseInstances.length) {
         index = 0;
      }

      return this.simplexNoiseInstances[index];
   }

   public CellularNoise cellularInstance(int index) {
      if (index >= this.cellularNoiseInstances.length) {
         index = 0;
      }

      return this.cellularNoiseInstances[index];
   }

   public double getRiverLargeBendSize() {
      return 140.0D * (double)this.generatorSettings.riverBendMult;
   }

   public double getRiverSmallBendSize() {
      return 30.0D * (double)this.generatorSettings.riverBendMult;
   }

   public double getRiverSeparation() {
      return 975.0D / (double)this.generatorSettings.riverFrequency;
   }

   public double getRiverValleyLevel() {
      return 0.3111111111111111D * (double)this.generatorSettings.riverSizeMult * (double)this.generatorSettings.riverFrequency;
   }

   public float getLakeFrequency() {
      return 649.0F * this.generatorSettings.RTGlakeFreqMult;
   }

   public float getLakeShoreLevel() {
      return 0.035F * this.generatorSettings.RTGlakeFreqMult * this.generatorSettings.RTGlakeSizeMult;
   }

   public float getLakeDepressionLevel() {
      return 0.15F * this.generatorSettings.RTGlakeFreqMult * this.generatorSettings.RTGlakeSizeMult;
   }

   public float getLakeBendSizeLarge() {
      return 80.0F * this.generatorSettings.RTGlakeShoreBend;
   }

   public float getLakeBendSizeMedium() {
      return 30.0F * this.generatorSettings.RTGlakeShoreBend;
   }

   public float getLakeBendSizeSmall() {
      return 12.0F * this.generatorSettings.RTGlakeShoreBend;
   }

   @SubscribeEvent
   public static void onWorldLoad(Load event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         Logger.debug("WorldEvent.Load: WorldType: {}, DimID: {}, DimType: {}, BiomeProvider: {}, IChunkGenerator: {}", world.getWorldType().getClass().getSimpleName(), world.provider.getDimension(), world.provider.getDimensionType(), world.provider.getBiomeProvider().getClass().getName(), ((ChunkProviderServer)world.getChunkProvider()).chunkGenerator.getClass().getName());
         if (world.provider.getDimension() == 0) {
            Logger.info("World Seed: " + world.getSeed());
         }
      }

   }

   @SubscribeEvent
   public static void onWorldUnload(Unload event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         Logger.debug("WorldEvent.Unload: WorldType: {}, DimID: {}, DimType: {}, BiomeProvider: {}, IChunkGenerator: {}", world.getWorldType().getClass().getSimpleName(), world.provider.getDimension(), world.provider.getDimensionType(), world.provider.getBiomeProvider().getClass().getName(), ((ChunkProviderServer)world.getChunkProvider()).chunkGenerator.getClass().getName());
      }

      INSTANCE_CACHE.remove(world);
   }
}
