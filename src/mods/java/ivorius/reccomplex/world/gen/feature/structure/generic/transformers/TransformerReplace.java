package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import ivorius.ivtoolkit.blocks.BlockStates;
import ivorius.ivtoolkit.gui.IntegerRange;
import ivorius.ivtoolkit.random.WeightedSelector;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.editstructure.transformers.TableDataSourceBTReplace;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.BlockExpression;
import ivorius.reccomplex.utils.presets.PresettedList;
import ivorius.reccomplex.utils.presets.PresettedObjects;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.WeightedBlockState;
import ivorius.reccomplex.world.gen.feature.structure.generic.presets.WeightedBlockStatePresets;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TransformerReplace extends TransformerSingleBlock<TransformerReplace.InstanceData> {
   public final PresettedList<WeightedBlockState> destination;
   public BlockExpression sourceMatcher;
   public boolean uniformly;

   public TransformerReplace() {
      this((String)null, BlockExpression.of(RecurrentComplex.specialRegistry, Blocks.WOOL, new IntegerRange(0, 15)));
      this.destination.setPreset("air");
   }

   public TransformerReplace(@Nullable String id, String sourceExpression) {
      super(id != null ? id : randomID(TransformerReplace.class));
      this.destination = new PresettedList<WeightedBlockState>(WeightedBlockStatePresets.instance(), (String)null);
      this.sourceMatcher = (BlockExpression)ExpressionCache.of(new BlockExpression(RecurrentComplex.specialRegistry), sourceExpression);
   }

   public static void setBlock(StructureSpawnContext context, int[] areaSize, BlockPos pos, WeightedBlockState entry, Supplier<NBTTagCompound> tileEntity) {
      if (entry.state != null) {
         GenericStructure.setBlock(context, areaSize, pos, entry.state, tileEntity);
      }

   }

   public TransformerReplace replaceWith(WeightedBlockState... states) {
      this.destination.setContents(Arrays.asList(states));
      return this;
   }

   public boolean matches(Environment environment, TransformerReplace.InstanceData instanceData, BlockPos sourcePos, IBlockState state) {
      return this.sourceMatcher.test(state);
   }

   public void transformBlock(TransformerReplace.InstanceData instanceData, Transformer.Phase phase, StructureSpawnContext context, RunTransformer transformer, int[] areaSize, BlockPos coord, IBlockState sourceState) {
      if (this.uniformly) {
         setBlock(context, areaSize, coord, instanceData.blockState, () -> {
            return instanceData.tileEntityInfo;
         });
      } else {
         WeightedBlockState state = this.selectRandomBlockState(context.random);
         setBlock(context, areaSize, coord, state, () -> {
            return state.tileEntityInfo;
         });
      }

   }

   @SideOnly(Side.CLIENT)
   public String getDisplayString() {
      return "Replace: " + this.sourceMatcher.getDisplayString((Object)null);
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceBTReplace(this, navigator, delegate);
   }

   public TransformerReplace.InstanceData prepareInstanceData(StructurePrepareContext context, IvWorldData worldData) {
      WeightedBlockState blockState = this.selectRandomBlockState(context.random);
      return new TransformerReplace.InstanceData(blockState, blockState.tileEntityInfo != null ? blockState.tileEntityInfo.copy() : null);
   }

   public WeightedBlockState selectRandomBlockState(EaglercraftRandom random) {
      WeightedBlockState blockState;
      if (((ArrayList)this.destination.getContents()).size() > 0) {
         blockState = (WeightedBlockState)WeightedSelector.selectItem(random, (Collection)this.destination.getContents());
      } else {
         blockState = new WeightedBlockState((Double)null, Blocks.AIR.getDefaultState(), (NBTTagCompound)null);
      }

      return blockState;
   }

   public TransformerReplace.InstanceData loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      return new TransformerReplace.InstanceData(nbt instanceof NBTTagCompound ? (NBTTagCompound)nbt : new NBTTagCompound());
   }

   public boolean generatesInPhase(TransformerReplace.InstanceData instanceData, Transformer.Phase phase) {
      return phase == Transformer.Phase.BEFORE;
   }

   public static class InstanceData implements NBTStorable {
      public WeightedBlockState blockState;
      public NBTTagCompound tileEntityInfo;

      public InstanceData(WeightedBlockState blockState, NBTTagCompound tileEntityInfo) {
         this.blockState = blockState;
         this.tileEntityInfo = tileEntityInfo;
      }

      public InstanceData(NBTTagCompound compound) {
         this.blockState = new WeightedBlockState(RecurrentComplex.specialRegistry, compound.getCompoundTag("blockState"));
         this.tileEntityInfo = compound.hasKey("tileEntityInfo", 10) ? compound.getCompoundTag("tileEntityInfo").copy() : null;
      }

      public NBTBase writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setTag("blockState", this.blockState.writeToNBT(RecurrentComplex.specialRegistry));
         if (this.tileEntityInfo != null) {
            compound.setTag("tileEntityInfo", this.tileEntityInfo.copy());
         }

         return compound;
      }
   }

   public static class NonUniformSerializer implements JsonDeserializer<TransformerReplace> {
      protected TransformerReplace.Serializer serializer;

      public NonUniformSerializer(TransformerReplace.Serializer serializer) {
         this.serializer = serializer;
      }

      public TransformerReplace deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         TransformerReplace transformer = this.serializer.deserialize(jsonElement, par2Type, context);
         transformer.uniformly = false;
         return transformer;
      }
   }

   public static class Serializer implements JsonDeserializer<TransformerReplace>, JsonSerializer<TransformerReplace> {
      private MCRegistry registry;
      private Gson gson;

      public Serializer(MCRegistry registry) {
         this.registry = registry;
         this.gson = (new GsonBuilder()).registerTypeAdapter(WeightedBlockState.class, new WeightedBlockState.Serializer(registry)).create();
      }

      public static String readLegacyMatcher(JsonObject jsonObject, String blockKey, String metadataKey) {
         if (jsonObject.has(blockKey)) {
            String sourceBlock = JsonUtils.getString(jsonObject, blockKey);
            int sourceMeta = JsonUtils.getInt(jsonObject, metadataKey, -1);
            return sourceMeta >= 0 ? String.format("%s & %s%d", sourceBlock, "metadata=", sourceMeta) : sourceBlock;
         } else {
            return null;
         }
      }

      public TransformerReplace deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "transformerReplace");
         String id = Transformer.readID(jsonObject);
         String expression = readLegacyMatcher(jsonObject, "source", "sourceMetadata");
         if (expression == null) {
            expression = JsonUtils.getString(jsonObject, "sourceExpression", "");
         }

         TransformerReplace transformer = new TransformerReplace(id, expression);
         transformer.uniformly = JsonUtils.getBoolean(jsonObject, "uniform", true);
         PresettedObjects.read(jsonObject, this.gson, transformer.destination, "destinationPreset", "destination", (new TypeToken<ArrayList<WeightedBlockState>>() {
         }).getType());
         if (jsonObject.has("dest")) {
            String destBlock = JsonUtils.getString(jsonObject, "dest");
            Block dest = this.registry.blockFromID(new ResourceLocation(destBlock));
            byte[] destMeta = (byte[])context.deserialize(jsonObject.get("destMetadata"), byte[].class);
            transformer.destination.setToCustom();

            for(byte b : destMeta) {
               ((ArrayList)transformer.destination.getContents()).add(new WeightedBlockState((Double)null, BlockStates.fromMetadata(dest, b), (NBTTagCompound)null));
            }
         }

         return transformer;
      }

      public JsonElement serialize(TransformerReplace transformer, Type par2Type, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("uniform", Boolean.valueOf(transformer.uniformly));
         jsonObject.addProperty("id", transformer.id());
         jsonObject.addProperty("sourceExpression", transformer.sourceMatcher.getExpression());
         PresettedObjects.write(jsonObject, this.gson, transformer.destination, "destinationPreset", "destination");
         return jsonObject;
      }
   }
}
