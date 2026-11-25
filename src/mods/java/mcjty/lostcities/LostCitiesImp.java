package mcjty.lostcities;

import javax.annotation.Nullable;
import mcjty.lostcities.api.ILostChunkGenerator;
import mcjty.lostcities.api.ILostCities;
import mcjty.lostcities.dimensions.world.WorldTypeTools;

public class LostCitiesImp implements ILostCities {
   public LostCitiesImp() {
   }

   @Nullable
   public ILostChunkGenerator getLostGenerator(int dimension) {
      return WorldTypeTools.getChunkGenerator(dimension);
   }
}
