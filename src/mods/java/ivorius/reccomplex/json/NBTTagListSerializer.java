package ivorius.reccomplex.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

public class NBTTagListSerializer implements JsonSerializer<NBTTagList>, JsonDeserializer<NBTTagList> {
   public NBTTagListSerializer() {
   }

   public JsonElement serialize(NBTTagList src, Type typeOfSrc, JsonSerializationContext context) {
      JsonArray array = new JsonArray();
      NBTTagList copy = src.copy();

      while(copy.tagCount() > 0) {
         array.add(context.serialize(copy.removeTag(0)));
      }

      return array;
   }

   public NBTTagList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (!json.isJsonArray()) {
         return null;
      } else {
         JsonArray jsonArray = json.getAsJsonArray();
         NBTTagList list = new NBTTagList();

         for(JsonElement element : jsonArray) {
            list.appendTag((NBTBase)context.deserialize(element, NBTToJson.getNBTTypeSmart(element)));
         }

         return list;
      }
   }
}
