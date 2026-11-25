package greymerk.roguelike.dungeon.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import greymerk.roguelike.config.RogueConfig;
import greymerk.roguelike.worldgen.PositionInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class SpawnCriteria {
   private int weight;
   private final List<ResourceLocation> biomes;
   private final List<Type> biomeTypes;
   private final List<Integer> validDimensions;
   private ArrayList<String> biomeStrings;

   public SpawnCriteria() {
      this(new JsonObject());
   }

   public SpawnCriteria(SpawnCriteria spawnCriteria) {
      this.biomes = new ArrayList();
      this.biomeTypes = new ArrayList();
      this.validDimensions = new ArrayList();
      this.weight = spawnCriteria.weight;
      this.biomes.addAll(spawnCriteria.biomes);
      this.biomeTypes.addAll(spawnCriteria.biomeTypes);
      this.validDimensions.addAll(spawnCriteria.validDimensions);
   }

   public SpawnCriteria(JsonObject data) {
      this.biomes = new ArrayList();
      this.biomeTypes = new ArrayList();
      this.validDimensions = new ArrayList();
      this.weight = data.has("weight") ? data.get("weight").getAsInt() : 1;
      this.biomeStrings = parseBiomes(data);
      this.biomes.addAll((Collection)this.biomeStrings.stream().map(ResourceLocation::new).collect(Collectors.toList()));
      this.addBiomeTypeCriteria(data);
      this.addDimensionCriteria(data);
   }

   public SpawnCriteria inherit(SpawnCriteria toInherit) {
      SpawnCriteria result = new SpawnCriteria(this);
      result.biomes.addAll(toInherit.biomes);
      result.biomeTypes.addAll(toInherit.biomeTypes);
      result.validDimensions.addAll(toInherit.validDimensions);
      return result;
   }

   private static ArrayList<String> parseBiomes(JsonObject data) {
      if (!data.has("biomes")) {
         return new ArrayList();
      } else {
         ArrayList<String> biomes = new ArrayList();

         for(JsonElement biome : data.get("biomes").getAsJsonArray()) {
            if (!biome.isJsonNull()) {
               biomes.add(biome.getAsString());
            }
         }

         return biomes;
      }
   }

   private void addBiomeTypeCriteria(JsonObject data) {
      if (data.has("biomeTypes")) {
         for(JsonElement biomeType : data.get("biomeTypes").getAsJsonArray()) {
            if (!biomeType.isJsonNull()) {
               Type type = Type.getType(biomeType.getAsString().toUpperCase(), new Type[0]);
               if (BiomeDictionary.getBiomes(type).size() > 0) {
                  this.biomeTypes.add(type);
               }
            }
         }
      }

   }

   private void addDimensionCriteria(JsonObject data) {
      if (data.has("dimensions")) {
         for(JsonElement dimension : data.get("dimensions").getAsJsonArray()) {
            if (!dimension.isJsonNull()) {
               this.validDimensions.add(Integer.valueOf(dimension.getAsInt()));
            }
         }
      }

   }

   public static boolean isValidDimension(int dimension) {
      return isValidDimension(dimension, RogueConfig.DIMENSIONWL.getIntList(), RogueConfig.DIMENSIONBL.getIntList());
   }

   public static boolean isValidDimension(int dimension, List<Integer> whiteList, List<Integer> blackList) {
      return !blackList.contains(Integer.valueOf(dimension)) && (whiteList.isEmpty() || whiteList.contains(Integer.valueOf(dimension)));
   }

   public int getWeight() {
      return this.weight;
   }

   public void setWeight(int weight) {
      this.weight = weight;
   }

   public void setBiomeTypes(List<Type> biomeTypes) {
      this.biomeTypes.addAll(biomeTypes);
   }

   public boolean isValid(PositionInfo positionInfo) {
      return this.isBiomeValid(positionInfo) && this.isBiomeTypeValid(positionInfo) && this.isDimensionValid(positionInfo);
   }

   private boolean isBiomeValid(PositionInfo positionInfo) {
      return this.biomes.isEmpty() || this.includesBiome(positionInfo);
   }

   private boolean includesBiome(PositionInfo positionInfo) {
      return this.biomes.contains(positionInfo.getBiome().getRegistryName());
   }

   private boolean isBiomeTypeValid(PositionInfo positionInfo) {
      return this.biomeTypes.isEmpty() || this.includesBiomeType(positionInfo);
   }

   private boolean includesBiomeType(PositionInfo positionInfo) {
      Biome biomeHere = positionInfo.getBiome();
      return this.biomeTypes.stream().anyMatch((biomeType) -> {
         return BiomeDictionary.hasType(biomeHere, biomeType);
      });
   }

   private boolean isDimensionValid(PositionInfo positionInfo) {
      return isValidDimension(positionInfo.getDimension()) && this.validDimensions.isEmpty() || this.includesDimension(positionInfo);
   }

   private boolean includesDimension(PositionInfo positionInfo) {
      return this.validDimensions.contains(Integer.valueOf(positionInfo.getDimension()));
   }

   public List<ResourceLocation> getBiomes() {
      return this.biomes;
   }

   public List<Type> getBiomeTypes() {
      return this.biomeTypes;
   }

   public List<Integer> getValidDimensions() {
      return this.validDimensions;
   }
}
