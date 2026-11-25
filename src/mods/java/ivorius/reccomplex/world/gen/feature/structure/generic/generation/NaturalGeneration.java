package ivorius.reccomplex.world.gen.feature.structure.generic.generation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.files.SimpleLeveledRegistry;
import ivorius.reccomplex.gui.editstructure.gentypes.TableDataSourceNaturalGeneration;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.presets.PresettedList;
import ivorius.reccomplex.utils.presets.PresettedObjects;
import ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData;
import ivorius.reccomplex.world.gen.feature.selector.CachedStructureSelectors;
import ivorius.reccomplex.world.gen.feature.selector.EnvironmentalSelection;
import ivorius.reccomplex.world.gen.feature.selector.MixingStructureSelector;
import ivorius.reccomplex.world.gen.feature.selector.NaturalStructureSelector;
import ivorius.reccomplex.world.gen.feature.selector.StructureSelector;
import ivorius.reccomplex.world.gen.feature.structure.Placer;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedBiomeMatcher;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedDimensionMatcher;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.SelectivePlacer;
import ivorius.reccomplex.world.gen.feature.structure.generic.presets.BiomeMatcherPresets;
import ivorius.reccomplex.world.gen.feature.structure.generic.presets.DimensionMatcherPresets;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NaturalGeneration extends GenerationType implements EnvironmentalSelection<NaturalStructureSelector.Category> {
   private static Gson gson = createGson();
   public final PresettedList<WeightedBiomeMatcher> biomeWeights;
   public final PresettedList<WeightedDimensionMatcher> dimensionWeights;
   private Double generationWeight;
   public String generationCategory;
   public SelectivePlacer placer;
   public NaturalGeneration.SpawnLimitation spawnLimitation;

   public NaturalGeneration() {
      this((String)null, "decoration");
      this.biomeWeights.setPreset("overworld");
      this.dimensionWeights.setPreset("overworld");
      this.placer = new SelectivePlacer();
   }

   public NaturalGeneration(@Nullable String id, String generationCategory) {
      super(id != null ? id : randomID(NaturalGeneration.class));
      this.biomeWeights = new PresettedList<WeightedBiomeMatcher>(BiomeMatcherPresets.instance(), (String)null);
      this.dimensionWeights = new PresettedList<WeightedDimensionMatcher>(DimensionMatcherPresets.instance(), (String)null);
      this.generationCategory = generationCategory;
   }

   public static Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(NaturalGeneration.class, new NaturalGeneration.Serializer());
      builder.registerTypeAdapter(WeightedBiomeMatcher.class, new WeightedBiomeMatcher.Serializer());
      builder.registerTypeAdapter(WeightedDimensionMatcher.class, new WeightedDimensionMatcher.Serializer());
      return builder.create();
   }

   public static Gson getGson() {
      return gson;
   }

   public static NaturalGeneration deserializeFromVersion1(JsonObject jsonObject, JsonDeserializationContext context) {
      String generationCategory = JsonUtils.getString(jsonObject, "generationCategory");
      NaturalGeneration naturalGeneration = new NaturalGeneration(readID(new JsonObject()), generationCategory);
      if (jsonObject.has("generationBiomes")) {
         WeightedBiomeMatcher[] infos = (WeightedBiomeMatcher[])gson.fromJson(jsonObject.get("generationBiomes"), WeightedBiomeMatcher[].class);
         naturalGeneration.biomeWeights.setContents(Arrays.asList(infos));
      } else {
         naturalGeneration.biomeWeights.setPreset("overworld");
      }

      naturalGeneration.dimensionWeights.setPreset("overworld");
      naturalGeneration.placer = SelectivePlacer.Serializer.readLegacyPlacer(context, JsonUtils.getJsonObject(jsonObject, "generationY", new JsonObject()));
      return naturalGeneration;
   }

   public static CachedStructureSelectors<MixingStructureSelector<NaturalGeneration, NaturalStructureSelector.Category>> selectors(StructureRegistry registry) {
      return ((NaturalGeneration.Cache)registry.module(NaturalGeneration.Cache.class)).selectors;
   }

   public Double getGenerationWeight() {
      return this.generationWeight;
   }

   public void setGenerationWeight(Double generationWeight) {
      this.generationWeight = generationWeight;
   }

   public double getGenerationWeight(WorldProvider provider, Biome biome) {
      return this.getActiveGenerationWeight() * StructureSelector.generationWeight(provider, biome, this.biomeWeights, this.dimensionWeights);
   }

   public NaturalStructureSelector.Category generationCategory() {
      return NaturalStructureSelector.CATEGORY_REGISTRY.getActive(this.generationCategory);
   }

   public double getActiveGenerationWeight() {
      return this.generationWeight != null ? this.generationWeight.doubleValue() : 1.0D;
   }

   public boolean hasDefaultWeight() {
      return this.generationWeight == null;
   }

   public boolean hasLimitations() {
      return this.spawnLimitation != null;
   }

   public NaturalGeneration.SpawnLimitation getLimitations() {
      return this.spawnLimitation;
   }

   @Nonnull
   public String id() {
      return this.id;
   }

   public void setID(@Nonnull String id) {
      this.id = id;
   }

   public String displayString() {
      return IvTranslations.get("reccomplex.generationInfo.natural");
   }

   @Nullable
   public Placer placer() {
      return this.placer;
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(MazeVisualizationContext mazeVisualizationContext, TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceNaturalGeneration(navigator, delegate, this);
   }

   public static class Cache extends SimpleLeveledRegistry.Module<StructureRegistry> {
      protected CachedStructureSelectors<MixingStructureSelector<NaturalGeneration, NaturalStructureSelector.Category>> selectors;

      public Cache() {
      }

      public void setRegistry(StructureRegistry registry) {
         this.selectors = new CachedStructureSelectors<MixingStructureSelector<NaturalGeneration, NaturalStructureSelector.Category>>((biome, worldProvider) -> {
            return new MixingStructureSelector(registry.activeMap(), worldProvider, biome, NaturalGeneration.class);
         });
      }

      public void invalidate() {
         this.selectors.clear();
      }
   }

   public static class Serializer implements JsonSerializer<NaturalGeneration>, JsonDeserializer<NaturalGeneration> {
      public Serializer() {
      }

      public NaturalGeneration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "naturalGenerationInfo");
         String id = GenerationType.readID(jsonObject);
         String generationCategory = JsonUtils.getString(jsonObject, "generationCategory");
         NaturalGeneration naturalGeneration = new NaturalGeneration(id, generationCategory);
         naturalGeneration.placer = (SelectivePlacer)SelectivePlacer.gson.fromJson(json, SelectivePlacer.class);
         if (jsonObject.has("generationWeight")) {
            naturalGeneration.generationWeight = JsonUtils.getDouble(jsonObject, "generationWeight");
         }

         PresettedObjects.read(jsonObject, NaturalGeneration.gson, naturalGeneration.biomeWeights, "biomeWeightsPreset", "generationBiomes", (new TypeToken<ArrayList<WeightedBiomeMatcher>>() {
         }).getType());
         PresettedObjects.read(jsonObject, NaturalGeneration.gson, naturalGeneration.dimensionWeights, "dimensionWeightsPreset", "generationDimensions", (new TypeToken<ArrayList<WeightedDimensionMatcher>>() {
         }).getType());
         if (jsonObject.has("spawnLimitation")) {
            naturalGeneration.spawnLimitation = (NaturalGeneration.SpawnLimitation)context.deserialize(jsonObject.get("spawnLimitation"), NaturalGeneration.SpawnLimitation.class);
         }

         return naturalGeneration;
      }

      public JsonElement serialize(NaturalGeneration src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = (JsonObject)SelectivePlacer.gson.toJsonTree(src.placer);
         jsonObject.addProperty("id", src.id);
         jsonObject.addProperty("generationCategory", src.generationCategory);
         if (src.generationWeight != null) {
            jsonObject.addProperty("generationWeight", src.generationWeight);
         }

         PresettedObjects.write(jsonObject, NaturalGeneration.gson, src.biomeWeights, "biomeWeightsPreset", "generationBiomes");
         PresettedObjects.write(jsonObject, NaturalGeneration.gson, src.dimensionWeights, "dimensionWeightsPreset", "generationDimensions");
         if (src.spawnLimitation != null) {
            jsonObject.add("spawnLimitation", context.serialize(src.spawnLimitation));
         }

         return jsonObject;
      }
   }

   public static class SpawnLimitation {
      public int maxCount = 1;
      public NaturalGeneration.SpawnLimitation.Context context = NaturalGeneration.SpawnLimitation.Context.DIMENSION;

      public SpawnLimitation() {
      }

      public boolean areResolved(World world, String structureID) {
         return WorldStructureGenerationData.get(world).getEntriesByID(structureID).size() < this.maxCount;
      }

      public static enum Context {
         @SerializedName("dimension")
         DIMENSION;

         private Context() {
         }
      }
   }
}
