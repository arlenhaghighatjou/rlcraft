package mcjty.lostcities.dimensions.world.lost.cityassets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import mcjty.lostcities.api.ILostCityCityStyle;
import mcjty.lostcities.varia.Tools;
import org.apache.commons.lang3.tuple.Pair;

public class CityStyle implements ILostCityCityStyle {
   private String name;
   private final List<Pair<Float, String>> buildingSelector = new ArrayList();
   private final List<Pair<Float, String>> bridgeSelector = new ArrayList();
   private final List<Pair<Float, String>> parkSelector = new ArrayList();
   private final List<Pair<Float, String>> fountainSelector = new ArrayList();
   private final List<Pair<Float, String>> stairSelector = new ArrayList();
   private final List<Pair<Float, String>> frontSelector = new ArrayList();
   private final List<Pair<Float, String>> railDungeonSelector = new ArrayList();
   private final List<Pair<Float, String>> multiBuildingSelector = new ArrayList();
   private String style;
   private Integer streetWidth;
   private Integer minFloorCount;
   private Integer minCellarCount;
   private Integer maxFloorCount;
   private Integer maxCellarCount;
   private Float explosionChance;
   private Character streetBlock;
   private Character streetBaseBlock;
   private Character streetVariantBlock;
   private Character parkElevationBlock;
   private Character corridorRoofBlock;
   private Character corridorGlassBlock;
   private Character railMainBlock;
   private Character borderBlock;
   private Character wallBlock;
   private Character sphereBlock;
   private Character sphereSideBlock;
   private Character sphereGlassBlock;
   private String inherit;
   private boolean resolveInherit = false;

   public CityStyle(JsonObject object) {
      this.readFromJSon(object);
   }

   public CityStyle(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public String getStyle() {
      return this.style;
   }

   public Float getExplosionChance() {
      return this.explosionChance;
   }

   public int getStreetWidth() {
      return this.streetWidth.intValue();
   }

   public Integer getMinFloorCount() {
      return this.minFloorCount;
   }

   public Integer getMinCellarCount() {
      return this.minCellarCount;
   }

   public Integer getMaxFloorCount() {
      return this.maxFloorCount;
   }

   public Integer getMaxCellarCount() {
      return this.maxCellarCount;
   }

   public Character getStreetBlock() {
      return this.streetBlock;
   }

   public Character getStreetBaseBlock() {
      return this.streetBaseBlock;
   }

   public Character getStreetVariantBlock() {
      return this.streetVariantBlock;
   }

   public Character getRailMainBlock() {
      return this.railMainBlock;
   }

   public Character getParkElevationBlock() {
      return this.parkElevationBlock;
   }

   public Character getCorridorRoofBlock() {
      return this.corridorRoofBlock;
   }

   public Character getCorridorGlassBlock() {
      return this.corridorGlassBlock;
   }

   public Character getBorderBlock() {
      return this.borderBlock;
   }

   public Character getWallBlock() {
      return this.wallBlock;
   }

   public Character getSphereBlock() {
      return this.sphereBlock;
   }

   public Character getSphereSideBlock() {
      return this.sphereSideBlock;
   }

   public Character getSphereGlassBlock() {
      return this.sphereGlassBlock;
   }

   public void init() {
      if (!this.resolveInherit) {
         this.resolveInherit = true;
         if (this.inherit != null) {
            CityStyle inheritFrom = AssetRegistries.CITYSTYLES.get(this.inherit);
            if (inheritFrom == null) {
               throw new RuntimeException("Cannot find citystyle '" + this.inherit + "' to inherit from!");
            }

            if (this.style == null) {
               this.style = inheritFrom.getStyle();
            }

            this.buildingSelector.addAll(inheritFrom.buildingSelector);
            this.bridgeSelector.addAll(inheritFrom.bridgeSelector);
            this.parkSelector.addAll(inheritFrom.parkSelector);
            this.fountainSelector.addAll(inheritFrom.fountainSelector);
            this.stairSelector.addAll(inheritFrom.stairSelector);
            this.frontSelector.addAll(inheritFrom.frontSelector);
            this.railDungeonSelector.addAll(inheritFrom.railDungeonSelector);
            this.multiBuildingSelector.addAll(inheritFrom.multiBuildingSelector);
            if (this.explosionChance == null) {
               this.explosionChance = inheritFrom.explosionChance;
            }

            if (this.streetWidth == null) {
               this.streetWidth = inheritFrom.streetWidth;
            }

            if (this.minFloorCount == null) {
               this.minFloorCount = inheritFrom.minFloorCount;
            }

            if (this.minCellarCount == null) {
               this.minCellarCount = inheritFrom.minCellarCount;
            }

            if (this.maxFloorCount == null) {
               this.maxFloorCount = inheritFrom.maxFloorCount;
            }

            if (this.maxCellarCount == null) {
               this.maxCellarCount = inheritFrom.maxCellarCount;
            }

            if (this.streetBlock == null) {
               this.streetBlock = inheritFrom.streetBlock;
            }

            if (this.streetBaseBlock == null) {
               this.streetBaseBlock = inheritFrom.streetBaseBlock;
            }

            if (this.streetVariantBlock == null) {
               this.streetVariantBlock = inheritFrom.streetVariantBlock;
            }

            if (this.parkElevationBlock == null) {
               this.parkElevationBlock = inheritFrom.parkElevationBlock;
            }

            if (this.corridorRoofBlock == null) {
               this.corridorRoofBlock = inheritFrom.corridorRoofBlock;
            }

            if (this.corridorGlassBlock == null) {
               this.corridorGlassBlock = inheritFrom.corridorGlassBlock;
            }

            if (this.railMainBlock == null) {
               this.railMainBlock = inheritFrom.railMainBlock;
            }

            if (this.borderBlock == null) {
               this.borderBlock = inheritFrom.borderBlock;
            }

            if (this.wallBlock == null) {
               this.wallBlock = inheritFrom.wallBlock;
            }

            if (this.sphereBlock == null) {
               this.sphereBlock = inheritFrom.sphereBlock;
            }

            if (this.sphereSideBlock == null) {
               this.sphereSideBlock = inheritFrom.sphereSideBlock;
            }

            if (this.sphereGlassBlock == null) {
               this.sphereGlassBlock = inheritFrom.sphereGlassBlock;
            }
         }
      }

   }

   public void readFromJSon(JsonObject object) {
      this.name = object.get("name").getAsString();
      if (object.has("inherit")) {
         this.inherit = object.get("inherit").getAsString();
      }

      if (object.has("style")) {
         this.style = object.get("style").getAsString();
      }

      if (object.has("explosionchance")) {
         this.explosionChance = object.get("explosionchance").getAsFloat();
      }

      if (object.has("streetblocks")) {
         JsonObject s = object.get("streetblocks").getAsJsonObject();
         if (s.has("border")) {
            this.borderBlock = s.get("border").getAsCharacter();
         }

         if (s.has("wall")) {
            this.wallBlock = s.get("wall").getAsCharacter();
         }

         if (s.has("street")) {
            this.streetBlock = s.get("street").getAsCharacter();
         }

         if (s.has("streetvariant")) {
            this.streetVariantBlock = s.get("streetvariant").getAsCharacter();
         }

         if (s.has("streetbase")) {
            this.streetBaseBlock = s.get("streetbase").getAsCharacter();
         }

         if (s.has("width")) {
            this.streetWidth = s.get("width").getAsInt();
         }
      }

      if (object.has("buildingsettings")) {
         JsonObject s = object.get("buildingsettings").getAsJsonObject();
         if (s.has("maxfloors")) {
            this.maxFloorCount = s.get("maxfloors").getAsInt();
         }

         if (s.has("maxcellars")) {
            this.maxCellarCount = s.get("maxcellars").getAsInt();
         }

         if (s.has("minfloors")) {
            this.minFloorCount = s.get("minfloors").getAsInt();
         }

         if (s.has("mincellars")) {
            this.minCellarCount = s.get("mincellars").getAsInt();
         }
      }

      if (object.has("railblocks")) {
         JsonObject s = object.get("railblocks").getAsJsonObject();
         this.railMainBlock = s.get("railmain").getAsCharacter();
      }

      if (object.has("parkblocks")) {
         JsonObject s = object.get("parkblocks").getAsJsonObject();
         this.parkElevationBlock = s.get("elevation").getAsCharacter();
      }

      if (object.has("corridorblocks")) {
         JsonObject s = object.get("corridorblocks").getAsJsonObject();
         if (s.has("roof")) {
            this.corridorRoofBlock = s.get("roof").getAsCharacter();
         }

         if (s.has("glass")) {
            this.corridorGlassBlock = s.get("glass").getAsCharacter();
         }
      }

      if (object.has("sphereblocks")) {
         JsonObject s = object.get("sphereblocks").getAsJsonObject();
         if (s.has("glass")) {
            this.sphereGlassBlock = s.get("glass").getAsCharacter();
         }

         if (s.has("border")) {
            this.sphereSideBlock = s.get("border").getAsCharacter();
         }

         if (s.has("inner")) {
            this.sphereBlock = s.get("inner").getAsCharacter();
         }
      }

      this.parseArraySafe(object, this.buildingSelector, "buildings", "building");
      this.parseArraySafe(object, this.multiBuildingSelector, "multibuildings", "multibuilding");
      this.parseArraySafe(object, this.parkSelector, "parks", "park");
      this.parseArraySafe(object, this.fountainSelector, "fountains", "fountain");
      this.parseArraySafe(object, this.stairSelector, "stairs", "stair");
      this.parseArraySafe(object, this.frontSelector, "fronts", "front");
      this.parseArraySafe(object, this.bridgeSelector, "bridges", "bridge");
      this.parseArraySafe(object, this.railDungeonSelector, "raildungeons", "dungeon");
   }

   private void parseArraySafe(JsonObject object, List<Pair<Float, String>> selector, String arrayName, String elName) {
      for(JsonElement element : this.getArraySafe(object, arrayName)) {
         if (element.getAsJsonObject().has("clear")) {
            selector.clear();
         } else {
            float factor = element.getAsJsonObject().get("factor").getAsFloat();
            String el = element.getAsJsonObject().get(elName).getAsString();
            selector.add(Pair.of(Float.valueOf(factor), el));
         }
      }

   }

   private JsonArray getArraySafe(JsonObject object, String key) {
      return object.has(key) ? object.get(key).getAsJsonArray() : new JsonArray();
   }

   public JsonObject writeToJSon() {
      JsonObject object = new JsonObject();
      object.add("type", new JsonPrimitive("citystyle"));
      object.add("name", new JsonPrimitive(this.name));
      object.add("style", new JsonPrimitive(this.style));
      JsonObject s = new JsonObject();
      s.add("street", new JsonPrimitive(this.streetBlock));
      s.add("streetvariant", new JsonPrimitive(this.streetVariantBlock));
      s.add("streetbase", new JsonPrimitive(this.streetBaseBlock));
      s.add("width", new JsonPrimitive(this.streetWidth));
      object.add("streetblocks", s);
      JsonArray array = new JsonArray();

      for(Pair<Float, String> pair : this.buildingSelector) {
         JsonObject o = new JsonObject();
         o.add("factor", new JsonPrimitive((Number)pair.getKey()));
         o.add("building", new JsonPrimitive((String)pair.getValue()));
         array.add(o);
      }

      object.add("buildings", array);
      array = new JsonArray();

      for(Pair<Float, String> pair : this.multiBuildingSelector) {
         JsonObject o = new JsonObject();
         o.add("factor", new JsonPrimitive((Number)pair.getKey()));
         o.add("multibuilding", new JsonPrimitive((String)pair.getValue()));
         array.add(o);
      }

      object.add("multibuildings", array);
      array = new JsonArray();

      for(Pair<Float, String> pair : this.parkSelector) {
         JsonObject o = new JsonObject();
         o.add("factor", new JsonPrimitive((Number)pair.getKey()));
         o.add("park", new JsonPrimitive((String)pair.getValue()));
         array.add(o);
      }

      object.add("parks", array);
      array = new JsonArray();

      for(Pair<Float, String> pair : this.fountainSelector) {
         JsonObject o = new JsonObject();
         o.add("factor", new JsonPrimitive((Number)pair.getKey()));
         o.add("fountain", new JsonPrimitive((String)pair.getValue()));
         array.add(o);
      }

      object.add("fountains", array);
      array = new JsonArray();

      for(Pair<Float, String> pair : this.stairSelector) {
         JsonObject o = new JsonObject();
         o.add("factor", new JsonPrimitive((Number)pair.getKey()));
         o.add("stair", new JsonPrimitive((String)pair.getValue()));
         array.add(o);
      }

      object.add("stairs", array);
      array = new JsonArray();

      for(Pair<Float, String> pair : this.frontSelector) {
         JsonObject o = new JsonObject();
         o.add("factor", new JsonPrimitive((Number)pair.getKey()));
         o.add("front", new JsonPrimitive((String)pair.getValue()));
         array.add(o);
      }

      object.add("fronts", array);
      array = new JsonArray();

      for(Pair<Float, String> pair : this.bridgeSelector) {
         JsonObject o = new JsonObject();
         o.add("factor", new JsonPrimitive((Number)pair.getKey()));
         o.add("bridge", new JsonPrimitive((String)pair.getValue()));
         array.add(o);
      }

      object.add("bridges", array);
      array = new JsonArray();

      for(Pair<Float, String> pair : this.railDungeonSelector) {
         JsonObject o = new JsonObject();
         o.add("factor", new JsonPrimitive((Number)pair.getKey()));
         o.add("dungeon", new JsonPrimitive((String)pair.getValue()));
         array.add(o);
      }

      object.add("raildungeons", array);
      return object;
   }

   public String getRandomStair(EaglercraftRandom random) {
      return Tools.getRandomFromList(random, this.stairSelector);
   }

   public String getRandomFront(EaglercraftRandom random) {
      return Tools.getRandomFromList(random, this.frontSelector);
   }

   public String getRandomRailDungeon(EaglercraftRandom random) {
      return Tools.getRandomFromList(random, this.railDungeonSelector);
   }

   public String getRandomPark(EaglercraftRandom random) {
      return Tools.getRandomFromList(random, this.parkSelector);
   }

   public String getRandomBridge(EaglercraftRandom random) {
      return Tools.getRandomFromList(random, this.bridgeSelector);
   }

   public String getRandomFountain(EaglercraftRandom random) {
      return Tools.getRandomFromList(random, this.fountainSelector);
   }

   public String getRandomBuilding(EaglercraftRandom random) {
      return Tools.getRandomFromList(random, this.buildingSelector);
   }

   public String getRandomMultiBuilding(EaglercraftRandom random) {
      return Tools.getRandomFromList(random, this.multiBuildingSelector);
   }
}
