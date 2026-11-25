package com.ferreusveritas.dynamictrees.worldgen.json;

import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.world.biome.Biome;

public class JsonBiomePropertyApplierChance implements IJsonBiomeApplier {
   public JsonBiomePropertyApplierChance() {
   }

   public void apply(BiomeDataBase dbase, JsonElement element, Biome biome) {
      if (element.isJsonObject()) {
         JsonObject object = element.getAsJsonObject();
         dbase.setChanceSelector(biome, this.readChanceSelector(object, biome), this.readMethod(object));
      } else if (element.isJsonPrimitive()) {
         JsonPrimitive prim = element.getAsJsonPrimitive();
         if (prim.isNumber()) {
            dbase.setChanceSelector(biome, this.createSimpleChanceSelector(prim.getAsFloat()), BiomeDataBase.Operation.REPLACE);
         } else if (prim.isString()) {
            String simple = prim.getAsString();
            if ("standard".equals(simple)) {
               dbase.setChanceSelector(biome, (rnd, spc, rad) -> {
                  return rnd.nextFloat() < (rad > 3 ? 2.0F / (float)rad : 1.0F) ? BiomePropertySelectors.EnumChance.OK : BiomePropertySelectors.EnumChance.CANCEL;
               }, BiomeDataBase.Operation.REPLACE);
            }
         }
      }

   }

   private BiomePropertySelectors.IChanceSelector createSimpleChanceSelector(float value) {
      if (value <= 0.0F) {
         return (rnd, spc, rad) -> {
            return BiomePropertySelectors.EnumChance.CANCEL;
         };
      } else {
         return value >= 1.0F ? (rnd, spc, rad) -> {
            return BiomePropertySelectors.EnumChance.OK;
         } : (rnd, spc, rad) -> {
            return rnd.nextFloat() < value ? BiomePropertySelectors.EnumChance.OK : BiomePropertySelectors.EnumChance.CANCEL;
         };
      }
   }

   private BiomePropertySelectors.IChanceSelector readChanceSelector(JsonObject mainObject, Biome biome) {
      JsonElement staticElement = mainObject.get("static");
      if (staticElement != null && staticElement.isJsonPrimitive()) {
         if (staticElement.getAsJsonPrimitive().isNumber()) {
            return this.createSimpleChanceSelector(staticElement.getAsJsonPrimitive().getAsFloat());
         }

         if (staticElement.getAsJsonPrimitive().isString()) {
            String value = staticElement.getAsString();
            if (this.isDefault(value)) {
               return (rnd, spc, rad) -> {
                  return BiomePropertySelectors.EnumChance.UNHANDLED;
               };
            }
         }
      }

      JsonElement mathElement = mainObject.get("math");
      if (mathElement != null) {
         JsonMath m = new JsonMath(mathElement, biome);
         return (rnd, spc, rad) -> {
            return rnd.nextFloat() < m.apply(rnd, spc, (float)rad) ? BiomePropertySelectors.EnumChance.OK : BiomePropertySelectors.EnumChance.CANCEL;
         };
      } else {
         return null;
      }
   }
}
