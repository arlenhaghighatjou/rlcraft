package mcjty.lostcities.dimensions.world.lost.cityassets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lostcities.api.ILostCityAsset;

public class PredefinedSphere implements ILostCityAsset {
   private String name;
   private int dimension;
   private int chunkX;
   private int chunkZ;
   private int centerX;
   private int centerZ;
   private int radius;
   private String biome;

   public PredefinedSphere(JsonObject object) {
      this.readFromJSon(object);
   }

   public PredefinedSphere(String name) {
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
      this.centerX = object.get("centerx").getAsInt();
      this.centerZ = object.get("centerz").getAsInt();
      this.radius = object.get("radius").getAsInt();
      if (object.has("biome")) {
         this.biome = object.get("biome").getAsString();
      } else {
         this.biome = null;
      }

   }

   private JsonArray getArraySafe(JsonObject object, String key) {
      return object.has(key) ? object.get(key).getAsJsonArray() : new JsonArray();
   }

   public JsonObject writeToJSon() {
      JsonObject object = new JsonObject();
      object.add("type", new JsonPrimitive("sphere"));
      object.add("name", new JsonPrimitive(this.name));
      return object;
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

   public int getCenterX() {
      return this.centerX;
   }

   public int getCenterZ() {
      return this.centerZ;
   }

   public int getRadius() {
      return this.radius;
   }

   public String getBiome() {
      return this.biome;
   }
}
