package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.nbt.NBTTagInt;

public class NBTTagIntSerializer implements JsonSerializer<NBTTagInt>, JsonDeserializer<NBTTagInt> {
   public NBTTagIntSerializer() {
   }

   public JsonElement serialize(NBTTagInt src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.getInt());
   }

   public NBTTagInt deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return new NBTTagInt(json.getAsInt());
   }
}
