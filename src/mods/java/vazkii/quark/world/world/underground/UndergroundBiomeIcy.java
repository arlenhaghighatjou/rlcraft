package vazkii.quark.world.world.underground;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.ConfigHelper;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.feature.UndergroundBiomes;

public class UndergroundBiomeIcy extends BasicUndergroundBiome {
   public static double stalagmiteChance;
   public static boolean usePackedIce;

   public UndergroundBiomeIcy() {
      super(Blocks.ICE.getDefaultState(), (IBlockState)null, (IBlockState)null, true);
   }

   public void fillCeiling(World world, BlockPos pos, IBlockState state) {
      if (UndergroundBiomes.icystoneEnabled) {
         world.setBlockState(pos, UndergroundBiomes.icystoneState, 2);
      }

   }

   public void fillWall(World world, BlockPos pos, IBlockState state) {
      this.fillCeiling(world, pos, state);
   }

   public void fillFloor(World world, BlockPos pos, IBlockState state) {
      IBlockState placeState = this.floorState;
      if (usePackedIce) {
         placeState = Blocks.PACKED_ICE.getDefaultState();
      }

      BlockPos placePos = pos;
      world.setBlockState(pos, placeState, 2);
      if (stalagmiteChance > 0.0D && world.rand.nextDouble() < stalagmiteChance) {
         int height = 3 + world.rand.nextInt(3);

         for(int i = 0; i < height; ++i) {
            placePos = placePos.up();
            IBlockState stateAt = world.getBlockState(placePos);
            if (!world.getBlockState(placePos).getBlock().isAir(stateAt, world, placePos)) {
               break;
            }

            world.setBlockState(placePos, placeState, 2);
         }
      }

   }

   public void setupConfig(String category) {
      stalagmiteChance = ConfigHelper.loadLegacyPropChance("Stalagmite Percentage Chance", category, "Stalagmite Chance", "The chance stalagmites will spawn", 0.015D);
      usePackedIce = ModuleLoader.config.getBoolean("Use Packed Ice", category, true, "");
   }
}
