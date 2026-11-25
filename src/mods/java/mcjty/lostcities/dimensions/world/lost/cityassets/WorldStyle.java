package mcjty.lostcities.dimensions.world.lost.cityassets;

import com.google.common.base.Predicates;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import mcjty.lostcities.api.ILostCityAsset;
import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import mcjty.lostcities.dimensions.world.lost.BiomeInfo;
import mcjty.lostcities.varia.ChunkCoord;
import mcjty.lostcities.varia.Tools;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;

public class WorldStyle implements ILostCityAsset {
   private String name;
   private String outsideStyle;
   private final List<Pair<Predicate<WorldStyle.Info>, Pair<Float, String>>> cityStyleSelector = new ArrayList();

   public WorldStyle(JsonObject object) {
      this.readFromJSon(object);
   }

   public WorldStyle(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void readFromJSon(JsonObject object) {
      this.name = object.get("name").getAsString();
      this.outsideStyle = object.get("outsidestyle").getAsString();

      for(JsonElement element : object.get("citystyles").getAsJsonArray()) {
         JsonObject o = element.getAsJsonObject();
         float factor = o.get("factor").getAsFloat();
         String building = o.get("citystyle").getAsString();
         Predicate<WorldStyle.Info> predicate = Predicates.alwaysTrue();
         if (o.has("biomes")) {
            JsonArray ar = o.get("biomes").getAsJsonArray();
            Set<String> biomes = new HashSet();

            for(JsonElement el : ar) {
               Biome biome = (Biome)Biome.REGISTRY.getObject(new ResourceLocation(el.getAsString()));
               if (biome != null) {
                  biomes.add(((ResourceLocation)Biome.REGISTRY.getNameForObject(biome)).toString());
               }
            }

            predicate = (info) -> {
               return this.hasBiomes(info, biomes);
            };
         }

         this.cityStyleSelector.add(Pair.of(predicate, Pair.of(Float.valueOf(factor), building)));
      }

   }

   private boolean isValidBiome(Set<String> biomeSet, Biome biome) {
      ResourceLocation object = (ResourceLocation)Biome.REGISTRY.getNameForObject(biome);
      return biomeSet.contains(object.toString());
   }

   private boolean hasBiomes(WorldStyle.Info info, Set<String> biomeSet) {
      Biome[] biomes = info.biomes;
      return this.isValidBiome(biomeSet, biomes[55]) || this.isValidBiome(biomeSet, biomes[54]) || this.isValidBiome(biomeSet, biomes[56]) || this.isValidBiome(biomeSet, biomes[5]) || this.isValidBiome(biomeSet, biomes[95]);
   }

   public String getOutsideStyle() {
      return this.outsideStyle;
   }

   public JsonObject writeToJSon() {
      JsonObject object = new JsonObject();
      object.add("type", new JsonPrimitive("worldstyle"));
      object.add("name", new JsonPrimitive(this.name));
      object.add("outsidestyle", new JsonPrimitive(this.outsideStyle));
      JsonArray array = new JsonArray();

      for(Pair<Predicate<WorldStyle.Info>, Pair<Float, String>> pair : this.cityStyleSelector) {
         JsonObject o = new JsonObject();
         Pair<Float, String> ff = (Pair)pair.getValue();
         o.add("factor", new JsonPrimitive((Number)ff.getKey()));
         o.add("citystyle", new JsonPrimitive((String)ff.getValue()));
         array.add(o);
      }

      object.add("citystyles", array);
      return object;
   }

   public String getRandomCityStyle(LostCityChunkGenerator provider, int chunkX, int chunkZ, Random random) {
      Biome[] biomes = BiomeInfo.getBiomeInfo(provider, new ChunkCoord(provider.dimensionId, chunkX, chunkZ)).getBiomes();
      WorldStyle.Info info = new WorldStyle.Info(biomes, chunkX, chunkZ);
      List<Pair<Float, String>> ct = new ArrayList();

      for(Pair<Predicate<WorldStyle.Info>, Pair<Float, String>> pair : this.cityStyleSelector) {
         if (((Predicate)pair.getKey()).test(info)) {
            ct.add(pair.getValue());
         }
      }

      return Tools.getRandomFromList(random, ct);
   }

   private static class Info {
      private Biome[] biomes;
      private int chunkX;
      private int chunkZ;

      public Info(Biome[] biomes, int chunkX, int chunkZ) {
         this.biomes = biomes;
         this.chunkX = chunkX;
         this.chunkZ = chunkZ;
      }
   }
}
