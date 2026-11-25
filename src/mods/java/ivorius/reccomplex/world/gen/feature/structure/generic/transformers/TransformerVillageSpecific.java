package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.ivtoolkit.transform.PosTransformer;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.editstructure.transformers.TableDataSourceBTBiomeSpecific;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.nbt.NBTNone;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.BlockExpression;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedBlockState;
import java.lang.reflect.Type;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TransformerVillageSpecific extends TransformerSingleBlock<NBTNone> {
   public final TransformerVillageSpecific.MyVillage village;
   public BlockExpression sourceMatcher;

   public TransformerVillageSpecific() {
      this((String)null, "");
   }

   public TransformerVillageSpecific(@Nullable String id, String sourceExpression) {
      super(id != null ? id : randomID(TransformerVillageSpecific.class));
      this.village = new TransformerVillageSpecific.MyVillage();
      this.sourceMatcher = (BlockExpression)ExpressionCache.of(new BlockExpression(RecurrentComplex.specialRegistry), sourceExpression);
   }

   public boolean matches(Environment environment, NBTNone instanceData, BlockPos sourcePos, IBlockState state) {
      return this.village.trySetType(environment.villageType) && this.sourceMatcher.test(state) && this.village.getBiomeSpecificBlockState(state) != state;
   }

   public void transformBlock(NBTNone instanceData, Transformer.Phase phase, StructureSpawnContext context, RunTransformer transformer, int[] areaSize, BlockPos coord, IBlockState sourceState) {
      if (this.village.trySetType(context.environment.villageType)) {
         IBlockState state = this.village.getBiomeSpecificBlockState(sourceState);
         context.setBlock(coord, PosTransformer.transformBlockState(state, context.transform), 2);
      }

   }

   public NBTNone prepareInstanceData(StructurePrepareContext context, IvWorldData worldData) {
      return new NBTNone();
   }

   public NBTNone loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      return new NBTNone();
   }

   @SideOnly(Side.CLIENT)
   public String getDisplayString() {
      return IvTranslations.get("reccomplex.transformer.villagereplace");
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceBTBiomeSpecific(this, navigator, delegate);
   }

   public boolean generatesInPhase(NBTNone instanceData, Transformer.Phase phase) {
      return phase == Transformer.Phase.BEFORE;
   }

   private static class MyVillage extends Village {
      private MyVillage() {
      }

      public boolean trySetType(@Nullable Integer type) {
         if (type != null) {
            this.structureType = type.intValue();
         }

         return type != null;
      }

      public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
         return false;
      }

      public IBlockState getBiomeSpecificBlockState(IBlockState blockstateIn) {
         return super.getBiomeSpecificBlockState(blockstateIn);
      }
   }

   public static class Serializer implements JsonDeserializer<TransformerVillageSpecific>, JsonSerializer<TransformerVillageSpecific> {
      private MCRegistry registry;
      private Gson gson;

      public Serializer(MCRegistry registry) {
         this.registry = registry;
         this.gson = (new GsonBuilder()).registerTypeAdapter(WeightedBlockState.class, new WeightedBlockState.Serializer(registry)).create();
      }

      public TransformerVillageSpecific deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "transformerReplace");
         String id = Transformer.readID(jsonObject);
         String expression = JsonUtils.getString(jsonObject, "sourceExpression", "");
         return new TransformerVillageSpecific(id, expression);
      }

      public JsonElement serialize(TransformerVillageSpecific transformer, Type par2Type, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", transformer.id());
         jsonObject.addProperty("sourceExpression", transformer.sourceMatcher.getExpression());
         return jsonObject;
      }
   }
}
