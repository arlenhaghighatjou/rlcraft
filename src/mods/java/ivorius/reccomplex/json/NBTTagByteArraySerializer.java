package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.nbt.NBTTagByteArray;

public class NBTTagByteArraySerializer implements JsonSerializer<NBTTagByteArray>, JsonDeserializer<NBTTagByteArray> {
   public NBTTagByteArraySerializer() {
   }

   public JsonElement serialize(NBTTagByteArray src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.getByteArray(), byte[].class);
   }

   public NBTTagByteArray deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return new NBTTagByteArray((byte[])context.deserialize(json, byte[].class));
   }
}
