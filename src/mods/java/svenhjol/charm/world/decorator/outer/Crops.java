package svenhjol.charm.world.decorator.outer;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockPotato;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import svenhjol.meson.decorator.MesonOuterDecorator;

public class Crops extends MesonOuterDecorator {
   public Crops(World world, BlockPos pos, EaglercraftRandom rand, List<ChunkPos> chunks) {
      super(world, pos, rand, chunks);
   }

   public void generate() {
      int max = 64;

      for(int i = 0; i < max; ++i) {
         int xx = this.rand.nextInt(16) + 8;
         int zz = this.rand.nextInt(16) + 8;
         BlockPos current = this.world.getHeight(this.pos.add(xx, 0, zz));
         boolean airAbove = this.world.getBlockState(current) == Blocks.AIR.getDefaultState();
         boolean grassBelow = this.world.getBlockState(current.offset(EnumFacing.DOWN)) == Blocks.GRASS.getDefaultState();
         if (airAbove && grassBelow) {
            if (this.rand.nextFloat() < 0.15F) {
               if (this.world.getBlockState(current.offset(EnumFacing.DOWN, 2)).isNormalCube() && this.world.getBlockState(current.offset(EnumFacing.NORTH).offset(EnumFacing.DOWN, 1)).isNormalCube() && this.world.getBlockState(current.offset(EnumFacing.EAST).offset(EnumFacing.DOWN, 1)).isNormalCube() && this.world.getBlockState(current.offset(EnumFacing.SOUTH).offset(EnumFacing.DOWN, 1)).isNormalCube() && this.world.getBlockState(current.offset(EnumFacing.WEST).offset(EnumFacing.DOWN, 1)).isNormalCube()) {
                  this.world.setBlockState(current.offset(EnumFacing.DOWN, 1), Blocks.WATER.getDefaultState(), 2);
                  this.world.setBlockState(current, Blocks.TRAPDOOR.getDefaultState().withProperty(BlockTrapDoor.OPEN, Boolean.valueOf(false)), 2);
               }
            } else {
               if (this.rand.nextFloat() < 0.85F) {
                  IBlockState farmland = Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, Integer.valueOf(6 + this.rand.nextInt(2)));
                  this.world.setBlockState(current.offset(EnumFacing.DOWN), farmland);
               }

               IBlockState crop = null;
               int type = this.rand.nextInt(8);
               if (type == 0) {
                  crop = ((BlockPotato)Blocks.POTATOES).withAge(6 + this.rand.nextInt(1));
               } else if (type == 1) {
                  crop = ((BlockCarrot)Blocks.CARROTS).withAge(6 + this.rand.nextInt(1));
               } else if (type == 2) {
                  crop = ((BlockBeetroot)Blocks.BEETROOTS).withAge(3);
               } else if (type <= 6) {
                  crop = ((BlockCrops)Blocks.WHEAT).withAge(6 + this.rand.nextInt(1));
               }

               if (crop != null) {
                  this.world.setBlockState(current, crop);
               }
            }
         }
      }

   }
}
