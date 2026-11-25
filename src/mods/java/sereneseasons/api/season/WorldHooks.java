package sereneseasons.api.season;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldHooks {
   public WorldHooks() {
   }

   public static boolean canSnowAtInSeason(World world, BlockPos pos, boolean checkLight, ISeasonState seasonState) {
      try {
         return ((Boolean)Class.forName("sereneseasons.season.SeasonASMHelper").getMethod("canSnowAtInSeason", World.class, BlockPos.class, Boolean.class, ISeasonState.class).invoke((Object)null, world, pos, checkLight, seasonState)).booleanValue();
      } catch (Exception var5) {
         throw new RuntimeException("An error occurred calling canSnowAtInSeason", var5);
      }
   }

   public static boolean canBlockFreezeInSeason(World world, BlockPos pos, boolean noWaterAdj, ISeasonState seasonState) {
      try {
         return ((Boolean)Class.forName("sereneseasons.season.SeasonASMHelper").getMethod("canBlockFreezeInSeason", World.class, BlockPos.class, Boolean.class, ISeasonState.class).invoke((Object)null, world, pos, noWaterAdj, seasonState)).booleanValue();
      } catch (Exception var5) {
         throw new RuntimeException("An error occurred calling canBlockFreezeInSeason", var5);
      }
   }

   public static boolean isRainingAtInSeason(World world, BlockPos pos, ISeasonState seasonState) {
      try {
         return ((Boolean)Class.forName("sereneseasons.season.SeasonASMHelper").getMethod("isRainingAtInSeason", World.class, BlockPos.class, ISeasonState.class).invoke((Object)null, world, pos, seasonState)).booleanValue();
      } catch (Exception var4) {
         throw new RuntimeException("An error occurred calling isRainingAtInSeason", var4);
      }
   }
}
