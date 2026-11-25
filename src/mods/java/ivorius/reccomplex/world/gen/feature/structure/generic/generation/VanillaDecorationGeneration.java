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
import com.google.gson.reflect.TypeToken;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.files.SimpleLeveledRegistry;
import ivorius.reccomplex.gui.editstructure.gentypes.TableDataSourceVanillaDecorationGeneration;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.presets.PresettedList;
import ivorius.reccomplex.utils.presets.PresettedObjects;
import ivorius.reccomplex.world.gen.feature.decoration.RCBiomeDecorator;
import ivorius.reccomplex.world.gen.feature.selector.CachedStructureSelectors;
import ivorius.reccomplex.world.gen.feature.selector.EnvironmentalSelection;
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
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VanillaDecorationGeneration extends GenerationType implements EnvironmentalSelection<RCBiomeDecorator.DecorationType> {
   private static Gson gson = createGson();
   public Double generationWeight;
   public final PresettedList<WeightedBiomeMatcher> biomeWeights;
   public final PresettedList<WeightedDimensionMatcher> dimensionWeights;
   public RCBiomeDecorator.DecorationType type;
   public SelectivePlacer placer;

   public VanillaDecorationGeneration() {
      this((String)null, (Double)null, RCBiomeDecorator.DecorationType.TREE, BlockPos.ORIGIN);
      this.biomeWeights.setPreset("overworld");
      this.dimensionWeights.setPreset("overworld");
      this.placer = new SelectivePlacer();
   }

   public VanillaDecorationGeneration(@Nullable String id, Double generationWeight, RCBiomeDecorator.DecorationType type, BlockPos spawnShift) {
      super(id != null ? id : randomID(VanillaDecorationGeneration.class));
      this.biomeWeights = new PresettedList<WeightedBiomeMatcher>(BiomeMatcherPresets.instance(), (String)null);
      this.dimensionWeights = new PresettedList<WeightedDimensionMatcher>(DimensionMatcherPresets.instance(), (String)null);
      this.type = type;
      this.generationWeight = generationWeight;
   }

   public static Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(VanillaDecorationGeneration.class, new VanillaDecorationGeneration.Serializer());
      builder.registerTypeAdapter(WeightedBiomeMatcher.class, new WeightedBiomeMatcher.Serializer());
      builder.registerTypeAdapter(WeightedDimensionMatcher.class, new WeightedDimensionMatcher.Serializer());
      return builder.create();
   }

   public static Gson getGson() {
      return gson;
   }

   public static CachedStructureSelectors<StructureSelector<VanillaDecorationGeneration, RCBiomeDecorator.DecorationType>> selectors(StructureRegistry registry) {
      return ((VanillaDecorationGeneration.Cache)registry.module(VanillaDecorationGeneration.Cache.class)).selectors;
   }

   @Nonnull
   public String id() {
      return this.id;
   }

   public void setID(@Nonnull String id) {
      this.id = id;
   }

   public String displayString() {
      return IvTranslations.get("reccomplex.generationInfo.decoration.title");
   }

   @Nullable
   public Placer placer() {
      return this.placer;
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(MazeVisualizationContext mazeVisualizationContext, TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceVanillaDecorationGeneration(navigator, delegate, this);
   }

   public double getActiveGenerationWeight() {
      return this.generationWeight != null ? this.generationWeight.doubleValue() : 1.0D;
   }

   public double getGenerationWeight(WorldProvider provider, Biome biome) {
      return this.getActiveGenerationWeight() * StructureSelector.generationWeight(provider, biome, this.biomeWeights, this.dimensionWeights);
   }

   public RCBiomeDecorator.DecorationType generationCategory() {
      return this.type;
   }

   public static class Cache extends SimpleLeveledRegistry.Module<StructureRegistry> {
      protected CachedStructureSelectors<StructureSelector<VanillaDecorationGeneration, RCBiomeDecorator.DecorationType>> selectors;

      public Cache() {
      }

      public void setRegistry(StructureRegistry registry) {
         this.selectors = new CachedStructureSelectors<StructureSelector<VanillaDecorationGeneration, RCBiomeDecorator.DecorationType>>((biome, worldProvider) -> {
            return new StructureSelector(registry.activeMap(), worldProvider, biome, VanillaDecorationGeneration.class);
         });
      }

      public void invalidate() {
         this.selectors.clear();
      }
   }

   public static class Serializer implements JsonSerializer<VanillaDecorationGeneration>, JsonDeserializer<VanillaDecorationGeneration> {
      public Serializer() {
      }

      public VanillaDecorationGeneration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "vanillaStructureSpawnInfo");
         String id = GenerationType.readID(jsonObject);
         Double spawnWeight = jsonObject.has("generationWeight") ? JsonUtils.getDouble(jsonObject, "generationWeight") : null;
         RCBiomeDecorator.DecorationType type = (RCBiomeDecorator.DecorationType)context.deserialize(jsonObject.get("type"), RCBiomeDecorator.DecorationType.class);
         int spawnX = JsonUtils.getInt(jsonObject, "spawnShiftX", 0);
         int spawnY = JsonUtils.getInt(jsonObject, "spawnShiftY", 0);
         int spawnZ = JsonUtils.getInt(jsonObject, "spawnShiftZ", 0);
         VanillaDecorationGeneration genInfo = new VanillaDecorationGeneration(id, spawnWeight, type, new BlockPos(spawnX, spawnY, spawnZ));
         PresettedObjects.read(jsonObject, VanillaDecorationGeneration.gson, genInfo.biomeWeights, "biomeWeightsPreset", "generationBiomes", (new TypeToken<ArrayList<WeightedBiomeMatcher>>() {
         }).getType());
         PresettedObjects.read(jsonObject, VanillaDecorationGeneration.gson, genInfo.dimensionWeights, "dimensionWeightsPreset", "generationDimensions", (new TypeToken<ArrayList<WeightedDimensionMatcher>>() {
         }).getType());
         genInfo.placer = JsonUtils.has(jsonObject, "placer") ? (SelectivePlacer)SelectivePlacer.gson.fromJson(JsonUtils.getJsonObject(jsonObject, "placer"), SelectivePlacer.class) : SelectivePlacer.surfacePlacer(0);
         return genInfo;
      }

      public JsonElement serialize(VanillaDecorationGeneration src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", src.id);
         if (src.generationWeight != null) {
            jsonObject.addProperty("generationWeight", src.generationWeight);
         }

         jsonObject.add("type", context.serialize(src.type));
         PresettedObjects.write(jsonObject, VanillaDecorationGeneration.gson, src.biomeWeights, "biomeWeightsPreset", "generationBiomes");
         PresettedObjects.write(jsonObject, VanillaDecorationGeneration.gson, src.dimensionWeights, "dimensionWeightsPreset", "generationDimensions");
         jsonObject.add("placer", SelectivePlacer.gson.toJsonTree(src.placer));
         return jsonObject;
      }
   }
}
