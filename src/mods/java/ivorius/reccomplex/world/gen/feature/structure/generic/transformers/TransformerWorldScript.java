package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.editstructure.transformers.TableDataSourceBTWorldScript;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.json.NBTToJson;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.BlockExpression;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedBlockState;
import ivorius.reccomplex.world.gen.script.WorldScriptMulti;
import java.lang.reflect.Type;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TransformerWorldScript extends TransformerSingleBlock<TransformerWorldScript.InstanceData> {
   public WorldScriptMulti script;
   public BlockExpression sourceMatcher;

   public TransformerWorldScript() {
      this((String)null, new WorldScriptMulti(), BlockExpression.of(RecurrentComplex.specialRegistry, Blocks.WOOL));
   }

   public TransformerWorldScript(@Nullable String id, WorldScriptMulti script, String sourceExpression) {
      super(id != null ? id : randomID(TransformerWorldScript.class));
      this.script = script;
      this.sourceMatcher = (BlockExpression)ExpressionCache.of(new BlockExpression(RecurrentComplex.specialRegistry), sourceExpression);
   }

   public boolean matches(Environment environment, TransformerWorldScript.InstanceData instanceData, BlockPos sourcePos, IBlockState state) {
      return this.sourceMatcher.test(state);
   }

   public void transformBlock(TransformerWorldScript.InstanceData instanceData, Transformer.Phase phase, StructureSpawnContext context, RunTransformer transformer, int[] areaSize, BlockPos coord, IBlockState sourceState) {
      StructureGenerator<NBTStorable> generator = (new StructureGenerator<NBTStorable>()).asChild(context).maturity(StructureSpawnContext.GenerateMaturity.FIRST);
      WorldScriptMulti.InstanceData scriptInstanceData = this.script.prepareInstanceData((StructurePrepareContext)generator.prepare().get(), coord);
      this.script.generate((StructureSpawnContext)generator.spawn().get(), transformer, scriptInstanceData, coord);
   }

   public TransformerWorldScript.InstanceData prepareInstanceData(StructurePrepareContext context, IvWorldData worldData) {
      return new TransformerWorldScript.InstanceData();
   }

   public TransformerWorldScript.InstanceData loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      TransformerWorldScript.InstanceData instanceData = new TransformerWorldScript.InstanceData();
      instanceData.readFromNBT(this, context, nbt);
      return instanceData;
   }

   @SideOnly(Side.CLIENT)
   public String getDisplayString() {
      return this.script.getDisplayString();
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceBTWorldScript(this, navigator, delegate);
   }

   public boolean generatesInPhase(TransformerWorldScript.InstanceData instanceData, Transformer.Phase phase) {
      return phase == Transformer.Phase.BEFORE;
   }

   public static class InstanceData implements NBTStorable {
      public InstanceData() {
      }

      public NBTBase writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         return compound;
      }

      public void readFromNBT(TransformerWorldScript transformer, StructureLoadContext context, NBTBase nbt) {
      }
   }

   public static class Serializer implements JsonDeserializer<TransformerWorldScript>, JsonSerializer<TransformerWorldScript> {
      private MCRegistry registry;
      private Gson gson;

      public Serializer(MCRegistry registry) {
         this.registry = registry;
         GsonBuilder builder = new GsonBuilder();
         builder.registerTypeAdapter(WeightedBlockState.class, new WeightedBlockState.Serializer(registry));
         NBTToJson.registerSafeNBTSerializer(builder);
         this.gson = builder.create();
      }

      public TransformerWorldScript deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "transformerReplace");
         String id = Transformer.readID(jsonObject);
         String expression = JsonUtils.getString(jsonObject, "sourceExpression", "");
         WorldScriptMulti script = (WorldScriptMulti)NBTCompoundObjects.read((NBTTagCompound)this.gson.fromJson(JsonUtils.getJsonObject(jsonObject, "script", new JsonObject()), NBTTagCompound.class), WorldScriptMulti::new);
         return new TransformerWorldScript(id, script, expression);
      }

      public JsonElement serialize(TransformerWorldScript transformer, Type par2Type, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", transformer.id());
         jsonObject.addProperty("sourceExpression", transformer.sourceMatcher.getExpression());
         jsonObject.add("script", this.gson.toJsonTree(NBTCompoundObjects.write(transformer.script)));
         return jsonObject;
      }
   }
}
