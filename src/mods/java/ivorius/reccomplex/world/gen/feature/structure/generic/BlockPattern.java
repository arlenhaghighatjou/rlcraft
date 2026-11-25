package ivorius.reccomplex.world.gen.feature.structure.generic;

import com.google.common.collect.Multimap;
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
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.math.Transforms;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.tools.GuavaCollectors;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.PositionedBlockExpression;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class BlockPattern implements NBTCompoundObject {
   public static final Gson gson = createGson();
   public final Selection pattern = new Selection(3);
   public final List<BlockPattern.Ingredient> ingredients = new ArrayList();

   public BlockPattern(List<Selection.Area> pattern, List<BlockPattern.Ingredient> ingredients) {
      this.pattern.addAll(pattern);
      this.ingredients.addAll(ingredients);
   }

   public BlockPattern() {
      this.pattern.add(new Selection.Area(true, new int[]{0, 0, 0}, new int[]{0, 0, 0}, "Sapling"));
      this.ingredients.add(new BlockPattern.Ingredient("Sapling", (PositionedBlockExpression)ExpressionCache.of(new PositionedBlockExpression(RecurrentComplex.specialRegistry), "block.id=minecraft:sapling"), true));
   }

   private static Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(BlockPattern.class, new BlockPattern.Serializer());
      builder.registerTypeAdapter(BlockPattern.Ingredient.class, new BlockPattern.IngredientSerializer(RecurrentComplex.specialRegistry));
      return builder.create();
   }

   public BlockPattern copy() {
      return new BlockPattern(this.pattern.copy(), this.ingredients);
   }

   public void transform(AxisAlignedTransform2D transform, int[] size) {
      this.pattern.transform(transform, size, 1);
   }

   @Nonnull
   public BlockPattern copy(AxisAlignedTransform2D transform, int[] size) {
      BlockPattern transformed = this.copy();
      transformed.transform(transform, size);
      return transformed;
   }

   public Optional<BlockPattern.Ingredient> findIngredient(String id) {
      return this.ingredients.stream().filter((i) -> {
         return i.identifier.equals(id);
      }).findFirst();
   }

   public boolean test(World world, BlockPos pos) {
      return this.pattern.compile(true).entrySet().stream().allMatch((entry) -> {
         return this.findIngredient((String)entry.getValue()).filter((i) -> {
            return ((Boolean)i.matcher.evaluate(() -> {
               return PositionedBlockExpression.Argument.at(world, pos.add(BlockPositions.fromIntArray(((MazeRoom)entry.getKey()).getCoordinates())));
            })).booleanValue();
         }).isPresent();
      });
   }

   public boolean canPlace(World world, BlockPos pos, int[] size, boolean rotate, boolean mirror) {
      return Transforms.transformStream(rotate, mirror).anyMatch((transform) -> {
         return this.copy(transform, size).testAll(world, pos).findAny().isPresent();
      });
   }

   public Multimap<AxisAlignedTransform2D, BlockPos> testAll(World world, BlockPos pos, int[] size, boolean rotate, boolean mirror) {
      return (Multimap)Transforms.transformStream(rotate, mirror).collect(GuavaCollectors.toMultimap((o) -> {
         return o;
      }, (transform) -> {
         Stream var10000 = this.copy(transform, size).testAll(world, pos);
         return var10000::iterator;
      }));
   }

   @Nonnull
   public Stream<BlockPos> testAll(World world, BlockPos pos) {
      return this.pattern.compile(true).keySet().stream().map((room) -> {
         return pos.subtract(BlockPositions.fromIntArray(room.getCoordinates()));
      }).filter((p) -> {
         return this.test(world, p);
      });
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.pattern.readFromNBT(compound.getCompoundTag("pattern"));
      this.ingredients.clear();
      this.ingredients.addAll(NBTCompoundObjects.readListFrom(compound, "ingredients", BlockPattern.Ingredient::new));
   }

   public void writeToNBT(NBTTagCompound compound) {
      NBTCompoundObjects.writeTo(compound, "pattern", this.pattern);
      NBTCompoundObjects.writeListTo(compound, "ingredients", this.ingredients);
   }

   public void forEach(Predicate<BlockPattern.Ingredient> filter, BiConsumer<BlockPos, String> consumer) {
      this.pattern.compile(true).entrySet().stream().filter((entry) -> {
         return (void)this.findIngredient((String)entry.getValue()).filter(filter).isPresent();
      }).map((entry) -> {
         return (void)Pair.of(BlockPositions.fromIntArray(((MazeRoom)entry.getKey()).getCoordinates()), entry.getValue());
      }).forEach((pair) -> {
         consumer.accept(pair.getKey(), pair.getValue());
      });
   }

   public static class Ingredient implements NBTCompoundObject {
      public String identifier;
      public PositionedBlockExpression matcher;
      public boolean delete;

      public Ingredient() {
         this.identifier = "";
         this.matcher = (PositionedBlockExpression)ExpressionCache.of(new PositionedBlockExpression(RecurrentComplex.specialRegistry), "");
         this.delete = true;
      }

      public Ingredient(String identifier, PositionedBlockExpression matcher, boolean delete) {
         this.identifier = identifier;
         this.matcher = matcher;
         this.delete = delete;
      }

      public void readFromNBT(NBTTagCompound compound) {
         this.identifier = compound.getString("identifier");
         this.matcher = (PositionedBlockExpression)ExpressionCache.of(new PositionedBlockExpression(RecurrentComplex.specialRegistry), compound.getString("blockExpression"));
         this.delete = compound.getBoolean("delete");
      }

      public void writeToNBT(NBTTagCompound compound) {
         compound.setString("identifier", this.identifier);
         compound.setString("blockExpression", this.matcher.getExpression());
         compound.setBoolean("delete", this.delete);
      }

      public BlockPattern.Ingredient copy() {
         return new BlockPattern.Ingredient(this.identifier, (PositionedBlockExpression)ExpressionCache.of(new PositionedBlockExpression(this.matcher.registry), this.matcher.getExpression()), this.delete);
      }
   }

   public static class IngredientSerializer implements JsonSerializer<BlockPattern.Ingredient>, JsonDeserializer<BlockPattern.Ingredient> {
      private MCRegistry registry;

      public IngredientSerializer(MCRegistry registry) {
         this.registry = registry;
      }

      public BlockPattern.Ingredient deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "blockPatternIngredient");
         String id = JsonUtils.getString(jsonObject, "id", "");
         String blockExpression = JsonUtils.getString(jsonObject, "blockExpression", "");
         boolean delete = JsonUtils.getBoolean(jsonObject, "delete", true);
         return new BlockPattern.Ingredient(id, (PositionedBlockExpression)ExpressionCache.of(new PositionedBlockExpression(this.registry), blockExpression), delete);
      }

      public JsonElement serialize(BlockPattern.Ingredient src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", src.identifier);
         jsonObject.addProperty("blockExpression", src.matcher.getExpression());
         jsonObject.addProperty("delete", Boolean.valueOf(src.delete));
         return jsonObject;
      }
   }

   public static class Serializer implements JsonSerializer<BlockPattern>, JsonDeserializer<BlockPattern> {
      public Serializer() {
      }

      public BlockPattern deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "blockPattern");
         List<Selection.Area> pattern = (List)context.deserialize(jsonObject.get("pattern"), (new TypeToken<List<Selection.Area>>() {
         }).getType());
         List<BlockPattern.Ingredient> ingredients = (List)BlockPattern.gson.fromJson(jsonObject.get("ingredients"), (new TypeToken<List<BlockPattern.Ingredient>>() {
         }).getType());
         return new BlockPattern(pattern, ingredients);
      }

      public JsonElement serialize(BlockPattern src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.add("pattern", BlockPattern.gson.toJsonTree(src.pattern));
         jsonObject.add("ingredients", BlockPattern.gson.toJsonTree(src.ingredients));
         return jsonObject;
      }
   }
}
