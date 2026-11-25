package com.ferreusveritas.dynamictrees.worldgen;

import com.ferreusveritas.dynamictrees.api.WorldGenRegistry;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeDataBasePopulator;
import com.ferreusveritas.dynamictrees.util.JsonHelper;
import com.ferreusveritas.dynamictrees.worldgen.json.IJsonBiomeApplier;
import com.ferreusveritas.dynamictrees.worldgen.json.IJsonBiomeSelector;
import com.ferreusveritas.dynamictrees.worldgen.json.JsonBiomePropertyApplierChance;
import com.ferreusveritas.dynamictrees.worldgen.json.JsonBiomePropertyApplierDensity;
import com.ferreusveritas.dynamictrees.worldgen.json.JsonBiomePropertyApplierSpecies;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeDataBasePopulatorJson implements IBiomeDataBasePopulator {
   public static final String SPECIES = "species";
   public static final String DENSITY = "density";
   public static final String CHANCE = "chance";
   public static final String CANCELVANILLA = "cancelvanilla";
   public static final String MULTIPASS = "multipass";
   public static final String SUBTERRANEAN = "subterranean";
   public static final String FORESTNESS = "forestness";
   public static final String BLACKLIST = "blacklist";
   public static final String RESET = "reset";
   public static final String WHITE = "white";
   public static final String SELECT = "select";
   public static final String APPLY = "apply";
   public static final String NAME = "name";
   public static final String TYPE = "type";
   public static Set<Biome> blacklistedBiomes = new HashSet();
   private static Map<String, IJsonBiomeSelector> jsonBiomeSelectorMap = new HashMap();
   private static Map<String, IJsonBiomeApplier> jsonBiomeApplierMap = new HashMap();
   private final JsonElement jsonElement;

   public BiomeDataBasePopulatorJson(ResourceLocation jsonLocation) {
      this(JsonHelper.load(jsonLocation));
   }

   public BiomeDataBasePopulatorJson(JsonElement jsonElement) {
      this.jsonElement = jsonElement;
   }

   public static void addJsonBiomeSelector(String name, IJsonBiomeSelector selector) {
      jsonBiomeSelectorMap.put(name, selector);
   }

   public static void addJsonBiomeApplier(String name, IJsonBiomeApplier applier) {
      jsonBiomeApplierMap.put(name, applier);
   }

   public static void cleanup() {
      jsonBiomeApplierMap = new HashMap();
      jsonBiomeSelectorMap = new HashMap();
      blacklistedBiomes = new HashSet();
   }

   public static void registerJsonCapabilities(WorldGenRegistry.BiomeDataBaseJsonCapabilityRegistryEvent event) {
      event.register("name", (jsonElement) -> {
         if (jsonElement == null) {
            return (b) -> {
               return false;
            };
         } else if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
            return readNameSelector(jsonElement.getAsString());
         } else {
            return jsonElement.isJsonArray() ? readArrayNameSelector(jsonElement.getAsJsonArray()) : (b) -> {
               return false;
            };
         }
      });
      event.register("type", (jsonElement) -> {
         if (jsonElement == null) {
            return (b) -> {
               return false;
            };
         } else {
            List<Type> typesWhitelist = new ArrayList();
            List<Type> typesBlacklist = new ArrayList();
            if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
               readTypeSelector(jsonElement.getAsString(), typesWhitelist, typesBlacklist);
            } else if (jsonElement.isJsonArray()) {
               readArrayTypeSelector(jsonElement.getAsJsonArray(), typesWhitelist, typesBlacklist);
            }

            return (b) -> {
               return biomeHasTypes(b, typesWhitelist) && (typesBlacklist.size() == 0 || !biomeHasAnyType(b, typesBlacklist));
            };
         }
      });
      event.register("species", new JsonBiomePropertyApplierSpecies());
      event.register("density", new JsonBiomePropertyApplierDensity());
      event.register("chance", new JsonBiomePropertyApplierChance());
      event.register("cancelvanilla", (dbase, element, biome) -> {
         if (element.isJsonPrimitive()) {
            boolean cancel = element.getAsBoolean();
            dbase.setCancelVanillaTreeGen(biome, cancel);
         }

      });
      event.register("multipass", (dbase, element, biome) -> {
         if (element.isJsonPrimitive()) {
            boolean multipass = element.getAsBoolean();
            if (multipass) {
               dbase.setMultipass(biome, (pass) -> {
                  switch(pass.intValue()) {
                  case 0:
                     return Integer.valueOf(0);
                  case 1:
                     return Integer.valueOf(5);
                  case 2:
                     return Integer.valueOf(3);
                  default:
                     return Integer.valueOf(-1);
                  }
               });
            }
         }

      });
      event.register("subterranean", (dbase, element, biome) -> {
         if (element.isJsonPrimitive()) {
            boolean subterranean = element.getAsBoolean();
            dbase.setIsSubterranean(biome, subterranean);
         }

      });
      event.register("forestness", (dbase, element, biome) -> {
         if (element.isJsonPrimitive()) {
            float forestness = element.getAsFloat();
            dbase.setForestness(biome, forestness);
         }

      });
      event.register("blacklist", (dbase, element, biome) -> {
         if (element.isJsonPrimitive()) {
            boolean blacklist = element.getAsBoolean();
            if (blacklist) {
               blacklistedBiomes.add(biome);
            } else {
               blacklistedBiomes.remove(biome);
            }
         }

      });
      event.register("reset", (dbase, element, biome) -> {
         dbase.setCancelVanillaTreeGen(biome, false);
         dbase.setSpeciesSelector(biome, (pos, dirt, rnd) -> {
            return new BiomePropertySelectors.SpeciesSelection();
         }, BiomeDataBase.Operation.REPLACE);
         dbase.setDensitySelector(biome, (rnd, nd) -> {
            return -1.0D;
         }, BiomeDataBase.Operation.REPLACE);
         dbase.setChanceSelector(biome, (rnd, spc, rad) -> {
            return BiomePropertySelectors.EnumChance.UNHANDLED;
         }, BiomeDataBase.Operation.REPLACE);
         dbase.setForestness(biome, 0.0F);
         dbase.setIsSubterranean(biome, false);
         dbase.setMultipass(biome, (pass) -> {
            return pass.intValue() == 0 ? 0 : -1;
         });
      });
   }

   private static void readArrayTypeSelector(JsonArray typeArray, List<Type> typesWhitelist, List<Type> typesBlacklist) {
      for(JsonElement element : typeArray) {
         if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String stringElem = element.getAsString();
            if (stringElem.charAt(0) == '!') {
               typesBlacklist.add(Type.getType(stringElem.substring(1), new Type[0]));
            } else {
               typesWhitelist.add(Type.getType(stringElem, new Type[0]));
            }
         }
      }

   }

   private static void readTypeSelector(String typeMatch, List<Type> typesWhitelist, List<Type> typesBlacklist) {
      for(String match : (List)Arrays.stream(typeMatch.split(",")).collect(Collectors.toList())) {
         if (match.charAt(0) == '!') {
            typesBlacklist.add(Type.getType(match.substring(1), new Type[0]));
         } else {
            typesWhitelist.add(Type.getType(match, new Type[0]));
         }
      }

   }

   private static Predicate<Biome> readArrayNameSelector(JsonArray nameArray) {
      List<String> namesWhitelist = new ArrayList();
      List<String> namesBlacklist = new ArrayList();

      for(JsonElement nameElement : nameArray) {
         if (nameElement.isJsonPrimitive()) {
            JsonPrimitive primitive = nameElement.getAsJsonPrimitive();
            if (primitive.isString()) {
               String biomeMatch = primitive.getAsString();
               if (biomeMatch.charAt(0) == '!') {
                  namesBlacklist.add(biomeMatch.substring(1));
               } else {
                  namesWhitelist.add(biomeMatch);
               }
            }
         }
      }

      return (biome) -> {
         String regName = String.valueOf(biome.getRegistryName());
         Stream var10000 = namesWhitelist.stream();
         regName.getClass();
         boolean var5;
         if (var10000.anyMatch(regName::matches)) {
            Stream var4 = namesBlacklist.stream();
            regName.getClass();
            if (var4.noneMatch(regName::matches)) {
               var5 = true;
               return var5;
            }
         }

         var5 = false;
         return var5;
      };
   }

   private static Predicate<Biome> readNameSelector(String biomeMatch) {
      return biomeMatch.charAt(0) == '!' ? (biome) -> {
         return !String.valueOf(biome.getRegistryName()).matches(biomeMatch.substring(1));
      } : (biome) -> {
         return String.valueOf(biome.getRegistryName()).matches(biomeMatch);
      };
   }

   public static boolean biomeHasAnyType(Biome biome, List<Type> types) {
      return types.stream().anyMatch((t) -> {
         return BiomeDictionary.hasType(biome, t);
      });
   }

   public static boolean biomeHasTypes(Biome biome, List<Type> types) {
      return types.stream().allMatch((t) -> {
         return BiomeDictionary.hasType(biome, t);
      });
   }

   public static boolean isComment(String s) {
      return s.startsWith("__");
   }

   public void populate(BiomeDataBase biomeDataBase) {
      if (this.jsonElement != null && this.jsonElement.isJsonArray()) {
         for(JsonElement sectionElement : this.jsonElement.getAsJsonArray()) {
            if (sectionElement.isJsonObject()) {
               JsonObject section = sectionElement.getAsJsonObject();
               this.readSection(section, biomeDataBase);
            }
         }
      }

   }

   private void readSection(JsonObject section, BiomeDataBase dbase) {
      List<BiomeDataBasePopulatorJson.JsonBiomeSelectorData> selectors = new LinkedList();
      List<BiomeDataBasePopulatorJson.JsonBiomeApplierData> appliers = new LinkedList();

      for(Entry<String, JsonElement> entry : section.entrySet()) {
         String key = (String)entry.getKey();
         JsonElement element = (JsonElement)entry.getValue();
         if (!isComment(key)) {
            if ("white".equals(key)) {
               if (element.isJsonPrimitive() && "all".equals(element.getAsString())) {
                  blacklistedBiomes.clear();
               }
            } else if ("select".equals(key)) {
               if (element.isJsonObject()) {
                  for(Entry<String, JsonElement> selectElement : element.getAsJsonObject().entrySet()) {
                     String selectorName = (String)selectElement.getKey();
                     if (!isComment(selectorName)) {
                        IJsonBiomeSelector selector = (IJsonBiomeSelector)jsonBiomeSelectorMap.get(selectorName);
                        if (selector != null) {
                           selectors.add(new BiomeDataBasePopulatorJson.JsonBiomeSelectorData(selector, (JsonElement)selectElement.getValue()));
                        } else {
                           System.err.println("Json Error: Undefined selector property \"" + selectorName + "\"");
                        }
                     }
                  }
               }
            } else if ("apply".equals(key)) {
               if (element.isJsonObject()) {
                  for(Entry<String, JsonElement> selectElement : element.getAsJsonObject().entrySet()) {
                     String applierName = (String)selectElement.getKey();
                     if (!isComment(applierName)) {
                        IJsonBiomeApplier applier = (IJsonBiomeApplier)jsonBiomeApplierMap.get(applierName);
                        if (applier != null) {
                           appliers.add(new BiomeDataBasePopulatorJson.JsonBiomeApplierData(applier, (JsonElement)selectElement.getValue()));
                        } else {
                           System.err.println("Json Error: Undefined applier property \"" + applierName + "\"");
                        }
                     }
                  }
               }
            } else {
               System.err.println("Json Error: Undefined operation \"" + key + "\"");
            }
         }
      }

      Stream<Biome> stream = Lists.newArrayList(Biome.REGISTRY).stream();

      for(BiomeDataBasePopulatorJson.JsonBiomeSelectorData s : selectors) {
         stream = stream.filter(s.getFilter());
      }

      stream = stream.filter((b) -> {
         return !blacklistedBiomes.contains(b);
      });
      stream.forEach((biome) -> {
         appliers.forEach((a) -> {
            a.apply(dbase, biome);
         });
      });
   }

   private class JsonBiomeApplierData {
      IJsonBiomeApplier applier;
      JsonElement elementData;

      JsonBiomeApplierData(IJsonBiomeApplier applier, JsonElement elementData) {
         this.applier = applier;
         this.elementData = elementData;
      }

      void apply(BiomeDataBase dbase, Biome biome) {
         this.applier.apply(dbase, this.elementData, biome);
      }
   }

   private class JsonBiomeSelectorData {
      final IJsonBiomeSelector selector;
      final JsonElement elementData;

      JsonBiomeSelectorData(IJsonBiomeSelector selector, JsonElement elementData) {
         this.selector = selector;
         this.elementData = elementData;
      }

      Predicate<Biome> getFilter() {
         return this.selector.getFilter(this.elementData);
      }
   }
}
