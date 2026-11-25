package sereneseasons.season;

import net.minecraft.util.math.BlockPos;
import sereneseasons.api.season.Season;

public interface ISeasonedWorld {
   boolean canSnowAtInSeason(BlockPos var1, boolean var2, Season var3);

   boolean canBlockFreezeInSeason(BlockPos var1, boolean var2, Season var3);
}
