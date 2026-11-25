package mcjty.lostcities.api;

import javax.annotation.Nullable;

public interface ILostCities {
   @Nullable
   ILostChunkGenerator getLostGenerator(int var1);
}
