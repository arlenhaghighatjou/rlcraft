package com.ferreusveritas.dynamictrees.worldgen.json;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map.Entry;
import net.minecraft.world.biome.Biome;

public class JsonBiomePropertyApplierSpecies implements IJsonBiomeApplier {
   public JsonBiomePropertyApplierSpecies() {
   }

   public void apply(BiomeDataBase dbase, JsonElement element, Biome biome) {
      if (element.isJsonObject()) {
         JsonObject object = element.getAsJsonObject();
         dbase.setSpeciesSelector(biome, this.readSpeciesSelector(object), this.readMethod(object));
      } else if (element.isJsonPrimitive()) {
         dbase.setSpeciesSelector(biome, this.createStaticSpeciesSelector(element.getAsString()), BiomeDataBase.Operation.REPLACE);
      }

   }

   private BiomePropertySelectors.ISpeciesSelector createStaticSpeciesSelector(String speciesName) {
      if (this.isDefault(speciesName)) {
         return new BiomePropertySelectors.StaticSpeciesSelector();
      } else {
         Species species = TreeRegistry.findSpeciesSloppy(speciesName);
         return species != Species.NULLSPECIES ? new BiomePropertySelectors.StaticSpeciesSelector(new BiomePropertySelectors.SpeciesSelection(species)) : null;
      }
   }

   private BiomePropertySelectors.ISpeciesSelector readSpeciesSelector(JsonObject mainObject) {
      JsonElement staticElement = mainObject.get("static");
      if (staticElement != null && staticElement.isJsonPrimitive()) {
         return this.createStaticSpeciesSelector(staticElement.getAsString());
      } else {
         JsonElement randomElement = mainObject.get("random");
         if (randomElement != null && randomElement.isJsonObject()) {
            BiomePropertySelectors.RandomSpeciesSelector rand = new BiomePropertySelectors.RandomSpeciesSelector();

            for(Entry<String, JsonElement> entry : randomElement.getAsJsonObject().entrySet()) {
               String speciesName = (String)entry.getKey();
               JsonElement speciesElement = (JsonElement)entry.getValue();
               int weight = 0;
               if (speciesElement.isJsonPrimitive() && speciesElement.getAsJsonPrimitive().isNumber()) {
                  weight = speciesElement.getAsJsonPrimitive().getAsInt();
                  if (weight > 0) {
                     if (this.isDefault(speciesName)) {
                        rand.add(weight);
                     } else {
                        Species species = TreeRegistry.findSpeciesSloppy(speciesName);
                        if (species != Species.NULLSPECIES) {
                           rand.add(species, weight);
                        }
                     }
                  }
               }
            }

            if (rand.getSize() > 0) {
               return rand;
            }
         }

         return null;
      }
   }
}
