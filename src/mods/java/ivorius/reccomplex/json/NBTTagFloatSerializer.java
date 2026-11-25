package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.nbt.NBTTagFloat;

public class NBTTagFloatSerializer implements JsonSerializer<NBTTagFloat>, JsonDeserializer<NBTTagFloat> {
   public NBTTagFloatSerializer() {
   }

   public JsonElement serialize(NBTTagFloat src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.getFloat());
   }

   public NBTTagFloat deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return new NBTTagFloat(json.getAsFloat());
   }
}
