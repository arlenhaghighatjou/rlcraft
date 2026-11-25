package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagEnd;

public class NBTTagEndSerializer implements JsonSerializer<NBTTagEnd>, JsonDeserializer<NBTTagEnd> {
   private static Constructor<NBTTagEnd> nbtTagEndConstructor;

   public NBTTagEndSerializer() {
   }

   public static Constructor<NBTTagEnd> getNbtTagEndConstructor() {
      if (nbtTagEndConstructor == null) {
         try {
            nbtTagEndConstructor = NBTTagEnd.class.getDeclaredConstructor();
         } catch (NoSuchMethodException var1) {
            var1.printStackTrace();
         }

         nbtTagEndConstructor.setAccessible(true);
      }

      return nbtTagEndConstructor;
   }

   public JsonElement serialize(NBTTagEnd src, Type typeOfSrc, JsonSerializationContext context) {
      return JsonNull.INSTANCE;
   }

   public NBTTagEnd deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return createNBTTagEnd();
   }

   @Nonnull
   public static NBTTagEnd createNBTTagEnd() {
      try {
         return (NBTTagEnd)getNbtTagEndConstructor().newInstance();
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException var1) {
         throw new RuntimeException(var1);
      }
   }
}
