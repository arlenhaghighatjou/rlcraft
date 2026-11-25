package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;

public class NBTTagVanillaDeserializer implements JsonDeserializer<NBTBase> {
   public NBTTagVanillaDeserializer() {
   }

   public NBTBase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      try {
         return JsonToNBT.getTagFromJson(json.toString());
      } catch (NBTException var5) {
         var5.printStackTrace();
         throw new JsonParseException("Could not parse JSON to NBT");
      }
   }
}
