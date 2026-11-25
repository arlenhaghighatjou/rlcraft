package svenhjol.charm.world.decorator.outer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;
import svenhjol.meson.decorator.MesonOuterDecorator;

public class Flowers extends MesonOuterDecorator {
   public Flowers(World world, BlockPos pos, EaglercraftRandom rand, List<ChunkPos> chunks) {
      super(world, pos, rand, chunks);
   }

   public void generate() {
      Biome biome = this.world.getBiome(this.pos);
      List<WorldGenerator> generators = new ArrayList();
      if ((double)biome.getDefaultTemperature() > 1.0D) {
         generators.add(new WorldGenDeadBush());
      } else if (biome == Biomes.SWAMPLAND || biome == Biomes.MUTATED_SWAMPLAND) {
         generators.add(new WorldGenBigMushroom());
         generators.add(new WorldGenWaterlily());
      }

      List<EnumFlowerType> flowerTypes = Arrays.asList(EnumFlowerType.values());
      Collections.shuffle(flowerTypes, this.rand);

      for(EnumFlowerType flower : flowerTypes.subList(0, 2)) {
         BlockFlower block = flower == EnumFlowerType.DANDELION ? Blocks.YELLOW_FLOWER : Blocks.RED_FLOWER;
         generators.add(new WorldGenFlowers(block, flower));
      }

      generators.add(new WorldGenReed());
      int max = 2 + this.rand.nextInt(4);

      for(int i = 0; i < max; ++i) {
         int xx = this.rand.nextInt(16) + 8;
         int zz = this.rand.nextInt(16) + 8;
         WorldGenerator gen = (WorldGenerator)generators.get(this.rand.nextInt(generators.size()));
         if (gen != null) {
            gen.generate(this.world, this.rand, this.world.getHeight(this.pos.add(xx, 0, zz)));
         }
      }

   }
}
