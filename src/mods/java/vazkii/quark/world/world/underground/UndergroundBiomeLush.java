package vazkii.quark.world.world.underground;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenShrub;
import vazkii.quark.base.module.ConfigHelper;

public class UndergroundBiomeLush extends BasicUndergroundBiome {
   private final WorldGenShrub shrubGen = new WorldGenShrub(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState());
   public static double grassChance;
   public static double shrubChance;
   public static double vineChance;

   public UndergroundBiomeLush() {
      super(Blocks.GRASS.getDefaultState(), (IBlockState)null, (IBlockState)null);
   }

   public void finalFloorPass(World world, BlockPos pos) {
      if (world.rand.nextDouble() < grassChance) {
         ItemDye.applyBonemeal(new ItemStack(Items.DYE, 1, 14), world, pos);
      }

      if (world.rand.nextDouble() < shrubChance) {
         this.shrubGen.generate(world, world.rand, pos.up());
      }

   }

   public void finalWallPass(World world, BlockPos pos) {
      for(EnumFacing facing : EnumFacing.HORIZONTALS) {
         BlockPos off = pos.offset(facing);
         BlockPos up = off.up();
         if (this.isCeiling(world, up, world.getBlockState(up)) && world.rand.nextDouble() < vineChance) {
            IBlockState stateAt = world.getBlockState(off);

            boolean did;
            for(did = false; stateAt.getBlock().isAir(stateAt, world, off) && off.getY() > 0; did = true) {
               world.setBlockState(off, Blocks.VINE.getDefaultState().withProperty(BlockVine.getPropertyFor(facing.getOpposite()), Boolean.valueOf(true)), 2);
               off = off.down();
               stateAt = world.getBlockState(off);
            }

            if (did) {
               return;
            }
         }
      }

   }

   public void setupConfig(String category) {
      grassChance = ConfigHelper.loadLegacyPropChance("Grass Percentage Chance", category, "Grass Chance", "The chance grass will spawn", 0.05D);
      shrubChance = ConfigHelper.loadLegacyPropChance("Shrub Percentage Chance", category, "Shrub Chance", "The chance shrubs will spawn", 0.01D);
      vineChance = ConfigHelper.loadLegacyPropChance("Vine Percentage Chance", category, "Vine Chance", "The chance vines will spawn", 0.125D);
   }
}
