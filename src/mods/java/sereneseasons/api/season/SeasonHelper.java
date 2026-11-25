package sereneseasons.api.season;

import net.minecraft.world.World;

public class SeasonHelper {
   public static SeasonHelper.ISeasonDataProvider dataProvider;

   public SeasonHelper() {
   }

   public static ISeasonState getSeasonState(World world) {
      ISeasonState data;
      if (!world.isRemote) {
         data = dataProvider.getServerSeasonState(world);
      } else {
         data = dataProvider.getClientSeasonState();
      }

      return data;
   }

   public interface ISeasonDataProvider {
      ISeasonState getServerSeasonState(World var1);

      ISeasonState getClientSeasonState();
   }
}
