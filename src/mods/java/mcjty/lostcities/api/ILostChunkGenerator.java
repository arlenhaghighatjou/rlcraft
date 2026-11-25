package mcjty.lostcities.api;

public interface ILostChunkGenerator {
   ILostChunkInfo getChunkInfo(int var1, int var2);

   int getRealHeight(int var1);

   ILostCityAssetRegistry<ILostCityBuilding> getBuildings();

   ILostCityAssetRegistry<ILostCityMultiBuilding> getMultiBuildings();

   ILostCityAssetRegistry<ILostCityCityStyle> getCityStyles();
}
