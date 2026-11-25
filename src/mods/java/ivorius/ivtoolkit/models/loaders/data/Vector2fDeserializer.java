package ivorius.ivtoolkit.models.loaders.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.lax1dude.eaglercraft.v1_8.vector.Vector2f;

public class Vector2fDeserializer implements JsonDeserializer<Vector2f> {
   public Vector2fDeserializer() {
   }

   public Vector2f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      float[] array = (float[])context.deserialize(json, float[].class);
      return new Vector2f(array[0], array[1]);
   }
}
