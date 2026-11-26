package net.theawesomegem.fishingmadebetter.common.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FishData {
   public String fishId;
   public String itemId;
   public int itemMetaData;
   public String description;
   public int minFishTime;
   public int maxFishTime;
   public int minErrorVariance;
   public int maxErrorVariance;
   public int minWeight;
   public int maxWeight;
   public FishData.TimeToFish time;
   public FishData.FishingLiquid liquid;
   public boolean rainRequired;
   public boolean thunderRequired;
   public int rarity;
   public int minDeepLevel;
   public int maxDeepLevel;
   public int minYLevel;
   public int maxYLevel;
   public int maxLightLevel;
   public int reproductionTime;
   public int eatingFrequency;
   public boolean trackable;
   public boolean biomeBlacklist;
   public boolean dimensionBlacklist;
   public List<String> biomeTagList = new ArrayList();
   public List<Integer> dimensionList = new ArrayList();
   public int timeOutsideOfWater;
   public boolean allowScaling;
   public String scalingItem;
   public int scalingItemMetadata;
   public boolean scalingUseWeight;
   public boolean allowFillet;
   public boolean defaultFillet;
   public String filletItem;
   public int filletItemMetadata;
   public boolean filletUseWeight;
   public Map<String, Integer[]> baitItemMap = new HashMap();

   public FishData() {
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      if (this.fishId != null) {
         json.addProperty("fishId", this.fishId);
      }
      if (this.itemId != null) {
         json.addProperty("itemId", this.itemId);
      }
      json.addProperty("itemMetaData", this.itemMetaData);
      if (this.description != null) {
         json.addProperty("description", this.description);
      }
      json.addProperty("minFishTime", this.minFishTime);
      json.addProperty("maxFishTime", this.maxFishTime);
      json.addProperty("minErrorVariance", this.minErrorVariance);
      json.addProperty("maxErrorVariance", this.maxErrorVariance);
      json.addProperty("minWeight", this.minWeight);
      json.addProperty("maxWeight", this.maxWeight);
      if (this.time != null) {
         json.addProperty("time", this.time.serializedName);
      }
      if (this.liquid != null) {
         json.addProperty("liquid", this.liquid.serializedName);
      }
      json.addProperty("rainRequired", this.rainRequired);
      json.addProperty("thunderRequired", this.thunderRequired);
      json.addProperty("rarity", this.rarity);
      json.addProperty("minDeepLevel", this.minDeepLevel);
      json.addProperty("maxDeepLevel", this.maxDeepLevel);
      json.addProperty("minYLevel", this.minYLevel);
      json.addProperty("maxYLevel", this.maxYLevel);
      json.addProperty("maxLightLevel", this.maxLightLevel);
      json.addProperty("reproductionTime", this.reproductionTime);
      json.addProperty("eatingFrequency", this.eatingFrequency);
      json.addProperty("trackable", this.trackable);
      json.addProperty("biomeBlacklist", this.biomeBlacklist);
      json.addProperty("dimensionBlacklist", this.dimensionBlacklist);
      if (this.biomeTagList != null) {
         JsonArray arr = new JsonArray();
         for (int i = 0; i < this.biomeTagList.size(); ++i) {
            arr.add(this.biomeTagList.get(i));
         }
         json.add("biomeTagList", arr);
      }
      if (this.dimensionList != null) {
         JsonArray arr = new JsonArray();
         for (int i = 0; i < this.dimensionList.size(); ++i) {
            arr.add(this.dimensionList.get(i));
         }
         json.add("dimensionList", arr);
      }
      json.addProperty("timeOutsideOfWater", this.timeOutsideOfWater);
      json.addProperty("allowScaling", this.allowScaling);
      if (this.scalingItem != null) {
         json.addProperty("scalingItem", this.scalingItem);
      }
      json.addProperty("scalingItemMetadata", this.scalingItemMetadata);
      json.addProperty("scalingUseWeight", this.scalingUseWeight);
      json.addProperty("allowFillet", this.allowFillet);
      json.addProperty("defaultFillet", this.defaultFillet);
      if (this.filletItem != null) {
         json.addProperty("filletItem", this.filletItem);
      }
      json.addProperty("filletItemMetadata", this.filletItemMetadata);
      json.addProperty("filletUseWeight", this.filletUseWeight);
      if (this.baitItemMap != null) {
         JsonObject map = new JsonObject();
         for (Entry<String, Integer[]> entry : this.baitItemMap.entrySet()) {
            JsonArray arr = new JsonArray();
            Integer[] values = entry.getValue();
            for (int i = 0; i < values.length; ++i) {
               arr.add(values[i]);
            }
            map.add(entry.getKey(), arr);
         }
         json.add("baitItemMap", map);
      }
      return json;
   }

   public static FishData fromJson(JsonElement element) {
      JsonObject json = element.getAsJsonObject();
      FishData data = new FishData();
      if (json.has("fishId")) {
         data.fishId = json.get("fishId").isJsonNull() ? null : json.get("fishId").getAsString();
      }
      if (json.has("itemId")) {
         data.itemId = json.get("itemId").isJsonNull() ? null : json.get("itemId").getAsString();
      }
      if (json.has("itemMetaData") && !json.get("itemMetaData").isJsonNull()) {
         data.itemMetaData = json.get("itemMetaData").getAsInt();
      }
      if (json.has("description")) {
         data.description = json.get("description").isJsonNull() ? null : json.get("description").getAsString();
      }
      if (json.has("minFishTime") && !json.get("minFishTime").isJsonNull()) {
         data.minFishTime = json.get("minFishTime").getAsInt();
      }
      if (json.has("maxFishTime") && !json.get("maxFishTime").isJsonNull()) {
         data.maxFishTime = json.get("maxFishTime").getAsInt();
      }
      if (json.has("minErrorVariance") && !json.get("minErrorVariance").isJsonNull()) {
         data.minErrorVariance = json.get("minErrorVariance").getAsInt();
      }
      if (json.has("maxErrorVariance") && !json.get("maxErrorVariance").isJsonNull()) {
         data.maxErrorVariance = json.get("maxErrorVariance").getAsInt();
      }
      if (json.has("minWeight") && !json.get("minWeight").isJsonNull()) {
         data.minWeight = json.get("minWeight").getAsInt();
      }
      if (json.has("maxWeight") && !json.get("maxWeight").isJsonNull()) {
         data.maxWeight = json.get("maxWeight").getAsInt();
      }
      if (json.has("time")) {
         data.time = json.get("time").isJsonNull() ? null : FishData.TimeToFish.fromSerializedName(json.get("time").getAsString());
      }
      if (json.has("liquid")) {
         data.liquid = json.get("liquid").isJsonNull() ? null : FishData.FishingLiquid.fromSerializedName(json.get("liquid").getAsString());
      }
      if (json.has("rainRequired") && !json.get("rainRequired").isJsonNull()) {
         data.rainRequired = json.get("rainRequired").getAsBoolean();
      }
      if (json.has("thunderRequired") && !json.get("thunderRequired").isJsonNull()) {
         data.thunderRequired = json.get("thunderRequired").getAsBoolean();
      }
      if (json.has("rarity") && !json.get("rarity").isJsonNull()) {
         data.rarity = json.get("rarity").getAsInt();
      }
      if (json.has("minDeepLevel") && !json.get("minDeepLevel").isJsonNull()) {
         data.minDeepLevel = json.get("minDeepLevel").getAsInt();
      }
      if (json.has("maxDeepLevel") && !json.get("maxDeepLevel").isJsonNull()) {
         data.maxDeepLevel = json.get("maxDeepLevel").getAsInt();
      }
      if (json.has("minYLevel") && !json.get("minYLevel").isJsonNull()) {
         data.minYLevel = json.get("minYLevel").getAsInt();
      }
      if (json.has("maxYLevel") && !json.get("maxYLevel").isJsonNull()) {
         data.maxYLevel = json.get("maxYLevel").getAsInt();
      }
      if (json.has("maxLightLevel") && !json.get("maxLightLevel").isJsonNull()) {
         data.maxLightLevel = json.get("maxLightLevel").getAsInt();
      }
      if (json.has("reproductionTime") && !json.get("reproductionTime").isJsonNull()) {
         data.reproductionTime = json.get("reproductionTime").getAsInt();
      }
      if (json.has("eatingFrequency") && !json.get("eatingFrequency").isJsonNull()) {
         data.eatingFrequency = json.get("eatingFrequency").getAsInt();
      }
      if (json.has("trackable") && !json.get("trackable").isJsonNull()) {
         data.trackable = json.get("trackable").getAsBoolean();
      }
      if (json.has("biomeBlacklist") && !json.get("biomeBlacklist").isJsonNull()) {
         data.biomeBlacklist = json.get("biomeBlacklist").getAsBoolean();
      }
      if (json.has("dimensionBlacklist") && !json.get("dimensionBlacklist").isJsonNull()) {
         data.dimensionBlacklist = json.get("dimensionBlacklist").getAsBoolean();
      }
      if (json.has("biomeTagList")) {
         if (json.get("biomeTagList").isJsonNull()) {
            data.biomeTagList = null;
         } else {
            JsonArray arr = json.get("biomeTagList").getAsJsonArray();
            data.biomeTagList = new ArrayList();
            for (int i = 0; i < arr.size(); ++i) {
               data.biomeTagList.add(arr.get(i).getAsString());
            }
         }
      }
      if (json.has("dimensionList")) {
         if (json.get("dimensionList").isJsonNull()) {
            data.dimensionList = null;
         } else {
            JsonArray arr = json.get("dimensionList").getAsJsonArray();
            data.dimensionList = new ArrayList();
            for (int i = 0; i < arr.size(); ++i) {
               data.dimensionList.add(Integer.valueOf(arr.get(i).getAsInt()));
            }
         }
      }
      if (json.has("timeOutsideOfWater") && !json.get("timeOutsideOfWater").isJsonNull()) {
         data.timeOutsideOfWater = json.get("timeOutsideOfWater").getAsInt();
      }
      if (json.has("allowScaling") && !json.get("allowScaling").isJsonNull()) {
         data.allowScaling = json.get("allowScaling").getAsBoolean();
      }
      if (json.has("scalingItem")) {
         data.scalingItem = json.get("scalingItem").isJsonNull() ? null : json.get("scalingItem").getAsString();
      }
      if (json.has("scalingItemMetadata") && !json.get("scalingItemMetadata").isJsonNull()) {
         data.scalingItemMetadata = json.get("scalingItemMetadata").getAsInt();
      }
      if (json.has("scalingUseWeight") && !json.get("scalingUseWeight").isJsonNull()) {
         data.scalingUseWeight = json.get("scalingUseWeight").getAsBoolean();
      }
      if (json.has("allowFillet") && !json.get("allowFillet").isJsonNull()) {
         data.allowFillet = json.get("allowFillet").getAsBoolean();
      }
      if (json.has("defaultFillet") && !json.get("defaultFillet").isJsonNull()) {
         data.defaultFillet = json.get("defaultFillet").getAsBoolean();
      }
      if (json.has("filletItem")) {
         data.filletItem = json.get("filletItem").isJsonNull() ? null : json.get("filletItem").getAsString();
      }
      if (json.has("filletItemMetadata") && !json.get("filletItemMetadata").isJsonNull()) {
         data.filletItemMetadata = json.get("filletItemMetadata").getAsInt();
      }
      if (json.has("filletUseWeight") && !json.get("filletUseWeight").isJsonNull()) {
         data.filletUseWeight = json.get("filletUseWeight").getAsBoolean();
      }
      if (json.has("baitItemMap")) {
         if (json.get("baitItemMap").isJsonNull()) {
            data.baitItemMap = null;
         } else {
            data.baitItemMap = new HashMap();
            for (Entry<String, JsonElement> entry : json.get("baitItemMap").getAsJsonObject().entrySet()) {
               JsonArray arr = entry.getValue().getAsJsonArray();
               Integer[] values = new Integer[arr.size()];
               for (int i = 0; i < values.length; ++i) {
                  values[i] = Integer.valueOf(arr.get(i).getAsInt());
               }
               data.baitItemMap.put(entry.getKey(), values);
            }
         }
      }
      return data;
   }

   public static FishData createData(String fishId, String itemId, int itemMetaData, String description, int minFishTime, int maxFishTime, int minErrorVariance, int maxErrorVariance, int minWeight, int maxWeight, FishData.TimeToFish time, FishData.FishingLiquid liquid, boolean rainRequired, boolean thunderRequired, int rarity, int minDeepLevel, int maxDeepLevel, int minYLevel, int maxYLevel, int maxLightLevel, int reproductionTime, int eatingFrequency, boolean trackable, boolean biomeBlacklist, boolean dimensionBlacklist, String[] biomeTagList, int[] dimensionList, int timeOutsideOfWater, boolean allowScaling, String scalingItem, int scalingItemMetadata, boolean scalingUseWeight, boolean allowFillet, boolean defaultFillet, String filletItem, int filletItemMetadata, boolean filletUseWeight, FishData.BaitEnum[] baitEnumArray) {
      FishData fishData = new FishData();
      fishData.fishId = fishId;
      fishData.itemId = itemId;
      fishData.itemMetaData = itemMetaData;
      fishData.description = description;
      fishData.minFishTime = minFishTime;
      fishData.maxFishTime = maxFishTime;
      fishData.minErrorVariance = minErrorVariance;
      fishData.maxErrorVariance = maxErrorVariance;
      fishData.minWeight = minWeight;
      fishData.maxWeight = maxWeight;
      fishData.time = time;
      fishData.liquid = liquid;
      fishData.rainRequired = rainRequired;
      fishData.thunderRequired = thunderRequired;
      fishData.rarity = rarity;
      fishData.minDeepLevel = minDeepLevel;
      fishData.maxDeepLevel = maxDeepLevel;
      fishData.minYLevel = minYLevel;
      fishData.maxYLevel = maxYLevel;
      fishData.maxLightLevel = maxLightLevel;
      fishData.reproductionTime = reproductionTime;
      fishData.eatingFrequency = eatingFrequency;
      fishData.trackable = trackable;
      fishData.biomeBlacklist = biomeBlacklist;
      fishData.dimensionBlacklist = dimensionBlacklist;
      fishData.biomeTagList = new ArrayList();

      for(String tag : biomeTagList) {
         fishData.biomeTagList.add(tag);
      }

      fishData.dimensionList = new ArrayList();

      for(int dim : dimensionList) {
         fishData.dimensionList.add(Integer.valueOf(dim));
      }

      fishData.timeOutsideOfWater = timeOutsideOfWater;
      fishData.allowScaling = allowScaling;
      fishData.scalingItem = scalingItem;
      fishData.scalingItemMetadata = scalingItemMetadata;
      fishData.scalingUseWeight = scalingUseWeight;
      fishData.allowFillet = allowFillet;
      fishData.defaultFillet = defaultFillet;
      fishData.filletItem = filletItem;
      fishData.filletItemMetadata = filletItemMetadata;
      fishData.filletUseWeight = filletUseWeight;

      for(FishData.BaitEnum entry : baitEnumArray) {
         fishData.baitItemMap.putAll(entry.getValues(entry));
      }

      return fishData;
   }

   public static enum BaitEnum {
      FRUIT,
      GRAIN,
      VEGETABLE,
      MEAT_NORMAL,
      MEAT_EXTRA,
      SMALL_PREDATOR_AQC,
      LARGE_PREDATOR_AQC,
      OCEAN_PREDATOR_AQC,
      HERBIVORE_AQC;

      Map<String, Integer[]> fruit = new HashMap<String, Integer[]>() {
         {
            this.put("minecraft:apple", new Integer[]{Integer.valueOf(0)});
            this.put("minecraft:melon", new Integer[]{Integer.valueOf(0)});
         }
      };
      Map<String, Integer[]> grain = new HashMap<String, Integer[]>() {
         {
            this.put("minecraft:wheat_seeds", new Integer[]{Integer.valueOf(0)});
            this.put("minecraft:pumpkin_seeds", new Integer[]{Integer.valueOf(0)});
            this.put("minecraft:melon_seeds", new Integer[]{Integer.valueOf(0)});
            this.put("minecraft:beetroot_seeds", new Integer[]{Integer.valueOf(0)});
         }
      };
      Map<String, Integer[]> vegetable = new HashMap<String, Integer[]>() {
         {
            this.put("minecraft:carrot", new Integer[]{Integer.valueOf(0)});
            this.put("minecraft:potato", new Integer[]{Integer.valueOf(0)});
            this.put("minecraft:beetroot", new Integer[]{Integer.valueOf(0)});
         }
      };
      Map<String, Integer[]> meatNormal = new HashMap<String, Integer[]>() {
         {
            this.put("minecraft:fish", new Integer[]{Integer.valueOf(2)});
            this.put("minecraft:spider_eye", new Integer[]{Integer.valueOf(0)});
            this.put("minecraft:rotten_flesh", new Integer[]{Integer.valueOf(0)});
         }
      };
      Map<String, Integer[]> meatExtra = new HashMap<String, Integer[]>() {
         {
            this.put("minecraft:porkchop", new Integer[]{Integer.valueOf(0)});
            this.put("minecraft:beef", new Integer[]{Integer.valueOf(0)});
            this.put("minecraft:chicken", new Integer[]{Integer.valueOf(0)});
            this.put("minecraft:rabbit", new Integer[]{Integer.valueOf(0)});
            this.put("minecraft:mutton", new Integer[]{Integer.valueOf(0)});
         }
      };
      Map<String, Integer[]> small_predator_aqc = new HashMap<String, Integer[]>() {
         {
            this.put("aquaculture:fish", new Integer[]{Integer.valueOf(19), Integer.valueOf(37)});
         }
      };
      Map<String, Integer[]> large_predator_aqc = new HashMap<String, Integer[]>() {
         {
            this.put("aquaculture:fish", new Integer[]{Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(17)});
         }
      };
      Map<String, Integer[]> ocean_predator_aqc = new HashMap<String, Integer[]>() {
         {
            this.put("aquaculture:fish", new Integer[]{Integer.valueOf(9), Integer.valueOf(10), Integer.valueOf(15), Integer.valueOf(16), Integer.valueOf(26)});
         }
      };
      Map<String, Integer[]> herbivore_aqc = new HashMap<String, Integer[]>() {
         {
            this.put("aquaculture:food", new Integer[]{Integer.valueOf(0), Integer.valueOf(1)});
         }
      };

      private BaitEnum() {
      }

      public Map<String, Integer[]> getValues(FishData.BaitEnum value) {
         switch(value) {
         case FRUIT:
            return this.fruit;
         case GRAIN:
            return this.grain;
         case VEGETABLE:
            return this.vegetable;
         case MEAT_NORMAL:
            return this.meatNormal;
         case MEAT_EXTRA:
            return this.meatExtra;
         case SMALL_PREDATOR_AQC:
            return this.small_predator_aqc;
         case LARGE_PREDATOR_AQC:
            return this.large_predator_aqc;
         case OCEAN_PREDATOR_AQC:
            return this.ocean_predator_aqc;
         case HERBIVORE_AQC:
            return this.herbivore_aqc;
         default:
            return this.vegetable;
         }
      }
   }

   public static enum FishingLiquid {
      WATER("water"),
      LAVA("lava"),
      VOID("void"),
      ANY("any");

      public final String serializedName;

      private FishingLiquid(String serializedName) {
         this.serializedName = serializedName;
      }

      public static FishData.FishingLiquid fromSerializedName(String name) {
         FishData.FishingLiquid[] values = values();
         for (int i = 0; i < values.length; ++i) {
            if (values[i].serializedName.equals(name)) {
               return values[i];
            }
         }

         return null;
      }
   }

   public static enum TimeToFish {
      DAY("day"),
      NIGHT("night"),
      ANY("any");

      public final String serializedName;

      private TimeToFish(String serializedName) {
         this.serializedName = serializedName;
      }

      public static FishData.TimeToFish fromSerializedName(String name) {
         FishData.TimeToFish[] values = values();
         for (int i = 0; i < values.length; ++i) {
            if (values[i].serializedName.equals(name)) {
               return values[i];
            }
         }

         return null;
      }
   }
}
