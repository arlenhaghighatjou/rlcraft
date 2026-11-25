package ivorius.reccomplex.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.reccomplex.json.JsonUtils;
import java.lang.reflect.Type;
import net.minecraft.item.ItemStack;

public class WeightedRandomChestContentSerializer implements JsonSerializer<WeightedRandomChestContent>, JsonDeserializer<WeightedRandomChestContent> {
   public WeightedRandomChestContentSerializer() {
   }

   public WeightedRandomChestContent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject jsonObject = JsonUtils.asJsonObject(json, "weightedRandomChestContent");
      int weight = JsonUtils.getInt(jsonObject, "weight");
      int genMin = JsonUtils.getInt(jsonObject, "genMin");
      int genMax = JsonUtils.getInt(jsonObject, "genMax");
      ItemStack stack = (ItemStack)context.deserialize(jsonObject.get("item"), ItemStack.class);
      return new WeightedRandomChestContent(stack, genMin, genMax, weight);
   }

   public JsonElement serialize(WeightedRandomChestContent src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("weight", Integer.valueOf(src.itemWeight));
      jsonObject.addProperty("genMin", Integer.valueOf(src.minStackSize));
      jsonObject.addProperty("genMax", Integer.valueOf(src.maxStackSize));
      jsonObject.add("item", context.serialize(src.theItemId, ItemStack.class));
      return jsonObject;
   }
}
