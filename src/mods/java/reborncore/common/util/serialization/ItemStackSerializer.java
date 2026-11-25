package reborncore.common.util.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
   private static final String NAME = "name";
   private static final String META_VALUE = "metaValue";
   private static final String STACK_SIZE = "stackSize";
   private static final String TAG_COMPOUND = "tagCompound";

   public ItemStackSerializer() {
   }

   public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (json.isJsonObject()) {
         JsonObject jsonObject = json.getAsJsonObject();
         String name = null;
         int metaValue = 0;
         int stackSize = 1;
         NBTTagCompound tagCompound = null;
         if (jsonObject.has("name") && jsonObject.get("name").isJsonPrimitive()) {
            name = jsonObject.getAsJsonPrimitive("name").getAsString();
         }

         if (jsonObject.has("metaValue") && jsonObject.get("metaValue").isJsonPrimitive()) {
            try {
               metaValue = jsonObject.getAsJsonPrimitive("metaValue").getAsInt();
            } catch (NumberFormatException var11) {
               ;
            }
         }

         if (jsonObject.has("stackSize") && jsonObject.get("stackSize").isJsonPrimitive()) {
            stackSize = jsonObject.getAsJsonPrimitive("stackSize").getAsInt();
         }

         if (jsonObject.has("tagCompound") && jsonObject.get("tagCompound").isJsonPrimitive()) {
            try {
               tagCompound = JsonToNBT.getTagFromJson(jsonObject.getAsJsonPrimitive("tagCompound").getAsString());
            } catch (NBTException var10) {
               ;
            }
         }

         if (name != null && Item.getByNameOrId(name) != null) {
            ItemStack itemStack = new ItemStack(Item.getByNameOrId(name), stackSize, metaValue);
            itemStack.setTagCompound(tagCompound);
            return itemStack;
         }
      }

      return ItemStack.EMPTY;
   }

   public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
      if (src != null && src.getItem() != null) {
         JsonObject jsonObject = new JsonObject();
         if (Item.REGISTRY.getNameForObject(src.getItem()) != null) {
            jsonObject.addProperty("name", ((ResourceLocation)Item.REGISTRY.getNameForObject(src.getItem())).toString());
            if (src.getItemDamage() != 0) {
               jsonObject.addProperty("metaValue", Integer.valueOf(src.getItemDamage()));
            }

            jsonObject.addProperty("stackSize", Integer.valueOf(src.getCount()));
            if (src.getTagCompound() != null) {
               jsonObject.addProperty("tagCompound", src.getTagCompound().toString());
            }

            return jsonObject;
         } else {
            return JsonNull.INSTANCE;
         }
      } else {
         return JsonNull.INSTANCE;
      }
   }
}
