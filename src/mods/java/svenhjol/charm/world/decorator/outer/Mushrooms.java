package svenhjol.charm.world.decorator.outer;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenerator;
import svenhjol.meson.decorator.MesonOuterDecorator;

public class Mushrooms extends MesonOuterDecorator {
   public Mushrooms(World world, BlockPos pos, EaglercraftRandom rand, List<ChunkPos> chunks) {
      super(world, pos, rand, chunks);
   }

   public void generate() {
      List<Block> mushrooms = new ArrayList();
      int max = 5 + this.rand.nextInt(10);
      mushrooms.add(Blocks.BROWN_MUSHROOM);
      mushrooms.add(Blocks.RED_MUSHROOM);
      WorldGenerator bigMushroom = new WorldGenBigMushroom();

      for(int i = 0; i < max; ++i) {
         int xx = this.rand.nextInt(16) + 8;
         int zz = this.rand.nextInt(16) + 8;
         BlockPos forMushroom = this.world.getHeight(this.pos.add(xx, 0, zz));
         boolean airAbove = this.world.getBlockState(forMushroom) == Blocks.AIR.getDefaultState();
         boolean grassBelow = this.world.getBlockState(forMushroom.offset(EnumFacing.DOWN)) == Blocks.GRASS.getDefaultState();
         if (airAbove && grassBelow) {
            if (this.rand.nextFloat() < 0.75F) {
               this.world.setBlockState(forMushroom, ((Block)mushrooms.get(this.rand.nextInt(mushrooms.size()))).getDefaultState());
            }

            if (this.rand.nextFloat() < 0.75F) {
               bigMushroom.generate(this.world, this.rand, forMushroom);
            }
         }
      }

   }
}
