package mcjty.lostcities.api;

import com.google.gson.JsonObject;

public interface ILostCityAsset {
   default void init() {
   }

   String getName();

   void readFromJSon(JsonObject var1);
}
