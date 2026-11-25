package ivorius.reccomplex.world.storage.loot;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.random.WeightedSelector;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.json.ItemStackSerializer;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.json.NBTToJson;
import ivorius.reccomplex.utils.expression.DependencyExpression;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandom.Item;
import net.minecraft.world.WorldServer;

public class GenericLootTable implements LootTable {
   public static final int LATEST_VERSION = 2;
   private static Gson gson = createGson();
   public final List<GenericLootTable.Component> components = new ArrayList();

   public GenericLootTable() {
   }

   public static Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(GenericLootTable.Component.class, new GenericLootTable.Component.Serializer());
      builder.registerTypeAdapter(GenericLootTable.RandomizedItemStack.class, new GenericLootTable.RandomizedItemStack.Serializer());
      builder.registerTypeAdapter(ItemStack.class, new ItemStackSerializer(RecurrentComplex.specialRegistry));
      builder.registerTypeAdapter(WeightedRandomChestContent.class, new WeightedRandomChestContentSerializer());
      NBTToJson.registerSafeNBTSerializer(builder);
      return builder.create();
   }

   public ItemStack getRandomItemStack(WorldServer server, EaglercraftRandom random) {
      int max = 0;

      for(GenericLootTable.Component component : this.components) {
         component.updateWeightCache();
         max += component.itemWeight;
      }

      return max > 0 ? ((GenericLootTable.Component)WeightedRandom.getRandomItem(random, this.components, max)).getRandomItemStack(random) : ItemStack.EMPTY;
   }

   public String getDescriptor() {
      return IvTranslations.get("inventoryGen.custom");
   }

   public static class Component extends Item {
      public final List<GenericLootTable.RandomizedItemStack> items = new ArrayList();
      public final DependencyExpression dependencies = new DependencyExpression();
      public String tableID;

      public Component() {
         super(0);
         this.tableID = "";
      }

      public Component(String tableID, List<GenericLootTable.RandomizedItemStack> items, String dependencies) {
         super(0);
         this.tableID = tableID;
         this.items.addAll(items);
         this.dependencies.setExpression(dependencies);
      }

      public static GenericLootTable.Component createDefaultComponent() {
         return new GenericLootTable.Component();
      }

      public ItemStack getRandomItemStack(EaglercraftRandom random) {
         if (this.items.size() == 0) {
            return null;
         } else {
            GenericLootTable.RandomizedItemStack item = (GenericLootTable.RandomizedItemStack)WeightedSelector.selectItem(random, this.items);
            return item.generateStack(random);
         }
      }

      public boolean areDependenciesResolved() {
         return this.dependencies.test(RecurrentComplex.saver);
      }

      public void updateWeightCache() {
         this.itemWeight = this.items.size();
      }

      public GenericLootTable.Component copy() {
         try {
            return ItemCollectionSaveHandler.INSTANCE.fromJSON(ItemCollectionSaveHandler.INSTANCE.toJSON(this));
         } catch (InventoryLoadException var2) {
            var2.printStackTrace();
            return null;
         }
      }

      public static class Serializer implements JsonDeserializer<GenericLootTable.Component>, JsonSerializer<GenericLootTable.Component> {
         public Serializer() {
         }

         public GenericLootTable.Component deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
            JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "status");
            int version;
            if (jsonObject.has("version")) {
               version = JsonUtils.getInt(jsonObject, "version");
            } else {
               version = 2;
               RecurrentComplex.logger.warn("InventoryGen JSON missing 'version', using latest (2)");
            }

            String generatorID = JsonUtils.getString(jsonObject, "inventoryGeneratorID", "");
            List<GenericLootTable.RandomizedItemStack> stacks = new ArrayList();
            if (jsonObject.has("items")) {
               stacks.addAll(Lists.newArrayList(GenericLootTable.gson.fromJson(jsonObject.get("items"), GenericLootTable.RandomizedItemStack[].class)));
            }

            if (version == 1 && jsonObject.has("contents")) {
               List<WeightedRandomChestContent> chestContents = Lists.newArrayList(GenericLootTable.gson.fromJson(jsonObject.get("contents"), WeightedRandomChestContent[].class));
               stacks.addAll(Collections2.transform(chestContents, (input) -> {
                  return GenericLootTable.RandomizedItemStack.from(input, 100);
               }));
            }

            String dependencyExpression = JsonUtils.getString(jsonObject, "dependencyExpression", "");
            if (version == 1 && jsonObject.has("dependencies")) {
               String[] dependencies = (String[])context.deserialize(jsonObject.get("dependencies"), String[].class);
               dependencyExpression = DependencyExpression.ofMods(dependencies);
            }

            return new GenericLootTable.Component(generatorID, stacks, dependencyExpression);
         }

         public JsonElement serialize(GenericLootTable.Component src, Type par2Type, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("version", Integer.valueOf(2));
            jsonObject.addProperty("inventoryGeneratorID", src.tableID);
            jsonObject.add("items", GenericLootTable.gson.toJsonTree(src.items));
            jsonObject.add("dependencyExpression", context.serialize(src.dependencies.getExpression()));
            return jsonObject;
         }
      }
   }

   public static class RandomizedItemStack implements ivorius.ivtoolkit.random.WeightedSelector.Item {
      public ItemStack itemStack;
      public int min;
      public int max;
      public double weight;

      public RandomizedItemStack(ItemStack itemStack, int min, int max, double weight) {
         this.itemStack = itemStack;
         this.min = min;
         this.max = max;
         this.weight = weight;
      }

      public static GenericLootTable.RandomizedItemStack from(WeightedRandomChestContent chestContent, int defaultWeight) {
         return new GenericLootTable.RandomizedItemStack(chestContent.theItemId.copy(), chestContent.minStackSize, chestContent.maxStackSize, (double)chestContent.itemWeight / (double)defaultWeight);
      }

      public ItemStack generateStack(EaglercraftRandom random) {
         int min = Math.min(this.max, this.min);
         int max = Math.max(this.max, this.min);
         ItemStack stack = this.itemStack.copy();
         stack.setCount(random.nextInt(max - min + 1) + min);
         return stack;
      }

      public double getWeight() {
         return this.weight;
      }

      public static class Serializer implements JsonDeserializer<GenericLootTable.RandomizedItemStack>, JsonSerializer<GenericLootTable.RandomizedItemStack> {
         public Serializer() {
         }

         public GenericLootTable.RandomizedItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = JsonUtils.asJsonObject(json, "randomizedItem");
            double weight = JsonUtils.getDouble(jsonObject, "weight");
            int min = JsonUtils.getInt(jsonObject, "min");
            int max = JsonUtils.getInt(jsonObject, "max");
            ItemStack stack = (ItemStack)context.deserialize(jsonObject.get("item"), ItemStack.class);
            return new GenericLootTable.RandomizedItemStack(stack, min, max, weight);
         }

         public JsonElement serialize(GenericLootTable.RandomizedItemStack src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("weight", Double.valueOf(src.weight));
            jsonObject.addProperty("min", Integer.valueOf(src.min));
            jsonObject.addProperty("max", Integer.valueOf(src.max));
            jsonObject.add("item", context.serialize(src.itemStack));
            return jsonObject;
         }
      }
   }
}
