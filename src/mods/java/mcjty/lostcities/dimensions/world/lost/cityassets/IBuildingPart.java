package mcjty.lostcities.dimensions.world.lost.cityassets;

public interface IBuildingPart {
   Character getMetaChar(String var1);

   Integer getMetaInteger(String var1);

   boolean getMetaBoolean(String var1);

   Float getMetaFloat(String var1);

   String getMetaString(String var1);

   String getName();

   char[][] getVslices();

   char[] getVSlice(int var1, int var2);

   Palette getLocalPalette();

   int getSliceCount();

   int getXSize();

   int getZSize();
}
