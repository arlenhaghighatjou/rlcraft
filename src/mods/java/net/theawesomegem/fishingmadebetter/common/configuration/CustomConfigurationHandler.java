package net.theawesomegem.fishingmadebetter.common.configuration;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
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
   private static File fishDataFolder = null;

   public CustomConfigurationHandler() {
   }

   public static void preInit(File file) {
      File modFolder = new File(file, "fishingmadebetter");
      if ((!modFolder.exists() || !modFolder.isDirectory()) && !modFolder.mkdir()) {
         CommonProxy.Logger.log(Level.FATAL, "Could not create the folder for configuration.");
      }

      if (modFolder.exists()) {
         fishDataFolder = new File(modFolder, "fishdata");
         if ((!fishDataFolder.exists() || !fishDataFolder.isDirectory()) && !fishDataFolder.mkdir()) {
            CommonProxy.Logger.log(Level.FATAL, "Could not create the folder for fish data.");
         }

      }
   }

   public static void postInit() {
      loadFishes();
   }

   public static boolean loadFishes() {
      if (fishDataFolder != null && fishDataFolder.exists()) {
         File[] configFiles = fishDataFolder.listFiles();
         if (configFiles.length < 1) {
            initDefaultFishes(fishDataFolder);
            configFiles = fishDataFolder.listFiles();
            CommonProxy.Logger.log(Level.INFO, "Initializing default fishes since no file(s) were found in the fishdata folder.");
         }

         int fileCount = 0;
         fishDataMap.clear();

         for(File configFile : configFiles) {
            if (configFile.exists() && configFile.getName().endsWith(".json")) {
               CommonProxy.Logger.log(Level.INFO, "Loading: " + configFile.getName());
               loadConfig(configFile);
               ++fileCount;
            }
         }

         CommonProxy.Logger.log(Level.INFO, String.format("Loaded %d file(s).", fileCount));
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

   private static void initDefaultFishes(File fishDataFolder) {
      Map<String, Map<String, FishData>> defaultFishDataMap = DefaultFishes.getDefaultFishMap();

      for(Entry<String, Map<String, FishData>> defaultFishEntry : defaultFishDataMap.entrySet()) {
         JsonObject fishDataJson = new JsonObject();
         Gson gson = (new GsonBuilder()).setPrettyPrinting().create();

         for(FishData fishData : ((Map)defaultFishEntry.getValue()).values()) {
            JsonElement element = gson.toJsonTree(fishData);
            fishDataJson.add(fishData.fishId, element);
         }

         String fishDataString = gson.toJson(fishDataJson);
         File file = new File(fishDataFolder, String.format("%s.json", defaultFishEntry.getKey()));

         boolean fileCreated;
         try {
            fileCreated = file.createNewFile();
         } catch (Exception var12) {
            var12.printStackTrace();
            continue;
         }

         if (!fileCreated) {
            CommonProxy.Logger.log(Level.FATAL, "Could not create the fish data file.");
         } else {
            try {
               boolean writeable = file.setWritable(true);
               if (!writeable) {
                  CommonProxy.Logger.log(Level.FATAL, "Cannot write/save the fish data file inside the fishdata folder. Insufficient permission.");
               } else {
                  PrintWriter writer = new PrintWriter(file);
                  writer.write(fishDataString);
                  writer.flush();
                  writer.close();
               }
            } catch (IOException var11) {
               var11.printStackTrace();
            }
         }
      }

   }

   private static void loadConfig(File configFile) {
      if (configFile != null && configFile.exists()) {
         try {
            boolean readable = configFile.setReadable(true);
            if (!readable) {
               CommonProxy.Logger.log(Level.FATAL, String.format("Cannot read file %s. No permission to read the file.", configFile.getName()));
               return;
            }

            String fishDataString = Files.toString(configFile, Charset.defaultCharset());
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
      Gson gson = new Gson();
      Map<String, FishData> tmpFishMap = new HashMap();

      for(Entry<String, JsonElement> fishDataEntry : fishDataJson.entrySet()) {
         JsonElement fishDataElement = (JsonElement)fishDataEntry.getValue();
         FishData fishData = (FishData)gson.fromJson(fishDataElement, FishData.class);
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
