package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.nbt.NBTTagIntArray;

public class NBTTagIntArraySerializer implements JsonSerializer<NBTTagIntArray>, JsonDeserializer<NBTTagIntArray> {
   public NBTTagIntArraySerializer() {
   }

   public JsonElement serialize(NBTTagIntArray src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.getIntArray(), int[].class);
   }

   public NBTTagIntArray deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return new NBTTagIntArray((int[])context.deserialize(json, int[].class));
   }
}
