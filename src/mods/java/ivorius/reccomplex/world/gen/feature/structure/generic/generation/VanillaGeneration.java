package ivorius.reccomplex.world.gen.feature.structure.generic.generation;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.blocks.Directions;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.files.SimpleLeveledRegistry;
import ivorius.reccomplex.gui.editstructure.gentypes.TableDataSourceVanillaGeneration;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.BiomeExpression;
import ivorius.reccomplex.world.gen.feature.structure.Placer;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.SelectivePlacer;
import ivorius.reccomplex.world.gen.feature.villages.GenericVillageCreationHandler;
import ivorius.reccomplex.world.gen.feature.villages.GenericVillagePiece;
import ivorius.reccomplex.world.gen.feature.villages.TemporaryVillagerRegistry;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public class VanillaGeneration extends GenerationType {
   public Double generationWeight;
   public double minBaseLimit;
   public double maxBaseLimit;
   public double minScaledLimit;
   public double maxScaledLimit;
   @Nullable
   public EnumFacing front;
   public BlockPos spawnShift;
   public BiomeExpression biomeExpression;

   public VanillaGeneration() {
      this((String)null, (Double)null, 0.0D, 2.0D, 1.0D, 1.0D, EnumFacing.NORTH, BlockPos.ORIGIN, "");
   }

   public VanillaGeneration(@Nullable String id, Double generationWeight, double minBaseLimit, double maxBaseLimit, double minScaledLimit, double maxScaledLimit, EnumFacing front, BlockPos spawnShift, String biomeExpression) {
      super(id != null ? id : randomID(VanillaGeneration.class));
      this.generationWeight = generationWeight;
      this.minBaseLimit = minBaseLimit;
      this.maxBaseLimit = maxBaseLimit;
      this.minScaledLimit = minScaledLimit;
      this.maxScaledLimit = maxScaledLimit;
      this.front = front;
      this.spawnShift = spawnShift;
      this.biomeExpression = (BiomeExpression)ExpressionCache.of(new BiomeExpression(), biomeExpression);
   }

   @Nonnull
   public String id() {
      return this.id;
   }

   public void setID(@Nonnull String id) {
      this.id = id;
   }

   public void setSpawnShift(BlockPos spawnShift) {
      this.spawnShift = spawnShift;
   }

   public String displayString() {
      return IvTranslations.get("reccomplex.generationInfo.vanilla.title");
   }

   @Nullable
   public Placer placer() {
      return SelectivePlacer.surfacePlacer(0);
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(MazeVisualizationContext mazeVisualizationContext, TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceVanillaGeneration(navigator, delegate, this);
   }

   public boolean generatesIn(Biome biome) {
      return this.biomeExpression.test(biome);
   }

   public double getActiveWeight() {
      return this.generationWeight != null ? this.generationWeight.doubleValue() : 1.0D;
   }

   public int getVanillaWeight(double tweak) {
      return MathHelper.floor(this.getActiveWeight() * tweak * (double)RCConfig.baseVillageSpawnWeight + 0.5D);
   }

   public static class Cache extends SimpleLeveledRegistry.Module<StructureRegistry> {
      public Cache() {
      }

      public void invalidate() {
         this.updateVanillaGenerations();

         for(Pair<Structure<?>, VanillaGeneration> pair : ((StructureRegistry)this.registry).getGenerationTypes(VanillaGeneration.class)) {
            String structureID = ((StructureRegistry)this.registry).id(pair.getLeft());
            String generationID = ((VanillaGeneration)pair.getRight()).id();
            Class<? extends GenericVillagePiece> clazz = GenericVillageCreationHandler.getPieceClass(structureID, generationID);
            if (clazz != null) {
               MapGenStructureIO.registerStructureComponent(clazz, "Rc:" + structureID + "_" + generationID);
            }
         }

      }

      private void updateVanillaGenerations() {
         TemporaryVillagerRegistry.instance().setHandlers(Sets.newHashSet((Iterable)Collections2.transform(((StructureRegistry)this.registry).getGenerationTypes(VanillaGeneration.class), (input) -> {
            return GenericVillageCreationHandler.forGeneration(((StructureRegistry)this.registry).id(input.getLeft()), ((VanillaGeneration)input.getRight()).id());
         }).stream().filter(Objects::nonNull).collect(Collectors.toList())));
      }
   }

   public static class Serializer implements JsonSerializer<VanillaGeneration>, JsonDeserializer<VanillaGeneration> {
      public Serializer() {
      }

      public VanillaGeneration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "vanillaStructureSpawnInfo");
         String id = GenerationType.readID(jsonObject);
         Double spawnWeight = jsonObject.has("generationWeight") ? JsonUtils.getDouble(jsonObject, "generationWeight") : null;
         double minBaseLimit = JsonUtils.getDouble(jsonObject, "minBaseLimit", 0.0D);
         double maxBaseLimit = JsonUtils.getDouble(jsonObject, "maxBaseLimit", 0.0D);
         double minScaledLimit = JsonUtils.getDouble(jsonObject, "minScaledLimit", 0.0D);
         double maxScaledLimit = JsonUtils.getDouble(jsonObject, "maxScaledLimit", 0.0D);
         int spawnX = JsonUtils.getInt(jsonObject, "spawnShiftX", 0);
         int spawnY = JsonUtils.getInt(jsonObject, "spawnShiftY", 0);
         int spawnZ = JsonUtils.getInt(jsonObject, "spawnShiftZ", 0);
         EnumFacing front = jsonObject.has("front") ? Directions.deserialize(JsonUtils.getString(jsonObject, "front")) : null;
         String biomeExpression = JsonUtils.getString(jsonObject, "biomeExpression", "");
         return new VanillaGeneration(id, spawnWeight, minBaseLimit, maxBaseLimit, minScaledLimit, maxScaledLimit, front, new BlockPos(spawnX, spawnY, spawnZ), biomeExpression);
      }

      public JsonElement serialize(VanillaGeneration src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", src.id);
         if (src.generationWeight != null) {
            jsonObject.addProperty("generationWeight", src.generationWeight);
         }

         jsonObject.addProperty("minBaseLimit", Double.valueOf(src.minBaseLimit));
         jsonObject.addProperty("maxBaseLimit", Double.valueOf(src.maxBaseLimit));
         jsonObject.addProperty("minScaledLimit", Double.valueOf(src.minScaledLimit));
         jsonObject.addProperty("maxScaledLimit", Double.valueOf(src.maxScaledLimit));
         jsonObject.addProperty("spawnShiftX", Integer.valueOf(src.spawnShift.getX()));
         jsonObject.addProperty("spawnShiftY", Integer.valueOf(src.spawnShift.getY()));
         jsonObject.addProperty("spawnShiftZ", Integer.valueOf(src.spawnShift.getZ()));
         if (src.front != null) {
            jsonObject.addProperty("front", Directions.serialize(src.front));
         }

         jsonObject.addProperty("biomeExpression", src.biomeExpression.getExpression());
         return jsonObject;
      }
   }
}
