package com.ferreusveritas.dynamictrees.worldgen;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeDataBasePopulator;
import com.ferreusveritas.dynamictrees.util.JsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import net.minecraft.util.ResourceLocation;

public class MultiDimensionalPopulator {
   public static final String DIM = "dim";
   public static final String ACTIVE = "active";
   public static final String FILES = "files";

   public MultiDimensionalPopulator(ResourceLocation jsonLocation, IBiomeDataBasePopulator defaultPopulator) {
      this(JsonHelper.load(jsonLocation), defaultPopulator);
   }

   public MultiDimensionalPopulator(JsonElement mainJsonElement, IBiomeDataBasePopulator defaultPopulator) {
      this.load(mainJsonElement, defaultPopulator);
   }

   private void load(JsonElement mainJsonElement, IBiomeDataBasePopulator defaultPopulator) {
      if (mainJsonElement != null && mainJsonElement.isJsonArray()) {
         for(JsonElement element : mainJsonElement.getAsJsonArray()) {
            if (element.isJsonObject()) {
               JsonObject obj = element.getAsJsonObject();
               int dim = obj.get("dim").getAsInt();
               boolean active = obj.get("active").getAsBoolean();
               JsonArray files = obj.get("files").getAsJsonArray();
               if (active) {
                  System.out.println("Loading custom populators for dimension: " + dim);
                  BiomeDataBase database = new BiomeDataBase();
                  defaultPopulator.populate(database);
                  TreeGenerator.getTreeGenerator().linkDimensionToDataBase(dim, database);

                  for(JsonElement filename : files) {
                     if (filename.isJsonPrimitive()) {
                        File file = new File(ModConfigs.configDirectory.getAbsolutePath() + "/" + filename.getAsString());
                        (new BiomeDataBasePopulatorJson(JsonHelper.load(file))).populate(database);
                     }
                  }
               }
            }
         }
      }

   }
}
