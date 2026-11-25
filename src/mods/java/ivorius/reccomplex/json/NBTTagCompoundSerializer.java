package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class NBTTagCompoundSerializer implements JsonSerializer<NBTTagCompound>, JsonDeserializer<NBTTagCompound> {
   public NBTTagCompoundSerializer() {
   }

   public JsonElement serialize(NBTTagCompound src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject jsonObject = new JsonObject();

      for(Object key : src.getKeySet()) {
         String keyString = (String)key;
         jsonObject.add(keyString, context.serialize(src.getTag(keyString)));
      }

      return jsonObject;
   }

   public NBTTagCompound deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (!json.isJsonObject()) {
         return null;
      } else {
         JsonObject jsonObject = json.getAsJsonObject();
         NBTTagCompound compound = new NBTTagCompound();

         for(Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            compound.setTag((String)entry.getKey(), (NBTBase)context.deserialize((JsonElement)entry.getValue(), NBTToJson.getNBTTypeSmart((JsonElement)entry.getValue())));
         }

         return compound;
      }
   }
}
