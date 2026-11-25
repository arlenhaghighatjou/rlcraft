package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.nbt.NBTBase;

public class NBTBaseSerializerSmart implements JsonSerializer<NBTBase>, JsonDeserializer<NBTBase> {
   public NBTBaseSerializerSmart() {
   }

   public NBTBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      Class<? extends NBTBase> aClass = NBTToJson.getNBTTypeSmart(json);
      if (aClass != null) {
         return (NBTBase)NBTToJson.nbtJson.fromJson(json, aClass);
      } else {
         throw new JsonParseException("Unknown NBT type");
      }
   }

   public JsonElement serialize(NBTBase src, Type typeOfSrc, JsonSerializationContext context) {
      return NBTToJson.nbtJson.toJsonTree(src);
   }
}
