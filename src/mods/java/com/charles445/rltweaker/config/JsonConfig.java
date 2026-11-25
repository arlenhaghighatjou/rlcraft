package com.charles445.rltweaker.config;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.init.JsonConfigLessCollisions;
import com.charles445.rltweaker.config.json.JsonDoubleBlockState;
import com.charles445.rltweaker.config.json.JsonFileName;
import com.charles445.rltweaker.config.json.JsonTypeToken;
import com.charles445.rltweaker.entity.ai.InvestigateAIConfig;
import com.charles445.rltweaker.handler.ReskillableHandler;
import com.charles445.rltweaker.util.CollisionUtil;
import com.charles445.rltweaker.util.ErrorUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class JsonConfig {
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(ResourceLocation.class, new TypeAdapter<ResourceLocation>() {
      public void write(JsonWriter out, ResourceLocation value) throws IOException {
         TypeAdapters.STRING.write(out, value.toString());
      }

      public ResourceLocation read(JsonReader in) throws IOException {
         return new ResourceLocation((String)TypeAdapters.STRING.read(in));
      }
   }).setPrettyPrinting().create();
   public static Map<String, Double> lessCollisions = new HashMap();
   public static Map<String, List<JsonDoubleBlockState>> reskillableTransmutation = new HashMap();
   public static Map<ResourceLocation, InvestigateAIConfig> investigateAI = new HashMap();

   public JsonConfig() {
   }

   public static void init() {
      if (ModConfig.patches.lessCollisions) {
         lessCollisions.putAll(JsonConfigLessCollisions.getDefaults());
         Map<String, Double> lcJson = (Map)processJson(JsonFileName.lessCollisions, lessCollisions, true);
         if (lcJson != null) {
            try {
               lessCollisions.putAll(lcJson);
               manuallyWriteToJson(JsonFileName.lessCollisions, lessCollisions);
            } catch (Exception var4) {
               RLTweaker.logger.error("Failed to merge write lessCollisions!");
               ErrorUtil.logSilent("JSON Merge Write LessCollisions");
            }
         }

         if (lessCollisions == null) {
            lessCollisions = new HashMap();
         }

         CollisionUtil.instance.addToStringReference(lessCollisions);
      }

      if (Loader.isModLoaded("reskillable") && ModConfig.server.reskillable.enabled && ModConfig.server.reskillable.customTransmutation) {
         reskillableTransmutation.clear();
         reskillableTransmutation.put("minecraft:stick", Arrays.asList(JsonDoubleBlockState.AIR));
         reskillableTransmutation = (Map)processJson(JsonFileName.reskillableTransmutation, reskillableTransmutation, false);
         if (reskillableTransmutation != null) {
            Object reskillableHandler = RLTweaker.handlers.get("reskillable");
            if (reskillableHandler instanceof ReskillableHandler) {
               ((ReskillableHandler)reskillableHandler).registerTransmutations();
            }
         }
      }

      try {
         loadInvestigateAIConfig();
      } catch (JsonConfig.JsonFileException var2) {
         RLTweaker.logger.error("Failed to load investigate AI file '{}'", var2.getFile(), var2);
      } catch (IOException var3) {
         RLTweaker.logger.error("Failed to load investigate AI config files", var3);
      }

   }

   public static void loadInvestigateAIConfig() throws JsonConfig.JsonFileException, IOException {
      investigateAI = readJsons(RLTweaker.jsonDirectory.resolve("investigateAI"), InvestigateAIConfig.class);
   }

   @Nullable
   public static <T> T processJson(JsonFileName jfn, T container, boolean forMerging) {
      try {
         return (T)processUncaughtJson(jfn, container, forMerging);
      } catch (Exception var4) {
         RLTweaker.logger.error("Error managing JSON File: " + jfn.get(), var4);
         ErrorUtil.logSilent("JSON Error: " + jfn.get());
         return (T)(forMerging ? null : container);
      }
   }

   @Nullable
   public static <T> T processUncaughtJson(JsonFileName jfn, T container, boolean forMerging) throws Exception {
      String jsonFileName = jfn.get();
      Type type = JsonTypeToken.get(jfn);
      Path jsonFile = RLTweaker.jsonDirectory.resolve(jsonFileName);
      if (Files.exists(jsonFile, new LinkOption[0])) {
         return (T)readJsonFromFile(jsonFile, type);
      } else {
         writeJsonToFile(jsonFile, type, container);
         return (T)(forMerging ? null : container);
      }
   }

   private static <T> void manuallyWriteToJson(JsonFileName jfn, T container) throws Exception {
      String jsonFileName = jfn.get();
      Type type = JsonTypeToken.get(jfn);
      Path jsonFile = RLTweaker.jsonDirectory.resolve(jsonFileName);
      writeJsonToFile(jsonFile, type, container);
   }

   private static <T> T readJsonFromFile(Path path, Type type) throws JsonIOException, JsonSyntaxException, IOException {
      Reader in = Files.newBufferedReader(path);
      Throwable var3 = null;

      Object var4;
      try {
         var4 = GSON.fromJson(in, type);
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (in != null) {
            if (var3 != null) {
               try {
                  in.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               in.close();
            }
         }

      }

      return (T)var4;
   }

   private static <T> T readJsonFromFile(Path path, Class<T> type) throws JsonIOException, JsonSyntaxException, IOException {
      Reader in = Files.newBufferedReader(path);
      Throwable var3 = null;

      Object var4;
      try {
         var4 = GSON.fromJson(in, type);
      } catch (Throwable var13) {
         var3 = var13;
         throw var13;
      } finally {
         if (in != null) {
            if (var3 != null) {
               try {
                  in.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               in.close();
            }
         }

      }

      return (T)var4;
   }

   private static void writeJsonToFile(Path path, Type type, Object src) throws JsonIOException, IOException {
      Files.createDirectories(path.getParent());
      Writer out = Files.newBufferedWriter(path);
      Throwable var4 = null;

      try {
         GSON.toJson(src, type, out);
      } catch (Throwable var13) {
         var4 = var13;
         throw var13;
      } finally {
         if (out != null) {
            if (var4 != null) {
               try {
                  out.close();
               } catch (Throwable var12) {
                  var4.addSuppressed(var12);
               }
            } else {
               out.close();
            }
         }

      }

   }

   private static <V> Map<ResourceLocation, V> readJsons(Path dir, Class<V> type) throws JsonConfig.JsonFileException, IOException {
      Map<ResourceLocation, V> map = new HashMap();
      if (!Files.exists(dir, new LinkOption[0])) {
         return map;
      } else {
         for(Path subDir : (Path[])Files.list(dir).filter((x$0) -> {
            return Files.isDirectory(x$0, new LinkOption[0]);
         }).toArray((x$0) -> {
            return new Path[x$0];
         })) {
            String modid = subDir.getFileName().toString();

            for(Path file : (Path[])Files.list(subDir).filter((x$0) -> {
               return Files.isRegularFile(x$0, new LinkOption[0]);
            }).filter((p) -> {
               return p.toString().endsWith(".json");
            }).toArray((x$0) -> {
               return new Path[x$0];
            })) {
               String fileName = file.getFileName().toString();
               ResourceLocation key = new ResourceLocation(modid, fileName.substring(0, fileName.lastIndexOf(46)));

               V value;
               try {
                  value = (V)readJsonFromFile(file, type);
               } catch (JsonSyntaxException | JsonIOException var16) {
                  throw new JsonConfig.JsonFileException(dir.relativize(file), var16);
               }

               map.put(key, value);
            }
         }

         return map;
      }
   }

   public static class JsonFileException extends Exception {
      private final Path file;

      public JsonFileException(Path file, Throwable cause) {
         super(cause);
         this.file = file;
      }

      public Path getFile() {
         return this.file;
      }
   }
}
