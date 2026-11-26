package net.theawesomegem.fishingmadebetter.common.configuration;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.HString;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.Item;
import net.theawesomegem.fishingmadebetter.DefaultFishes;
import net.theawesomegem.fishingmadebetter.common.data.FishData;
import net.theawesomegem.fishingmadebetter.proxy.CommonProxy;
import net.theawesomegem.fishingmadebetter.util.TextUtils;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class CustomConfigurationHandler {
   public static Map<String, FishData> fishDataMap = new HashMap();
   public static Map<String, ArrayList<Integer>> possibleBaitMap = new HashMap();
   private static VFile2 fishDataFolder = null;

   public CustomConfigurationHandler() {
   }

   public static void preInit(VFile2 file) {
      VFile2 modFolder = new VFile2(file, "fishingmadebetter");
      fishDataFolder = new VFile2(modFolder, "fishdata");
   }

   public static void postInit() {
      loadFishes();
   }

   public static boolean loadFishes() {
      if (fishDataFolder != null) {
         List<VFile2> configFiles = fishDataFolder.listFiles(false);
         if (configFiles.size() < 1) {
            initDefaultFishes(fishDataFolder);
            configFiles = fishDataFolder.listFiles(false);
            CommonProxy.Logger.log(Level.INFO, "Initializing default fishes since no file(s) were found in the fishdata folder.");
         }

         int fileCount = 0;
         fishDataMap.clear();

         for(VFile2 configFile : configFiles) {
            if (configFile.exists() && configFile.getName().endsWith(".json")) {
               CommonProxy.Logger.log(Level.INFO, "Loading: " + configFile.getName());
               loadConfig(configFile);
               ++fileCount;
            }
         }

         CommonProxy.Logger.log(Level.INFO, HString.format("Loaded %d file(s).", fileCount));
         initBaitMap();
         return true;
      } else {
         CommonProxy.Logger.log(Level.FATAL, "Could not load fishes because the folder for the fish data does not exist.");
         return false;
      }
   }

   private static void initBaitMap() {
      possibleBaitMap.clear();

      for(Entry<String, FishData> dataEntry : fishDataMap.entrySet()) {
         if (!((FishData)dataEntry.getValue()).baitItemMap.isEmpty()) {
            for(Entry<String, Integer[]> baitEntry : ((FishData)dataEntry.getValue()).baitItemMap.entrySet()) {
               ArrayList<Integer> metaArray = new ArrayList();
               if (possibleBaitMap.containsKey(baitEntry.getKey())) {
                  metaArray = (ArrayList)possibleBaitMap.get(baitEntry.getKey());
               }

               metaArray.addAll(Arrays.asList(baitEntry.getValue()));
               possibleBaitMap.put(baitEntry.getKey(), metaArray);
            }
         }
      }

   }

   private static void initDefaultFishes(VFile2 fishDataFolder) {
      Map<String, Map<String, FishData>> defaultFishDataMap = DefaultFishes.getDefaultFishMap();

      for(Entry<String, Map<String, FishData>> defaultFishEntry : defaultFishDataMap.entrySet()) {
         JsonObject fishDataJson = new JsonObject();
         Gson gson = (new GsonBuilder()).setPrettyPrinting().create();

         for(FishData fishData : defaultFishEntry.getValue().values()) {
            JsonElement element = fishData.toJson();
            fishDataJson.add(fishData.fishId, element);
         }

         String fishDataString = gson.toJson(fishDataJson);
         (new VFile2(fishDataFolder, HString.format("%s.json", defaultFishEntry.getKey()))).setAllChars(fishDataString);
      }

   }

   private static void loadConfig(VFile2 configFile) {
      String fishDataString = configFile != null ? configFile.getAllChars() : null;
      if (fishDataString != null) {
         try {
            loadFishData((new JsonParser()).parse(fishDataString));
         } catch (Exception var3) {
            var3.printStackTrace();
         }

      } else {
         CommonProxy.Logger.log(Level.FATAL, "Could not load the fish data config file. File does not exist.");
      }
   }

   private static void loadFishData(JsonElement jsonElement) {
      JsonObject fishDataJson = jsonElement.getAsJsonObject();
      Map<String, FishData> tmpFishMap = new HashMap();

      for(Entry<String, JsonElement> fishDataEntry : fishDataJson.entrySet()) {
         JsonElement fishDataElement = (JsonElement)fishDataEntry.getValue();
         FishData fishData = FishData.fromJson(fishDataElement);
         if (!TextUtils.isEmpty(fishData.itemId)) {
            Item item = Item.getByNameOrId(fishData.itemId);
            if (item == null) {
               CommonProxy.Logger.log(Level.WARN, "Tried loading fish data entry with invalid item: " + fishData.itemId + " , skipping entry.");
            } else {
               tmpFishMap.put(fishDataEntry.getKey(), fishData);
            }
         }
      }

      CommonProxy.Logger.log(Level.INFO, "=========== Loaded Fishes ============");

      for(Entry<String, FishData> entry : tmpFishMap.entrySet()) {
         fishDataMap.put(entry.getKey(), entry.getValue());
         CommonProxy.Logger.log(Level.INFO, ((FishData)entry.getValue()).fishId);
      }

      CommonProxy.Logger.log(Level.INFO, "======================================");
   }

   public static Collection<FishData> getAllFishData() {
      return fishDataMap.values();
   }
}
