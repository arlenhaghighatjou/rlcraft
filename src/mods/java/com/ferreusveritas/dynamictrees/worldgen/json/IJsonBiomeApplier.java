package com.ferreusveritas.dynamictrees.worldgen.json;

import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.biome.Biome;

public interface IJsonBiomeApplier {
   String DEFAULT = "...";
   String REPLACE = "replace";
   String BEFORE = "before";
   String AFTER = "after";

   void apply(BiomeDataBase var1, JsonElement var2, Biome var3);

   default BiomeDataBase.Operation readMethod(JsonObject object) {
      JsonElement method = object.get("method");
      if (method != null && method.isJsonPrimitive() && method.getAsJsonPrimitive().isString()) {
         String methodName = method.getAsJsonPrimitive().getAsString();
         if ("replace".equals(methodName)) {
            return BiomeDataBase.Operation.REPLACE;
         }

         if ("before".equals(methodName)) {
            return BiomeDataBase.Operation.SPLICE_BEFORE;
         }

         if ("after".equals(methodName)) {
            return BiomeDataBase.Operation.SPLICE_AFTER;
         }
      }

      return BiomeDataBase.Operation.REPLACE;
   }

   default boolean isDefault(String candidate) {
      return "...".equals(candidate);
   }
}
