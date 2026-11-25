package mcjty.lostcities.api;

import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ILostChunkInfo {
   boolean isCity();

   @Nullable
   ILostCityInfo getCityInfo();

   String getBuildingType();

   @Nonnull
   RailChunkType getRailType();

   int getRailLevel();

   int getCityLevel();

   int getNumFloors();

   int getNumCellars();

   float getDamage(int var1);

   int getRuinLevel();

   Collection<ILostExplosion> getExplosions();

   int getMaxHighwayLevel();

   @Nullable
   ILostSphere getSphere();
}
