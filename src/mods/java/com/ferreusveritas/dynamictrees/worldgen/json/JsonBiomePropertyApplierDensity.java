package com.ferreusveritas.dynamictrees.worldgen.json;

import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class JsonBiomePropertyApplierDensity implements IJsonBiomeApplier {
   public JsonBiomePropertyApplierDensity() {
   }

   public void apply(BiomeDataBase dbase, JsonElement element, Biome biome) {
      if (element.isJsonObject()) {
         JsonObject object = element.getAsJsonObject();
         BiomeDataBase.Operation operation = this.readMethod(object);
         dbase.setDensitySelector(biome, readDensitySelector(object, biome), operation);
      } else if (element.isJsonArray()) {
         dbase.setDensitySelector(biome, createScaleDensitySelector(element.getAsJsonArray()), BiomeDataBase.Operation.REPLACE);
      } else if (element.isJsonPrimitive()) {
         JsonPrimitive prim = element.getAsJsonPrimitive();
         if (prim.isNumber()) {
            float value = prim.getAsFloat();
            dbase.setDensitySelector(biome, (rnd, n) -> {
               return (double)value;
            }, BiomeDataBase.Operation.REPLACE);
         } else if (prim.isString()) {
            String simple = prim.getAsString();
            if ("standard".equals(simple)) {
               double treeDensity = (double)MathHelper.clamp((float)biome.decorator.treesPerChunk / 10.0F, 0.0F, 1.0F);
               dbase.setDensitySelector(biome, (rnd, n) -> {
                  return n * treeDensity;
               }, BiomeDataBase.Operation.REPLACE);
            }
         }
      }

   }

   private static BiomePropertySelectors.IDensitySelector createScaleDensitySelector(JsonArray jsonArray) {
      if (jsonArray != null && jsonArray.isJsonArray()) {
         List<Float> parameters = new ArrayList();

         for(JsonElement element : jsonArray) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
               parameters.add(Float.valueOf(element.getAsJsonPrimitive().getAsFloat()));
            }
         }

         switch(parameters.size()) {
         case 0:
            return (rnd, n) -> {
               return n;
            };
         case 1:
            return (rnd, n) -> {
               return n * (double)((Float)parameters.get(0)).floatValue();
            };
         case 2:
            return (rnd, n) -> {
               return n * (double)((Float)parameters.get(0)).floatValue() + (double)((Float)parameters.get(1)).floatValue();
            };
         case 3:
            return (rnd, n) -> {
               return (n * (double)((Float)parameters.get(0)).floatValue() + (double)((Float)parameters.get(1)).floatValue()) * (double)((Float)parameters.get(2)).floatValue();
            };
         default:
            return (rnd, n) -> {
               return 0.0D;
            };
         }
      } else {
         return (rnd, n) -> {
            return n;
         };
      }
   }

   private static BiomePropertySelectors.IDensitySelector readDensitySelector(JsonObject mainObject, Biome biome) {
      JsonElement scaleElement = mainObject.get("scale");
      if (scaleElement != null && scaleElement.isJsonArray()) {
         return createScaleDensitySelector(scaleElement.getAsJsonArray());
      } else {
         JsonElement staticElement = mainObject.get("static");
         if (staticElement != null && staticElement.isJsonPrimitive() && staticElement.getAsJsonPrimitive().isNumber()) {
            return (rnd, n) -> {
               return (double)staticElement.getAsJsonPrimitive().getAsFloat();
            };
         } else {
            JsonElement mathElement = mainObject.get("math");
            if (mathElement != null) {
               JsonMath m = new JsonMath(mathElement, biome);
               return (rnd, n) -> {
                  return (double)m.apply(rnd, (float)n);
               };
            } else {
               return null;
            }
         }
      }
   }
}
