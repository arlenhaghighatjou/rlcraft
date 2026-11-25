package mcjty.lostcities.api;

public interface ILostCityBuilding extends ILostCityAsset {
   float getPrefersLonely();

   int getMaxFloors();

   int getMaxCellars();

   int getMinFloors();

   int getMinCellars();

   char getFillerBlock();
}
