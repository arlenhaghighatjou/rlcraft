package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.nbt.NBTTagShort;

public class NBTTagShortSerializer implements JsonSerializer<NBTTagShort>, JsonDeserializer<NBTTagShort> {
   public NBTTagShortSerializer() {
   }

   public JsonElement serialize(NBTTagShort src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.getShort());
   }

   public NBTTagShort deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return new NBTTagShort(json.getAsShort());
   }
}
