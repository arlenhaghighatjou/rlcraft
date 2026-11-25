package mcjty.lostcities.dimensions.world.lost.cityassets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import mcjty.lostcities.api.ILostCityAsset;

public class PredefinedCity implements ILostCityAsset {
   private String name;
   private int dimension;
   private int chunkX;
   private int chunkZ;
   private int radius;
   private String cityStyle;
   private final List<PredefinedCity.PredefinedBuilding> predefinedBuildings = new ArrayList();
   private final List<PredefinedCity.PredefinedStreet> predefinedStreets = new ArrayList();

   public PredefinedCity(JsonObject object) {
      this.readFromJSon(object);
   }

   public PredefinedCity(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void readFromJSon(JsonObject object) {
      this.name = object.get("name").getAsString();
      this.dimension = object.get("dimension").getAsInt();
      this.chunkX = object.get("chunkx").getAsInt();
      this.chunkZ = object.get("chunkz").getAsInt();
      this.radius = object.get("radius").getAsInt();
      if (object.has("citystyle")) {
         this.cityStyle = object.get("citystyle").getAsString();
      }

      for(JsonElement element : this.getArraySafe(object, "buildings")) {
         JsonObject o = element.getAsJsonObject();
         boolean multi;
         String building;
         if (o.has("building")) {
            building = o.get("building").getAsString();
            multi = false;
         } else {
            building = o.get("multibuilding").getAsString();
            multi = true;
         }

         boolean preventRuins = o.has("preventruins") && o.get("preventruins").getAsBoolean();
         int relChunkX = o.get("chunkx").getAsInt();
         int relChunkZ = o.get("chunkz").getAsInt();
         PredefinedCity.PredefinedBuilding b = new PredefinedCity.PredefinedBuilding(building, relChunkX, relChunkZ, multi, preventRuins);
         this.predefinedBuildings.add(b);
      }

      for(JsonElement element : this.getArraySafe(object, "streets")) {
         JsonObject o = element.getAsJsonObject();
         int relChunkX = o.get("chunkx").getAsInt();
         int relChunkZ = o.get("chunkz").getAsInt();
         PredefinedCity.PredefinedStreet b = new PredefinedCity.PredefinedStreet(relChunkX, relChunkZ);
         this.predefinedStreets.add(b);
      }

   }

   private JsonArray getArraySafe(JsonObject object, String key) {
      return object.has(key) ? object.get(key).getAsJsonArray() : new JsonArray();
   }

   public JsonObject writeToJSon() {
      JsonObject object = new JsonObject();
      object.add("type", new JsonPrimitive("city"));
      object.add("name", new JsonPrimitive(this.name));
      return object;
   }

   public List<PredefinedCity.PredefinedBuilding> getPredefinedBuildings() {
      return this.predefinedBuildings;
   }

   public List<PredefinedCity.PredefinedStreet> getPredefinedStreets() {
      return this.predefinedStreets;
   }

   public int getDimension() {
      return this.dimension;
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }

   public int getRadius() {
      return this.radius;
   }

   public String getCityStyle() {
      return this.cityStyle;
   }

   public static class PredefinedBuilding {
      private final String building;
      private final int relChunkX;
      private final int relChunkZ;
      private final boolean multi;
      private final boolean preventRuins;

      public PredefinedBuilding(String building, int relChunkX, int relChunkZ, boolean multi, boolean preventRuins) {
         this.building = building;
         this.relChunkX = relChunkX;
         this.relChunkZ = relChunkZ;
         this.multi = multi;
         this.preventRuins = preventRuins;
      }

      public String getBuilding() {
         return this.building;
      }

      public int getRelChunkX() {
         return this.relChunkX;
      }

      public int getRelChunkZ() {
         return this.relChunkZ;
      }

      public boolean isMulti() {
         return this.multi;
      }

      public boolean isPreventRuins() {
         return this.preventRuins;
      }
   }

   public static class PredefinedStreet {
      private final int relChunkX;
      private final int relChunkZ;

      public PredefinedStreet(int relChunkX, int relChunkZ) {
         this.relChunkX = relChunkX;
         this.relChunkZ = relChunkZ;
      }

      public int getRelChunkX() {
         return this.relChunkX;
      }

      public int getRelChunkZ() {
         return this.relChunkZ;
      }
   }
}
