package net.theawesomegem.fishingmadebetter.common.data;

import com.google.gson.annotations.SerializedName;
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
      @SerializedName("fruit")
      FRUIT,
      @SerializedName("grain")
      GRAIN,
      @SerializedName("vegetable")
      VEGETABLE,
      @SerializedName("meatNormal")
      MEAT_NORMAL,
      @SerializedName("meatExtra")
      MEAT_EXTRA,
      @SerializedName("smallPredatorAQC")
      SMALL_PREDATOR_AQC,
      @SerializedName("largePredatorAQC")
      LARGE_PREDATOR_AQC,
      @SerializedName("oceanPredatorAQC")
      OCEAN_PREDATOR_AQC,
      @SerializedName("herbivoreAQC")
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
      @SerializedName("water")
      WATER,
      @SerializedName("lava")
      LAVA,
      @SerializedName("void")
      VOID,
      @SerializedName("any")
      ANY;

      private FishingLiquid() {
      }
   }

   public static enum TimeToFish {
      @SerializedName("day")
      DAY,
      @SerializedName("night")
      NIGHT,
      @SerializedName("any")
      ANY;

      private TimeToFish() {
      }
   }
}
