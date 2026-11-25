package me.jellysquid.mods.phosphor.api;

public interface IChunkLightingData {
   short[] getNeighborLightChecks();

   void setNeighborLightChecks(short[] var1);

   boolean isLightInitialized();

   void setLightInitialized(boolean var1);

   void setSkylightUpdatedPublic();
}
