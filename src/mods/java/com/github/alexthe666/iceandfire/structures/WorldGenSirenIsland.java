package com.github.alexthe666.iceandfire.structures;

import com.github.alexthe666.iceandfire.entity.EntitySiren;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenSirenIsland extends WorldGenerator {
   public WorldGenSirenIsland() {
   }

   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int up = rand.nextInt(4) + 1;
      BlockPos center = position.up(up);
      int layer = 0;
      int sirens = 0;

      for(int sirensMax = 1 + rand.nextInt(3); !worldIn.getBlockState(center).isOpaqueCube() && center.getY() >= 0; center = center.down()) {
         ++layer;

         for(float i = 0.0F; i < (float)this.getRadius(layer, up); i = (float)((double)i + 0.5D)) {
            for(float j = 0.0F; (double)j < 6.283185307179586D * (double)i + (double)rand.nextInt(2); j = (float)((double)j + 0.5D)) {
               BlockPos stonePos = new BlockPos(Math.floor((double)center.getX() + Math.sin((double)j) * (double)i + (double)rand.nextInt(2)), (double)center.getY(), Math.floor((double)center.getZ() + Math.cos((double)j) * (double)i + (double)rand.nextInt(2)));
               worldIn.setBlockState(stonePos, this.getStone(rand), 3);
               BlockPos upPos = stonePos.up();
               if (worldIn.isAirBlock(upPos) && worldIn.isAirBlock(upPos.east()) && worldIn.isAirBlock(upPos.north()) && worldIn.isAirBlock(upPos.north().east()) && rand.nextInt(3) == 0 && sirens < sirensMax) {
                  ++sirens;
                  this.spawnSiren(worldIn, rand, upPos.north().east());
               }
            }
         }
      }

      ++layer;

      for(float i = 0.0F; i < (float)this.getRadius(layer, up); i = (float)((double)i + 0.5D)) {
         for(float j = 0.0F; (double)j < 6.283185307179586D * (double)i + (double)rand.nextInt(2); j = (float)((double)j + 0.5D)) {
            for(BlockPos stonePos = new BlockPos(Math.floor((double)center.getX() + Math.sin((double)j) * (double)i + (double)rand.nextInt(2)), (double)center.getY(), Math.floor((double)center.getZ() + Math.cos((double)j) * (double)i + (double)rand.nextInt(2))); !worldIn.getBlockState(stonePos).isOpaqueCube() && stonePos.getY() >= 0; stonePos = stonePos.down()) {
               worldIn.setBlockState(stonePos, this.getStone(rand), 3);
            }
         }
      }

      return false;
   }

   private int getRadius(int layer, int up) {
      return layer > up ? (int)((double)layer * 0.25D) + up : layer;
   }

   private IBlockState getStone(Random random) {
      int chance = random.nextInt(100);
      if (chance > 90) {
         return Blocks.MOSSY_COBBLESTONE.getDefaultState();
      } else if (chance > 70) {
         return Blocks.GRAVEL.getDefaultState();
      } else {
         return chance > 45 ? Blocks.COBBLESTONE.getDefaultState() : Blocks.STONE.getDefaultState();
      }
   }

   private void spawnSiren(World worldIn, Random rand, BlockPos position) {
      EntitySiren siren = new EntitySiren(worldIn);
      siren.setSinging(true);
      siren.setHairColor(rand.nextInt(2));
      siren.setSingingPose(rand.nextInt(2));
      siren.setPositionAndRotation((double)position.getX() + 0.5D, (double)(position.getY() + 1), (double)position.getZ() + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
      if (!worldIn.isRemote) {
         worldIn.spawnEntity(siren);
      }

   }
}
