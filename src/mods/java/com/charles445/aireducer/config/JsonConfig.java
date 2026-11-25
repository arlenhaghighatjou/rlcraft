package com.charles445.aireducer.config;

import com.charles445.aireducer.AIReducer;
import com.charles445.aireducer.config.init.JsonConfigTaskDelay;
import com.charles445.aireducer.config.json.JsonFileName;
import com.charles445.aireducer.config.json.JsonTypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;

public class JsonConfig {
   public static List<String> jsonErrors = new ArrayList();
   public static Map<String, Integer> taskDelay = new HashMap();

   public JsonConfig() {
   }

   public static void init() {
      taskDelay.putAll(JsonConfigTaskDelay.getDefaults());
      Map<String, Integer> taskDelayJson = (Map)processJson(JsonFileName.taskDelay, taskDelay, true);
      if (taskDelayJson != null) {
         try {
            taskDelay.putAll(taskDelayJson);
            manuallyWriteToJson(JsonFileName.taskDelay, taskDelay);
         } catch (Exception var2) {
            AIReducer.logger.error("Failed to merge write taskDelay!");
         }
      }

      if (taskDelay == null) {
         taskDelay = new HashMap();
      }

   }

   @Nullable
   public static <T> T processJson(JsonFileName jfn, T container, boolean forMerging) {
      try {
         return (T)processUncaughtJson(jfn, container, forMerging);
      } catch (Exception var4) {
         AIReducer.logger.error("Error managing JSON File: " + jfn.get(), var4);
         jsonErrors.add("config/aireducer/" + jfn.get() + " failed to load!");
         return (T)(forMerging ? null : container);
      }
   }

   @Nullable
   public static <T> T processUncaughtJson(JsonFileName jfn, T container, boolean forMerging) throws Exception {
      String jsonFileName = jfn.get();
      Type type = JsonTypeToken.get(jfn);
      File jsonFile = new File(AIReducer.jsonDirectory, jsonFileName);
      if (jsonFile.exists()) {
         Gson gson = buildNewGson();
         return (T)gson.fromJson(new FileReader(jsonFile), type);
      } else {
         Gson gson = buildNewGson();
         FileUtils.write(jsonFile, gson.toJson(container, type), (String)null);
         return (T)(forMerging ? null : container);
      }
   }

   private static <T> void manuallyWriteToJson(JsonFileName jfn, T container) throws Exception {
      String jsonFileName = jfn.get();
      Type type = JsonTypeToken.get(jfn);
      Gson gson = buildNewGson();
      File jsonFile = new File(AIReducer.jsonDirectory, jsonFileName);
      FileUtils.write(jsonFile, gson.toJson(container, type), (String)null);
   }

   private static Gson buildNewGson() {
      return (new GsonBuilder()).setPrettyPrinting().excludeFieldsWithModifiers(new int[]{2, 8}).create();
   }
}
