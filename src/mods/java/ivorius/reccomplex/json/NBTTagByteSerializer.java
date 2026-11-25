package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.nbt.NBTTagByte;

public class NBTTagByteSerializer implements JsonSerializer<NBTTagByte>, JsonDeserializer<NBTTagByte> {
   public NBTTagByteSerializer() {
   }

   public JsonElement serialize(NBTTagByte src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.getByte());
   }

   public NBTTagByte deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return new NBTTagByte(json.getAsByte());
   }
}
