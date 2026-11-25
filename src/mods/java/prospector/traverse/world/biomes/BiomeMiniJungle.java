package prospector.traverse.world.biomes;

import java.util.Random;
import net.minecraft.block.BlockTallGrass.EnumType;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMelon;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import prospector.traverse.config.TraverseConfig;
import prospector.traverse.world.WorldGenConstants;

public class BiomeMiniJungle extends Biome implements WorldGenConstants {
   public static BiomeProperties properties = new BiomeProperties("Mini Jungle");

   public BiomeMiniJungle() {
      super(properties);
      this.decorator.treesPerChunk = 30;
      this.decorator.flowersPerChunk = 5;
      this.decorator.grassPerChunk = 25;
      this.decorator.reedsPerChunk = 2;
      this.decorator.clayPerChunk = 3;
      this.decorator.waterlilyPerChunk = 12;
      this.spawnableMonsterList.add(new SpawnListEntry(EntityOcelot.class, 1, 1, 1));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 4, 4, 4));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityParrot.class, 40, 1, 2));
   }

   public int getSkyColorByTemp(float currentTemperature) {
      return TraverseConfig.disableCustomSkies ? super.getSkyColorByTemp(currentTemperature) : -3997717;
   }

   public WorldGenerator getRandomWorldGenForGrass(Random rand) {
      return rand.nextInt(4) == 0 ? new WorldGenTallGrass(EnumType.FERN) : new WorldGenTallGrass(EnumType.GRASS);
   }

   public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
      return (WorldGenAbstractTree)(rand.nextInt(10) == 0 ? BIG_TREE_FEATURE : new WorldGenTrees(false, 2 + rand.nextInt(3), JUNGLE_LOG, JUNGLE_LEAVES, true));
   }

   public void decorate(World worldIn, Random rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
      int i = rand.nextInt(16) + 8;
      int j = rand.nextInt(16) + 8;
      int height = worldIn.getHeight(pos.add(i, 0, j)).getY() * 2;
      if (height < 1) {
         height = 1;
      }

      int k = rand.nextInt(height);
      if (TerrainGen.decorate(worldIn, rand, pos, EventType.PUMPKIN)) {
         (new WorldGenMelon()).generate(worldIn, rand, pos.add(i, k, j));
      }

      WorldGenVines worldgenvines = new WorldGenVines();
      if (TerrainGen.decorate(worldIn, rand, pos, EventType.GRASS)) {
         for(int var12 = 0; var12 < 50; ++var12) {
            k = rand.nextInt(16) + 8;
            int l = 128;
            int i1 = rand.nextInt(16) + 8;
            worldgenvines.generate(worldIn, rand, pos.add(k, 128, i1));
            new EntityPig(worldIn);
         }
      }

   }

   static {
      properties.setTemperature(Biomes.JUNGLE.getDefaultTemperature());
      properties.setRainfall(Biomes.JUNGLE.getRainfall());
      properties.setBaseHeight(-0.1F);
      properties.setHeightVariation(0.5F);
      properties.setWaterColor(-14372836);
   }
}
