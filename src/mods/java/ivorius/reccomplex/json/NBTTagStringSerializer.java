package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.nbt.NBTTagString;

public class NBTTagStringSerializer implements JsonSerializer<NBTTagString>, JsonDeserializer<NBTTagString> {
   public NBTTagStringSerializer() {
   }

   public JsonElement serialize(NBTTagString src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.getString());
   }

   public NBTTagString deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return new NBTTagString(json.getAsString());
   }
}
