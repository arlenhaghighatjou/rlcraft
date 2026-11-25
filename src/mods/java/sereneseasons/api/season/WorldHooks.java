package sereneseasons.api.season;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sereneseasons.season.SeasonASMHelper;

public class WorldHooks {
   public WorldHooks() {
   }

   public static boolean canSnowAtInSeason(World world, BlockPos pos, boolean checkLight, ISeasonState seasonState) {
      return SeasonASMHelper.canSnowAtInSeason(world, pos, checkLight, seasonState);
   }

   public static boolean canBlockFreezeInSeason(World world, BlockPos pos, boolean noWaterAdj, ISeasonState seasonState) {
      return SeasonASMHelper.canBlockFreezeInSeason(world, pos, noWaterAdj, seasonState);
   }

   public static boolean isRainingAtInSeason(World world, BlockPos pos, ISeasonState seasonState) {
      return SeasonASMHelper.isRainingAtInSeason(world, pos, seasonState);
   }
}
