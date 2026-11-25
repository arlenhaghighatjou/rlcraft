package mcjty.lostcities.dimensions.world.lost.cityassets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lostcities.api.ILostCityMultiBuilding;

public class MultiBuilding implements ILostCityMultiBuilding {
   private String name;
   private int dimX;
   private int dimZ;
   private String[][] buildings;

   public MultiBuilding(JsonObject object) {
      this.readFromJSon(object);
   }

   public MultiBuilding(String name, int dimX, int dimZ) {
      this.name = name;
      this.dimX = dimX;
      this.dimZ = dimZ;
      this.buildings = new String[dimX][dimZ];
   }

   public MultiBuilding set(int x, int z, String building) {
      this.buildings[x][z] = building;
      return this;
   }

   public String getBuilding(int x, int z) {
      return this.buildings[x][z];
   }

   public int getDimX() {
      return this.dimX;
   }

   public int getDimZ() {
      return this.dimZ;
   }

   public String getName() {
      return this.name;
   }

   public void readFromJSon(JsonObject object) {
      this.name = object.get("name").getAsString();
      this.dimX = object.get("dimx").getAsInt();
      this.dimZ = object.get("dimz").getAsInt();
      JsonArray buildingArray = object.get("buildings").getAsJsonArray();
      this.buildings = new String[this.dimX][this.dimZ];

      for(int z = 0; z < this.dimZ; ++z) {
         JsonArray ar = buildingArray.get(z).getAsJsonArray();

         for(int x = 0; x < this.dimX; ++x) {
            this.buildings[z][x] = ar.get(x).getAsString();
         }
      }

   }

   public JsonObject writeToJSon() {
      JsonObject object = new JsonObject();
      object.add("type", new JsonPrimitive("multibuilding"));
      object.add("name", new JsonPrimitive(this.name));
      object.add("dimx", new JsonPrimitive(this.dimX));
      object.add("dimz", new JsonPrimitive(this.dimZ));
      JsonArray buildingArray = new JsonArray();

      for(String[] b : this.buildings) {
         JsonArray a = new JsonArray();

         for(String s : b) {
            a.add(new JsonPrimitive(s));
         }

         buildingArray.add(a);
      }

      object.add("buildings", buildingArray);
      return object;
   }
}
