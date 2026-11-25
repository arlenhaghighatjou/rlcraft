package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.editstructure.transformers.TableDataSourceBTProperty;
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
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TransformerProperty extends TransformerSingleBlock<NBTNone> {
   public BlockExpression sourceMatcher;
   public String propertyName;
   public String propertyValue;

   public TransformerProperty() {
      this((String)null, "", "", "");
   }

   public TransformerProperty(@Nullable String id, String sourceExpression, String propertyName, String propertyValue) {
      super(id != null ? id : randomID(TransformerProperty.class));
      this.propertyName = "";
      this.propertyValue = "";
      this.sourceMatcher = (BlockExpression)ExpressionCache.of(new BlockExpression(RecurrentComplex.specialRegistry), sourceExpression);
      this.propertyName = propertyName;
      this.propertyValue = propertyValue;
   }

   @Nonnull
   public static <T extends Comparable<T>> Optional<IBlockState> withProperty(IBlockState state, String propertyName, String propertyValue) {
      Optional<IProperty<T>> mProperty = state.getProperties().keySet().stream().filter((p) -> {
         return p.getName().equals(propertyName);
      }).findFirst();
      if (mProperty.isPresent()) {
         IProperty<T> property = (IProperty)mProperty.get();
         return property.getAllowedValues().stream().filter((v1) -> {
            return property.getName(v1).equals(propertyValue);
         }).findAny().map((v) -> {
            return state.withProperty(property, v);
         });
      } else {
         return Optional.empty();
      }
   }

   public static Stream<String> propertyValueStream(String propertyName) {
      Stream var10000 = Block.REGISTRY.getKeys().stream();
      RegistryNamespacedDefaultedByKey var10001 = Block.REGISTRY;
      Block.REGISTRY.getClass();
      return var10000.map(var10001::getObject).map((b) -> {
         return (IProperty)b.getDefaultState().getPropertyKeys().stream().filter((p) -> {
            return p.getName().equals(propertyName);
         }).findFirst().orElse((Object)null);
      }).filter(Objects::nonNull).flatMap((p) -> {
         Stream var10000 = p.getAllowedValues().stream();
         p.getClass();
         return var10000.map(p::getName);
      });
   }

   public static Stream<String> propertyNameStream() {
      Stream var10000 = Block.REGISTRY.getKeys().stream();
      RegistryNamespacedDefaultedByKey var10001 = Block.REGISTRY;
      Block.REGISTRY.getClass();
      return var10000.map(var10001::getObject).flatMap((b) -> {
         return b.getDefaultState().getPropertyKeys().stream().map(IProperty::getName);
      });
   }

   public boolean matches(Environment environment, NBTNone instanceData, BlockPos sourcePos, IBlockState state) {
      return this.sourceMatcher.test(state) && withProperty(state, this.propertyName, this.propertyValue).isPresent();
   }

   public void transformBlock(NBTNone instanceData, Transformer.Phase phase, StructureSpawnContext context, RunTransformer transformer, int[] areaSize, BlockPos coord, IBlockState sourceState) {
      withProperty(sourceState, this.propertyName, this.propertyValue).ifPresent((state) -> {
         context.setBlock(coord, state, 2);
      });
   }

   public NBTNone prepareInstanceData(StructurePrepareContext context, IvWorldData worldData) {
      return new NBTNone();
   }

   public NBTNone loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      return new NBTNone();
   }

   @SideOnly(Side.CLIENT)
   public String getDisplayString() {
      return TextFormatting.GREEN + this.propertyName + TextFormatting.RESET + " -> " + TextFormatting.GREEN + this.propertyValue + TextFormatting.RESET;
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceBTProperty(this, navigator, delegate);
   }

   public boolean generatesInPhase(NBTNone instanceData, Transformer.Phase phase) {
      return phase == Transformer.Phase.BEFORE;
   }

   public static class Serializer implements JsonDeserializer<TransformerProperty>, JsonSerializer<TransformerProperty> {
      private MCRegistry registry;

      public Serializer(MCRegistry registry) {
         this.registry = registry;
      }

      public TransformerProperty deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "transformerPropertyReplace");
         String id = Transformer.readID(jsonObject);
         String expression = JsonUtils.getString(jsonObject, "sourceExpression", "");
         String propertyName = JsonUtils.getString(jsonObject, "propertyName", "");
         String propertyValue = JsonUtils.getString(jsonObject, "propertyValue", "");
         return new TransformerProperty(id, expression, propertyName, propertyValue);
      }

      public JsonElement serialize(TransformerProperty transformer, Type par2Type, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", transformer.id());
         jsonObject.addProperty("sourceExpression", transformer.sourceMatcher.getExpression());
         jsonObject.addProperty("propertyName", transformer.propertyName);
         jsonObject.addProperty("propertyValue", transformer.propertyValue);
         return jsonObject;
      }
   }
}
