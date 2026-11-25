package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.nbt.NBTTagDouble;

public class NBTTagDoubleSerializer implements JsonSerializer<NBTTagDouble>, JsonDeserializer<NBTTagDouble> {
   public NBTTagDoubleSerializer() {
   }

   public JsonElement serialize(NBTTagDouble src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.getDouble());
   }

   public NBTTagDouble deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return new NBTTagDouble(json.getAsDouble());
   }
}
