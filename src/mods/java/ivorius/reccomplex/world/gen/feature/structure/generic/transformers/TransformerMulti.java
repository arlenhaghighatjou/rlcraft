package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.NBTTagLists;
import ivorius.reccomplex.gui.editstructure.transformers.TableDataSourceBTMulti;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.expression.EnvironmentExpression;
import ivorius.reccomplex.utils.presets.PresettedObject;
import ivorius.reccomplex.utils.presets.PresettedObjects;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLiveContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.presets.TransfomerPresets;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public class TransformerMulti extends Transformer<TransformerMulti.InstanceData> {
   public static Gson gson = createGson();
   protected final PresettedObject<TransformerMulti.Data> data;

   public TransformerMulti() {
      this((String)null, "");
   }

   public TransformerMulti(@Nullable String id, String expression) {
      this(id, expression, Collections.emptyList());
   }

   public TransformerMulti(@Nullable String id, String expression, Collection<Transformer> transformers) {
      super(id != null ? id : randomID(TransformerMulti.class));
      this.data = new PresettedObject<TransformerMulti.Data>(TransfomerPresets.instance(), (String)null);
      this.data.setContents(new TransformerMulti.Data());
      (this.data.getContents()).environmentExpression.setExpression(expression);
      (this.data.getContents()).transformers.addAll(transformers);
   }

   private static Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(TransformerMulti.Data.class, new TransformerMulti.DataSerializer());
      StructureRegistry.TRANSFORMERS.constructGson(builder);
      return builder.create();
   }

   public static TransformerMulti fromPreset(String preset) {
      TransformerMulti transformer = new TransformerMulti();
      transformer.setID("preset_" + preset);
      transformer.data.setPreset(preset);
      return transformer;
   }

   public static TransformerMulti fuse(List<Transformer> transformers) {
      TransformerMulti transformer = new TransformerMulti("fused", "");
      transformers.forEach((t) -> {
         transformer.getTransformers().add(t);
      });
      return transformer;
   }

   public boolean isEmpty(TransformerMulti.InstanceData instanceData) {
      return instanceData.pairedTransformers.isEmpty();
   }

   public TransformerMulti.InstanceData fuseDatas(List<NBTStorable> instanceDatas) {
      List<Transformer> transformers = this.getTransformers();
      TransformerMulti.InstanceData instanceData = new TransformerMulti.InstanceData();

      for(int i = 0; i < instanceDatas.size(); ++i) {
         instanceData.pairedTransformers.add(Pair.of(transformers.get(i), instanceDatas.get(i)));
      }

      return instanceData;
   }

   public List<Transformer> getTransformers() {
      return (this.data.getContents()).transformers;
   }

   public EnvironmentExpression getEnvironmentMatcher() {
      return (this.data.getContents()).environmentExpression;
   }

   public PresettedObject<TransformerMulti.Data> getData() {
      return this.data;
   }

   @SideOnly(Side.CLIENT)
   public String getDisplayString() {
      if (this.data.getPreset() != null) {
         return (String)this.data.getPresetRegistry().metadata(this.data.getPreset()).map((m) -> {
            return TextFormatting.GREEN + m.title;
         }).orElse(TextFormatting.GOLD + this.data.getPreset());
      } else {
         int amount = this.getTransformers().size();
         return amount == 0 ? IvTranslations.get("reccomplex.transformer.multi.none") : IvTranslations.format("reccomplex.transformer.multi.multiple", new Object[]{amount});
      }
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceBTMulti(this, navigator, delegate);
   }

   public TransformerMulti.InstanceData prepareInstanceData(StructurePrepareContext context, IvWorldData worldData) {
      TransformerMulti.InstanceData instanceData = new TransformerMulti.InstanceData();
      instanceData.deactivated = !this.getEnvironmentMatcher().test(context.environment);
      if (!instanceData.deactivated) {
         this.getTransformers().forEach((t) -> {
            instanceData.pairedTransformers.add(Pair.of(t, t.prepareInstanceData(context, worldData)));
         });
      }

      return instanceData;
   }

   public void configureInstanceData(TransformerMulti.InstanceData instanceData, StructurePrepareContext context, IvWorldData worldData, RunTransformer transformer) {
      if (!instanceData.deactivated) {
         instanceData.pairedTransformers.forEach((pair) -> {
            ((Transformer)pair.getLeft()).configureInstanceData((NBTStorable)pair.getRight(), context, worldData, transformer);
         });
      }

   }

   public TransformerMulti.InstanceData loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      TransformerMulti.InstanceData instanceData = new TransformerMulti.InstanceData();
      instanceData.readFromNBT(context, nbt, this.getTransformers());
      return instanceData;
   }

   public boolean mayGenerate(TransformerMulti.InstanceData instanceData, StructurePrepareContext context, IvWorldData worldData) {
      return instanceData.deactivated || instanceData.pairedTransformers.stream().allMatch((input) -> {
         return ((Transformer)input.getLeft()).mayGenerate((NBTStorable)input.getRight(), context, worldData);
      });
   }

   public boolean skipGeneration(TransformerMulti.InstanceData instanceData, StructureLiveContext context, BlockPos pos, IBlockState state, IvWorldData worldData, BlockPos sourcePos) {
      return !instanceData.deactivated && instanceData.pairedTransformers.stream().anyMatch((input) -> {
         return ((Transformer)input.getLeft()).skipGeneration((NBTStorable)input.getRight(), context, pos, state, worldData, sourcePos);
      });
   }

   public void transform(TransformerMulti.InstanceData instanceData, Transformer.Phase phase, StructureSpawnContext context, IvWorldData worldData, RunTransformer transformer) {
      if (!instanceData.deactivated) {
         instanceData.pairedTransformers.forEach((pair) -> {
            ((Transformer)pair.getLeft()).transform((NBTStorable)pair.getRight(), phase, context, worldData, transformer);
         });
      }

   }

   public static class Data {
      public final List<Transformer> transformers = new ArrayList();
      public final EnvironmentExpression environmentExpression = new EnvironmentExpression();

      public Data() {
      }
   }

   public static class DataSerializer implements JsonDeserializer<TransformerMulti.Data>, JsonSerializer<TransformerMulti.Data> {
      public DataSerializer() {
      }

      public TransformerMulti.Data deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "transformerMultiData");
         TransformerMulti.Data data = new TransformerMulti.Data();
         data.environmentExpression.setExpression(JsonUtils.getString(jsonObject, "environmentMatcher", ""));
         Transformer.idRandomizers.push(new EaglercraftRandom(-559038737L));
         Transformer[] transformers = (Transformer[])TransformerMulti.gson.fromJson(jsonObject.get("transformers"), Transformer[].class);
         if (transformers != null) {
            Collections.addAll(data.transformers, transformers);
         }

         Transformer.idRandomizers.pop();
         return data;
      }

      public JsonElement serialize(TransformerMulti.Data src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("environmentMatcher", src.environmentExpression.getExpression());
         jsonObject.add("transformers", TransformerMulti.gson.toJsonTree(src.transformers));
         return jsonObject;
      }
   }

   public static class InstanceData implements NBTStorable {
      public static final String KEY_TRANSFORMERS = "transformers";
      public final List<Pair<Transformer, NBTStorable>> pairedTransformers = new ArrayList();
      public boolean deactivated;

      public InstanceData() {
      }

      public void readFromNBT(StructureLoadContext context, NBTBase nbt, List<Transformer> transformers) {
         NBTTagCompound compound = nbt instanceof NBTTagCompound ? (NBTTagCompound)nbt : new NBTTagCompound();
         NBTTagLists.compoundsFrom(compound, "transformers").forEach((transformerCompound) -> {
            String transformerID = transformerCompound.getString("id");
            transformers.stream().filter((t) -> {
               return (void)t.id().equals(transformerID);
            }).findAny().ifPresent((transformer) -> {
               this.pairedTransformers.add(Pair.of(transformer, transformer.loadInstanceData(context, transformerCompound.getTag("data"))));
            });
         });
         this.deactivated = compound.getBoolean("deactivated");
      }

      public NBTBase writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         NBTTagLists.writeTo(compound, "transformers", (List)this.pairedTransformers.stream().map((pair) -> {
            NBTTagCompound transformerCompound = new NBTTagCompound();
            transformerCompound.setTag("data", ((NBTStorable)pair.getRight()).writeToNBT());
            transformerCompound.setString("id", ((Transformer)pair.getLeft()).id());
            return transformerCompound;
         }).collect(Collectors.toList()));
         compound.setBoolean("deactivated", this.deactivated);
         return compound;
      }
   }

   public static class Serializer implements JsonDeserializer<TransformerMulti>, JsonSerializer<TransformerMulti> {
      public Serializer() {
      }

      public TransformerMulti deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "transformerMulti");
         TransformerMulti transformer = new TransformerMulti();
         transformer.setID(Transformer.readID(jsonObject));
         if (!PresettedObjects.read(jsonObject, TransformerMulti.gson, transformer.data, "dataPreset", "data", TransformerMulti.Data.class) && jsonObject.has("environmentMatcher") && jsonObject.has("transformers")) {
            transformer.getData().setContents(TransformerMulti.gson.fromJson(jsonObject, TransformerMulti.Data.class));
         }

         return transformer;
      }

      public JsonElement serialize(TransformerMulti src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", src.id());
         PresettedObjects.write(jsonObject, TransformerMulti.gson, src.data, "dataPreset", "data");
         return jsonObject;
      }
   }
}
